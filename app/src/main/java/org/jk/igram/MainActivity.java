package org.jk.igram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.jk.igram.activities.models.UserInfo;
import org.jk.igram.activities.utils.InstagramUtils;

import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private List<Bitmap> bitmaps;
    private Map<Bitmap, Long> mHashMap;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private final String CLIENT_ID = "";
    private final String SECRET_ID = "";
    private final String CALL_BACK_URL = "";
    private InstagramUtils mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.fetchpic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.fetchAllPic(new InstagramUtils.PictureLoadCallback() {
                    @Override
                    public void onPictureLoad(List<String> pictures) {
                        pictures.get(0);

                    }
                });
            }
        });



        startInstagramLogin();

    }



    private void startInstagramLogin() {
        mApp = new InstagramUtils(this, CLIENT_ID,
                SECRET_ID, CALL_BACK_URL);
        mApp.setListener(new InstagramUtils.UserInfoCallBack() {
            @Override
            public void onInfoLoad(UserInfo mInfo) {

            }


        });
       // if(! mApp.hasAccessToken())
            mApp.authorize();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mApp.handleOnActvityResult(data);
    }
}