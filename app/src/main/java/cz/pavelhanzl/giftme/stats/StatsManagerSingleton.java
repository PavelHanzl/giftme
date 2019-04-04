package cz.pavelhanzl.giftme.stats;

import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Tato třída umožnuje vytvořit současně pouze jednu instanci (Singleton), získává potřebná data pro
 * statistiky a uchovává je v sobě pomocí členských proměnných, přístup k nim je možný z celé aplikace
 * pomocí getterů.
 */
public class StatsManagerSingleton {

    private static StatsManagerSingleton INSTANCE = null;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private int mValueOfBoughtGifts = 0;
    private int mValueOfUnboughtGifts = 0;
    private int mValueOfAllGifts;

    private int mCountOfBoughtGifts =0;
    private int mCountOfUnBoughtGifts =0;
    private int mCountOfAllGifts;

    private int mSumOfAllPersonsBudgets =0;
    private int mNumberOfPersons = 0;

    private StatsManagerSingleton() {
    }

    ;

    /**
     * Získá instanci tohoto singletonu.
     * @return
     */
    public static StatsManagerSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StatsManagerSingleton();
        }
        return (INSTANCE);
    }

    public int getValueOfBoughtGifts() {
        return mValueOfBoughtGifts;
    }

    public int getValueOfUnboughtGifts() {
        return mValueOfUnboughtGifts;
    }

    public int getValueOfAllGifts() {
        return mValueOfBoughtGifts+mValueOfUnboughtGifts;
    }

    public int getCountOfBoughtGifts() {
        return mCountOfBoughtGifts;
    }

    public int getCountOfUnBoughtGifts() {
        return mCountOfUnBoughtGifts;
    }

    public int getCountOfAllGifts() {
        return mCountOfBoughtGifts+mCountOfUnBoughtGifts;
    }

    public int getSumOfAllPersonsBudgets() {
        return mSumOfAllPersonsBudgets;
    }

    public int getNumberOfPersons() {
        return mNumberOfPersons;
    }

    /**
     * Projde seznam giftlistů, které si uživatel vytvořil v Menu->Giftlists. A načte všechna potřebná
     * data pro Fragment_Stats.
     */
    public void getStatsData() {
        //vynulování členských proměnných
        mValueOfBoughtGifts=mValueOfUnboughtGifts=mValueOfAllGifts=mCountOfBoughtGifts=mCountOfUnBoughtGifts=mCountOfAllGifts=mSumOfAllPersonsBudgets=mNumberOfPersons=0;


        Log.d("Statsmanager", "Retrieving collection of users persons.");
        CollectionReference all = mDb.collection("/Users/" + mAuth.getCurrentUser().getEmail() + "/Names");

        all.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //prochází všechna jména v uživatelově giftlistu
                    for (DocumentSnapshot name : task.getResult()) {
                        //Log.v("User", name.get("name").toString());
                        mNumberOfPersons++;
                        mSumOfAllPersonsBudgets += ((Long) name.get("budget")).intValue();

                        CollectionReference personsGiftlist = mDb.collection("/Users/" + mAuth.getCurrentUser().getEmail() + "/Names/" + name.getId() + "/Giftlist");
                        personsGiftlist.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                //prochází všechny dárky u jednotlivých jmen v giftlistu
                                for (DocumentSnapshot gift : task.getResult()) {
                                    //Pokud je dárek koupen
                                    if((boolean) gift.get("bought")){
                                        //Log.v("Koupený dárek", gift.get("name").toString());
                                        mCountOfBoughtGifts++;
                                        mValueOfBoughtGifts += ((Long) gift.get("price")).intValue();
                                    }else {//Pokud dárek není koupen
                                        mCountOfUnBoughtGifts++;
                                        mValueOfUnboughtGifts += ((Long) gift.get("price")).intValue();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

    }

}
