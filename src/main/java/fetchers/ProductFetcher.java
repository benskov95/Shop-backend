package fetchers;

import com.google.gson.Gson;
import dto.ProductDTO;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import utils.HttpUtils;

public class ProductFetcher {
    
    private static String productsUrl = "https://fakestoreapi.com/products";

    public static String fetchProducts(ExecutorService threadPool, Gson gson) throws InterruptedException, ExecutionException, TimeoutException, IOException {

        Callable <List<ProductDTO>> productsTask = new Callable<List<ProductDTO>>() {
            @Override
            public List<ProductDTO> call() throws Exception {
                String products = HttpUtils.fetchData(productsUrl);
                List<ProductDTO> productsDTO = gson.fromJson(products, List.class);
                return productsDTO;
            }
        };

        Future<List<ProductDTO>> futureProducts = threadPool.submit( productsTask);
        List<ProductDTO> productsDTO = futureProducts.get(5, TimeUnit.SECONDS);
        String productsJson = gson.toJson(productsDTO);

        return productsJson;
    }
    
}
