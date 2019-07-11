package kim.jeonghyeon.androidlibrary.architecture

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

abstract class BasePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    var currentFragment: Fragment? = null


    override fun setPrimaryItem(container: ViewGroup, position: Int, nextFragment: Any) {
        if (nextFragment is BaseFragment) nextFragment.selected = true

        val currentFragment = currentFragment
        if (currentFragment !== nextFragment) {
            if (currentFragment is BaseFragment) currentFragment.selected = false
        }

        this.currentFragment = nextFragment as Fragment

        super.setPrimaryItem(container, position, nextFragment)
    }
}