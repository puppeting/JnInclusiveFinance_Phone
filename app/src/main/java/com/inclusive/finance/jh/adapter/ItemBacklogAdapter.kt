package com.inclusive.finance.jh.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.JsonObject
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.databinding.ItemBacklogBinding
import com.inclusive.finance.jh.utils.SZWUtils

class ItemBacklogAdapter<T : JsonObject> :
    BaseQuickAdapter<T, BaseViewHolder>(R.layout.item_backlog,ArrayList()) {
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(
            ItemBacklogBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            ).root
        )
    }

    override fun convert(holder: BaseViewHolder, item: T) {
        DataBindingUtil.getBinding<ItemBacklogBinding>(holder.itemView)?.apply {
            title=SZWUtils.getJsonObjectString(item,"NAME")
            num=SZWUtils.getJsonObjectString(item,"NUM")+"项待处理"
        }

    }

}