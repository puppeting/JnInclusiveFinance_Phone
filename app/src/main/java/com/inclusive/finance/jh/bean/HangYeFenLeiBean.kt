package com.inclusive.finance.jh.bean

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.inclusive.finance.jh.BR


class HangYeFenLeiBean : BaseObservable(){
    var key = "" //名称
    var value = "" //数值

    @Bindable
    var canCheck = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.checked)
        }
    @Bindable
    var checked = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.checked)
        }


    var title: String=""
    var id: String? = null
    var parentId: String? = null
    var parentKey: String? = null
    var parentValue: String? = null
    var name: String? = null
    var children: ArrayList<HangYeFenLeiBean>? = null

}