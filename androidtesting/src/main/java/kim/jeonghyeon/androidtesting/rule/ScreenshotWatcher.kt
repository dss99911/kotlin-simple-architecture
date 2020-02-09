package kim.jeonghyeon.androidtesting.rule

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.screenshot.Screenshot
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.IOException

/**
 * todo there is bug that screenshot is took before ui is drawn.
 */
class ScreenshotWatcher : TestWatcher() {
    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun succeeded(description: Description) {
        val locale =
            ApplicationProvider.getApplicationContext<Context>()
                .resources
                .configuration
                .locales[0]
        captureScreenshot(description.methodName + "_" + locale.toLanguageTag())
    }

    private fun captureScreenshot(name: String) {
        val capture = Screenshot.capture()
        capture.format = Bitmap.CompressFormat.PNG
        capture.name = name
        try {
            capture.process()
        } catch (ex: IOException) {
            throw IllegalStateException(ex)
        }
    }

    override fun failed(
        e: Throwable,
        description: Description
    ) {
        captureScreenshot(description.methodName + "_fail")
    }
}