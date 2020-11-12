package facades;

import dto.ProductOrderDTO;
import dto.ProductOrderlineDTO;
import entities.Product;
import entities.ProductOrder;
import entities.ProductOrderline;
import entities.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import security.errorhandling.InsufficientFunds;

public class ProductOrderFacade {
    
    private static EntityManagerFactory emf;
    private static ProductOrderFacade instance;

    private ProductOrderFacade() {
    }

    public static ProductOrderFacade getProductFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new ProductOrderFacade();
        }
        return instance;
    }
    
    public List<ProductOrderDTO> getAllOrders() {
        EntityManager em = emf.createEntityManager();
        Query q = em.createQuery("SELECT p from ProductOrder p");
        List<ProductOrder> orders = q.getResultList();
        
        List<ProductOrderDTO> orderDTOs = new ArrayList<>();
        for (ProductOrder order : orders) {
            orderDTOs.add(new ProductOrderDTO(
                    order.getId(), 
                    order.getUser().getUsername(), 
                    order.getHasRequestedRefund()));
        }
        return orderDTOs;
    }
    
   public List<ProductOrderDTO> getOrdersByUsername(String username) {
        EntityManager em = emf.createEntityManager();
        Query q = em.createQuery("SELECT p from ProductOrder p WHERE p.user.username = :username");
        q.setParameter("username", username);
        List<ProductOrder> orders = q.getResultList();
        
        List<ProductOrderDTO> orderDTOs = new ArrayList<>();
        for (ProductOrder order : orders) {
            orderDTOs.add(new ProductOrderDTO(order));
        }
        return orderDTOs;
    }
    
    public List<ProductOrderlineDTO> getOrderlinesByOrderId(int orderId) {
        EntityManager em = emf.createEntityManager();
        ProductOrder p = em.find(ProductOrder.class, orderId);
        
        List<ProductOrderline> orderlines = p.getOrderlines();
        List<ProductOrderlineDTO> olDTOs = new ArrayList<>();
        for (ProductOrderline ol : orderlines) {
            olDTOs.add(new ProductOrderlineDTO(
                    ol.getId(), 
                    ol.getProduct().getId(),
                    ol.getProduct().getTitle(), 
                    ol.getProduct().getPrice(), 
                    ol.getProduct().getDescription(), 
                    ol.getProduct().getCategory(), 
                    ol.getProduct().getImage(),
                    ol.getQuantity()
            ));
        }
        
        return olDTOs;
    }
    
    
    public void requestRefund(int orderId) {
        EntityManager em = emf.createEntityManager();
        ProductOrder p = em.find(ProductOrder.class, orderId);
        
        try {
            em.getTransaction().begin();
            p.setHasRequestedRefund(true);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    } 
    
    public double refundOrder(int orderId) {
        EntityManager em = emf.createEntityManager();
        ProductOrder p = em.find(ProductOrder.class, orderId);
        User user = em.find(User.class, p.getUser().getUsername());
        
        try {
        em.getTransaction().begin();
        user.setBalance(user.getBalance() + p.getTotalPrice());
        em.remove(p);
        em.getTransaction().commit();
        return user.getBalance();
        } finally {
            em.close();
        }
    }
    
    public ProductOrderDTO addOrder(ProductOrderDTO orderDTO) throws InsufficientFunds {
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, orderDTO.getUsername());
        ProductOrder order = new ProductOrder(user);
        prepareOrder(order, orderDTO);
        finalizeOrder(order);
        try {
            em.getTransaction().begin();
            em.persist(order);
            em.getTransaction().commit();
            return orderDTO;
        } finally {
            em.close();
        }
    }
    
    public void prepareOrder(ProductOrder order, ProductOrderDTO orderDTO) {
        for (ProductOrderlineDTO orderlineDTO : orderDTO.getOrderlines()) {
            Product product = new Product(
                    orderlineDTO.getTitle(), 
                    orderlineDTO.getPrice(), 
                    orderlineDTO.getDescription(), 
                    orderlineDTO.getCategory(), 
                    orderlineDTO.getImage());
            order.addOrderline(new ProductOrderline(product, orderlineDTO.getQuantity()));
        }
    }
    
    public void finalizeOrder(ProductOrder order) throws InsufficientFunds {
        order.calcTotalPrice();
        if (order.getUser().getBalance() < order.getTotalPrice()) {
            throw new InsufficientFunds("You do not have enough money to make this purchase.");
        } else {
            order.getUser().setBalance(order.getUser().getBalance() - order.getTotalPrice());
        }
    }
    
}
