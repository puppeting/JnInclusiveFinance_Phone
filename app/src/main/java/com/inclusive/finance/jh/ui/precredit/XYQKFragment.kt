package com.inclusive.finance.jh.ui.precredit

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
import com.inclusive.finance.jh.databinding.FragmentXyqkBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act
import java.math.BigDecimal

/**
 * 信用情况
 * */
class XYQKFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentXyqkBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentXyqkBinding.inflate(inflater, container, false).apply {
            presenterClick = this@XYQKFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data=viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@XYQKFragment)
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.adapter=adapter
    }
    var getUrl=""
    var saveUrl=""
    var businessType=""
    override fun initData() {
//                val mainData = SZWUtils.getJson(context, "修改担保企业担保分析.json")
//                val list = Gson().fromJson<MutableList<BaseTypeBean>>(
//                    mainData,
//                    object : TypeToken<ArrayList<BaseTypeBean>>() {}.type
//                )
//
//                adapter.setNewInstance(list)
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_PRECREDIT,
            -> {
                getUrl=Urls.get_preCredit_xyqk
                saveUrl=Urls.save_preCredit_xyqk
            }
        }
        businessType=SZWUtils.getBusinessType(viewModel.businessType)
        DataCtrlClass.KHGLNet.getBaseTypePoPList(requireActivity(),getUrl, keyId = viewModel.keyId,businessType = businessType) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel,it)
                it.forEach { bean ->
                    when (bean.dataKey) {
                        "jtzzc",
                        "jtzfz",
                        -> {
                            bean.addOnPropertyChangedCallback(object :
                                Observable.OnPropertyChangedCallback() {
                                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                                    if (propertyId == BR.valueName) {
                                        val jtzzc= SZWUtils.getCalculateCount(it, "jtzzc")
                                        val jtzfz= SZWUtils.getCalculateCount(it, "jtzfz")
                                        val jtjzc = BigDecimal(jtzzc - jtzfz)
                                        //家庭净资产
                                        SZWUtils.setCalculateCount(it, "jtjzc", jtjzc)
                                    }
                                }
                            })
                        }
                    }
                    when (bean.dataKey) {
                        "gdzc",
                        "ldzc",
                        -> {
                            bean.addOnPropertyChangedCallback(object :
                                Observable.OnPropertyChangedCallback() {
                                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                                    if (propertyId == BR.valueName) {
                                        val gdzc= SZWUtils.getCalculateCount(it, "gdzc")
                                        val ldzc= SZWUtils.getCalculateCount(it, "ldzc")
                                        val jtzzc = BigDecimal(gdzc + ldzc)
                                        //家庭总资产
                                        SZWUtils.setCalculateCount(it, "jtzzc", jtzzc)
                                    }
                                }
                            })
                        }
                    }
                }
                adapter.setNewInstance(it)
            }
        }
    }

    override fun saveData() {
        DataCtrlClass.KHGLNet.saveBaseTypePoPList(requireActivity(), saveUrl, adapter.data, keyId = viewModel.keyId,businessType=businessType) {
            if (it != null) {
                refreshData()
                if (isAdded){
                    (activity as BaseActivity).refreshData()
                }
            }
        }
    }
    override fun onClick(v: View?) {
        when (v) {
            viewBind.btSave -> {
                saveData()
            }
        }
    }

}