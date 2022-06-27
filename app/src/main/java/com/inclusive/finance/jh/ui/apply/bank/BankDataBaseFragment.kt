package com.inclusive.finance.jh.ui.apply.bank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.PreferencesService
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentBankDataBaseBinding
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act

class BankDataBaseFragment : MyBaseFragment() {
    private var mjson: String? = ""
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentBankDataBaseBinding
    var getUrl=""
    var businessType=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentBankDataBaseBinding.inflate(inflater, container, false).apply {
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
//        viewBind.mRecyclerView.setItemViewCacheSize(30)
//        viewBind.mRecyclerView.addItemDecoration(GridItemDecoration.Builder(context).setColor(ContextCompat.getColor(act,R.color.Black)).setHorizontalSpan(15f).setVerticalSpan(15f).build())
        viewBind.mRecyclerView.adapter=adapter
        //        viewBind.scrollView.post {
        //            adapter.emptyView = View.inflate(context, R.layout.empty_view, null).apply {
        //                layoutParams = FrameLayout.LayoutParams(ScreenUtils.getScreenWidth(), viewBind.scrollView.measuredHeight)
        //                findViewById<TextView>(R.id.empty_text).text = "暂无数据，请尝试查询或刷新。"
        //            }
        //        }

    }

    override fun refreshData(type: Int?) {
        adapter.setNewInstance(null)
        super.refreshData(type)
    }
    override fun initData() {

        mjson=viewModel.jsonObject
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_APPLY,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE, -> {
                context?.let {
                    mjson= PreferencesService.getJson(
                        it
                    )
                }
                getUrl = when (viewModel.title) {
                    "汇总信息" -> Urls.getList_bank
                    "明细" -> Urls.getList_bankDetail
                    else -> ""
                }
            }
            ApplyModel.BUSINESS_TYPE_JNJ_CJ_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_CJ_COMPANY,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_SJ_PERSONAL,
            ApplyModel.BUSINESS_TYPE_SJ_COMPANY,
            ApplyModel.BUSINESS_TYPE_RC_OFF_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_RC_ON_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_RC_ON_SITE_COMPANY,-> {
                getUrl = when (viewModel.title) {
                    "汇总信息" -> Urls.get_jnj_cj_personal_whyw
                    "明细" -> Urls.get_jnj_cj_personal_whyw_mx
                    else -> ""
                }
            }
            ApplyModel.BUSINESS_TYPE_VISIT_NEW,
            ApplyModel.BUSINESS_TYPE_VISIT_EDIT,
            ApplyModel.BUSINESS_TYPE_PRECREDIT,
            -> {
                getUrl = when (viewModel.title) {
                    "汇总信息" -> Urls.get_visit_whyw
                    "明细" -> Urls.get_visit_whyw_mx
                    else -> ""
                }
            }
            ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY,
            -> {
                getUrl = when (viewModel.title) {
                    "汇总信息" -> Urls.getList_sunshine_bank
                    "明细" -> Urls.getList_sunshine_bankDetail
                    else -> ""
                }
            }
        }
        businessType=SZWUtils.getBusinessType(viewModel.businessType)

        DataCtrlClass.ApplyNet.getListBank(requireActivity(), url = getUrl, keyId=viewModel.keyId, json=mjson,businessType = businessType) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)
                adapter.setNewInstance(it)
                if (isAdded)
                (activity as BaseActivity).refreshData()
            }
        }
    }

}