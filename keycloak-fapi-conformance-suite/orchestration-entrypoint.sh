#!/bin/sh
/bin/cp /https/server.pem /keycloak-gatekeeper/tls.crt
/bin/cp /https/server-key.pem /keycloak-gatekeeper/tls.key
/bin/cp /https/client-ca.pem /keycloak-gatekeeper/client-ca.crt
/bin/cp /https/server.pem /keycloak/tls.crt
/bin/cp /https/server-key.pem /keycloak/tls.key
/bin/cp /https/client-ca.pem /keycloak/client-ca.crt
git clone ${OPENID_CONFORMANCE_SUITE_GIT_URL} && yes | cp -rf Dockerfile-server ./conformance-suite/Dockerfile && yes | cp -rf server-entrypoint.sh ./conformance-suite/server-entrypoint.sh
if [ $KEEP_ALIVE == true ]; then docker-compose up --build; else docker-compose up --build --exit-code-from test_runner; fi