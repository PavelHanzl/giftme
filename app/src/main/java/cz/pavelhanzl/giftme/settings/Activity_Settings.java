package cz.pavelhanzl.giftme.settings;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import cz.pavelhanzl.giftme.R;

public class Activity_Settings extends AppCompatActivity {
    private CardView mCardViewChangePassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white); //nastaví ikonku křížku v actionbaru (nahradí defaultní šipku)
        setTitle(R.string.activity_settings_title); //nastaví titulek

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.framelayout_settings_for_fragment, new SettingsFragment())
                .commit(); //zavede fragment do layoutu


    }


}
