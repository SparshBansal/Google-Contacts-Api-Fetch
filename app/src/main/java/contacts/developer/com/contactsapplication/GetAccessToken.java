package contacts.developer.com.contactsapplication;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Contacts;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;


/**
 * Created by sparsh on 2/14/17.
 */

public class GetAccessToken {
    private static final String TAG = GetAccessToken.class.getSimpleName();
    private static InputStream inputStream;
    private static JSONObject jsonObject;
    private static String json = "";
    private static Context context;
    private static OnTokenRecievedInterface tokenReceivedListener = null;

    public GetAccessToken(Context context) {
        this.context = context;
    }

    public void setOnTokenReceivedListener(OnTokenRecievedInterface tokenRecievedListener){
        this.tokenReceivedListener = tokenRecievedListener;
    }

    public String getAccessToken(String url, final String token, final String client_id, final String client_secret, final String redirect_uri, final String grant_type) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this.context);

        Uri uri = Uri.parse(url);
        /*Uri final_uri = uri.buildUpon().appendQueryParameter("code", token)
                .appendQueryParameter("client_id", client_id)
                .appendQueryParameter("client_secret", client_secret)
                .appendQueryParameter("redirect_uri", redirect_uri)
                .appendQueryParameter("grant_type", grant_type).build();*/

        String postUrl = uri.toString();
        Log.d(TAG, "getAccessToken: " + postUrl);

        mRequestQueue.add(new StringRequest(Request.Method.POST, postUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.has("access_token")){
                        String accessToken = responseObject.getString("access_token");
                        tokenReceivedListener.onTokenReceived(accessToken);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.toString());
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                Log.d(TAG, "onErrorResponse: " + error.networkResponse.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String , String> headers = new HashMap<>();
                headers.put("Content-Type" , "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String , String> params = new HashMap<>();
                params.put("code" , token);
                params.put("client_id" , client_id);
                params.put("client_secret" , client_secret);
                params.put("redirect_uri" , redirect_uri);
                params.put("grant_type" , grant_type);

                return params;
            }
        });

//        new GetTokenTask().execute(token,client_id , client_secret , redirect_uri , grant_type);
        return null;
    }
}

class GetTokenTask extends AsyncTask<String,Void,String>{

    @Override
    protected String doInBackground(String... params) {

        String authCode = params[0];
        String client_id = params[1];
        String client_secret = params[2];
        String redirect_uri = params[3];
        String grant_type = params[4];

        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();


        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(httpTransport,jsonFactory,client_id,client_secret,authCode,redirect_uri).execute();
            GoogleCredential credentials = new GoogleCredential.Builder()
                    .setClientSecrets(client_id,client_secret)
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory).build();
            credentials.setFromTokenResponse(tokenResponse);
            Log.d(TAG, "getAccessToken: " + tokenResponse.getAccessToken());
            return tokenResponse.getAccessToken();
        } catch (IOException e) {
            Log.d(TAG, "doInBackground: " + e.getMessage());
            Log.d(TAG, "getAccessToken: Some fucking exception occurred");
        }

        return null;
    }

    @Override
    protected void onPostExecute(String token) {
        super.onPostExecute(token);
        Log.d(TAG, "onPostExecute: " + token);
    }
}