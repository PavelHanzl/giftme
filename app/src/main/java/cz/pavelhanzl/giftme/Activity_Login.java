package cz.pavelhanzl.giftme;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wang.avi.AVLoadingIndicatorView;

public class Activity_Login extends AppCompatActivity {
    ImageView mImageViewLogo;
    LinearLayout mLinearLayout;
    Button mButtonLogin, mButtonSignup;
    AVLoadingIndicatorView avi;


    Handler mHandler = new Handler();
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mLinearLayout.setVisibility(View.VISIBLE);
            mButtonSignup.setVisibility(View.VISIBLE);
            TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -150.0f);
            animation.setDuration(700);
            animation.setFillAfter(true);
            mImageViewLogo.startAnimation(animation);
            stopAnim();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLinearLayout = findViewById(R.id.linearLayout_login_fields);
        mImageViewLogo = findViewById(R.id.imageView_logo);
        mButtonSignup = findViewById(R.id.button_signup);
        avi = findViewById(R.id.avi);
        mHandler.postDelayed(mRunnable,3000);// 3s timeout pro splash screen
        startAnim();
    }
    void startAnim(){
        avi.show();
        //avi.smoothToShow();
    }

    void stopAnim(){
        avi.hide();
        //avi.smoothToHide();
    }
}
