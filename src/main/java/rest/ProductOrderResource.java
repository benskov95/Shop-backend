package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ProductOrderDTO;
import facades.ProductOrderFacade;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import errorhandling.InsufficientFunds;
import errorhandling.MissingInput;
import utils.EMF_Creator;

@Path("orders")
public class ProductOrderResource {
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final ProductOrderFacade ORDER_FACADE = ProductOrderFacade.getProductFacade(EMF);
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public String getAllOrders() {
        List<ProductOrderDTO> orderDTOs = ORDER_FACADE.getAllOrders();
        return GSON.toJson(orderDTOs);
    }
    
    @GET
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "admin"})
    public String getOrdersByUsername(@PathParam("username") String username) {
        List<ProductOrderDTO> orderDTOs = ORDER_FACADE.getOrdersByUsername(username);
        return GSON.toJson(orderDTOs);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    public String addOrder(String productOrderDTO) throws InsufficientFunds, MissingInput {
        ProductOrderDTO pDTO = GSON.fromJson(productOrderDTO, ProductOrderDTO.class);
        ProductOrderDTO addedDTO = ORDER_FACADE.addOrder(pDTO);
        return GSON.toJson(addedDTO);
    }
    
    @PUT
    @Path("{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    public String requestRefund(@PathParam("orderId") int orderId) {
        ORDER_FACADE.requestRefund(orderId);
        return "{\"refundMsg\":" + "\"Refund has been requested and will be approved by an admin at some point.\"}";
    }
    
    @DELETE
    @Path("{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
//    @RolesAllowed("admin")
    public String refundOrder(@PathParam("orderId") int orderId) throws MissingInput {
       double currentBalance = ORDER_FACADE.refundOrder(orderId);
       return "{\"refundMsg\":" + "\"Refund complete. Your balance is now: " + currentBalance + " DKK\"}";
    }
    
}
