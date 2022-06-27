package com.inclusive.finance.jh.ui.apply.bank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions.Builder
import androidx.navigation.Navigation
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.PreferencesService
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentBankDataBinding
import org.jetbrains.anko.support.v4.act

/**
 * 我行数据
 * */
class BankDataFragment : MyBaseFragment() {
    private   var mgroup: ChipGroup?=null
    private var json2: String? = ""
    private var json1: String? = ""
    private var mjson: String? = ""
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentBankDataBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentBankDataBinding.inflate(inflater, container, false).apply {
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        viewModel.title = "汇总信息"
        viewBind.btDetail.visibility = when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_VISIT_NEW,
            ApplyModel.BUSINESS_TYPE_VISIT_EDIT,
            ApplyModel.BUSINESS_TYPE_PRECREDIT,
            -> {
                View.GONE
            }
            else -> {
                View.VISIBLE
            }
        }
         when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_APPLY,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE
            -> {
                viewBind.chipGroup1.visibility =  View.VISIBLE
                getJtcy()
            }
            else -> {
                viewBind.chipGroup1.visibility = View.GONE
            }
        }
        var chipId1 = R.id.bt_simple1
        viewBind.chipGroup1.check(chipId1)
        viewBind.chipGroup1.setOnCheckedChangeListener { group, checkedId ->
            //            获取当前展示的fragment
//            val fragment = childFragmentManager.primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment
//            var isShowingFragment = false //是否是当前显示的fragment。
            //如果重复点击。就刷新数据，保留选择状态
            if (chipId1 != -1) chipId1 = checkedId
            else {
                group.check(chipId1)
                return@setOnCheckedChangeListener
            }
              when (checkedId) {
                R.id.bt_simple1 -> {
                    context?.let {
                        PreferencesService.saveJson(
                            it, json1.toString()
                        )
                    }
                    navTo(mgroup,R.id.bt_simple)
                }

                R.id.bt_detail1 -> {
                    context?.let {
                        PreferencesService.saveJson(
                            it, json2.toString()
                        )
                    }
                    navTo(mgroup,R.id.bt_detail)

                }

                else -> {
                    null
                }
            }

        }


    }
    fun getJtcy(){
        DataCtrlClass.ApplyNet.getJiaTing(requireActivity(), url = Urls.getJtxx, keyId=viewModel.keyId, viewModel.jsonObject) {
            if (it != null) {
                mjson= Gson().toJson(it[0])
                context?.let {
                    PreferencesService.saveJson(
                        it, mjson.toString()
                    )
                }
                if(it?.size!! >1){
                    json2= Gson().toJson(it[1])
                    viewBind?.btDetail1?.visibility=View.VISIBLE
                }else{
                    viewBind?.btDetail1?.visibility=View.GONE
                }
                viewBind.chipGroup.setOnCheckedChangeListener { group, checkedId ->
                    navTo(group, checkedId)
                }
                 if (isAdded)
                    (activity as BaseActivity).refreshData()
            }
        }
    }

    private fun navTo(group: ChipGroup?, checkedId: Int) {
        mgroup = group
        var chipId = R.id.bt_simple
        //            获取当前展示的fragment
        val fragment = childFragmentManager.primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment
        var isShowingFragment = false //是否是当前显示的fragment。
        //如果重复点击。就刷新数据，保留选择状态
        if (checkedId != -1) chipId = checkedId
        else {
            group?.check(chipId)
            return
        }
        val findNavController = Navigation.findNavController(act, R.id.apply_nav_host_fragment_bank_data)
        val fragmentId = when (checkedId) {
            R.id.bt_simple -> {
                viewModel.title = "汇总信息"
                isShowingFragment = fragment is BankDataBaseFragment
                R.id.guaranteePersonalFragment
            }

            R.id.bt_detail -> {
                viewModel.title = "明细"
                isShowingFragment = fragment is BankDataBaseFragment
                R.id.guaranteePersonalFragment
            }

            else -> {
                null
            }
        }
        if (fragmentId != null) {
            val build = Builder().setPopUpTo(fragmentId, true).build()
            if (isShowingFragment) (fragment as MyBaseFragment).refreshData()
            else findNavController.navigate(fragmentId, null, build)
        }
    }
}