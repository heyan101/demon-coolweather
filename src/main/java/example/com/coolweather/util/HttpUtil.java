package example.com.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 和服务端的交互
 * Created by Demon on 2016/1/7.
 */
public class HttpUtil
{
    /**
     * 用 HttpURLConnection 类向服务端发送 GET 请求
     * @param address 要请求的服务器的地址
     * @param listener 回调服务返回的结果
     */
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                HttpURLConnection httpURLConnection = null;
                try
                {
                    URL url = new URL(address);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(9000);
                    httpURLConnection.setReadTimeout(9000);
                    InputStream in = httpURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String readLine = "";
                    while ((readLine = reader.readLine()) != null)
                    {
                        response.append(readLine);
                    }
                    if (listener != null)
                    {
                        // 回调 onFinish() 方法
                        listener.onFinish(response.toString());
                    }
                }
                catch (Exception e)
                {
                    if (listener != null)
                    {
                        // 回调 onError() 方法
                        listener.onError(e);
                    }
                }
                finally
                {
                    if (httpURLConnection != null)
                    {
                        httpURLConnection.disconnect();
                    }
                }
            }
        });
    }
}
