package cz.pavelhanzl.giftme.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import cz.pavelhanzl.giftme.R;

public class Activity_Settings extends AppCompatActivity {
    private EditText mEditTextIdealBudget;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white); //nastaví ikonku křížku v actionbaru (nahradí defaultní šipku)
        setTitle(R.string.activity_settings_title);


        //link xml a java kódu
        mEditTextIdealBudget = findViewById(R.id.activity_settings_EditText_Ideal_Budget);

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
                saveSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveSettings(){
        String idealBudgetString = mEditTextIdealBudget.getText().toString();


        //nastaví hodnotu idealniho budgetu na 0 pokud uživatel nevyplnil pole s idealnim budgetem
        if (idealBudgetString.trim().isEmpty()) mEditTextIdealBudget.setText("0");

        //převede stringový edittext na int
        int price = Integer.parseInt(mEditTextIdealBudget.getText().toString());

//        //přidá hodnotu do databáze
//        CollectionReference giftlistReference = FirebaseFirestore.getInstance().collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Names").document(getIntent().getStringExtra("personsID")).collection("Giftlist");
//        giftlistReference.add(new Gift(name,price,archived,bought));
        Toast.makeText(this, getString(R.string.activity_newgift_gift_added), Toast.LENGTH_SHORT).show();
        finish();
    }
}
