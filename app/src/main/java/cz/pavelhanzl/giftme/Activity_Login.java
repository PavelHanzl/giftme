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

public class Activity_Login extends AppCompatActivity {
    ImageView mImageViewLogo;
    LinearLayout mLinearLayout;
    Button mButtonLogin, mButtonSignup;

    Handler mHandler = new Handler();
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mLinearLayout.setVisibility(View.VISIBLE);

            TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -150.0f);
            animation.setDuration(700);
            animation.setFillAfter(true);
            mImageViewLogo.startAnimation(animation);


        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLinearLayout = findViewById(R.id.linearLayout_login_fields);
        mImageViewLogo = findViewById(R.id.imageView_logo);
        mHandler.postDelayed(mRunnable,2500);// 2,5s timeout pro splash screen
    }
}
