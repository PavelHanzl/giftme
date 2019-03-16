package cz.pavelhanzl.giftme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Activity_Signup extends AppCompatActivity {
    Button mButtonBack, mButtonCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mButtonBack = findViewById(R.id.button_back);
        mButtonCreateAccount = findViewById(R.id.button_create_account);

        //ukončí aktivitu Signup a vrátí se k předchozí
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
