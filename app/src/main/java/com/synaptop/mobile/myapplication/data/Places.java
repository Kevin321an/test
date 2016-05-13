package com.synaptop.mobile.myapplication.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by FM on 5/11/2016.
 */
public class Places implements Parcelable {

    public static final Parcelable.Creator<Places> CREATOR =
            new Parcelable.Creator<Places>() {
                @Override
                public Places createFromParcel(Parcel in) {
                    return new Places(in);
                }

                @Override
                public Places[] newArray(int size) {
                    return new Places[size];
                }
            };

    public float lng;
    public float lat;
    public String url;
    public String title;
    public String available;



    public Places(float lng, float lat, String url, String title) {
        this.lng = lng;
        this.lat = lat;
        this.url = url;
        this.title = title;
    }
    public Places(float lng, float lat, String url, String title, String available) {
        this.lng = lng;
        this.lat = lat;
        this.url = url;
        this.title = title;
        this.available = available;
    }

    private Places(Parcel input) {
        lng = input.readFloat();
        lat = input.readFloat();
        url = input.readString();
        title = input.readString();
        available = input.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeFloat(lng);
        out.writeFloat(lat);
        out.writeString(url);
        out.writeString(title);
        out.writeString(available);
    }
    @Override
    public String toString() {
        return "lng: " + lng + "lat: " + lat + "id: " + title + "\n"
                + this.title + "\n"
                + this.url + "\n"
                + this.lng + "\n"
                + this.lng + "\n";

    }


}
