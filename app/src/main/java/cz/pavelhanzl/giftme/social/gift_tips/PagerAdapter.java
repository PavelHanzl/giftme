package cz.pavelhanzl.giftme.social.gift_tips;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import cz.pavelhanzl.giftme.social.gift_tips.others_gift_tips.FragmentOthersTips;
import cz.pavelhanzl.giftme.social.gift_tips.own_gift_tips.FragmentOwnTips;

/**
 * Adapter pro ActivityGiftTips pager.
 *
 * @author Pavel Hanzl
 * @version 1.04
 * @since   03-05-2019
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumberOfTabs;
    private final Bundle mFragmentBundle;

    /**
     * Stará se o stránkování tabview. Předává jednotlivým tabům (fragmentům) argumenty, což je podobné jako když si mezi
     * aktivitami předáváme extras přes intent.
     * @param fm
     * @param numberOfTabs
     * @param data
     */
    public PagerAdapter(FragmentManager fm, int numberOfTabs, Bundle data) {
        super(fm);
        this.mNumberOfTabs=numberOfTabs;
        this.mFragmentBundle = data;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                FragmentOwnTips ownTips = new FragmentOwnTips();
                ownTips.setArguments(mFragmentBundle); // nastavuje argumenty (konkrétně email zvoleného uživatele) předané z Activity_GiftTips pro fragment Fragment_OwnTips
                return ownTips;
            case 1:
                FragmentOthersTips othersTips = new FragmentOthersTips();
                othersTips.setArguments(mFragmentBundle); // nastavuje argumenty (konkrétně email zvoleného uživatele) předané z Activity_GiftTips pro fragment Fragment_OthersTips
                return othersTips;
            default:
                    return null;

        }
    }


    @Override
    public int getCount() {
        return mNumberOfTabs;
    }
}
