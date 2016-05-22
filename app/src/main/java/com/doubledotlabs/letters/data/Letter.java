package com.doubledotlabs.letters.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Letter implements Parcelable {

    public String letter;
    public double x, y;
    public boolean found;

    public Letter(String letter, double x, double y) {
        this.letter = letter;
        this.x = x;
        this.y = y;
    }

    protected Letter(Parcel in) {
        letter = in.readString();
        x = in.readDouble();
        y = in.readDouble();
        found = in.readInt() == 1;
    }

    public static final Creator<Letter> CREATOR = new Creator<Letter>() {
        @Override
        public Letter createFromParcel(Parcel in) {
            return new Letter(in);
        }

        @Override
        public Letter[] newArray(int size) {
            return new Letter[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(letter);
        dest.writeDouble(x);
        dest.writeDouble(y);
        dest.writeInt(found ? 1 : 0);
    }
}
