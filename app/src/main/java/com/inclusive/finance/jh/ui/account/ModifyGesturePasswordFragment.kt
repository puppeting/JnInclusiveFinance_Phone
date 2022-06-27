package com.inclusive.finance.jh.ui.account

import android.app.Service
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ObjectUtils
import com.blankj.utilcode.util.SPUtils
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.databinding.FragmentModifyGesturePasswordBinding
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.widget.gesture.GestureLockLayout
import com.inclusive.finance.jh.widget.gesture.GestureLockLayout.OnLockResetListener
import org.jetbrains.anko.support.v4.act
import java.util.*

class ModifyGesturePasswordFragment : MyBaseFragment(), View.OnClickListener,
    View.OnFocusChangeListener {
    private var animation: Animation? = null
    lateinit var viewModel:MainActivityModel
    lateinit var viewBind: FragmentModifyGesturePasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBind = FragmentModifyGesturePasswordBinding.inflate(inflater, container, false)
        return viewBind.root
    }

    override fun initView() {
        viewModel = ViewModelProvider(act).get(MainActivityModel::class.java)
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            NavHostFragment.findNavController(this)
                .navigateUp()
        }
        viewBind.tvAccount.text = SPUtils.getInstance().getString(Constants.SPUtilsConfig.SP_PHONE)
        //设置提示view 每行每列点的个数
        viewBind.displayView.setDotCount(3)
        //        //设置提示view 选中状态的颜色
//        display_view.setDotSelectedColor(Color.parseColor("#01367A"));
//        //设置提示view 非选中状态的颜色
//        mLockDisplayView.setDotUnSelectedColor(Color.parseColor("#999999"));
        //设置手势解锁view 每行每列点的个数
        viewBind.gestureView.setDotCount(3)
        //设置手势解锁view 最少连接数
        viewBind.gestureView.setMinCount(4)
        //设置手势解锁view 模式为重置密码模式
        viewBind.gestureView.setMode(GestureLockLayout.RESET_MODE)

        //初始化动画
        animation = AnimationUtils.loadAnimation(activity, R.anim.shake)
        viewBind.tvOk.setOnClickListener(this)
        viewBind.reSet.setOnClickListener(this)
        viewBind.etOkPass.onFocusChangeListener = this
        initEvents()
    }

    /**
     * 重置手势布局（只是布局）
     */
    private fun resetGesture() {
        Handler().postDelayed({ viewBind.gestureView.resetGesture() }, 300)
    }

    private fun initEvents() {
        viewBind.gestureView.setOnLockResetListener(object : OnLockResetListener {
            override fun onConnectCountUnmatched(connectCount: Int, minCount: Int) {
                //连接数小于最小连接数时调用
                viewBind.settingHint.text = "最少连接" + minCount + "个点"
                resetGesture()
            }

            override fun onFirstPasswordFinished(answerList: List<Int>) {
                //第一次绘制手势成功时调用
//                Log.e("TAG", "第一次密码=" + answerList);
                viewBind.settingHint.text = "确认解锁图案"
                //将答案设置给提示view
                viewBind.displayView.setAnswer(answerList)
                //重置
                resetGesture()
            }

            override fun onSetPasswordFinished(isMatched: Boolean, answerList: List<Int>) {
                //第二次密码绘制成功时调用
                Log.e("TAG", "第二次密码=$answerList")
                if (isMatched) {
                    if (SPUtils.getInstance().getString(Constants.SPUtilsConfig.GESTURELOCK_KEY)
                            .isEmpty()
                    ) {
                        context?.let { SZWUtils.showSnakeBarSuccess("设置手势密码成功") }
                        SPUtils.getInstance().put(Constants.SPUtilsConfig.ISGESTURELOCK_KEY, true)
                        viewModel.isGestureLogin.value=true
                    } else {
                        context?.let { SZWUtils.showSnakeBarSuccess("修改手势密码成功") }
                    }
                    // 两次答案一致，保存
                    SPUtils.getInstance().put(Constants.SPUtilsConfig.GESTURELOCK_KEY, answerList.toString())
                    // 关闭
                    NavHostFragment.findNavController(this@ModifyGesturePasswordFragment)
                        .navigateUp()
                } else {
                    viewBind.hintTV.visibility = View.VISIBLE
                    val vib = context?.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
                    vib.vibrate(300)
                    viewBind.hintTV.startAnimation(animation)
                    viewBind.gestureView.startAnimation(animation)
                    resetGesture()
                }
            }
        })
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_ok -> {
                val okPassword: String = viewBind.etOkPass.text.toString().trim()
                if (ObjectUtils.isEmpty(okPassword)) {
                    viewBind.tvErrorMsg.text = "密码不能为空"
                    return
                }
                if (!ObjectUtils.equals(
                        okPassword,
                        SPUtils.getInstance().getString(
                            SPUtils.getInstance().getString(Constants.SPUtilsConfig.SP_PHONE)
                        )
                    )
                ) {
                    viewBind.tvErrorMsg.text = "原密码不正确"
                    return
                }
                KeyboardUtils.hideSoftInput(activity)
                viewBind.llPassword.visibility = View.GONE
                viewBind.clGesture.visibility = View.VISIBLE
            }
            R.id.reSet -> {
                viewBind.gestureView.setOnLockResetListener(null)
                viewBind.settingHint.text = "绘制解锁图案"
                viewBind.displayView.setAnswer(ArrayList<Int>())
                viewBind.gestureView.resetGesture()
                viewBind.gestureView.setMode(GestureLockLayout.RESET_MODE)
                viewBind.hintTV.visibility = View.INVISIBLE
                initEvents()
            }
        }
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
//        if (hasFocus)
//            viewModel.accountPop?.setAdjustInputMode(
//                v.id,
//                BasePopupWindow.FLAG_KEYBOARD_ANIMATE_ALIGN or BasePopupWindow.FLAG_KEYBOARD_FORCE_ADJUST or BasePopupWindow.FLAG_KEYBOARD_ALIGN_TO_VIEW
//            )
    }
}