package com.balancehero.example.androidtesting;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.runner.screenshot.ScreenCapture;
import androidx.test.runner.screenshot.Screenshot;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.IOException;
import java.util.Locale;

/**
 * todo there is bug that screenshot is took before ui is drawn.
 */
public class ScreenshotWatcher extends TestWatcher {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void succeeded(Description description) {
        Locale locale = ApplicationProvider.getApplicationContext()
                .getResources()
                .getConfiguration()
                .getLocales()
                .get(0);
        captureScreenshot(description.getMethodName() + "_" + locale.toLanguageTag());
    }


    private void captureScreenshot(String name) {
        ScreenCapture capture = Screenshot.capture();
        capture.setFormat(Bitmap.CompressFormat.PNG);
        capture.setName(name);
        try {
            capture.process();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }


    @Override
    protected void failed(Throwable e, Description description) {
        captureScreenshot(description.getMethodName() + "_fail");
    }
}