package rest;

import dto.ProductDTO;
import entities.Product;
import entities.Role;
import entities.User;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

public class ProductResourceTest {
    
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static User user, admin;
    private static Role r1, r2;
    private static Product p1, p2, p3;


    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        user = new User("user", "test123");
        admin = new User("admin", "test123");
        r1 = new Role("user");
        r2 = new Role("admin");
        p1 = new Product("testproduct 1", 50.5, "this is test product 1", "testing", "random1.url");
        p2 = new Product("testproduct 2", 20, "this is test product 2", "testing", "random2.url");
        p3 = new Product("testproduct 3", 700.75, "this is test product 3", "testing", "random3.url");
        
        try {
            user.addRole(r1);
            admin.addRole(r2);
            em.getTransaction().begin();
            em.createNamedQuery("ProductOrderline.deleteAllRows").executeUpdate();
            em.createNamedQuery("Product.deleteAllRows").executeUpdate();
            em.createNamedQuery("ProductOrder.deleteAllRows").executeUpdate();
            em.createNamedQuery("Roles.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.persist(user);
            em.persist(admin);
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    private static String securityToken;

    private static void login(String role, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", role, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                .when().post("/login")
                .then()
                .extract().path("token");
    }

    @Test
    public void testServerIsUp() {
        login("user", "test123");
        given()
                .header("x-access-token", securityToken).when()
                .get("products").then()
                .statusCode(200);
    }
    
    @Test
    public void testGetAllProducts() {
       List<ProductDTO> productList;

       login("user", "test123");
       productList = given()
                .header("x-access-token", securityToken)
                .contentType("application/json")
                .get("/products")
                .then()
                .extract().body().jsonPath().getList("", ProductDTO.class);


        assertThat(productList.size(), equalTo(3));
    }
    
    @Test
    public void testGetProductById() {
        login("admin", "test123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .get("/products/{id}", p3.getId())
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("title", equalTo(p3.getTitle()));
    }
    
    @Test
    public void testAddProduct() {
        p1.setTitle("added product");
        ProductDTO pDTO = new ProductDTO(p1);
        
         login("admin", "test123");
         given()
                .header("x-access-token", securityToken)
                .contentType("application/json")
                .body(pDTO)
                .when()
                .post("/products")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("title", equalTo("added product"));
    }
    
    @Test
    public void testDeleteProduct() {
        login("admin", "test123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .delete("/products/{id}", p2.getId())
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("image", equalTo(p2.getImage()));
    }
    
    @Test
    public void testEditProduct() {
        p1.setDescription("this product has been edited");
        ProductDTO editDTO = new ProductDTO(p1);
        
        login("admin", "test123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(editDTO)
                .put("/products/{id}", editDTO.getProductId())
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("description", equalTo("this product has been edited"));
        
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .get("/products/{id}", p1.getId())
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("description", equalTo(editDTO.getDescription()));
                
    }
    
    @Test
    public void testMissingInputWhenEditing() {
        p1.setTitle("new product");
        p1.setDescription("");
        ProductDTO pDTO = new ProductDTO(p1);
        
        login("admin", "test123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(pDTO)
                .put("/products/{id}", pDTO.getProductId())
                .then()
                .assertThat()
                .statusCode(403)
                .and()
                .assertThat()
                .body("message", equalTo("All fields must be filled out."));
                
    }
    
    @Test
    public void testAlreadyExistsWhenAdding() {
        ProductDTO pDTO = new ProductDTO(p1);
        
        login("admin", "test123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(pDTO)
                .post("/products")
                .then()
                .assertThat()
                .statusCode(403)
                .and()
                .assertThat()
                .body("message", equalTo("This product already exists in the database."));
                
    }
    
}
