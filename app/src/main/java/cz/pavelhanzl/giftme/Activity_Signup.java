package cz.pavelhanzl.giftme;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Activity_Signup extends AppCompatActivity {
    //Deklarace členských proměnných
    private Button mButtonBack, mButtonCreateAccount;
    private EditText mEditTextEmail, mEditTextPassword, mEditTextConfirmPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Získá odkaz na FirebaseAuth instanci
        mAuth = FirebaseAuth.getInstance();

        //Link xml a java kódu
        mButtonBack = findViewById(R.id.button_back);
        mButtonCreateAccount = findViewById(R.id.button_create_account);
        mEditTextEmail = findViewById(R.id.editText_signup_email);
        mEditTextPassword = findViewById(R.id.editText_signup_password);
        mEditTextConfirmPassword = findViewById(R.id.editText_signup_confirm_password);

        //vytváří uživatele
        mButtonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegistration();
            }
        });

        //ukončí aktivitu Signup a vrátí se k předchozí
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void attemptRegistration() {

        // resetuje errory zobrazené ve formu
        mEditTextEmail.setError(null);
        mEditTextPassword.setError(null);
        mEditTextConfirmPassword.setError(null);

        // uchovává hodnoty edittextů v okamžiku registrace
        String email = mEditTextEmail.getText().toString();
        String password =mEditTextPassword.getText().toString();
        String confirmPassword = mEditTextConfirmPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Kontrola správnosti hesla.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mEditTextPassword.setError(getString(R.string.error_invalid_password));
            focusView = mEditTextPassword;
            cancel = true;
        }

        // Kontrola správnosti emailu..
        if (TextUtils.isEmpty(email)) {
            mEditTextEmail.setError(getString(R.string.error_field_required));
            focusView = mEditTextEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEditTextEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEditTextEmail;
            cancel = true;
        }

        if (cancel) {
            // Nastal error, nepokoušej se o registraci a nastav focus na první špatně vyplněné pole.
            focusView.requestFocus();
        } else {
            createAccount(email,password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Add own logic to check for a valid password (minimum 6 characters)
        String confirmPassword = mEditTextConfirmPassword.getText().toString();
        return confirmPassword.equals(password) && password.length() > 6;
    }




    /**
     * Vytvoří užiatele ve  Firebase databázi, pokud stejný ještě neexistuje.
     * @param email email uživatele
     * @param password heslo uživatele
     */
    public void createAccount(String email, String password){
    mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("Giftee", "createUserWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                Toast.makeText(Activity_Signup.this,R.string.account_created,
                        Toast.LENGTH_SHORT).show();
                finish();
                //updateUI(user);
            } else {
                // If sign in fails, display a message to the user.
                Log.w("Giftee", "createUserWithEmail:failure", task.getException());
                Toast.makeText(Activity_Signup.this, R.string.registration_failed,
                        Toast.LENGTH_SHORT).show();
                //updateUI(null);
            }

            // ...
        }
    });
    }
}
