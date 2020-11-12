package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class ProductOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    @ManyToOne
    private User user;
    
    @OneToMany(mappedBy = "productOrder")
    private List<ProductOrderline> orderlines = new ArrayList<>();

    public ProductOrder(User user, List<ProductOrderline> orderlines) {
        this.user = user;
        this.orderlines = orderlines;
    }

    public ProductOrder() {
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

    public List<ProductOrderline> getOrderlines() {
        return orderlines;
    }

    public void setOrderlines(List<ProductOrderline> orderlines) {
        this.orderlines = orderlines;
    }
    
    
    
}
