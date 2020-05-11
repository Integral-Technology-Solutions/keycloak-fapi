#!/bin/sh

SCRIPTDIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
CONFORMANCE_SUITE_GIT_URL='https://git@gitlab.com/openid/conformance-suite.git'
#KEYCLOAK_BASE_IMAGE='jboss/keycloak:6.0.1'

setKeycloakVersion() {
  if [ $# -eq 0 ]
    then
      KEYCLOAK_BASE_IMAGE='jboss/keycloak:latest'
  else
    KEYCLOAK_BASE_IMAGE=$1
  fi
}

cleanup() {
  echo 'Attempting to clean up project files'
  cd $SCRIPTDIR
  docker-compose stop || true
  docker-compose kill || true
  $(rm -rf conformance-suite || true) && $(rm docker-compose.override.yml || true)
  echo 'Clean up attempt complete'
}
trap cleanup EXIT


setKeycloakVersion
echo "Running OpenID FAPI Conformance Test Suite against Keycloak image $KEYCLOAK_BASE_IMAGE"

cleanup

git clone $CONFORMANCE_SUITE_GIT_URL

cd $SCRIPTDIR/conformance-suite
mvn clean package -DskipTests
mv docker-compose.yml docker-compose.replaced.yml

cd $SCRIPTDIR
cp files/openid-docker-compose.override.yml docker-compose.override.yml
export KEYCLOAK_BASE_IMAGE=$KEYCLOAK_BASE_IMAGE
docker-compose up