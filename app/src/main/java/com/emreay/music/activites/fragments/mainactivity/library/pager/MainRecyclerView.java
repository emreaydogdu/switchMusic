package com.emreay.music.activites.fragments.mainactivity.library.pager;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.emreay.music.R;
import com.emreay.music.util.Util;

public abstract class MainRecyclerView<A extends RecyclerView.Adapter, LM extends RecyclerView.LayoutManager> extends AbsLibraryPagerRecyclerViewFragment<A, LM> {

    private int gridSize;
    private int currentLayoutRes;
    private RecyclerView recyclerView;

    public final int getGridSize() {
        if (gridSize == 0) { gridSize = (isLandscape())?loadGridSizeLand():loadGridSize(); }
        return gridSize;
    }

    public int getMaxGridSize() {
        return (isLandscape())?getResources().getInteger(R.integer.max_columns_land):getResources().getInteger(R.integer.max_columns);
    }

    public void setAndSaveGridSize(final int gridSize) {
        int oldLayoutRes = getItemLayoutRes();
        this.gridSize = gridSize;
        if (isLandscape()) {
            saveGridSizeLand(gridSize);
        } else {
            saveGridSize(gridSize);
        }
        if (oldLayoutRes != getItemLayoutRes()) {
            invalidateLayoutManager();
            invalidateAdapter();
        } else {
            setGridSize(gridSize);
        }
    }

    @LayoutRes
    protected int getItemLayoutRes() {
        if (getGridSize() > getMaxGridSizeForList()) {
            return R.layout.item_grid;
        }
        return R.layout.item_list;
    }

    protected final void notifyLayoutResChanged(@LayoutRes int res) {
        this.currentLayoutRes = res;
        recyclerView = getRecyclerView();
        if (recyclerView != null) {
            applyRecyclerViewPaddingForLayoutRes(recyclerView, currentLayoutRes);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        applyRecyclerViewPaddingForLayoutRes(getRecyclerView(), currentLayoutRes);
        getRecyclerView().setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
    }

    protected void applyRecyclerViewPaddingForLayoutRes(@NonNull RecyclerView recyclerView, @LayoutRes int res) {
        int padding;
        if (res == R.layout.item_grid) {
            padding = (int) (getResources().getDisplayMetrics().density * 2);
        } else {
            padding = 0;
        }
        recyclerView.setPadding(padding, padding, padding, padding);
    }

    protected abstract void saveGridSizeLand(int gridColumns);
    protected abstract int loadGridSize();
    protected abstract void saveGridSize(int gridColumns);
    protected abstract int loadGridSizeLand();

    protected abstract void setGridSize(int gridSize);

    protected int getMaxGridSizeForList() {
        if (isLandscape()) return getActivity().getResources().getInteger(R.integer.default_list_columns_land);
        return getActivity().getResources().getInteger(R.integer.default_list_columns);
    }

    protected final boolean isLandscape() {
        return Util.isLandscape(getResources());
    }


}
