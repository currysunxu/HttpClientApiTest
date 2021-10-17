import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;

import java.util.Date;

import static org.hamcrest.Matchers.equalTo;

public class TestToken {


    @Test
    public void testToken(){
        long startTime =  System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            RestAssured.given()
                    .queryParam("corpid","wwd6da61649bd66fea")
                    .queryParam("corpsecret","1JPyY9GvPLZfpvxEDjok-Xt_9v7HIBYJhZUoO6EgNGY")
                    .when().get("https://qyapi.weixin.qq.com/cgi-bin/gettoken")
                    .then().statusCode(200).body("errcode",equalTo(0));
            System.out.println("loop count"+String.valueOf(i));
        }
        long endTime =  System.currentTimeMillis();
        long usedTime = (endTime-startTime)/1000;
        System.out.println("cost time is "+String.valueOf(usedTime));
    }

    @Test
    public void testLoop(){
        long startTime =  System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            System.out.println("loop count"+String.valueOf(i));
        }
        long endTime =  System.currentTimeMillis();
        long usedTime = (endTime-startTime)/1000;
        System.out.println("cost time is "+String.valueOf(usedTime));
    }
}
