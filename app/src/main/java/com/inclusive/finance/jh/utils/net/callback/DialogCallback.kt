package com.inclusive.finance.jh.utils.net.callback

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ObjectUtils
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.pop.ConfirmPop
import com.inclusive.finance.jh.ui.login.LoginPasswordActivity
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.net.AbsNetBean
import com.inclusive.finance.jh.utils.net.callback.exception.ApiException
import com.inclusive.finance.jh.widget.CustomProgress
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import java.net.ConnectException
import java.net.SocketTimeoutException


/**
 * ================================================
 * 描    述：对于网络请求是否需要弹出进度对话框
 * 修订历史：
 * ================================================
 */
abstract class DialogCallback<T : AbsNetBean>(
    val context: Context? = null,
    private val isDialog: Boolean? = false, var rootView: View? = null,
) : JsonCallback<T>() {
    companion object {
        var isConfirmPopShow = false
    }

    override fun onStart(request: Request<T, out Request<*, *>>?) {
        super.onStart(request)
        if (isDialog == true) {
            CustomProgress.show(context, "加载中", true, null)
        }
    }

    override fun onFinish() {
        CustomProgress.disMiss()
    }

    open fun onFail(apiError: ApiException) {
        SZWUtils.showSnakeBarError(rootView, apiError.message ?: "")
        if (apiError.errCode == 555 && apiError.errMsg.contains("Token失效")) {
            //            UIHelper.goLoginPage(AppContext.getInstance())
            //            UserMgr.getInstance().isLogin = false

            if (!isConfirmPopShow && context != null && !(context as FragmentActivity).supportFragmentManager.isDestroyed) ConfirmPop(context, "Token失效，请重新登录") {
                ARouter.getInstance().build("/com/LoginPasswordActivity")
                    .withTransition(R.anim.slide_in_bottom, android.R.anim.fade_out)
                    .navigation(context)

                //                Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
                //                if (context is MainActivity) {
                Handler(Looper.getMainLooper()).postDelayed({
                    ActivityUtils.finishOtherActivities(LoginPasswordActivity::class.java)
                    //                        ARouter.getInstance().build("/com/LoginPasswordActivity")
                    //                            .withTransition(R.anim.slide_in_bottom, android.R.anim.fade_out)
                    //                            .navigation()
                }, 450)
                //
                //                }
                isConfirmPopShow = false
            }.show(context.supportFragmentManager, this.javaClass.name)
            isConfirmPopShow = true
        }
    }

    override fun onError(response: Response<T>) {
        super.onError(response)
        CustomProgress.disMiss()
        if (response.exception is ApiException) {
            val apiError = ApiException(response.code(), response.message())
            onFail(response.exception as ApiException)
        } else {
            var errCode = 70001
            var errMsg: String? = "网络错误，请检查网络是否正常"
            if (response.exception != null) {
                when {
                    response.rawResponse != null -> {
                        LogUtils.e(errMsg)
                        errCode = response.rawResponse.code()
                        errMsg = response.rawResponse.message()
                    }
                    response.exception is ConnectException -> {
                        val msg = response.exception.localizedMessage
                        errCode = 70002
                        errMsg = "无法连接服务器，请检查网络是否正常"
                    }
                    response.exception is SocketTimeoutException -> {
                        errCode = 70002
                        errMsg = "连接服务器超时"
                    }
                    else -> {
                        LogUtils.e(errMsg)
                    }
                }
            } else {
                if (response.rawResponse != null) {
                    errCode = response.rawResponse.code()
                    errMsg = response.rawResponse.message()
                }
            }
            if (ObjectUtils.isEmpty(errMsg)) {
                errMsg = "Token失效"
            }
            val apiError = ApiException(errCode, errMsg)
            onFail(apiError)
        }
    }
}