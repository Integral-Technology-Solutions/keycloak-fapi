# FAPI-SIG (Financial-grade API Security : Special Interest Group)

## Overview

FAPI-SIG is a group whose activity is mainly supporting [Financial-grade API (FAPI)](https://openid.net/wg/fapi/) and its related specifications to keycloak.

FAPI-SIG is open to everybody so that anyone can join it anytime. Nothing special need not to be done to join it. Who want to join it can only access to the communication channels shown below.  All of its activities and outputs are public so that anyone can access them.

FAPI-SIG mainly treats FAPI and its related specifications but not limited to. E.g., Ecosystems employing FAPI for their API Security like UK OpenBanking and Australia Consumer Data Right (CDR).

## Goals

Currently, proposed goals are as follows.

- [Read and Write API Security Profile (FAPI-RW)](https://openid.net/specs/openid-financial-api-part-2-ID2.html)
  - Implement and contribute necessary features
  - Pass FAPI-RW conformance tests (both FAPI-RW OP w/ MTLS and FAPI-RW OP w/ Private Key)
  - Get the certificates

- [Client Initiated Backchannel Authentication Profile (FAPI-CIBA)](https://openid.net/specs/openid-financial-api-ciba-ID1.html)
  - Implement and contribute necessary features
  - Pass FAPI-CIBA conformance tests (only both FAPI-CIBA OP poll w/ MTLS and FAPI-CIBA OP poll w/ Private Key)
  - Get the certificates

## Open Works

Currently, proposed open works are as follows.

- Integrating FAPI conformance tests run into keycloak’s CI/CD pipeline

- Implement [JWT Secured Authorization Response Mode for OAuth 2.0 (JARM)](https://openid.net/specs/openid-financial-api-jarm-ID1.html)

- Implement security profiles for Apps run on mobile devices
  - [RFC 8252 OAuth 2.0 for Native Apps](https://tools.ietf.org/html/rfc8252)
  - [OAuth 2.0 for Browser-Based Apps](https://tools.ietf.org/html/draft-ietf-oauth-browser-based-apps-06)

- Implement [FAPI-RW App2App](https://openid.net/2020/06/23/openid-foundation-announces-fapi-rw-app2app-certification-launched/)

## Communication Channels

Not only FAPI-SIG member but others can communicate with each other by the following ways.

- Mail : Google Group [keycloak developer mailing list](https://groups.google.com/forum/#!topic/keycloak-dev/Ck_1i5LHFrE)
- Chat : Zulip Chat stream ([#dev-sig-fapi](https://keycloak.zulipchat.com/#narrow/stream/248413-dev-sig-fapi))
- Meeting : Web meeting on a regular basis

## Working Repository

All of FAPI-SIG's activity outputs can be stored on [jsoss-sig/keycloak-fapi](https://github.com/jsoss-sig/keycloak-fapi/tree/master/FAPI-SIG) repository in github.

Who want to submit the output needs to send the pull-request to this repository.

## How to run FAPI Conformance suite with Keycloak server in your local machine

### Software requirements

* [Docker CE](https://docs.docker.com/install/)
* [Docker Compose](https://docs.docker.com/compose/)
* JDK and [Maven](https://maven.apache.org/)

### Run FAPI Conformance suite, Keycloak server and Test Runner

Edit `hosts` file as per the [Modify your hosts file](#Modify-your-hosts-file) section

This repository contains default self-signed certificates for HTTPS, client private keys, Keycloak Realm JSON and FAPI Conformance suite config JSONs.
If you would like to use the configurations as it is, you only need to build and boot all the containers using Docker Compose.

Run the following command from the project basedir to start the test suite

```
docker-compose up --build
```
The OpenID FAPI Conformance test interface will then be reachable at [https://localhost:8443](https://localhost:8443).


To run the test suite and have all containers exit upon test completion, instead run the following command from the project basedir.
See instructions in [Run FAPI Conformance test plan](#Run-FAPI-Conformance-test-plan) 
section for running the tests manually in your browser.
```
docker-compose up --build --exit-code-from test_runner
```

The following options can be set as environment variables before the above command:

* `KEYCLOAK_BASE_IMAGE` (default: jboss/keycloak:latest)
    * The keycloak image version used in the test suite
* `AUTOMATE_TESTS` (default: true)
    * Set to false to stop conformance-suite tests automatically running
* `MVN_HOME` (default: ~/.m2)
    * Set to use a custom maven home path


**Example:**
```
KEYCLOAK_BASE_IMAGE=jboss/keycloak:6.0.1 docker-compose up --build
```

Once `test_runner` service has finished and exited, test reports will be copied to the 
[./conformance-suite/report](./conformance-suite/report) directory.

To stop and remove all containers, run the following:
```
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)
```


### Modify your `hosts` file

To access to Keycloak and Resource server with FQDN, modify your `hosts` file in your local machine as follows.

```
127.0.0.1 as.keycloak-fapi.org rs.keycloak-fapi.org conformance-suite.keycloak-fapi.org
```

### Run FAPI Conformance test plan

1. Open https://conformance-suite.keycloak-fapi.org
2. Click `Create a new test plan` button.
3. Choose `FAPI-RW-ID2 (and OpenBankingUK): Authorization server test (latest version)` as Test Plan.
4. Choose `Client Authentication Type` you want to test.
5. Choose `plain_fapi` as FAPI Profile.
6. Choose `plain_response` as FAPI Response Mode.
7. Click `JSON` tab and paste content of the configuration.
  * If you want to use private_key_jwt client authentication, use [fapi-conformance-suite-configs/fapi-rw-id2-with-private-key-PS256-PS256.json](./fapi-conformance-suite-configs/fapi-rw-id2-with-private-key-PS256-PS256.json) or [fapi-conformance-suite-configs/fapi-rw-id2-with-private-key-ES256-ES256.json](./fapi-conformance-suite-configs/fapi-rw-id2-with-private-key-ES256-ES256.json).
  * If you want to use mtls client authentication, use [fapi-conformance-suite-configs/fapi-rw-id2-with-mtls-PS256-PS256.json](./fapi-conformance-suite-configs/fapi-rw-id2-with-mtls-PS256-PS256.json) or [fapi-conformance-suite-configs/fapi-rw-id2-with-mtls-ES256-ES256.json](./fapi-conformance-suite-configs/fapi-rw-id2-with-mtls-ES256-ES256.json).
8. Click `Create Test Plan` button and follow the instructions. To proceed with the tests, You can authenticate using `john` account with password `john`. When rejecting authentication scenario, you can use `mike` account with password `mike`. In this case, you need to click `No` button to cancel the authentication in the consent screen.


## For Developers

**Currently, generators of all configurations are written with bash script and some CLI tools for linux-amd64.**

Run `generate-all.sh` script simply to generate self-signed certificates for HTTPS, client private keys, Keycloak Realm JSON and FAPI Conformance suite config JSONs.

```
./generate-all.sh
```

Now, you can boot a Keycloak server with new configurations.

```
docker-compose up --force-recreate
```

## Run FAPI Conformance test against local built keycloak

If you would like to run FAPI Conformance test against local built keycloak, modify `docker-compose.yml` as follows.

```
@@ -28,6 +28,7 @@ services:
      - ./https/server.pem:/etc/x509/https/tls.crt
      - ./https/server-key.pem:/etc/x509/https/tls.key
      - ./https/client-ca.pem:/etc/x509/https/client-ca.crt
+     - <path to locally built keycloak>:/opt/jboss/keycloak
     ports:
      - "8787:8787"
     environment:
```

It overrides the keycloak of the base image with the one built on the local machine.


##Custom files in the conformance suite

The conformance-suite folder within this repository is a local copy of OpenIds FAPI conformance suite (https://gitlab.com/openid/conformance-suite/).
Incorporating the suite and running the conformance tests within docker-compose requires adding custom files into the base OpenId FAPI conformance suite.
Below is a list of the custom files currently used by the base conformance-suite.
* /conformance-suite/run-tests.sh
  * Script for running / creating test plans
* /conformance-suite/Dockerfile
  * Dockerfile which installs python dependencies, exposes ports and kicks off building the project via the server-entrypoint script 
* /conformance-suite/server-entrypoint.sh
  * Script for building the project with maven
* /conformance-suite/scripts/run-test-plan.py
  * Existing file within the base conformance-suite repo that has been slightly modified to output test results to the file system.


**Running different test plans**

To create custom test plans, add code into /conformance-suite/run-tests.sh as per commented out examples.
JSON configuration files for test plans should be created in /conformance-suite/.gitlab-ci/

## License

* [Apache License, Version 2.0](./LICENSE)

