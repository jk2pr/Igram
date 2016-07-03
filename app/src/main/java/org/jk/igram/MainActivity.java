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
    private final String CLIENT_ID = "43ef4ede2335453fa73067588ab09fe0";
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
                "bf94790f6a3b49359f9442a5650c06c9", "http://xavient.com");
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