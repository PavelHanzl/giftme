package cz.pavelhanzl.giftme.social.gift_tips;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

import cz.pavelhanzl.giftme.R;
import cz.pavelhanzl.giftme.giftlist.Name;
import cz.pavelhanzl.giftme.giftlist.persons_giftlist.Activity_NewGift;
import cz.pavelhanzl.giftme.giftlist.persons_giftlist.Adapter_Gift_Default;
import cz.pavelhanzl.giftme.giftlist.persons_giftlist.Gift;
import cz.pavelhanzl.giftme.giftlist.persons_giftlist_archive.Activity_Persons_Gitflist_Archive;
import cz.pavelhanzl.giftme.social.AddedUser;

public class Activity_GiftTips extends AppCompatActivity implements Fragment_OwnTips.OnFragmentInteractionListener,Fragment_OthersTips.OnFragmentInteractionListener {
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;
    private CollectionReference mGiftReference;
    private Adapter_Gift_Default mAdapter_gift_default;

    private DocumentSnapshot mDocumentSnapshotName;
    private AddedUser mSelectedNameObject;
    private DocumentReference mDocumentReferenceName;

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Persons_Giftlist","Processing oncreate");
        setContentView(R.layout.activity_gifttips);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white); //nastaví ikonku bílé šipky v actionbaru (nahradí defaultní černou šipku)
        setTitle(getString(R.string.gifttips_title));


        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        setTabLayout();
        getDocumentSnapshotForSelectedName();

    }

    private void setViewPager() {

        //vytvoří bundle který obsahuje email zvoleného uživatele v social části a následně předá jeho email do fragmentů "Fragment_OwnTips" nebo "Fragment_OthersTips"
        Bundle bundle = new Bundle();
        bundle.putString("selectedEmail", mSelectedNameObject.getEmail());

        final ViewPager viewPager = findViewById(R.id.activity_gifttips_viewpager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(),mTabLayout.getTabCount(),bundle);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setTabLayout() {
        mTabLayout = findViewById(R.id.activity_gifttips_tablayout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.tab_layout_has_on_wishlist));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.others_suggest));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
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

                        mSelectedNameObject = mDocumentSnapshotName.toObject(AddedUser.class);
                        setTitle(getString(R.string.gifttips_title) + " for " + mSelectedNameObject.getName());
                        Log.d("Activity_Gifttips", " mSelectedNameObject " + mSelectedNameObject.getEmail());


                        setViewPager();



//                        mGiftReference = mDb.collection("Users").document(mAuth.getCurrentUser().getEmail()).collection("Names").document(mDocumentReferenceName.getId()).collection("Giftlist");
//                        Log.d("Activity persons Giftli", mGiftReference.getPath());
//
//                        setUpFloatingButtons();
//                        setUpRecyclerView();
//                        mAdapter_gift_default.startListening();


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
        //mAdapter_gift_default.stopListening();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
