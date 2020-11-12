package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ProductOrderDTO;
import dto.ProductOrderlineDTO;
import facades.ProductOrderFacade;
import facades.UserFacade;
import fetchers.ExampleFetcher;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import security.errorhandling.InsufficientFunds;
import utils.EMF_Creator;

@Path("order")
public class ProductOrderResource {
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final ProductOrderFacade PRODUCT_FACADE = ProductOrderFacade.getProductFacade(EMF);
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllOrders() {
        List<ProductOrderDTO> orderDTOs = PRODUCT_FACADE.getAllOrders();
        return GSON.toJson(orderDTOs);
    }
    
    @GET
    @Path("user/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getOrdersByUsername(@PathParam("username") String username) {
        List<ProductOrderDTO> orderDTOs = PRODUCT_FACADE.getOrdersByUsername(username);
        return GSON.toJson(orderDTOs);
    }
    
    @GET
    @Path("{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getOrderlinesByOrderId(@PathParam("orderId") int id) {
        List<ProductOrderlineDTO> olDTOs = PRODUCT_FACADE.getOrderlinesByOrderId(id);
        return GSON.toJson(olDTOs);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
//    @RolesAllowed({"user", "admin"})
    public String addOrder(String productOrderDTO) throws InsufficientFunds {
        ProductOrderDTO pDTO = GSON.fromJson(productOrderDTO, ProductOrderDTO.class);
        ProductOrderDTO addedDTO = PRODUCT_FACADE.addOrder(pDTO);
        return GSON.toJson(addedDTO);
    }
    
    @PUT
    @Path("{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String refundOrder(@PathParam("orderId") int orderId) {
       double currentBalance = PRODUCT_FACADE.refundOrder(orderId);
       return "{\"refundMsg\":" + "Refund complete. Your balance is now: " + currentBalance + " DKK\"}";
    }
    
}
