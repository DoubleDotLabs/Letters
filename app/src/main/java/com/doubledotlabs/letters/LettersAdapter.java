package com.doubledotlabs.letters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import java.util.ArrayList;

public class LettersAdapter extends RecyclerView.Adapter<LettersAdapter.ViewHolder> {

    Activity activity;
    public ArrayList<Letter> letters;

    public LettersAdapter(Activity activity, ArrayList<Letter> letters) {
        this.activity = activity;
        this.letters = letters;
    }

    @Override
    public LettersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder((SquareLetterView) ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_letter, null));
    }

    @Override
    public void onBindViewHolder(LettersAdapter.ViewHolder holder, int position) {
        Toast.makeText(activity, letters.get(position).letter, Toast.LENGTH_SHORT).show();
        holder.v.setLetter(letters.get(position).letter);
    }

    @Override
    public int getItemCount() {
        return letters.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SquareLetterView v;
        public ViewHolder(SquareLetterView v) {
            super(v);
            this.v = v;
        }
    }
}
