package kim.jeonghyeon.sample.view.parcelable

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class ParcelableData<O : ParcelableOption>(
    val int: Int,
    val action: Serializable,//be careful, there can be memery leak
    val option: O,
    val option2: ParcelableOption

) : Parcelable {
    fun action() {
        action as () -> Unit
        action.invoke()
    }

    companion object {
        fun <O : ParcelableOption> create(
            int: Int,
            action: () -> Unit,
            option: O
        ): ParcelableData<O> =
            ParcelableData(int, action as Serializable, option, option)
    }
}

interface ParcelableOption : Parcelable

@Parcelize
data class TestOpion(val isTest: Boolean, val testName: String) : ParcelableOption