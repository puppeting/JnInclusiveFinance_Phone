package com.inclusive.finance.jh.ui.investigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.PreferencesService
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentXjlBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act

/**
 * 现金流
 * */
class XJLFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentXjlBinding
    var getUrl=""
    var getUrl2=""

    var businessType=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentXjlBinding.inflate(inflater, container, false).apply {
            presenterClick = this@XJLFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data=viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@XJLFragment)
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.setItemViewCacheSize(30)
        viewBind.mRecyclerView.adapter=adapter
    }

    override fun initData() {
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_APPLY,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE,
            -> {
                getUrl=Urls.getXJL
                getUrl2=Urls.getlistGtjkr
                viewBind.chipGroup.visibility=View.VISIBLE
                getJtcy()
            }
            ApplyModel.BUSINESS_TYPE_JNJ_CJ_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_CJ_COMPANY,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_SJ_PERSONAL,
            ApplyModel.BUSINESS_TYPE_SJ_COMPANY,
            ApplyModel.BUSINESS_TYPE_RC_OFF_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_RC_ON_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_RC_ON_SITE_COMPANY,
            -> {
                getUrl=Urls.get_jnj_cj_personal_xjl
            }

        }
        businessType=SZWUtils.getBusinessType(viewModel.businessType)
        DataCtrlClass.KHGLNet.getBaseTypePoPList(requireActivity(),url = getUrl,keyId =  viewModel.keyId,businessType = businessType) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel,it)
                adapter.setNewInstance(it)
                if (isAdded)
                    (activity as BaseActivity).refreshData()
            }
        }
    }


    fun getJtcy(){
        DataCtrlClass.ApplyNet.getJiaTing(requireActivity(), url = Urls.getJtxx, keyId=viewModel.keyId, viewModel.jsonObject) {
            if (it != null) {

                if(it?.size!! >1){
                     viewBind?.btDetailWei?.visibility=View.VISIBLE
                }else{
                    viewBind?.btDetailWei?.visibility=View.GONE
                }
                 }
                if (isAdded)
                    (activity as BaseActivity).refreshData()
            }
        }

    override fun onClick(v: View?) {
        when (v) {

            viewBind.btSimple->{

                 DataCtrlClass.KHGLNet.getBaseTypePoPList(requireActivity(),url = getUrl,keyId =  viewModel.keyId,businessType = businessType) {
                    if (it != null) {
                        SZWUtils.setSeeOnlyMode(viewModel,it)
                        adapter.setNewInstance(it)
                        if (isAdded)
                            (activity as BaseActivity).refreshData()
                    }
                }

            }
            viewBind.btDetailWei->{
                adapter.setNewInstance(null)
                DataCtrlClass.KHGLNet.getBaseTypePoPList(requireActivity(),url = getUrl2,keyId =  viewModel.keyId,businessType = businessType) {
                    if (it != null) {
                        SZWUtils.setSeeOnlyMode(viewModel,it)
                        adapter.setNewInstance(it)
                        if (isAdded)
                            (activity as BaseActivity).refreshData()
                    }
                }

            }
            //            viewBind.btNext -> {
            //                ConfirmPop(context,""){
            //
            //                }.showPopupWindow()
            //            }
        }
    }

}