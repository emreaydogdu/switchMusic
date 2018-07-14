package com.emreay.music.activites.fragments.mainactivity.library.pager;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emreay.music.R;
import com.emreay.music.util.ViewUtil;
import com.github.florent37.expectanim.ExpectAnim;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollUpdateListener;
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter;

import static com.github.florent37.expectanim.core.Expectations.height;

public abstract class AbsLibraryPagerRecyclerViewFragment<A extends RecyclerView.Adapter, LM extends RecyclerView.LayoutManager> extends AbsLibraryPagerFragment implements OnOffsetChangedListener {

    public static final String TAG = AbsLibraryPagerRecyclerViewFragment.class.getSimpleName();

    private Unbinder unbinder;
    private ImageView shadow;
    private Integer scroll = 0;

    @BindView(R.id.container)
    View container;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @Nullable
    @BindView(android.R.id.empty)
    TextView empty;

    private A adapter;
    private LM layoutManager;
    private ProgressBar mProgressBar;
    private LinearLayout searchLayout;
    private ExpectAnim anim;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        unbinder = ButterKnife.bind(this, view);
        shadow = view.findViewById(R.id.shadow);
        mProgressBar = view.findViewById(R.id.progress_bar);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getLibraryFragment().addOnAppBarOffsetChangedListener(this);

        initLayoutManager();
        initAdapter();
        setUpRecyclerView();

        searchLayout = view.findViewById(R.id.searchLayout);
        setUpSearchBar();

    }

    public static void collapse(final View v, int duration, int targetHeight) {
        int prevHeight  = v.getHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new AnticipateOvershootInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    private void setUpSearchBar() {
        anim = new ExpectAnim()
                .expect(searchLayout).toBe(height(400))
                //.expect(recyclerView).toBe(
                //        belowOf(searchLayout)
                //)
                .toAnimation();
    }

    private void setUpRecyclerView() {
        if (recyclerView instanceof FastScrollRecyclerView) {
            ViewUtil.setUpFastScrollRecyclerViewColor(getActivity(), ((FastScrollRecyclerView) recyclerView), ThemeStore.accentColor(getActivity()));
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        VerticalOverScrollBounceEffectDecorator decor = new VerticalOverScrollBounceEffectDecorator(new RecyclerViewOverScrollDecorAdapter(recyclerView));

        decor.setOverScrollUpdateListener(new IOverScrollUpdateListener() {
            @Override
            public void onOverScrollUpdate(IOverScrollDecor decor, int state, float offset) {
                final View view = decor.getView();
                if (offset > 0) {
                    collapse(searchLayout,1000,400);
                } else if (offset < 0) {
                    // 'view' is currently being over-scrolled from the bottom.
                } else {
                    // No over-scroll is in-effect.
                    // This is synonymous with having (state == STATE_IDLE).
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                scroll += dy;
                if (dy != 0) collapse(searchLayout,1000,00);
                shadow.setVisibility((scroll == 0)?View.INVISIBLE:View.VISIBLE);

            }
        });

    }

    protected void invalidateLayoutManager() {
        initLayoutManager();
        recyclerView.setLayoutManager(layoutManager);
    }

    protected void invalidateAdapter() {
        initAdapter();
        checkIsEmpty();
        recyclerView.setAdapter(adapter);
    }

    private void initAdapter() {
        adapter = createAdapter();

        adapter.notifyDataSetChanged();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkIsEmpty();
                recyclerView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void initLayoutManager() {
        layoutManager = createLayoutManager();
    }

    protected A getAdapter() {
        return adapter;
    }
    protected LM getLayoutManager() {
        return layoutManager;
    }
    protected RecyclerView getRecyclerView() {
        return recyclerView;
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        //container.setPadding(container.getPaddingLeft(), container.getPaddingTop(), container.getPaddingRight(), getLibraryFragment().getTotalAppBarScrollingRange() + i);
    }

    private void checkIsEmpty() {
        if (empty != null) {
            empty.setText(getEmptyMessage());
            empty.setVisibility(adapter == null || adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    @StringRes
    protected int getEmptyMessage() {
        return R.string.empty;
    }

    @LayoutRes
    protected int getLayoutRes() {
        return R.layout.fragment_main_activity_recycler_view;
    }

    protected abstract LM createLayoutManager();

    @NonNull
    protected abstract A createAdapter();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getLibraryFragment().removeOnAppBarOffsetChangedListener(this);
        unbinder.unbind();
    }
}
