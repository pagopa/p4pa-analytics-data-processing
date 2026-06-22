import com.github.jk1.license.render.*
import com.github.jk1.license.filter.*
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
  java
  id("org.springframework.boot") version "4.1.0"
  id("io.spring.dependency-management") version "1.1.7"
  jacoco
  id("org.sonarqube") version "7.3.1.8318"
  id("com.github.ben-manes.versions") version "0.53.0"
  id("org.openapi.generator") version "7.23.0"
  id("com.gorylenko.gradle-git-properties") version "4.0.1"
  id("com.github.jk1.dependency-license-report") version "3.1.4"
}

group = "it.gov.pagopa.payhub"
version = "0.0.1"
description = "p4pa-analytics-data-processing"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
  compileClasspath {
    resolutionStrategy.activateDependencyLocking()
  }
}

licenseReport {
  renderers = arrayOf(XmlReportRenderer("third-party-libs.xml", "Back-End Libraries"))
  outputDir = "$projectDir/dependency-licenses"
  filters = arrayOf(SpdxLicenseBundleNormalizer())
}
tasks.classes {
  finalizedBy(tasks.generateLicenseReport)
}

repositories {
  mavenCentral()
}

val springDocOpenApiVersion = "3.0.3"
val openApiToolsVersion = "0.2.10"
val micrometerVersion = "1.7.0"
val bouncycastleVersion = "1.84"
val httpClientVersion = "5.6.1"
val httpCoreVersion = "5.4.2"
val kafkaAppender = "0.2.0-RC2"
val lz4JavaVersion = "1.11.0"
val commonsLang3Version = "3.20.0"
val temporalVersion = "1.35.0"
val protobufJavaVersion = "4.35.1"
val grpcBomVersion = "1.82.0"
val guavaVersion = "33.6.0-jre"
val otelVersion = "1.63.0"
val mapStructVersion = "1.6.3"
val podamVersion = "8.0.2.RELEASE"

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webmvc")
  implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
  implementation("org.springframework.boot:spring-boot-starter-restclient")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-aspectj")
  implementation("org.springframework.data:spring-data-commons")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocOpenApiVersion") {
    exclude(group = "org.apache.commons", module = "commons-lang3")
  }
  implementation ("org.apache.commons:commons-lang3:${commonsLang3Version}")
  implementation("io.micrometer:micrometer-tracing-bridge-otel:$micrometerVersion")
  implementation("io.micrometer:micrometer-registry-prometheus")
  implementation("org.openapitools:jackson-databind-nullable:$openApiToolsVersion")
  implementation ("org.mapstruct:mapstruct:${mapStructVersion}")
  implementation ("org.bouncycastle:bcprov-jdk18on:${bouncycastleVersion}")
  implementation("org.apache.httpcomponents.client5:httpclient5:$httpClientVersion")
  implementation("org.apache.httpcomponents.core5:httpcore5:$httpCoreVersion")
  implementation("com.github.danielwegener:logback-kafka-appender:$kafkaAppender") {
    exclude(group = "org.lz4", module = "lz4-java")
  }
  implementation("at.yawk.lz4:lz4-java:$lz4JavaVersion")
  // Temporal
  implementation("io.temporal:temporal-spring-boot-starter:$temporalVersion") {
    exclude(group = "com.google.protobuf", module = "protobuf-java")
    exclude(group = "com.google.protobuf", module = "protobuf-java-util")
    exclude(group = "io.grpc", module = "grpc-bom")
    exclude(group = "com.google.guava", module = "guava")
  }
  implementation("com.google.protobuf:protobuf-java:$protobufJavaVersion")
  implementation("com.google.protobuf:protobuf-java-util:${protobufJavaVersion}")
  implementation(platform("io.grpc:grpc-bom:${grpcBomVersion}"))
  implementation("com.google.guava:guava:$guavaVersion")
  implementation("io.opentelemetry:opentelemetry-opentracing-shim:${otelVersion}")

  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")
  annotationProcessor("org.mapstruct:mapstruct-processor:$mapStructVersion")
  testAnnotationProcessor("org.projectlombok:lombok")
  testAnnotationProcessor("org.mapstruct:mapstruct-processor:$mapStructVersion")

  //	Testing
  testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
  testImplementation("org.springframework.boot:spring-boot-starter-security-test")
  testImplementation("org.mockito:mockito-core")
  testImplementation("org.projectlombok:lombok")
  testImplementation ("uk.co.jemos.podam:podam:${podamVersion}")
}

tasks.withType<Test> {
  useJUnitPlatform()
  finalizedBy(tasks.jacocoTestReport)
}

val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
  mockitoAgent("org.mockito:mockito-core") { isTransitive = false }
}
tasks {
  jar {
      from("${rootProject.projectDir}") {
          include("LICENSE.md")
          into("META-INF")
      }
  }
  test {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
    testLogging.events = setOf(TestLogEvent.FAILED)
    testLogging.exceptionFormat = TestExceptionFormat.FULL
  }
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    xml.required = true
  }
}

val projectInfo = mapOf(
  "artifactId" to project.name,
  "version" to project.version
)

tasks {
  val processResources by getting(ProcessResources::class) {
    filesMatching("**/application.yml") {
      expand(projectInfo)
    }
  }
  processResources.dependsOn("dependenciesBuild")
}

tasks.compileJava {
  dependsOn("dependenciesBuild")
}

tasks.register("dependenciesBuild") {
  group = "AutomaticallyGeneratedCode"
  description = "grouping all together automatically generate code tasks"

  dependsOn(
    "openApiGenerate"
  )
}

configure<SourceSetContainer> {
  named("main") {
    java.srcDir("$projectDir/build/generated/src/main/java")
  }
}

springBoot {
  buildInfo()
  mainClass.value("it.gov.pagopa.analytics.process.AnalyticsDataProcessingApplication")
}

openApiGenerate {
  generatorName.set("spring")
  inputSpec.set("$rootDir/openapi/p4pa-analytics-data-processing.openapi.yaml")
  outputDir.set("$projectDir/build/generated")
  apiPackage.set("it.gov.pagopa.analytics.process.controller.generated")
  modelPackage.set("it.gov.pagopa.analytics.process.dto.generated")
  typeMappings.set(mapOf(
    "ScheduleEnum" to "it.gov.pagopa.analytics.process.enums.ScheduleEnum",
    "WorkflowExecutionStatus" to "io.temporal.api.enums.v1.WorkflowExecutionStatus"
  ))
  configOptions.set(mapOf(
    "dateLibrary" to "java8",
    "requestMappingMode" to "api_interface",
    "useSpringBoot4" to "true",
    "useJackson3" to "true",
    "interfaceOnly" to "true",
    "useTags" to "true",
    "useBeanValidation" to "true",
    "generateConstructorWithAllArgs" to "true",
    "generatedConstructorWithRequiredArgs" to "true",
    "enumPropertyNaming" to "original",
    "additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
  ))
}
