package com.inclusive.finance.jh.adapter

import android.text.Selection
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.inclusive.finance.jh.databinding.ItemSearchBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.bean.KVBean

class ItemSearchFilterAdapter<T : KVBean> :
    BaseQuickAdapter<T, BaseViewHolder>(R.layout.item_search,ArrayList<T>()) {
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(ItemSearchBinding.inflate(LayoutInflater.from(context),parent,false).root)
    }
    override fun convert(helper: BaseViewHolder, item: T) {
        val itemBindingUtil =DataBindingUtil.getBinding<ItemSearchBinding>(helper.itemView)
        Selection.setSelection(itemBindingUtil?.tv?.text, itemBindingUtil?.tv?.text?.length?:0)

    }

}