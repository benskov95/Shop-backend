package facades;

import dto.ProductOrderDTO;
import dto.ProductOrderlineDTO;
import entities.Product;
import entities.ProductOrder;
import entities.ProductOrderline;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
        } finally {
            em.close();
        }
        return orderDTO;
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
