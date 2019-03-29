package cz.pavelhanzl.giftme.wishlist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import cz.pavelhanzl.giftme.R;

public class Activity_NewOwnGiftTip extends AppCompatActivity {
    private EditText mEditTextName;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_gifttip);


        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close); //nastaví ikonku křížku v actionbaru (nahradí defaultní šipku)
        setTitle(R.string.activity_newowngifttip_add_new_gifttip);

        //link xml a java kódu
        mEditTextName = findViewById(R.id.activity_NewGiftTip_EditText_Name);

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
                saveGiftTip();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveGiftTip(){
        String name = mEditTextName.getText().toString();
        String bookedBy = null;
        String tipBy = mAuth.getCurrentUser().getEmail();
        //kontrola zda-li nějaké z polí není prázdné
        if (name.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.activity_newname_unfilled_fields), Toast.LENGTH_LONG).show();
            return;
        }



        //přidá hodnotu do databáze
        CollectionReference ownGiftTipsReference = FirebaseFirestore.getInstance().collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("OwnGiftTips");
        ownGiftTipsReference.add(new GiftTip(name,tipBy,bookedBy));
        Toast.makeText(this, getString(R.string.activity_new_own_gifttip_name_added), Toast.LENGTH_SHORT).show();
        finish();
    }
}
