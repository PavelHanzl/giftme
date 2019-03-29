package cz.pavelhanzl.giftme.social.gift_tips;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumberOfTabs;
    private final Bundle mFragmentBundle;

    public PagerAdapter(FragmentManager fm, int numberOfTabs, Bundle data) {
        super(fm);
        this.mNumberOfTabs=numberOfTabs;
        this.mFragmentBundle = data;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Fragment_OwnTips ownTips = new Fragment_OwnTips();
                ownTips.setArguments(mFragmentBundle); // nastavuje argumenty (konkrétně email zvoleného uživatele) předané z Activity_GiftTips pro fragment Fragment_OwnTips
                return ownTips;
            case 1:
                Fragment_OthersTips othersTips = new Fragment_OthersTips();
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
