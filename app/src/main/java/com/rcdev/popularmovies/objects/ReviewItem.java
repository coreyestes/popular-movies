package com.rcdev.popularmovies.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by coreyestes
 * 2016 02 04
 */
public class ReviewItem implements Parcelable {

    private String author;
    private String content;

    public ReviewItem(){super();}

    public ReviewItem(String author, String content) {
        this.author = author;
        this.content = content;

    }

    protected ReviewItem(Parcel in) {
        author = in.readString();
        content = in.readString();
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(content);
    }

    public static final Parcelable.Creator<ReviewItem> CREATOR = new Parcelable.Creator<ReviewItem>() {
        @Override
        public ReviewItem createFromParcel(Parcel parcel) {
            return new ReviewItem(parcel);
        }

        @Override
        public ReviewItem[] newArray(int i) {
            return new ReviewItem[i];
        }
    };

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

