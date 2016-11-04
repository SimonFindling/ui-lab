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
- Fork this repository
- Create a branch from master for changes you want to Push back to the template (e.g. OAuth)
- Set up a [Docker Hub](https://hub.docker.com/) account
- Log into  `travis-ci.org` with the github account where your fork is hosted (should be a public repository)
- Activate the repository in `travis`
- Edit `.travis.yml` file and replace `GROUP` env with the name of your group
- Create secrets: e.g `travis encrypt DOCKER_EMAIL=mail@example.com` and replace them in `.travis.yml`. 
 See [here](https://docs.travis-ci.com/user/environment-variables/#Encrypting-environment-variables) for details.
- Make change, commit, push and see if `travis` builds
- If successfully build, check your dockerhub account of the images appears
- Pull if from docker hub.
- TODO push to dev stage immediately + fill docker-compose.yml file


## Documentation of API Gateway
Once the API Gateway is set up via 
- `cd api-gateway && mvn spring-boot:run` or 
- `docker run -p8081:8081 <your group name>/api-gateway` 
the documentation can be accessed through `http://localhost:8081/docs/api-guide.html`