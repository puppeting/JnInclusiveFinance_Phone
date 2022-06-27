package com.inclusive.finance.jh.ui.investigation

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
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentLlsrBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import org.jetbrains.anko.support.v4.act

/**
 * 履历收入
 * */
class LLSRFragment : MyBaseFragment(), PresenterClick, OnRefreshLoadMoreListener {
    lateinit var mAdapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var viewBind: FragmentLlsrBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentLlsrBinding.inflate(inflater, container, false).apply {
            presenterClick = this@LLSRFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        mAdapter = ItemBaseTypeAdapter(this@LLSRFragment)
        mAdapter.keyId = viewModel.keyId
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRefreshLayout.setOnRefreshListener(this)
        viewBind.mRecyclerView.adapter = mAdapter
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshState = Constants.RefreshState.STATE_REFRESH
        currentPage = 1
        initData()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        refreshState = Constants.RefreshState.STATE_LOAD_MORE
        initData()
    }

    override fun initData() {
        DataCtrlClass.KHGLNet.getBaseTypePoPList(requireActivity(), url = Urls.getLLSR, keyId = viewModel.keyId) {
            if (it != null) {
                viewBind.mRefreshLayout.finishRefresh()
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