FROM docker/compose:latest

RUN apk add --update --upgrade --no-cache  git openjdk11 maven
RUN git clone https://git@gitlab.com/openid/conformance-suite.git
ADD . .

WORKDIR ./conformance-suite
RUN mvn install -DskipTests
RUN echo $PATH
RUN chmod 755 ./target/fapi-test-suite.jar
RUN ls -l ./target
WORKDIR /
ENTRYPOINT [ "docker-compose", "up" ]