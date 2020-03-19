package kim.jeonghyeon.sample.viewmodel.startactivity

import android.app.Activity
import com.google.common.truth.Truth.assertThat
import kim.jeonghyeon.androidlibrary.architecture.livedata.Resource
import kim.jeonghyeon.androidlibrary.architecture.mvvm.StartActivityResult
import kim.jeonghyeon.testing.BaseViewModelTest
import kim.jeonghyeon.testing.awaitData
import kim.jeonghyeon.testing.awaitResult
import org.junit.Test
import org.koin.test.inject

class StartActivityViewModelTest : BaseViewModelTest() {
    val viewModel by inject<StartActivityViewModel>()

    @Test
    fun onClick_ok() {
        //GIVEN
        viewModel.mockStartActivityResult(StartActivityResult(Activity.RESULT_OK, null))

        //WHEN
        viewModel.onClick()

        //THEN
        assertThat(viewModel.result.awaitData()).isEqualTo("It's success")

    }

    @Test
    fun onClick_cancel() {
        //GIVEN
        viewModel.mockStartActivityResult(StartActivityResult(Activity.RESULT_CANCELED, null))

        //WHEN
        viewModel.onClick()

        //THEN
        assertThat(viewModel.result.awaitResult()).isInstanceOf(Resource.Error::class.java)
    }
}