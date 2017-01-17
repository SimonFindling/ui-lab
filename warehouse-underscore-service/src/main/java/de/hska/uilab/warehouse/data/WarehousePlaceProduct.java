package de.hska.uilab.warehouse.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "warehouseplaceproduct")
public class WarehousePlaceProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "warehouseplace_id")
    private Long warehouseplaceid;

    @Column(name = "product_id")
    private Integer productid;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "unit")
    @Enumerated(EnumType.STRING)
    private Unit unit;

    protected WarehousePlaceProduct() {
    }

    public WarehousePlaceProduct(final Long warehouseplaceid, final Integer productid, final int quantity, final Unit unit) {
        this.warehouseplaceid = warehouseplaceid;
        this.productid = productid;
        this.quantity = quantity;
        this.unit = unit;
    }

    public Long getWarehouseplace() {
        return warehouseplaceid;
    }

    public void setWarehouseplaceid(Long warehouseplaceid) {
        this.warehouseplaceid = warehouseplaceid;
    }

    public Integer getProductid() {
        return productid;
    }

    public void setProductid(Integer productid) {
        this.productid = productid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public enum Unit {
        PIECE, BOX
    }
}
