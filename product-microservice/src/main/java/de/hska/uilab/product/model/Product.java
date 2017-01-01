package de.hska.uilab.product.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String name;
    private double price;
    private String image;
    private String description;
	private String ean;
	
	protected Product() {}

    public Product(String name, double price, String image, String description, String ean) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.description = description;
        this.ean = ean;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    
    public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

    public Long getId() {
        return id;
    }
}
