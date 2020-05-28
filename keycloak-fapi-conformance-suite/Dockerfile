FROM docker/compose:latest
RUN apk add --update --upgrade --no-cache  git
ADD . .
ENTRYPOINT [ "sh", "./orchestration-entrypoint.sh" ]