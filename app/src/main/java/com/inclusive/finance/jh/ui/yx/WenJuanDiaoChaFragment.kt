package com.inclusive.finance.jh.ui.yx

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonParser
import com.inclusive.finance.jh.BR
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseListCardAdapter
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentWenjuandiaochaWjdcBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act
import java.util.*

/**
 * 信息员照片
 * */
class WenJuanDiaoChaFragment : MyBaseFragment(), PresenterClick {
    
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentWenjuandiaochaWjdcBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBind = FragmentWenjuandiaochaWjdcBinding.inflate(inflater, container, false).apply {
            presenterClick = this@WenJuanDiaoChaFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@WenJuanDiaoChaFragment)
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.adapter = adapter
        adapter.textListItemConfig = { parentItem, parentPosition, sonItem, sonPosition ->
            ItemBaseListCardAdapter.TextListItemConfig().apply {
                when {
                    SZWUtils.getJsonObjectString(sonItem, "sxdb") == "是" -> {
                        textDefaultColor = R.color.colorPrimary
                    }
                }
            }
        }
    }

    val subscribe_wjdc: (adapter: ItemBaseTypeAdapter<BaseTypeBean>, data: ArrayList<BaseTypeBean>,rootView:View) -> Unit = { adapter, it, view->
        fun calculate(it: ArrayList<BaseTypeBean>, bean: BaseTypeBean, adapter: ItemBaseTypeAdapter<BaseTypeBean>) {

            it.forEachIndexed{ index, typeBean ->
                //            businessItems：经营项目 取消必填校验
                //            annualIncome: 年收入  取消必填校验
                //            familyMember : 家庭成员是否发生变化 取消必填校验
                when (typeBean.dataKey) {
                    "businessItems",
                    "annualIncome",
                    "familyMember",
                    -> {
                        typeBean.requireable = bean.valueName == "0"
                        adapter.notifyItemChanged(index)
                    }
                }
            }
        }
        it.forEach { bean ->
            when (bean.dataKey) {
                "waitCreditReasonCode",
                -> {
                    bean.addOnPropertyChangedCallback(object :
                        Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                calculate(it, bean, adapter)
                            }
                        }
                    })
                    calculate(it, bean, adapter)
                }
            }
        }
    }

    val subscribe_sxpy: (adapter: ItemBaseTypeAdapter<BaseTypeBean>, data: ArrayList<BaseTypeBean>,rootView:View) -> Unit = { adapter, it, view->
        fun calculate(it: ArrayList<BaseTypeBean>, bean: BaseTypeBean, adapter: ItemBaseTypeAdapter<BaseTypeBean>) {
          val requireable= when (bean.dataKey) {
              "sxdb" -> {
                  bean.valueName!="02"
              }
              "waitCreditReasonCode" -> {
                  bean.valueName=="0"
              }
              else -> {
                  true
              }
          }
            it.forEachIndexed { index, typeBean ->
//                jtfzThreeToFive:家庭负债3-5万  取消必填校验
//                jtfzOverFive:家庭负债5万以上  取消必填校验
//                familyManage:家庭经营不正常  取消必填校验
//                jtfsblbg:家庭发生不良变故   取消必填校验
                when (typeBean.dataKey) {
                    "jtfzThreeToFive",
                    "jtfzOverFive",
                    "familyManage",
                    "jtfsblbg",
                    -> {
                        typeBean.requireable = requireable
                        adapter.notifyItemChanged(index)
                    }

                }
            }
        }
        it.forEach { bean ->
            when (bean.dataKey) {
                "sxdb",
                "waitCreditReasonCode"
                -> {
                    bean.addOnPropertyChangedCallback(object :
                        Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            if (propertyId == BR.valueName) {
                                calculate(it, bean, adapter)
                            }
                        }
                    })
                    calculate(it, bean, adapter)
                }
            }
        }
    }


    var getUrl = ""
    var getHList = ""
    var saveUrl = ""
    var businessType = ""
    override fun refreshData(type: Int?) {
        when (type) {
            -1 -> {
                super.refreshData(type)
            }
            132 -> {
                getHList(0)
            }
        }

    }

    override fun initData() {
        //                val mainData = SZWUtils.getJson(context, "修改担保企业担保分析.json")
        //                val list = Gson().fromJson<MutableList<BaseTypeBean>>(
        //                    mainData,
        //                    object : TypeToken<ArrayList<BaseTypeBean>>() {}.type
        //                )
        //                adapter.setNewInstance(list)
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_QUESTIONNAIRE
            -> {
                getUrl = Urls.get_wjdc_xxy
                getHList = Urls.get_wjdc_h_list
                adapter.subscribe=subscribe_wjdc
            }
            ApplyModel.BUSINESS_TYPE_CREDIT_REVIEW
            -> {
                getUrl = Urls.get_sxpy_xxy
                getHList = Urls.get_sxpy_h_list
                adapter.subscribe=subscribe_sxpy
            }
        }
        businessType = SZWUtils.getBusinessType(viewModel.businessType)
        getHList(0)

    }

    var pre: String = ""
    var now: String = ""
    var next: String = ""
    private fun getHList(destination: Int) {
       val  current = when (destination) {
            -1 -> pre
            0 -> now
            1 -> next
            else -> now
        }

        DataCtrlClass.YXNet.getWJDCInfo(requireActivity(), getHList, keyId = viewModel.keyId, houseNumber = current,oldHouseNumber = now) {
            if (it != null) {
//                val index = adapter.data.indexOfLast { bean -> bean.model == "list"
//
//                }
//                if (index != -1) {
//                    adapter.data.removeAt(index)
//                    adapter.notifyItemRemoved(index)
//                }
                SZWUtils.setSeeOnlyMode(viewModel, it)
                adapter.setNewInstance(it)
                pre = SZWUtils.getJsonObjectString(JsonParser.parseString(it.lastOrNull { baseTypeBean -> baseTypeBean.model == "list" }?.valueName).asJsonObject, "pre")
                now = SZWUtils.getJsonObjectString(JsonParser.parseString(it.lastOrNull { baseTypeBean -> baseTypeBean.model == "list" }?.valueName).asJsonObject, "now")
                next = SZWUtils.getJsonObjectString(JsonParser.parseString(it.lastOrNull { baseTypeBean -> baseTypeBean.model == "list" }?.valueName).asJsonObject, "next")
                viewBind.btUp.visibility = if (pre.isEmpty()) View.GONE else View.VISIBLE
                viewBind.btNext.visibility = if (next.isEmpty()) View.GONE else View.VISIBLE
                viewBind.tvNum.text="第${it?.get(1).houseNumberPage?.NUM}户/共${it?.get(1).houseNumberPage?.TOTAL}户"
                if (isAdded) (activity as BaseActivity).refreshData()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            viewBind.btUp -> {
                getHList(-1)
            }
            viewBind.btNext -> {
                getHList(1)
            }
        }
    }

}