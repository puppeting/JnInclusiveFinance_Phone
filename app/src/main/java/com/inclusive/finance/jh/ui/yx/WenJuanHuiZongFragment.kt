package com.inclusive.finance.jh.ui.yx

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.adapter.ItemBaseListAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentWenjuandiaochaWjhzBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.BaseTypePop
import com.inclusive.finance.jh.pop.DownPop
import com.inclusive.finance.jh.utils.SZWUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import org.jetbrains.anko.support.v4.act
import java.util.ArrayList

/**
 * 问卷汇总
 * */
class WenJuanHuiZongFragment : MyBaseFragment(), PresenterClick , OnRefreshLoadMoreListener {
    lateinit var mAdapter: ItemBaseListAdapter<JsonObject>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentWenjuandiaochaWjhzBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentWenjuandiaochaWjhzBinding.inflate(inflater, container, false).apply {
            presenterClick = this@WenJuanHuiZongFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        mAdapter = ItemBaseListAdapter(this) //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        // 当数据不满一页时，是否继续自动加载（默认为true）
        //        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.layoutBaseList.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshLoadMoreListener(this)
        initStatusView()
    }
    var rwmcList = ArrayList<BaseTypeBean.Enum12>()
    private fun initStatusView() {
        rwmcList.clear() //        状态（默认异常处理中，01检验中；02异常处理中；03流程中；04完成）
        //        rwmcList.add(BaseTypeBean.Enum12().apply {
        //            valueName = "检验中"
        //            keyName = "01"
        //        })
//        status：全部：空 | 未完成：01  |  已完成：02 默认未完成
        rwmcList.add(BaseTypeBean.Enum12().apply {
            valueName="全部"
            keyName=""
        })
        rwmcList.add(BaseTypeBean.Enum12().apply {
            valueName="未完成"
            keyName="01"
        })
        rwmcList.add(BaseTypeBean.Enum12().apply {
            valueName="已完成"
            keyName="02"
        })
        val listener: (v: View) -> Unit = {
            DownPop(context, enums12 = when (it) {
                viewBind.downRwmc -> rwmcList
                else -> arrayListOf()
            }, checkedTextView = it as AppCompatCheckedTextView, isSingleChecked = true) { k, v, p ->
                when (it) {
                    viewBind.downRwmc -> status_rwmc = k
                }
            }.showPopupWindow(it)
        }
        viewBind.downRwmc.text = "未完成"
        viewBind.downRwmc.setOnClickListener(listener)
    }

    private var status_rwmc = "01"
    var getUrl = ""
    var getUrlPop = ""
    var saveUrlPop = ""
    override fun initData() { //                val mainData = SZWUtils.getJson(context, "修改担保企业担保分析.json")
        //                val list = Gson().fromJson<MutableList<BaseTypeBean>>(
        //                    mainData,
        //                    object : TypeToken<ArrayList<BaseTypeBean>>() {}.type
        //                )
        //
        //                adapter.setNewInstance(list)
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_QUESTIONNAIRE,
            -> {
                getUrl = Urls.get_wjdc_hz
                viewBind.selectlay.visibility=View.GONE
                viewBind.buttonLay.visibility=View.GONE
            }
            ApplyModel.BUSINESS_TYPE_CREDIT_REVIEW,
            -> {
                getUrl = Urls.get_sxpy_db_list
                getUrlPop = Urls.get_sxpy_db_pop
                saveUrlPop = Urls.save_sxpy_db_pop
                viewBind.selectlay.visibility=View.GONE
                viewBind.buttonLay.visibility=if (viewModel.seeOnly==true) View.GONE else View.VISIBLE
            }
            ApplyModel.BUSINESS_TYPE_COMPARISON_OF_QUOTAS,
            -> {
                getUrl = Urls.get_sxpy_db_list
                getUrlPop = Urls.get_sxpy_db_pop
                saveUrlPop = "${Urls.save_sxpy_db_pop}?taskId=${viewModel.keyId}"
//                viewBind.selectlay.visibility=if (viewModel.seeOnly==true) View.GONE else View.VISIBLE
                viewBind.buttonLay.visibility=if (viewModel.seeOnly==true) View.GONE else View.VISIBLE
            }
        }
        DataCtrlClass.YXNet.getUnityList(context = requireActivity(),url=getUrl, pageNum = currentPage,viewModel.keyId?:"",status =status_rwmc ) {
                viewBind.mRefreshLayout.finishRefresh()
                if (it != null) {
                    if (refreshState == Constants.RefreshState.STATE_REFRESH) {
                        mAdapter.initTitleLay(context, viewBind.layoutBaseList.root, it) {
                            mAdapter.setNewInstance(it.list)
                        }
                    } else {
                        mAdapter.addData(it.list)

                    }
                    if (!it.list.isNullOrEmpty()) {
                        viewBind.mRefreshLayout.finishLoadMore()
                        currentPage++
                    } else {
                        viewBind.mRefreshLayout.finishLoadMoreWithNoMoreData()
                    }
                } else {
                    viewBind.mRefreshLayout.finishLoadMoreWithNoMoreData()
                }

            }
        }

        override fun onRefresh(refreshLayout: RefreshLayout) {
            refreshState = Constants.RefreshState.STATE_REFRESH
            currentPage = 1
            viewBind.root.postDelayed({ initData() }, 0)
        }

        override fun onLoadMore(refreshLayout: RefreshLayout) {
            refreshState = Constants.RefreshState.STATE_LOAD_MORE
            initData()
        }

    private var mLastClickTime: Long = 0
    private val TIME_INTERVAL = 500L
    override fun onClick(v: View?) {
        if (v == viewBind.btSearch) {
            if (System.currentTimeMillis() - mLastClickTime > TIME_INTERVAL) {
                viewBind.mRefreshLayout.autoRefresh()
                mLastClickTime = System.currentTimeMillis()
            }
            return
        }
        if (v != null) SZWUtils.getJsonObjectBeanFromList(mAdapter.data) { jsonObject ->
            when (v) {
                viewBind.chipEdit -> {
                    BaseTypePop(context, this, "修改",getUrl = getUrlPop, saveUrl = saveUrlPop ,json=jsonObject, ) {adapter,resultStr->
                        viewBind.mRefreshLayout.autoRefresh()
                        if (isAdded)
                            (activity as BaseActivity).refreshData()
                    }.show(childFragmentManager, this.javaClass.name)
                }
                else -> {
                }
            }
        }
    }

}