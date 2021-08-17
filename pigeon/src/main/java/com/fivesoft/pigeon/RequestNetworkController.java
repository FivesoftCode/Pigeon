package com.fivesoft.pigeon;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.fivesoft.pigeon.Pigeon.METHOD_GET;
import static com.fivesoft.pigeon.Pigeon.REQUEST_PARAM;

class RequestNetworkController {

private static final int SOCKET_TIMEOUT = 15000;
private static final int READ_TIMEOUT   = 25000;

protected OkHttpClient client;

private static RequestNetworkController mInstance;

public static synchronized RequestNetworkController getInstance() {
    if(mInstance == null) {
        mInstance = new RequestNetworkController();
    }
    return mInstance;
}

private OkHttpClient getClient() {
    if (client == null) {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();

    try {
        final TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        }};

        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        builder.connectTimeout(SOCKET_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.hostnameVerifier((hostname, session) -> true);
    } catch (Exception e) {
        e.printStackTrace();
    }

    client = builder.build();
    }

    return client;
}

void execute(Pigeon pigeon) {
    Request.Builder reqBuilder = new Request.Builder();
    Headers.Builder headerBuilder = new Headers.Builder();

    final String tag = pigeon.tag;

    if(pigeon.getHeaders().size() > 0) {
        HashMap<String, Object> headers = pigeon.getHeaders();
        for(HashMap.Entry<String, Object> header : headers.entrySet()) {
            headerBuilder.add(header.getKey(), String.valueOf(header.getValue()));
        }
    }

    try {
        if (pigeon.getRequestType() == REQUEST_PARAM) {
            if (pigeon.method.equals(METHOD_GET)) {
                HttpUrl.Builder httpBuilder;
                try {
                    httpBuilder = HttpUrl.parse(pigeon.url).newBuilder();
                } catch (NullPointerException ne) {
                    throw new NullPointerException("Unexpected url: " + pigeon.url);
                }

                if (pigeon.getParams().size() > 0) {
                    HashMap<String, Object> params = pigeon.getParams();

                    for (HashMap.Entry<String, Object> param : params.entrySet()) {
                        httpBuilder.addQueryParameter(param.getKey(), String.valueOf(param.getValue()));
                    }
                }
                reqBuilder.url(httpBuilder.build()).headers(headerBuilder.build()).get();
            } else {
                FormBody.Builder formBuilder = new FormBody.Builder();
                if (pigeon.getParams().size() > 0) {
                    HashMap<String, Object> params = pigeon.getParams();

                    for (HashMap.Entry<String, Object> param : params.entrySet()) {
                        formBuilder.add(param.getKey(), String.valueOf(param.getValue()));
                    }
                }

                RequestBody reqBody = formBuilder.build();

                reqBuilder.url(pigeon.url).headers(headerBuilder.build()).method(pigeon.method, reqBody);
            }
        } else {
            RequestBody reqBody = RequestBody.create(new Gson().toJson(pigeon.getParams()), okhttp3.MediaType.parse(pigeon.contentType));

            if (pigeon.method.equals(METHOD_GET)) {
                reqBuilder.url(pigeon.url).headers(headerBuilder.build()).get();
            } else {
                reqBuilder.url(pigeon.url).headers(headerBuilder.build()).method(pigeon.method, reqBody);
            }
        }

        Request req = reqBuilder.build();

        getClient().newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                pigeon.getActivity().runOnUiThread(() -> {
                    if(pigeon.requestListener != null)
                        pigeon.requestListener.onErrorResponse(tag, e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {
                final String responseBody;
                try {
                    responseBody = response.body().string().trim();
                    pigeon.getActivity().runOnUiThread(() -> {
                        try {
                            if (pigeon.requestListener != null)
                                pigeon.requestListener.onResponse(tag, responseBody);
                        } catch (JSONException e) {
                            pigeon.requestListener.onErrorResponse(tag, e.toString());
                        }
                    });
                } catch (Exception e) {
                    if (pigeon.requestListener != null)
                        pigeon.requestListener.onErrorResponse(tag, e.toString());
                }
            }
        });
    } catch (Exception e) {
        if(pigeon.requestListener != null)
            pigeon.requestListener.onErrorResponse(tag, e.getMessage());
    }
}
}