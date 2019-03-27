package cz.pavelhanzl.giftme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;

public class Logic_DrawerFragment extends Fragment {

    /**
     * Nastaví aktivní ikonu v menu drawer podle předaného indexu.
     * @param index
     */
    public void setActiveMenuIcon(int index) {
        Log.d("Logic_DrawerFragment","Setting " + (index+1) + ". drawer menu icon to active.");
        NavigationView navigationView = ((Activity_Main) getActivity()).mNavigationView;
        navigationView.getMenu().getItem(index).setChecked(true);
    }

}
