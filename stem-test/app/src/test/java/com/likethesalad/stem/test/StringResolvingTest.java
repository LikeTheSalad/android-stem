package com.likethesalad.stem.test;

import static org.junit.Assert.assertEquals;

import android.app.Application;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.StyleSpan;
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

    @Test
    public void check_welcomeMessageStringIsResolved_bold() {
        Application application = RuntimeEnvironment.getApplication();

        Spanned control = (SpannedString) application.getText(R.string.tag_control);
        Spanned welcomeMessage = (SpannedString) application.getText(R.string.welcome_message_bold);

        compareSpanned(createStringWithBoldStyle("Welcome to something bold", 11, 25), control);
        compareSpanned(createStringWithBoldStyle("Welcome to Stem Test", 11, 20), welcomeMessage);
    }

    private void compareSpanned(Spanned expected, Spanned actual) {
        // Verify content
        assertEquals(expected.toString(), actual.toString());

        // Verify spans
        Object[] expectedSpans = getAllSpans(expected);
        Object[] actualSpans = getAllSpans(actual);
        assertEquals(expectedSpans.length, actualSpans.length);

        for (int i = 0; i < expectedSpans.length; i++) {
            StyleSpan expectedSpan = (StyleSpan) expectedSpans[i];
            StyleSpan actualSpan = (StyleSpan) actualSpans[i];
            int expectedStart = expected.getSpanStart(expectedSpan);
            int expectedEnd = expected.getSpanEnd(expectedSpan);
            int actualStart = actual.getSpanStart(actualSpan);
            int actualEnd = actual.getSpanEnd(actualSpan);
            assertEquals(expectedStart, actualStart);
            assertEquals(expectedEnd, actualEnd);
            assertEquals(expectedSpan.getSpanTypeId(), actualSpan.getSpanTypeId());
            assertEquals(expectedSpan.describeContents(), actualSpan.describeContents());
            assertEquals(expectedSpan.getFontWeightAdjustment(), actualSpan.getFontWeightAdjustment());
            assertEquals(expectedSpan.getStyle(), actualSpan.getStyle());
        }
    }

    private static Object[] getAllSpans(Spanned first) {
        return first.getSpans(0, first.length() - 1, Object.class);
    }

    private Spanned createStringWithBoldStyle(String text, int boldStart, int boldEnd) {
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), boldStart, boldEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }
}
