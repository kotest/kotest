package com.example.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;


public class SampleIntentService extends IntentService {
    public SampleIntentService() {
        super("SampleIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(
                "example", Context.MODE_PRIVATE).edit();
        editor.putString("SAMPLE_DATA", "sample data");
        editor.apply();
    }
}
