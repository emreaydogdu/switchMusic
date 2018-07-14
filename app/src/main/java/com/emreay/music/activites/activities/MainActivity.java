package com.emreay.music.activites.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.emreay.music.App;
import com.emreay.music.R;
import com.emreay.music.activites.activities.base.SlidingMusicPanelActivity;
import com.emreay.music.activites.activities.intro.AppIntroActivity;
import com.emreay.music.activites.fragments.mainactivity.folders.FoldersFragment;
import com.emreay.music.activites.fragments.mainactivity.library.LibraryFragment;
import com.emreay.music.dialogs.ChangelogDialog;
import com.emreay.music.helper.MusicPlayerRemote;
import com.emreay.music.helper.SearchQueryHelper;
import com.emreay.music.loader.AlbumLoader;
import com.emreay.music.loader.ArtistSongLoader;
import com.emreay.music.loader.PlaylistSongLoader;
import com.emreay.music.model.Song;
import com.emreay.music.service.MusicService;
import com.emreay.music.util.PreferenceUtil;

import java.util.ArrayList;

public class MainActivity extends SlidingMusicPanelActivity {

    public static final int APP_INTRO_REQUEST = 100;
    public static final int PURCHASE_REQUEST = 101;

    private static final int LIBRARY = 0;
    private static final int FOLDERS = 1;

    @Nullable MainActivityFragmentCallbacks currentFragment;

    private boolean blockRequestPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            //ThemeUtil.setDrawUnderStatusbar(getWindow());
            findViewById(R.id.container).setFitsSystemWindows(false);
        }

        setVolume();

        if (savedInstanceState == null) {
            setMusicChooser(PreferenceUtil.getInstance(this).getLastMusicChooser());
        } else {
            restoreCurrentFragment();
        }

        if (!checkShowIntro()) {
            checkShowChangelog();
        }
    }
