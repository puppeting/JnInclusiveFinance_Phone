package com.inclusive.finance.jh.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class PhoneNumberTextWatcher(private val editText: EditText, private var listener:(b:Boolean)->Unit) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s == null || s.isEmpty()) return
        var group = StringBuilder()
        val str =s.toString().replace("+86","")
        for (i in 0 until str.length) {
            val a = str[i]
            if (a in '0'..'9') {
                group.append(a)
            }
        }
        if (group.length > 11) {
            group = StringBuilder(group.substring(0, 11))
        }
        val sb = StringBuilder()
        for (i in 0 until group.length) {
            if (i == 3 || i == 8 || group[i] != ' ') {
                sb.append(group[i])
                if ((sb.length == 4 || sb.length == 9) && sb[sb.length - 1] != ' ') {
                    sb.insert(sb.length - 1, ' ')
                }
            }
        }
        if (sb.toString() != s.toString()) {
            editText.setText(sb.toString())
            editText.setSelection(sb.length)
        }
        listener.invoke(sb.length>=13)
    }

    override fun afterTextChanged(s: Editable) {

    }

}