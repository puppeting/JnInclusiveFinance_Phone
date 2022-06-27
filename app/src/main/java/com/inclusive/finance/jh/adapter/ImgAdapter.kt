package com.inclusive.finance.jh.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.bean.PicBean
import com.inclusive.finance.jh.databinding.ItemImgBinding
import com.inclusive.finance.jh.utils.SZWUtils

class ImgAdapter<T : PicBean>(var seeOnly: Boolean) :
    BaseQuickAdapter<T, BaseViewHolder>(R.layout.item_img,ArrayList<T>()) {
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(ItemImgBinding.inflate(LayoutInflater.from(context), parent, false).root)
    }

    override fun convert(helper: BaseViewHolder, item: T) {
        DataBindingUtil.getBinding<ItemImgBinding>(helper.itemView)?.apply {
            data = item
            btCheck.visibility = if (item.filePath.isNullOrEmpty()) View.GONE else if(seeOnly) View.GONE else View.VISIBLE
            Glide.with(context).load(SZWUtils.getIntactUrl(item.filePath)).into(img)
        }
    }
}