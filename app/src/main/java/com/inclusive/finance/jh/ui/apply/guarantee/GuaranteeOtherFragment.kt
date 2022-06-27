package com.inclusive.finance.jh.ui.apply.guarantee

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
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentGuaranteeOtherBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act

/**
 *
其他担保
 * */
class GuaranteeOtherFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentGuaranteeOtherBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBind = FragmentGuaranteeOtherBinding.inflate(inflater, container, false).apply {
            presenterClick = this@GuaranteeOtherFragment
            viewModel = ViewModelProvider(act)[ApplyModel::class.java]
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@GuaranteeOtherFragment)
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.adapter = adapter
    }

    override fun initData() {
        DataCtrlClass.KHGLNet.getBaseTypePoPList(requireActivity(), Urls.getListDB_QTDB, keyId = viewModel.keyId) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)
                adapter.setNewInstance(it)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            viewBind.btSave -> {
                DataCtrlClass.KHGLNet.saveBaseTypePoPList(requireActivity(), Urls.saveListDB_QTDB, adapter.data, keyId = viewModel.keyId) {
                    if (it != null) {
                        if (isAdded) (activity as BaseActivity).refreshData()
                    }
                }
            }
        }


    }

}