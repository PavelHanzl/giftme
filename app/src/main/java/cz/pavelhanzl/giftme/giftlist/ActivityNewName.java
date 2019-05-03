package cz.pavelhanzl.giftme.giftlist;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import cz.pavelhanzl.giftme.R;

/**
 * Tato třída přidá novou osobu (objekt tipu Name) do seznamu obsahující gifltlisty těchto osob (Menu->Giftlists).
 *
 * @author  Pavel Hanzl
 * @version 1.04
 * @since   03-05-2019
 */
public class ActivityNewName extends AppCompatActivity {
    private EditText mEditTextName;
    private EditText mEditTextBudget;
    private FirebaseAuth mAuth;

    private boolean mEditing;
    private DocumentReference mEditedDocumentReference;
    private Name mEditedNameObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_name); //nastaví layout


        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close); //nastaví ikonku křížku v actionbaru (nahradí defaultní šipku)
        setTitle(R.string.activity_newname_add_new_name); //nastaví title v actionbaru

        //link xml a java kódu
        mEditTextName = findViewById(R.id.activity_NewName_EditText_Name);
        mEditTextBudget = findViewById(R.id.activity_NewName_EditText_Budget);

        mAuth = FirebaseAuth.getInstance();// získá instanci přihlášení

        checkIfEditing(); //spustí editaci položky místo vytvoření nové položky pokud z předchozí
                          //aktivity získáme přes intent informaci o tom, že nechceme vytváet, nýbrž editovat
    }

    /**
     * Zkontroluje, jestli chceme vytvořit nového uživatele nebo upravit stávajícího.
     * Pokud intent obsahuje "edit" true, pak jej chceme editovat. V tom pípadě předvyplní edittexty na příslušné hodnoty,
     * a přes mEditing zapne editovací mód této třidy.
     */
    private void checkIfEditing() {
        mEditing = getIntent().getBooleanExtra("edit",false);
        if(mEditing){
            setTitle(R.string.activity_newname_edit_name);

            mEditedDocumentReference = FirebaseFirestore.getInstance().document(getIntent().getStringExtra("path")); //Getting intent, setting DocumentReference
            mEditedDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    mEditedNameObject = documentSnapshot.toObject(Name.class);
                    mEditTextName.setText(mEditedNameObject.getName());
                    mEditTextBudget.setText(String.valueOf(mEditedNameObject.getBudget()));
                }
            });
        }
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
        String name = mEditTextName.getText().toString();
        String budgetString = mEditTextBudget.getText().toString();
        //kontrola zda-li nějaké z polí není prázdné
        if (name.trim().isEmpty() || budgetString.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.activity_newname_unfilled_fields), Toast.LENGTH_LONG).show();
            return;
        }

        //převede stringový edittext na int
        int budget = Integer.parseInt(mEditTextBudget.getText().toString());


        //pokud nevytváříme uživatele, ale pouze editujeme, tak uloží změněná data uživatele
        if(mEditing) {
            mEditedDocumentReference.set(new Name(name,budget));
            Toast.makeText(this, getString(R.string.activity_newname_name_edited), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //přidá hodnotu do databáze
        CollectionReference nameReference = FirebaseFirestore.getInstance().collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Names");
        nameReference.add(new Name(name,budget));
        Toast.makeText(this, getString(R.string.activity_newname_name_added), Toast.LENGTH_SHORT).show();
        finish();
    }
}
