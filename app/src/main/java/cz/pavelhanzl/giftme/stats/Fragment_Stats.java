package cz.pavelhanzl.giftme.stats;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.pavelhanzl.giftme.Logic_DrawerFragment;
import cz.pavelhanzl.giftme.R;

public class Fragment_Stats extends Logic_DrawerFragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private View mView;
    private TextView mTextViewValueOfBoughtGifts,mTextViewValueOfBoughtGifts2,mTextViewValueOfUnoughtGifts,mTextViewValueOfAllGifts,mTextViewCountOfBoughtGifts,mTextViewCountOfUnoughtGifts,mTextViewCountOfAllGifts,mTextViewSumOfAllPersonsBudgets,mTextViewNumberOfPersons;
    private StatsManagerSingleton stats = StatsManagerSingleton.getInstance();

    private PieChart mPieChartValueOfGifts, mPieChartCountOfGifts;
    private HorizontalBarChart mHorizontalBarChartBudgetsInfo;

    private TextView txtTimerDay, txtTimerHour, txtTimerMinute, txtTimerSecond, tvEvent;
    private Handler handler;
    private Runnable runnable;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setActiveMenuIcon(3);
        mView = inflater.inflate(R.layout.fragment_stats, container, false);

        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //link xml a java kódu
        mTextViewValueOfBoughtGifts = mView.findViewById(R.id.fragment_stats_TextView_Value_of_bought_gifts);
        mTextViewValueOfBoughtGifts2 = mView.findViewById(R.id.fragment_stats_TextView_Value_of_bought_gifts2);
        mTextViewValueOfUnoughtGifts = mView.findViewById(R.id.fragment_stats_TextView_Value_of_unbought_gifts);
        mTextViewValueOfAllGifts = mView.findViewById(R.id.fragment_stats_TextView_Value_of_all_gifts);

        mTextViewCountOfBoughtGifts = mView.findViewById(R.id.fragment_stats_TextView_Count_of_bought_gifts);
        mTextViewCountOfUnoughtGifts = mView.findViewById(R.id.fragment_stats_TextView_Count_of_unbought_gifts);
        mTextViewCountOfAllGifts = mView.findViewById(R.id.fragment_stats_TextView_Count_of_all_gifts);

        mTextViewSumOfAllPersonsBudgets = mView.findViewById(R.id.fragment_stats_TextView_Sum_of_all_persons_budgets);
        mTextViewNumberOfPersons = mView.findViewById(R.id.fragment_stats_TextView_Number_of_persons);

        mPieChartValueOfGifts = mView.findViewById(R.id.fragment_stats_PieChart_valueOfGifts);
        mPieChartCountOfGifts = mView.findViewById(R.id.fragment_stats_PieChart_countOfGifts);
        mHorizontalBarChartBudgetsInfo = mView.findViewById(R.id.fragment_stats_HorizontalBarChart_budgetsInfo);

        txtTimerDay = mView.findViewById(R.id.txtTimerDay);
        txtTimerHour = mView.findViewById(R.id.txtTimerHour);
        txtTimerMinute = mView.findViewById(R.id.txtTimerMinute);
        txtTimerSecond = mView.findViewById(R.id.txtTimerSecond);
        tvEvent = mView.findViewById(R.id.eventIsActiveTitle);

        countDownStart();
        setTextViews();
        setGraphs();



        return mView;
    }

    private void setGraphs() {
        setPieChartValueOfGifts();
        setPieChartCountOfGifts();
        setHorizontalBarChartBudgetsInfo();
    }

    private void setHorizontalBarChartBudgetsInfo() {
        Toast.makeText(getContext(), "setting bar chart", Toast.LENGTH_SHORT).show();
        // arraylist pro hodnoty sloupců
        ArrayList<BarEntry> yValues = new ArrayList<>();

        float barWidth = 9f;
        float spaceForBar = 10f;
        //přidá hodnoty do sloupců
        yValues.add(new BarEntry(2f, stats.getValueOfBoughtGifts()));
        yValues.add(new BarEntry(1f,  stats.getSumOfAllPersonsBudgets()));
        Log.d("horizontal", "1:" + stats.getValueOfBoughtGifts());
        Log.d("horizontal", "2:" + yValues.get(0));

        BarDataSet barDataSet = new BarDataSet(yValues," ");
        BarData barData = new BarData(barDataSet);


        //vykresluje hodnoty do barů
        mHorizontalBarChartBudgetsInfo.setDrawValueAboveBar(false);

        // skyje Y gridlines
        mHorizontalBarChartBudgetsInfo.getAxisLeft().setEnabled(false);
        // skyje X osu
        mHorizontalBarChartBudgetsInfo.getXAxis().setEnabled(false);

       // skryje popis
        mHorizontalBarChartBudgetsInfo.getDescription().setEnabled(false);

       // skryje legendu
       mHorizontalBarChartBudgetsInfo.getLegend().setEnabled(false);

       //nastaví minimální hodnotu na 0 - zábrání autozoomu
        mHorizontalBarChartBudgetsInfo.getAxisLeft().setAxisMinimum(0f);

       // Design
       barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
       barDataSet.setDrawValues(true);
       barData.setValueTextSize(10f);
       barData.setValueTextColor(getResources().getColor(R.color.white80transparent));

       mHorizontalBarChartBudgetsInfo.setData(barData);
       mHorizontalBarChartBudgetsInfo.setFitBars(true);
       mHorizontalBarChartBudgetsInfo.invalidate();

    }

    private void setPieChartValueOfGifts() {
        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(stats.getValueOfBoughtGifts(), "Bought"));
        entries.add(new PieEntry(stats.getValueOfUnboughtGifts(), "Unbought"));


        PieDataSet pieDataSet = new PieDataSet(entries, "Un/bought stats");
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        pieDataSet.setValueTextColor(getResources().getColor(R.color.white80transparent));
        pieDataSet.setValueTextSize(20);

        PieData data = new PieData(pieDataSet);
        mPieChartValueOfGifts.setData(data);
        mPieChartValueOfGifts.animateY(1000, Easing.EaseInOutCubic);
        mPieChartValueOfGifts.getDescription().setEnabled(false);
        mPieChartValueOfGifts.getLegend().setEnabled(false);
        mPieChartValueOfGifts.setCenterText(getString(R.string.value_of_all_gifts_pie_chart_label)+"\n"+stats.getValueOfAllGifts());

        mPieChartValueOfGifts.invalidate(); // refresh
    }


    private void setPieChartCountOfGifts() {
        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(stats.getCountOfBoughtGifts(), "Bought"));
        entries.add(new PieEntry(stats.getCountOfUnBoughtGifts(), "Unbought"));


        PieDataSet pieDataSet = new PieDataSet(entries, "Un/bought stats");
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        pieDataSet.setValueTextColor(getResources().getColor(R.color.white80transparent));
        pieDataSet.setValueTextSize(20);

        PieData data = new PieData(pieDataSet);
        data.setValueFormatter(new DecimalFormater(0));//odstraní desetinná místa
        mPieChartCountOfGifts.setUsePercentValues(false);

        mPieChartCountOfGifts.setData(data);
        mPieChartCountOfGifts.animateY(1000, Easing.EaseInOutCubic);
        mPieChartCountOfGifts.getDescription().setEnabled(false);
        mPieChartCountOfGifts.getLegend().setEnabled(false);
        mPieChartCountOfGifts.setCenterText(getString(R.string.count_of_all_gifts_pie_chart_label)+"\n"+stats.getCountOfAllGifts());


        mPieChartCountOfGifts.invalidate(); // refresh
    }


    public void countDownStart() {
        handler = new Handler();
        runnable = new Runnable() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    Date futureDate = dateFormat.parse(Calendar.getInstance().get(Calendar.YEAR) + "-12-25");
                    Date currentDate = new Date();
                    //pokud ještě nebyly Vánoce a pokud nenastal nový rok, tak odpočítávej. Po přechodu na nový rok se spustí nové odpočítávání.
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

                        //formát kdy jsou zobrazena minimálně 2 decimální čísla, pokud je jich míň tak se doplní nulama
                        txtTimerDay.setText("" + String.format("%02d", days));
                        txtTimerHour.setText("" + String.format("%02d", hours));
                        txtTimerMinute.setText("" + String.format("%02d", minutes));
                        txtTimerSecond.setText("" + String.format("%02d", seconds));
                    } else {
                        tvEvent.setVisibility(View.VISIBLE);
                        tvEvent.setText(R.string.eventIsActiveTitle);
                        textViewsCountdownGone();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }
    public void textViewsCountdownGone() {
        mView.findViewById(R.id.LinearLayout10).setVisibility(View.GONE);
        mView.findViewById(R.id.LinearLayout11).setVisibility(View.GONE);
        mView.findViewById(R.id.LinearLayout12).setVisibility(View.GONE);
        mView.findViewById(R.id.LinearLayout13).setVisibility(View.GONE);

    }


    private void setTextViews() {
        mTextViewValueOfBoughtGifts.setText(String.valueOf(stats.getValueOfBoughtGifts()));
        mTextViewValueOfBoughtGifts2.setText(String.valueOf(stats.getValueOfBoughtGifts()));
        mTextViewValueOfUnoughtGifts.setText(String.valueOf(stats.getValueOfUnboughtGifts()));
        mTextViewValueOfAllGifts.setText(String.valueOf(stats.getValueOfAllGifts()));

        mTextViewCountOfBoughtGifts.setText(String.valueOf(stats.getCountOfBoughtGifts()));
        mTextViewCountOfUnoughtGifts.setText(String.valueOf(stats.getCountOfUnBoughtGifts()));
        mTextViewCountOfAllGifts.setText(String.valueOf(stats.getCountOfAllGifts()));

        mTextViewSumOfAllPersonsBudgets.setText(String.valueOf(stats.getSumOfAllPersonsBudgets()));
        mTextViewNumberOfPersons.setText(String.valueOf(stats.getNumberOfPersons()));

    }

    @Override
    public void onResume() {

        super.onResume();
        setActiveMenuIcon(3);
    }

}
