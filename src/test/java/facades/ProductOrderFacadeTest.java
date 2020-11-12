package facades;

import dto.ProductOrderDTO;
import entities.Product;
import entities.ProductOrder;
import entities.ProductOrderline;
import entities.Role;
import entities.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    
    private void setUpTestData(EntityManager em) {
        setValues();
        try {
            user.addRole(r1);
            admin.addRole(r2);
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
            em.createNamedQuery("ProductOrder.deleteAllRows").executeUpdate();
            em.createNamedQuery("Product.deleteAllRows").executeUpdate();
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
