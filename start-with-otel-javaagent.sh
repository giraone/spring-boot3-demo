#!/bin/bash

export OTEL_METRICS_EXPORTER=otlp
java -javaagent:./observability/setup2/opentelemetry-javaagent.jar \
     -jar target/demo-0.0.1-SNAPSHOT.jar
