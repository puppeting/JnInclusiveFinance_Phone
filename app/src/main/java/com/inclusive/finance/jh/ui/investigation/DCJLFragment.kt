package com.inclusive.finance.jh.ui.investigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.TimeUtils
import com.google.gson.JsonObject
import com.inclusive.finance.jh.BR
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentDcjlBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.BaseTypePop
import com.inclusive.finance.jh.pop.ProcessProcessingPop
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

/**
 *

调查结论
 * */
class DCJLFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentDcjlBinding
    var getUrl = ""
    var saveUrl = ""
    var temporaryUrl = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentDcjlBinding.inflate(inflater, container, false).apply {
            presenterClick = this@DCJLFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@DCJLFragment)
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.adapter = adapter
    }

    override fun initData() { //                val mainData = SZWUtils.getJson(context, "修改担保企业担保分析.json")
        //                val list = Gson().fromJson<MutableList<BaseTypeBean>>(
        //                    mainData,
        //                    object : TypeToken<ArrayList<BaseTypeBean>>() {}.type
        //                )
        //
        //                adapter.setNewInstance(list)

        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY -> {
                getUrl = Urls.get_sunshine_DCjl
                saveUrl = Urls.save_sunshine_DCJL
                temporaryUrl = Urls.saveTemporary_sunshine_DCJL
                viewBind.btApproval.visibility = View.VISIBLE
            }
            else -> {
                getUrl = Urls.getDCjl
                saveUrl = Urls.saveDCJL
                temporaryUrl = Urls.saveTemporary_DCJL
                viewBind.btApproval.visibility = View.GONE
            }
        }
        DataCtrlClass.KHGLNet.getBaseTypePoPList(requireActivity(), getUrl, keyId = viewModel.keyId) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)
                calculate(adapter, it)
                if (viewModel.businessType == ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY) {
                    resultCalculate(adapter, it)
                }
                adapter.setNewInstance(it)
            }
        }
    }

    private fun resultCalculate(adapter: ItemBaseTypeAdapter<BaseTypeBean>, it: java.util.ArrayList<BaseTypeBean>) {
        val applyerRiskResult = it.firstOrNull { item -> item.dataKey == "applyerRiskResult" }?.valueName
        val joapplyerRiskResult = it.firstOrNull { item -> item.dataKey == "joapplyerRiskResult" }?.valueName
        val applyerZxResult = it.firstOrNull { item -> item.dataKey == "applyerZxResult" }?.valueName
        val joapplyerZxResult = it.firstOrNull { item -> item.dataKey == "joapplyerZxResult" }?.valueName
        when {
            applyerRiskResult.isNullOrEmpty() || applyerZxResult.isNullOrEmpty() -> viewBind.btApproval.visibility = View.VISIBLE
            joapplyerRiskResult.isNullOrEmpty() && joapplyerZxResult.isNullOrEmpty() -> { //                      else->
                if ((applyerRiskResult + applyerZxResult).contains("不通过")) viewBind.btApproval.visibility = View.VISIBLE
                else viewBind.btApproval.visibility = View.GONE
            } //                    joapplyerRiskResult.isNullOrEmpty() or joapplyerZxResult.isNullOrEmpty() -> {
            //                        viewBind.btApproval.visibility = View.VISIBLE
            //                    }
            else -> {
                if ((applyerRiskResult + joapplyerRiskResult + applyerZxResult + joapplyerZxResult).contains("不通过")) viewBind.btApproval.visibility = View.VISIBLE
                else viewBind.btApproval.visibility = View.GONE
            }
        }
    }

    private fun calculate(adapter: ItemBaseTypeAdapter<BaseTypeBean>, it: ArrayList<BaseTypeBean>) {
        it.forEach { bean ->
            when (bean.dataKey) {
                "endDate",
                -> {
                    bean.addOnPropertyChangedCallback(object :
                        Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                calculateQX(it)
                            }
                        }
                    })
                }
            }
            when (bean.dataKey) {
                "fuZhaiTotal",
                "ziChanTotal",
                "creditLine",
                "fzhj",
                "zchj",
                -> {
                    bean.addOnPropertyChangedCallback(object :
                        Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                calculateZCFZL(it)
                            }
                        }
                    })
                }
            }
        }
        calculateQX(it)
        calculateZCFZL(it)
    }

    private fun calculateQX(it: ArrayList<BaseTypeBean>) {
        val startDate = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd", Locale.CHINA))
        val endDate = it.firstOrNull { item -> item.dataKey == "endDate" }?.valueName
        if (!startDate.isNullOrEmpty() && !endDate.isNullOrEmpty()) {
            it.firstOrNull { item -> item.dataKey == "timeLimit" }?.valueName = SZWUtils.getMonthSpace(endDate, startDate)
                .toString()
        }
    }

    private fun calculateZCFZL(it: ArrayList<BaseTypeBean>) {
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY -> {
                val fzhj = SZWUtils.getCalculateCount(it, "fzhj")
                val zchj = SZWUtils.getCalculateCount(it, "zchj")
                val creditLine = SZWUtils.getCalculateCount(it, "creditLine")

                val dhzcfz = BigDecimal(if ((zchj + creditLine) > 0) (fzhj + creditLine) / (zchj + creditLine) * 100 else 0.00) //贷后资产负债率应=（原负债+授信结论中授信金额）/（原资产+授信结论中授信金额）*100%
                SZWUtils.setCalculateCount(it, "dhzcfz", dhzcfz)
            }
            else -> {

                val fuZhaiTotal = SZWUtils.getCalculateCount(it, "fuZhaiTotal")
                val ziChanTotal = SZWUtils.getCalculateCount(it, "ziChanTotal")
                val creditLine = SZWUtils.getCalculateCount(it, "creditLine")

                val dhzcfz = BigDecimal(if ((ziChanTotal + creditLine) > 0) (fuZhaiTotal + creditLine) / (ziChanTotal + creditLine) * 100 else 0.00) //贷后资产负债率应=（原负债+授信结论中授信金额）/（原资产+授信结论中授信金额）*100%
                SZWUtils.setCalculateCount(it, "dhzcfz", dhzcfz)
            }
        }

    }

    override fun onClick(v: View?) {
        when (v) {
            viewBind.btTemporarySave -> {
                DataCtrlClass.ApplyNet.saveTemporary(context, temporaryUrl, adapter.data, keyId = viewModel.keyId) {
                    if (it != null) {
                        if (isAdded) (activity as BaseActivity).refreshData()
                    }
                }
            }
            else -> DataCtrlClass.KHGLNet.saveBaseTypePoPList(context, saveUrl, adapter.data, keyId = viewModel.keyId) {
                if (it != null) {
                    when (v) {
                        viewBind.btApproval -> {
                            BaseTypePop(context, this, "发起签批", Urls.get_sunshine_approval, Urls.save_sunshine_approval, JsonObject(), viewModel.creditId) { adapter, resultStr ->
                                if (adapter.data.firstOrNull { item -> item.dataKey == "admitType" }?.valueName == "3") {
                                    val applyerRiskResult = this.adapter.data.firstOrNull { item -> item.dataKey == "applyerRiskResult" }?.valueName
                                    val joapplyerRiskResult = this.adapter.data.firstOrNull { item -> item.dataKey == "joapplyerRiskResult" }?.valueName
                                    val applyerZxResult = this.adapter.data.firstOrNull { item -> item.dataKey == "applyerZxResult" }?.valueName
                                    val joapplyerZxResult = this.adapter.data.firstOrNull { item -> item.dataKey == "joapplyerZxResult" }?.valueName
                                    if (applyerRiskResult == "不通过") {
                                        this.adapter.data.firstOrNull { item -> item.dataKey == "applyerRiskResult" }?.valueName = "人工通过"
                                    }
                                    if (joapplyerRiskResult == "不通过") {
                                        this.adapter.data.firstOrNull { item -> item.dataKey == "joapplyerRiskResult" }?.valueName = "人工通过"
                                    }
                                    if (applyerZxResult == "不通过") {
                                        this.adapter.data.firstOrNull { item -> item.dataKey == "applyerZxResult" }?.valueName = "人工通过"
                                    }
                                    if (joapplyerZxResult == "不通过") {
                                        this.adapter.data.firstOrNull { item -> item.dataKey == "joapplyerZxResult" }?.valueName = "人工通过"
                                    }

                                } else refreshData()
                            }.show(childFragmentManager, this.javaClass.name)
                        }
                        viewBind.btSave -> {

                            if (isAdded) (activity as BaseActivity).refreshData()
                            DataCtrlClass.SXSPNet.getSXSPById(context, keyId = viewModel.keyId, businessType = viewModel.businessType, type =SZWUtils.getBusinessType(viewModel.businessType) ) { configurationBean ->
                                if (configurationBean != null) {
                                    ProcessProcessingPop(context, configurationBean, keyId = viewModel.keyId, businessType = viewModel.businessType) {
                                        refreshData()
                                    }.show(childFragmentManager, this.javaClass.name)
                                }
                            }
                        }
                    }
                }
            }
        }


    }

}