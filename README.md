# Spring Boot 3 Features Demo

Basically inspired by

- [Spring Tips: "the road to Spring Boot 3: Spring Framework 6"](https://spring.io/blog/2022/10/26/spring-tips-the-road-to-spring-boot-3-spring-framework-6)
- [OpenValue'S "Switching from Spring Cloud Sleuth to Micrometer Tracing / Micrometer Observation for Spring Boot 3"](https://openvalue.blog/posts/2022/12/16/tracing-in-spring-boot-2-and-3/)
- [Baeldung's "Observability with Spring Boot 3"](https://www.baeldung.com/spring-boot-3-observability)

## Shows

- `@ControllerAdvice` with `ProblemDetail` (RFC 7807).
- Access to jakarta packages.
- Using metrics with `@Observed` and `ObservationRegistry/Observation`
- Interface based `WebClient`

## Content

There is one endpoint `/calculate/{input1}[?input2={input2}]` calculating the *fibonacci* value directly for `input1`
and also indirectly via a REST call to a second endpoint `/calculate2/{input}` one the same service or a remote
service of the same type.

## Tests from Command Line

### ProblemDetail

```shell
$ curl http://localhost:8080/calculate/0
{"type":"about:blank","title":"Bad Request","status":400,"detail":"PathVariable input1 must be between [1,30]","instance":"/calculate/0"}
```

### Calculations and interface based `WebClient`

```shell
curl http://localhost:8080/calculate/10
{"ok":true,"value1":55,"value2":0}

curl http://localhost:8080/calculate/10?input2=10
{"ok":true,"value1":55,"value2":21}
```

### Metrics and Observation

```shell
curl http://localhost:8080/actuator/metrics | jq
```

or interactive in browser using http://localhost:8080.

```json
{
  "names": [
    "application.ready.time",
    "application.started.time",
    "demo.calculate",
    "demo.calculate.active",
    "disk.free",
    "disk.total",
    ...
    "http.server.requests",
  ]
}
```

Using the "demo.calculate" metrics endpoint

```shell
curl http://localhost:8080/actuator/metrics/demo.calculate | jq
```

```json
{
  "name": "demo.calculate",
  "description": null,
  "baseUnit": "seconds",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 436
    },
    {
      "statistic": "TOTAL_TIME",
      "value": 0.038327775
    },
    {
      "statistic": "MAX",
      "value": 0
    }
  ],
  "availableTags": [
    {
      "tag": "error",
      "values": [
        "none"
      ]
    }
  ]
}
``` 

## Test locally

- Run Service 1 using the environment variables
  `SERVER_PORT=8080;SPRING_APPLICATION_NAME=demo1;APPLICATION_CLIENT_PORT=8081`
- Run Service 2 using the environment variables
  `SERVER_PORT=8081;SPRING_APPLICATION_NAME=demo2;APPLICATION_CLIENT_PORT=8080`

With this setup, the services can call each other.

# Observability - Metrics, Tracing, Logs

*One of the more interesting new features in Spring Boot 3 is the support for Prometheus exemplars.
Exemplars are references to data outside the metrics published by an application.
They allow linking metrics data to distributed traces. In that case, the published metrics contain a reference to the traceId.*

https://github.com/open-telemetry/opentelemetry-java/tree/main/exporters/zipkin

## Setup 1

- See https://spring.io/blog/2022/10/12/observability-with-spring-boot-3
- See https://piotrminkowski.com/2022/11/03/spring-boot-3-observability-with-grafana/
- See https://github.com/open-telemetry/opentelemetry-java/blob/main/sdk-extensions/autoconfigure/README.md

Hint for Grafana: the setup allows write access and is editable!

### Client Instrumentation

*How is the Java Spring Boot 3 Application instrumented and where are the changes located?*

- Metrics: `io.micrometer:micrometer-registry-prometheus` via changes in
  - [pom.xml](pom.xml) 
  - [application.yml](src/main/resources/application.yml) (`management.endpoints.web.exposure.include=prometheus`)
- Tracing: `io.micrometer:micrometer-tracing-bridge-otel` plus `io.opentelemetry:opentelemetry-exporter-zipkin` via changes in
  - [pom.xml](pom.xml)
  - and a couple of environment variables in [start-demo1.sh](start-demo1.sh) / [start-demo2.sh](start-demo2.sh)
- Logs: `com.github.loki4j:loki4j.logback.Loki4jAppender` via changes in
  - [pom.xml](pom.xml) 
  - [logback-spring.xml](src/main/resources/logback-spring.xml)

### Backends and Visualization

- Metrics: [Grafana](https://grafana.com/metrics/) via [Prometheus](https://prometheus.io/)
- Tracing: [Grafana Tempo](https://grafana.com/oss/tempo/) 
- Logs: [Grafana LOKI](https://grafana.com/oss/loki/)

## Setup 2

See https://grafana.com/blog/2022/05/04/how-to-capture-spring-boot-metrics-with-the-opentelemetry-java-instrumentation-agent/

First remove all dependencies of Setup 1 in [pom.xml](pom.xml).

In Setup 1 metrics were provided by the Spring Boot app using `io.micrometer:micrometer-registry-prometheus`
and scraped by *Prometheus* (and from there send to *Grafana*). In this Setup 2, the scraping is done by
the [Open Telemetry Collector].(https://github.com/open-telemetry/opentelemetry-collector) (a Go implementation).
The OTEL Collector’s pipeline has 3 steps: Receivers —> Processors —> Exporters.

- for the receiver, we use `otlp`
- for the processor, we use `batch`
- for the exporter, we use `batch`

## Setup 3

Same as Setup 1, but with [VictoriaMetrics](https://docs.victoriametrics.com/) instead of Prometheus.

### Client Instrumentation

*How is the Java Spring Boot 3 Application instrumented and where are the changes located?*

No changes in code. Instrumentation via
[OpenTelemetry Java instrumentation agent](https://github.com/open-telemetry/opentelemetry-java-instrumentation).

Start with

```bash
export OTEL_METRICS_EXPORTER=otlp
export OTEL_TRACES_EXPORTER=otlp
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
java -javaagent:./observability/setup2/opentelemetry-javaagent.jar \
     -jar target/demo-0.0.1-SNAPSHOT.jar`
```

## Setup 4

Same as Setup 1, but using [Grafana Agent](https://grafana.com/docs/grafana-cloud/data-configuration/agent/about_agent/)
to scrape the `actuator/prometheus` endpoints and pass the data to Grafana Cloud.

---

## Run the setups

### Run setup 1

This setup starts 6 containers using [setup1/docker-compose.yml](observability/setup1/docker-compose.yml).
There are two identical Spring Boot Services, the first `demo1-sb3` will call the second one `demo2-sb3`.

First you have to build the image for the Spring Boot demo application.

```shell
./mvnw -DskipTests=true package
docker build --tag=spring-boot3-demo:latest .
```

Start/Stop containers

```shell
cd observability/setup1
# Start
docker-compose up -d
# Stop
docker-compose down
```

Result for setup 1

```
$ docker ps
CONTAINER ID   IMAGE             COMMAND                  CREATED          STATUS          PORTS                    NAMES
8660c442fd91   grafana/grafana   "/run.sh"                14 seconds ago   Up 11 seconds   0.0.0.0:3000->3000/tcp   grafana-sb3
eee20e7917e1   prom/prometheus   "/bin/prometheus --e…"   14 seconds ago   Up 11 seconds   0.0.0.0:9090->9090/tcp   prometheus-sb3
34b87d1db40e   grafana/loki      "/usr/bin/loki -conf…"   14 seconds ago   Up 13 seconds   0.0.0.0:3100->3100/tcp   loki-sb3
```

Result for setup 2 (docker)

```
t.b.d
```

Use setup2 locally with Grafana Cloud

```
./otelcol-contrib.exe --config=file:./otel-collector-pull-cloud.yaml
```

### Check Services for Readiness

- Spring Boot Service 1:
  - Readiness: `curl http://localhost:8080/actuator/health`
  - Make call to Service 1: `curl http://localhost:8080/calculate/16?input2=12`
- Spring Boot Service 2:
  - Readiness: `curl http://localhost:8081/actuator/health`
  - Make call to Service 2: `curl http://localhost:8081/calculate/10`
- Prometheus:
  - Readiness: `curl http://localhost:9090/-/ready` ==> *Prometheus Server is Ready.*
  - [UI](http://localhost:9090/) - Use e.g. `demo_calculate_seconds_sum` as a query
  - [Management API Dokumentation](https://prometheus.io/docs/prometheus/latest/management_api/)
- Tempo:
  - Readiness: `curl http://localhost:3200/ready` ==> *ready*. If the message is *Ingester not ready: ingester check ready failed waiting for 15s after being ready
*, then wait some seconds.
  - Port für Ingest: `curl http://localhost:9411/` ==> *unexpected end of JSON input*
  - [API Dokumentation](https://grafana.com/docs/tempo/latest/api_docs/)
- LOKI:
  - Readiness: `curl http://localhost:3100/ready` ==> *ready*
  - Test query: `curl -G -s  "http://localhost:3100/loki/api/v1/query" \
    --data-urlencode \
    'query=sum(rate({job="varlogs"}[10m])) by (level)' | jq`
  - [API Dokumentation](https://grafana.com/docs/loki/latest/api/)
- Mimir
  - [UI](http://localhost:58080/)
  - Status: http://localhost:58080/services
- Grafana:
  - [UI](http://localhost:3000/)
  - [API Dokumentation](https://grafana.com/docs/grafana/latest/developers/http_api/)
- VictoriaMetrics:
  - [UI](http://localhost:8428/)
  - Readiness: `curl http://localhost:8428/metrics` ==> *List of metrics*
- OTEL Collector
  - Readiness: `curl http://localhost:13133/heath/status` ==> `{"status":"Server available","upSince":"2023-07-01T16:17:40.248631996Z","uptime":"3m43.492991139s"}`
  - Readiness API may be customized - see https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/main/extension/healthcheckextension/README.md
  - Metrics: `curl http://localhost:13888/metrics` ==> Prometheus metrics

### Perform some client calls

```shell
curl http://localhost:8080/calculate/10?input2=12
curl http://localhost:8081/calculate/10?input2=12
```

### UIs

Prometheus Targets

![Prometheus Targets](docs/images/setup1/prometheus-targets.png)

Grafana Data Sources

![Grafana Data Sources](docs/images/setup1/grafana-datasources.png)

Grafana Dashboards

![Grafana Dashboards](docs/images/setup1/grafana-dashboards.png)

Grafana Metrics (Prometheus)

![Grafana Metrics (Prometheus)](docs/images/setup1/grafana-prometheus.png)

Grafana Tracing (Tempo)

![Grafana Tracing (Tempo)](docs/images/setup1/grafana-tempo.png)

Grafana Logging (LOKI)

![Grafana Logging (LOKI)](docs/images/setup1/grafana-loki.png)

## Run setup 2

./otelcol --config observability/setup2/docker/otel/otel-collector.yaml

### Check Services for Readiness

- OTEL Collector:
  - Readiness: `curl http://localhost:13133` ==> *{"status":"Server available","upSince":"2022-11-08T05:50:29.269885109Z","uptime":"14.027149655s"}*
  - [API Dokumentation](https://github.com/open-telemetry/opentelemetry-collector/blob/main/docs/troubleshooting.md#health-check)
