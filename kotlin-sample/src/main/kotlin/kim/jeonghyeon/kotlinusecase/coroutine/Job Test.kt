package kim.jeonghyeon.kotlinusecase.coroutine

import kotlinx.coroutines.Job

/**
 * Job is tree structure
 * when corountine is built by launch() or await(). child job is added to parent job
 * and process wait for job completed, all the child job should be completed.
 *
 */
fun test() {
    Job()
}