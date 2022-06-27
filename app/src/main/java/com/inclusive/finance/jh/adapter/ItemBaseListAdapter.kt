package com.inclusive.finance.jh.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.flyco.roundview.RoundTextView
import com.google.gson.JsonObject
import com.inclusive.finance.jh.IRouter
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseListBean
import com.inclusive.finance.jh.bean.ListTitle
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.databinding.ItemRecyclerBinding
import com.inclusive.finance.jh.databinding.ItemTextBinding
import com.inclusive.finance.jh.pop.BaseTypePop
import com.inclusive.finance.jh.pop.DownPop
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.widget.MyWebActivity
import org.jetbrains.anko.ems


class ItemBaseListAdapter<T : JsonObject>(
    var fragment: MyBaseFragment,
    var listener: ((jsonObject: JsonObject) -> Unit)? = null,
) : BaseQuickAdapter<T, BaseViewHolder>(R.layout.item_recycler, ArrayList<T>()), LoadMoreModule {
    var listBean: BaseListBean? = null
    var emsCount = 0
    var equally = false
    var singleCheck = true //列表是否单选 默认单选
    var singleEmsWith = 0.00

    init {
        setOnItemClickListener { _, _, position ->
            if (SZWUtils.getJsonObjectBoolean(data[position], "isCheck")) {
                data[position].addProperty("isCheck", false)
                notifyItemChanged(position - headerLayoutCount)
            } else {
                if (singleCheck) data.forEachIndexed { index, kvBean ->
                    if (SZWUtils.getJsonObjectBoolean(kvBean, "isCheck")) {
                        kvBean.addProperty("isCheck", false)
                        notifyItemChanged(index - headerLayoutCount)
                    }
                }
                data[position].addProperty("isCheck", true)
                listener?.invoke(data[position])
                notifyItemChanged(position - headerLayoutCount)
            }
        }
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(ItemRecyclerBinding.inflate(LayoutInflater.from(context), parent, false).root)
    }

    //必须首先调用
    fun initTitleLay(context: Context?, rootView: View, listBean: BaseListBean?, sortListener: ((sortStr: String) -> Unit)? = {}, listener: (() -> Unit?)?) {
        this.listBean = listBean
        val scrollView: HorizontalScrollView = rootView.findViewWithTag("scrollView")
        val titleLay: LinearLayoutCompat = rootView.findViewWithTag("titleLay")
        val titleView: RoundTextView? = rootView.findViewWithTag("titleView")
        titleLay.removeAllViews()
        emsCount = 0
        singleEmsWith = 0.00
        equally = listBean?.equally ?: false

        titleView?.text = listBean?.title
        titleView?.visibility = if (listBean?.title.isNullOrEmpty()) View.GONE else View.VISIBLE
        listBean?.titleList?.forEach { emsCount += it.ems }

        scrollView.post {
            singleEmsWith = (scrollView.width).toDouble() / emsCount
            listBean?.titleList?.forEach { listTitleBean ->
                if (fragment.context != null) titleLay.addView(DataBindingUtil.inflate<ItemTextBinding>(LayoutInflater.from(fragment.context), R.layout.item_text, null, false)
                    .apply {


                        if (equally) {

                            tv.layoutParams = ConstraintLayout.LayoutParams((listTitleBean.ems * singleEmsWith).toInt(), SizeUtils.dp2px(32f))
                        } else tv.ems = listTitleBean.ems
                        context?.let { it1 -> tv.setTextColor(ContextCompat.getColor(it1, R.color.color_text_title)) }
                        if (!listTitleBean.enums12.isNullOrEmpty()) {
                            tv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_sort_24, 0) // TODO: 2020/9/27 弹窗，筛选，赋值，刷新
                            tv.setOnClickListener { v ->
                                DownPop(context, enums12 = listTitleBean.enums12, checkedTextView = null, isSingleChecked = true) { key, value, position ->
                                    sortListener?.invoke(key)
                                }.showPopupWindow(tv)
                            }


                        }
                        setControl(fragment, true, listTitleBean, JsonObject(), tv, textListItemConfig.invoke(null, 0))

                    }.root)
            }
            listener?.invoke()
        }

    }

    var itemRecyclerViewBackGroundColor: (item: T, holder: BaseViewHolder) -> Int = { item, holder ->
        when {
            SZWUtils.getJsonObjectBoolean(item, "isCheck") -> R.color.color_main_orangeAlpha
            holder.adapterPosition % 2 == 0 -> R.color.white
            else -> R.color.line2
        }
    }

    override fun convert(holder: BaseViewHolder, item: T) {
        DataBindingUtil.getBinding<ItemRecyclerBinding>(holder.itemView)?.apply {
            mRecyclerView.setBackgroundColor(ContextCompat.getColor(fragment.requireContext(), itemRecyclerViewBackGroundColor(item, holder)))
            mRecyclerView.layoutManager = LinearLayoutManager(fragment.requireContext(), RecyclerView.HORIZONTAL, false)
            ItemTextAdapter<ListTitle>(fragment, item, holder.adapterPosition, singleEmsWith, equally).apply {
                mRecyclerView.adapter = this
                textListItemConfig = this@ItemBaseListAdapter.textListItemConfig.invoke(item, holder.adapterPosition)
                setNewInstance(listBean?.titleList)
                setOnItemClickListener { _, _, _ ->
                    this@ItemBaseListAdapter.setOnItemClick(holder.itemView, holder.adapterPosition)
                }
            }

        }

    }


    var textListItemConfig: (item: T?, adapterPosition: Int) -> ItemTextAdapter.TextListItemConfig = { item, index ->
        ItemTextAdapter.TextListItemConfig()
    }
}

