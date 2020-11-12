package dto;

import java.util.List;

public class ProductOrderDTO {
    
    private String username;
    private List<ProductOrderlineDTO> orderlines;

    public ProductOrderDTO(String username, List<ProductOrderlineDTO> orderlines) {
        this.username = username;
        this.orderlines = orderlines;
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
