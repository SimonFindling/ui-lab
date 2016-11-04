# ui-lab
This is a template for creating a prototype of a cloud and sass solution with Spring Cloud and Docker. 
This project is part of the masters course Graphical user interfaces.

## Configuration
### Local setup
If you want to build and set up the dockers containers locally:
```bash
export GROUP="<your group name>"
./build_locally.sh
# set it up locally
docker-compose -f docker-compose.yml up -d
```

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
11. TODO push to dev stage immediately + fill docker-compose.yml file

## Steps for adding a new service
1. Add new service as `module` in root `pom.xml`. It has to have a `Dockerfile` in the module root.
2. Add docker build config in `.travis.yml` for the new service
3. Add service to `build_locally.sh`
4. Add service to `docker-compose.yml`
5. Add a new route in `application.yaml` of the api-gateway


## Documentation of API Gateway
Once the API Gateway is set up via 
- `cd api-gateway && mvn spring-boot:run` or 
- `docker run -p8081:8081 <your group name>/api-gateway` 
the documentation can be accessed through `http://localhost:8081/docs/api-guide.html`