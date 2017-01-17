# ui-lab
This is a template for creating a prototype of a cloud and sass solution with Spring Cloud and Docker. 
This project is part of the masters course Graphical user interfaces.

## Configuration
### Local setup
If you want to build and set up the dockers containers locally:
```bash
./build_locally.sh 
# or 
mvn clean install
# shut old services down and remove old images
docker-compose stop && docker-compose rm -f
# set containers up and follow logs
docker-compose up -d --remove-orphans && docker-compose logs -tf
```
Check `curl localhost:8081/docs/api-guide.html`, which can take some time
until everything is set up. Consider `docker logs -tf <container-hash>` for the logs.

### Setup for [Travis](https://travis-ci.org)
1. Fork this repository
2. Create a branch from master for changes you want to push back to the template (e.g. OAuth)
3. Set up a [Docker Hub](https://hub.docker.com/) account
  1. Create new repositories on Docker Hub for each mircoservice. Currently api-gateway, login-microservice, discovery-service and template-project. 
5. Log into  `travis-ci.org` with the github account where your fork is hosted (should be a public repository)
6. Activate the repository in `travis`
7. Go to the settings of your travis repository.
8. Under `Environment Variables` set `DOCKER_USER` and `DOCKER_PASS` with your information. See [here](https://cinhtau.net/wp/use-travis-ci-in-github-to-build-and-deploy-to-dockerhub/#Deploy_to_Dockerhub) for details but **use names as specified here**.
9. Make a change, commit, push and see if `travis` builds
10. If the build was successful, check your dockerhub account if the images appears
11. Pull if from docker hub.
12. The automatic pull to your server will be explained in the **Update Images via Watchtower** section.

### Setup for server
#### Intial setup
1. Use the `docker-compose.yml` file from root and copy it onto the remote server
2. Run `docker compose up -d` there. **Note:** that it pulls per default the **:latest** version/tag of the container. Specify another version if you're working on a branch. 

#### Update Images via Watchtower
Another alternative is [Watchtower](https://github.com/CenturyLinkLabs/watchtower). Watchtower runs as an docker container and checks all few minutes, if a new version of your running containers is available. If a new version is available, watchtower automatically pulls it and restarts the container.

To start Watchtower for watching all running containers use (without arguments):
```bash
$ docker run -d --name watchtower -v /var/run/docker.sock:/var/run/docker.sock centurylink/watchtower
```
Watchtower only updates running containers, therefore make sure the containers you want to update are already running.

## Steps for adding a new service
1. Add new service as `module` in root `pom.xml` so the travis build will still work. 
2. In the POM of your new service add the following:

    ```
	<parent>
		<groupId>de.hska.uilab</groupId>
		<artifactId>ui-lab</artifactId>
		<version>0.0.1-SNAPSHOT</version>
	</parent>
    ```
3. This adds the root `pom.xml` as parent-POM, which adds the maven-docker-plugin and spring boot to your service. The `template-project` shows how your new service `pom.xml` should look
4. Add service to `docker-compose.yml`
5. a new route for the api-gateway is added automatically, when your service is connected to eureka with the `@EnableDiscoveryClient` Annotation
6. To use the configuration service, add your application name and the config service url to your bootstrap.yml file like this 
```
spring:
  application:
    name: <application-name>
  cloud:
    config:
      uri: http://config-service:8888
      fail-fast: true
```
7. Now you can create a config file (replacing your local application.yml) in `config-service/src/main/resources/shared`. This file must be named like your `application name` in `bootstrap.yml` and must have the file ending `.yml`. Please note that config-service provides a standard configuration for every service in `shared/application.yml`. You only need to add those entries to your service configuration file, that are not already included in the standard configuration.


## Documentation of API Gateway
### With running Spring container
Once the API Gateway is set up via 
- `cd api-gateway && mvn spring-boot:run` or 
- `docker run -p8081:8081 uilab/api-gateway` 
the documentation can be accessed through `http://localhost:8081/docs/api-guide.html`

### Statically
## How to compile the api gateway proposal
 You need to install [asciidoctor](http://asciidoctor.org) first. Then run
 ```bash
 $ asciidoctor api-gateway/src/main/asciidoc/api-guide.adoc
 $ open api-gateway/src/main/asciidoc/api-guide.html
 ```

## Routing
- With `sidecar` the route to a service is defined by its `spring.application.name` in the `bootstrap.properties` or `yml`.
Then the defined resources in the controllers can be accessed. Here is an example. 
```bash
$ curl -D- -X GET localhost:8081/login/admin/admin
```
- It is possible to see all current routings under `<gateway-url>/routes`. See [here](http://212.227.198.46:8081/routes) for a working gateway server.

## Web Access Url
- Group `noname`: `http://82.165.207.147:8081/docs/api-guide.html`
- Group `sfindling`: `http://212.227.198.46:8081/docs/api-guide.html`

# Inspiration / Props
- [piggymetrics](https://github.com/sqshq/PiggyMetrics)
- [spring-cloud-example](https://github.com/kbastani/spring-cloud-microservice-example)
- [docker-hook](https://github.com/schickling/docker-hook)
- [Watchtower](https://github.com/CenturyLinkLabs/watchtower)
