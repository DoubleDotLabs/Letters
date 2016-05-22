package com.doubledotlabs.letters.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doubledotlabs.letters.R;
import com.doubledotlabs.letters.data.Letter;

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
        return new ViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_letter, null));
    }

    @Override
    public void onBindViewHolder(LettersAdapter.ViewHolder holder, int position) {
        ((TextView) holder.v.findViewById(R.id.letter)).setText(letters.get(position).letter);
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
