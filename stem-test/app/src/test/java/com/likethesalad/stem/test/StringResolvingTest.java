package com.likethesalad.stem.test;

import static org.junit.Assert.assertEquals;

import android.app.Application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class StringResolvingTest {

    @Test
    public void check_welcomeMessageStringIsResolved() {
        Application application = RuntimeEnvironment.getApplication();

        String welcomeMessage = application.getString(R.string.welcome_message);

        assertEquals("Welcome to Stem Test", welcomeMessage);
    }
}
