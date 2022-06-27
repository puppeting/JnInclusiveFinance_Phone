package com.inclusive.finance.jh.ui.creditmgr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.ChipGroup
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentJjscBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act

/**
 * 借据上传
 * */
class JJSCFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentJjscBinding

    var getUrl = ""
    var saveUrl = ""
    var businessType = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentJjscBinding.inflate(inflater, container, false).apply {
            presenterClick = this@JJSCFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@JJSCFragment)
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act) //        viewBind.mRecyclerView.setItemViewCacheSize(30)
        viewBind.mRecyclerView.adapter = adapter
    }


    override fun initData() { //        val mainData = SZWUtils.getJson(context, "现场调查.json")
        //        val it = Gson().fromJson<MutableList<BaseTypeBean>>(mainData, object :
        //            TypeToken<ArrayList<BaseTypeBean>>() {}.type)
        //
        //        adapter.setNewInstance(it)

        viewBind.linearLayout.visibility = if (viewModel.seeOnly == true) View.GONE else View.VISIBLE
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_LGFK,
            -> {
                getUrl = Urls.get_yxJjsc
                saveUrl = Urls.save_yxJjsc
            }
        }
        businessType = SZWUtils.getBusinessType(viewModel.businessType)
        DataCtrlClass.CreditManagementNet.getLWHCList(requireActivity(), getUrl, viewModel.keyId, businessType = businessType) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)
                adapter.setNewInstance(it)
                if (isAdded) (activity as BaseActivity).refreshData()
            }
        }
    }


    override fun saveData() {

        DataCtrlClass.KHGLNet.saveBaseTypePoPList(context, saveUrl, adapter.data, keyId = viewModel.keyId, businessType = businessType) {
            if (it != null) {
                refreshData()
                if (isAdded) (activity as BaseActivity).refreshData()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            viewBind.btSave -> {
                saveData()
            }
            viewBind.btNext -> {
                if (viewModel.applyCheckBean?.completeCheckBean?.lgfkSecondLevelBean?.jjscCheck == true){
                    ActivityCompat.requireViewById<ChipGroup>(act, R.id.chipGroup).check(R.id.bt_lgfkyzd)
                }
            }
        }
    }

}