package com.inclusive.finance.jh.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.blankj.utilcode.util.RegexUtils

/**
 * @author Bananv
 */
object EditTextRegex {
    fun textRegex(errorText: TextView?=null, view: EditText, regex: String?="", regexErrorMsg: String? = "",textChangeListener: ((str:String)->Unit)?=null): TextWatcher {
        val textWatcher = object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int,
            ) {
                textChangeListener?.invoke(s.toString())
                if (!regex.isNullOrEmpty() && !view.text.isNullOrEmpty()) {
                    if (RegexUtils.isMatch(regex, view.text)) {
                        errorText?.visibility = View.GONE
                    } else {
                        errorText?.visibility = View.VISIBLE
                        errorText?.text = regexErrorMsg
                    }
                } else {
                    errorText?.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        }
        view.addTextChangedListener(textWatcher)
        return textWatcher
    }
}