package kim.jeonghyeon.sample.etc.web

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.provider.MediaStore
import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kim.jeonghyeon.androidlibrary.BuildConfig
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * file choose by webapp request
 */
class FileSelectableWebChromeClient(
    val activity: AppCompatActivity,
    val fileChooserTitle: String,
    val needCompress: Boolean = false
) : SampleWebChromeClient(activity) {

    val REQUEST_CODE_WEBVIEW_FILE_CHOOSER = 2033

    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    lateinit var tempFileForImageCompress: File
    lateinit var tempFileForImageCompressUri: Uri

    override fun onShowFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>,
        fileChooserParams: FileChooserParams
    ): Boolean {
        this.filePathCallback?.onReceiveValue(null)
        this.filePathCallback = null
        this.filePathCallback = filePathCallback

        tempFileForImageCompress = getTempImageFile()
        tempFileForImageCompressUri = getFilePathUri(tempFileForImageCompress)
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileForImageCompressUri)

        val i = Intent().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                type = fileChooserParams.acceptTypes?.takeIf { it.isNotEmpty() }?.get(0)
                    ?: "image/*"
                action = Intent.ACTION_OPEN_DOCUMENT
                addCategory(Intent.CATEGORY_OPENABLE)
            } else {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
        }

        // Create file chooser intent
        Intent.createChooser(i, fileChooserTitle).apply {
            // Set camera intent to file chooser
            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(captureIntent))
        }.let {
            // On select image call onActivityResult method of activity
            activity.startActivityForResult(it, REQUEST_CODE_WEBVIEW_FILE_CHOOSER)
        }
        return true
    }

    private fun getTempImageFile(): File = File(activity.externalCacheDir, "webview")
        .apply {
            if (!this.exists()) {
                this.mkdirs()
            }
        }
        .run {
            File(this, "IMG_" + System.currentTimeMillis() + ".jpg")
        }

    private fun getFilePathUri(file: File): Uri =
        file.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    activity,
                    BuildConfig.APPLICATION_ID + ".fileProvider",
                    it
                )
            } else {
                Uri.fromFile(it)
            }
        }

    private fun resizeImage(uri: Uri, scaleTo: Int = 1024, maxSizeBytes: Int = 200 * 1024): Uri {
        val contentResolver = activity.contentResolver
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true

        contentResolver.openFileDescriptor(uri, "r").use { pfd ->
            pfd?.fileDescriptor.let { fd ->
                BitmapFactory.decodeFileDescriptor(fd, null, bmOptions)
                val photoW = bmOptions.outWidth
                val photoH = bmOptions.outHeight

                // Determine how much to scale down the image
                val scaleFactor = if (photoW < scaleTo || photoH < scaleTo) 1 else Math.min(
                    photoW / scaleTo,
                    photoH / scaleTo
                )

                bmOptions.inJustDecodeBounds = false
                bmOptions.inSampleSize = scaleFactor

                val resized = BitmapFactory.decodeStream(
                    contentResolver.openInputStream(uri),
                    null,
                    bmOptions
                )
                    ?: return uri

                ByteArrayOutputStream().use { baos ->
                    var currSize: Int
                    var currQuality = 100

                    do {
                        baos.flush()
                        baos.reset()

                        resized.compress(Bitmap.CompressFormat.JPEG, currQuality, baos)
                        currSize = baos.toByteArray().size
                        // limit quality by 5 percent every time
                        currQuality -= 5
                    } while (currSize >= maxSizeBytes && currQuality > 5)

                    tempFileForImageCompress.outputStream().use { fos ->
                        fos.write(baos.toByteArray())
                    }
                    resized.recycle()
                }
            }
        }
        return tempFileForImageCompressUri
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_WEBVIEW_FILE_CHOOSER) {
            var result: Array<Uri>? = null
            val dataString = data?.dataString
            if (resultCode == RESULT_OK) {
                result =
                    arrayOf(
                        (dataString?.let { Uri.parse(dataString) }
                            ?: tempFileForImageCompressUri).run {
                            if (needCompress) resizeImage(this) else this
                        })
            }

            filePathCallback?.onReceiveValue(result)
            filePathCallback = null
        }
    }
}