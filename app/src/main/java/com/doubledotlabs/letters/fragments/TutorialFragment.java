package com.doubledotlabs.letters.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.doubledotlabs.letters.R;

public class TutorialFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tutorial, container, false);

        ((ImageView) v.findViewById(R.id.imageView)).setImageResource(getArguments().getInt("image"));
        ((TextView) v.findViewById(R.id.title)).setText(getArguments().getString("title"));
        ((TextView) v.findViewById(R.id.content)).setText(getArguments().getString("content"));

        return v;
    }
}
