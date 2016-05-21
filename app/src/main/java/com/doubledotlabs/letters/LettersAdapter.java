package com.doubledotlabs.letters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class LettersAdapter extends RecyclerView.Adapter<LettersAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<Letter> letters;

    public LettersAdapter(Activity activity, ArrayList<Letter> letters) {
        this.activity = activity;
        this.letters = letters;
    }

    @Override
    public LettersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(LettersAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return letters.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View v;
        public ViewHolder(View v) {
            super(v);
            this.v = v;
        }
    }
}
