package kim.jeonghyeon.androidlibrary.ui.binder.recyclerview;

import androidx.annotation.NonNull;
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
    private VM viewModel;

    public BaseRecyclerViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        mBinding = binding;

    }

    public void bind(VM viewModel) {
        this.viewModel = viewModel;
        mBinding.setVariable(getViewModelId(), viewModel);
        mBinding.executePendingBindings();
    }

    public VM getViewModel() {
        return viewModel;
    }

    /**
     * if viewModel name is not "model", override this method to set the other view model name
     *
     * @return viewModel Id
     */
    protected abstract int getViewModelId();
}
