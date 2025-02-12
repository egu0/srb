package im.eg.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
public final class HttpUtils {

    static final String POST = "POST";
    static final String GET = "GET";
    static final int CONN_TIMEOUT = 30000;// ms
    static final int READ_TIMEOUT = 30000;// ms

    /**
     * post 方式发送http请求.
     */
    public static byte[] doPost(String strUrl, byte[] reqData) {
        return send(strUrl, POST, reqData);
    }

    /**
     * get方式发送http请求.
     */
    public static byte[] doGet(String strUrl) {
        return send(strUrl, GET, null);
    }

    public static byte[] send(String strUrl, String reqMethod, byte[] reqData) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setConnectTimeout(CONN_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setRequestMethod(reqMethod);
            connection.connect();
            if (reqMethod.equalsIgnoreCase(POST)) {
                OutputStream os = connection.getOutputStream();
                os.write(reqData);
                os.flush();
                os.close();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;
            StringBuilder bankXmlBuffer = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                bankXmlBuffer.append(inputLine);
            }
            in.close();
            connection.disconnect();
            return bankXmlBuffer.toString().getBytes();
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            return null;
        }
    }

    /**
     * 从输入流中读取数据
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();// 网页的二进制数据
        outStream.close();
        inStream.close();
        return data;
    }
}