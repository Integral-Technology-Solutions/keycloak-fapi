#!/bin/sh
# In case the conformance suite needs to be updated in the future, it will be handy to keep these cp commands
cp ./automation-files/Dockerfile-server ./conformance-suite/Dockerfile
cp ./automation-files/server-entrypoint.sh ./conformance-suite/server-entrypoint.sh
cp ./automation-files/run-tests.sh ./conformance-suite/
chmod +x ./conformance-suite/run-tests.sh
cp ./fapi-conformance-suite-configs/fapi-rw-id2-with-private-key-PS256-PS256.json ./conformance-suite/.gitlab-ci/fapi-rw-id2-with-private-key-PS256-PS256.json
if [ $KEEP_ALIVE == true ]; then docker-compose up --build; else docker-compose up --build --exit-code-from test_runner; fi