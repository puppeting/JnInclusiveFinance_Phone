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
import com.inclusive.finance.jh.databinding.FragmentGsxxBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act

/**
 * 工商信息
 * */
class GSXXFragment : MyBaseFragment(), PresenterClick {
    lateinit var mAdapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentGsxxBinding
    var getUrl = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentGsxxBinding.inflate(inflater, container, false).apply {
            presenterClick = this@GSXXFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        mAdapter = ItemBaseTypeAdapter(this@GSXXFragment)
        mAdapter.keyId = viewModel.keyId
        mAdapter.subscribe = subscribe
//        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
//        viewBind.mRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
//            ContextCompat.getDrawable(requireContext(), R.drawable.divider_base)
//                ?.let { setDrawable(it) }
//        })
        viewBind.mRecyclerView.adapter = mAdapter
    }

    val subscribe: (adapter: ItemBaseTypeAdapter<BaseTypeBean>, data: ArrayList<BaseTypeBean>, rootView: View) -> Unit = { adapter, it, view ->
        calculateJTCY(adapter, it)
    }

    private fun calculateJTCY(adapter: ItemBaseTypeAdapter<BaseTypeBean>, it: ArrayList<BaseTypeBean>) {
        it.forEach { bean ->
            when (bean.dataKey) {
                "shopTypeCd",
                -> {
                    bean.addOnPropertyChangedCallback(object :
                        Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                calculate(it, bean, adapter)
                            }
                        }
                    })
                    calculate(it, bean, adapter)
                }
            }
        }
    }

    private fun calculate(it: ArrayList<BaseTypeBean>, bean: BaseTypeBean, adapter: ItemBaseTypeAdapter<BaseTypeBean>) {
        it.forEachIndexed { index, typeBean ->
            when (typeBean.dataKey) {
                "companyPropertyCd",
                -> {
                    if (typeBean.visibility != (bean.valueName == "2")) {
                        typeBean.visibility = bean.valueName == "2"
                        adapter.notifyItemChanged(index)
                    }

                }
            }
        }
    }

    override fun initData() {
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_APPLY -> {
                getUrl = Urls.getGSXX
            }
            ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY -> {
                getUrl = Urls.getSunshineGSXX
            }
            else -> {
                getUrl = Urls.getGSXX
            }
        }
        DataCtrlClass.KHGLNet.getBaseTypePoPList(requireActivity(), url = getUrl, keyId = viewModel.keyId) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)
                mAdapter.setNewInstance(it)
                if (isAdded) (activity as BaseActivity).refreshData()
            }
        }
    }


    override fun onClick(v: View?) {
        if (mAdapter.data.size>0)
        mAdapter.newBtnClick(mAdapter.data[0])
    }

}