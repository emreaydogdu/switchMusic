package com.emreay.music.activites.fragments.mainactivity.library;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.emreay.music.BuildConfig;
import com.emreay.music.R;
import com.emreay.music.activites.activities.MainActivity;
import com.emreay.music.activites.activities.SearchActivity;
import com.emreay.music.activites.activities.SettingsActivity;
import com.emreay.music.activites.activities.base.MainActivityFragment;
import com.emreay.music.activites.fragments.mainactivity.library.pager.MainRecyclerView;
import com.emreay.music.adapter.MusicLibraryPagerAdapter;
import com.emreay.music.dialogs.CreatePlaylistDialog;
import com.emreay.music.helper.MusicPlayerRemote;
import com.emreay.music.loader.SongLoader;
import com.emreay.music.util.PreferenceUtil;
import com.emreay.music.util.Util;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.TabLayoutUtil;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class LibraryFragment extends MainActivityFragment implements MainActivity.MainActivityFragmentCallbacks, ViewPager.OnPageChangeListener, View.OnClickListener {

    private Unbinder unbinder;
    public ImageView shadow,profile,bs_profile;

    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.pager)   ViewPager pager;
    @BindView(R.id.root) ViewGroup root;

    private MusicLibraryPagerAdapter pagerAdapter;
    private BottomSheetDialog mBottomSheetDialog,mOptionSheet;
    private View contentView;

    SharedPreferences preferences;

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    public LibraryFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        unbinder = ButterKnife.bind(this, view);

        shadow = view.findViewById(R.id.shadow);

        profileBottomSheet();
        menuBottomSheet();

        profile = view.findViewById(R.id.profile);

        preferences = getActivity().getSharedPreferences("myprefs", 0);
        String img_str = preferences.getString("userphoto", BuildConfig.FLAVOR);
        if (!img_str.equals(BuildConfig.FLAVOR)) {
            byte[] imageAsBytes = Base64.decode(img_str.getBytes(), 0);
            profile.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            bs_profile.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }

        profile.setOnClickListener(this);
        view.findViewById(R.id.menu).setOnClickListener(this);

        return view;
    }


    private void profileBottomSheet() {
        mBottomSheetDialog = new BottomSheetDialog(getContext());
        contentView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_profile,null);
        mBottomSheetDialog.setContentView(contentView);
        mBottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        mBottomSheetDialog.setCancelable(true);

        bs_profile = contentView.findViewById(R.id.bs_profile);

        contentView.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 2);

            }
        });

        contentView.findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), SettingsActivity.class);
                getActivity().startActivity(myIntent);
                mBottomSheetDialog.dismiss();
            }
        });
    }
    private void menuBottomSheet() {
        mOptionSheet = new BottomSheetDialog(getContext());
        contentView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_options,null);
        mOptionSheet.setContentView(contentView);
        mOptionSheet.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        mOptionSheet.setCancelable(true);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            Drawable drawable = Util.loadImage(getContext(),data.getData(),getResources());
            profile.setImageDrawable(drawable);
            bs_profile.setImageDrawable(drawable);
            profile.buildDrawingCache();
            Util.saveImage(getContext(),profile);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pager.removeOnPageChangeListener(this);
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setStatusbarColorAuto(view);
        getMainActivity().setNavigationbarColorAuto();
        getMainActivity().setTaskDescriptionColorAuto();

        setUpViewPager();
    }

    private void setUpViewPager() {
        pagerAdapter = new MusicLibraryPagerAdapter(getActivity(), getChildFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(pagerAdapter.getCount() - 1);

        tabs.setupWithViewPager(pager);
        //tabs2.setupWithViewPager(pager);


        int primaryColor = ThemeStore.primaryColor(getActivity());
        int normalColor = ToolbarContentTintHelper.toolbarSubtitleColor(getActivity(), primaryColor);
        int selectedColor = ToolbarContentTintHelper.toolbarTitleColor(getActivity(), primaryColor);
        TabLayoutUtil.setTabIconColors(tabs, normalColor, selectedColor);
        tabs.setTabTextColors(normalColor, selectedColor);
        tabs.setScrollBarSize(20);
        tabs.setSelectedTabIndicatorColor(ThemeStore.accentColor(getActivity()));

        int startPosition = PreferenceUtil.getInstance(getActivity()).getDefaultStartPage();
        startPosition = startPosition == -1 ? PreferenceUtil.getInstance(getActivity()).getLastPage() : startPosition;
        pager.setCurrentItem(startPosition);
        PreferenceUtil.getInstance(getActivity()).setLastPage(startPosition); // just in case
        pager.addOnPageChangeListener(this);
    }

    public Fragment getCurrentFragment() {
        return pagerAdapter.getFragment(pager.getCurrentItem());
    }

    private boolean isPlaylistPage() {
       // return pager.getCurrentItem() == MusicLibraryPagerAdapter.MusicFragments.PLAYLIST.ordinal();
        return false;
    }

    public void addOnAppBarOffsetChangedListener(AppBarLayout.OnOffsetChangedListener onOffsetChangedListener) {
        //appbar.addOnOffsetChangedListener(onOffsetChangedListener);
    }

    public void removeOnAppBarOffsetChangedListener(AppBarLayout.OnOffsetChangedListener onOffsetChangedListener) {
        //appbar.removeOnOffsetChangedListener(onOffsetChangedListener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (pager == null) return false;
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof MainRecyclerView) {
            MainRecyclerView absLibraryRecyclerViewCustomGridSizeFragment = (MainRecyclerView) currentFragment;
            if (handleGridSizeMenuItem(absLibraryRecyclerViewCustomGridSizeFragment, item)) {
                return true;
            }
        }

        int id = item.getItemId();
        switch (id) {
            case R.id.action_shuffle_all:
                MusicPlayerRemote.openAndShuffleQueue(SongLoader.getAllSongs(getActivity()), true);
                return true;
            case R.id.action_new_playlist:
                CreatePlaylistDialog.create().show(getChildFragmentManager(), "CREATE_PLAYLIST");
                return true;
            case R.id.action_search:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpGridSizeMenu(@NonNull MainRecyclerView fragment, @NonNull SubMenu gridSizeMenu) {
        switch (fragment.getGridSize()) {
            case 1:
                gridSizeMenu.findItem(R.id.action_grid_size_1).setChecked(true);
                break;
            case 2:
                gridSizeMenu.findItem(R.id.action_grid_size_2).setChecked(true);
                break;
            case 3:
                gridSizeMenu.findItem(R.id.action_grid_size_3).setChecked(true);
                break;
            case 4:
                gridSizeMenu.findItem(R.id.action_grid_size_4).setChecked(true);
                break;
            case 5:
                gridSizeMenu.findItem(R.id.action_grid_size_5).setChecked(true);
                break;
            case 6:
                gridSizeMenu.findItem(R.id.action_grid_size_6).setChecked(true);
                break;
            case 7:
                gridSizeMenu.findItem(R.id.action_grid_size_7).setChecked(true);
                break;
            case 8:
                gridSizeMenu.findItem(R.id.action_grid_size_8).setChecked(true);
                break;
        }
        int maxGridSize = fragment.getMaxGridSize();
        if (maxGridSize < 8) {
            gridSizeMenu.findItem(R.id.action_grid_size_8).setVisible(false);
        }
        if (maxGridSize < 7) {
            gridSizeMenu.findItem(R.id.action_grid_size_7).setVisible(false);
        }
        if (maxGridSize < 6) {
            gridSizeMenu.findItem(R.id.action_grid_size_6).setVisible(false);
        }
        if (maxGridSize < 5) {
            gridSizeMenu.findItem(R.id.action_grid_size_5).setVisible(false);
        }
        if (maxGridSize < 4) {
            gridSizeMenu.findItem(R.id.action_grid_size_4).setVisible(false);
        }
        if (maxGridSize < 3) {
            gridSizeMenu.findItem(R.id.action_grid_size_3).setVisible(false);
        }
    }

    private boolean handleGridSizeMenuItem(@NonNull MainRecyclerView fragment, @NonNull MenuItem item) {
        int gridSize = 0;
        switch (item.getItemId()) {
            case R.id.action_grid_size_1:
                gridSize = 1;
                break;
            case R.id.action_grid_size_2:
                gridSize = 2;
                break;
            case R.id.action_grid_size_3:
                gridSize = 3;
                break;
            case R.id.action_grid_size_4:
                gridSize = 4;
                break;
            case R.id.action_grid_size_5:
                gridSize = 5;
                break;
            case R.id.action_grid_size_6:
                gridSize = 6;
                break;
            case R.id.action_grid_size_7:
                gridSize = 7;
                break;
            case R.id.action_grid_size_8:
                gridSize = 8;
                break;
        }
        if (gridSize > 0) {
            item.setChecked(true);
            fragment.setAndSaveGridSize(gridSize);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleBackPress() {
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        PreferenceUtil.getInstance(getActivity()).setLastPage(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu:
                mOptionSheet.show();
                break;
            case R.id.profile:
                mBottomSheetDialog.show();
                break;
        }

    }


    //TODO: GEREKSIZ
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (pager == null) return;
        inflater.inflate(R.menu.menu_main, menu);
        if (isPlaylistPage()) {
            menu.add(0, R.id.action_new_playlist, 0, R.string.new_playlist_title);
        }
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof MainRecyclerView && currentFragment.isAdded()) {
            MainRecyclerView absLibraryRecyclerViewCustomGridSizeFragment = (MainRecyclerView) currentFragment;

            MenuItem gridSizeItem = menu.findItem(R.id.action_grid_size);
            if (Util.isLandscape(getResources())) {
                gridSizeItem.setTitle(R.string.action_grid_size_land);
            }
            setUpGridSizeMenu(absLibraryRecyclerViewCustomGridSizeFragment, gridSizeItem.getSubMenu());

        } else {
            menu.removeItem(R.id.action_grid_size);
            menu.removeItem(R.id.action_colored_footers);
        }
        Activity activity = getActivity();
        if (activity == null) return;
        //ToolbarContentTintHelper.handleOnCreateOptionsMenu(getActivity(), toolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(toolbar));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Activity activity = getActivity();
        if (activity == null) return;
        //ToolbarContentTintHelper.handleOnPrepareOptionsMenu(activity, toolbar);
    }


}
