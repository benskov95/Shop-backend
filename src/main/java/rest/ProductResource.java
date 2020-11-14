package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ProductDTO;
import errorhandling.AlreadyExists;
import errorhandling.MissingInput;
import facades.ProductFacade;
import fetchers.ProductFetcher;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
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
import utils.EMF_Creator;

@Path("products")
public class ProductResource {
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final ProductFacade PRODUCT_FACADE = ProductFacade.getProductFacade(EMF);
    private static ExecutorService es = Executors.newCachedThreadPool();
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "admin"})
    public String getAllProducts() {
        List<ProductDTO> productDTOs = PRODUCT_FACADE.getAllProducts();
        return GSON.toJson(productDTOs);
    }
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "admin"})
    public String getProductById(@PathParam("id") int id) {
        ProductDTO pDTO = PRODUCT_FACADE.getProductById(id);
        return GSON.toJson(pDTO);
    }
    
    @GET
    @Path("fetch-products")
    @Produces(MediaType.APPLICATION_JSON)
    public String fetchProductsExternally() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        String jsonProducts = ProductFetcher.fetchProducts(es, GSON);
        return jsonProducts;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public String addProduct(String product) throws AlreadyExists, MissingInput {
        ProductDTO pDTO = GSON.fromJson(product, ProductDTO.class);
        ProductDTO addedProduct = PRODUCT_FACADE.addProduct(pDTO);
        return GSON.toJson(addedProduct);
    }
    
    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public String deleteProduct(@PathParam("id") int id) throws AlreadyExists {
        ProductDTO deletedProduct = PRODUCT_FACADE.deleteProduct(id);
        return GSON.toJson(deletedProduct);
    }
    
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public String editProduct(@PathParam("id") int id, String product) throws AlreadyExists, MissingInput {
        ProductDTO pDTO = GSON.fromJson(product, ProductDTO.class);
        pDTO.setProductId(id);
        ProductDTO editedProduct = PRODUCT_FACADE.editProduct(pDTO);
        return GSON.toJson(editedProduct);
    }
    
    
}
