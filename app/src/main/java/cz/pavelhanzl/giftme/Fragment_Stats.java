package cz.pavelhanzl.giftme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_Stats extends Logic_DrawerFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setActiveMenuIcon(2);
        return inflater.inflate(R.layout.fragment_stats,container,false);
    }



    @Override
    public void onResume() {

        super.onResume();
        setActiveMenuIcon(2);
    }

}
