FROM alpine:latest
ADD . .
RUN apk add --update --upgrade --no-cache maven openjdk11 docker-cli
CMD sleep 1m ; \
    docker cp $(docker ps -f name=conformance_suite --quiet):/fapi-conformance-suite-configs/. /json-config ; \
    mvn -DconfigFile=${FAPI_TEST_JSON_CONFIG_FILENAME} test ;  \
    docker cp /target/surefire-reports/. $(docker ps -f name=conformance_suite --quiet):/test-runner/report