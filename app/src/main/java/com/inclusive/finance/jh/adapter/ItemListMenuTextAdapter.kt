package com.inclusive.finance.jh.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.databinding.ItemListMenuTextBinding

class ItemListMenuTextAdapter<T : String> :
    BaseQuickAdapter<T, BaseViewHolder>(R.layout.item_list_menu_text,ArrayList()) {
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(
            ItemListMenuTextBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            ).root
        )
    }

    override fun convert(holder: BaseViewHolder, item: T) {
        DataBindingUtil.getBinding<ItemListMenuTextBinding>(holder.itemView)?.apply {
            data=item
        }

    }

}