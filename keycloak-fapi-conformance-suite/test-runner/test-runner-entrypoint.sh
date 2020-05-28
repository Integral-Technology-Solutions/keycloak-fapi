#!/bin/sh

sleep ${TEST_DELAY}

# Some volumes are not playing nice when running Docker in Docker - docker cp is the workaround
echo 'copying json config files'
docker cp $(docker ps -f name=conformance_suite --quiet):/fapi-conformance-suite-configs/. /json-config

echo 'running tests'
mvn -DconfigFile=${FAPI_TEST_JSON_CONFIG_FILENAME} test

echo 'copying test report back to host'
docker cp /target/surefire-reports/. $(docker ps -f name=conformance_suite --quiet):/test-runner/report