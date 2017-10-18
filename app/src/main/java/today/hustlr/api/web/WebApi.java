package today.hustlr.api.web;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;

import java.util.Date;
import java.util.concurrent.Executors;
import com.loopj.android.http.MySSLSocketFactory;
/**
 * Created by tuando on 10/14/17.
 */

public final class WebApi {
    private final AsyncHttpClient client = new AsyncHttpClient();
    private static final int API_TIMEOUT = 30 * 1000;
    private final Context context;
    private final String appName;
    private final String platform;
    private final String apiBaseUrl;

    public WebApi(Context context) {
        client.setTimeout(API_TIMEOUT);
        client.setResponseTimeout(API_TIMEOUT);
        client.setConnectTimeout(API_TIMEOUT);
        client.setEnableRedirects(true, true, false);
        client.setMaxConnections(1);
        client.setThreadPool(Executors.newSingleThreadExecutor());
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());

        this.context = context;
        this.platform = "Android";
        this.appName = "Hustlr";
        this.apiBaseUrl = "https://mobile-api-local.hustlrinc.com:1443/";
    }

}
