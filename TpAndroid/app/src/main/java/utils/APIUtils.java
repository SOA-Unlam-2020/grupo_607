package utils;

import interfaces.APIService;
import retrofitClient.RetrofitClient;

/**
 * Uso esta clase para obtener el servicio instanciando Retrofit y creando la firma de las operacion
 * del web services REST expuesto por la catedra
 */
public class APIUtils {

    public APIUtils() {
    }

    //URI
    public static final String BASE_URL = "http://so-unlam.net.ar/api/api/";

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

}
