package com.inclusive.finance.jh.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.SizeUtils
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.adapter.ItemMainAdapter
import com.inclusive.finance.jh.base.MainNavigationFragment
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.databinding.FragmentMainBinding
import com.inclusive.finance.jh.pop.AccountPop
import com.inclusive.finance.jh.utils.GridAutofitLayoutManager
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.jetbrains.anko.support.v4.act

class MainFragment : MainNavigationFragment(), OnRefreshListener {
    private var accountPop: AccountPop? = null
    lateinit var viewModel: MainActivityModel
    lateinit var adapter: ItemMainAdapter
    var viewBind: FragmentMainBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act).get(MainActivityModel::class.java)
        if (viewBind == null) {
            viewBind = FragmentMainBinding.inflate(inflater, container, false).apply {
                data = viewModel
                adapter = ItemMainAdapter(viewModel)
                var manager=GridLayoutManager(requireContext(),3)
//              GridAutofitLayoutManager(requireContext(), SizeUtils.dp2px(84f))
                mRecyclerView.layoutManager= manager
                mRecyclerView.adapter = adapter
                lifecycleOwner = viewLifecycleOwner

            }
            oneInit()
        }
        viewBind?.lifecycleOwner = viewLifecycleOwner
        return viewBind?.root ?: View(context)
    }

    private fun oneInit() {
        StatusBarUtil.darkMode(act)
//        StatusBarUtil.setPaddingSmart(act, viewBind?.actionBarCustom?.appBar)
        viewBind?.mRefreshLayout?.setOnRefreshListener(this)
        viewBind?.mRefreshLayout?.setEnableLoadMore(false)
//        viewBind?.actionBarCustom?.toolbar?.menu?.add("欢迎您")?.apply {
//            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
//            actionView = TextView(context).apply {
//                setPadding(15)
//                text = "欢迎您"
//            }
//        }

        onRefresh(viewBind?.mRefreshLayout!!)
    }

    //    override fun initView() {
    ////        val mainData = SZWUtils.getJson(context, "mainData.json")
    ////        val list = Gson().fromJson<MutableList<MainMenuBean>>(mainData, object :
    ////            TypeToken<ArrayList<MainMenuBean>>() {}.type)
    ////        adapter.setNewInstance(list)
    //
    //    }
    /*
    * 横竖屏切换时 ，刷新子布局高度。
    * */
    override fun onConfigurationChanged(newConfig: Configuration) {
        adapter.notifyDataSetChanged()
        super.onConfigurationChanged(newConfig)
    }

    /**
    登录后刷新数据，
     */
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(Constants.BusAction.Bus_LoginSuccess)])
    fun loginSuccess(str: String) {
        onRefresh(viewBind?.mRefreshLayout!!)
    }

    override fun refreshData(type: Int?) {

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        //        val mainData = SZWUtils.getJson(context, "mainData.json")
        //        val list = Gson().fromJson<MutableList<MainMenuBean>>(mainData, object :
        //            TypeToken<ArrayList<MainMenuBean>>() {}.type)
        //        adapter.setNewInstance(list)
//        if (MyApplication.user != null) (viewBind?.actionBarCustom?.toolbar?.menu?.getItem(0)?.actionView as TextView).text =
//            "欢迎：${(MyApplication.user as User).userInfo?.realname}"
        DataCtrlClass.getMainMenuList(requireActivity()) {
            viewBind?.mRefreshLayout?.finishRefresh()
            if (it != null) {
                adapter.setNew(it)
            }
        }
    }
}