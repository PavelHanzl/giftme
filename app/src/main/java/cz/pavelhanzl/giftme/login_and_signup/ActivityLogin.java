package cz.pavelhanzl.giftme.login_and_signup;

import android.content.Intent;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import cz.pavelhanzl.giftme.ActivityMain;
import cz.pavelhanzl.giftme.R;

/**
 * Třída starající se o přihlášení uživatele do aplikace.
 *
 * @author Pavel Hanzl
 * @version 1.04
 * @since   03-05-2019
 */
public class ActivityLogin extends AppCompatActivity {
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
        if(!(currentUser == null)){
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
        mMainScreenIntent = new Intent(this, ActivityMain.class);


        mHandler.postDelayed(mRunnable,3000);// 3s timeout pro splash screen
        startAnim();//startuje animaci progress

        // Přepne uživatele na aktivitu signup
        final Intent signupIntent = new Intent(this, ActivitySignup.class);
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

    /**
     * Přihláší uživatele, pokud zadá správné uživatelské jméno a heslo.
     * @param email
     * @param password
     */
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

    /**
     * Zajišťuje, že zadané hodnoty nejsou prázdné.
     * @param validatedText
     * @return
     */
    private String makeNotEmpty(String validatedText){
        if(validatedText.isEmpty()){
            return validatedText+" ";
        }  else{
            return validatedText;
        }
    }

}
