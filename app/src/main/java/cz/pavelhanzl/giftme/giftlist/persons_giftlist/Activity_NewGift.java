package cz.pavelhanzl.giftme.giftlist.persons_giftlist;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import cz.pavelhanzl.giftme.R;

public class Activity_NewGift extends AppCompatActivity {
    private EditText mEditTextName;
    private EditText mEditTextPrice;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_gift);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close); //nastaví ikonku křížku v actionbaru (nahradí defaultní šipku)

        setTitle(R.string.activity_newgift_add_new_name);


        //link xml a java kódu
        mEditTextName = findViewById(R.id.activity_NewGift_EditText_Name);
        mEditTextPrice = findViewById(R.id.activity_NewGift_EditText_Price);

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
                saveGift();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveGift(){
        String name = mEditTextName.getText().toString();
        String priceString = mEditTextPrice.getText().toString();
        boolean bought = false;
        boolean archived = false;

        //kontrola zda-li nějaké z polí není prázdné
        if (name.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.activity_newname_unfilled_fields), Toast.LENGTH_LONG).show();
            return;
        }

        //nastaví hodnotu ceny na 0 pokud uživatel nevyplnil pole s cenou
        if (priceString.trim().isEmpty()) mEditTextPrice.setText("0");

        //převede stringový edittext na int
        int price = Integer.parseInt(mEditTextPrice.getText().toString());

        //přidá hodnotu do databáze
        CollectionReference giftlistReference = FirebaseFirestore.getInstance().collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Names").document(getIntent().getStringExtra("personsID")).collection("Giftlist");
        giftlistReference.add(new Gift(name,price,archived,bought));
        Toast.makeText(this, getString(R.string.activity_newgift_gift_added), Toast.LENGTH_SHORT).show();
        finish();
    }
}
