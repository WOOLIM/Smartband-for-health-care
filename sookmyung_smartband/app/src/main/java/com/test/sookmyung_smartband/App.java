package com.test.sookmyung_smartband;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.beardedhen.androidbootstrap.TypefaceProvider;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ActiveAndroid.initialize(this);
        ActiveAndroid.setLoggingEnabled(false);

        TypefaceProvider.registerDefaultIconSets();
    }
}
