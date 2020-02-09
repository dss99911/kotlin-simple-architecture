package com.example.android.architecture.blueprints.todoapp

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseFragment
import kim.jeonghyeon.androidtesting.BaseFragmentTest

abstract class TodoFragmentTest<T : BaseFragment> : BaseFragmentTest<T>() {
    override val theme: Int = R.style.AppTheme
}