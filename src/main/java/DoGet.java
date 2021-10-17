
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 *普通的GET请求
 */
public class DoGet {
    public static void main(String[] args) throws Exception {
        long startTime =  System.currentTimeMillis();
        List<NameValuePair> nameValuePairs = new ArrayList();
        nameValuePairs.add(new BasicNameValuePair("corpid", "wwd6da61649bd66fea"));
        nameValuePairs.add(new BasicNameValuePair("corpsecret", "1JPyY9GvPLZfpvxEDjok-Xt_9v7HIBYJhZUoO6EgNGY"));

        for (int i = 0; i < 1000 ; i++) {
            // 创建Httpclient对象
            CloseableHttpClient httpclient = HttpClients.createDefault();
            // 创建http GET请求
            URI uri = new URIBuilder("https://qyapi.weixin.qq.com/cgi-bin/gettoken").setParameters(nameValuePairs).build();
            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
            CloseableHttpResponse response = null;
            try {
                // 执行请求
                response = httpclient.execute(httpGet);
                // 判断返回状态是否为200
                if (response.getStatusLine().getStatusCode() != 200) {
                    break;
                }
            } finally {
                if (response != null) {
                    response.close();
                }
                //相当于关闭浏览器
                httpclient.close();
            }
            System.out.println("loop count"+String.valueOf(i));
        }
        long endTime =  System.currentTimeMillis();
        long usedTime = (endTime-startTime)/1000;
        System.out.println("cost time is "+String.valueOf(usedTime));
    }
}