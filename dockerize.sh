#!/bin/bash

docker rmi demo-sb3
mvn -ntp verify -DskipTests jib:dockerBuild