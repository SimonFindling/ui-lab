package de.hska.uilab.product.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ProductsMock {
    private Product p1 = new Product(1, 1, "Product1", "path/p1", "Informaton p1", 1000);
    private Product p2 = new Product(2, 1, "Product2", "path/p2", "Informaton p2", 2000);
    private Product p3 = new Product(3, 1, "Product3", "path/p3", "Informaton p3", 3000);

    private static List<Product> ALL = new ArrayList<>();
    private AtomicLong id = new AtomicLong(1);

    public ProductsMock() {

        // mavogel: deactivate for api docu
        //		ALL.add(p1);
        //		ALL.add(p2);
        //		ALL.add(p3);
    }

    public List<Product> getAllProducts() {
        return ALL;
    }

    public Product getProductById(int id) {
        for (Product product : ALL) {
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
        return ALL.add(product);
    }

    public boolean changeProduct(Product product) {
        Product productById = getProductById(product.getId());
        if (productById != null) {
            ALL.remove(productById);
            ALL.add(product);
            return true;
        }
        return false;
    }

    public boolean deleteProductById(int productId) {
        Product productById = getProductById(productId);
        if (productById != null) {
            return ALL.remove(productById);
        }
        return false;
    }

    public void deleteAllProducts() {
            ALL.clear();
    }

}
