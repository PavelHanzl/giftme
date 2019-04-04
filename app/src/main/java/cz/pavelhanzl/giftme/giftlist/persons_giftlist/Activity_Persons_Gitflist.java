package cz.pavelhanzl.giftme.giftlist.persons_giftlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.View;
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

import cz.pavelhanzl.giftme.Activity_Main;
import cz.pavelhanzl.giftme.giftlist.persons_giftlist_archive.Activity_Persons_Gitflist_Archive;
import cz.pavelhanzl.giftme.giftlist.Name;
import cz.pavelhanzl.giftme.R;

/**
 * Tato třída udává chování aplikace, pokud se uživatel nachází v seznamu dárků vybrané osoby
 * (Menu->Giftlists->Osoba). Ze získaného extras v intentu vytvoří objekt typu Name, se kterým dále
 * pracuje. V databázi najde kolekci Giftlist patřící tomuto uživateli a položky v ní zobrazí pomocí
 * recycleview uživateli.
 */
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
        setContentView(R.layout.activity_persons_gitflist); //nastaví layout aplikace
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white); //nastaví ikonku bílé šipky v actionbaru (nahradí defaultní černou šipku)
        setTitle(getString(R.string.giftlist_title));//nastaví titulek v actionbaru

        mDb = FirebaseFirestore.getInstance(); //získá instantci databáze
        mAuth = FirebaseAuth.getInstance(); //získá instanci ověření

        getDocumentSnapshotForSelectedName();

    }


    /**
     * Získá objekt ze zvolené položky v předchozí aktivitě. Pomocí getString extra získá cestu ke
     * zvolenému dokumentu (v tomto případě cestu ke zvolené osobě). Získávání dat z databáze
     * firestore probíhá asynchronně, a kód této třídy závisí na získaném objektu, proto se
     * zbytek kodu nachází až v onComplete isSuccessful metodě.
     */
    private void getDocumentSnapshotForSelectedName() {
        //provede se pouze tehdy, pokud z předchozí aktivity dostaneme Intent se StringExtra obsahující cestu ke zvolené osobě
        if(getIntent().getStringExtra("path") != null){
            mDocumentReferenceName = mDb.document(getIntent().getStringExtra("path")); //Getting intent, setting DocumentReference
        }

        //získá odkaz na dokument, který je umístěn na cestě získané z předchozí aktivity pomocí StringExtras.
        mDocumentReferenceName.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    mDocumentSnapshotName = task.getResult();
                    if (mDocumentSnapshotName.exists()) {
                        // Focus: Logika po načtení objektu zvoleného jména

                        mSelectedNameObject = mDocumentSnapshotName.toObject(Name.class); //ze zvoleného dokumentu získá objekt typu Name
                        setTitle(getString(R.string.giftlist_title) + " - " + mSelectedNameObject.getName()); //nastaví title v ActionBaru a přidá za něj jméno aktuálně zvolené osoby

                        mGiftReference = mDb.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Names").document(mDocumentReferenceName.getId()).collection("Giftlist");

                        showAtFirstRunOnly(); //spustí tutorial pomocí tap target view při prvním spuštění této aktivity
                        setUpFloatingButtons(); //nastaví floating buton pro přidání nového dárku a zobrazení archivu
                        setUpRecyclerView(); //provede nastavení recycleview

                        mAdapter_gift_default.startListening();

                    } else {
                        //dokument v zadané cestě neexistuje
                        Log.d("Activity_Persons_Giftli", "No such document");
                    }
                } else {
                    //načtení dokumentu na zadané cestě selhalo s uvedeným errorem
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
     * Odstraní položku z recyclerView při posunutí položky doprava (archivuje) nebo doleva (odstraní).
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
                    mAdapter_gift_default.archiveItem(viewHolder.getAdapterPosition());
                    Toast.makeText( getApplicationContext(), getString(R.string.swipe_archived), Toast.LENGTH_SHORT ).show();
                } else if (i == ItemTouchHelper.LEFT) {
                    mAdapter_gift_default.deleteItem(viewHolder.getAdapterPosition());
                    snackbarUndoDelete();
                }


            }

            /**
             * Zobrazí snackbar s možností vrátit smazání položky.
             */
            private void snackbarUndoDelete() {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.coordinatorLayoutPersonsGiftlist), getString(R.string.swipe_deleted), 6000);
                snackbar.setAction(getString(R.string.swipe_deleted_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText( getApplicationContext(),  getString(R.string.snackbar_restored), Toast.LENGTH_LONG ).show();
                        mAdapter_gift_default.restoreItem();

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
                    background = new ColorDrawable(getResources().getColor(R.color.swipeToArchive));
                    icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_archive);

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
                } else { // s view se momentálně neposouvá
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
        mAdapter_gift_default.stopListening();
    }

    /**
     * Nastaví floating button pro přidání dárku na giftlist. A v extra odešle ID otevřené osoby,
     * pro kterou je určen otevřený giftlist. Zároveň nastaví floating button pro otevření archivu vybrané
     * osoby.
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



    /**
     * Při prvním spuštění aplikace spustí "tutorial", který uživateli popíše základní funkčnost aplikace na této obrazovce.
     * Využívá knihovny taptargetview.
     */
    private void showAtFirstRunOnly(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(Activity_Main.preferences, Context.MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStartActivityPersonsGitflist",true);
        if(firstStart){
            new TapTargetSequence(this)
                    .targets(
                            TapTarget.forView(findViewById(R.id.activity_personsGiftlist_floatingButton_add_gift), getString(R.string.taptarget_persons_giftlist_addbutton_title) + " " + mSelectedNameObject.getName(), getString(R.string.taptarget_persons_giftlist_addbutton_desription)).tintTarget(false).cancelable(false),
                            TapTarget.forView(findViewById(R.id.activity_personsGiftlist_floatingButton_show_archive), getString(R.string.taptarget_persons_giftlist_archivebutton_title), getString(R.string.taptarget_persons_giftlist_archivebutton_desription)).tintTarget(false))
                            .start();


            prefs.edit().putBoolean("firstStartActivityPersonsGitflist",false).apply(); //nastaví první spuštění na false - tedy kód uvnitř tohoto ifu se již podruhé neprovede
        }


    }

}
