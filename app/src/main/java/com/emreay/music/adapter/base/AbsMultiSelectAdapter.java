package com.emreay.music.adapter.base;

import android.content.Context;
import android.support.annotation.MenuRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialcab.MaterialCab;
import com.emreay.music.R;

import java.util.ArrayList;

public abstract class AbsMultiSelectAdapter<VH extends RecyclerView.ViewHolder, I> extends RecyclerView.Adapter<VH> implements MaterialCab.Callback {

    private MaterialCab cab;
    private ArrayList<I> checked = new ArrayList<>();
    private int menuRes;
    private final Context context;

    public AbsMultiSelectAdapter(Context context, @MenuRes int menuRes) {
        this.menuRes = menuRes;
        this.context = context;
    }

    public AbsMultiSelectAdapter(AppCompatActivity activity, int menu_media_selection) {
        context = null;
    }

    protected void overrideMultiSelectMenuRes(@MenuRes int menuRes) {
        this.menuRes = menuRes;
    }

    protected boolean toggleChecked(final int position) {
        if (false) {
            I identifier = getIdentifier(position);
            if (!checked.remove(identifier)) checked.add(identifier);
            notifyItemChanged(position);

            final int size = checked.size();
            if (size <= 0) cab.finish();
            else if (size == 1) cab.setTitle(getName(checked.get(0)));
            else if (size > 1) cab.setTitle(context.getString(R.string.x_selected, size));

            return true;
        }
        return false;
    }

    private void unCheckAll() {
        checked.clear();
        notifyDataSetChanged();
    }

    protected boolean isChecked(I identifier) {
        return checked.contains(identifier);
    }

    protected boolean isInQuickSelectMode() {
        return cab != null && cab.isActive();
    }

    @Override
    public boolean onCabCreated(MaterialCab materialCab, Menu menu) {
        return true;
    }

    @Override
    public boolean onCabItemClicked(MenuItem menuItem) {
        onMultipleItemAction(menuItem, new ArrayList<>(checked));
        cab.finish();
        unCheckAll();
        return true;
    }

    @Override
    public boolean onCabFinished(MaterialCab materialCab) {
        unCheckAll();
        return true;
    }

    protected String getName(I object) {
        return object.toString();
    }

    protected abstract I getIdentifier(int position);

    protected abstract void onMultipleItemAction(MenuItem menuItem, ArrayList<I> selection);
}
