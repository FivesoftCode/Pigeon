package com.fivesoft.pigeon;

import android.app.Activity;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;

import java.util.HashMap;

public class Pigeon {

    private final Activity activity;

    /**
     * The GET method requests a representation of the specified resource. Requests using GET should only retrieve data.
     */
    public static final String METHOD_GET =    "GET";
    /**
     * The POST method is used to submit an entity to the specified resource, often causing a change in state or side effects on the server.
     */
    public static final String METHOD_POST =   "POST";
    /**
     * The PUT method replaces all current representations of the target resource with the request payload.
     */
    public static final String METHOD_PUT =    "PUT";
    /**
     * The DELETE method deletes the specified resource.
     */
    public static final String METHOD_DELETE = "DELETE";


    public static final String CONTENT_TYPE_APP_JSON = "application/json";
    public static final String CONTENT_TYPE_APP_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_TEXT_HTML = "text/html";
    public static final String CONTENT_TYPE_APP_XML = "application/xml";
    public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";


    public static final int REQUEST_PARAM = 0;
    public static final int REQUEST_BODY  = 1;

    String url = null;
    String tag = null;
    String method = METHOD_GET;
    String contentType;
    int requestType = 0;

    HashMap<String, Object> params = new HashMap<>();
    HashMap<String, Object> headers = new HashMap<>();
    RequestListener requestListener;

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
     *     <li>{@link #METHOD_GET}</li>
     *     <li>{@link #METHOD_POST}</li>
     *     <li>{@link #METHOD_PUT}</li>
     *     <li>{@link #METHOD_DELETE}</li>
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
     * Sets content type of the call.
     * @param contentType content type string. For more info visit:
     *                   <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Type">link</a>
     * @return Current Pigeon instance.
     */

    public Pigeon setContentType(String contentType){
        this.contentType = contentType;
        return this;
    }

    /**
     * Sets 'Authorization' header to 'Bearer ' + bearer.
     * <br>
     * <b>Warning!</b>
     * <br>Must be called after (or without) method {@link #setHeaders(HashMap)}, otherwise
     * authorization header will be overwritten.
     * @param bearer your authorization bearer (Without Bearer keyword ex. 'xxxxxxxyyyyyyyzzzzzzz')
     * @return Current Pigeon instance.
     */

    public Pigeon setAuthorizationBearer(@Nullable String bearer){

        if(headers == null)
            headers = new HashMap<>();

        if(bearer == null)
            headers.remove("Authorization");
        else
            headers.put("Authorization", "Bearer " + bearer);
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
        RequestNetworkController.getInstance().execute(this);
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
