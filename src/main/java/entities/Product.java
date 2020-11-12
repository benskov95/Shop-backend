package entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@NamedQuery (name = "Product.deleteAllRows", query = "DELETE FROM Product")
@Table(name = "product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "product_id")
    private int id;
    @Column (name = "title")
    private String title;
    @Column (name = "price")
    private double price;
    @Column (name = "description", length = 300)
    private String description;
    @Column (name = "category")
    private String category;
    @Column (name = "image")
    private String image;
    
    @OneToMany (mappedBy = "product")
    private List<ProductOrderline> orderlines;
    
//    @ManyToMany
//    @JoinColumn(name = "productorder_id", referencedColumnName = "order_id")
//    private List<ProductOrder> orders = new ArrayList<>();

    public Product(String title, double price, String description, String category, String image) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.category = category;
        this.image = image;
    }

    public Product() {
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

//    public List<ProductOrder> getOrders() {
//        return orders;
//    }
//
//    public void setOrders(List<ProductOrder> orders) {
//        this.orders = orders;
//    }

    public List<ProductOrderline> getOrderlines() {
        return orderlines;
    }

    public void setOrderlines(List<ProductOrderline> orderlines) {
        this.orderlines = orderlines;
    }
    
}
