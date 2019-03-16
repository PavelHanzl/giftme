package cz.pavelhanzl.giftme;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        //Link xml a java kódu
        mButtonBack = findViewById(R.id.button_back);
        mButtonCreateAccount = findViewById(R.id.button_create_account);

        mButtonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: dodělat logiku k vytváření uživatele
                createAccount("hanzlpavel77@gmail.com","123456Ab");
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

    public void createAccount(String email, String password){
    mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("Giftee", "createUserWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                Toast.makeText(Activity_Signup.this, user.toString(),
                        Toast.LENGTH_SHORT).show();
                //updateUI(user);
            } else {
                // If sign in fails, display a message to the user.
                Log.w("Giftee", "createUserWithEmail:failure", task.getException());
                Toast.makeText(Activity_Signup.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
                //updateUI(null);
            }

            // ...
        }
    });
    }
}
