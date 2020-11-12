package dto;

import entities.ProductOrder;
import java.util.List;

public class ProductOrderDTO {
    
    private int id;
    private String username;
    private boolean hasRequestedRefund;
    private List<ProductOrderlineDTO> orderlines;

    public ProductOrderDTO(int id, String username, boolean hasRequestedRefund) {
        this.id = id;
        this.username = username;
        this.hasRequestedRefund = hasRequestedRefund;
    }

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

    public boolean getHasRequestedRefund() {
        return hasRequestedRefund;
    }

    public void setHasRequestedRefund(boolean hasRequestedRefund) {
        this.hasRequestedRefund = hasRequestedRefund;
    }
    
}
