package kim.jeonghyeon.androidlibrary.ui.binder.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kim.jeonghyeon.androidlibrary.BR;
import kim.jeonghyeon.androidlibrary.ui.binder.RecyclerViewBinder;

/**
 * Created by hyun.kim on 27/12/17.
 * <p>
 * This is for using RecyclerView without implementing RecyclerViewAdapter by developer.
 * refer to {{@link RecyclerViewBinder#bindRecyclerView(RecyclerView, BaseRecyclerViewModel, int)}}
 */
@SuppressWarnings("unused")
public class BaseRecyclerViewAdapter<VM> extends RecyclerView.Adapter<BaseRecyclerViewHolder<VM>> {

    @NonNull
    private final List<VM> viewModelList;
    private final int layoutId;
    private OnItemClickListener<VM> onItemClickListener;

    public BaseRecyclerViewAdapter(@NonNull List<VM> viewModelList, int layoutId) {
        this.viewModelList = viewModelList;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public BaseRecyclerViewHolder<VM> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, layoutId, parent, false);

        final BaseRecyclerViewHolder<VM> simpleViewHolder = new BaseRecyclerViewHolder<VM>(binding) {

            @Override
            protected int getViewModelId() {
                return BaseRecyclerViewAdapter.this.getViewModelId();
            }
        };

        simpleViewHolder.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(BaseRecyclerViewAdapter.this, simpleViewHolder.itemView, simpleViewHolder.getAdapterPosition());
            }
        });

        return simpleViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerViewHolder<VM> holder, int position) {
        holder.bind(viewModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return viewModelList.size();
    }

    @SuppressWarnings({"WeakerAccess", "SameReturnValue"})
    protected int getViewModelId() {
        return BR.viewModel;
    }

    public void setOnClickListener(OnItemClickListener<VM> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener<VM> {
        void onItemClick(BaseRecyclerViewAdapter<VM> adapter, View view, int position);
    }

}
