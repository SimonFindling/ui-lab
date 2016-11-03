# ui-lab
This is a template for creating a prototype of a cloud and sass solution with Spring Cloud and Docker. 
This project is part of the masters course Graphical user interfaces.

## Configuration
```bash
cd parent && mvn clean install
cd api-gateway && mvn clean package docker:build
# to test it locally
docker run -p8081:8081 mefiroofficial/api-gateway
```

## Documentation of API Gateway
Once the API Gateway is set up via `mvn spring-boot:run` the documentation
can be accessed through `http://localhost:8081/docs/api-guide.html`