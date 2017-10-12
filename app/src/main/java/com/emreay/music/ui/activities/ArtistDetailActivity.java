package com.emreay.music.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcab.MaterialCab;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.emreay.music.R;
import com.emreay.music.adapter.album.HorizontalAlbumAdapter;
import com.emreay.music.adapter.song.ArtistSongAdapter;
import com.emreay.music.dialogs.AddToPlaylistDialog;
import com.emreay.music.dialogs.SleepTimerDialog;
import com.emreay.music.glide.PhonographColoredTarget;
import com.emreay.music.glide.artistimage.ArtistImage;
import com.emreay.music.glide.palette.BitmapPaletteTranscoder;
import com.emreay.music.glide.palette.BitmapPaletteWrapper;
import com.emreay.music.helper.MusicPlayerRemote;
import com.emreay.music.interfaces.CabHolder;
import com.emreay.music.interfaces.LoaderIds;
import com.emreay.music.interfaces.PaletteColorHolder;
import com.emreay.music.lastfm.rest.LastFMRestClient;
import com.emreay.music.lastfm.rest.model.LastFmArtist;
import com.emreay.music.loader.ArtistLoader;
import com.emreay.music.misc.SimpleObservableScrollViewCallbacks;
import com.emreay.music.misc.WrappedAsyncTaskLoader;
import com.emreay.music.model.Artist;
import com.emreay.music.model.Song;
import com.emreay.music.ui.activities.base.AbsSlidingMusicPanelActivity;
import com.emreay.music.util.ArtistSignatureUtil;
import com.emreay.music.util.NavigationUtil;
import com.emreay.music.util.PhonographColorUtil;
import com.emreay.music.util.PreferenceUtil;
import com.emreay.music.util.Util;
import com.github.florent37.expectanim.ExpectAnim;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.kabouzeid.appthemehelper.util.ColorUtil;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.github.florent37.expectanim.core.Expectations.height;
import static com.github.florent37.expectanim.core.Expectations.leftOfParent;
import static com.github.florent37.expectanim.core.Expectations.rightOfParent;
import static com.github.florent37.expectanim.core.Expectations.topOfParent;
import static com.github.florent37.expectanim.core.Expectations.width;

public class ArtistDetailActivity extends AbsSlidingMusicPanelActivity implements PaletteColorHolder, CabHolder, LoaderManager.LoaderCallbacks<Artist> {

    public static final String TAG = ArtistDetailActivity.class.getSimpleName();
    private static final int LOADER_ID = LoaderIds.ARTIST_DETAIL_ACTIVITY;

    public static final String EXTRA_ARTIST_ID = "extra_artist_id";

