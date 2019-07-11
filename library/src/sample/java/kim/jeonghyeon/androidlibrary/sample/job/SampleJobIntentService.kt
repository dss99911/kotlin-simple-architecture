package kim.jeonghyeon.androidlibrary.sample.job

import android.content.Intent
import androidx.core.app.JobIntentService

class SampleJobIntentService : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        //process long task in queue on working thread.
    }
}