package utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by tuando on 10/11/17.
 */


public class HustlrAPI {

    private static String rootApiUrl = "https://192.168.99.100:1443";
    private static String hmacSecret = "263dbbca-0fbc-4fb2-b8bf-a9d751983052";
    private static String signUp = "/v1/devices/user/sign_up";
    private static String saveUser = "/v1/devices/user/save_user";
    private static String logIn = "/v1/devices/user/login";
    private static String getUserByEmail = "/v1/devices/user/get_user_by_email";
    private static String apiKey = "mobile_device";


    private static String generateAuthHeader(String url, String requestBody) {

        String header = "";

        try {

            long time = System.currentTimeMillis();

            String timestamp = String.valueOf(time);

            String data =  timestamp + "-" + requestBody;
            String signature = StringUtils.hashMac(data, hmacSecret);
            header = "Credential=" + apiKey + ", Window=" + timestamp + ", Signature=" + signature;
        } catch (Exception ex) {

        }
        return header;

    }

    public static String addUser() {

        try {
            JSONObject obj = new JSONObject();
            obj.put("user_id","");
            obj.put("email","user1@yopmail.com");
            obj.put("password","Hustlr2017");
            obj.put("user_id","");
            obj.put("first_name", "User 1");
            obj.put("last_name","Last");
            obj.put("street","3904 Railroad Ave");
            obj.put("city","Fairfax");
            obj.put("region","VA");
            obj.put("postal_code","22030");
            obj.put("cell_phone","7036466862");
            return getResponseFromJsonURL(rootApiUrl + signUp, obj.toString());
        } catch (JSONException ex) {
            return null;
        }


    }


    public static String getResponseFromJsonURL(String urlStr, String requestBody) {
        try {
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            StringBuffer jsonString = new StringBuffer();

            URL url = new URL(urlStr);
            InputStream inStream = null;
            String header = generateAuthHeader(urlStr, requestBody);

            try {
                HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.setHostnameVerifier(hostnameVerifier);
                urlConnection.setRequestProperty("Authorization", header);
                urlConnection.setRequestProperty("Host", "192.168.99.100");

                OutputStreamWriter out = new   OutputStreamWriter(urlConnection.getOutputStream());
                out.write(requestBody);
                out.close();

                int HttpResult =urlConnection.getResponseCode();
                if(HttpResult ==HttpsURLConnection.HTTP_OK){
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream(),"utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        jsonString.append(line + "\n");
                    }
                    br.close();

                    System.out.println(""+jsonString.toString());

                }else{
                    System.out.println(urlConnection.getResponseMessage());
                }

//                inStream = urlConnection.getInputStream();
//                BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
//                jsonString = new StringBuffer();
//
//                String line;
//                while ((line = br.readLine()) != null) {
//                    jsonString.append(line);
//                }
//                br.close();
            } catch (Exception e) {

                Log.e("TAG",e.toString());
            } finally {
                if (inStream != null) {
                    inStream.close();
                }
            }
            return jsonString.toString();
        } catch (Exception e) {
            return "";
//            Log.e(TAG, "error initializing SkipVerificationn thread", e);
        }


    }



}
