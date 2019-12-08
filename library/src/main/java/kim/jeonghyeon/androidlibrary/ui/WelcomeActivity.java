package kim.jeonghyeon.androidlibrary.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import kim.jeonghyeon.androidlibrary.BaseApplication;
import kim.jeonghyeon.androidlibrary.R;
import kim.jeonghyeon.androidlibrary.deprecated.mvp.EmptyActivity;

import static kim.jeonghyeon.androidlibrary.extension.GlobalFunctionsKt.isFromVersion;

/**
 * layout lists
 */
@SuppressWarnings("unused")
abstract public class WelcomeActivity extends EmptyActivity {

    private int[] layouts;
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private Button btnSkip, btnNext;
    //	viewpager change listener
    private final ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }

            if (getListener() != null) {
                getListener().onPageSelected(position);
            }
        }

        @SuppressWarnings("EmptyMethod")
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @SuppressWarnings("EmptyMethod")
        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @NonNull
    abstract protected int[] getPageLayoutIds();

    protected Button getBtnSkip() {
        return btnSkip;
    }

    protected Button getBtnNext() {
        return btnNext;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    @Nullable
    abstract public Listener getListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layouts = getPageLayoutIds();
        // Making notification bar transparent
        if (isFromVersion(Build.VERSION_CODES.LOLLIPOP)) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);


        // layouts of all welcome sliders
        // add few more layouts if you want

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(v -> {
            if (getListener() != null) {
                getListener().onSkip();
            }
        });

        btnNext.setOnClickListener(v -> {
            // checking for last page
            // if last page home screen will be launched
            int prev = viewPager.getCurrentItem();
            int next = prev + 1;

            if (next < layouts.length) {
                // move to next screen

                if (getListener() != null) {
                    if (!getListener().onNext(prev, next)) {
                        return;
                    }
                }

                viewPager.setCurrentItem(next);
            } else {
                if (getListener() != null) {
                    getListener().onCompleted();
                }
            }
        });
    }

    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[layouts.length];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(ContextCompat.getColor(BaseApplication.getInstance(), R.color.dot_inactive));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(ContextCompat.getColor(BaseApplication.getInstance(), R.color.dot_active));
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    /**
     * Making notification bar transparent
     */
    @SuppressLint("ObsoleteSdkInt")
    private void changeStatusBarColor() {
        if (isFromVersion(Build.VERSION_CODES.LOLLIPOP)) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public interface Listener {
        void onSkip();

        /**
         * @return true, move to next
         */
        boolean onNext(int prevIndex, int nextIndex);

        void onPageSelected(int index);

        void onCompleted();

        void onInstantiateItem(int position, View itemView);
    }

    /**
     * View pager adapter
     */
    class MyViewPagerAdapter extends PagerAdapter {
        @Nullable
        private LayoutInflater layoutInflater;

        MyViewPagerAdapter() {
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            assert layoutInflater != null;
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            if (getListener() != null) {
                getListener().onInstantiateItem(position, view);
            }

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
