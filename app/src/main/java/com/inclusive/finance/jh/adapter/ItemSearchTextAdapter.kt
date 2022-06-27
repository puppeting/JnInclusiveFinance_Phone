package com.inclusive.finance.jh.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.databinding.ItemSearchTextBinding

class ItemSearchTextAdapter<T : Any> :
    BaseQuickAdapter<T, BaseViewHolder>(R.layout.item_search_text,ArrayList<T>()) {
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(
            ItemSearchTextBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            ).root
        )
    }

    override fun convert(holder: BaseViewHolder, item: T) {
        DataBindingUtil.getBinding<ItemSearchTextBinding>(holder.itemView)?.apply {
            data=item.toString()
        }

    }

}