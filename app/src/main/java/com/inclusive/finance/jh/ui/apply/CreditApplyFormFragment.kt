package com.inclusive.finance.jh.ui.apply

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
import com.inclusive.finance.jh.databinding.FragmentCreditApplyFormBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act
import java.util.*
import kotlin.collections.ArrayList

/**
 * 授信申请单
 * */
class CreditApplyFormFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentCreditApplyFormBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentCreditApplyFormBinding.inflate(inflater, container, false).apply {
            presenterClick = this@CreditApplyFormFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@CreditApplyFormFragment)
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.setItemViewCacheSize(30)
        viewBind.mRecyclerView.adapter = adapter
    }

    override fun initData() {
        DataCtrlClass.KHGLNet.getBaseTypePoPList(requireActivity(), url = Urls.getSXSQD, keyId = viewModel.keyId) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)
                calculate(adapter,it)
                adapter.setNewInstance(it)
            }
        }
    }

    private fun calculate(adapter: ItemBaseTypeAdapter<BaseTypeBean>, it: ArrayList<BaseTypeBean>) {
        it.forEach { bean ->
            when (bean.dataKey) {
                "startDate",
                "endDate",
                -> {
                    bean.addOnPropertyChangedCallback(object :
                        Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                val startDate = it.firstOrNull { item -> item.dataKey == "startDate" }?.valueName
                                val endDate = it.firstOrNull { item -> item.dataKey == "endDate" }?.valueName
                                if (!startDate.isNullOrEmpty() && !endDate.isNullOrEmpty()) {
                                    it.firstOrNull { item -> item.dataKey == "term" }?.valueName = SZWUtils.getMonthSpace(endDate, startDate).toString()
                                }
                            }
                        }
                    })
                }
            }
        }
    }




    override fun saveData() {
        DataCtrlClass.KHGLNet.saveBaseTypePoPList(context, Urls.saveSXSQD, adapter.data, keyId = viewModel.keyId) {
            if (it != null) {
                if (isAdded)
                (activity as BaseActivity).refreshData()
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