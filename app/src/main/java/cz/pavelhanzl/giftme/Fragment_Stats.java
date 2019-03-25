package cz.pavelhanzl.giftme;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Fragment_Stats extends Logic_DrawerFragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private View mView;
    private TextView mTextViewTest;
    private int mOverallBudget = 0;

    private TextView txtTimerDay, txtTimerHour, txtTimerMinute, txtTimerSecond;
    private TextView tvEvent;
    private Handler handler;
    private Runnable runnable;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setActiveMenuIcon(2);
        mView = inflater.inflate(R.layout.fragment_stats, container, false);

        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mTextViewTest = mView.findViewById(R.id.fragment_stats_TextView_1);
        setTextviews();


        txtTimerDay = mView.findViewById(R.id.txtTimerDay);
        txtTimerHour = mView.findViewById(R.id.txtTimerHour);
        txtTimerMinute = mView.findViewById(R.id.txtTimerMinute);
        txtTimerSecond = mView.findViewById(R.id.txtTimerSecond);
        tvEvent = mView.findViewById(R.id.eventIsActiveTitle);

        countDownStart();


//        CollectionReference aca = mDb.collection("/Users/"+mAuth.getCurrentUser().getEmail()+"/Names/4OXDLAgSWSxnpFitvLOW/Giftlist");
//        aca.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                int price = 0;
//                int numberOfPollAnswers = 0;
//                int pollAnswerSize = task.getResult().size();
//                for (DocumentSnapshot answer : task.getResult()) {
//                    if((boolean)answer.get("bought")){
//                    numberOfPollAnswers++;
//                    price += ((Long) answer.get("price")).intValue();}
//                }
//                Log.v("NUMBER OF POLL BOUGHT ", String.valueOf(numberOfPollAnswers));
//                Log.v("SIZE", String.valueOf(pollAnswerSize));
//                Log.v("price", String.valueOf(price));
//
//            }});


        return mView;
    }


    public void countDownStart() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd");
                    // Please here set your event date//YYYY-MM-DD
                    Date futureDate = dateFormat.parse("2019-12-25");
                    Date currentDate = new Date();
                    if (!currentDate.after(futureDate)) {
                        long diff = futureDate.getTime()
                                - currentDate.getTime();
                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;
                        txtTimerDay.setText("" + String.format("%02d", days));
                        txtTimerHour.setText("" + String.format("%02d", hours));
                        txtTimerMinute.setText(""
                                + String.format("%02d", minutes));
                        txtTimerSecond.setText(""
                                + String.format("%02d", seconds));
                    } else {
                        tvEvent.setVisibility(View.VISIBLE);
                        tvEvent.setText("The event started!");
                        textViewGone();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }
    public void textViewGone() {
        mView.findViewById(R.id.LinearLayout10).setVisibility(View.GONE);
        mView.findViewById(R.id.LinearLayout11).setVisibility(View.GONE);
        mView.findViewById(R.id.LinearLayout12).setVisibility(View.GONE);
        mView.findViewById(R.id.LinearLayout13).setVisibility(View.GONE);

    }


    private void setTextviews() {
        mTextViewTest.setText(String.valueOf(StatsManagerSingleton.getInstance().getOverallBudget()));
    }

    @Override
    public void onResume() {

        super.onResume();
        setActiveMenuIcon(2);
    }

}
