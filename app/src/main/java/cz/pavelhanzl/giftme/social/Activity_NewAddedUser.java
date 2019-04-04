package cz.pavelhanzl.giftme.social;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import cz.pavelhanzl.giftme.R;

public class Activity_NewAddedUser extends AppCompatActivity {
    private EditText mEditTextName;
    private EditText mEditTextEmail;
    private FirebaseAuth mAuth;
    private String name,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_added_user);


        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close); //nastaví ikonku křížku v actionbaru (nahradí defaultní šipku)
        setTitle(R.string.activity_newname_add_new_name);


        //link xml a java kódu
        mEditTextName = findViewById(R.id.activity_NewAddedUser_EditText_name);
        mEditTextEmail = findViewById(R.id.activity_NewAddedUser_EditText_email);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_name_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_name_save:
                saveName();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Tato metoda získá uživatelem zadaná data, provede ověření zadaných údajů na validitu a poté
     * je uloží do firestore databáze (případně upraví stávající data). Po uložení do databáze
     * se vrací na předešlou aktivitu.
     */
    private void saveName(){
        name = mEditTextName.getText().toString();
        email = mEditTextEmail.getText().toString();

        //kontrola zda-li nějaké z polí není prázdné
        if (name.trim().isEmpty() || email.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.activity_newname_unfilled_fields), Toast.LENGTH_LONG).show();
            return;
        }

        //kontrola zda-li nezadávám svůj vlastní e-mail
        if (email.trim().equals(mAuth.getCurrentUser().getEmail())){
            Toast.makeText(this, getString(R.string.activity_newaddeduser_your_email_error), Toast.LENGTH_LONG).show();
            return;
        }

        //kontrola, zda-li je zadaný uživatel registrovaný
        DocumentReference userReference = FirebaseFirestore.getInstance().collection("Users").document(email);
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //pokud zadaný email nalezen přidá hodnotu do databáze
                        CollectionReference addedUserReference = FirebaseFirestore.getInstance().collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("AddedUsers");
                        addedUserReference.add(new AddedUser(name,email));
                        Toast.makeText(Activity_NewAddedUser.this, getString(R.string.activity_newaddeduser_user_found), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(Activity_NewAddedUser.this, getString(R.string.activity_newaddeduser_user_does_not_exists), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Activity_NewAddedUser.this, getString(R.string.activity_newaddeduser_error)+" "+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
