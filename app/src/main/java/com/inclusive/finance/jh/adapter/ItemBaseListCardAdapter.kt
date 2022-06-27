package com.inclusive.finance.jh.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.JsonObject
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.IRouter
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.R.id
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseListBean
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.ListBean
import com.inclusive.finance.jh.bean.ListTitle
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.databinding.ItemBaseListCardBinding
import com.inclusive.finance.jh.databinding.ItemBaseListCardRowMoreBinding
import com.inclusive.finance.jh.databinding.ItemBaseListCardTextBinding
import com.inclusive.finance.jh.pop.BaseTypePop
import com.inclusive.finance.jh.pop.ConfirmPop
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.widget.MyWebActivity

class ItemBaseListCardAdapter<T : JsonObject>(var fragment: MyBaseFragment,var listener: ((jsonObject: JsonObject) -> Unit)? = null,) :
    BaseQuickAdapter<T, BaseViewHolder>(R.layout.item_base_list_card, ArrayList()), LoadMoreModule {
    var keyId: String? = "" //列表内按钮和弹出窗时使用
    var subscribe: ((adapter: ItemBaseTypeAdapter<BaseTypeBean>, data: ArrayList<BaseTypeBean>, rootView: View) -> Unit?)? = null
    var subscribeChildLayoutDrawListener:((holder: BaseViewHolder, item: T)->Unit?)?=null
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        addChildClickViewIds(R.id.bt_more, R.id.bt_seeOnly, R.id.bt_change)
        return BaseViewHolder(
            ItemBaseListCardBinding.inflate(
                LayoutInflater.from(context), parent, false
            ).root
        )
    }

    var textListItemConfigFun: (item: T?, adapterPosition: Int) -> TextListItemConfig = { item, index ->
        TextListItemConfig()
    }
    var textListItemConfig: TextListItemConfig = TextListItemConfig()

    class TextListItemConfig {
        var textDefaultColor = R.color.color_text_title
        var textLinkColor = R.color.color_main_blue
        var textCheckColor = textDefaultColor

    }

    var baseTypeBean: BaseTypeBean? = null
    var listBean: BaseListBean = BaseListBean(true, "", arrayListOf(), arrayListOf())
    fun setListData(data: BaseTypeBean? = null, bean: BaseListBean? = null, list: MutableList<T>? = null) {
        baseTypeBean = data
        listBean = bean ?: data?.listBean ?: BaseListBean(true, "", arrayListOf(), arrayListOf())
        setNewInstance(list)

    }
    //设置数据 元数据副本 不会改变原数据 增加check状态
    var checkListBean:ArrayList<ListBean> = arrayListOf()
    override fun setNewInstance(list: MutableList<T>?) {
        checkListBean.clear()
        list?.mapTo(checkListBean) { ListBean(it) }
        super.setNewInstance(list)
    }

    override fun addData(newData: Collection<T>) {
        newData.mapTo(checkListBean) { ListBean(it) }
        super.addData(newData)
    }
    override fun convert(holder: BaseViewHolder, item: T) {
        val binding = DataBindingUtil.getBinding<ItemBaseListCardBinding>(holder.itemView)
        binding?.data = item
        binding?.checkBean = checkListBean[holder.bindingAdapterPosition]
        binding?.contentLay?.removeAllViews()
        listBean.titleList.forEachIndexed { index, listTitle ->
//            showRowCount : -1 无此字段或 不设数值  ，-2 全部显示
            var showRowCount = SZWUtils.getJsonObjectInt(item, "showRowCount")
            if (showRowCount == -1) {
                showRowCount = 3//默认显示三个
            }
             val titleViewBind = DataBindingUtil.inflate<ItemBaseListCardTextBinding>(LayoutInflater.from(context), R.layout.item_base_list_card_text, null, false)
//            titleViewBind.titleData = listTitle.value
            setControl(fragment, true, listTitle, item, titleViewBind, textListItemConfigFun.invoke(item, holder.bindingAdapterPosition))
            setControl(fragment, false, listTitle, item, titleViewBind, textListItemConfigFun.invoke(item, holder.bindingAdapterPosition))

            when {
                showRowCount >0 && index == showRowCount -> {
                    val rowMoreBinding = DataBindingUtil.inflate<ItemBaseListCardRowMoreBinding>(LayoutInflater.from(context), R.layout.item_base_list_card_row_more, null, false)
                    rowMoreBinding.btMore.setOnClickListener {
                        item.addProperty("showRowCount", -2)
                        notifyItemChanged(holder.bindingAdapterPosition)
                    }
                    rowMoreBinding.contentLay.addView(titleViewBind.root, 0)
                    binding?.contentLay?.addView(rowMoreBinding.root)
                }
                showRowCount in 1 until index -> {
                    return@forEachIndexed
                }
                else -> {
                    binding?.contentLay?.addView(titleViewBind.root)
                }
            }
        }

        //列表没有点击事件 才会执行actionType改变按钮，否则就显示默认按钮。
        if (getOnItemChildClickListener() == null) {
            iniActionButton(binding, item)

        }else{
            subscribeChildLayoutDrawListener?.invoke(holder,item)
        }
    }

    private fun <T : ListTitle> setControl(fragment: MyBaseFragment, isTitle: Boolean, item: T, bean: JsonObject?, viewBind: ItemBaseListCardTextBinding, textListItemConfig: TextListItemConfig) {
        val jsonObjectString = if (isTitle) item.value else SZWUtils.getJsonObjectString(bean, item.key)
        val viewModel = ViewModelProvider(fragment.requireActivity() as BaseActivity)[ApplyModel::class.java]
        val tvValue = if (isTitle) viewBind.title else viewBind.tvValue
        val split = when {
            jsonObjectString.contains("@Local:") -> {
                val split = jsonObjectString.split("@Local:")
                val function: (View) -> Unit = {
                    if (split.size > 1) IRouter.goF(it, id.action_to_navActivity, split[1], viewModel.creditId, bean, viewModel.businessType, viewModel.seeOnly)
                }
                tvValue.setOnClickListener(function)
                split
            }
            jsonObjectString.contains("#Internet:") -> {
                val split = jsonObjectString.split("#Internet:")
                tvValue.setOnClickListener {
                    if (split.size > 1) {
                        ARouter.getInstance()
                            .build("/com/MyWebActivity") //                        .withString(Intent_WebUrl, "http://192.168.3.32:8081/onlinePreview?url=http%3A%2F%2F212.129.130.163%3A3000%2Ftscepdf.pdf&officePreviewType=pdf")
                            //                        .withString(Intent_WebUrl, "http://debugtbs.qq.com")
                            .withString(MyWebActivity.Intent_WebUrl, SZWUtils.getIntactUrl(split[1]))
                            .withBoolean("isPDF", split[1].contains(".pdf"))
                            .withString(MyWebActivity.Intent_WebTitle, split[0]).navigation()
                    }
                }
                split
            }
            jsonObjectString.contains("#LocalPop:") -> {
                val split = jsonObjectString.split("#LocalPop:")
                tvValue.setOnClickListener {
                    if (split.size > 1) {
                        BaseTypePop(mContext = fragment.requireContext(), fragment = fragment, "查看", split[1], "", bean, viewModel.creditId) { adapter, resultStr ->

                        }.show(fragment.childFragmentManager, "adapter")
                    }
                }
                split
            }
            else -> null
        }
        val dataStr = if (split != null) { //下划线
            //                tv.paint.flags = Paint.UNDERLINE_TEXT_FLAG
            //                tv.paint.isAntiAlias = true
            split[0]
        } else {
            jsonObjectString
        }
        //设置文字颜色
        tvValue.setTextColor(ContextCompat.getColor(
            fragment.requireContext(), when {
                bean != null && SZWUtils.getJsonObjectBoolean(bean, "isCheck") -> textListItemConfig.textCheckColor
                split != null -> textListItemConfig.textLinkColor
                else -> {
                    textListItemConfig.textDefaultColor
                }
            }
        ))
        if (isTitle) {
            viewBind.titleData = dataStr
        }else {
            viewBind.data = dataStr
        }

    }


    private fun iniActionButton(viewBind: ItemBaseListCardBinding?, jsonObject: T) {
        val actionTypes = listBean.actionTypes?.split(",")
        viewBind?.btMore?.visibility = View.GONE
        viewBind?.btChange?.visibility = View.GONE
        viewBind?.btSeeOnly?.visibility = View.GONE
        viewBind?.btPdf?.visibility = View.GONE
        actionTypes?.forEach {
            when (it) {
                "1" -> {
                    if (listBean.deleteUrl.isEmpty()) {
                        return
                    }
                    viewBind?.btMore?.visibility = View.VISIBLE
                    viewBind?.btMore?.text = "删除"

                    viewBind?.btMore?.setOnClickListener {
                        ConfirmPop(context, "确认删除") { confirm ->
                            if (confirm) {
                                DataCtrlClass.KHGLNet.deleteBaseTypePoPList(context, SZWUtils.getIntactUrl(listBean?.deleteUrl), keyId = keyId, jsonObject) {
                                    fragment.refreshData(131)
                                }
                            }
                        }.show(fragment.childFragmentManager, "adapter")


                    }
                }
                "2" -> {
                    if (listBean.getUrl.isEmpty() || listBean.saveUrl.isEmpty()) {
                        return
                    }
                    viewBind?.btChange?.visibility = View.VISIBLE
                    viewBind?.btChange?.setOnClickListener {
                        BaseTypePop(context, fragment, "修改", SZWUtils.getIntactUrl(listBean.getUrl), SZWUtils.getIntactUrl(listBean.saveUrl), jsonObject, keyId = keyId, subscribe = subscribe) { adapter, resultStr ->
                            fragment.refreshData(132)
                        }.show(fragment.childFragmentManager, "adapter")
                    }
                }
                "3" -> {
                    if (listBean.getUrl.isEmpty()) {
                        return
                    }
                    viewBind?.btSeeOnly?.visibility = View.VISIBLE

                    viewBind?.btSeeOnly?.setOnClickListener {
                        BaseTypePop(context, fragment, "查看", SZWUtils.getIntactUrl(listBean.getUrl), SZWUtils.getIntactUrl(listBean.saveUrl), jsonObject, keyId = keyId, subscribe = subscribe).show(fragment.childFragmentManager, "adapter")
                    }
                }
                "4" -> {
                    viewBind?.btPdf?.visibility = View.VISIBLE
                    viewBind?.btPdf?.setOnClickListener {
                        if (listBean.pdfUrl.isNotEmpty()) ARouter.getInstance()
                            .build("/com/MyWebActivity") //                        .withString(Intent_WebUrl, "http://192.168.3.32:8081/onlinePreview?url=http%3A%2F%2F212.129.130.163%3A3000%2Ftscepdf.pdf&officePreviewType=pdf")
                            //                        .withString(Intent_WebUrl, "http://debugtbs.qq.com")
                            .withString(MyWebActivity.Intent_WebUrl, SZWUtils.getIntactUrl(listBean.pdfUrl))
                            .withBoolean("isPDF", true)
                            .withString(MyWebActivity.Intent_WebTitle, "征信PDF").navigation()
                        else SZWUtils.showSnakeBarMsg("暂无征信PDF")
                    }
                }
            } //                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT) //                params.leftMargin = SizeUtils.dp2px(15f) //                chip.layoutParams = params
        }
    }
}