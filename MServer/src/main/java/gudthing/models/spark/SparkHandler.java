package gudthing.models.spark;

import gudthing.models.InstructionModels.Health;
import gudthing.models.InstructionType;
import gudthing.models.URLService;
import gudthing.properties.SessionExceptionOperation;
import gudthing.properties.SessionFailureOperation;
import gudthing.properties.SessionSuccessOperations;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Ben on 12/05/2016.
 */
public class SparkHandler {
    private static RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());

    private static ClientHttpRequestFactory getClientHttpRequestFactory() {
        int timeout = 3000;
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeout);
        return clientHttpRequestFactory;
    }

    //WORDCOUNT, WORDSEARCH
    public static void handleRequest(ClientWithSparkInstruction clientWithSparkInstruction){

        String url = URLService.urlEncoder(clientWithSparkInstruction);
        String health = url + "/health";
        url += "/spark";
        //url+= SparkType.WORDCOUNT.mapping();

        try {

            if(isConnected(clientWithSparkInstruction)){
                ClientWithSparkInstruction response = restTemplate.postForObject(url, clientWithSparkInstruction, ClientWithSparkInstruction.class);
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                clientWithSparkInstruction.setResponse(response.getResponse());
                checkForException(response.getResponse());
                //return response.getResponse();
                SessionSuccessOperations.incrementCounter();
                return;
            }else{
                SessionExceptionOperation.incrementCounter();
                clientWithSparkInstruction.setResponse("Could Not connect to the client. Please check the Health of client via Query Wizard.");
                return;
            }

        }catch (Exception e){
            System.out.println("EXCEPTION");
            SessionFailureOperation.incrementCounter();
            clientWithSparkInstruction.setResponse("EXCEPTION occured when trying to communicate with MCLient");
            SessionFailureOperation.incrementCounter();
            return;
        }
    }

    public static boolean isConnected(ClientWithSparkInstruction clientWithInstruction) {
        String url = URLService.urlEncoder(clientWithInstruction);
        url+= InstructionType.HEALTH.mapping();


        boolean connected = URLService.testResponse(url);

        if (connected) {
            Health health = restTemplate.getForObject(url, Health.class);
            if (health != null && health.getStatus().equals("UP")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static void checkForException(String response) {
        if(response.toLowerCase().contains("exception") || response.toLowerCase().contains("error")){
            SessionExceptionOperation.incrementCounter();
        }
    }

};
