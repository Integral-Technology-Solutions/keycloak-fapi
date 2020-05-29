FROM alpine:latest
ADD . .
RUN apk add --update --upgrade --no-cache maven openjdk11 docker-cli curl
CMD [ "sh", "./test-runner-entrypoint.sh" ]