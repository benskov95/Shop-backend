/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fetchers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.PriceConversionDTO;
import dto.ProductDTO;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import utils.HttpUtils;


public class PriceConversionFetcher {
    
    public static String fetchPriceConversion(ExecutorService threadPool, Gson gson) throws InterruptedException, ExecutionException, TimeoutException, IOException {
        
        String conversionUrl = "https://api.frankfurter.app/latest?to=USD,DKK";

        Callable <PriceConversionDTO> productsTask = new Callable<PriceConversionDTO>() {
            @Override
            public PriceConversionDTO call() throws Exception {
                String convertedPrice = HttpUtils.fetchData(conversionUrl);
                PriceConversionDTO priceDTO = gson.fromJson(convertedPrice, PriceConversionDTO.class);
                return priceDTO;
            }
        };

        Future<PriceConversionDTO> futureConvertedPrice = threadPool.submit( productsTask);
        PriceConversionDTO convertedPrice = futureConvertedPrice.get(5, TimeUnit.SECONDS);
        String convertedPriceJson = gson.toJson(convertedPrice);

        return convertedPriceJson;
    }
   
}
