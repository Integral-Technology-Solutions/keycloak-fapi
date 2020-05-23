FROM keycloak/keycloak-gatekeeper:6.0.1

ADD config.yml /opt/config.yml
ADD docker-entrypoint.sh /usr/local/sbin/

ENTRYPOINT [ "/usr/local/sbin/docker-entrypoint.sh" ]

