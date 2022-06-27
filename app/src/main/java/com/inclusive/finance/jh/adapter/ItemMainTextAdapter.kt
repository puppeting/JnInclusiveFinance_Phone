package com.inclusive.finance.jh.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.JsonObject
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.databinding.ItemMainTextBinding
import com.inclusive.finance.jh.utils.SZWUtils

class ItemMainTextAdapter<T : JsonObject> :
    BaseQuickAdapter<T, BaseViewHolder>(R.layout.item_main_text,ArrayList<T>()) {
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(
            ItemMainTextBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            ).root
        )
    }

    override fun convert(holder: BaseViewHolder, item: T) {
        DataBindingUtil.getBinding<ItemMainTextBinding>(holder.itemView)?.apply {
            data=SZWUtils.getJsonObjectString(item,"NAME")+":"+SZWUtils.getJsonObjectString(item,"NUM")
        }

    }

}