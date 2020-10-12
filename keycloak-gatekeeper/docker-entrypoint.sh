#!/bin/sh
/opt/keycloak-gatekeeper \
    --config /opt/config.yml \
    --listen 0.0.0.0:10443 \
    --discovery-url https://${KEYCLOAK_FQDN}/auth/realms/${KEYCLOAK_REALM}