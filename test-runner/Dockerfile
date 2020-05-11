FROM alpine:3.4
COPY ./test-runner-service.sh /test-runner-service.sh
RUN echo 'aaaaaaaaaaarrrrgh'
RUN "apk add --no-cache --upgrade curl jq"
RUN "chmod +x /test-runner-service.sh"
RUN "/test-runner-service.sh"