package cz.pavelhanzl.giftme;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.concurrent.CountDownLatch;

public class Activity_Persons_Gitflist extends AppCompatActivity {
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;
    private CollectionReference mGiftReference;
    private Adapter_Gift_Default mAdapter_gift_default;

    private DocumentSnapshot mDocumentSnapshotName;
    private Name mSelectedNameObject;
    private DocumentReference mDocumentReferenceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Persons_Giftlist","Processing oncreate");
        setContentView(R.layout.activity_persons_gitflist);
        setTitle(getString(R.string.giftlist_title));

        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        getDocumentSnapshotForSelectedName();

    }


    /**
     * Získá objekt ze zvolené položky v předchozí aktivitě.
     * Získávání dat z databáze firestore probíhá asynchronně, a kód této třídy závisí na získaném objektu, proto se zbytek kodu nachází až v onComplete isSuccessful metodě.
     */
    private void getDocumentSnapshotForSelectedName() {
        if(getIntent().getStringExtra("path") != null){
            Log.d("Persons_Giftlist","Getting intent, setting DocumentReference");
            mDocumentReferenceName = mDb.document(getIntent().getStringExtra("path"));
        }

        mDocumentReferenceName.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    mDocumentSnapshotName = task.getResult();
                    if (mDocumentSnapshotName.exists()) {
                        // Focus: Logika po načtení objektu zvoleného jména

                        mSelectedNameObject = mDocumentSnapshotName.toObject(Name.class);
                        setTitle(getString(R.string.giftlist_title) + " - " + mSelectedNameObject.getName());
                        Log.d("Activity_Persons_Giftli", " mSelectedNameObject " + mSelectedNameObject.getName());

                        mGiftReference = mDb.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Names").document(mDocumentReferenceName.getId()).collection("Giftlist");
                        Log.d("Activity persons Giftli", mGiftReference.getPath());

                        setUpFloatingButtons();
                        setUpRecyclerView();
                        mAdapter_gift_default.startListening();


                    } else {
                        Log.d("Activity_Persons_Giftli", "No such document");
                    }
                } else {
                    Log.d("Activity_Persons_Giftli", "get snapshot failed with ", task.getException());
                }
            }
        });
    }


    /**
     * Nastavuje recyclerView. Řadí podle jména.
     */
    private void setUpRecyclerView() {
        Query query = mGiftReference.orderBy("bought", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Gift> options = new FirestoreRecyclerOptions.Builder<Gift>().setQuery(query, Gift.class).build();
        mAdapter_gift_default = new Adapter_Gift_Default(options);

        RecyclerView recyclerView = findViewById(R.id.activity_personsGiftlist_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter_gift_default);

        deleteItemFromRecyclerView(recyclerView);
        setCardsOnClickAction();
    }

    /**
     * Odstraní položku z recyclerView při posunutí položky doprava nebo doleva.
     *
     * @param recyclerView
     */
    private void deleteItemFromRecyclerView(RecyclerView recyclerView) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;

            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                int adapterPosition = viewHolder.getAdapterPosition();
                if (i == ItemTouchHelper.RIGHT) {
                    mAdapter_gift_default.archivateItem(viewHolder.getAdapterPosition());
                    Toast.makeText( getApplicationContext(), "Archivated", Toast.LENGTH_SHORT ).show();
                } else if (i == ItemTouchHelper.LEFT) {
                    mAdapter_gift_default.deleteItem(viewHolder.getAdapterPosition());
                    Toast.makeText( getApplicationContext(), "Deleted", Toast.LENGTH_SHORT ).show();
                }


            }

//            @Override
//            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//                    // Here, if dX > 0 then swiping right.
//                    // If dX < 0 then swiping left.
//                    // If dX == 0 then at at start position.
//                    if (dX>80){
//                        Toast.makeText(Activity_Persons_Gitflist.this, "20 doprava", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//                }
//            }
        }).attachToRecyclerView(recyclerView);
    }

    /**
     * Znovu inicializuje obrazovku při statu této aktivity. Důléžité při přechodu zpět z vytvoření nové item, aby se znovu spustilo poslochání na Adaptéru.
     */
    @Override
    public void onStart() {
        super.onStart();
        getDocumentSnapshotForSelectedName();
    }


    @Override
    public void onStop() {
        super.onStop();
        mAdapter_gift_default.stopListening();
    }

    /**
     * Nastaví floating button pro přidání dárku na giftlist. A v extra odešle ID otevřené osoby, pro kterou je určen otevřený giftlist.
     */
    private void setUpFloatingButtons() {
        FloatingActionButton buttonAddGift = findViewById(R.id.activity_personsGiftlist_floatingButton_add_gift);
        buttonAddGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Activity_NewGift.class).putExtra("personsID",mDocumentReferenceName.getId()));
            }
        });

        FloatingActionButton buttonShowArchive = findViewById(R.id.activity_personsGiftlist_floatingButton_show_archive);
        buttonShowArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Activity_Persons_Gitflist_Archive.class).putExtra("path",mDocumentReferenceName.getPath()));
            }
        });
    }

    /**
     * Nastavuje co se stane po kliknutí na na checkbox u itemu.
     */
    //TODO: přejmenovat tuto metodu, aby odpovídala svému záměru
    private void setCardsOnClickAction() {
        mAdapter_gift_default.setOnItemClickListener(new Adapter_Gift_Default.OnItemClickListener() {
            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {
                Gift gift = documentSnapshot.toObject(Gift.class);

                //pokud není checkbox "bought" zaškrtlý, tak ho zaškrtne a naopak...
                if(!gift.isBought()){
                    gift.setBought(true);
                    Toast.makeText(getApplicationContext(),getString(R.string.checkbox_isBought_true_toast), Toast.LENGTH_SHORT).show();
                }else {
                    gift.setBought(false);
                    Toast.makeText(getApplicationContext(),getString(R.string.checkbox_isBought_false_toast), Toast.LENGTH_SHORT).show();
                }

                documentSnapshot.getReference().set(gift);

            }
        });
    }

}
