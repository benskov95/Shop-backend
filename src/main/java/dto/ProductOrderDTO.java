package dto;

import entities.ProductOrder;
import java.util.List;

public class ProductOrderDTO {
    
    private int id;
    private String username;
    private List<ProductOrderlineDTO> orderlines;

    public ProductOrderDTO(String username, List<ProductOrderlineDTO> orderlines) {
        this.username = username;
        this.orderlines = orderlines;
    }
    
    public ProductOrderDTO(ProductOrder order) {
        this.id = order.getId();
        this.username = order.getUser().getUsername();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<ProductOrderlineDTO> getOrderlines() {
        return orderlines;
    }

    public void setOrderlines(List<ProductOrderlineDTO> orderlines) {
        this.orderlines = orderlines;
    }
    
}
