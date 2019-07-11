package kim.jeonghyeon.androidlibrary.ui.binder.recyclerview;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by hyun.kim on 27/12/17.
 * <p>
 * refer to {{@link BaseRecyclerViewAdapter}}
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class BaseRecyclerViewHolder<VM> extends RecyclerView.ViewHolder {
    @NonNull
    private final ViewDataBinding mBinding;

    public BaseRecyclerViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        mBinding = binding;

    }

    public void setOnClickListener(@Nullable View.OnClickListener onClickListener) {
        if (onClickListener != null) {
            itemView.setOnClickListener(onClickListener);
        }
    }

    public void bind(VM viewModel) {
        mBinding.setVariable(getViewModelId(), viewModel);
        mBinding.executePendingBindings();
    }

    /**
     * if viewModel name is not "viewModel", override this method to set the other view model name
     *
     * @return viewModel Id
     */
    protected abstract int getViewModelId();
}
