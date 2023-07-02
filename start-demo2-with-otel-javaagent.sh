#!/bin/bash

export SERVER_PORT=8081
export APPLICATION_CLIENT_PORT=8080
export SPRING_APPLICATION_NAME=demo2
export SPRING_PROFILES_ACTIVE=setup2

export OTEL_JAVA_AGENT_VERSION=1.27.0
export OTEL_SERVICE_NAME=demo2
export OTEL_RESOURCE_ATTRIBUTES="deployment.environment=production,service.namespace=sb3,service.version=1.1,service.instance.id=1"

export OTEL_METRICS_EXPORTER=otlp
export OTEL_TRACES_EXPORTER=none
export OTEL_LOGS_EXPORTER=none

# this setting is for using an OTEL collector running locally
# export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317

# these 2 vars and 3 settings are for grafana cloud
export GRAFANA_INSTANCE_ID="9999999"
export GRAFANA_API_KEY="the-token-goes-here"
export OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf
export OTEL_EXPORTER_OTLP_ENDPOINT='https://otlp-gateway-prod-eu-west-2.grafana.net/otlp'
export OTEL_EXPORTER_OTLP_HEADERS="Authorization=Basic $(echo -n $GRAFANA_INSTANCE_ID:$GRAFANA_API_KEY | base64 -w 0)"

# export OTEL_PROPAGATORS=b3

java \
  -javaagent:./target/opentelemetry-javaagent-${OTEL_JAVA_AGENT_VERSION}.jar \
  -jar target/demo-sb3.jar
