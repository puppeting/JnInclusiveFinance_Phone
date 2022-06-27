package com.inclusive.finance.jh.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.HangYeFenLeiBean

/**
 * 作者：PangLei on 2019/3/21 0021 11:03
 *
 *
 * 邮箱：xjs250@163.com
 */
class HangYeFenLeiAdapter(var type: Int, var listener: (bean: HangYeFenLeiBean) -> Unit) :
    BaseQuickAdapter<HangYeFenLeiBean, BaseViewHolder>(R.layout.item_hyfl) {
    override fun convert(helper: BaseViewHolder, item: HangYeFenLeiBean) {
        helper.setIsRecyclable(false)
        helper.setText(R.id.tv_title, when (type) {
            BaseTypeBean.TYPE_8 -> item.title
            BaseTypeBean.TYPE_21 -> item.name
            BaseTypeBean.TYPE_23 -> item.title
            BaseTypeBean.TYPE_24 -> item.title
            else -> item.title
        })
        val recyclerView = helper.getView<RecyclerView>(R.id.rv_hy)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val iv = helper.getView<ImageView>(R.id.iv_vs)
        val tv = helper.getView<TextView>(R.id.tv_title)

        tv.setOnClickListener {
            when {
                type == BaseTypeBean.TYPE_8&&item.children.isNullOrEmpty() -> listener.invoke(item)
                type == BaseTypeBean.TYPE_21&& !item.parentId.isNullOrEmpty() -> listener.invoke(item)
                type == BaseTypeBean.TYPE_23-> listener.invoke(item)
                type == BaseTypeBean.TYPE_24&&item.canCheck-> listener.invoke(item)
            }
        }
        if (item.children.isNullOrEmpty()) {
            iv.visibility = View.INVISIBLE
        }
        iv.setOnClickListener { view ->
            if ("invisable" == view.tag) {
                iv.tag = "visable"
                iv.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
                recyclerView.visibility = View.VISIBLE
            } else {
                iv.tag = "invisable"
                iv.setImageResource(R.drawable.ic_baseline_arrow_right_24)
                recyclerView.visibility = View.GONE
            }
        }
        val mAdapter = HangYeFenLeiAdapter(type) {
            listener.invoke(it)
        }
        recyclerView.adapter = mAdapter
        mAdapter.setNewData(item.children)
    }
}