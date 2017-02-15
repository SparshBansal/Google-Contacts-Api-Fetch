package contacts.developer.com.contactsapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnTokenRecievedInterface {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static Button bImportContacts;
    private static RecyclerView contactsView;
    private static Dialog authDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bImportContacts = (Button) findViewById(R.id.b_import_contacts);
        contactsView = (RecyclerView) findViewById(R.id.rv_contacts_view);

        bImportContacts.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.b_import_contacts:
                importContacts();

        }
    }


    public void importContacts() {
        // Launch the Authorization Dialog
        launchAuthDialog();
    }

    public void launchAuthDialog() {

        // Configure auth dialog
        authDialog = new Dialog(this);
        authDialog.setTitle("Sign in to Google");
        authDialog.setCancelable(true);
        authDialog.setContentView(R.layout.auth_dialog);

        authDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(MainActivity.this, "Sign In Unsuccessfull", Toast.LENGTH_SHORT).show();
            }
        });

        WebView webView = (WebView) authDialog.findViewById(R.id.wv_auth_view);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(GoogleConstants.OAUTH_URL + "?redirect_uri=" + GoogleConstants.REDIRECT_URI
                + "&response_type=code&client_id=" + GoogleConstants.CLIENT_ID
                + "&scope=" + GoogleConstants.OAUTH_SCOPE);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            boolean authComplete = false;

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("?code=") && !authComplete) {
                    Uri uri = Uri.parse(url);
                    String authCode = uri.getQueryParameter("code");
                    authComplete = true;
                    authDialog.dismiss();
                    GetAccessToken jParser = new GetAccessToken(getApplicationContext());
                    jParser.setOnTokenReceivedListener(MainActivity.this);
                    jParser.getAccessToken(GoogleConstants.TOKEN_URL, authCode, GoogleConstants.CLIENT_ID,
                            GoogleConstants.CLIENT_SECRET, GoogleConstants.REDIRECT_URI,
                            GoogleConstants.GRANT_TYPE
                    );
                    Log.d(TAG, "onPageFinished: " + authCode);
                }
            }
        });
        authDialog.show();
    }

    @Override
    public void onTokenReceived(String accessToken) {

        // Get the contact from https://www.google.com/m8/feeds/contacts/other/full?access_token="your_access_token"&alt=json

    }
}
