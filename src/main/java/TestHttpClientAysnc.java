import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TestHttpClientAysnc {
    public static void main(String[] args) throws IOException, URISyntaxException, ExecutionException, InterruptedException {

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(50000)
                .setSocketTimeout(50000)
                .setConnectionRequestTimeout(0)
                .build();

        //配置io线程
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().
                setIoThreadCount(Runtime.getRuntime().availableProcessors())
                .setSoKeepAlive(true)
                .build();
        //设置连接池大小
        ConnectingIOReactor ioReactor=null;
        try {
            ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
        } catch (IOReactorException e) {
            e.printStackTrace();
        }
        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor);
        connManager.setMaxTotal(1000);
        connManager.setDefaultMaxPerRoute(200);


        final CloseableHttpAsyncClient client = HttpAsyncClients.custom().
                setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .build();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        for (int i = 0; i < 1000; i++) {
            List<NameValuePair> nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("corpid", "wwd6da61649bd66fea"));
            nameValuePairs.add(new BasicNameValuePair("corpsecret", "1JPyY9GvPLZfpvxEDjok-Xt_9v7HIBYJhZUoO6EgNGY"));
            URI uri = new URIBuilder("https://qyapi.weixin.qq.com/cgi-bin/gettoken").setParameters(nameValuePairs).build();
            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
            //同步请求,获取access-token
            response = httpclient.execute(httpGet);
            String jsonObject = EntityUtils.toString(response.getEntity());
            //Deserialization json
            ObjectMapper objectMapper = new ObjectMapper();
            //json to map
            Map<String,String> result = objectMapper.readValue(jsonObject, new TypeReference<Map<String,String>>() { });

            //start 异步请求,用之前api的access-token
            client.start();

            URI uri2 = new URIBuilder("https://qyapi.weixin.qq.com/cgi-bin/getcallbackip").setParameter("access_token",result.get("access_token")).build();
            // 创建最终 http GET请求
            HttpGet httpGet2 = new HttpGet(uri2);
            client.execute(httpGet2, new Back());
        }


    }

    static class Back implements FutureCallback<HttpResponse> {

        private long start = System.currentTimeMillis();

        Back() {
        }

        public void completed(HttpResponse httpResponse) {
            try {
                System.out.println("cost completed is:" + (System.currentTimeMillis() - start) + ":" + EntityUtils.toString(httpResponse.getEntity()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void failed(Exception e) {
            System.err.println(" cost is:" + (System.currentTimeMillis() - start) + ":" + e);
        }

        public void cancelled() {
        }
    }
}