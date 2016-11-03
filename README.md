# ui-lab
This is a template for creating a prototype of a cloud and sass solution with Spring Cloud and Docker. 
This project is part of the masters course Graphical user interfaces.

## Configuration
### Locally
If you want to set up the docker environment locally.

TODO: script for local creation. get rid of parent pom
```bash
cd parent && mvn clean install
cd api-gateway && mvn clean package docker:build
# to test it locally
docker run -p8081:8081 mefiroofficial/api-gateway
```

### Travis
- Set up docker hub account
- Create an organization
- Log into  `travis-ci.org` with the github account your fork is hosted
- Activate the repository there
- Edit `.travis.yml` file for `GROUP` env
- Create secrets. Hint: `travis encrypt DOCKER_EMAIL=mail@example.com --add env.global`

## Documentation of API Gateway
Once the API Gateway is set up via `mvn spring-boot:run` the documentation
can be accessed through `http://localhost:8081/docs/api-guide.html`