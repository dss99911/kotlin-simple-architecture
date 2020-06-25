package com.example.sampleandroid.library

import androidx.compose.Composable
import androidx.compose.CompositionLifecycleObserver
import androidx.compose.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class ComposableCoroutineScopeImpl(
    val context: CoroutineContext
) : CoroutineScope, CompositionLifecycleObserver {
    val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main.immediate + job

    override fun onEnter() {}
    override fun onLeave() = job.cancel()
}

@Composable
fun composableCoroutineScope(
    context: CoroutineContext = EmptyCoroutineContext
): CoroutineScope = remember {
    ComposableCoroutineScopeImpl(context)
}