package kim.jeonghyeon.androidlibrary.ui.binder.recyclerview;

import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public class SelectableRecyclerViewModel<VM extends SelectableRecyclerViewItemViewModel> extends BaseRecyclerViewModel<VM> implements OnItemSelectionChangedListener<VM> {

    public static final int SELECTION_MODE_NONE = 0;
    public static final int SELECTION_MODE_SINGLE = 1;
    public static final int SELECTION_MODE_MULTIPLE = 2;
    private final ArrayList<Integer> selectedPositions = new ArrayList<>();
    private int selectionMode = SELECTION_MODE_NONE;

    public void setSelectionMode(int selectionMode) {
        this.selectionMode = selectionMode;
        removeItemsIfSelectionModeNotAllow();
    }

    /**
     * @return -1 if not selected, if multiple selection mode, it returns last selected position
     */
    public int getSelectedPosition() {
        return selectedPositions.size() == 0 ? -1 : selectedPositions.get(selectedPositions.size() - 1);
    }

    public void setSelectedPosition(int... positions) {
        clearSelection();
        addSelectedPosition(positions);
    }

    /**
     * @return all the selected positions. not sorted. ordered by selected order
     */
    @NonNull
    @SuppressWarnings("unused")
    public List<Integer> getSelectedPositions() {
        return new ArrayList<>(selectedPositions);
    }

    public void addSelectedPosition(@NonNull int... positions) {
        for (int position : positions) {
            if (selectedPositions.contains(position)) {
                continue;
            }

            selectedPositions.add(position);
        }
        applySelectionToItem();

        removeItemsIfSelectionModeNotAllow();
    }

    public void removeSelection(@NonNull int... position) {
        for (int i : position) {
            selectedPositions.remove((Integer) i);
        }
        applySelectionToItem();
    }

    public void clearSelection() {
        selectedPositions.clear();
        applySelectionToItem();
    }

    private void applySelectionToItem() {
        for (int i = 0; i < getItemViewModels().size(); i++) {
            VM vm = getItemViewModels().get(i);
            vm.getSelected().set(selectedPositions.contains(i));
        }
    }

    private void removeItemsIfSelectionModeNotAllow() {
        if (selectionMode == SELECTION_MODE_NONE && selectedPositions.size() > 0) {
            clearSelection();
        } else if (selectionMode == SELECTION_MODE_SINGLE && selectedPositions.size() > 1) {
            setSelectedPosition(getSelectedPosition());
        }
    }

    @Override
    @CallSuper
    public void onItemClick(BaseRecyclerViewAdapter<VM> adapter, View view, int position) {
        super.onItemClick(adapter, view, position);
        boolean alreadySelected = selectedPositions.contains(position);
        if (alreadySelected) {
            removeSelection(position);
        } else {
            addSelectedPosition(position);
        }

        onItemSelectionChanged(this, getItemViewModels().get(position), position);
    }

    @Override
    public void onItemSelectionChanged(SelectableRecyclerViewModel<VM> listViewModel, VM itemViewModel, int position) {

    }
}
