package com.inclusive.finance.jh.ui.apply

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
import com.inclusive.finance.jh.databinding.FragmentPicListBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act

/**
 * 影像资料
 * */
class PicListFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentPicListBinding

    var getUrl = ""
    var saveUrl = ""
    var businessType = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentPicListBinding.inflate(inflater, container, false).apply {
            presenterClick = this@PicListFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@PicListFragment)
        adapter.keyId = viewModel.keyId
        adapter.type = "yxzl"
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        //        viewBind.mRecyclerView.setItemViewCacheSize(30)
        viewBind.mRecyclerView.adapter = adapter
    }


    override fun initData() {
        //        val mainData = SZWUtils.getJson(context, "现场调查.json")
        //        val it = Gson().fromJson<MutableList<BaseTypeBean>>(mainData, object :
        //            TypeToken<ArrayList<BaseTypeBean>>() {}.type)
        //
        //        adapter.setNewInstance(it)

        viewBind.linearLayout.visibility=if (viewModel.seeOnly==true)View.GONE else View.VISIBLE
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_APPLY,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE,
            ApplyModel.BUSINESS_TYPE_PICADD,
            -> {
                getUrl = Urls.getPicList
                saveUrl = Urls.savePicList
            }
            ApplyModel.BUSINESS_TYPE_JNJ_CJ_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_CJ_COMPANY,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL,
            -> {
                getUrl = Urls.get_jnj_cj_personal_yxzl
                saveUrl = Urls.save_jnj_cj_personal_yxzl
            }
            ApplyModel.BUSINESS_TYPE_SJ_PERSONAL,
            ApplyModel.BUSINESS_TYPE_SJ_COMPANY,
            ApplyModel.BUSINESS_TYPE_RC_OFF_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_RC_ON_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_RC_ON_SITE_COMPANY,
            -> {
                viewBind.linearLayout.visibility=View.GONE
                getUrl = Urls.get_jnj_cj_personal_yxzl
                saveUrl = Urls.save_jnj_cj_personal_yxzl
            }
            ApplyModel.BUSINESS_TYPE_SJ,
            -> {
                getUrl = Urls.get_sj_picList
                saveUrl = Urls.save_sj_picList
            }
            ApplyModel.BUSINESS_TYPE_VISIT_NEW,
            ApplyModel.BUSINESS_TYPE_VISIT_EDIT,
            ApplyModel.BUSINESS_TYPE_PRECREDIT,
            -> {
                getUrl = Urls.get_visit_yxzl
                saveUrl = Urls.save_visit_yxzl
            }
            ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER,
            -> {
                getUrl = Urls.get_yxgl_yxzl
                saveUrl = Urls.save_yxgl_yxzl
            }
            ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY,
            -> {
                getUrl = Urls.get_sunshine_yxzl
                saveUrl = Urls.save_sunshine_yxzl
            }
        }
        businessType = SZWUtils.getBusinessType(viewModel.businessType)
        DataCtrlClass.ApplyNet.getPicList(requireActivity(), getUrl, viewModel.keyId,  businessType = businessType) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)
                adapter.setNewInstance(it)
                if (isAdded) (activity as BaseActivity).refreshData()
            }
        }
    }


    override fun saveData() {

        DataCtrlClass.KHGLNet.saveBaseTypePoPList(requireActivity(), saveUrl, adapter.data, keyId = viewModel.keyId, businessType = businessType) {
            if (it != null) {
                if (isAdded) (activity as BaseActivity).refreshData()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            viewBind.btSave -> {
                saveData()
            }
            //            viewBind.btNext -> {
            //                ConfirmPop(context,""){
            //
            //                }.showPopupWindow()
            //            }
        }
    }

}