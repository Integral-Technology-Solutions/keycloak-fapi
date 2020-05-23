FROM docker/compose:latest
ARG OPENID_CONFORMANCE_SUITE_GIT_URL
# install git - docker/compose image is alpine under the covers
RUN apk add --update --upgrade --no-cache  git
# add whole project into orchestration container
ADD . .
# clone openId conformance suite and prepare to be run with keycloak test environment
RUN git clone ${OPENID_CONFORMANCE_SUITE_GIT_URL} && yes | cp -rf Dockerfile-server ./conformance-suite/Dockerfile
# entrypoint to run entire test suite within this orchestration container
ENTRYPOINT [ "docker-compose", "up", "--build" ]