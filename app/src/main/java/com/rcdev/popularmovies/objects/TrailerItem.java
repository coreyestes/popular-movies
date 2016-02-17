package com.rcdev.popularmovies.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by coreyestes
 * 2016 02 04
 */
public class TrailerItem implements Parcelable {

    private String name;
    private String key;

    public TrailerItem(){super();}


    public TrailerItem(String name, String key) {
        this.name = name;
        this.key = key;
    }

    protected TrailerItem(Parcel in) {
        name = in.readString();
        key = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(key);
    }

    public static final Parcelable.Creator<TrailerItem> CREATOR = new Parcelable.Creator<TrailerItem>() {
        @Override
        public TrailerItem createFromParcel(Parcel parcel) {
            return new TrailerItem(parcel);
        }

        @Override
        public TrailerItem[] newArray(int i) {
            return new TrailerItem[i];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
