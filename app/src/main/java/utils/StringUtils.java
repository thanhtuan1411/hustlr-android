package utils;

import android.annotation.SuppressLint;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;

import org.jetbrains.annotations.NotNull;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by tuando on 10/19/17.
 */

public final class StringUtils {
    public static final String PHONE_REG_EXP = "^((\\+\\d{1,3}(-| )?\\(?\\d\\)?(-| )?\\d{1,5})|(\\(?\\d{2,6}\\)?))(-| )?(\\d{3,4})(-| )?(\\d{4})(( x| ext)\\d{1,5}){0,1}$";
    public static final String HTTP_PREFIX = "http://";
    public static final String HTTPS_PREFIX = "https://";
    public static final String FILE_PREFIX = "file://";
    public static final String CONTENT_PREFIX = "content://";
    private static final String SALT = "SALT";

    @NotNull
    public static byte[] bytesFromString(String value) {
        return value != null && value.length() > 0 ? Base64.decode(value, Base64.DEFAULT) : new byte[0];
    }

    public static String bytesToString(byte[] value) {
        return value != null ? Base64.encodeToString(value, Base64.DEFAULT) : null;
    }

    public static byte[] toSha512(String value) {
        MessageDigest md;

        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return md.digest(value.getBytes());
    }

    /*public static byte[] toSha256(String value) {
        MessageDigest md;

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return md.digest((value + SALT).getBytes());
    }*/

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString();
    }

    public static String hashMac(String text, String secretKey)
            throws SignatureException {

        try {
            Key sk = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance(sk.getAlgorithm());
            mac.init(sk);
            final byte[] hmac = mac.doFinal(text.getBytes());
            return toHexString(hmac);
        } catch (NoSuchAlgorithmException e1) {
            // throw an exception or pick a different encryption method
            throw new SignatureException(
                    "error building signature, no such algorithm in device "
                            );
        } catch (InvalidKeyException e) {
            throw new SignatureException(
                    "error building signature, invalid key ");
        }
    }

    public static String toSha256Str(String value) {
        MessageDigest md;

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        md.update((value + SALT).getBytes());
        String result = bytesToHexString(md.digest());
        Logger.d("toSha256Str = "+result);
        return result;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    @NotNull
    public static String nullSafe(String value) {
        return value == null ? "" : value;
    }

    public static String removeHttpPrefix(String photoUrl) {
        if (photoUrl.toLowerCase().startsWith(HTTP_PREFIX))
            return photoUrl.substring(HTTP_PREFIX.length());

        if (photoUrl.toLowerCase().startsWith(HTTPS_PREFIX))
            return photoUrl.substring(HTTPS_PREFIX.length());

        return photoUrl;
    }

    public static String addHttpsPrefix(String photoUrl) {
        if (TextUtils.isEmpty(photoUrl) || photoUrl.toLowerCase().startsWith(HTTP_PREFIX) || photoUrl.toLowerCase().startsWith(HTTPS_PREFIX))
            return photoUrl;
        else
            return HTTPS_PREFIX + photoUrl;
    }

    @SuppressLint("DefaultLocale")
    public static String moneyString(float sum) {
        return String.format("$ %.2f", sum);
    }

    public static boolean visitIsUrgentCare(String serviceLineName, String urgentCareName) {
        return (!(serviceLineName == null || urgentCareName == null) && serviceLineName.toLowerCase().startsWith(urgentCareName.toLowerCase()));
    }

    public static boolean visitIsNoDocumentation(String serviceLineName, String noDocumentation) {
        return (!(serviceLineName == null || noDocumentation == null) && serviceLineName.equalsIgnoreCase(noDocumentation));
    }

    public static int stringToInt(String s) {
        int i = 0;
        if (s != null) {
            try {
                i = Integer.parseInt(s);
            } catch (NumberFormatException nfe) {
                Logger.i("Could not parse %s", s);
            }
        }
        return i;
    }

    public static Spanned html(String s) {
        if (s == null) s = "";
        return Html.fromHtml(s);
    }



    public static String getAge(Date dateOfBirth){
        if(dateOfBirth != null) {
            return getAge(dateOfBirth.getTime());
        }

        return "";
    }

    public static String getAge(long dateOfBirth){
        String ageS = "";
        if(dateOfBirth > 0) {
            Calendar dob = Calendar.getInstance(Locale.getDefault());
            Calendar today = Calendar.getInstance(Locale.getDefault());

            dob.setTimeInMillis(dateOfBirth);

            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            ageS = String.valueOf(age);
        }

        return ageS;
    }
}
