FROM docker/compose:latest
RUN apk add --update --upgrade --no-cache  git
ADD . .
RUN git clone https://git@gitlab.com/openid/conformance-suite.git && yes | cp -rf Dockerfile-server ./conformance-suite/Dockerfile
ENTRYPOINT [ "docker-compose", "up", "--build" ]