package kim.jeonghyeon.androidlibrary.sample.storage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    companion object {
        private const val PICTURES_DIR_ACCESS_REQUEST_CODE = 42
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accessExternalPicturesDirectory()
    }

    private fun accessExternalPicturesDirectory() {
        val intent: Intent = (getSystemService(Context.STORAGE_SERVICE) as StorageManager)
            .primaryStorageVolume.createAccessIntent(Environment.DIRECTORY_PICTURES)
        startActivityForResult(intent, PICTURES_DIR_ACCESS_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == PICTURES_DIR_ACCESS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            // User approved access to scoped directory.
            if (resultData != null) {
                val picturesDirUri: Uri = resultData.data

                // Save user's approval for accessing this directory
                // in your app.
                contentResolver.takePersistableUriPermission(
                    picturesDirUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }
    }
}