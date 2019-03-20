package cz.pavelhanzl.giftme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Activity_NewName extends AppCompatActivity {
    private EditText mEditTextName;
    private EditText mEditTextBudget;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__new_name);

        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close); //nastaví ikonku křížku v actionbaru (nahradí defaultní šipku)
        setTitle(R.string.activity_newname_add_new_name);

        //link xml a java kódu
        mEditTextName = findViewById(R.id.activity_NewName_EditText_Name);
        mEditTextBudget = findViewById(R.id.activity_NewName_EditText_Budget);

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

    private void saveName(){
        String name = mEditTextName.getText().toString();
        int budget = Integer.parseInt(mEditTextBudget.getText().toString());

        if (name.trim().isEmpty() || mEditTextBudget.getText().toString().trim().isEmpty()){
            Toast.makeText(this, getString(R.string.activity_newname_unfilled_fields), Toast.LENGTH_LONG).show();
            return;
        }

        CollectionReference nameReference = FirebaseFirestore.getInstance().collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Names");
        nameReference.add(new Name(name,budget));
        Toast.makeText(this, getString(R.string.activity_newname_name_added), Toast.LENGTH_SHORT).show();
        finish();
    }
}
