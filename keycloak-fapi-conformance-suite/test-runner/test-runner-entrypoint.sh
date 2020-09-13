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

[ "$AUTOMATE_TESTS" == "true" ] &&
docker exec $(docker ps -f name=default_server --quiet) bash -c "./conformance-suite/run-tests.sh --server-tests-only"