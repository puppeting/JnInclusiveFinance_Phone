package com.inclusive.finance.jh.pop


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ScreenUtils
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.databinding.PopAccountBinding
import com.inclusive.finance.jh.utils.StatusBarUtil
import org.jetbrains.anko.support.v4.act

class AccountPop :DialogFragment() {
    lateinit var dataBind: PopAccountBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明

        dataBind = PopAccountBinding.inflate(inflater, container, false)
        return dataBind.root
    }
    private lateinit var viewModel: MainActivityModel
    override fun onStart() {
        super.onStart()
        StatusBarUtil.immersive(dialog?.window)
        //        setStyle(STYLE_NO_TITLE,R.style.MyDialog)
        val params = dialog?.window?.attributes
        dialog?.setCanceledOnTouchOutside(true)
        params?.width = ScreenUtils.getScreenWidth()
        params?.height = RelativeLayout.LayoutParams.MATCH_PARENT
        params?.gravity = Gravity.CENTER
        //高度自己定义
        dialog?.window?.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        RxBus.get().register(this)
        viewModel = ViewModelProvider(act)[MainActivityModel::class.java]
        viewModel.isGestureLogin.value = SPUtils.getInstance().getBoolean(
            Constants.SPUtilsConfig.ISGESTURELOCK_KEY, false
        )
    }
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag("dismiss")])
    fun dismiss(arg: String) {
        dismiss()
    }

}