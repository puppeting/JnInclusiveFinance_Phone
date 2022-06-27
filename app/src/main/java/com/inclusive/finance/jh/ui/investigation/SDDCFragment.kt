package com.inclusive.finance.jh.ui.investigation

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
import com.inclusive.finance.jh.bean.SDDCBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentSddcBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 实地调查
 * */
class SDDCFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<SDDCBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentSddcBinding
    var getUrl=""
    var saveUrl=""
    var temporaryUrl=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentSddcBinding.inflate(inflater, container, false).apply {
            presenterClick = this@SDDCFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@SDDCFragment)
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.setItemViewCacheSize(30)
        viewBind.mRecyclerView.adapter = adapter
    }

    override fun initData() {
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY -> {
               getUrl=Urls.get_sunshine_SDDC
               saveUrl=Urls.save_sunshine_SDDC
                temporaryUrl= Urls.saveTemporary_sunshine_SDDC
            }
            else -> {
                getUrl=Urls.getSDDC
                saveUrl=Urls.saveSDDC
                temporaryUrl=Urls.saveTemporary_SDDC
            }
        }
        DataCtrlClass.SXDCNet.getSDDCList(requireActivity(), url =getUrl , keyId = viewModel.keyId) {
            if (it != null) {
                var mit=it.result
                if(mit==null){
                    SZWUtils.showSnakeBarError(it.message.toString())
                    return@getSDDCList
                }
                SZWUtils.setSeeOnlyMode(viewModel, mit)
                mit.forEach { baseTypeBean ->
                    when (baseTypeBean.dataKey) {
                        "otherAssets",
                        "currentInterest",
                        "fc",
                        "cl",
                        "qtzc",
                        "dk",
                        "xyk",
                        "qtfz",
                        -> {
                            baseTypeBean.addOnPropertyChangedCallback(object :
                                Observable.OnPropertyChangedCallback() {
                                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                                    if (propertyId == BR.valueName) {
                                        val currentInterest = SZWUtils.getCalculateCount(mit, "currentInterest")
                                        val otherAssets = SZWUtils.getCalculateCount(mit, "otherAssets")
                                        SZWUtils.setCalculateCount(mit, "ownAccumulProp", if (currentInterest > 0) BigDecimal((currentInterest - otherAssets) / currentInterest * 100) else BigDecimal.ZERO) //                                        王兴隆:
                                        //                                        总资产（万元）= 房产+车辆+其他资产
                                        //                                        总负债（万元）= 贷款+信用卡+其他负债
                                        //                                        净资产（万元）=总资产-总负债
                                        //                                        资产负债率（%）=总负债/总资产*100
                                        calculate(mit)
                                    }
                                }
                            })
                        }
                    }
                    when (baseTypeBean.dataKey) {
                        "productType",
                        -> {
                            fun setVisibility() {
                                mit.forEachIndexed{ index, typeBean ->
                                    if (typeBean.cplxstatus.isNotEmpty() ) {
                                        if (baseTypeBean.valueName == typeBean.cplxstatus) {
                                            typeBean.visibility = true
                                            typeBean.requireable=true
                                        }
                                        else {
                                            typeBean.visibility = false
                                            typeBean.requireable = false
                                        }
                                        adapter.notifyItemChanged(index)
                                    }
                                }
                            }
                            setVisibility()
                            baseTypeBean.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                                override fun onPropertyChanged(sender: Observable?, propertyId: Int){
                                    if (propertyId == BR.valueName) {
                                        setVisibility()
                                    }
                                }
                            })
                        }
                    }
                    when (baseTypeBean.dataKey) {
                        "totInvest"->{
                            mit.forEachIndexed{ index, typeBean ->
                                if (baseTypeBean.dataKey == "totInvest") {
                                        baseTypeBean.addOnPropertyChangedCallback(object :
                                            Observable.OnPropertyChangedCallback() {
                                            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                                                if (propertyId == BR.valueName) {
                                                     if(baseTypeBean.valueName!=null&&baseTypeBean.valueName!=""&&it.item.toString()!="0"){
                                                        val totInvest2 = BigDecimal(baseTypeBean.valueName)
                                                        val item2 = BigDecimal(it.item.toString())
                                                        try {
                                                            SZWUtils.setCalculateCount(mit, "dkzjztrzebl", item2.divide(totInvest2,4, RoundingMode.HALF_UP).multiply( BigDecimal(100)))
                                                        }catch (e:Exception){

                                                        }
                                                    }
                                                }
                                            }

                                        })

                                }

                            }

                        }

                    }
                }
                calculate(mit)
                adapter.setNewInstance(mit)
            }
        }
    }

    private fun calculate(it: ArrayList<SDDCBean>) {
        val fc = SZWUtils.getCalculateCount(it, "fc")
        val cl = SZWUtils.getCalculateCount(it, "cl")
        val qtzc = SZWUtils.getCalculateCount(it, "qtzc")
        val dk = SZWUtils.getCalculateCount(it, "dk")
        val xyk = SZWUtils.getCalculateCount(it, "xyk")
        val qtfz = SZWUtils.getCalculateCount(it, "qtfz")
        val zzc = BigDecimal(fc + cl + qtzc)
        val zfz = BigDecimal(dk + xyk + qtfz)
        SZWUtils.setCalculateCount(it, "zzc", zzc)
        SZWUtils.setCalculateCount(it, "zfz", zfz)
        SZWUtils.setCalculateCount(it, "jzc", zzc.minus(zfz))
        SZWUtils.setCalculateCount(it, "zcfzl", if (zzc > BigDecimal.ZERO) zfz.divide(zzc, 2, 0).multiply(BigDecimal.valueOf(100)) else BigDecimal.ZERO)
    }

    override fun saveData() {
        DataCtrlClass.KHGLNet.saveBaseTypePoPList(context, saveUrl, adapter.data, keyId = viewModel.keyId) {
            if (it != null) {
                if (isAdded) (activity as BaseActivity).refreshData()
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
            viewBind.btSave -> {
                saveData()
            }
        }
    }

}