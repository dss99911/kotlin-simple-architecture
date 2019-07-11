package kim.jeonghyeon.androidlibrary.sample.job

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Intent
import androidx.core.app.JobIntentService
import kim.jeonghyeon.androidlibrary.extension.ctx
import kim.jeonghyeon.androidlibrary.extension.jobScheduler
import java.util.concurrent.TimeUnit

class JobschedulerSample {
    companion object {
        const val JOB_ID_SAMPLE = 1
        const val JOB_ID_SAMPLE_INTENT = 2
        val INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(10)
    }

    fun schedulePeriodic(): Boolean {
        //if already exists, ignore it.
        if (jobScheduler.getPendingJob(JOB_ID_SAMPLE) != null) {
            return true
        }

        return ComponentName(ctx, SampleJobService::class.java)
                .let { JobInfo.Builder(JOB_ID_SAMPLE, it) }
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)//service will be registered on boot
                .setPeriodic(INTERVAL_MILLIS)
                .build()
                .let { jobScheduler.schedule(it) } == JobScheduler.RESULT_SUCCESS
    }

    fun enqueWork() {
        JobIntentService.enqueueWork(ctx, SampleJobIntentService::class.java, JOB_ID_SAMPLE_INTENT, Intent()/*input the data to deliver */)
    }
}