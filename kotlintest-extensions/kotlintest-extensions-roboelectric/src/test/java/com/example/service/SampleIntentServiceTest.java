package com.example.service;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.example.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboSharedPreferences;

import static org.junit.Assert.assertNotSame;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class SampleIntentServiceTest {

    @Test
    public void addsDataToSharedPreference() {
        Application application = RuntimeEnvironment.application;
        RoboSharedPreferences preferences = (RoboSharedPreferences) application
                .getSharedPreferences("example", Context.MODE_PRIVATE);
        Intent intent =  new Intent(application, SampleIntentService.class);
        SampleIntentService registrationService = new SampleIntentService();

        registrationService.onHandleIntent(intent);

        assertNotSame("", preferences.getString("SAMPLE_DATA", ""), "");
    }
}
