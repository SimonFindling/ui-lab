package de.hska.uilab.product.schema;

import java.util.ArrayList;
import java.util.List;

public class ProductsMock {
	private Product p1 = new Product(1, 1, "Product1", "path/p1", "Informaton p1", 1000);
	private Product p2 = new Product(2, 1, "Product2", "path/p2", "Informaton p2", 2000);
	private Product p3 = new Product(3, 1, "Product3", "path/p3", "Informaton p3", 3000);

	private List<Product> all = new ArrayList<>();

	public ProductsMock() {
		all.add(p1);
		all.add(p2);
		all.add(p3);
	}

	public List<Product> getAllProducts() {
		return all;
	}

	public Product getProductById(int id) {
		for (Product product : all) {
			if (product.getId() == id) {
				return product;
			}
		}
		return null;
	}

	public boolean createProduct(Product product) {
		Product productById = getProductById(product.getId());
		if (productById != null) {
			return false;
		}
		return all.add(product);
	}

	public boolean changeProduct(Product product) {
		Product productById = getProductById(product.getId());
		if (productById != null) {
			all.remove(productById);
			all.add(product);
			return true;
		}
		return false;
	}

	public boolean deleteProductById(int productId) {
		Product productById = getProductById(productId);
		if (productById != null) {
			return all.remove(productById);
		}
		return false;
	}

}
