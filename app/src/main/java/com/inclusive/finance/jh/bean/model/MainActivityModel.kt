package com.inclusive.finance.jh.bean.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.SPUtils
import com.inclusive.finance.jh.bean.KehuBean
import com.inclusive.finance.jh.bean.User
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.pop.AccountPop
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityModel  @Inject constructor(): ViewModel() {
    var userInfo: MutableLiveData<User> = MutableLiveData(User())
    var accountPop: AccountPop?=null
    var idCardNum: String?=null
    var kehuBean: MutableLiveData<KehuBean> = MutableLiveData(KehuBean())
    val isGestureLogin: MutableLiveData<Boolean> by lazy {MutableLiveData(
        SPUtils.getInstance().getBoolean(
            Constants.SPUtilsConfig.ISGESTURELOCK_KEY, false
        ))
    }
    val isFingerLogin: MutableLiveData<Boolean> by lazy {MutableLiveData(
        SPUtils.getInstance().getBoolean(
            Constants.SPUtilsConfig.ISFINGERLOCK_KEY, false
        ))
    }

}