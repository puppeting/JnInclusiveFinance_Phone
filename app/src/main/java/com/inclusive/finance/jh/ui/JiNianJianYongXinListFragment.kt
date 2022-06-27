package com.inclusive.finance.jh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.JsonObject
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.IRouter
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseListCardAdapter
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentJinianjianyongxinListBinding
import com.inclusive.finance.jh.databinding.ItemBaseListCardBinding
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.ui.filter.Filter
import com.inclusive.finance.jh.ui.filter.Filter.TagFilter
import com.inclusive.finance.jh.ui.filter.FiltersFragment
import com.inclusive.finance.jh.ui.filter.SearchViewModel
import com.inclusive.finance.jh.ui.filter.Tag
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.act


/**
 * 检验用信列表
 * */
@AndroidEntryPoint
class JiNianJianYongXinListFragment : MyBaseFragment(), OnItemChildClickListener,
    OnRefreshLoadMoreListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentJinianjianyongxinListBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListCardAdapter<JsonObject>
    var event: Lifecycle.Event? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act)[MainActivityModel::class.java]

        viewBind = FragmentJinianjianyongxinListBinding.inflate(inflater, container, false).apply {
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    var dataList: MutableList<Filter> = mutableListOf()
    var subscribeChildLayoutDrawListener: (holder: BaseViewHolder, item: JsonObject) -> Unit = { holder, _ ->
        val viewBind = DataBindingUtil.getBinding<ItemBaseListCardBinding>(holder.itemView)
        viewBind?.btMore?.visibility = View.VISIBLE
        viewBind?.btSeeOnly?.visibility = View.GONE
        viewBind?.btChange?.visibility = View.GONE

    }
    var data: LinkedHashMap<String, ArrayList<BaseTypeBean.Enum12>>?=LinkedHashMap()

    override fun initView() {
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.mTitle.text = "季年检"


        mAdapter = ItemBaseListCardAdapter(this)
        //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        // 当数据不满一页时，是否继续自动加载（默认为true）
        //        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.mRecyclerView.adapter = mAdapter
        mAdapter.setOnItemChildClickListener(this)
        mAdapter.subscribeChildLayoutDrawListener = subscribeChildLayoutDrawListener
        viewBind.mRefreshLayout.setOnRefreshLoadMoreListener(this)
//        mAdapter.itemRecyclerViewBackGroundColor={ item, holder ->
//            when {
//                SZWUtils.getJsonObjectBoolean(item, "isCheck") -> R.color.color_main_orangeAlpha
//                SZWUtils.getJsonObjectString(item, "color")=="1" -> R.color.color_main_redAlpha
//                holder.adapterPosition % 2 == 0 -> R.color.white
//                else -> R.color.line2
//            }
//        }
        viewBind.actionBarCustom.toolbar.apply {
            inflateMenu(R.menu.add_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_open_filters -> {
                        val fragment = findFiltersFragment()
                        fragment?.showFiltersSheet()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }

        initStatusView()
        mAdapter.setOnItemClickListener { _, _, position ->
            //单选
//            if (isSingleCheck){
//                mAdapter.checkListBean.find { it.checked }?.let { bean ->
//                    bean.checked=false
//                    mAdapter.notifyItemChanged(mAdapter.checkListBean.indexOf(bean))
//                }
//            }
//            mAdapter.checkListBean[position].checked = !mAdapter.checkListBean[position].checked
        }
        viewBind.ivSelect.setOnClickListener {

             SZWUtils.getJsonObjectBeanListFromList(mAdapter.data) { jsonObject ->
                 val sb=StringBuffer()
                 val relationid=StringBuffer()
                 val suggestionid=StringBuffer()

                 jsonObject.forEachIndexed { _, kvBean ->
                     val jsonObjectString = SZWUtils.getJsonObjectString(kvBean, "flag")
                      val id = SZWUtils.getJsonObjectString(kvBean, "id")
                     when (jsonObjectString) {
                         "0" -> {
                             SZWUtils.showSnakeBarMsg("存在无法操作数据")
                             return@getJsonObjectBeanListFromList
                         }
                         "1" -> {
                             sb.append("$id,")
                             relationid.append("$id,")
                         }
                         "2" -> {
                             sb.append("$id,")
                             suggestionid.append("$id,")
                         }
                         else -> {
                             sb.append("$id,")
                         }
                     }

                 }
                 DataCtrlClass.RCJNet.plSubmit(requireActivity(),Urls.dhxtnewFlow,sb.toString(),"8"){
                     if(it==200){

                         EditPopBath(context, title = "签署意见"){
                             val result=it.split(",")
                             DataCtrlClass.RCJNet.plhandleFlowBatch(requireActivity(),Urls.handleFlowBatchFlow,relationid.toString(),suggestionid.toString(),result[0],result[1],"8") {
                                 refreshData()
                             }

                         }.show(childFragmentManager, this.javaClass.name)
                     }
                 }

             }
        }
        DataCtrlClass.RCJNet.getJGMCList(requireActivity()) {
            if (it != null) {
                jgList = arrayListOf()
                var index = 0
                val mdata = ArrayList<BaseTypeBean.Enum12>()

                data?.set("机构", mdata)
                it.forEach { jsonObject ->
                    jgList.add(BaseTypeBean.Enum12().apply {
                        keyName = SZWUtils.getJsonObjectString(jsonObject, "orgCode")
                        valueName = SZWUtils.getJsonObjectString(jsonObject, "departName")
                        val elements0 = TagFilter(Tag("1$index", "orgCode", "机构", valueName = "" + valueName, keyName = "0$keyName", isSingleCheck = true))
                        dataList.add(elements0)
                        val bean=BaseTypeBean.Enum12()
                        bean.keyName=keyName
                        bean.valueName=valueName
                        mdata.add(bean)
                        index++
                    })
                }
                if (jgList.size > 0) {
                    orgCode = jgList[0].keyName
                }
                val viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
                lifecycleScope.launch {
                    delay(500)
                    dataList.let { it1 ->
                        viewModel.setSupportedFilters(it1)
                        viewModel.toggleFilter(dataList[1], enabled = true, singleCheck = true)
                     }
                }
                viewBind.mRefreshLayout.autoRefresh()
            }
        }
    }

    private var jgList = ArrayList<BaseTypeBean.Enum12>()
    private val listMenuDatas = mutableListOf<String>().apply {
        add("检查")
        add("查看检查")
        add("提交")
        add("查看进度")

    }
    private var dbztStatus = ""
     private fun initStatusView() {
        jgList.clear()
        val elements1 = TagFilter(Tag("0", "processStatus", "流程状态", valueName = "检验中", keyName = "01", isSingleCheck = true))
        val elements2 = TagFilter(Tag("2", "processStatus", "流程状态", valueName = "待处理", keyName = "02", isSingleCheck = true))
        val elements3 = TagFilter(Tag("3", "processStatus", "流程状态", valueName = "流程中", keyName = "03", isSingleCheck = true))
        val elements4 = TagFilter(Tag("4", "processStatus", "流程状态", valueName = "完成", keyName = "04", isSingleCheck = true))
        val elements5 = TagFilter(Tag("5", "checkStatus", "检验状态", valueName = "全部", keyName = "", isSingleCheck = true))
        val elements6 = TagFilter(Tag("6", "checkStatus", "检验状态", valueName = "异常", keyName = "02", isSingleCheck = true))
        val elements7 = TagFilter(Tag("7", "checkStatus", "检验状态", valueName = "正常", keyName = "01", isSingleCheck = true))
        val elements8 = TagFilter(Tag("8", "dbztStatus", "是否待办", valueName = "是", keyName = "1", isSingleCheck = true))
        val elements9= TagFilter(Tag("9", "dbztStatus", "是否待办", valueName = "否", keyName = "0", isSingleCheck = true))
        dataList.add(elements1)
        dataList.add(elements2)
        dataList.add(elements3)
        dataList.add(elements4)
        dataList.add(elements5)
        dataList.add(elements6)
        dataList.add(elements7)
        dataList.add(elements8)
        dataList.add(elements9)
        //
        val mdata = ArrayList<BaseTypeBean.Enum12>()
        data?.set("流程状态", mdata)
        val bean=BaseTypeBean.Enum12()
        bean.keyName="01"
        bean.valueName="检验中"
        val bean2=BaseTypeBean.Enum12()
        bean2.keyName="02"
        bean2.valueName="待处理"
        val bean3=BaseTypeBean.Enum12()
        bean3.keyName="03"
        bean3.valueName="流程中"
        val bean4=BaseTypeBean.Enum12()
        bean4.keyName="04"
        bean4.valueName="完成"
        mdata.add(bean)
        mdata.add(bean2)
        mdata.add(bean3)
        mdata.add(bean4)

        val mdatajy = ArrayList<BaseTypeBean.Enum12>()
        data?.set("检验状态", mdatajy)
        mdatajy.add(BaseTypeBean.Enum12().apply {
            keyName=" "
            valueName="全部"
            checked=false
        })
        mdatajy.add(BaseTypeBean.Enum12().apply {
            keyName="02"
            valueName="异常"
            checked=false
        })
        mdatajy.add(BaseTypeBean.Enum12().apply {
            keyName="01"
            valueName="正常"
            checked=false

        })
        val mdatadb = ArrayList<BaseTypeBean.Enum12>()
        data?.set("是否待办", mdatadb)
        mdatadb.add(BaseTypeBean.Enum12().apply {
            keyName="1"
            valueName="是"
            checked=false
        })
        mdatadb.add(BaseTypeBean.Enum12().apply {
            keyName="0"
            valueName="否"
            checked=false

        })
    }


    private var searchStr = ""
     private var checkStatus = ""
    private var processStatus = "02"
    private var orgCode = ""
    private var resultCountCallBack: ((count: String) -> Unit)?=null
    override fun initData() {
        DataCtrlClass.JNJNet.getJNJYXList(
            context = requireActivity(), pageNum = currentPage, searchStr = searchStr, orgCode = orgCode, status = processStatus, checkResult = checkStatus,daibanstatus = dbztStatus
        ) {
            viewBind.mRefreshLayout.finishRefresh()
            if (it != null) {

                if (refreshState == Constants.RefreshState.STATE_REFRESH) {
                    mAdapter.setListData(bean = it, list = it.list)
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
            resultCountCallBack?.invoke(mAdapter.data.size.toString())

        }
    }

    override fun refreshData(type: Int?) {
        currentPage=1
        viewBind.mRefreshLayout.autoRefresh()

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


    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val jsonObject = mAdapter.data[position]
        val checkFlag = SZWUtils.getJsonObjectString(jsonObject, "checkFlag")
        val flag = SZWUtils.getJsonObjectString(jsonObject, "flag")
        when (view.id) {
            R.id.bt_more -> {
                BaseListMenuPop(requireActivity(), listMenuDatas) {
                    when (listMenuDatas[it]) {
                        "检查" -> {
                            when {
                                checkFlag.contains("1") -> IRouter.goF(view, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "id"), ApplyModel.BUSINESS_TYPE_JNJ_YX, false)
                                else -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                            }

                        }
                        "查看检查" -> {
                            IRouter.goF(view, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "id"), ApplyModel.BUSINESS_TYPE_JNJ_YX, true)
                        }
                        "提交" -> {
                            when {
                                flag.contains("0") -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                                flag.contains("2") -> {
                                    DataCtrlClass.SXSPNet.getSXSPById(requireActivity(), keyId = SZWUtils.getJsonObjectString(jsonObject, "id"), businessType = ApplyModel.BUSINESS_TYPE_JNJ_YX_SUBMIT, type = SZWUtils.getBusinessType(ApplyModel.BUSINESS_TYPE_JNJ_YX_SUBMIT)) { configurationBean ->
                                        if (configurationBean != null) {
//                                            ProcessProcessingPop(context, configurationBean, keyId = SZWUtils.getJsonObjectString(jsonObject, "id"), businessType = ApplyModel.BUSINESS_TYPE_JNJ_YX2) {
//                                                viewBind.mRefreshLayout.autoRefresh()
//                                            }.show(childFragmentManager, this.javaClass.name)
                                            ApprovalSuggestionPop(
                                                context,""
                                                , keyId = SZWUtils.getJsonObjectString(jsonObject, "id"), businessType = ApplyModel.BUSINESS_TYPE_JNJ_YX_SUBMIT
                                            ) {
                                                refreshData()
                                            }.show(childFragmentManager, this.javaClass.name)
                                        }
                                    }

                                }
                                else -> DataCtrlClass.SXSPNet.getSXSPById(requireActivity(), keyId = SZWUtils.getJsonObjectString(jsonObject, "id"), businessType = ApplyModel.BUSINESS_TYPE_JNJ_YX, type = SZWUtils.getBusinessType(ApplyModel.BUSINESS_TYPE_JNJ_YX)) { configurationBean ->
                                    if (configurationBean != null) {
                                        ProcessProcessingPop(context, configurationBean, keyId = SZWUtils.getJsonObjectString(jsonObject, "id"), businessType = ApplyModel.BUSINESS_TYPE_JNJ_YX) {
                                            viewBind.mRefreshLayout.autoRefresh()
                                        }.show(childFragmentManager, this.javaClass.name)
                                    }
                                }
                            }
                        }
                        "查看进度" -> {
                            CheckProgressPop(context, SZWUtils.getJsonObjectString(jsonObject, "id"), type = SZWUtils.getBusinessType(ApplyModel.BUSINESS_TYPE_JNJ_YX), businessType = ApplyModel.BUSINESS_TYPE_JNJ_YX).show(childFragmentManager, this.javaClass.name)

                        }
                        else -> {
                        }
                    }
                }.showPopupWindow(view)
            }

        }
    }

    /**
    返回后刷新数据，
     */
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [com.hwangjr.rxbus.annotation.Tag(Constants.BusAction.Bus_Refresh_List)])
    fun backRefresh(str: String) {
        viewBind.mRefreshLayout.autoRefresh()
    }


    private var filtersFragment: FiltersFragment? = null
    private fun findFiltersFragment(): FiltersFragment? { //        if (filtersFragment == null) {

        filtersFragment = FiltersFragment(this) { list, str,c ->
            resultCountCallBack=c
            searchStr = str
            checkStatus = ""
            processStatus = ""
            orgCode = ""
            list.forEach {
                val tagFilter = it.filter as TagFilter
                when (tagFilter.tag.categoryId) {
                    "checkStatus" -> {
                        checkStatus = tagFilter.tag.keyName
                    }
                    "processStatus" -> {
                        processStatus = tagFilter.tag.keyName
                    }
                    "orgCode" -> {
                        orgCode = tagFilter.tag.keyName
                    }
                    "dbztStatus" -> {
                        dbztStatus = tagFilter.tag.keyName
                    }
                }
            }
            refreshData()
        } //        }
        filtersFragment?.show(childFragmentManager, this.javaClass.name)

        return filtersFragment
    }
}