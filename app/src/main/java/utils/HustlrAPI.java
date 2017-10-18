package utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Created by tuando on 10/11/17.
 */

public class HustlrAPI {

    public static String getResponseFromJsonURL(String urlStr) {


        try {
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            URL url = new URL(urlStr);
            InputStream inStream = null;

            try {
                HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setHostnameVerifier(hostnameVerifier);
                inStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
                StringBuffer jsonString = new StringBuffer();

                String line;
                while ((line = br.readLine()) != null) {
                    jsonString.append(line);
                }
                br.close();
            } catch (Exception e) {
//                Log.e(TAG, "error fetching data from server", e);
            } finally {
                if (inStream != null) {
                    inStream.close();
                }
            }
        } catch (Exception e) {
//            Log.e(TAG, "error initializing SkipVerificationn thread", e);
        }

        return "";
    }

//    private static void disableSSLCertificateChecking() {
//        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
//            public X509Certificate[] getAcceptedIssuers() {
//                return null;
//            }
//
//            @Override
//            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
//                // Not implemented
//            }
//
//            @Override
//            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
//                // Not implemented
//            }
//        } };
//
//        try {
//            SSLContext sc = SSLContext.getInstance("TLS");
//
//            sc.init(null, trustAllCerts, new java.security.SecureRandom());
//
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//    }

}
