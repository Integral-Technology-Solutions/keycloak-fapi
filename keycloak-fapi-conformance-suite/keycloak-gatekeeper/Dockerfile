FROM keycloak/keycloak-gatekeeper:6.0.1
ADD config.yml /opt/config.yml
ADD docker-entrypoint.sh .
ADD tls.crt /etc/x509/https/
ADD tls.key /etc/x509/https/
ADD client-ca.crt /etc/x509/https/
ENTRYPOINT [ "sh", "./docker-entrypoint.sh" ]