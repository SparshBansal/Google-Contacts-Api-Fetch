package contacts.developer.com.contactsapplication;

/**
 * Created by sparsh on 2/14/17.
 */

public class GoogleConstants {
    public static final String CLIENT_ID = "client_id";


    public static String CLIENT_SECRET = "client_secret";
    // Use your own client secret

    public static String REDIRECT_URI = "http://localhost";
    public static String GRANT_TYPE = "authorization_code";
    public static String TOKEN_URL = "https://accounts.google.com/o/oauth2/token";
    public static String OAUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    public static String OAUTH_SCOPE = "https://www.googleapis.com/auth/contacts.readonly";

    public static final String CONTACTS_URL = "https://www.google.com/m8/feeds/contacts/default/full";
    public static final int MAX_NB_CONTACTS = 1000;
    public static final String APP = "Contacts App";
}
