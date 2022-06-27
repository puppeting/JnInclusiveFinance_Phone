package com.inclusive.finance.jh.ui.yx

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
import com.inclusive.finance.jh.databinding.FragmentWenjuandiaochaXxyzpBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.ui.ApplyActivity
import com.inclusive.finance.jh.ui.ApplyActivity.Companion.ClassType.SXPY
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act

/**
 * 信息员照片
 * */
class XinXiYuanZhaoPianFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentWenjuandiaochaXxyzpBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentWenjuandiaochaXxyzpBinding.inflate(inflater, container, false).apply {
            presenterClick = this@XinXiYuanZhaoPianFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@XinXiYuanZhaoPianFragment)
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.adapter = adapter
    }

    var getUrl = ""
    var saveUrl = ""
    var temporaryUrl = ""
    var businessType = ""
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
                getUrl = Urls.get_wjdc_xxy
                saveUrl = Urls.save_wjdc_xxy
            }
            ApplyModel.BUSINESS_TYPE_CREDIT_REVIEW,
            -> {
                getUrl = Urls.get_sxpy_xxy
                saveUrl = Urls.save_sxpy_xxy
            }
        }
        businessType = SZWUtils.getBusinessType(viewModel.businessType)
        DataCtrlClass.YXNet.getXXYInfo(requireActivity(), getUrl, keyId = viewModel.keyId, type = "edit") {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)
                adapter.setNewInstance(it)
            }
        }

    }

    override fun saveData() {
        DataCtrlClass.YXNet.saveXXYList(context, saveUrl, keyId = viewModel.keyId, json = adapter.data) {
            if (it != null) {
                try {
                    if (isAdded) (activity as BaseActivity).refreshData()
                    val applyActivity = activity as ApplyActivity
                    applyActivity.onClick(SXPY)
                } catch (e: Exception) {
                    e.printStackTrace()
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