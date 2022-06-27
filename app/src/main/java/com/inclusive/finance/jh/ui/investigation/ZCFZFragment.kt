package com.inclusive.finance.jh.ui.investigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.*
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.jetbrains.anko.support.v4.act
import java.util.ArrayList
import com.inclusive.finance.jh.BR

/**
 * 资产负债
 * */
class ZCFZFragment : MyBaseFragment(), PresenterClick, OnRefreshListener {
    lateinit var mAdapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentZcfzBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentZcfzBinding.inflate(inflater, container, false).apply {
            presenterClick = this@ZCFZFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        mAdapter = ItemBaseTypeAdapter(this@ZCFZFragment)
        mAdapter.keyId = viewModel.keyId
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.setItemViewCacheSize(30)
        viewBind.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshListener(this)
    }

    override fun initData() {
        DataCtrlClass.KHGLNet.getBaseTypePoPList(requireActivity(), url = Urls.getZCFZ, keyId = viewModel.keyId) {
            viewBind.mRefreshLayout.finishRefresh()
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)
                mAdapter.setNewInstance(it)
                if (isAdded) (activity as BaseActivity).refreshData()
            }
        }
    }
    val subscribe: (adapter:ItemBaseTypeAdapter<BaseTypeBean>, data: ArrayList<BaseTypeBean>, rootView:View) -> Unit = { adapter, it, view->

        calculateJTCY(adapter,it)
    }

    private fun calculateJTCY(adapter: ItemBaseTypeAdapter<BaseTypeBean>, it: ArrayList<BaseTypeBean>) {
        it.forEach { bean ->
            when (bean.keyName) {
                "类型",
                -> {
                        bean.addOnPropertyChangedCallback(object :
                            Observable.OnPropertyChangedCallback() {
                            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                                if (propertyId == BR.valueName) {
                                    calculate(it, bean, adapter, bean.valueName)
                                }
                            }
                        })

                    calculate(it, bean, adapter,bean.valueName)
                }
            }
        }
    }
    private fun calculate(it: ArrayList<BaseTypeBean>, bean: BaseTypeBean, adapter: ItemBaseTypeAdapter<BaseTypeBean>,str: String) {
        it.forEachIndexed { index, typeBean ->
            when (typeBean.dataKey) {
                "note",
                -> {
                    if(bean.dataValue=="1"){
                        typeBean.keyName = "备注"
                    }else if(bean.dataValue=="2"){
                        typeBean.keyName = "房产位于"
                     }
                    adapter.notifyItemChanged(index)

                }
                "fwzk" ->{
                    if(bean.dataValue=="1"){
                         typeBean.visibility=false
                    }else if(bean.dataValue=="2"){
                        typeBean.visibility=true
                    }
                    adapter.notifyItemChanged(index)
                }

            }
        }
    }
    override fun onClick(v: View?) {
//        DataCtrlClass.SXDCNet.queryZxInfo(context,viewModel.creditId){}
        when (v) {
            viewBind.fab -> {
                viewBind.fab.isExpanded = true
            }
            viewBind.scrim -> {
                viewBind.fab.isExpanded = false
            }
            viewBind.bt1,-> {
                val baseTypeBean = mAdapter.data.firstOrNull { it.model == "gdzc" }
                baseTypeBean?.let { mAdapter.newBtnClick(it,subscribe) }
                viewBind.fab.isExpanded = false
            }
            viewBind.bt2,-> {
                val baseTypeBean = mAdapter.data.firstOrNull { it.model == "ldzc" }
                baseTypeBean?.let { mAdapter.newBtnClick(it,subscribe) }
                viewBind.fab.isExpanded = false
            }
            viewBind.bt3,-> {
                val baseTypeBean = mAdapter.data.firstOrNull { it.model == "ldfz" }
                baseTypeBean?.let { mAdapter.newBtnClick(it,subscribe) }
                viewBind.fab.isExpanded = false
            }
            viewBind.bt4, -> {
                val baseTypeBean = mAdapter.data.firstOrNull { it.model == "qt" }
                baseTypeBean?.let { mAdapter.newBtnClick(it,subscribe) }
                viewBind.fab.isExpanded = false
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        initData()
    }

}