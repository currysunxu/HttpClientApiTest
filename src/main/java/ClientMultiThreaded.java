import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

public class ClientMultiThreaded extends Thread {
    CloseableHttpClient httpClient;
    HttpGet httpget;
    int id;

    public ClientMultiThreaded(CloseableHttpClient httpClient, HttpGet httpget,
                               int id) {
        this.httpClient = httpClient;
        this.httpget = httpget;
        this.id = id;
    }
    @Override
    public void run() {
        try{
            //Executing the request
            CloseableHttpResponse httpresponse = httpClient.execute(httpget);

            //Displaying the status of the request.
            System.out.println("status of thread "+id+":"+httpresponse.getStatusLine());

            //Retrieving the HttpEntity and displaying the no.of bytes read
            HttpEntity entity = httpresponse.getEntity();
            if (entity != null) {
                System.out.println("Bytes read by thread thread "+id+": "+EntityUtils.toByteArray(entity).length);
            }
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {

        //Creating the Client Connection Pool Manager by instantiating the PoolingHttpClientConnectionManager class.
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

        //Set the maximum number of connections in the pool
        connManager.setMaxTotal(100);

        //Create a ClientBuilder Object by setting the connection manager
        HttpClientBuilder clientbuilder = HttpClients.custom().setConnectionManager(connManager);

        //Build the CloseableHttpClient object using the build() method.
        CloseableHttpClient httpclient = clientbuilder.build();

        //Creating the HttpGet requests
        HttpGet httpget1 = new HttpGet("http://www.tutorialspoint.com/");
        HttpGet httpget2 = new HttpGet("http://www.google.com/");
        HttpGet httpget3 = new HttpGet("https://www.qries.com/");
        HttpGet httpget4 = new HttpGet("https://in.yahoo.com/");

        //Creating the Thread objects
        ClientMultiThreaded thread1 = new ClientMultiThreaded(httpclient,httpget1, 1);
        ClientMultiThreaded thread2 = new ClientMultiThreaded(httpclient,httpget2, 2);
        ClientMultiThreaded thread3 = new ClientMultiThreaded(httpclient,httpget3, 3);
        ClientMultiThreaded thread4 = new ClientMultiThreaded(httpclient,httpget4, 4);

        //Starting all the threads
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        //Joining all the threads
        thread1.join();
        thread2.join();
        thread3.join();
        thread4.join();
    }
}