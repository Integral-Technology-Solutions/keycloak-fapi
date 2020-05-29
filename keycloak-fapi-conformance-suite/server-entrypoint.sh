#!/bin/sh
mvn package -DskipTests
java -jar target/fapi-test-suite.jar \
    -Djava.security.egd=file:/dev/./urandom \
    --fintechlabs.base_url=https://host.docker.internal:8443 \
    --fintechlabs.devmode=true \
    --fintechlabs.startredir=true