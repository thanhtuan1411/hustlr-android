package utils;

import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import today.hustlr.api.entity.User;

/**
 * Created by tuando on 10/11/17.
 */


public class HustlrAPI {

    private static String rootApiUrl = "https://192.168.99.100:1443";
//    private static String rootApiUrl = "https://mobile-api-local.hustlrtech.com:1443";
    private static String host = "192.168.99.100";
//    private static String rootApiUrl = "https://mobile-api-dev.hustlrtech.com";


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
                //Get User Obj
                Constants.loggedInUser = new User();
                JSONObject user_obj = result_obj.getJSONObject("user");
                Constants.loggedInUser.user_id = user_obj.getString("user_id");
                Constants.loggedInUser.first_name = user_obj.getString("first_name");
                Constants.loggedInUser.last_name = user_obj.getString("last_name");
                Constants.loggedInUser.email = user_obj.getString("email");
                Constants.loggedInUser.enabled = user_obj.getBoolean("enabled");
                Constants.loggedInUser.title = user_obj.getString("title");
                Constants.loggedInUser.date_of_birth = user_obj.getString("date_of_birth");
                Constants.loggedInUser.gender = user_obj.getString("gender");
                Constants.loggedInUser.street = user_obj.getString("street");
                Constants.loggedInUser.streettwo = user_obj.getString("streettwo");
                Constants.loggedInUser.postal_code = user_obj.getString("postal_code");
                Constants.loggedInUser.city = user_obj.getString("city");
                Constants.loggedInUser.region = user_obj.getString("country");
                Constants.loggedInUser.cell_phone = user_obj.getString("cell_phone");
                Constants.loggedInUser.status = user_obj.getString("status");

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

    public static User getUserByEmail(String email) {
        User user = new User();
        try {
            JSONObject obj = new JSONObject();
            obj.put("email",email);
        String result = getResponseFromAPI(rootApiUrl + getUserByEmail, obj.toString());

            try {
                JSONObject result_obj = new JSONObject(result);
                user.email = result_obj.getString("email");
                user.first_name = result_obj.getString("first_name");
                user.last_name = result_obj.getString("last_name");
                user.cell_phone = result_obj.getString("cell_phone");
                user.street = result_obj.getString("street");
                user.streettwo = result_obj.getString("streettwo");
                user.city = result_obj.getString("city");
                user.region = result_obj.getString("region");
                user.status = result_obj.getString("status");
                user.postal_code = result_obj.getString("postal_code");
                return user;
            } catch (Throwable t) {
                return null;
            }
        } catch (JSONException ex) {
            return null;
        }
    }

    public static String addUser(User user) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("email",user.email);
            obj.put("password",user.password);
            obj.put("first_name", user.first_name);
            obj.put("last_name",user.last_name);
            obj.put("street",user.street);
            obj.put("streettwo",user.streettwo);
            obj.put("city", user.city);
            obj.put("region",user.region);
            obj.put("postal_code",user.postal_code);
            obj.put("cell_phone",user.cell_phone);
            return getResponseFromAPI(rootApiUrl + signUp, obj.toString());
        } catch (JSONException ex) {
            return null;
        }
    }

    public static boolean saveUser(User user) {
        try {

            JSONObject obj = new JSONObject();
            obj.put("user_id",user.user_id);
            obj.put("email",user.email);
            obj.put("password",user.password);
            obj.put("first_name", user.first_name);
            obj.put("last_name",user.last_name);
            obj.put("street",user.street);
            obj.put("streettwo",user.streettwo);
            obj.put("city", user.city);
            obj.put("region",user.region);
            obj.put("postal_code",user.postal_code);
            obj.put("cell_phone",user.cell_phone);
            String result = getResponseFromAPI(rootApiUrl + saveUser, obj.toString());

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

//            urlStr = rootApiUrl + "/health";

            StringBuffer jsonString = new StringBuffer();

            URL url = new URL(urlStr);
            InputStream inStream = null;
            String header = generateAuthHeader(urlStr, requestBody);

            try {
                HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");

                if (rootApiUrl.indexOf("192.168.99.100:1443") >= 0) {
                    //Ignore SSL on local environment
                    HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    };
                    urlConnection.setHostnameVerifier(hostnameVerifier);
                }

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
