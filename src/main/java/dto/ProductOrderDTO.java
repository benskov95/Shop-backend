package dto;

import entities.ProductOrder;
import entities.ProductOrderline;
import java.util.ArrayList;
import java.util.List;

public class ProductOrderDTO {
    
    private int id;
    private String username;
    private boolean hasRequestedRefund;
    private List<ProductOrderlineDTO> orderlines;
    private double totalPrice;
    
    public ProductOrderDTO(ProductOrder order) {
        this.id = order.getId();
        this.username = order.getUser().getUsername();
        this.hasRequestedRefund = order.getHasRequestedRefund();
        this.orderlines = convertListToDTO(order.getOrderlines());
        this.totalPrice = order.getTotalPrice();
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
    
    public final List<ProductOrderlineDTO> convertListToDTO(List<ProductOrderline> orderlines) {
        List<ProductOrderlineDTO> olDTOs = new ArrayList<>();
        for (ProductOrderline ol : orderlines) {
            olDTOs.add(new ProductOrderlineDTO(ol));
        }
        return olDTOs;
    }

    public boolean getHasRequestedRefund() {
        return hasRequestedRefund;
    }

    public void setHasRequestedRefund(boolean hasRequestedRefund) {
        this.hasRequestedRefund = hasRequestedRefund;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
}
