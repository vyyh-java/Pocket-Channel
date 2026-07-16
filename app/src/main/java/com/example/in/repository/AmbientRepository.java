package com.example.in.repository;

import android.content.Context;
import android.health.connect.datatypes.Metadata;
import android.net.Uri;

import androidx.media3.common.MediaItem;

import com.example.in.R;
import com.example.in.data.entity.Ambient;

import java.util.ArrayList;
import java.util.List;

public class AmbientRepository {

    public List<Ambient> getAmbient(Context context) {
        List<Ambient> ambients = new ArrayList<>();
        String packageName = context.getPackageName();

        //set data
        ambients.add(new Ambient(R.raw.rain, "Rain", convertIntoUrl("rain", packageName)));
        ambients.add(new Ambient(R.raw.forest, "Forest", convertIntoUrl("forest", packageName)));
        ambients.add(new Ambient(R.raw.camp, "Night", convertIntoUrl("camp", packageName)));

        return ambients;
    }

    public MediaItem convertIntoMediaItem(Ambient ambient){
        return new MediaItem.Builder()
                .setMediaId(String.valueOf(ambient.getResId()))
                .setUri(Uri.parse(ambient.getResUrl()))
                .build();
    }

    /*
    rain, forest, night/camp
    *
    **/

    private String convertIntoUrl(String rawName, String packageName){
        return "android.resource://" + packageName + "/raw/"+rawName;
    }

}
