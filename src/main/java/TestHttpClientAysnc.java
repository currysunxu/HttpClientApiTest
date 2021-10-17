import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
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
import java.util.concurrent.TimeUnit;

public class TestHttpClientAysnc {
    public static void main(String[] args) throws IOException, URISyntaxException {

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
        connManager.setDefaultMaxPerRoute(10);


        final CloseableHttpAsyncClient client = HttpAsyncClients.custom().
                setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .build();


        //构造请求
//        String url = "http://127.0.0.1:9200/_bulk";
//        HttpPost httpPost = new HttpPost(url);
//        StringEntity entity = null;

        for (int i = 0; i < 1000; i++) {
            List<NameValuePair> nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("corpid", "wwd6da61649bd66fea"));
            nameValuePairs.add(new BasicNameValuePair("corpsecret", "1JPyY9GvPLZfpvxEDjok-Xt_9v7HIBYJhZUoO6EgNGY"));
            URI uri = new URIBuilder("https://qyapi.weixin.qq.com/cgi-bin/gettoken").setParameters(nameValuePairs).build();
            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
//        try {
//            String a = "{ \"index\": { \"_index\": \"test\", \"_type\": \"test\"} }\n" +
//                    "{\"name\": \"上海\",\"age\":33}\n";
//            entity = new StringEntity(a);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        httpPost.setEntity(entity);

            //start

            client.start();

            //异步请求
            client.execute(httpGet, new Back());

        }
//
//        while(true){
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }

    static class Back implements FutureCallback<HttpResponse> {

        private long start = System.currentTimeMillis();
        Back(){
        }

        public void completed(HttpResponse httpResponse) {
            try {
                System.out.println("cost completed is:"+(System.currentTimeMillis()-start)+":"+ EntityUtils.toString(httpResponse.getEntity()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void failed(Exception e) {
            System.err.println(" cost is:"+(System.currentTimeMillis()-start)+":"+e);
        }

        public void cancelled() {

        }
    }
}