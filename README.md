# keycloak-fapi

The purpose of this project is to run [Conformance Testing for FAPI Read/Write OPs](https://openid.net/certification/fapi_op_testing/) with Keycloak server, find issues and improve them.
The final goal is, of course, to receive official FAPI OpenID Provider Certifications.

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
See instructions in [Run FAPI Conformance test plan](#Run-FAPI-Conformance-test-plan) 
section for running the tests manually in your browser.


To run the test suite and have all containers exit upon test completion, instead run the following command from the project basedir.
Note, the OpenID FAPI conformance test interface will not be available.

```
docker-compose up --build --exit-code-from test_runner
```

The following options can be set as environment variables before the above command:

* `KEYCLOAK_BASE_IMAGE` (default: jboss/keycloak:latest)
    * The keycloak image version used in the test suite
* `FAPI_TEST_JSON_CONFIG_FILENAME` (default: fapi-rw-id2-with-private-key-RS256-PS256.json)
    * Refers to a JSON config file in 
    [./keycloak-fapi-conformance-suite/fapi-conformance-suite-configs](./keycloak-fapi-conformance-suite/fapi-conformance-suite-configs) 
    directory
* `MAVEN_HOME` (default: ~/.m2)
    * The path of the host's local maven repo (e.g. /path/to/.m2) 


**Example:**
```
KEYCLOAK_BASE_IMAGE=jboss/keycloak:6.0.1 KEEP_ALIVE=true docker-compose up --build
```

Once `test_runner` service has finished and exited, test reports will be copied to the 
[./conformance-suite/report](./conformance-suite/report) directory.

Clean up exited containers after tests are run with the following command:
```
docker rm $(docker ps -a -f status=exited -q)
```

If running with with docker-compose up --build, use the following command to stop and clean up all containers after tests have been run in the browser:
```
docker exec -it $(docker ps -f name=conformance_suite --quiet) sh -c "docker-compose stop" && docker-compose down --rmi all --remove-orphans && docker rm $(docker ps -a -f status=exited -q)

OR FOR WINDOWS:
winpty docker exec -it $(docker ps -f name=conformance_suite --quiet) sh -c "docker-compose stop" && docker-compose down --rmi all --remove-orphans && docker rm $(docker ps -a -f status=exited -q)
```

### Modify your `hosts` file

To access to Keycloak and Resource server with FQDN, modify your `hosts` file in your local machine as follows.

```
127.0.0.1 as.keycloak-fapi.org rs.keycloak-fapi.org conformance-suite.keycloak-fapi.org
```

### Run FAPI Conformance test plan

1. Open https://conformance-suite.keycloak-fapi.org
2. Choose **FAPI-RW-ID2: with private key and mtls holder of key Test Plan** in test plans
3. Click `JSON` tab and paste content of [./keycloak-fapi-conformance-suite/fapi-conformance-suite-configs/fapi-rw-id2-with-private-key-RS256-PS256.json](./keycloak-fapi-conformance-suite/fapi-conformance-suite-configs/fapi-rw-id2-with-private-key-RS256-PS256.json).
4. Click `Start Test Plan` button and follow the instructions. To proceed with the tests, You can authenticate using `john` account with password `john`.

**Note: There is a known issue when using PS256/ES256 as request object signature alg. To proceed with all the test cases, we need to use RS256 instead of them currently.**


## How to deploy the servers on the internet TBC

If you would like to deploy on the internet, follow instructions below which use Amazon Linux 2 on Amazaon EC2 as an example.

Install Docker.

```
sudo yum update -y
sudo yum install -y docker
sudo service docker start
sudo usermod -a -G docker ec2-user
```

Install Docker Compose.

```
sudo curl -L "https://github.com/docker/compose/releases/download/1.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

Clone sources from GitHub.

```
git clone https://gitlab.com/openid/conformance-suite.git
git clone https://github.com/jsoss-sig/keycloak-fapi.git
```

Export environment variables with the FQDN which you want to use.

```
export KEYCLOAK_FQDN=as.keycloak-fapi.org
export RESOURCE_FQDN=rs.keycloak-fapi.org
export CONFORMANCE_SUITE_FQDN=conformance-suite.keycloak-fapi.org
```

Modify `conformance-suite/docker-compose.xml` as follows.

**Note: We need to set `fintechlabs.base_url` with public FQDN to change from `https://localhost:8443`** 

```
       context: ./server-dev
     volumes:
      - ./target/:/server/
-    command: java -jar /server/fapi-test-suite.jar --fintechlabs.devmode=true --fintechlabs.startredir=true
+    command: java -jar /server/fapi-test-suite.jar --fintechlabs.devmode=true --fintechlabs.startredir=true --fintechlabs.base_url=https://${CONFORMANCE_SUITE_FQDN}
     links:
      - mongodb:mongodb
      - microauth:microauth
```

Build FAPI Conformance suite server and boot the all containers using Docker Compose.

```
cd conformance-suite
mvn clean package
docker-compose up -d
```

Generate server certificates, Keycloak realm config and FAPI Conformance suite configs with your FQDN.

```
cd ../keycloak-fapi
./setup-fqdn.sh
```

Boot the containers using Docker Compose.

```
docker-compose up -d
```


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

**Custom files in the conformance suite**
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

## License

* [Apache License, Version 2.0](./LICENSE)

