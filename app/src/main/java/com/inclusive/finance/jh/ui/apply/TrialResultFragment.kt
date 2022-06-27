package com.inclusive.finance.jh.ui.apply

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.inclusive.finance.jh.BR
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentTrialResultBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.BaseTypePop
import com.inclusive.finance.jh.pop.ProcessProcessingPop
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act
import java.util.*

/**
 *
Trial result
初审结果
 * */
class TrialResultFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentTrialResultBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBind = FragmentTrialResultBinding.inflate(inflater, container, false).apply {
            presenterClick = this@TrialResultFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@TrialResultFragment)
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.adapter = adapter
    }

    override fun initData() {
        //                val mainData = SZWUtils.getJson(context, "修改担保企业担保分析.json")
        //                val list = Gson().fromJson<MutableList<BaseTypeBean>>(
        //                    mainData,
        //                    object : TypeToken<ArrayList<BaseTypeBean>>() {}.type
        //                )
        //
        //                adapter.setNewInstance(list)
        DataCtrlClass.KHGLNet.getBaseTypePoPList(requireActivity(), Urls.getList_csjg, keyId = viewModel.keyId) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)
//                val applyerRiskResult = it.firstOrNull { item -> item.dataKey == "applyerRiskResult" }?.valueName
//                val joapplyerRiskResult = it.firstOrNull { item -> item.dataKey == "joapplyerRiskResult" }?.valueName
//                val applyerZxResult = it.firstOrNull { item -> item.dataKey == "applyerZxResult" }?.valueName
//                val joapplyerZxResult = it.firstOrNull { item -> item.dataKey == "joapplyerZxResult" }?.valueName
//                when {
//                    applyerRiskResult.isNullOrEmpty() || applyerZxResult.isNullOrEmpty() -> viewBind.btApproval.visibility = View.VISIBLE
//                    joapplyerRiskResult.isNullOrEmpty() && joapplyerZxResult.isNullOrEmpty() -> {
////                      else->
//                          if ((applyerRiskResult + applyerZxResult).contains("不通过")) viewBind.btApproval.visibility = View.VISIBLE
//                        else viewBind.btApproval.visibility = View.GONE
//                    }
////                    joapplyerRiskResult.isNullOrEmpty() or joapplyerZxResult.isNullOrEmpty() -> {
////                        viewBind.btApproval.visibility = View.VISIBLE
////                    }
//                    else -> {
//                        if ((applyerRiskResult + joapplyerRiskResult + applyerZxResult + joapplyerZxResult).contains("不通过")) viewBind.btApproval.visibility = View.VISIBLE
//                        else viewBind.btApproval.visibility = View.GONE
//                    }
//                }
                adapter.setNewInstance(it)
            }
        }
    }
    val subscribe: (adapter: ItemBaseTypeAdapter<BaseTypeBean>, data: ArrayList<BaseTypeBean>, rootView:View) -> Unit = { adapter, it, view->
            it.forEach { item->
                if (item.dataKey=="admitType") {
                    fun setVisibility() {
                        it.forEachIndexed{ index, typeBean ->
                            when (typeBean.dataKey) {
                                "jbqk",
                                "basy",
                                "baly",
                                "dbcs",
                                "fxfkcs"
                                -> {
                                    //备案准入
                                    when (item.valueName) {
                                        "2", "1" -> {
                                            typeBean.visibility = true
                                            typeBean.requireable=true
                                        }
                                        else -> {
                                            typeBean.visibility = false
                                            typeBean.requireable = false
                                        }
                                    }
                                    adapter.notifyItemChanged(index)
                                }
                            }
                        }
                    }
                    setVisibility()
                    item.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int){
                            if (propertyId == BR.valueName) {
                                setVisibility()
                            }
                        }
                    })
                }
            }
    }

    override fun onClick(v: View?) {
        DataCtrlClass.KHGLNet.saveBaseTypePoPList(requireActivity(), Urls.save_csjg, adapter.data, keyId = viewModel.keyId) {
            if (it != null) {
                if (isAdded)
                (activity as BaseActivity).refreshData()
                when (v) {
                    viewBind.btApproval -> {
                        BaseTypePop(context, this, "发起签批", Urls.get_csjg_approval, Urls.save_csjg_approval, JsonObject(), viewModel.creditId,subscribe = subscribe) {adapter,resultStr->
                            if (adapter.data.firstOrNull { item -> item.dataKey == "admitType" }?.valueName =="3") {
                                val applyerRiskResult = this.adapter.data.firstOrNull { item -> item.dataKey == "applyerRiskResult" }?.valueName
                                val joapplyerRiskResult =  this.adapter.data.firstOrNull { item -> item.dataKey == "joapplyerRiskResult" }?.valueName
                                val applyerZxResult =  this.adapter.data.firstOrNull { item -> item.dataKey == "applyerZxResult" }?.valueName
                                val joapplyerZxResult =  this.adapter.data.firstOrNull { item -> item.dataKey == "joapplyerZxResult" }?.valueName
                                if (applyerRiskResult=="不通过") {
                                    this.adapter.data.firstOrNull { item -> item.dataKey == "applyerRiskResult" }?.valueName="人工通过"
                                }
                                if (joapplyerRiskResult=="不通过") {
                                    this.adapter.data.firstOrNull { item -> item.dataKey == "joapplyerRiskResult" }?.valueName="人工通过"
                                }
                                if (applyerZxResult=="不通过") {
                                    this.adapter.data.firstOrNull { item -> item.dataKey == "applyerZxResult" }?.valueName="人工通过"
                                }
                                if (joapplyerZxResult=="不通过") {
                                    this.adapter.data.firstOrNull { item -> item.dataKey == "joapplyerZxResult" }?.valueName="人工通过"
                                }

                            }else
                            refreshData()
                        }.show(childFragmentManager,this.javaClass.name)
                    }
                    viewBind.btSave -> {
                        DataCtrlClass.SXSPNet.getSXSPById(requireActivity(), keyId = viewModel.creditId,businessType =viewModel.businessType, type = "0") { configurationBean ->
                            if (configurationBean != null) {
                                ProcessProcessingPop(context, configurationBean,keyId =  viewModel.keyId,businessType =viewModel.businessType) {
                                    refreshData()
                                }.show(childFragmentManager,this.javaClass.name)
                            }
                        }
                    }
                }
            }
        }

    }

}