package cz.pavelhanzl.giftme.giftlist.persons_giftlist_archive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import cz.pavelhanzl.giftme.Activity_Main;
import cz.pavelhanzl.giftme.giftlist.Activity_NewName;
import cz.pavelhanzl.giftme.giftlist.persons_giftlist.Gift;
import cz.pavelhanzl.giftme.giftlist.Name;
import cz.pavelhanzl.giftme.R;

public class Activity_Persons_Gitflist_Archive extends AppCompatActivity {
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;
    private CollectionReference mGiftReference;
    private Adapter_Gift_Archive mAdapter_gift_archive;

    private DocumentSnapshot mDocumentSnapshotName;
    private Name mSelectedNameObject;
    private DocumentReference mDocumentReferenceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Giftlist Archive","Processing oncreate");
        setContentView(R.layout.activity_persons_gitflist_archive);
        setTitle(getString(R.string.archive_title));

        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        getDocumentSnapshotForSelectedName();
        showAtFirstRunOnly();

    }


    /**
     * Získá objekt ze zvolené položky v předchozí aktivitě.
     * Získávání dat z databáze firestore probíhá asynchronně, a kód této třídy závisí na získaném objektu, proto se zbytek kodu nachází až v onComplete isSuccessful metodě.
     */
    private void getDocumentSnapshotForSelectedName() {
        if(getIntent().getStringExtra("path") != null){
            Log.d("Giftlist Archive","Getting intent, setting DocumentReference");
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
                        setTitle(getString(R.string.archive_title) + " - " + mSelectedNameObject.getName());
                        Log.d("Activity_archive", " mSelectedNameObject " + mSelectedNameObject.getName());

                        mGiftReference = mDb.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Names").document(mDocumentReferenceName.getId()).collection("GiftlistArchive");
                        Log.d("Activity persons Giftli", mGiftReference.getPath());

                        setUpRecyclerView();
                        mAdapter_gift_archive.startListening();


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
        mAdapter_gift_archive = new Adapter_Gift_Archive(options);

        RecyclerView recyclerView = findViewById(R.id.activity_personsGiftlistArchive_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter_gift_archive);

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

                if (i == ItemTouchHelper.RIGHT) {
                    mAdapter_gift_archive.unarchiveItem(viewHolder.getAdapterPosition());
                    Toast.makeText( getApplicationContext(), getString(R.string.swipe_unarchived), Toast.LENGTH_SHORT ).show();
                } else if (i == ItemTouchHelper.LEFT) {
                    mAdapter_gift_archive.deleteItem(viewHolder.getAdapterPosition());
                    snackbarUndoDelete();
                }


            }

            private void snackbarUndoDelete() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.coordinatorLayoutPersonsGiftlistArchive), getString(R.string.swipe_deleted), 6000);
                snackbar.setAction(getString(R.string.swipe_deleted_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText( getApplicationContext(),  getString(R.string.snackbar_restored), Toast.LENGTH_LONG ).show();
                        mAdapter_gift_archive.restoreItem();

                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 20;
                ColorDrawable background;
                Drawable icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_archive);

                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + icon.getIntrinsicHeight();

                if (dX > 0) { // Swiping to the right
                    Log.d("Swiping:","Right");

                    //nastaví background a ikonku
                    background = new ColorDrawable(getResources().getColor(R.color.swipeToUnArchive));
                    icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_unarchive_white);

                    //vypočítá pozici pro background
                    background.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                            itemView.getBottom());

                    //vypočítá pozici pro ikonku
                    int iconLeft = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                    int iconRight = itemView.getLeft() + iconMargin;
                    icon.setBounds(iconRight, iconTop, iconLeft, iconBottom);

                    background.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                            itemView.getBottom());

                } else if (dX < 0) { // Swiping to the left
                    Log.d("Swiping:","Left");

                    //nastaví background a ikonku
                    background = new ColorDrawable(getResources().getColor(R.color.swipeToDelete));
                    icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_delete_sweep_white);

                    //vypočítá pozici pro background
                    background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                            itemView.getTop(), itemView.getRight(), itemView.getBottom());

                    //vypočítá pozici pro ikonku
                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                            itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else { // view is unSwiped
                    background = new ColorDrawable(getResources().getColor(R.color.transparent));
                    background.setBounds(0, 0, 0, 0);
                }
                background.draw(c);
                icon.draw(c);


            }

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
        mAdapter_gift_archive.stopListening();
    }



    /**
     * Nastavuje co se stane po kliknutí na checkbox u itemu.
     */
    //TODO: přejmenovat tuto metodu, aby odpovídala svému záměru
    private void setCardsOnClickAction() {
        mAdapter_gift_archive.setOnItemClickListener(new Adapter_Gift_Archive.OnItemClickListener() {
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

    /**
     * Při prvním spuštění aplikace spustí "tutorial", který uživateli popíše základní funkčnost aplikace na této obrazovce.
     * Využívá knihovny taptargetview.
     */
    private void showAtFirstRunOnly(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(Activity_Main.preferences, Context.MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStartActivityPersonsGitflistArchive",true);
        if(firstStart){
            //Drawable icon = getResources().getDrawable(R.drawable.ic_archive);
            TapTargetView.showFor(this,
                    TapTarget.forView(findViewById(R.id.action_bar), getString(R.string.taptarget_giftlist_archive_title), getString(R.string.taptarget_giftlist_archive_desription))
                            .tintTarget(false)
                            .targetCircleColor(R.color.colorPrimaryDark)
                            .icon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_archive))
                    );

            prefs.edit().putBoolean("firstStartActivityPersonsGitflistArchive",false).apply(); //nastaví první spuštění na false - tedy kód uvnitř tohoto ifu se již podruhé neprovede
        }


    }

}
