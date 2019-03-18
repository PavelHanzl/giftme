package cz.pavelhanzl.giftme;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.TextView;

public class Activity_Main extends AppCompatActivity {
    //Deklarace členských proměnných
    private DrawerLayout mDrawerLayout;
    private String mStringUserEmail;
    private TextView mTextViewUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__main);

        //nastaví toolbar (defaultně je vyplý ve styles.xml)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Link xml a java kódu
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mTextViewUserEmail = findViewById(R.id.nav_textview_user_email);

        //přidá ikonku pro vyvolání drawer menu do toolbaru
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState(); //synchronizuje animaci ikonky s polohou drawer menu

        //nastaví jméno uživatele v drawer menu

    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else {
        super.onBackPressed();}
    }
}
