package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "productorder")
public class ProductOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "order_id")
    private int id;
    
    @ManyToOne
    @JoinColumn (name = "user_name", referencedColumnName = "user_name")
    private User user;
    
    @ManyToMany (mappedBy = "orders", cascade = CascadeType.PERSIST)
    private List<Product> products = new ArrayList<>();

    public ProductOrder(User user) {
        this.user = user;
    }

    public ProductOrder() {
    }
    
    public void addProduct(Product product) {
        product.getOrders().add(this);
        this.products.add(product);
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
    
}
