package kim.jeonghyeon.sample.framework.job

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import androidx.annotation.RequiresApi
import org.jetbrains.anko.doAsync

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class SampleJobService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        //it is called on main thread

        doAsync {
            //process task
            jobFinished(params, true)
        }

        return true // need to call jobFinished(). true means this side will call jobFinished().
//        return false // job completed. so, can destroy the service
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        //onStopJob is called when system decides to stop your job forcefully before it finishes

        return true //reschedule the current job
//        return false//end the job, and later next job will be processed
    }
}