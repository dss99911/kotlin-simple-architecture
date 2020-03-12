package kim.jeonghyeon.sample.etc.job

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService

/**
 * add the below on Manifest.
 * Todo but looks not mandatory for JobIntentService.(maybe it is required for Alarm case)
 * android:permission="android.permission.BIND_JOB_SERVICE"
 *
 */
class SampleJobIntentService : JobIntentService() {
    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                context,
                SampleJobIntentService::class.java,
                JobServiceConstant.JobID.SAMPLE,
                intent
            )
        }
    }

    override fun onHandleWork(intent: Intent) {
        TODO("process Long time task")
    }
}