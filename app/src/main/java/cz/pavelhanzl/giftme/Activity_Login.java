package cz.pavelhanzl.giftme;

import android.content.Intent;
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
    //členské proměnné
    ImageView mImageViewLogo;
    LinearLayout mLinearLayout;
    Button mButtonLogin, mButtonSignup;
    AVLoadingIndicatorView avi; //progress animace

    Handler mHandler = new Handler();
    //spustí se v on create po nastaveném timeout
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mLinearLayout.setVisibility(View.VISIBLE); //zobrazí kolonky pro přihlášení
            mButtonSignup.setVisibility(View.VISIBLE); //zobrazí tlačítko pro registraci

            //animace loga
            TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -150.0f);
            animation.setDuration(700);
            animation.setFillAfter(true);
            mImageViewLogo.startAnimation(animation);

            stopAnim(); //zastaví progress animaci
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Linkuje views ze xml s java kódem
        mLinearLayout = findViewById(R.id.linearLayout_login_fields);
        mImageViewLogo = findViewById(R.id.imageView_logo);
        mButtonSignup = findViewById(R.id.button_signup);
        avi = findViewById(R.id.avi);


        mHandler.postDelayed(mRunnable,3000);// 3s timeout pro splash screen
        startAnim();//startuje animaci progress

        // Přepne uživatele na activity signup
        final Intent signupIntent = new Intent(this, Activity_Signup.class);
        mButtonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(signupIntent);
            }
        });
    }


    /**
     * Zapne animaci progress při spuštění aplikace.
     */
    void startAnim(){
        avi.show();
        //avi.smoothToShow();
    }

    /**
     * Ukončí animaci progress.
     */
    void stopAnim(){
        avi.hide();
        //avi.smoothToHide();
    }
}
