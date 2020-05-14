package kim.jeonghyeon.sample.etc.ui.dialog

import android.content.Context
import androidx.appcompat.app.AlertDialog
import kim.jeonghyeon.androidlibrary.extension.toast

object AlertDialogSample {
    fun singleChoiceDialog(context: Context) {
        AlertDialog.Builder(context).setSingleChoiceItems(arrayOf("1","2"), 0) { dialog, which ->
            toast(which.toString())
        }.setTitle("title").show()
    }
}