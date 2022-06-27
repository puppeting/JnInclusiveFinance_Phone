package com.inclusive.finance.jh.ui.creditmgr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentLgfkBinding
import com.inclusive.finance.jh.ui.apply.credit.CreditFaceFragment
import com.inclusive.finance.jh.ui.investigation.VodFragment
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act

/**
 * 离柜放款
 * */
class LGFKFragment : MyBaseFragment() {
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentLgfkBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentLgfkBinding.inflate(inflater, container, false).apply {
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        viewModel.title = "离柜放款"
        var chipId = R.id.bt_lwhc
        viewBind.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            /***/ //            获取当前展示的fragment
            val fragment = childFragmentManager.primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment
            var isShowingFragment = false //是否是当前显示的fragment。

            val findNavController = Navigation.findNavController(act, R.id.apply_nav_host_fragment_lgfk)
            val fragmentId = when (checkedId) {
                R.id.bt_lwhc -> {
                    viewModel.title = "联网核查"

                    isShowingFragment = fragment is LWHCFragment
                    R.id.LWHCFragment
                }
                R.id.bt_face -> {
                    if (viewModel.applyCheckBean?.completeCheckBean?.lgfkSecondLevelBean?.lwhcCheck != true) {
                        group.check(chipId)
                        SZWUtils.showSnakeBarMsg("请先完善联网核查")
                        return@setOnCheckedChangeListener
                    }
                    viewModel.title = "人脸识别"
                    isShowingFragment = fragment is CreditFaceFragment
                    R.id.CreditFaceFragment
                }
                R.id.bt_lylx -> {
                    if (viewModel.applyCheckBean?.completeCheckBean?.lgfkSecondLevelBean?.rlsbCheck != true) {
                        group.check(chipId)
                        SZWUtils.showSnakeBarMsg("请先完善人脸识别")
                        return@setOnCheckedChangeListener
                    }
                    viewModel.title = "录音录像"
                    isShowingFragment = fragment is VodFragment
                    R.id.VodFragment
                }
                R.id.bt_jjsc -> {
                    if (viewModel.applyCheckBean?.completeCheckBean?.lgfkSecondLevelBean?.lylxCheck != true) {
                        group.check(chipId)
                        SZWUtils.showSnakeBarMsg("请先完善录音录像")
                        return@setOnCheckedChangeListener
                    }
                    viewModel.title = "借据上传"
                    isShowingFragment = fragment is JJSCFragment
                    R.id.JJSCFragment
                }
                R.id.bt_lgfkyzd -> {
//                    if (viewModel.applyCheckBean?.completeCheckBean?.lgfkSecondLevelBean?.jjscCheck != true) {
//                        group.check(chipId)
//                        SZWUtils.showSnakeBarMsg("请先完善借据上传")
//                        return@setOnCheckedChangeListener
//                    }
                    //如果离柜放款验证单不能看就请求一下，弹个结果就行。
                    if (viewModel.applyCheckBean?.completeCheckBean?.lgfkSecondLevelBean?.lgfkyzdCheck != true) {
                        group.check(chipId)
                        DataCtrlClass.CreditManagementNet.getLWHCList(context, Urls.get_yxLgfkyzd, viewModel.keyId, businessType = SZWUtils.getBusinessType(viewModel.businessType)){

                        }
                        return@setOnCheckedChangeListener
                    }
                    viewModel.title = "离柜放款验证单"
                    isShowingFragment = fragment is LGFKYZDFragment
                    R.id.LGFKYZDFragment
                }
                else -> {
                    null
                }
            }
            /***/ // 如果重复点击。就刷新数据，保留选择状态
            if (checkedId != -1) {
                chipId = checkedId
            } else {
                group.check(chipId)
                return@setOnCheckedChangeListener
            }
            if (fragmentId != null) {
                val build = NavOptions.Builder().setPopUpTo(fragmentId, true).build()
                if (isShowingFragment) (fragment as MyBaseFragment).refreshData()
                else findNavController.navigate(fragmentId, null, build)
            }
        }
    }
}