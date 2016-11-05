# ui-lab
This is a template for creating a prototype of a cloud and sass solution with Spring Cloud and Docker. 
This project is part of the masters course Graphical user interfaces.

## Configuration
### Local setup
If you want to build and set up the dockers containers locally:
```bash
# build all local docker images
export GROUP=<your group name>
# export SERVER_URL=localhost
./build_locally.sh
# but set $GROUP and $SERVER_URL before!
docker-compose up -d
```
Check `curl localhost:8081/docs/api-guide.html`, which can take some time
until everything is set up.

### Setup for [Travis](https://travis-ci.org)
1. Fork this repository
2. Create a branch from master for changes you want to Push back to the template (e.g. OAuth)
3. Set up a [Docker Hub](https://hub.docker.com/) account
4. Log into  `travis-ci.org` with the github account where your fork is hosted (should be a public repository)
5. Activate the repository in `travis`
6. Edit `.travis.yml` file and replace `GROUP` env with the name of your group
7. Create secrets: e.g `travis encrypt DOCKER_EMAIL=mail@example.com` and replace them in `.travis.yml`. 
 See [here](https://docs.travis-ci.com/user/environment-variables/#Encrypting-environment-variables) for details.
8. Make change, commit, push and see if `travis` builds
9. If successfully build, check your dockerhub account of the images appears
10. Pull if from docker hub.
11. Push to dev/prod stage immediately

TODO check eureka server. services not registered! Really need a serverip?

### Setup for server
#### Intial setup
1. Set up ENV Vars
```bash
export GROUP="<your group name>"
export SERVER_URL="<server ip>"
```
2. Use the `docker-compose.yml` file from root and copy it onto the remote server
3. Run `docker compose up -d` there

#### Add Webhooks
I suggest using [docker-hook](https://github.com/schickling/docker-hook). Each time
a new version of the image is pushed by travis to docker hub the webhook is
trigged. Read all steps first please!
1. Prepare the server using the following [instructions](https://github.com/schickling/docker-hook#1-prepare-your-server)
2. Maybe a `sudo apt-get install python python-pip` and `sudo pip install requests` will be necessary.
<!--2. TODO remove: -->
<!--Add a hook for each docker image `$ docker-hook -t <auth-token> -c <command>` where `<command>`-->
<!--could be `sh ./deploy.sh` with the script-->
<!--```bash-->
<!--#! /bin/bash-->

<!--IMAGE="yourname/app"-->
<!--docker ps | grep $IMAGE | awk '{print $1}' | xargs docker stop-->
<!--docker pull $IMAGE-->
<!--#docker run -p<PORT>:<PORT> -d $IMAGE-->
<!--# or-->
<!--docker compose up -d-->
<!--```-->
3. Copy the `deploy_hook.sh` script and register it via `$ docker-hook -t <auth-token> -c sh ${HOME}/deploy_hooks.sh &`

## Steps for adding a new service
1. Add new service as `module` in root `pom.xml`. It has to have a `Dockerfile` in the module root.
2. Add docker build config in `.travis.yml` for the new service
3. Add service to `build_locally.sh`
4. Add service to `docker-compose.yml`
5. Add a new route in `application.yml` of the api-gateway
6. Add the hook url to the new images on docker hub when it's available there.


## Documentation of API Gateway
Once the API Gateway is set up via 
- `cd api-gateway && mvn spring-boot:run` or 
- `docker run -p8081:8081 <your group name>/api-gateway` 
the documentation can be accessed through `http://localhost:8081/docs/api-guide.html`

# TODOS
- Get `zuul` running with `serviceId` instead of `urls`.
- FIX `curl -D- ${SERVER_URL}:8081/login-api/login/admin/admin` which causes atm 
`Caused by: com.netflix.client.ClientException: Load balancer does not have available server for client: login-microservice`
- Start the docker-hook command so that it's still running although the terminal is closed.

# Inspiration / Props
- [piggymetrics](https://github.com/sqshq/PiggyMetrics)
- [spring-cloud-example](https://github.com/kbastani/spring-cloud-microservice-example)
- [docker-hook](https://github.com/schickling/docker-hook)