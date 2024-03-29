package cz.pavelhanzl.giftme.social.gift_tips.others_gift_tips;

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
import cz.pavelhanzl.giftme.wishlist.GiftTip;
/**
 * Třída, která v sociální části Giftme přidává k uživateli tipy na dárky od jiných uživatelů.
 *
 * @author Pavel Hanzl
 * @version 1.04
 * @since   03-05-2019
 */
public class ActivityNewOthersGiftTip extends AppCompatActivity {
    private EditText mEditTextName;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_gifttip);//nastaví layout


        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close); //nastaví ikonku křížku v actionbaru (nahradí defaultní šipku)
        setTitle(R.string.activity_newowngifttip_add_new_gifttip); //nastaví title v actionbaru

        //link xml a java kódu
        mEditTextName = findViewById(R.id.activity_NewGiftTip_EditText_Name);

        mAuth = FirebaseAuth.getInstance();// získá instanci přihlášení

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
        //kontrola zda-li nějaké z polí není prázdné
        if (name.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.activity_newname_unfilled_fields), Toast.LENGTH_LONG).show();
            return;
        }

        //přidá hodnotu do databáze
        CollectionReference othersGiftTipsReference = FirebaseFirestore.getInstance().collection("Users").document(getIntent().getStringExtra("selectedUserEmail")).collection("OthersGiftTips");
        othersGiftTipsReference.add(new GiftTip(name,tipBy,bookedBy,""));
        Toast.makeText(this, getString(R.string.activity_new_own_gifttip_name_added), Toast.LENGTH_SHORT).show();
        finish();
    }
}
