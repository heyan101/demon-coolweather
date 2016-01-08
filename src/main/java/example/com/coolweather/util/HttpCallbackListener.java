package example.com.coolweather.util;

/**
 * Created by Demon on 2016/1/7.
 */
public interface HttpCallbackListener
{
    void onFinish(String response);
    void onError(Exception e);
}
