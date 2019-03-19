package cz.pavelhanzl.giftme;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.LoginFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Activity_Main extends AppCompatActivity {
    //Deklarace členských proměnných
    private DrawerLayout mDrawerLayout;
    private String mStringUserEmail;
    private TextView mTextViewUserEmail;
    private NavigationView mNavigationView;
    private FirebaseAuth mAuth;
    private Intent mIntentLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__main);

        //nastaví toolbar (defaultně je vyplý ve styles.xml)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Link xml a java kódu
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);

        //nastaví Intents
        mIntentLogin = new Intent(this, Activity_Login.class);

        //Získá instanci FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        setLoggedInUserInDrawerMenu();


        //přidá ikonku pro vyvolání drawer menu do toolbaru
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState(); //synchronizuje animaci ikonky s polohou drawer menu

        //nastavi fragment na giftlist pri prvnim otevreni aktivity - neni predana zadne savedInstanceState
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment_Giftlist()).commit();
            mNavigationView.setCheckedItem(R.id.nav_giftlist);
        }

        //Listenery:
        setActionsForDrawerMenuItems();


    }

    private void setActionsForDrawerMenuItems() {
        //řídí navigaci
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_giftlist:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment_Giftlist()).addToBackStack(null).commit();
                        break;
                    case R.id.nav_groups:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment_Groups()).addToBackStack(null).commit();
                        break;
                    case R.id.nav_stats:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment_Stats()).addToBackStack(null).commit();
                        break;
                    case R.id.nav_settings:
                        //TODO: dodělat settings
                        Toast.makeText(Activity_Main.this, "settings pressed", Toast.LENGTH_SHORT).show();
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

    private void setLoggedInUserInDrawerMenu() {
        View headerView = mNavigationView.getHeaderView(0);
        mTextViewUserEmail = headerView.findViewById(R.id.nav_textview_user_email);
        mTextViewUserEmail.setText(mAuth.getCurrentUser().getEmail());
    }

    /**
     * Odhlásí uživatele z aplikace a zobrazí login
     */
    private void LogOut() {
        //TODO: dodělat "opravdu odhlásit?"
        mAuth.signOut();
        startActivity(mIntentLogin);
        finish();
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
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            Log.i("Giftme Activity main", "popping backstack");
            getSupportFragmentManager().popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }
}
