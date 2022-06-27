package com.inclusive.finance.jh.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.JsonElement
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.databinding.ItemBaseListCardTextBinding

class ItemBaseListCardTextAdapter<T : JsonElement> :
    BaseQuickAdapter<T, BaseViewHolder>(R.layout.item_base_list_card_text,ArrayList()) {
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(
            ItemBaseListCardTextBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            ).root
        )
    }

    override fun convert(holder: BaseViewHolder, item: T) {
        DataBindingUtil.getBinding<ItemBaseListCardTextBinding>(holder.itemView)?.apply {
            data="34444444"
            titleData="12131"
        }

    }

}