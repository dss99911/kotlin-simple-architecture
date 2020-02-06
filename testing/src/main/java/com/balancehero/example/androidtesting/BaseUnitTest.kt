package com.balancehero.example.androidtesting

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.test.KoinTest

/**
 * while unit test, need to start koin module and stop.
 */
@ExperimentalCoroutinesApi
abstract class BaseUnitTest : KoinTest {
    abstract val modules: List<Module>

    @get:Rule
    val rule = InstantTaskExecutorRule()


    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun baseBefore() {
        startKoin {
            printLogger()
            modules(modules)
        }
    }

    @After
    fun baseAfter() {
        stopKoin()
    }
}