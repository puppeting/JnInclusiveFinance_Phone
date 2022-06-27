package com.inclusive.finance.jh.ui.apply.report

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
import com.inclusive.finance.jh.databinding.FragmentReportSimpleBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act

/**
 * 调查报告·简
 * */
class ReportSimpleFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentReportSimpleBinding
    private var getUrl = ""
    private var saveUrl = ""
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        viewBind = FragmentReportSimpleBinding.inflate(inflater, container, false).apply {
            presenterClick = this@ReportSimpleFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@ReportSimpleFragment)
        adapter.keyId = viewModel.keyId
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.setItemViewCacheSize(30)
        viewBind.mRecyclerView.adapter=adapter
    }

    override fun initData() {
        //        val mainData = SZWUtils.getJson(context, "征信授权.json")
        //        val list = Gson().fromJson<MutableList<BaseTypeBean>>(
        //            mainData,
        //            object : TypeToken<ArrayList<BaseTypeBean>>() {}.type
        //        )
        //        adapter.setNewInstance(list)
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_INVESTIGATE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE, -> {//
                getUrl = Urls.get_sxdc_dcbg
            }
        }
        DataCtrlClass.KHGLNet.getBaseTypePoPList(requireActivity(), getUrl, keyId = viewModel.keyId) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)
                 adapter.setNewInstance(it)
            }
        }
    }

    override fun saveData() {
        DataCtrlClass.KHGLNet.saveBaseTypePoPList(context, saveUrl, adapter.data, keyId = viewModel.keyId) {
            if (it != null) {
                if (isAdded)
                    (activity as BaseActivity).refreshData()
            }
        }
    }

    override fun onClick(v: View?) {

    }

}