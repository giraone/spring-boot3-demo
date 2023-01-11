#!/bin/bash

export SERVER_PORT=8081
export APPLICATION_CLIENT_PORT=8080
export SPRING_APPLICATION_NAME=demo2
export OTEL_SERVICE_NAME=demo2

export SPRING_PROFILES_ACTIVE=setup1

export OTEL_METRICS_EXPORTER=otlp
export OTEL_TRACES_EXPORTER=otlp
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317

# export OTEL_PROPAGATORS=b3

java \
  -javaagent:./observability/setup2/opentelemetry-javaagent.jar \
  -jar target/demo-sb3.jar
