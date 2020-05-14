package kim.jeonghyeon.sample.etc.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kim.jeonghyeon.sample.R
import kotlinx.android.synthetic.main.swipe_refresh_layout.*

class SwipeRefreshLayoutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.swipe_refresh_layout, container, false)
        lyt_swipe.isRefreshing = false//show refresh view or not
        return inflate
    }
}