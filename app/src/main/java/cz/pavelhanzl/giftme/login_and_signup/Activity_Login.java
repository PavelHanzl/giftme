package cz.pavelhanzl.giftme.login_and_signup;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wang.avi.AVLoadingIndicatorView;

import cz.pavelhanzl.giftme.Activity_Main;
import cz.pavelhanzl.giftme.R;

public class Activity_Login extends AppCompatActivity {
    //Deklarace členských proměnných
    private ImageView mImageViewLogo;
    private LinearLayout mLinearLayout;
    private Button mButtonLogin, mButtonSignup;
    private EditText mEditTextEmail, mEditTextPassword;
    private AVLoadingIndicatorView avi; //progress animace
    private FirebaseAuth mAuth;
    private Intent mMainScreenIntent;

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

    //kontroluje zdali je někdo přihlášen, pokud ano, přesměruje ho to na hlavní aktivitu
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Toast.makeText(getApplicationContext(), "nikdo neprihlasen",
                    Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Prihlasen:" + currentUser.getEmail(),
                    Toast.LENGTH_SHORT).show();
            startActivity(mMainScreenIntent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Získá odkaz na FirebaseAuth instanci
        mAuth = FirebaseAuth.getInstance();

        //Link xml a java kódu
        mLinearLayout = findViewById(R.id.linearLayout_login_fields);
        mImageViewLogo = findViewById(R.id.imageView_logo);
        mButtonSignup = findViewById(R.id.button_signup);
        mButtonLogin =findViewById(R.id.button_login_now);
        mEditTextEmail=findViewById(R.id.editText_login_username);
        mEditTextPassword=findViewById(R.id.editText_login_password);
        avi = findViewById(R.id.avi);

        //inicializuje intent
        mMainScreenIntent = new Intent(this, Activity_Main.class);


        mHandler.postDelayed(mRunnable,3000);// 3s timeout pro splash screen
        startAnim();//startuje animaci progress

        // Přepne uživatele na aktivitu signup
        final Intent signupIntent = new Intent(this, Activity_Signup.class);
        mButtonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(signupIntent);
            }
        });

        // Přepne uživatele na hlavni aktivitu, pokud se uspesne prihlasi
        //final Intent mainScreenIntent = new Intent(this, Activity_Main.class);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(mEditTextEmail.getText().toString(),mEditTextPassword.getText().toString());
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


    void signIn(String email, String password){
    // validuje email a heslo na prázdné hodnoty
    email = makeNotEmpty(email);
    password = makeNotEmpty(password);


    mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("Giftme", "signInWithEmail:success");
                Toast.makeText(getApplicationContext(), R.string.login_successful,
                        Toast.LENGTH_SHORT).show();
                FirebaseUser user = mAuth.getCurrentUser();
                startActivity(mMainScreenIntent);
                finish();
            } else {
                // If sign in fails, display a message to the user.
                Log.w("Giftme", "signInWithEmail:failure", task.getException());
                Toast.makeText(getApplicationContext(), R.string.login_failed,
                        Toast.LENGTH_SHORT).show();
            }

            // ...
        }
    });
    }
    private String makeNotEmpty(String validatedText){
        if(validatedText.isEmpty()){
            return validatedText+" ";
        }  else{
            return validatedText;
        }
    }

}
