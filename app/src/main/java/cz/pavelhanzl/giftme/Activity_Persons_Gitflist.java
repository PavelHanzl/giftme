package cz.pavelhanzl.giftme;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        final DocumentReference documentReferenceName = mDb.document(getIntent().getStringExtra("path"));
        documentReferenceName.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    mDocumentSnapshotName = task.getResult();
                    if (mDocumentSnapshotName.exists()) {
                        // Focus: Logika po načtení objektu zvoleného jména

                        mSelectedNameObject = mDocumentSnapshotName.toObject(Name.class);
                        setTitle(getString(R.string.giftlist_title) + " - " + mSelectedNameObject.getName());
                        Log.d("Activity_Persons_Giftli", " mSelectedNameObject " + mSelectedNameObject.getName());

                        mGiftReference = mDb.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Names").document(documentReferenceName.getId()).collection("Giftlist");
                            Log.d("Activity persons Giftli",mGiftReference.getPath());

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
        Query query = mGiftReference.orderBy("name", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Gift> options = new FirestoreRecyclerOptions.Builder<Gift>().setQuery(query, Gift.class).build();
        mAdapter_gift_default = new Adapter_Gift_Default(options);

        RecyclerView recyclerView = findViewById(R.id.activity_personsGiftlist_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter_gift_default);

        //deleteItemFromRecyclerView(recyclerView);
        //setCardsOnClickAction();
    }



    @Override
    public void onStop() {
        super.onStop();
        mAdapter_gift_default.stopListening();
    }
}