class ItemTextAdapter<T : ListTitle>(var fragment: MyBaseFragment, var bean: JsonObject, var parentAdapterPosition: Int, var singleEmsWith: Double, var equally: Boolean) :
    BaseQuickAdapter<T, BaseViewHolder>(R.layout.item_text, ArrayList<T>()) {
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(ItemTextBinding.inflate(LayoutInflater.from(fragment.requireContext()), parent, false).root)
    }

    var textListItemConfig: TextListItemConfig = TextListItemConfig()

    class TextListItemConfig {
        var textDefaultColor = R.color.color_text_title
        var textLinkColor = R.color.color_main_blue
        var textCheckColor = textDefaultColor

    }

    override fun convert(holder: BaseViewHolder, item: T) {
        DataBindingUtil.getBinding<ItemTextBinding>(holder.itemView)?.apply {
            setControl(fragment, false, item, bean, holder.itemView, textListItemConfig)
            if (item.value.contains("序号")) {
                data = (parentAdapterPosition + 1).toString()
            }
            if (equally) {
                tv.layoutParams = ConstraintLayout.LayoutParams((item.ems * singleEmsWith).toInt(), SizeUtils.dp2px(32f))
            } else {
                tv.setEms(item.ems)
            }
        } //        "本地@Local:信息概况"
        //        "网页#Internet:https://192.168.3.31/jeecg-boot/doc.html"
        //        "弹窗仅查看#LocalPop:https://192.168.3.31/jeecg-boot/doc.html?id=2019-12"
    }


}

private fun <T : ListTitle> ItemTextBinding.setControl(fragment: MyBaseFragment, isTitle: Boolean, item: T, bean: JsonObject?, view: View, textListItemConfig: ItemTextAdapter.TextListItemConfig) {
    val jsonObjectString = if (isTitle) item.value else SZWUtils.getJsonObjectString(bean, item.key)
    val viewModel = ViewModelProvider(fragment.requireContext() as BaseActivity).get(ApplyModel::class.java)
    val split = when {
        jsonObjectString.contains("@Local:") -> {
            val split = jsonObjectString.split("@Local:")
            view.setOnClickListener {
                if (split.size > 1) IRouter.goF(it, R.id.action_to_navActivity, split[1], viewModel.creditId, bean, viewModel.businessType, viewModel.seeOnly)
            }
            split
        }
        jsonObjectString.contains("#Internet:") -> {
            val split = jsonObjectString.split("#Internet:")
            view.setOnClickListener {
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
            view.setOnClickListener {
                if (split.size > 1) {
                    BaseTypePop(mContext = fragment.requireContext(), fragment = fragment, "查看", split[1], "", bean, viewModel.creditId) {adapter,resultStr->

                    }.show(fragment.childFragmentManager, "adapter")
                }
            }
            split
        }
        else -> null
    }
    data = if (split != null) { //下划线
        //                tv.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        //                tv.paint.isAntiAlias = true
        split[0]
    } else {
        jsonObjectString
    }

    //设置文字颜色
    tv.setTextColor(ContextCompat.getColor(fragment.requireContext(), when {
        bean != null && SZWUtils.getJsonObjectBoolean(bean, "isCheck") -> textListItemConfig.textCheckColor
        split != null -> textListItemConfig.textLinkColor
        else -> {
            textListItemConfig.textDefaultColor
        }
    }))
}