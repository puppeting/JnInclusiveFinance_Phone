package com.inclusive.finance.jh.adapter

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ObjectUtils
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.bean.ProcessHistort
import com.inclusive.finance.jh.utils.SZWUtils
import com.umeng.umcrash.UMCrash

class CheckProgressAdapter : BaseQuickAdapter<ProcessHistort, BaseViewHolder>(R.layout.item_progress_recycle) {
    override fun convert(holder: BaseViewHolder, item: ProcessHistort) {
        try {
            if (holder.adapterPosition == 0) {
                holder.setVisible(R.id.iv_top_line, false)
            } else {
                holder.setVisible(R.id.iv_top_line, true)
            }
            holder.setVisible(R.id.iv_bottom_line, item.curNodeName?.contains("结束")!=true)
            holder.getView<ImageView>(R.id.iv_status).imageTintList= ColorStateList.valueOf(ContextCompat.getColor(context,R.color.colorPrimary))
            holder.setText(R.id.tv_name, item.curNodeName)
            holder.setText(R.id.tv_time,"审批时间:"+ StringUtils.null2Length0(item.endTime))
            if (ObjectUtils.isNotEmpty(item.id)) {
                holder.setText(R.id.tv_spr, "审批人:       " + item.handler)
                holder.setText(R.id.tv_spjg, "审批结果:  " + getCzlx(item.operateType))
                holder.setText(R.id.tv_spyj, "审批意见:  " + item.handlerOpinion)
                holder.setText(R.id.tv_hbyj, "会办意见:  " + item.item2)
                holder.setGone(R.id.tv_hbyj, item.item2.isNullOrEmpty())
                holder.setText(R.id.tv_start_time, "开始时间:  " + item.beginTime)
            } else {
                holder.setText(R.id.tv_start_time, "审批人:       " + item.handler)
                holder.setVisible(R.id.tv_spyj, false)
                holder.setGone(R.id.tv_hbyj, false)
                holder.setVisible(R.id.tv_spjg, false)
                //                holder.setGone(R.id.tv_start_time, false);
                holder.setVisible(R.id.iv_bottom_line, false)
                holder.getView<ImageView>(R.id.iv_status).imageTintList= ColorStateList.valueOf(ContextCompat.getColor(context,R.color.Gray))
            }
            val iv = holder.getView<ImageView>(R.id.iv_url)
            iv.visibility=if (item.annex.isNullOrEmpty()) View.GONE else View.VISIBLE
            SZWUtils.loadPhotoImg(context,item.annex,iv)
        } catch (e: Exception) {
            UMCrash.generateCustomLog(e, "UmengException")
            e.printStackTrace()
        }
    }

    private fun getCzlx(s: String?): String {
        return when (s) {
            "agree" ->
                "同意"
            "returnTo" ->
                "回退"
            "reject" ->
                "否决"
            else -> {
                ""
            }
        }
    }
}