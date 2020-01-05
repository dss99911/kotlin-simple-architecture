package kim.jeonghyeon.androidlibrary.ui.binder.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kim.jeonghyeon.androidlibrary.BR


class NoScrollListView : LinearLayout {
    constructor(context: Context) : super(context) {
        orientation = VERTICAL
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        orientation = VERTICAL
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        orientation = VERTICAL
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        orientation = VERTICAL
    }

    /**
     * if viewModel Id is different. override this
     */
    protected val viewModelId: Int
        get() = BR.model

    fun setItemListAndItemLayoutId(list: List<*>, @LayoutRes layoutId: Int) {
        removeAllViews()

        list.forEachIndexed { index, _ ->
            val binding = createBinding(layoutId)
            binding.setVariable(viewModelId, list[index])
            binding.executePendingBindings()
            addView(binding.root)
        }
    }

    fun createBinding(@LayoutRes layoutId: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, this, false)
    }
}
