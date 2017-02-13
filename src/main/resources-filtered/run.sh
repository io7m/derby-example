#!/bin/sh
# ${project.name} ${project.version}

FELIX_VERSION="${org.apache.felix.version}"

exec java \
  -XX:+PrintGCDetails \
  -XX:+PrintGCTimeStamps \
  -Dgosh.args="--nointeractive" \
  -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:2324 \
  -jar bin/org.apache.felix.main-${FELIX_VERSION}.jar
