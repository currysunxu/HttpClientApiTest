import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class MultiHttpClientConnThread extends Thread {
    private CloseableHttpClient client;
    private HttpGet get;

    public MultiHttpClientConnThread(CloseableHttpClient client, HttpGet get) {
        this.client = client;
        this.get = get;
    }

    // standard constructors
    public void run(){
        try {
            HttpResponse response = client.execute(get);
            EntityUtils.consume(response.getEntity());
        } catch (ClientProtocolException ex) {
        } catch (IOException ex) {
        }
    }
}