# p4pa-analytics-data-processing

This application represent the data processing layer of the analytics tool related to **Piattaforma Unitaria** product (PU).

See [PU Microservice Architecture](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1405845916/Architettura+microservizi) for more details.

## 🧱 Role

* To handle the transformations of data from `raw` (provided by [p4pa-analytics-data-ingestion](https://github.com/pagopa/p4pa-analytics-data-ingestion)) layer to `dmt` layer (consumer by [p4pa-superset](https://github.com/pagopa/p4pa-superset)).

## 🌐 APIs
See [OpenAPI](openapi/generated.openapi.json), exposed through the following path:
* `/swagger-ui/index.html`

### 📌 Relevant APIs
* `GET /workflowhub/workflows/{workflowId}/status`: To get workflow status;
* `GET /workflowhub/schedules/{scheduleId}/info`: To get schedule info;

### 📌 Common HTTP status returned:
* `401`: Invalid access token provided, thus a new login is required;
* `403`: Trying to access a not authorized resource.

## 🔎 Monitoring
See available actuator endpoints through the following path:
* `/actuator`

### 📌 Relevant endpoints
* Health (provide an accessToken to see details): `/actuator/health`
  * Liveness: `/actuator/health/liveness`
  * Readiness: `/actuator/health/readiness`
* Metrics: `/actuator/metrics`
  * Prometheus: `/actuator/prometheus`

Further endpoints are exposed through the JMX console.

## ✏️ Logging
See [log configured pattern](/src/main/resources/logback-spring.xml).

## 🔗 Dependencies

### 🗄️ Resources
* Postgresql
* Temporal.io

## 🔧 Configuration

See [application.yml](src/main/resources/application.yml) for each configurable property.

### 📌 Relevant configurations

#### 🌐 Application Server
| ENV         | DESCRIPTION                       | DEFAULT |
|-------------|-----------------------------------|---------|
| SERVER_PORT | Application server listening port | 8080    |

#### ✏️ Logging
| ENV                                   | DESCRIPTION                                                                                                                                                                     | DEFAULT |
|---------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------|
| LOG_LEVEL_ROOT                        | Base level                                                                                                                                                                      | INFO    |
| LOG_LEVEL_PAGOPA                      | Base level of custom classes                                                                                                                                                    | INFO    |
| LOG_LEVEL_SPRING                      | Level applied to Spring framework                                                                                                                                               | INFO    |
| LOG_LEVEL_SPRING_BOOT_AVAILABILITY    | To print availability events                                                                                                                                                    | DEBUG   |
| LOGGING_LEVEL_API_REQUEST_EXCEPTION   | Level applied to APIs exception                                                                                                                                                 | INFO    |
| LOG_LEVEL_PERFORMANCE_LOG             | Level applied to [PerformanceLog](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1540096383/Logging#2.2.-Log-di-performance)                                               | INFO    |
| LOG_LEVEL_PERFORMANCE_LOG_API_REQUEST | Level applied to [API Performance Log](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1540096383/Logging#2.2.2.1.-Log-di-perfomance-per-le-API)                            | INFO    |
| LOG_LEVEL_PERFORMANCE_LOG_REST_INVOKE | Level applied to [REST invoke Performance Log](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1540096383/Logging#2.2.2.2.-Log-di-performance-per-i-servizi-REST-integrati) | INFO    |

#### 🔁 Integrations

##### 🕒 Temporal.io
| ENV                                                       | DESCRIPTION                                                            | DEFAULT   |
|-----------------------------------------------------------|------------------------------------------------------------------------|-----------|
| TEMPORAL_SERVER_HOST                                      | Temporal hostname                                                      | localhost |
| TEMPORAL_SERVER_PORT                                      | Temporal port                                                          | 7233      |
| TEMPORAL_SERVER_ENABLE_HTTPS                              | To use HTTPS when invoking Temporal                                    | false     |
| TEMPORAL_SERVER_NAMESPACE                                 | Temporal namespace                                                     | pu        |
| DEFAULT_ACTIVITY_CONFIG_START_TO_CLOSE_TIMEOUT_IN_SECONDS | Default startToClose activity timeout (seconds)                        | 300       |
| DEFAULT_ACTIVITY_CONFIG_RETRY_INITIAL_INTERVAL_IN_MILLIS  | Default initial interval to wait during retries (milliseconds)         | 1000      |
| DEFAULT_ACTIVITY_CONFIG_RETRY_BACKOFF_COEFFICIENT         | Default backoff coefficient used to increase the delay between retries | 1.5       |
| DEFAULT_ACTIVITY_CONFIG_RETRY_MAXIMUM_ATTEMPTS            | Default maximum number of retries                                      | 30        |

See `workflow.*` properties on [application.yml](src/main/resources/application.yml) to check configuration for each workflow.

###### 📥 TaskQueue poller sizes
| ENV                            | DESCRIPTION                                                       | DEFAULT |
|--------------------------------|-------------------------------------------------------------------|---------|
| WF_DATA_PROCESSING_POLLER_SIZE | Poller size configured for Temporal task queue `DataProcessingWF` | 5       |

#### 💼 Business logic
| ENV                              | DESCRIPTION                                                           | DEFAULT    |
|----------------------------------|-----------------------------------------------------------------------|------------|
| SCHEDULE_ASSESSMENTS_CLASSIFY_WF | Frequency of AssessmentsClassify data processing WF (cron expression) | 0 1 * * *  |

#### 🔑 keys
| ENV                                  | DESCRIPTION                                                                              | DEFAULT |
|--------------------------------------|------------------------------------------------------------------------------------------|---------|
| JWT_TOKEN_PUBLIC_KEY                 | p4pa-auth JWT public key                                                                 |         |

## 🛠️ Getting Started

### 📝 Prerequisites

Ensure the following tools are installed on your machine:

1. **Java 21+**
2. **Gradle** (or use the Gradle wrapper included in the repository)
3. **Docker** (to build and run on an isolated environment, optional)

### 🔐 Write Locks

```sh
./gradlew dependencies --write-locks
```

### ⚙️ Build

```sh
./gradlew clean build
```

### 🧪 Test

#### 📌 JUnit
```sh
./gradlew test
```

### 🚀 Run local

```sh
./gradlew bootRun
```

### 🐳 Build & run through Docker
```sh
docker build -t <APP_NAME> .
docker run --env-file <ENV_FILE> <APP_NAME>
```
