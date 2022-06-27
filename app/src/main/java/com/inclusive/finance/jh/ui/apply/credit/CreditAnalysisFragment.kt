package com.inclusive.finance.jh.ui.apply.credit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.EmptyViewBinding
import com.inclusive.finance.jh.databinding.FragmentCreditAnalysisBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act
/**
 * 征信解析
 *
 * */
class CreditAnalysisFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentCreditAnalysisBinding
    lateinit var emptyViewBind: EmptyViewBinding
    var getUrl=""
    var businessType=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentCreditAnalysisBinding.inflate(inflater, container, false).apply {
            presenterClick = this@CreditAnalysisFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this)
        adapter.keyId = viewModel.keyId
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.setItemViewCacheSize(30)
        viewBind.mRecyclerView.adapter=adapter
        emptyViewBind = EmptyViewBinding.inflate(LayoutInflater.from(act))
        adapter.setEmptyView(emptyViewBind.root)
    }

    override fun initData() {
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_APPLY,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE,
            ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_ZXGL,
            ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY,
            -> {
                getUrl= Urls.creditAnalysisList
            }
            ApplyModel.BUSINESS_TYPE_JNJ_YX,
            ApplyModel.BUSINESS_TYPE_JNJ_CJ_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_CJ_COMPANY,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_SJ_PERSONAL,
            ApplyModel.BUSINESS_TYPE_SJ_COMPANY,
            ApplyModel.BUSINESS_TYPE_RC_OFF_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_RC_ON_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_RC_ON_SITE_COMPANY,
            -> {
                getUrl= Urls.get_jnj_cj_personal_zxgl_zxjx
            }
        }
        businessType=SZWUtils.getBusinessType(viewModel.businessType)
        DataCtrlClass.ApplyNet.creditAnalysisList(requireActivity(),getUrl, viewModel.creditId, viewModel.jsonObject,dhId = viewModel.dhId,businessType = businessType) { it, msg ->
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)
                adapter.setNewInstance(it)
            } else {
                emptyViewBind.emptyText.text = msg
            }
        }
    }

    private var mLastClickTime: Long = 0
    private val TIME_INTERVAL = 500L
    override fun onClick(v: View?) {
//        if (v == viewBind.btSearch) {
//            if (System.currentTimeMillis() - mLastClickTime > TIME_INTERVAL) {
//                DataCtrlClass.ApplyNet.creditAnalysisAdd(context, viewModel.creditId,  viewModel.jsonObject) {
//                    refreshData()
//                }
//                mLastClickTime = System.currentTimeMillis()
//            }
//            return
//        }
        when (v) {
            viewBind.btRefresh -> {
                refreshData()
            }
            //            viewBind.btNext -> {
            //                ConfirmPop(context, "") {
            //
            //                }.showPopupWindow()
            //            }
        }
    }
}