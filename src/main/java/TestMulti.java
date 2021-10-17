import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


/**
 * 没效果失败了，等待研究
 */
public class TestMulti {

    public void test2(List<NameValuePair> nameValuePairs) throws IOException, URISyntaxException, InterruptedException {
        PoolingHttpClientConnectionManager  poolingConnManager
                = new PoolingHttpClientConnectionManager();
        poolingConnManager.setMaxTotal(10);
        poolingConnManager.setDefaultMaxPerRoute(5);
        HttpHost host = new HttpHost("qyapi.weixin.qq.com", 80);
        poolingConnManager.setMaxPerRoute(new HttpRoute(host), 5);

        // 创建Httpclient对象 with poolingConnManager
        CloseableHttpClient client = HttpClients.custom().
                setConnectionManager(poolingConnManager).build();
        // 创建Httpclient对象
//        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建http GET请求
        URI uri = new URIBuilder("https://qyapi.weixin.qq.com/cgi-bin/gettoken").setParameters(nameValuePairs).build();

        HttpGet get = new HttpGet(uri);
        MultiHttpClientConnThread thread1
                = new MultiHttpClientConnThread(client, get);
        MultiHttpClientConnThread thread2
                = new MultiHttpClientConnThread(client, get);
        MultiHttpClientConnThread thread3
                = new MultiHttpClientConnThread(client, get);
        thread1.start();
        thread2.start();
        thread3.start();
        thread1.join();
        thread2.join();
        thread3.join();
//        client.execute(new HttpGet(uri));
//        assertTrue(poolingConnManager.getTotalStats().getLeased() == 1);
    }

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {

        long startTime =  System.currentTimeMillis();
        List<NameValuePair> nameValuePairs = new ArrayList();
        nameValuePairs.add(new BasicNameValuePair("corpid", "wwd6da61649bd66fea"));
        nameValuePairs.add(new BasicNameValuePair("corpsecret", "1JPyY9GvPLZfpvxEDjok-Xt_9v7HIBYJhZUoO6EgNGY"));
        TestMulti a = new TestMulti();
//        for (int i = 0; i < 100; i++) {
//            a.test2(nameValuePairs);
//            System.out.println("loop count"+String.valueOf(i));
//        }
        a.test2(nameValuePairs);
        long endTime =  System.currentTimeMillis();
        long usedTime = (endTime-startTime)/1000;
        System.out.println("cost time is "+String.valueOf(usedTime));
    }
}
