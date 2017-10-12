package com.emreay.music.ui.fragments.mainactivity.library.pager;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.emreay.music.ui.fragments.AbsMusicServiceFragment;
import com.emreay.music.ui.fragments.mainactivity.test.TestFragment;

public class AbsTestPagerFragment extends AbsMusicServiceFragment {

    /* http://stackoverflow.com/a/2888433 */
    @Override
    public LoaderManager getLoaderManager() {
        return getParentFragment().getLoaderManager();
    }

    public TestFragment getLibraryFragment() {
        return (TestFragment) getParentFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
