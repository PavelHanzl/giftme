package cz.pavelhanzl.giftme.social.gift_tips;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.tabs.TabLayout;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import cz.pavelhanzl.giftme.Activity_Main;
import cz.pavelhanzl.giftme.R;
import cz.pavelhanzl.giftme.giftlist.persons_giftlist.Adapter_Gift_Default;
import cz.pavelhanzl.giftme.social.AddedUser;
import cz.pavelhanzl.giftme.social.gift_tips.others_gift_tips.Fragment_OthersTips;
import cz.pavelhanzl.giftme.social.gift_tips.own_gift_tips.Fragment_OwnTips;

public class Activity_GiftTips extends AppCompatActivity implements Fragment_OwnTips.OnFragmentInteractionListener, Fragment_OthersTips.OnFragmentInteractionListener {
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;
    private CollectionReference mGiftReference;
    private Adapter_Gift_Default mAdapter_gift_default;

    private DocumentSnapshot mDocumentSnapshotName;
    private AddedUser mSelectedNameObject;
    private DocumentReference mDocumentReferenceName;

    public TabLayout mTabLayout;

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

    /**
     * Nastaví viewpager a přiřadí mu PagerAdapter. Zároveň do bundle uloží email uživatele,
     * na kterého jsme kliknuli v Menu->Friends.
     */
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

    /**
     * Přidá taby a nastaví jim text.
     */
    private void setTabLayout() {
        mTabLayout = findViewById(R.id.activity_gifttips_tablayout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.tab_layout_has_on_wishlist));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.others_suggest));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }


    /**
     * Získá objekt ze zvolené položky v předchozí aktivitě.
     * Získávání dat z databáze firestore probíhá asynchronně, a kód této třídy závisí na získaném
     * objektu, proto se zbytek kodu nachází až v onComplete isSuccessful metodě.
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
                        showAtFirstRunOnly();


                    } else {
                        Log.d("Activity_Persons_Giftli", "No such document");
                    }
                } else {
                    Log.d("Activity_Persons_Giftli", "get snapshot failed with ", task.getException());
                }
            }
        });
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    /**
     * Při prvním spuštění aplikace spustí "tutorial", který uživateli popíše základní funkčnost aplikace na této obrazovce.
     * Využívá knihovny taptargetview.
     */
    private void showAtFirstRunOnly(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(Activity_Main.preferences, Context.MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStartActivityGiftTips",true);
        if(firstStart){
            TapTargetView.showFor(this,
                    TapTarget.forView(findViewById(R.id.frag_own_tips_recycler_view), getString(R.string.taptarget_gifttips_title), getString(R.string.taptarget_gifttips_description))
                            .icon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_done_color_primary))
            );

            prefs.edit().putBoolean("firstStartActivityGiftTips",false).apply(); //nastaví první spuštění na false - tedy kód uvnitř tohoto ifu se již podruhé neprovede
        }


    }

}
