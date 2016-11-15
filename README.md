# ui-lab
This is a template for creating a prototype of a cloud and sass solution with Spring Cloud and Docker. 
This project is part of the masters course Graphical user interfaces.

## Configuration
### Local setup
If you want to build and set up the dockers containers locally:
```bash
# build all local docker images
export DOCKER_USER=<your group name>
export TAG=latest
./build_locally.sh (or mvn install in the main project)
# Note the docker-compose.yml also needs the ENV vars
docker-compose up -d
```
Check `curl localhost:8081/docs/api-guide.html`, which can take some time
until everything is set up. Consider `docker logs -tf <container-hash>` for the logs.

### Setup for [Travis](https://travis-ci.org)
1. Fork this repository
2. Create a branch from master for changes you want to push back to the template (e.g. OAuth)
3. Set up a [Docker Hub](https://hub.docker.com/) account
4. Log into  `travis-ci.org` with the github account where your fork is hosted (should be a public repository)
5. Activate the repository in `travis`
6. Edit `.travis.yml` file and replace `GROUP` env with the name of your group, which should be the same like
your docker hub account.
7. Create secrets: e.g `travis encrypt DOCKER_EMAIL=mail@example.com` and replace them in `.travis.yml`. 
 See [here](https://docs.travis-ci.com/user/environment-variables/#Encrypting-environment-variables) for details.
8. Make a change, commit, push and see if `travis` builds
9. If the build was successful, check your dockerhub account if the images appears
10. Pull if from docker hub.
11. The Push to your server will be explained in the **Add webhooks** section.

### Setup for server
#### Intial setup
1. Set up ENV Vars
```bash
$ export DOCKER_USER="<your group name>"
$ export TAG=latest
```
2. Use the `docker-compose.yml` file from root and copy it onto the remote server
3. Run `docker compose up -d` there. **Note:** that it pulls per default the **:latest** version/tag
of the container. Specify another version if you're working on a branch. 

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
5. Add a new route in `application.yml` of the api-gateway
6. Add the hook url to the image at docker hub or initially run the docker container of the service on your server with Watchtower


## Documentation of API Gateway
### With running Spring container
Once the API Gateway is set up via 
- `cd api-gateway && mvn spring-boot:run` or 
- `docker run -p8081:8081 <your group name>/api-gateway` 
the documentation can be accessed through `http://localhost:8081/docs/api-guide.html`

### Statically
## How to compile the api gateway proposal
 You need to install [asciidoctor](http://asciidoctor.org) first. Then run
 ```bash
 # compile
 asciidoctor api-gateway/src/main/asciidoc/api-guide.adoc
 # open it
 open api-gateway/src/main/asciidoc/api-guide.html
 ```

## Routing
- With `sidecar` the route to a service is defined by its `spring.application.name` in the `bootstrap.properties` or `yml`.
Then the defined resources in the controllers can be accessed. Here is an example. 
```bash
curl -D- -X GET localhost:8081/login/login/admin/admin
```
- It is possible to see all current routings under `<gateway-url>/routes`. See [here](http://212.227.198.46:8081/routes) for a working gateway server.
# Inspiration / Props
- [piggymetrics](https://github.com/sqshq/PiggyMetrics)
- [spring-cloud-example](https://github.com/kbastani/spring-cloud-microservice-example)
- [docker-hook](https://github.com/schickling/docker-hook)
- [Watchtower](https://github.com/CenturyLinkLabs/watchtower)