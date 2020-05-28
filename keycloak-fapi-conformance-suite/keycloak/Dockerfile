ARG KEYCLOAK_BASE_IMAGE
FROM ${KEYCLOAK_BASE_IMAGE}

USER root

# Realm config
ADD --chown=jboss:jboss realm.json /opt/jboss/

ADD tls.crt /etc/x509/https/
ADD tls.key /etc/x509/https/
ADD client-ca.crt /etc/x509/https/

ENV KEYCLOAK_IMPORT=/opt/jboss/realm.json
ENV X509_CA_BUNDLE=/etc/x509/https/client-ca.crt