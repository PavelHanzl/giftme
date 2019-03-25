package cz.pavelhanzl.giftme;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class StatsManagerSingleton {

    private static StatsManagerSingleton INSTANCE = null;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private int mOverallBudget = 0;

    private StatsManagerSingleton() {
    }

    ;

    public static StatsManagerSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StatsManagerSingleton();
        }
        return (INSTANCE);
    }

    public int getOverallBudget() {
        return mOverallBudget;
    }

    // other instance methods can follow
    public void getStatsData() {
        mOverallBudget = 0;


        Log.d("Statsmanager", "Retrieving stats data.");
        CollectionReference all = mDb.collection("/Users/" + mAuth.getCurrentUser().getEmail() + "/Names");

        all.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot name : task.getResult()) {
                    Log.v("User", name.get("name").toString());
                    CollectionReference personsGiftlist = mDb.collection("/Users/" + mAuth.getCurrentUser().getEmail() + "/Names/" + name.getId() + "/Giftlist");
                    personsGiftlist.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot gift : task.getResult()) {
                                Log.v("Dárek", gift.get("name").toString());
                                mOverallBudget += ((Long) gift.get("price")).intValue();

                            }

                        }
                    });
                }
                Log.v("Budget", String.valueOf(mOverallBudget));

            }
        });


    }

}
