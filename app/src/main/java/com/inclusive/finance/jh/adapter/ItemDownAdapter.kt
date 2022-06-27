package com.inclusive.finance.jh.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.databinding.ItemDownChipBinding

class ItemDownAdapter<T : BaseTypeBean.Enum12> :
    BaseQuickAdapter<T, BaseViewHolder>(R.layout.item_down_chip, ArrayList()) {
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(ItemDownChipBinding.inflate(LayoutInflater.from(context), parent, false).root)
    }

    override fun convert(holder: BaseViewHolder, item: T) {
        DataBindingUtil.getBinding<ItemDownChipBinding>(holder.itemView)?.apply {
            this.data = item
            (root as CheckedTextView).setTextColor(Color.parseColor(item.textColor))
        }

    }

}