package de.hska.uilab.product;

import de.hska.uilab.product.model.Product;
import de.hska.uilab.product.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ProductMicroserviceApplication implements ApplicationRunner {

	@Autowired
	ProductRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(ProductMicroserviceApplication.class, args);
	}

	// Fill Database with some demo Products..
	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
/*		repository.save(new Product("HTC Vive"));
		repository.save(new Product("Oculus Rift"));
		repository.save(new Product("Sony PlayStation VR"));
		repository.save(new Product("Samsung Gear VR"));
		repository.save(new Product("Google Daydream View"));
		repository.save(new Product("Microsoft HoloLens"));
		repository.save(new Product("Fove 0"));
		repository.save(new Product("Razer OSVR HDK 2"));*/
	}

}
