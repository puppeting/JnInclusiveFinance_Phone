package com.inclusive.finance.jh.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.databinding.ItemMapTextBinding
import java.util.HashMap

class ItemMapTextAdapter<T : HashMap<String, String>> :
    BaseQuickAdapter<T, BaseViewHolder>(R.layout.item_map_text,ArrayList<T>()) {
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(
            ItemMapTextBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            ).root
        )
    }

    override fun convert(holder: BaseViewHolder, item: T) {
        DataBindingUtil.getBinding<ItemMapTextBinding>(holder.itemView)?.apply {
            title=item["name"]
            address=item["address"]
        }

    }
}