/*
    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.nav_library:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setMusicChooser(LIBRARY);
                            }
                        }, 200);
                        break;
                    case R.id.nav_folders:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setMusicChooser(FOLDERS);
                            }
                        }, 200);
                        break;
                    case R.id.nav_test:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setMusicChooser(TEST);
                            }
                        }, 200);
                        break;
                    case R.id.buy_pro:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivityForResult(new Intent(MainActivity.this, PurchaseActivity.class), PURCHASE_REQUEST);
                            }
                        }, 200);
                        break;
                    case R.id.nav_settings:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                            }
                        }, 200);
                        break;
                    case R.id.nav_about:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                            }
                        }, 200);
                        break;
                }
                return true;
            }
        });
    }
*/

    private void setMusicChooser(int key) {
        if (!App.isProVersion() && key == FOLDERS) {
            Toast.makeText(this, R.string.folder_view_is_a_pro_feature, Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent(this, PurchaseActivity.class), PURCHASE_REQUEST);
            key = LIBRARY;
        }

        PreferenceUtil.getInstance(this).setLastMusicChooser(key);
        switch (key) {
            case LIBRARY:
                setCurrentFragment(LibraryFragment.newInstance());
                break;
            case FOLDERS:
                setCurrentFragment(FoldersFragment.newInstance(this));
                break;
        }
    }
    private void setCurrentFragment(@SuppressWarnings("NullableProblems") Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, null).commit();
        currentFragment = (MainActivityFragmentCallbacks) fragment;
    }
    private void restoreCurrentFragment() {
        currentFragment = (MainActivityFragmentCallbacks) getSupportFragmentManager().findFragmentById(R.id.container);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_INTRO_REQUEST) {
            blockRequestPermissions = false;
            if (!hasPermissions()) {
                requestPermissions();
            }
            checkSetUpPro(); // good chance that pro version check was delayed on first start
        } else if (requestCode == PURCHASE_REQUEST) {
            if (resultCode == RESULT_OK) {
                checkSetUpPro();
            }
        }
    }

    @Override
    protected void requestPermissions() {
        if (!blockRequestPermissions) super.requestPermissions();
    }

    @Override
    protected View createContentView() {
        return wrapSlidingMusicPanel(R.layout.activity_main);
    }

    @Override
    public void onPlayingMetaChanged() {
        super.onPlayingMetaChanged();
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        handlePlaybackIntent(getIntent());
    }

    @Override
    public boolean handleBackPress() {
        return super.handleBackPress() || (currentFragment != null && currentFragment.handleBackPress());
    }

    private void handlePlaybackIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        Uri uri = intent.getData();
        String mimeType = intent.getType();
        boolean handled = false;

        if (intent.getAction() != null && intent.getAction().equals(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH)) {
            final ArrayList<Song> songs = SearchQueryHelper.getSongs(this, intent.getExtras());
            if (MusicPlayerRemote.getShuffleMode() == MusicService.SHUFFLE_MODE_SHUFFLE) {
                MusicPlayerRemote.openAndShuffleQueue(songs, true);
            } else {
                MusicPlayerRemote.openQueue(songs, 0, true);
            }
            handled = true;
        }

        if (uri != null && uri.toString().length() > 0) {
            MusicPlayerRemote.playFromUri(uri);
            handled = true;
        } else if (MediaStore.Audio.Playlists.CONTENT_TYPE.equals(mimeType)) {
            final int id = (int) parseIdFromIntent(intent, "playlistId", "playlist");
            if (id >= 0) {
                int position = intent.getIntExtra("position", 0);
                ArrayList<Song> songs = new ArrayList<>();
                songs.addAll(PlaylistSongLoader.getPlaylistSongList(this, id));
                MusicPlayerRemote.openQueue(songs, position, true);
                handled = true;
            }
        } else if (MediaStore.Audio.Albums.CONTENT_TYPE.equals(mimeType)) {
            final int id = (int) parseIdFromIntent(intent, "albumId", "album");
            if (id >= 0) {
                int position = intent.getIntExtra("position", 0);
                MusicPlayerRemote.openQueue(AlbumLoader.getAlbum(this, id).songs, position, true);
                handled = true;
            }
        } else if (MediaStore.Audio.Artists.CONTENT_TYPE.equals(mimeType)) {
            final int id = (int) parseIdFromIntent(intent, "artistId", "artist");
            if (id >= 0) {
                int position = intent.getIntExtra("position", 0);
                MusicPlayerRemote.openQueue(ArtistSongLoader.getArtistSongList(this, id), position, true);
                handled = true;
            }
        }
        if (handled) {
            setIntent(new Intent());
        }
    }

    private long parseIdFromIntent(@NonNull Intent intent, String longKey, String stringKey) {
        long id = intent.getLongExtra(longKey, -1);
        if (id < 0) {
            String idString = intent.getStringExtra(stringKey);
            if (idString != null) {
                try {
                    id = Long.parseLong(idString);
                } catch (NumberFormatException e) {
                }
            }
        }
        return id;
    }


    public  void    setUpPro()           {
        //navigationView.getMenu().removeGroup(R.id.navigation_drawer_menu_category_buy_pro);
    }
    private boolean checkShowIntro()     {
        if (!PreferenceUtil.getInstance(this).introShown()) {
            PreferenceUtil.getInstance(this).setIntroShown();
            ChangelogDialog.setChangelogRead(this);
            blockRequestPermissions = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivityForResult(new Intent(MainActivity.this, AppIntroActivity.class), APP_INTRO_REQUEST);
                }
            }, 50);
            return true;
        }
        return false;
    }
    private boolean checkShowChangelog() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int currentVersion = pInfo.versionCode;
            if (currentVersion != PreferenceUtil.getInstance(this).getLastChangelogVersion()) {
                ChangelogDialog.create().show(getSupportFragmentManager(), "CHANGE_LOG_DIALOG");
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
    private void    checkSetUpPro()      {
        if (App.isProVersion()) {
            setUpPro();
        }
    }

    public interface MainActivityFragmentCallbacks { boolean handleBackPress(); }

}