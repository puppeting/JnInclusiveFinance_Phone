package com.inclusive.finance.jh.bean

import androidx.databinding.BaseObservable
import java.io.Serializable

/**
 * Created by 史忠文
 * on 2017/8/9.
 */

abstract class KeyAndValueBean : BaseObservable(),Serializable {
    var key: String = ""
        get() = if (absKey().isEmpty()) {
            field
        } else absKey()
    var value = ""
        get() = if (absValue().isEmpty()) {
            field
        } else absValue()
    val isCheck
        get() =absCheck()
    abstract fun absKey(): String
    abstract fun absValue(): String
    abstract fun absCheck(): Boolean
}