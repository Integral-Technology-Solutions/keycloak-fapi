#!/bin/sh

# Some volumes are not playing nice when running Docker in Docker - docker cp is the workaround
echo 'copying json config files... will then wait for test environment to be ready'
docker cp $(docker ps -f name=conformance_suite --quiet):/fapi-conformance-suite-configs/. /json-config

# Wait for server to start before running tests - check every 30s
until $(curl -k --output /dev/null --silent --head --fail https://host.docker.internal:8443)
do
    sleep 30
done

# Sometimes keycloak is still starting up at this point if no maven dependencies need downloading in server service
sleep 10

echo 'running tests'
mvn -DconfigFile=${FAPI_TEST_JSON_CONFIG_FILENAME} test

echo 'copying test report back to host'
docker cp /target/surefire-reports/. $(docker ps -f name=conformance_suite --quiet):/test-runner/report