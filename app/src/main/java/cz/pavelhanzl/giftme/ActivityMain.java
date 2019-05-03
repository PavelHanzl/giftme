package cz.pavelhanzl.giftme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import cz.pavelhanzl.giftme.giftlist.FragmentGiftlist;
import cz.pavelhanzl.giftme.login_and_signup.ActivityLogin;
import cz.pavelhanzl.giftme.settings.ActivitySettings;
import cz.pavelhanzl.giftme.social.FragmentSocial;
import cz.pavelhanzl.giftme.stats.FragmentStats;
import cz.pavelhanzl.giftme.wishlist.FragmentWishlist;
/**
 * Hlavní aktivita aplikace, spouští se po přihášení do aplikace. V případě že je uživatel již přihlášen,
 * tak je toto defaultní aktivita, kterou uvidí.
 *
 * @author  Pavel Hanzl
 * @version 1.04
 * @since   03-05-2019
 */
public class ActivityMain extends AppCompatActivity {
    //Deklarace členských proměnných
    private DrawerLayout mDrawerLayout;
    private TextView mTextViewUserEmail;
    public NavigationView mNavigationView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    public static final String preferences = "prefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //nastaví toolbar (defaultně je vyplý ve styles.xml)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Link xml a java kódu
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);

        //Získá instanci FirebaseAuth a FirebaseFirestore
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();


        createUserAccountInDatabaseIfDoesNotExists();

        setLoggedInUserInDrawerMenu();


        //přidá ikonku pro vyvolání drawer menu do toolbaru
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState(); //synchronizuje animaci ikonky s polohou drawer menu

        //nastavi fragment na giftlist pri prvnim otevreni aktivity - neni predana zadne savedInstanceState
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentGiftlist()).commit();
            mNavigationView.setCheckedItem(R.id.nav_giftlist);
        }

        //Listenery:
        setActionsForDrawerMenuItems();


    }

    /**
     * Vytvoří v databázi dokument s přihlášeným uživatelem (v podstatě mu založí účet), pokud ještě
     * svůj účet nemá - tzn. pokud používá aplikaci úplně prvně.
     */
    private void createUserAccountInDatabaseIfDoesNotExists() {
        //kontrola, zda-li je zadaný uživatel registrovaný
        DocumentReference userReference = FirebaseFirestore.getInstance().collection("Users").document(mAuth.getCurrentUser().getEmail());
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //pokud zadaný email nalezen tak nic nedělá
                        Log.d("Activity main", "User already exists, do not creating a profile.");
                        return;
                    } else {
                        DocumentReference userReference = FirebaseFirestore.getInstance().collection("Users").document(mAuth.getCurrentUser().getEmail());
                        Map<String, Object> user = new HashMap<>();
                        user.put("exists", true);
                        userReference.set(user);
                        Log.d("Activity main", "User profile created.");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * Nastaví, co se má stát při kliknutí na položky ve vysouvacím menu.
     */
    private void setActionsForDrawerMenuItems() {
        //řídí navigaci
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_giftlist:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentGiftlist()).addToBackStack(null).commit();
                        break;
                    case R.id.nav_wishlist:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentWishlist()).addToBackStack(null).commit();
                        break;
                    case R.id.nav_friends:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentSocial()).addToBackStack(null).commit();
                        break;
                    case R.id.nav_stats:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentStats()).addToBackStack(null).commit();
                        break;
                    case R.id.nav_settings:
                        startActivity(new Intent(getApplicationContext(), ActivitySettings.class));
                        break;
                    case R.id.nav_logout:
                        LogOut();
                        break;
                }

                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    /**
     * Nastaví aktuálně přihlášeného uživatele v headeru vysouvacího navigačního menu,
     */
    private void setLoggedInUserInDrawerMenu() {
        View headerView = mNavigationView.getHeaderView(0);
        mTextViewUserEmail = headerView.findViewById(R.id.nav_textview_user_email);
        mTextViewUserEmail.setText(mAuth.getCurrentUser().getEmail());
    }

    /**
     * Zobrazí alertbox, zda-li se chce uživatel odhlásit.
     * Pokud ano, tak odhlásí uživatele z aplikace a zobrazí login.
     */
    private void LogOut() {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                this);
        builder.setTitle(getString(R.string.activity_main_logout_title));
        builder.setMessage(getString(R.string.activity_main_logout_message));
        builder.setNegativeButton(getString(R.string.frag_others_gifttips_alert_button_no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //nic nedělej
                    }
                });
        builder.setPositiveButton(getString(R.string.frag_others_gifttips_alert_button_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        mAuth.signOut();
                        startActivity(new Intent(getApplicationContext(), ActivityLogin.class));
                        finish();
                    }
                });
        builder.show();


    }

    /**
     * Přepisuje funkci tlačítka back.
     * Pokud je otevřené drawer menu, tak se při stisknutí tlačítka BACK zavře.
     * Pokud není otevřené menu, tak se vrací mezi procházenými fragmenty.
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            Log.i("Giftme Activity main", "popping backstack");
            getSupportFragmentManager().popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

}
