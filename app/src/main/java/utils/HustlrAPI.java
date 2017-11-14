package utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import today.hustlr.api.entity.User;

/**
 * Created by tuando on 10/11/17.
 */


public class HustlrAPI {

//    private static String rootApiUrl = "https://192.168.99.100:1443";
    private static String rootApiUrl = "https://mobile-api-dev.hustlrtech.com";
    private static String host = "mobile-api-dev.hustlrtech.com";
    private static String hmacSecret = "263dbbca-0fbc-4fb2-b8bf-a9d751983052";
    private static String signUp = "/v1/devices/user/sign_up";
    private static String saveUser = "/v1/devices/user/save_user";
    private static String logIn = "/v1/devices/user/login";
    private static String getUserByEmail = "/v1/devices/user/get_user_by_email";
    private static String verifyCode = "/v1/devices/user/verify_code";
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

    public static boolean login(String email, String password, String phone) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("email", email);
            obj.put("password", password);



            String result = getResponseFromAPI(rootApiUrl + logIn, obj.toString());

            try {

                JSONObject result_obj = new JSONObject(result);

                if (result_obj.getBoolean("success")) {
                    return true;
                }

            } catch (Throwable t) {

                return false;
            }
        } catch (JSONException e) {
            Log.e("TAG",e.toString());

        }

        return false;

    }

    public static String addUser(User user) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("email",user.email);
            obj.put("password",user.password);
            obj.put("first_name", user.first_name);
            obj.put("last_name",user.last_name);
            obj.put("street",user.street);
            obj.put("street2",user.street2);
            obj.put("city", user.city);
            obj.put("region",user.region);
            obj.put("postal_code",user.postal_code);
            obj.put("cell_phone",user.cell_phone);
            return getResponseFromAPI(rootApiUrl + signUp, obj.toString());
        } catch (JSONException ex) {
            return null;
        }
    }

    public static boolean verifyCode(String code, String email) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("code",code);
            obj.put("email",email);
            String result = getResponseFromAPI(rootApiUrl + verifyCode, obj.toString());

            try {

                JSONObject result_obj = new JSONObject(result);

                if (result_obj.getBoolean("success")) {
                    return true;
                }

            } catch (Throwable t) {
                return false;
            }
        } catch (JSONException ex) {
            return false;
        }
        return false;
    }


    public static String getResponseFromAPI(String urlStr, String requestBody) {
        try {

            StringBuffer jsonString = new StringBuffer();

            URL url = new URL(urlStr);
            InputStream inStream = null;
            String header = generateAuthHeader(urlStr, requestBody);

            try {
                HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", header);
                urlConnection.setRequestProperty("Host", host);

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
