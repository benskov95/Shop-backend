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
    
    @Column (name = "refund_requested")
    private boolean hasRequestedRefund;
    
    @Column ( name = "total_price")
    private double totalPrice;
    
    @ManyToOne
    @JoinColumn (name = "user_name", referencedColumnName = "user_name")
    private User user;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<ProductOrderline> orderlines = new ArrayList<>();

    public ProductOrder(User user) {
        this.user = user;
        this.hasRequestedRefund = false;
    }

    public ProductOrder() {
    }
    
    public void addOrderline(ProductOrderline orderline) {
        orderline.setOrder(this);
        this.orderlines.add(orderline);
    }

    public int getId() {
        return id;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public void calcTotalPrice() {
        for (ProductOrderline ol : this.orderlines) {
            this.totalPrice += ol.getProduct().getPrice() * ol.getQuantity();
        }
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

    public boolean getHasRequestedRefund() {
        return hasRequestedRefund;
    }

    public void setHasRequestedRefund(boolean hasRequestedRefund) {
        this.hasRequestedRefund = hasRequestedRefund;
    }
    
}
