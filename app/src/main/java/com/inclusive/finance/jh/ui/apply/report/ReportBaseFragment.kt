package com.inclusive.finance.jh.ui.apply.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.inclusive.finance.jh.BR
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentReportBaseBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.ProcessProcessingPop
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act
import java.math.BigDecimal

/**
 * 调查报告-{基本信息，}
 * */
@Deprecated("暂时不用")
class ReportBaseFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentReportBaseBinding
    private var getUrl = ""
    private var saveUrl = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentReportBaseBinding.inflate(inflater, container, false).apply {
            presenterClick = this@ReportBaseFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }
    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@ReportBaseFragment)
        adapter.keyId = viewModel.keyId
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.setItemViewCacheSize(30)
        viewBind.mRecyclerView.adapter=adapter
    }

    override fun initData() {
        //        val mainData = SZWUtils.getJson(context, "征信授权.json")
        //        val list = Gson().fromJson<MutableList<BaseTypeBean>>(
        //            mainData,
        //            object : TypeToken<ArrayList<BaseTypeBean>>() {}.type
        //        )
        //
        //        adapter.setNewInstance(list)
        viewBind.btSave.visibility=View.VISIBLE
        viewBind.btSubmit.visibility=View.GONE
        when (viewModel.title) {
            "基本信息" -> {
                getUrl = Urls.getDCBGBaseInfo
                saveUrl = Urls.saveDCBGBaseInfo
                viewBind.btSubmit.visibility=View.VISIBLE
            }
            "信贷历史" -> {
                getUrl = Urls.getDCBGXDLSInfo
                saveUrl = ""
                viewBind.btSave.visibility=View.GONE
            }
            "申请贷款信息" -> {
                getUrl = Urls.getDCBGSQDKInfo
                saveUrl = Urls.saveDCBGSQDKInfo
            }
            "共同借款人和担保人的信息" -> {
                getUrl = Urls.getDCBGGTJKInfo
                saveUrl = ""
                viewBind.btSave.visibility=View.GONE
            }
            "担保人资格检查" -> {
                getUrl = Urls.getDCBGDBRZGInfo
                saveUrl = ""
                viewBind.btSave.visibility=View.GONE
            }
            "第三方评价" -> {
                getUrl = Urls.getDCBGDDSFPJInfo
                saveUrl = Urls.saveDCBGDDSFPJInfo
            }
            "贷款资格审查" -> {
                getUrl = Urls.getDCBGDDKZGInfo
                saveUrl = Urls.saveDCBGDDKZGInfo
            }
            "申请人财务报表" -> {
                getUrl = Urls.getDCBGCWBBInfo
                saveUrl = Urls.saveDCBGCWBBInfo
            }
            "风险因素分析及调查结论" -> {
                getUrl = Urls.getDCBGFXYSInfo
                saveUrl = Urls.saveDCBGFXYSInfo
            }
            "按揭类-基本信息" -> {
                getUrl = Urls.getDCBGAJJBXXInfo
                saveUrl = Urls.saveDCBGAJJBXXInfo
            }
            "借款人条件及购房情况认定" -> {
                getUrl = Urls.getDCBGJKRTJGFQKInfo
                saveUrl = Urls.saveDCBGJKRTJGFQKInfo
            }
            "首付款支付及房价核实" -> {
                getUrl = Urls.getDCBGsfkzfInfo
                saveUrl = Urls.saveDCBGsfkzfInfo
            }
            "还款能力" -> {
                getUrl = Urls.getDCBGhknlfInfo
                saveUrl = Urls.saveDCBGhknlfInfo
            }
            "抵押物状况" -> {
                getUrl = Urls.getDCBGdywzkInfo
                saveUrl = Urls.saveDCBGdywzkInfo
            }
            "面谈情况" -> {
                getUrl = Urls.getDCBGmtqkInfo
                saveUrl = Urls.saveDCBGmtqkInfo
            }
            "调查意见" -> {
                getUrl = Urls.getDCBGdcyjInfo
                saveUrl = Urls.saveDCBGdcyjInfo
            }
        }
        DataCtrlClass.KHGLNet.getBaseTypePoPList(requireActivity(), getUrl, keyId = viewModel.keyId) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)
                when (viewModel.title) {
                    "基本信息" -> {
                        initPO(it)
                    }
                    "申请人财务报表" -> {
                        calculate(it)
                    }
                    "还款能力" -> {
                        hcnlCalculate(it)
                    }
                    else -> {
                    }
                }
                adapter.setNewInstance(it)
            }
        }
    }
    /**还款能力   计算*/
    private fun hcnlCalculate(it: ArrayList<BaseTypeBean>) {
        it.forEach{bean->
            when (bean.dataKey) {
                "jkrysr",
                "gjrysr"-> {
                    bean.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                val jkrysr = it.firstOrNull { item -> item.dataKey == "jkrysr" && item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull() ?: 0.00
                                val gjrysr = it.firstOrNull { item -> item.dataKey == "gjrysr" && item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull() ?: 0.00
                                val jkrysrDecimal = BigDecimal(jkrysr)
                                val gjrysrDecimal = BigDecimal(gjrysr)
                                //家庭月收入合计（元）
                                it.firstOrNull { item-> item.dataKey=="jtysrhj" }?.valueName=jkrysrDecimal.add(gjrysrDecimal).setScale(2,BigDecimal.ROUND_HALF_UP).toString()

                            }
                        }
                    })
                }
            }
        }
    }
    /**申请人财务报表   计算*/
    private fun calculate(it: ArrayList<BaseTypeBean>) {
        it.forEach { bean ->
            when (bean.dataKey) {
                "xjyhck",
                "lcjtz",
                "yskx1" ,
                "gdzcyzzm" ,
                "gdzcwzzm" ,
                "qtzc",
                "yhjk" ,
                "mjrz" ,
                "yskx2" ,
                "qtfz" -> {
                    bean.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                val gdzcyzzm = it.firstOrNull { item -> item.dataKey == "gdzcyzzm" && item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull() ?: 0.00
                                val zczj=(it.firstOrNull { item-> item.dataKey=="xjyhck"&& item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull()?:0.00)
                                    .plus(it.firstOrNull { item-> item.dataKey=="lcjtz"&&item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull()?:0.00)
                                    .plus(it.firstOrNull { item-> item.dataKey=="yskx1"&&item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull()?:0.00)
                                    .plus(gdzcyzzm)
                                    .plus(it.firstOrNull { item-> item.dataKey=="gdzcwzzm"&&item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull()?:0.00)
                                    .plus(it.firstOrNull { item-> item.dataKey=="qtzc"&&item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull()?:0.00)
                                val fzhj=(it.firstOrNull { item-> item.dataKey=="yhjk"&& item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull()?:0.00)
                                    .plus(it.firstOrNull { item-> item.dataKey=="mjrz"&&item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull()?:0.00)
                                    .plus(it.firstOrNull { item-> item.dataKey=="yskx2"&&item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull()?:0.00)
                                    .plus(it.firstOrNull { item-> item.dataKey=="qtfz"&&item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull()?:0.00)
                                val gdzcyzzmDecimal = BigDecimal(gdzcyzzm).setScale(2,BigDecimal.ROUND_HALF_UP)
                                val zczjDecimal = BigDecimal(zczj)
                                val fzhjDecimal = BigDecimal(fzhj)

                                //资产总计
                                it.firstOrNull { item-> item.dataKey=="zczj" }?.valueName= zczjDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).toString()
                                //负债合计
                                it.firstOrNull { item-> item.dataKey=="fzhj" }?.valueName=fzhjDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).toString()
                                //所有者权益
                                it.firstOrNull { item-> item.dataKey=="syzqy" }?.valueName= zczjDecimal.subtract(fzhjDecimal).setScale(2,BigDecimal.ROUND_HALF_UP).toString()
                                //资产负债率
                                it.firstOrNull { item-> item.dataKey=="zcfzl" }?.valueName=if (zczjDecimal> BigDecimal.ZERO)fzhjDecimal.divide(zczjDecimal,2,BigDecimal.ROUND_HALF_UP).toString() else ""
                                //核实的固定资产对负债比
                                it.firstOrNull { item-> item.dataKey=="gdzcdfzb" }?.valueName=if (fzhjDecimal> BigDecimal.ZERO)gdzcyzzmDecimal.divide(fzhjDecimal,2,BigDecimal.ROUND_HALF_UP).toString() else ""
                                //实际权益
                                it.firstOrNull { item-> item.dataKey=="sjqy" }?.valueName= zczjDecimal.subtract(fzhjDecimal).setScale(2,BigDecimal.ROUND_HALF_UP).toString()
                            }
                        }
                    })
                }
            }
            when (bean.dataKey) {
                "nfzzc",
                "nkzpsr"-> {
                    bean.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                val nfzzc = it.firstOrNull { item -> item.dataKey == "nfzzc" && item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull() ?: 0.00
                                val nkzpsr = it.firstOrNull { item -> item.dataKey == "nkzpsr" && item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull() ?: 0.00
                                val nfzzcDecimal = BigDecimal(nfzzc)
                                val nkzpsrDecimal = BigDecimal(nkzpsr)
                                //负债支出与收入比
                                it.firstOrNull { item-> item.dataKey=="fzsrb" }?.valueName=if (nkzpsrDecimal> BigDecimal.ZERO)nfzzcDecimal.divide(nkzpsrDecimal,2,BigDecimal.ROUND_HALF_UP).toString() else ""

                            }
                        }
                    })
                }
            }
            when (bean.dataKey) {
                "sjqy",
                "csqy",
                "qjnlr",
                "qjnzbzr",
                "qntqzj",
                "zjsz"-> {
                    bean.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                val sjqy = it.firstOrNull { item -> item.dataKey == "sjqy" && item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull() ?: 0.00
                                val yyqy=(it.firstOrNull { item-> item.dataKey=="csqy"&& item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull()?:0.00)
                                    .plus(it.firstOrNull { item-> item.dataKey=="qjnlr"&&item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull()?:0.00)
                                    .plus(it.firstOrNull { item-> item.dataKey=="qjnzbzr"&&item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull()?:0.00)
                                    .minus(it.firstOrNull { item-> item.dataKey=="qntqzj"&&item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull()?:0.00)
                                    .plus(it.firstOrNull { item-> item.dataKey=="zjsz"&&item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull()?:0.00)
                                val yyqyDecimal = BigDecimal(yyqy)
                                val sjqyDecimal = BigDecimal(sjqy)


                                //应有权益
                                it.firstOrNull { item-> item.dataKey=="yyqy" }?.valueName=yyqyDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).toString()
                                //权益差=if(应有权益>实际权益，应有权益-实际权益，实际权益-应有权益)
                                val qycDecimal = if (yyqyDecimal > sjqyDecimal) yyqyDecimal.subtract(sjqyDecimal) else sjqyDecimal.subtract(yyqyDecimal)
                                it.firstOrNull { item-> item.dataKey=="qyc" }?.valueName=qycDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).toString()
                                //差异率=if(应有权益>实际权益，(权益差/(应有权益+0.0000000001))，(权益差/(实际权益+0.000001)))
                                it.firstOrNull { item-> item.dataKey=="cyl" }?.valueName=(if (yyqyDecimal>sjqyDecimal)qycDecimal.divide(yyqyDecimal+0.0000000001.toBigDecimal(),10,BigDecimal.ROUND_HALF_UP)else qycDecimal.divide(sjqyDecimal+0.000001.toBigDecimal(),6,BigDecimal.ROUND_HALF_UP)).setScale(2,BigDecimal.ROUND_HALF_UP).toString()

                            }
                        }
                    })
                }
            }


        }
    }
    /**根据婚姻状况筛选配偶信息，是否必填*/
    private fun initPO(it: ArrayList<BaseTypeBean>) {
        it.forEach { bean ->
            if (bean.keyName == "婚姻状况") { //对婚姻状况进行监听，如果改变了。触发回调，已婚再婚，配偶信息必填，否则选填
                bean.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                        if (propertyId == BR.valueName) {
                            it.forEach { typeBean ->
                                when (typeBean.dataKey) {
                                    "poxm",
                                    "poyddh",
                                    "pozjhm",
                                    "pogzdw",
                                    "pogznx",
                                    "ponx",
                                    -> typeBean.requireable = bean.valueName.contains("已") || bean.valueName.contains("再")
                                }
                            }
                        }
                    }
                })
                it.forEach { typeBean ->
                    when (typeBean.dataKey) {
                        "poxm",
                        "poyddh",
                        "pozjhm",
                        "pogzdw",
                        "pogznx",
                        "ponx",
                        -> typeBean.requireable = bean.valueName.contains("已") || bean.valueName.contains("再")
                    }
                }
            }
        }
    }

    override fun saveData() {
        DataCtrlClass.KHGLNet.saveBaseTypePoPList(context, saveUrl, adapter.data, keyId = viewModel.keyId) {
            if (it != null) {
                if (isAdded)
                (activity as BaseActivity).refreshData()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            viewBind.btSubmit -> {
                DataCtrlClass.SXSPNet.getSXSPById(context,keyId = viewModel.keyId?:""){
                    if (it!=null){
                        ProcessProcessingPop(context,it,viewModel.keyId?:""){

                        }.show(childFragmentManager,this.javaClass.name)
                    }
                }
            }
            viewBind.btSave -> {
                saveData()
            }
        }
    }

}