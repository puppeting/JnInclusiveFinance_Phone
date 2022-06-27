package com.inclusive.finance.jh.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.blankj.utilcode.util.ObjectUtils
import com.blankj.utilcode.util.SPUtils
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.app.MyApplication
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.User
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.databinding.FragmentModifyPasswordBinding
import com.inclusive.finance.jh.ui.NavActivity
import org.jetbrains.anko.support.v4.act

class ModifyPasswordFragment : MyBaseFragment(), View.OnFocusChangeListener {
    private lateinit var viewModel:MainActivityModel
    lateinit var viewBind: FragmentModifyPasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBind = FragmentModifyPasswordBinding.inflate(inflater, container, false)
        return viewBind.root
    }


    override fun initView() {
        viewModel = ViewModelProvider(act).get(MainActivityModel::class.java)
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            NavHostFragment.findNavController(this)
                .navigateUp()
        }
        if (isAdded)
        if (act is NavActivity){
            viewBind.actionBarCustom.root.visibility=View.GONE
        }
        viewBind.tvAccount.text = SPUtils.getInstance().getString(Constants.SPUtilsConfig.SP_PHONE)
        viewBind.tvOk.setOnClickListener {
            val oldPassword: String = viewBind.etOld.text.toString().trim()
            val newPassword: String =viewBind.etNewPass.text.toString().trim()
            val okPassword: String = viewBind.etOkPass.text.toString().trim()
            if (ObjectUtils.isEmpty(oldPassword) || ObjectUtils.isEmpty(newPassword) || ObjectUtils.isEmpty(
                    okPassword
                )
            ) {
                viewBind.tvErrorMsg.text = "密码不能为空"
                return@setOnClickListener
            }
            if (!ObjectUtils.equals(
                    oldPassword,
                    SPUtils.getInstance().getString(
                        SPUtils.getInstance().getString(Constants.SPUtilsConfig.SP_PHONE)
                    )
                )
            ) {
                viewBind.tvErrorMsg.text = "原密码不正确"
                return@setOnClickListener
            }
            if (!ObjectUtils.equals(newPassword, okPassword)) {
                viewBind.tvErrorMsg.text = "两次输入密码不一致"
                return@setOnClickListener
            }

            viewBind.tvErrorMsg.text =""
            DataCtrlClass.LoginNet.changePasswordForApp(context,(MyApplication.user as User).userInfo?.username?:"",viewBind.tvAccount.text.toString(),newPassword,viewBind.root){
                if (it!=null){
                    SPUtils.getInstance().put(
                        SPUtils.getInstance().getString(Constants.SPUtilsConfig.SP_PHONE),
                        okPassword
                    )
                    if (isAdded)
                    if (act is NavActivity){
                        act.finish()
                    }else{
                        NavHostFragment.findNavController(this)
                            .navigateUp()
                    }
                }
            }


        }

       viewBind.etOld.onFocusChangeListener = this
       viewBind.etNewPass.onFocusChangeListener = this
       viewBind.etOkPass.onFocusChangeListener = this
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
//        if (hasFocus)
//            viewModel.accountPop?.setAdjustInputMode(
//                v.id,
//                BasePopupWindow.FLAG_KEYBOARD_ANIMATE_ALIGN or BasePopupWindow.FLAG_KEYBOARD_FORCE_ADJUST or BasePopupWindow.FLAG_KEYBOARD_ALIGN_TO_VIEW
//            )
    }


}