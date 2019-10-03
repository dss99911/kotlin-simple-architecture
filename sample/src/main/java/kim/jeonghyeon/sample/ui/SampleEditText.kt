package kim.jeonghyeon.sample.ui

import android.text.InputFilter
import android.view.inputmethod.EditorInfo
import android.widget.EditText

class SampleEditText {
    fun limitLength(editText: EditText) {
        editText.filters = arrayOf(InputFilter.LengthFilter(250))
    }

    fun inputType(editText: EditText) {
        editText.inputType = EditorInfo.TYPE_CLASS_NUMBER
        editText.inputType = EditorInfo.TYPE_CLASS_TEXT
    }
}