#!/bin/sh
cp /https/server.pem /keycloak-gatekeeper/tls.crt
cp /https/server-key.pem /keycloak-gatekeeper/tls.key
cp /https/client-ca.pem /keycloak-gatekeeper/client-ca.crt
cp /https/server.pem /keycloak/tls.crt
cp /https/server-key.pem /keycloak/tls.key
cp /https/client-ca.pem /keycloak/client-ca.crt
git clone ${OPENID_CONFORMANCE_SUITE_GIT_URL}
yes | cp -rf /automation-files/Dockerfile-server /conformance-suite/Dockerfile
yes | cp -rf /automation-files/server-entrypoint.sh ./conformance-suite/server-entrypoint.sh
cp /automation-files/run-tests.sh ./conformance-suite/
cp /fapi-conformance-suite-configs/fapi-rw-id2-with-private-key-PS256-PS256.json /conformance-suite/.gitlab-ci/fapi-rw-id2-with-private-key-PS256-PS256.json
if [ $KEEP_ALIVE == true ]; then docker-compose up --build; else docker-compose up --build --exit-code-from test_runner; fi