    @BindView(R.id.image) ImageView artistImage;
    @BindView(R.id.list) ObservableListView songListView;
    @BindView(R.id.title) TextView artistName;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.imageCard) CardView imageCard;
    @BindView(R.id.background) View background;

    View songListHeader;
    RecyclerView albumRecyclerView;
    private ExpectAnim expectAnimMove;

    private MaterialCab cab;
    private int toolbarColor;
    private boolean usePalette;

    private Artist artist;
    @Nullable
    private Spanned biography;
    private MaterialDialog biographyDialog;
    private HorizontalAlbumAdapter albumAdapter;
    private ArtistSongAdapter songAdapter;

    private LastFMRestClient lastFMRestClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDrawUnderStatusbar(true);
        setLightStatusbar(true);
        ButterKnife.bind(this);

        supportPostponeEnterTransition();

        lastFMRestClient = new LastFMRestClient(this);
        usePalette = PreferenceUtil.getInstance(this).albumArtistColoredFooters();

        initViews();
        setUpViews();
        //setUpToolbar();
        setUpAnim();

        getSupportLoaderManager().initLoader(LOADER_ID, getIntent().getExtras(), this);
    }

    private void setUpAnim() {
        expectAnimMove = new ExpectAnim()
                .expect(artistName)
                .toBe(topOfParent().withMarginDp(10),
                     leftOfParent().withMarginDp(0))

                .expect(imageCard)
                .toBe(rightOfParent().withMarginDp(25),
                        topOfParent().withMarginDp(10),
                                            height(100),
                                             width(100))
                .toAnimation();
    }

    @Override
    protected View createContentView() {
        return wrapSlidingMusicPanel(R.layout.activity_artist_detail);
    }

    private final SimpleObservableScrollViewCallbacks observableScrollViewCallbacks = new SimpleObservableScrollViewCallbacks() {


        @Override
        public void onScrollChanged(int scrollY, boolean b, boolean b2) {

            final float percent = (500 + scrollY * 1f) / 300;
            expectAnimMove.setPercent(percent);

            // Translate list background
            background.setTranslationY(Math.max(0, -scrollY - 145));

        }
    };

    private void initViews() {
        songListHeader = LayoutInflater.from(this).inflate(R.layout.artist_detail_header, songListView, false);
        albumRecyclerView = ButterKnife.findById(songListHeader, R.id.recycler_view);
    }

    private void setUpViews() {
        setUpSongListView();
        setUpAlbumRecyclerView();
    }

    private void setUpSongListView() {
        //songListView.setPadding(0, 400, 0, 100);
        songListView.setScrollViewCallbacks(observableScrollViewCallbacks);
        songListView.addHeaderView(songListHeader);

        songAdapter = new ArtistSongAdapter(this, getArtist().getSongs(), this);
        songListView.setAdapter(songAdapter);

    }

    private void setUpAlbumRecyclerView() {
        albumRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        albumAdapter = new HorizontalAlbumAdapter(this, getArtist().albums, usePalette, this);
        albumRecyclerView.setAdapter(albumAdapter);
        albumAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (albumAdapter.getItemCount() == 0) finish();
            }
        });
    }

    protected void setUsePalette(boolean usePalette) {
        albumAdapter.usePalette(usePalette);
        PreferenceUtil.getInstance(this).setAlbumArtistColoredFooters(usePalette);
        this.usePalette = usePalette;
    }

    private void reload() {
        getSupportLoaderManager().restartLoader(LOADER_ID, getIntent().getExtras(), this);
    }

    private void loadBiography() {
        loadBiography(Locale.getDefault().getLanguage());
    }

    private void loadBiography(@Nullable final String lang) {
        biography = null;

        lastFMRestClient.getApiService()
                .getArtistInfo(getArtist().getName(), lang, null)
                .enqueue(new Callback<LastFmArtist>() {
                    @Override
                    public void onResponse(@NonNull Call<LastFmArtist> call, @NonNull Response<LastFmArtist> response) {
                        final LastFmArtist lastFmArtist = response.body();
                        if (lastFmArtist != null && lastFmArtist.getArtist() != null) {
                            final String bioContent = lastFmArtist.getArtist().getBio().getContent();
                            if (bioContent != null && !bioContent.trim().isEmpty()) {
                                biography = Html.fromHtml(bioContent);
                            }
                        }

                        // If the "lang" parameter is set and no biography is given, retry with default language
                        if (biography == null && lang != null) {
                            loadBiography(null);
                            return;
                        }

                        if (!Util.isAllowedToDownloadMetadata(ArtistDetailActivity.this)) {
                            if (biography != null) {
                                biographyDialog.setContent(biography);
                            } else {
                                biographyDialog.dismiss();
                                Toast.makeText(ArtistDetailActivity.this, getResources().getString(R.string.biography_unavailable), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<LastFmArtist> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        biography = null;
                    }
                });
    }

    private void loadArtistImage(final boolean forceDownload) {
        if (forceDownload) {
            ArtistSignatureUtil.getInstance(this).updateArtistSignature(getArtist().getName());
        }
        Glide.with(this)
                .load(new ArtistImage(getArtist().getName(), forceDownload))
                .asBitmap()
                .transcode(new BitmapPaletteTranscoder(this), BitmapPaletteWrapper.class)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.default_artist_image)
                .signature(ArtistSignatureUtil.getInstance(this).getArtistSignature(getArtist().getName()))
                .dontAnimate()
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .listener(new RequestListener<ArtistImage, BitmapPaletteWrapper>() {
                    @Override
                    public boolean onException(@Nullable Exception e, ArtistImage model, Target<BitmapPaletteWrapper> target, boolean isFirstResource) {
                        if (forceDownload) {
                            Toast.makeText(ArtistDetailActivity.this, e != null ? e.getClass().getSimpleName() : "Error", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(BitmapPaletteWrapper resource, ArtistImage model, Target<BitmapPaletteWrapper> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (forceDownload) {
                            Toast.makeText(ArtistDetailActivity.this, getString(R.string.updated_artist_image), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                })
                .into(new PhonographColoredTarget(artistImage) {
                    @Override
                    public void onColorReady(int color) {
                        setColors(color);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            reload();
        }
    }

    @Override
    public int getPaletteColor() {
        return toolbarColor;
    }

    private void setColors(int color) {
        toolbarColor = color;
        //artistName.setBackgroundColor(color);
        //artistName.setTextColor(MaterialValueHelper.getPrimaryTextColor(this, ColorUtil.isColorLight(color)));
        setNavigationbarColor(color);
        setTaskDescriptionColor(color);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_artist_detail, menu);
        menu.findItem(R.id.action_colored_footers).setChecked(usePalette);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        final ArrayList<Song> songs = songAdapter.getDataSet();
        switch (id) {
            case R.id.action_sleep_timer:
                new SleepTimerDialog().show(getSupportFragmentManager(), "SET_SLEEP_TIMER");
                return true;
            case R.id.action_equalizer:
                NavigationUtil.openEqualizer(this);
                return true;
            case R.id.action_shuffle_artist:
                MusicPlayerRemote.openAndShuffleQueue(songs, true);
                return true;
            case R.id.action_play_next:
                MusicPlayerRemote.playNext(songs);
                return true;
            case R.id.action_add_to_current_playing:
                MusicPlayerRemote.enqueue(songs);
                return true;
            case R.id.action_add_to_playlist:
                AddToPlaylistDialog.create(songs).show(getSupportFragmentManager(), "ADD_PLAYLIST");
                return true;
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_biography:
                if (biographyDialog == null) {
                    biographyDialog = new MaterialDialog.Builder(this)
                            .title(artist.getName())
                            .positiveText(android.R.string.ok)
                            .build();
                }
                if (Util.isAllowedToDownloadMetadata(ArtistDetailActivity.this)) {
                    if (biography != null) {
                        biographyDialog.setContent(biography);
                        biographyDialog.show();
                    } else {
                        Toast.makeText(ArtistDetailActivity.this, getResources().getString(R.string.biography_unavailable), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    biographyDialog.show();
                    loadBiography();
                }
                return true;
            case R.id.action_re_download_artist_image:
                Toast.makeText(ArtistDetailActivity.this, getResources().getString(R.string.updating), Toast.LENGTH_SHORT).show();
                loadArtistImage(true);
                return true;
            case R.id.action_colored_footers:
                item.setChecked(!item.isChecked());
                setUsePalette(item.isChecked());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public MaterialCab openCab(int menuRes, @NonNull final MaterialCab.Callback callback) {
        if (cab != null && cab.isActive()) cab.finish();
        cab = new MaterialCab(this, R.id.cab_stub)
                .setMenu(menuRes)
                .setCloseDrawableRes(R.drawable.ic_close_white_24dp)
                .setBackgroundColor(PhonographColorUtil.shiftBackgroundColorForLightText(getPaletteColor()))
                .start(new MaterialCab.Callback() {
                    @Override
                    public boolean onCabCreated(MaterialCab materialCab, Menu menu) {
                        setStatusbarColor(ColorUtil.stripAlpha(toolbarColor));
                        return callback.onCabCreated(materialCab, menu);
                    }

                    @Override
                    public boolean onCabItemClicked(MenuItem menuItem) {
                        return callback.onCabItemClicked(menuItem);
                    }

                    @Override
                    public boolean onCabFinished(MaterialCab materialCab) {
                        //setStatusbarColor(ColorUtil.withAlpha(toolbarColor, toolbarAlpha));
                        return callback.onCabFinished(materialCab);
                    }
                });
        return cab;
    }

    @Override
    public void onBackPressed() {
        if (cab != null && cab.isActive()) cab.finish();
        else {
            albumRecyclerView.stopScroll();
            super.onBackPressed();
        }
    }

    @Override
    public void onMediaStoreChanged() {
        super.onMediaStoreChanged();
        reload();
    }

    @Override
    public void setStatusbarColor(int color) {
        super.setStatusbarColor(color);
        setLightStatusbar(false);
    }

    private void setArtist(Artist artist) {
        this.artist = artist;
        loadArtistImage(false);

        if (Util.isAllowedToDownloadMetadata(this)) {
            loadBiography();
        }

        artistName.setText(artist.getName());
        songAdapter.swapDataSet(artist.getSongs());
        albumAdapter.swapDataSet(artist.albums);
    }

    private Artist getArtist() {
        if (artist == null) artist = new Artist();
        return artist;
    }

    @Override
    public Loader<Artist> onCreateLoader(int id, Bundle args) {
        return new AsyncArtistDataLoader(this, args.getInt(EXTRA_ARTIST_ID));
    }

    @Override
    public void onLoadFinished(Loader<Artist> loader, Artist data) {
        supportStartPostponedEnterTransition();
        setArtist(data);
    }

    @Override
    public void onLoaderReset(Loader<Artist> loader) {
        this.artist = new Artist();
        songAdapter.swapDataSet(artist.getSongs());
        albumAdapter.swapDataSet(artist.albums);
    }

    private static class AsyncArtistDataLoader extends WrappedAsyncTaskLoader<Artist> {
        private final int artistId;

        public AsyncArtistDataLoader(Context context, int artistId) {
            super(context);
            this.artistId = artistId;
        }

        @Override
        public Artist loadInBackground() {
            return ArtistLoader.getArtist(getContext(), artistId);
        }
    }
}