package cz.pavelhanzl.giftme.settings;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import cz.pavelhanzl.giftme.R;

public class ActivityChangePassword extends AppCompatActivity {
    private EditText mEditTextOldPass;
    private EditText mEditTextNewPass;
    private EditText mEditTextNewPassConfirm;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_settings_change_password);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close); //nastaví ikonku křížku v actionbaru (nahradí defaultní šipku)

        setTitle(R.string.activity_settings_change_pass_title);


        //link xml a java kódu
        mEditTextOldPass = findViewById(R.id.activity_settings_change_password_old);
        mEditTextNewPass = findViewById(R.id.activity_settings_change_password_new);
        mEditTextNewPassConfirm = findViewById(R.id.activity_settings_change_password_new_confirm);

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
                savePassword();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Načte uživatelem zadaná data, provede na nich jednoduchou validaci, reautentifikuje uživatele
     * a pokusí se uložit změněné heslo do firebase databáze.
     */
    private void savePassword() {
        String oldPass = mEditTextOldPass.getText().toString();
        final String newPass = mEditTextNewPass.getText().toString();
        String confirmNewPass = mEditTextNewPassConfirm.getText().toString();


        //kontrola zda-li nějaké z polí není prázdné
        if (oldPass.trim().isEmpty() || newPass.trim().isEmpty() || confirmNewPass.trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.activity_newname_unfilled_fields), Toast.LENGTH_LONG).show();
            return;
        }

        //kontrola, zda-li se nová hesla shodují
        if (!newPass.equals(confirmNewPass)) {
            Toast.makeText(this, getString(R.string.settings_changepass_pass_do_not_match), Toast.LENGTH_LONG).show();
            return;
        }


        user = FirebaseAuth.getInstance().getCurrentUser();

        final String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPass);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(ActivityChangePassword.this, getString(R.string.settings_changepass_failed), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ActivityChangePassword.this, getString(R.string.settings_changepass_sucessfull), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ActivityChangePassword.this, getString(R.string.settings_changepass_authentication_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


