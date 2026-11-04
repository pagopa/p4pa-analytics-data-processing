# syntax=docker/dockerfile:1.4@sha256:9ba7531bd80fb0a858632727cf7a112fbfd19b17e94c4e84ced81e24ef1a0dbc

#
# 🎯 Version Management
#
ARG IMAGE="public.ecr.aws/docker/library/eclipse-temurin"

#The image uses Temurin Java 21 on Ubuntu 24.04 (Noble), with Python 3.12
#installed through APT. The glibc-based distribution supports the native Linux
#wheels required by dbt's experimental parser, unlike Alpine/musl.
#dbt is installed in `/app/.venv`, which is also included in `PATH` so Java
#subprocesses can invoke it without activating the virtual environment.
#Update both `IMAGE_VERSION` and `IMAGE_SHA` when updating the base image.
# It could be possible to return to Alpine once resolved: https://github.com/dbt-labs/dbt/issues/15607
ARG IMAGE_VERSION="21-jdk-noble"
ARG IMAGE_SHA="114cabb73033ca4ce7ceec3deb62396bc1b716282bae051ed3c1d82e38fcab2d"
ARG APPINSIGHTS_VERSION="3.7.9"

# 🌍 Timezone Configuration
ARG TZ="Europe/Rome"

# 🔧 Build Configuration
ARG GRADLE_OPTS="-Dorg.gradle.daemon=false \
    -Dorg.gradle.parallel=true \
    -Dorg.gradle.caching=true \
    -Dorg.gradle.configureondemand=true \
    -Dorg.gradle.jvmargs=-Xmx2g"

# 👤 App Configuration
ARG APP_USER="appuser"
ARG APP_GROUP="appgroup"
ARG APP_HOME="/app"

#
# 📥 Base Setup Stage
#
FROM ${IMAGE}@sha256:${IMAGE_SHA} AS base
ARG APP_USER
ARG APP_GROUP

# Install base packages
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    wget \
    unzip \
    bash \
    git && \
    rm -rf /var/lib/apt/lists/*

# Reuse Noble's default UID/GID 1000 for the build user.
RUN groupmod --new-name ${APP_GROUP} ubuntu && \
    usermod --login ${APP_USER} --home /home/${APP_USER} --move-home ubuntu

#
# 📦 Gradle Setup Stage
#
FROM base AS gradle-setup
ARG GRADLE_OPTS
ARG APP_USER
ARG APP_GROUP

# Set environment variables for Gradle
ENV GRADLE_OPTS="${GRADLE_OPTS}"

WORKDIR /build

COPY --chown=${APP_USER}:${APP_GROUP} gradle gradle/
COPY --chown=${APP_USER}:${APP_GROUP} gradlew gradlew

# Download and verify Gradle with progress bar
RUN ./gradlew --version

#
# 📚 Dependencies Stage
#
FROM gradle-setup AS dependencies
ARG APP_USER
ARG APP_GROUP

WORKDIR /build

# Copy build configuration
COPY --chown=${APP_USER}:${APP_GROUP} build.gradle.kts settings.gradle.kts ./
COPY --chown=${APP_USER}:${APP_GROUP} gradle.lockfile ./
COPY --chown=${APP_USER}:${APP_GROUP} openapi openapi/
COPY .git .git

# Generate OpenAPI stubs and download dependencies
RUN mkdir -p src/main/java && \
    chown -R ${APP_USER}:${APP_GROUP} /build && \
    chmod -R 775 /build

USER ${APP_USER}

RUN ./gradlew dependenciesBuild dependencies --no-daemon >/dev/null

#
# 🏗️ Build Stage
#
FROM dependencies AS build
ARG APP_USER
ARG APP_GROUP

# Copy source code
COPY --chown=${APP_USER}:${APP_GROUP} src src/

# Build application
RUN ./gradlew bootJar --no-daemon

#
# 🚀 Runtime Stage
#
FROM ${IMAGE}@sha256:${IMAGE_SHA} AS runtime-base
ARG APP_USER
ARG APP_GROUP
ARG APP_HOME
ARG APPINSIGHTS_VERSION
ARG TZ

WORKDIR ${APP_HOME}

# Set timezone environment variable
ENV TZ=${TZ}
ENV PATH="${APP_HOME}/.venv/bin:${PATH}"

# 🛡️ Security Setup and Timezone
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get upgrade -y && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
        ca-certificates \
        tini \
        curl \
        python3 \
        python3-venv \
        # Configure timezone + ENV=TZ \
        tzdata \
        git && \
    rm -rf /var/lib/apt/lists/* && \
    # Create user and group \
    groupadd --system ${APP_GROUP} && \
    useradd --system --gid ${APP_GROUP} --create-home ${APP_USER} && \
    # Install dbt-core \
    python3 -m venv .venv && \
    python -m pip install --no-cache-dir "dbt-core==1.12.6" "dbt-postgres==1.11.0" && \
    python -m pip check && \
    dbt --version

ADD --chmod=644 https://github.com/microsoft/ApplicationInsights-Java/releases/download/${APPINSIGHTS_VERSION}/applicationinsights-agent-${APPINSIGHTS_VERSION}.jar ${APP_HOME}/applicationinsights-agent.jar

FROM runtime-base AS runtime
ARG APP_USER
ARG APP_GROUP
ARG APP_HOME

# Copy dbt project and scripts
COPY --chown=${APP_USER}:${APP_GROUP} p4pa_analytics_dbt dbt/
COPY --chown=${APP_USER}:${APP_GROUP} script script/
RUN chmod +x /app/script/*

# 📦 Copy Artifacts
COPY --from=build /build/build/libs/*.jar ${APP_HOME}/app.jar

# 📝 Set Permissions
RUN chown -R ${APP_USER}:${APP_GROUP} ${APP_HOME}

# 🔌 Container Configuration
EXPOSE 8080
USER ${APP_USER}

# 🎬 Startup Configuration
ENTRYPOINT ["/usr/bin/tini", "--"]
CMD ["java", "-jar", "/app/app.jar"]
