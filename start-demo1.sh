#!/bin/bash

export SERVER_PORT=8080
export APPLICATION_CLIENT_PORT=8081
export SPRING_APPLICATION_NAME=demo1
export OTEL_SERVICE_NAME=demo1

export SPRING_PROFILES_ACTIVE=setup1

export OTEL_METRICS_EXPORTER=none
export OTEL_TRACES_EXPORTER=zipkin
export OTEL_EXPORTER_TRACES_ENDPOINT=http://localhost:9411/api/v2/spans # zipkin ingest

export OTEL_PROPAGATORS=b3

java \
  -jar target/demo-0.0.1-SNAPSHOT.jar
