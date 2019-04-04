package cz.pavelhanzl.giftme;

import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import android.util.Log;

/**
 * Touto třídou se extendují fragmenty použité v Activity_Main. Obsahuje metodu pro nastavení
 * aktivní položky menu.
 */
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
