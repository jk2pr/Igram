package org.jk.igram.activities.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.jk.igram.activities.InstagramWebActivity;
import org.jk.igram.activities.models.UserInfo;
import org.jk.igram.activities.sessions.Session;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class InstagramUtils {
    private Session mSession;
    //private InstagramDialog mDialog;
    private UserInfoCallBack mListener;
    private ProgressDialog mProgress;
    private String mAuthUrl;
    private String mTokenUrl;
    private String mAccessToken;
    private AppCompatActivity mCtx;
    private String mClientId;
    private String mClientSecret;

    private static int WHAT_FINALIZE = 0;
    private static int WHAT_ERROR = 1;
    private static int WHAT_FETCH_INFO = 2;
    private static int WHAT_FETCH_ALL_PIC = 3;


    public static String mCallbackUrl = "";
    private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    private static final String API_URL = "https://api.instagram.com/v1";

    private static final String TAG = "InstagramAPI";

    public InstagramUtils(AppCompatActivity context, String clientId, String clientSecret,
                          String callbackUrl) {
        mClientId = clientId;
        mClientSecret = clientSecret;
        mCtx = context;
        mSession = new Session(context);
        mAccessToken = mSession.getAccessToken();
        mCallbackUrl = callbackUrl;
        mTokenUrl = TOKEN_URL + "?client_id=" + clientId + "&client_secret="
                + clientSecret + "&redirect_uri=" + mCallbackUrl + "&grant_type=authorization_code";
        mAuthUrl = AUTH_URL + "?client_id=" + clientId + "&redirect_uri="
                + mCallbackUrl + "&response_type=code&display=touch&scope=likes+comments+relationships";


        // mDialog= InstagramDialog.newInstance(mAuthUrl,this);
        mProgress = new ProgressDialog(context);
        mProgress.setCancelable(false);
    }

    public void handleOnActvityResult(Intent data) {
        if (data.getStringExtra("ERROR") == null)
            getUserProfile(data.getStringExtra("DATA"));
        else
            Toast.makeText(mCtx, data.getStringExtra("ERROR"), Toast.LENGTH_LONG);

    }

    private void getUserProfile(final String code) {
        mProgress.setMessage("Getting access token ...");
        mProgress.show();

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Getting access token");
                int what = WHAT_FETCH_INFO;
                try {
                    URL url = new URL(TOKEN_URL);
                    Log.i(TAG, "Opening Token URL " + url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write("client_id=" + mClientId +
                            "&client_secret=" + mClientSecret +
                            "&grant_type=authorization_code" +
                            "&redirect_uri=" + mCallbackUrl +
                            "&code=" + code);
                    writer.flush();
                    String response = streamToString(urlConnection.getInputStream());
                    Log.i(TAG, "response " + response);
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                    UserInfo mUserInfor = new UserInfo();

                    String id = jsonObj.getJSONObject("user").getString("id");
                    String username = jsonObj.getJSONObject("user").getString("username");
                    String fullname = jsonObj.getJSONObject("user").getString("full_name");
                    String bio = jsonObj.getJSONObject("user").getString("bio");
                    String profile_pic = jsonObj.getJSONObject("user").getString("profile_picture");
                    String website = jsonObj.getJSONObject("user").getString("website");

                    mUserInfor.setBio(bio);
                    mUserInfor.setFullname(fullname);
                    mUserInfor.setId(id);
                    mUserInfor.setProfile_picture(profile_pic);
                    mUserInfor.setUsername(username);
                    mUserInfor.setWebsite(website);


                    mAccessToken = jsonObj.getString("access_token");
                    Log.i(TAG, "Got access token: " + mAccessToken);

                    mSession.storeAccessToken(mAccessToken, id, username, fullname);
                    mListener.onInfoLoad(mUserInfor);
                } catch (Exception ex) {
                    what = WHAT_ERROR;
                    ex.printStackTrace();
                }

                dismissDialog();
                //   mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));

            }
        }.start();
    }

    public void fetchUserName() {
        mProgress.setMessage("Finalizing ...");
        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Fetching user info");
                int what = WHAT_FETCH_ALL_PIC;
                try {
                    URL url = new URL(API_URL + "/users/" + mSession.getId() + "/?access_token=" + mAccessToken);

                    Log.d(TAG, "Opening URL " + url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.connect();
                    String response = streamToString(urlConnection.getInputStream());
                    System.out.println(response);
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                    String name = jsonObj.getJSONObject("data").getString("full_name");
                    String bio = jsonObj.getJSONObject("data").getString("bio");
                    Log.i(TAG, "Got name: " + name + ", bio [" + bio + "]");
                } catch (Exception ex) {
                    what = WHAT_ERROR;
                    ex.printStackTrace();
                }

                dismissDialog();
                //     mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
            }
        }.start();
    }


    /*private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_ERROR) {
                mProgress.dismiss();
                if (msg.arg1 == 1) {
                    mListener.onFail("Failed to get access token");
                } else if (msg.arg1 == 2) {
                    mListener.onFail("Failed to get user information");
                }
            } else if (msg.what == WHAT_FETCH_INFO) {
                fetchUserName();
            } else if(msg.what==WHAT_FETCH_ALL_PIC){
                mListener.onSuccess();
            }else{
                mProgress.dismiss();
                mListener.onSuccess(null);
            }
        }
    };
*/
    public boolean hasAccessToken() {
        return (mAccessToken == null) ? false : true;
    }

    public void setListener(UserInfoCallBack listener) {
        mListener = listener;
    }

    public String getUserName() {
        return mSession.getUsername();
    }

    public String getId() {
        return mSession.getId();
    }

    public String getName() {
        return mSession.getName();
    }


    public void authorize() {

        Intent intent = new Intent(mCtx, InstagramWebActivity.class);
        intent.putExtra(InstagramWebActivity.KEY_URL, mAuthUrl);
        mCtx.startActivityForResult(intent,0);
    }

    private String streamToString(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }

    public void resetAccessToken() {
        if (mAccessToken != null) {
            mSession.resetAccessToken();
            mAccessToken = null;
        }
    }

    public void fetchAllPic(final PictureLoadCallback mCallBack) {

        new Thread() {
            @Override
            public void run() {
                String picUrl = null;
                Log.i(TAG, "Fetching pic Data info");
                int what = WHAT_FINALIZE;
                try {
                    URL example = new URL("https://api.instagram.com/v1/users/self/media/recent?access_token="
                            + mAccessToken);

                    URLConnection tc = example.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            tc.getInputStream()));

                    String line;
                    while ((line = in.readLine()) != null) {
                        JSONObject ob = new JSONObject(line);

                        JSONArray object = ob.getJSONArray("data");

                        List<String> pictures = new ArrayList<String>();

                        for (int i = 0; i < object.length(); i++) {

                            JSONObject jo = (JSONObject) object.get(i);
                            JSONObject nja = (JSONObject) jo.getJSONObject("images");

                            JSONObject purl3 = (JSONObject) nja
                                    .getJSONObject("thumbnail");
                            picUrl = purl3.getString("url");
                            Log.i(TAG, "" + purl3.getString("url"));
                            pictures.add(picUrl);
                        }
                        mCallBack.onPictureLoad(pictures);
                        dismissDialog();

                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
                //  mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0, picUrl));

            }
        }.start();
    }


    private void dismissDialog() {
        ((Activity) mCtx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.dismiss();
            }
        });

    }


   /* @Override
    public void onComplete(String accessToken) {
        getUserProfile(accessToken);
    }

    @Override
    public void onError(String error) {
        mListener.onFail("Authorization failed");
    }*/

    /*public interface AuthListner {
        *//*  public abstract void onSuccess();*//*
        public abstract void onSuccess(UserInfo b);

        public abstract void onFail(String error);
    }*/

    public interface UserInfoCallBack {
        void onInfoLoad(UserInfo info);
    }

    public interface PictureLoadCallback {
        void onPictureLoad(List<String> pictures);
    }

    public interface ProfileLoadCallback {
        void onProfileLoad(List<String> pictures);
    }
}