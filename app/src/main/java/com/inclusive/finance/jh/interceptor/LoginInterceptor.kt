package com.inclusive.finance.jh.interceptor

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.alibaba.android.arouter.launcher.ARouter
import com.inclusive.finance.jh.app.MyApplication

@Interceptor(priority = 1, name = "登录拦截器")
class LoginInterceptor : IInterceptor {
    var mContext: Context? = null
    override fun process(postcard: Postcard, callback: InterceptorCallback) {

        // callback.onInterrupt(new RuntimeException("我觉得有点异常"));      // 觉得有问题，中断路由流程
        when {
            postcard.path.contains("StartGBActivity") || postcard.path.contains("GroupPwdActivity") -> when {
                !MyApplication.checkUserLogin() -> {
                    callback.onInterrupt(null)
                    ARouter.getInstance().build("/com/LoginChooseActivity").navigation()
                }
                else -> callback.onContinue(postcard)  // 处理完成，交还控制权
            }
            else -> callback.onContinue(postcard)  // 处理完成，交还控制权
        }
        // 以上两种至少需要调用其中一种，否则不会继续路由
    }

    override fun init(context: Context) {
        mContext = context
        // 拦截器的初始化，会在sdk初始化的时候调用该方法，仅会调用一次
    }
}