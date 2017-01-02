package highway62.reminderapp.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import highway62.reminderapp.constants.Consts;

/**
 * Created by Highway62 on 20/09/2016.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter implements
        ViewPager.OnPageChangeListener {

    private FragmentManager fm;
    private ManualTimeHandler handler;

    public ViewPagerAdapter(FragmentManager fm, ManualTimeHandler handler) {
        super(fm);
        this.fm = fm;
        this.handler = handler;
    }

    @Override
    public Fragment getItem(int position) {
        TimeSelectorFragment frag = TimeSelectorFragment.newInstance(position);
        frag.setHandler(handler);
        return frag;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public int getCount() {
        return Consts.PAGES * Consts.LOOPS;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
