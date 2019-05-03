package cz.pavelhanzl.giftme;

import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import android.util.Log;

/**
 * Touto třídou se extendují fragmenty použité v Activity_Main. Obsahuje metodu pro nastavení
 * aktivní položky menu.
 *
 * @author Pavel Hanzl
 * @version 1.04
 * @since   03-05-2019
 */
public class LogicDrawerFragment extends Fragment {

    /**
     * Nastaví aktivní ikonu v menu drawer podle předaného indexu.
     * @param index
     */
    public void setActiveMenuIcon(int index) {
        Log.d("Logic_DrawerFragment","Setting " + (index+1) + ". drawer menu icon to active.");
        NavigationView navigationView = ((ActivityMain) getActivity()).mNavigationView;
        navigationView.getMenu().getItem(index).setChecked(true);
    }

}
