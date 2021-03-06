package com.emreay.music.activites.fragments.mainactivity.library.pager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;

import com.emreay.music.R;
import com.emreay.music.adapter.artist.ArtistAdapter;
import com.emreay.music.interfaces.LoaderIds;
import com.emreay.music.loader.ArtistLoader;
import com.emreay.music.misc.WrappedAsyncTaskLoader;
import com.emreay.music.model.Artist;
import com.emreay.music.util.PreferenceUtil;

import java.util.ArrayList;
public class ArtistsFragment extends MainRecyclerView<ArtistAdapter, GridLayoutManager> implements LoaderManager.LoaderCallbacks<ArrayList<Artist>> {

    public static final String TAG = ArtistsFragment.class.getSimpleName();

    private static final int LOADER_ID = LoaderIds.ARTISTS_FRAGMENT;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @NonNull
    @Override
    protected GridLayoutManager createLayoutManager() {
        return new GridLayoutManager(getActivity(), getGridSize());
    }

    @NonNull
    @Override
    protected ArtistAdapter createAdapter() {
        int itemLayoutRes = getItemLayoutRes();
        notifyLayoutResChanged(itemLayoutRes);
        ArrayList<Artist> dataSet = getAdapter() == null ? new ArrayList<Artist>() : getAdapter().getDataSet();
        return new ArtistAdapter(
                getLibraryFragment().getMainActivity(),
                dataSet,
                itemLayoutRes);
    }

    @Override
    protected int getEmptyMessage() {
        return R.string.no_artists;
    }

    @Override
    public void onMediaStoreChanged() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    protected int loadGridSize() {
        return PreferenceUtil.getInstance(getActivity()).getArtistGridSize(getActivity());
    }

    @Override
    protected void saveGridSize(int gridSize) {
        PreferenceUtil.getInstance(getActivity()).setArtistGridSize(gridSize);
    }

    @Override
    protected int loadGridSizeLand() {
        return PreferenceUtil.getInstance(getActivity()).getArtistGridSizeLand(getActivity());
    }

    @Override
    protected void saveGridSizeLand(int gridSize) {
        PreferenceUtil.getInstance(getActivity()).setArtistGridSizeLand(gridSize);
    }

    @Override
    protected void setGridSize(int gridSize) {
        getLayoutManager().setSpanCount(gridSize);
        getAdapter().notifyDataSetChanged();
    }


    @Override
    public Loader<ArrayList<Artist>> onCreateLoader(int id, Bundle args) {
        return new AsyncArtistLoader(getActivity());
    }


    @Override
    public void onLoadFinished(Loader<ArrayList<Artist>> loader, ArrayList<Artist> data) {
        getAdapter().swapDataSet(data);
    }


    @Override
    public void onLoaderReset(Loader<ArrayList<Artist>> loader) {
        getAdapter().swapDataSet(new ArrayList<Artist>());
    }

    private static class AsyncArtistLoader extends WrappedAsyncTaskLoader<ArrayList<Artist>> {
        public AsyncArtistLoader(Context context) {
            super(context);
        }

        @Override
        public ArrayList<Artist> loadInBackground() {
            return ArtistLoader.getAllArtists(getContext());
        }
    }
}
