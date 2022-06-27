package com.inclusive.finance.jh.pop

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ImgAdapter
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.PicBean
import com.inclusive.finance.jh.databinding.PopImgEditBinding
import com.inclusive.finance.jh.utils.GridAutofitLayoutManager
import com.inclusive.finance.jh.utils.StatusBarUtil
import org.apache.commons.lang3.StringUtils

/**
 * 多张图片编辑
 */
class MultiplePicEditPop<T : BaseTypeBean>(var mContext: Context, var parentAdapter: ItemBaseTypeAdapter<T>, var item: T, var seeOnly: Boolean, var listener: () -> Unit) :
    DialogFragment(), View.OnClickListener {

    private var mAdapter: ImgAdapter<PicBean> = ImgAdapter(seeOnly)

    //    override fun onCreateContentView(): View {
    //        return createPopupById(R.layout.pop_img_edit)
    //    }
    override fun onStart() {
        super.onStart()
        StatusBarUtil.immersive(dialog?.window)
        //        setStyle(STYLE_NO_TITLE,R.style.MyDialog)
        val params = dialog?.window?.attributes
        dialog?.setCanceledOnTouchOutside(false)
        params?.width = ScreenUtils.getScreenWidth()
        params?.height = RelativeLayout.LayoutParams.MATCH_PARENT
        params?.gravity = Gravity.CENTER
        //高度自己定义
        dialog?.window?.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)

    }

    lateinit var dataBind: PopImgEditBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明

        dataBind = PopImgEditBinding.inflate(inflater, container, false)
        return dataBind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataBind.mRecyclerView.layoutManager = GridAutofitLayoutManager(requireContext(), SizeUtils.dp2px(90f))
        dataBind.mRecyclerView.adapter = mAdapter
        if (!seeOnly) {
            item.picList?.add(0, PicBean())
        }
        mAdapter.setNewInstance(item.picList)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            if (mAdapter.data[position].filePath.isNullOrEmpty()) parentAdapter.selectPic(item) {
                mAdapter.notifyDataSetChanged()
            } else {
                parentAdapter.selectPic(item, true, position - 1)
            }
        }
        dataBind.btLay.visibility = if (seeOnly) View.GONE else View.VISIBLE
        dataBind.ivClose.setOnClickListener(this)
        dataBind.btDelete.setOnClickListener(this)
        dataBind.btOk.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.bt_delete -> {
                ConfirmPop(mContext, "确定删除吗？") { confirm ->
                    if (confirm) {
                        val list = arrayListOf<PicBean>()
                        item.picList?.forEach {
                            if (!it.checked) list.add(it)
                        }
                        val last = item.picList?.lastOrNull { it.filePath.isNullOrEmpty() }
                        if (last == null) {
                            item.picList?.add(PicBean())
                        }
                        item.picList = list
                        item.picUrl = StringUtils.join(list, ",")
                        mAdapter.setNewInstance(item.picList)
                        mAdapter.notifyDataSetChanged()
                    }

                }.show(childFragmentManager, this.javaClass.name)
            }
            R.id.iv_close,
            R.id.bt_ok,
            -> {
                listener.invoke()
                dismiss()
            }
        }
    }

    override fun dismiss() {
        listener.invoke()
        super.dismiss()
    }
}