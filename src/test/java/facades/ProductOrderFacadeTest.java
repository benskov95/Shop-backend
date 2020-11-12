package facades;

import dto.ProductOrderDTO;
import entities.Product;
import entities.ProductOrder;
import entities.ProductOrderline;
import entities.Role;
import entities.User;
import errorhandling.InsufficientFunds;
import errorhandling.MissingInput;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

public class ProductOrderFacadeTest {
    
    private static EntityManagerFactory emf;
    private static ProductOrderFacade facade;
    private static User user, admin;
    private static Role r1, r2;
    private static Product p1, p2, p3;
    private static ProductOrder order1, order2;
    private static ProductOrderline ol1, ol2, ol3, ol4, ol5;
    
    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = ProductOrderFacade.getProductFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        setUpTestData(em);
    }

    @AfterEach
    public void tearDown() {
    }
    
    @Test
    public void testGetAllOrders() {
        List<ProductOrderDTO> orderDTOs = facade.getAllOrders();
        int baseNumberOfOrders = 2;
        assertEquals(baseNumberOfOrders, orderDTOs.size());
    }
    
    @Test
    public void testGetOrderById() {
        ProductOrderDTO pDTO = facade.getOrderById(order2.getId());
        assertEquals(pDTO.getUsername(), order2.getUser().getUsername());
    }
    
    @Test
    public void testGetOrdersByUsername() {
        List<ProductOrderDTO> orderDTOs = facade.getOrdersByUsername(user.getUsername());
        for (ProductOrderDTO pDTO : orderDTOs) {
            assertEquals(user.getUsername(), pDTO.getUsername());
        }
    }
    
    @Test
    public void testRequestRefund() {
        facade.requestRefund(order1.getId());
        List<ProductOrderDTO> orderDTOs = facade.getOrdersByUsername(order1.getUser().getUsername());
        assertTrue(orderDTOs.get(0).getHasRequestedRefund());
    }
    
    @Test
    public void testRefundOrder() throws MissingInput {
        int id = admin.getOrders().get(0).getId();
        double currentBalance = admin.getBalance();
        facade.requestRefund(id);
        double newBalance = facade.refundOrder(id);
        assertTrue(newBalance > currentBalance);
    }
    
    @Test
    public void testAddOrder() throws InsufficientFunds, MissingInput {
        ProductOrder newOrder = new ProductOrder(user);
        ProductOrderline ol6 = new ProductOrderline(p2, 7);
        newOrder.addOrderline(ol6);
        ProductOrderDTO newOrderDTO = new ProductOrderDTO(newOrder);
        ProductOrderDTO addedDTO = facade.addOrder(newOrderDTO);
        ProductOrderDTO compareDTO = facade.getOrderById(addedDTO.getId());
        assertTrue(compareDTO.getUsername().equals(addedDTO.getUsername()));
    }
    
    private void setUpTestData(EntityManager em) {
        setValues();
        try {
            user.addRole(r1);
            admin.addRole(r2);
            user.setBalance(500);
            admin.setBalance(500);
            order1.addOrderline(ol1);
            order1.addOrderline(ol2);
            order2.addOrderline(ol3);
            order2.addOrderline(ol4);
            order2.addOrderline(ol5);
            order1.calcTotalPrice();
            order2.calcTotalPrice();
            user.getOrders().add(order1);
            admin.getOrders().add(order2);
            
            em.getTransaction().begin();
            em.createNamedQuery("ProductOrderline.deleteAllRows").executeUpdate();
            em.createNamedQuery("Product.deleteAllRows").executeUpdate();
            em.createNamedQuery("ProductOrder.deleteAllRows").executeUpdate();
            em.createNamedQuery("Roles.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.persist(user);
            em.persist(admin);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    private void setValues() {
        user = new User("user", "test123");
        admin = new User("admin", "test123");
        r1 = new Role("user");
        r2 = new Role("admin");
        order1 = new ProductOrder(user);
        order2 = new ProductOrder(admin);
        p1 = new Product("testproduct 1", 50.5, "this is test product 1", "testing", "random1.url");
        p2 = new Product("testproduct 2", 20, "this is test product 2", "testing", "random2.url");
        p3 = new Product("testproduct 3", 700.75, "this is test product 3", "testing", "random3.url");
        ol1 = new ProductOrderline(p1, 3);
        ol2 = new ProductOrderline(p2, 27);
        ol3 = new ProductOrderline(p3, 5);
        ol4 = new ProductOrderline(p2, 9);
        ol5 = new ProductOrderline(p3, 1);
    }

    
}
