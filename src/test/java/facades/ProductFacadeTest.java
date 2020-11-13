package facades;

import dto.ProductDTO;
import entities.Product;
import errorhandling.AlreadyExists;
import errorhandling.MissingInput;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

public class ProductFacadeTest {
    
    private static EntityManagerFactory emf;
    private static ProductFacade facade;
    private static Product p1, p2, p3;
        
    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = ProductFacade.getProductFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        p1 = new Product("testproduct 1", 50.5, "this is test product 1", "testing", "random1.url");
        p2 = new Product("testproduct 2", 20, "this is test product 2", "testing", "random2.url");
        p3 = new Product("testproduct 3", 700.75, "this is test product 3", "testing", "random3.url");
        
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Product.deleteAllRows").executeUpdate();
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
    }
    
    @Test
    public void testGetAllProducts() {
        List<ProductDTO> productDTOs = facade.getAllProducts();
        int baseNumberOfProducts = 3;
        assertEquals(baseNumberOfProducts, productDTOs.size());
    }
    
    @Test
    public void testGetProductById() {
        ProductDTO pDTO = facade.getProductById(p3.getId());
        assertEquals(pDTO.getImage(), p3.getImage());
    }
    
    @Test
    public void testAddProduct() throws AlreadyExists, MissingInput {
        p1.setTitle("This is a new product");
        ProductDTO pDTO = new ProductDTO(p1);
        ProductDTO addedDTO = facade.addProduct(pDTO);
        assertTrue(p1.getId() != addedDTO.getProductId());
        
    }
    
    @Test
    public void testDeleteProduct() throws AlreadyExists {
        ProductDTO pDTO = facade.deleteProduct(p2.getId());
        assertThrows(NullPointerException.class, () ->  {
             facade.getProductById(pDTO.getProductId());
        });
    }
    
    @Test
    public void testEditProduct() throws AlreadyExists, MissingInput {
        ProductDTO pDTO = new ProductDTO(p1);
        pDTO.setTitle("Editing product");
        pDTO.setCategory("testing");
        ProductDTO editedDTO = facade.editProduct(pDTO);
        ProductDTO confirmDTO = facade.getProductById(editedDTO.getProductId());
        assertTrue(confirmDTO.getTitle().equals("Editing product"));
        assertTrue(confirmDTO.getProductId() == p1.getId());
    }
    
    @Test
    public void testAlreadyExists() {
        AlreadyExists thrown =
                assertThrows(AlreadyExists.class, () ->  {
                    facade.addProduct(new ProductDTO(p1));
                });
        assertTrue(thrown.getMessage().equals("This product already exists in the database."));
    }
    
    @Test
    public void testMissingInput() {
        p2.setPrice(0);
        p2.setCategory("");
        MissingInput thrown =
                assertThrows(MissingInput.class, () ->  {
                    facade.editProduct(new ProductDTO(p2));
                });
        assertTrue(thrown.getMessage().equals("All fields must be filled out."));
    }
    
}
