package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ProductOrderDTO;
import facades.ProductOrderFacade;
import facades.UserFacade;
import fetchers.ExampleFetcher;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import security.errorhandling.InsufficientFunds;
import utils.EMF_Creator;

@Path("order")
public class ProductOrderResource {
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final ProductOrderFacade PRODUCT_FACADE = ProductOrderFacade.getProductFacade(EMF);
    private static ExecutorService es = Executors.newCachedThreadPool();
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
//    @RolesAllowed({"user", "admin"})
    public String addOrder(String productOrderDTO) throws InsufficientFunds {
        ProductOrderDTO pDTO = GSON.fromJson(productOrderDTO, ProductOrderDTO.class);
        ProductOrderDTO addedDTO = PRODUCT_FACADE.addOrder(pDTO);
        return GSON.toJson(addedDTO);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String test() {
        return GSON.toJson("HALLO");
    }
    
}
