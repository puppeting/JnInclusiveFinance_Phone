package com.inclusive.finance.jh.bean.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.SPUtils
import com.inclusive.finance.jh.config.Constants

class LoginPasswordModel: ViewModel() {
    var isGestureLogin: MutableLiveData<Boolean> = MutableLiveData(
        SPUtils.getInstance().getBoolean(
            Constants.SPUtilsConfig.ISGESTURELOCK_KEY, false
        )
    )
    var isLoginGesture: MutableLiveData<Boolean> = MutableLiveData(
        SPUtils.getInstance().getBoolean(
            Constants.SPUtilsConfig.ISGESTURELOCK_KEY, false
        )
    )
    var isLoginFinger: MutableLiveData<Boolean> = MutableLiveData(
        SPUtils.getInstance().getBoolean(
            Constants.SPUtilsConfig.ISFINGERLOCK_KEY, false
        )
    )
    var isFingerLogin: MutableLiveData<Boolean> = MutableLiveData(
        SPUtils.getInstance().getBoolean(
            Constants.SPUtilsConfig.ISFINGERLOCK_KEY, false
        )
    )
}