package com.inclusive.finance.jh.bean

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.inclusive.finance.jh.BR

class KehuBean : BaseObservable() {
    @Bindable
    var createTime: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.createTime)
        }

    @Bindable
    var ghr: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.ghr)
        }

    @Bindable
    var ghrjg: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.ghrjg)
        }

    @Bindable
    var id: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.id)
        }

    @Bindable
    var khlx: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.khlx)
        }

    @Bindable
    var khxm: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.khxm)
        }

    @Bindable
    var lxdh: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.lxdh)
        }

    @Bindable
    var sfzfm: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.sfzfm)
        }

    @Bindable
    var sfzzm: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.sfzzm)
        }

    @Bindable
    var state: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.state)
        }

    @Bindable
    var creditId: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.creditId)
        }

    @Bindable
    var ywghq: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.ywghq)
        }

    @Bindable
    var zjdz: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.zjdz)
        }

    @Bindable
    var zjyxqjs: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.zjyxqjs)
        }

    @Bindable
    var zjyxqks: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.zjyxqks)
        }
}