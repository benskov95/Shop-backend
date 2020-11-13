package rest;

import dto.ProductOrderDTO;
import entities.Product;
import entities.ProductOrder;
import entities.ProductOrderline;
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

public class ProductOrderResourceTest {
    
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static User user, admin;
    private static Role r1, r2;
    private static Product p1, p2, p3;
    private static ProductOrder order1, order2;
    private static ProductOrderline ol1, ol2, ol3, ol4, ol5;
    private static final double ORDER2_TOTAL = 4384.5;


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
        setUpTestData(em);
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
        login("admin", "test123");
        given()
                .header("x-access-token", securityToken).when()
                .get("orders").then()
                .statusCode(200);
    }
    
    @Test
    public void testGetAllOrders() {
       List<ProductOrderDTO> orderList;

       login("admin", "test123");
       orderList = given()
                .header("x-access-token", securityToken)
                .contentType("application/json")
                .get("/orders")
                .then()
                .extract().body().jsonPath().getList("", ProductOrderDTO.class);


        assertThat(orderList.size(), equalTo(2));
    }
    
    @Test
    public void testGetOrderById() {
        login("user", "test123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .get("/orders/id/{id}", order2.getId())
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("username", equalTo("admin"));
    }
    
    @Test
    public void testGetOrdersByUsername() {
        List<ProductOrderDTO> orderList;
        
        login("user", "test123");
        orderList = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .get("/orders/{username}", user.getUsername())
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().body().jsonPath().getList("", ProductOrderDTO.class);
        
        assertThat(orderList.size(), equalTo(1));
    }
    
    @Test
    public void testRequestRefund() {
       login("user", "test123");
       given()
                .header("x-access-token", securityToken)
                .contentType("application/json")
                .put("/orders/request/{id}", order1.getId())
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("refundMsg", equalTo("Refund has been requested and will be approved by an admin at some point."));

    }
    
    @Test
    public void testDenyRefund() {
       login("user", "test123");
       given()
                .header("x-access-token", securityToken)
                .contentType("application/json")
                .put("/orders/request/{id}", order1.getId())
                .then()
                .assertThat()
                .statusCode(200);
       
        login("admin", "test123");
        given()
                .header("x-access-token", securityToken)
                .contentType("application/json")
                .put("/orders/deny/{id}", order1.getId())
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("refundMsg", equalTo("Refund has been denied."));
    }
    
    @Test
    public void testRefundOrder() {
       // Refund request needs to be made before refund
       login("user", "test123");
       given()
                .header("x-access-token", securityToken)
                .contentType("application/json")
                .put("/orders/request/{id}", order2.getId())
                .then()
                .assertThat()
                .statusCode(200);
       
       login("admin", "test123");
       given()
                .header("x-access-token", securityToken)
                .contentType("application/json")
                .delete("/orders/{id}", order2.getId())
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("refundMsg", equalTo("Refund complete. Your balance is now: " + ORDER2_TOTAL + " DKK"));
    }
    
    @Test
    public void testAddOrder() {
        ProductOrderDTO pDTO = setUpTestOrder(user);
        
         login("user", "test123");
         given()
                .header("x-access-token", securityToken)
                .contentType("application/json")
                .body(pDTO)
                .when()
                .post("/orders")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("username", equalTo(user.getUsername()));
    }
    
    @Test
    public void testInsufficientFundsWhenOrdering() {
        changeUserBalance(0);
        ProductOrderDTO pDTO = setUpTestOrder(user);
        
        login("user", "test123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(pDTO)
                .post("/orders")
                .then()
                .assertThat()
                .statusCode(403)
                .and()
                .assertThat()
                .body("message", equalTo("You do not have enough money to make this purchase."));
                
    }
    
    private void setUpTestData(EntityManager em) {
    setValues();
    try {
        user.addRole(r1);
        admin.addRole(r2);
        user.setBalance(400);
        admin.setBalance(0);
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
    
    private ProductOrderDTO setUpTestOrder(User testUser) {
        ProductOrder order = new ProductOrder(testUser);
        order.addOrderline(new ProductOrderline(p1, 5));
        order.addOrderline(new ProductOrderline(p2, 1));
        testUser.getOrders().add(order);
        return new ProductOrderDTO(order);
    }
    
    private void changeUserBalance(double newBalance) {
        EntityManager em = emf.createEntityManager();
        try {
        User testUser = em.find(User.class, user.getUsername());
        em.getTransaction().begin();
        testUser.setBalance(newBalance);
        em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
}
