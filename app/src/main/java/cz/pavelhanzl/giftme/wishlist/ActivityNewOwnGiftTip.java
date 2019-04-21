package cz.pavelhanzl.giftme.wishlist;

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
/**
 * Tato třída přidá nový vlastní gifttip (objekt tipu Gifttip) do seznamu v Menu->MyWishlist.
 */
public class ActivityNewOwnGiftTip extends AppCompatActivity {
    private EditText mEditTextName;
    private EditText mEditTextDesription;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_own_gifttip);


        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close); //nastaví ikonku křížku v actionbaru (nahradí defaultní šipku)
        setTitle(R.string.activity_newowngifttip_add_new_gifttip);

        //link xml a java kódu
        mEditTextName = findViewById(R.id.activity_NewGiftTip_EditText_Name);
        mEditTextDesription = findViewById(R.id.activity_NewGiftTip_EditText_Description);

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

    /**
     * Tato metoda získá uživatelem zadaná data, provede ověření zadaných údajů na validitu a poté
     * je uloží do firestore databáze (případně upraví stávající data). Po uložení do databáze
     * se vrací na předešlou aktivitu.
     */
    private void saveGiftTip(){
        String name = mEditTextName.getText().toString();
        String bookedBy = null;
        String tipBy = mAuth.getCurrentUser().getEmail();
        String description = mEditTextDesription.getText().toString();

        //kontrola zda-li name není prázdné
        if (name.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.activity_newname_unfilled_fields), Toast.LENGTH_LONG).show();
            return;
        }

        //pokud není vyplněn popis nastaví ho na prázdnou hodnotu
        if (description.trim().isEmpty()){
            description = "";
        }

        //přidá hodnotu do databáze
        CollectionReference ownGiftTipsReference = FirebaseFirestore.getInstance().collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("OwnGiftTips");
        ownGiftTipsReference.add(new GiftTip(name,tipBy,bookedBy,description));
        Toast.makeText(this, getString(R.string.activity_new_own_gifttip_name_added), Toast.LENGTH_SHORT).show();
        finish();
    }
}
