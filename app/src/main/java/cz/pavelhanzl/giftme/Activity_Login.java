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

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.wang.avi.AVLoadingIndicatorView;

public class Activity_Login extends AppCompatActivity {
    //Deklarace členských proměnné
    private ImageView mImageViewLogo;
    private LinearLayout mLinearLayout;
    private Button mButtonLogin, mButtonSignup;
    private AVLoadingIndicatorView avi; //progress animace
    private FirebaseAuth mAuth;

    private Handler mHandler = new Handler();
    //spustí se v on create po nastaveném timeout
    private Runnable mRunnable = new Runnable() {
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
        //initializuje FirebaseAuth instance
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        //Link xml a java kódu
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