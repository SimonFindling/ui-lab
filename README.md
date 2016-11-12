# ui-lab
This is a template for creating a prototype of a cloud and sass solution with Spring Cloud and Docker. 
This project is part of the masters course Graphical user interfaces.

## Configuration
### Local setup
If you want to build and set up the dockers containers locally:
```bash
# build all local docker images
export GROUP=<your group name>
export SERVER_URL=localhost
./build_locally.sh
# Note the docker-compose.yml also need the ENV vars
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
$ export GROUP="<your group name>"
$ export SERVER_URL="<server ip>"
```
2. Use the `docker-compose.yml` file from root and copy it onto the remote server
3. Run `docker compose up -d` there. **Note:** that it pulls per default the **:latest** version/tag
of the container. Specify another version if you're working on a branch. 

#### Add Webhooks
I suggest using [docker-hook](https://github.com/schickling/docker-hook). Each time
a new version of the image is pushed by travis to docker hub the webhook is
triggered. Read all steps first please!

1. Prepare the server using the following [instructions](https://github.com/schickling/docker-hook#1-prepare-your-server)
2. Maybe the following commands will be necessary
```bash
$ sudo apt-get install python python-pip
$ sudo pip install requests
```
3. Copy the `deploy_hook.sh` script in the HOME dir and register it via 
```bash
$ docker-hook -t <auth-token> -c sh ${HOME}/deploy_hooks.sh &
```
4. Add the webhook url described in the instruction link above to the new image in docker hub. Maybe you
have to run the `deploy_hook.sh` manually once or trigger the travis build again after the
webhook url has been added.

#### Update Images via Watchtower
Another alternative is [Watchtower](https://github.com/CenturyLinkLabs/watchtower). Watchtower runs as an docker container and checks all few minutes, if a new version of your running containers is available. If a new version is available, watchtower automatically pulls it and restarts the container.

To start Watchtower use:
```bash
$ docker run -d --name watchtower -v /var/run/docker.sock:/var/run/docker.sock centurylink/watchtower nginx redis
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

# TODOS
- Get `zuul` running with `serviceId` instead of `urls`. Read [here](https://github.com/sqshq/PiggyMetrics#api-gateway)
- Start the docker-hook command so that it's still running although the terminal is closed.

# Inspiration / Props
- [piggymetrics](https://github.com/sqshq/PiggyMetrics)
- [spring-cloud-example](https://github.com/kbastani/spring-cloud-microservice-example)
- [docker-hook](https://github.com/schickling/docker-hook)
- [Watchtower](https://github.com/CenturyLinkLabs/watchtower)