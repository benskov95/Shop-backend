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
import errorhandling.InsufficientFunds;
import errorhandling.MissingInput;

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
            orderDTOs.add(new ProductOrderDTO(order));
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
    
    public double refundOrder(int orderId) throws MissingInput {
        EntityManager em = emf.createEntityManager();
        ProductOrder p = em.find(ProductOrder.class, orderId);
        User user = em.find(User.class, p.getUser().getUsername());
        
        if (!p.getHasRequestedRefund()) {
            throw new MissingInput("This order has not had a refund requested.");
        }
        
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
    
    public ProductOrderDTO addOrder(ProductOrderDTO orderDTO) throws InsufficientFunds, MissingInput {
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, orderDTO.getUsername());
        ProductOrder order = new ProductOrder(user);
        prepareOrder(order, orderDTO);
        checkIfProductExists(order, em);
        finalizeOrder(order);
        try {
            em.getTransaction().begin();
            em.persist(order);
            em.getTransaction().commit();
            return new ProductOrderDTO(order);
        } finally {
            em.close();
        }
    }
    
    private void prepareOrder(ProductOrder order, ProductOrderDTO orderDTO) throws MissingInput {
        for (ProductOrderlineDTO orderlineDTO : orderDTO.getOrderlines()) {
            if (orderlineDTO.getTitle().length() < 3 ||
            orderlineDTO.getPrice() < 0.1 ||
            orderlineDTO.getDescription().length() < 5 ||
            orderlineDTO.getCategory().length() < 3 ||
            orderlineDTO.getImage().length() < 5) 
        {
            throw new MissingInput("All fields must be filled out.");
        } else {
            Product product = new Product(
                    orderlineDTO.getTitle(), 
                    orderlineDTO.getPrice(), 
                    orderlineDTO.getDescription(), 
                    orderlineDTO.getCategory(), 
                    orderlineDTO.getImage());
            order.addOrderline(new ProductOrderline(product, orderlineDTO.getQuantity()));
            }
        }
    }
    
    private void finalizeOrder(ProductOrder order) throws InsufficientFunds {
        order.calcTotalPrice();
        if (order.getUser().getBalance() < order.getTotalPrice()) {
            throw new InsufficientFunds("You do not have enough money to make this purchase.");
        } else {
            order.getUser().setBalance(order.getUser().getBalance() - order.getTotalPrice());
        }
    }
    
    private void checkIfProductExists(ProductOrder p, EntityManager em) {
        for (ProductOrderline ol : p.getOrderlines()) {    
            Query q = em.createQuery("SELECT p FROM Product p WHERE p.title = :title");
            q.setParameter("title", ol.getProduct().getTitle());
            if (q.getResultList().size() > 0) {
                 ol.setProduct((Product) q.getResultList().get(0));
            }
        }
    }
    
}
