package entities;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQuery (name = "ProductOrderline.deleteAllRows", query = "DELETE FROM ProductOrderline")
@Table(name = "productorderline")
public class ProductOrderline implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "orderline_id")
    private int id;
    
    @Column (name = "quantity")
    private int quantity;
    
    @ManyToOne (cascade = CascadeType.PERSIST)
    @JoinColumn (name = "product_id", referencedColumnName = "product_id")
    private Product product;
    
    @ManyToOne
    @JoinColumn (name = "order_id", referencedColumnName = "order_id")
    private ProductOrder order;

    public ProductOrderline(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public ProductOrderline() {
    }    
    
    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductOrder getOrder() {
        return order;
    }

    public void setOrder(ProductOrder order) {
        this.order = order;
    }
    
}
