FROM maven:3.6.3-openjdk-11-slim
ADD . .
CMD sleep 1m && mvn clean test