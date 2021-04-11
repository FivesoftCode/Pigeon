package com.fivesoft.pigeon;

import android.app.Activity;

import org.json.JSONException;

import java.util.HashMap;

public class Pigeon {

    private HashMap<String, Object> params = new HashMap<>();
    private HashMap<String, Object> headers = new HashMap<>();
    private final Activity activity;
    private int requestType = 0;
    private RequestListener requestListener;

    /**
     * The GET method requests a representation of the specified resource. Requests using GET should only retrieve data.
     */
    public static final String GET      = "GET";
    /**
     * The POST method is used to submit an entity to the specified resource, often causing a change in state or side effects on the server.
     */
    public static final String POST     = "POST";
    /**
     * The PUT method replaces all current representations of the target resource with the request payload.
     */
    public static final String PUT      = "PUT";
    /**
     * The DELETE method deletes the specified resource.
     */
    public static final String DELETE   = "DELETE";

    public static final int REQUEST_PARAM = 0;
    public static final int REQUEST_BODY  = 1;

    private String url = null;
    private String tag = null;
    private String method = GET;

    private Pigeon(Activity activity) {
        this.activity = activity;
}

    /**
     * Creates new Pigeon instance.
     * @param activity Running activity.
     * @return New Pigeon instance.
     */

    public static Pigeon from(Activity activity){
        return new Pigeon(activity);
    }

    /**
     * Sets the request headers.
     * @param headers Headers map.
     * @return Current Pigeon instance.
     */

    public Pigeon setHeaders(HashMap<String, Object> headers){
        this.headers = headers;
        return this;
    }

    /**
     * Adds header to request.
     * @param key Header key.
     * @param value Header value.
     * @return Current Pigeon instance.
     */

    public Pigeon addHeader(String key, Object value){
        if(headers == null)
            headers = new HashMap<>();

        headers.put(key, value);
        return this;
    }

    /**
     * Sets the request listener called when results are ready.
     * @param requestListener The request listener.
     * @return Current Pigeon instance.
     */

    public Pigeon setListener(RequestListener requestListener){
        this.requestListener = requestListener;
        return this;
    }

    /**
     * Sets the request params.
     * @param params Params map.
     * @return Current Pigeon instance.
     */

    public Pigeon setParams(HashMap<String, Object> params, int requestType) {
        this.params = params;
        this.requestType = requestType;
        return this;
    }

    /**
     * Adds param to request.
     * @param name Param name.
     * @param value Param value.
     * @return Current Pigeon instance.
     */

    public Pigeon addParam(String name, Object value){
        if(params == null)
            params = new HashMap<>();

        headers.put(name, value);
        return this;
    }

    /**
     * Sets the request url.
     * @param url The url.
     * @return Current Pigeon instance.
     */

    public Pigeon setUrl(String url){
        this.url = url;
        return this;
    }

    /**
     * Sets the request method.
     * There are 4 methods available:
     * <ul>
     *     <li>{@link #GET}</li>
     *     <li>{@link #POST}</li>
     *     <li>{@link #PUT}</li>
     *     <li>{@link #DELETE}</li>
     * </ul>
     * For more info about HTTP request methods check <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods">this</a>.
     * @param method The method.
     * @return Current Pigeon instance.
     */

    public Pigeon setMethod(String method){
        this.method = method;
        return this;
    }

    /**
     * Sets the tag which may help you to identify the request
     * when you are making multiple calls.
     * @param tag The tag.
     * @return Current Pigeon instance.
     */

    public Pigeon setTag(String tag){
        this.tag = tag;
        return this;
    }

    /**
     * Starts the api call.
     */

    public void fly(){
        RequestNetworkController.getInstance().execute(this, method, url, tag, requestListener);
    }

    public HashMap<String, Object> getParams() {
        return params;
}

    public HashMap<String, Object> getHeaders() {
        return headers;
}

    public Activity getActivity() {
        return activity;
    }

    public int getRequestType() {
        return requestType;
}

    public interface RequestListener {
         void onResponse(String tag, String response) throws JSONException;
         void onErrorResponse(String tag, String message);
    }

}
