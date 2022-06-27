package com.inclusive.finance.jh.bean.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.SPUtils
import com.inclusive.finance.jh.BR
import com.inclusive.finance.jh.config.Constants

open class User : BaseObservable() {
    var list:ArrayList<User>?= arrayListOf()
    @Bindable
    var num: Int? = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.num)
        }

    fun add() {
        num = num?.plus(1)
    }
}

open class MyViewModel : ViewModel() {

    var isGestureLogin: MutableLiveData<Boolean> = MutableLiveData(
        SPUtils.getInstance().getBoolean(
            Constants.SPUtilsConfig.ISGESTURELOCK_KEY, false
        )
    )
    val user: MutableLiveData<User> = MutableLiveData(User())
    val list: MutableLiveData<ArrayList<User>> by lazy {
        val mutableLiveData = MutableLiveData<ArrayList<User>>()
        mutableLiveData.value = ArrayList()
        mutableLiveData.value?.add(User())
        mutableLiveData.value?.add(User())
        mutableLiveData.value?.add(User())
        mutableLiveData
    }

}