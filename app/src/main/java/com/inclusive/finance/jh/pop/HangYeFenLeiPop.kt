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
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ScreenUtils
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.HangYeFenLeiAdapter
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.databinding.PopHyflBinding
import com.inclusive.finance.jh.utils.StatusBarUtil

/**
 * 行业分类
 */
class HangYeFenLeiPop(context: Context, var bean: BaseTypeBean, var checkedTextView: AppCompatCheckedTextView?, var type: Int) : DialogFragment(), View.OnClickListener {

    var mAdapter: HangYeFenLeiAdapter = HangYeFenLeiAdapter(type){
        when (type) {
            BaseTypeBean.TYPE_8 -> {
                checkedTextView?.text = it.title
                bean.valueName=it.value+"##"+ it.title
                dismiss()
            }
            BaseTypeBean.TYPE_21 -> {
                checkedTextView?.text = it.name
                bean.valueName=it.id+"##"+ it.title
                dismiss()
            }
            BaseTypeBean.TYPE_23 -> {
                checkedTextView?.text = it.title
                bean.valueName=it.value+"##"+ it.title
                dismiss()
            }
            BaseTypeBean.TYPE_24 -> {
                if (bean.isSingleChecked) {
                    checkedTextView?.text = it.title
                    bean.valueName = it.value + "##" + it.title
                    dismiss()
                } else {
                    // TODO: 2021/4/23 多选
                }
            }
        }


    }

    //    override fun onCreateContentView(): View {
//        return createPopupById(R.layout.pop_hyfl)
//    }
    lateinit var dataBind: PopHyflBinding
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明

        dataBind = PopHyflBinding.inflate(inflater, container, false)
        return dataBind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataBind.mRecyclerView.layoutManager = LinearLayoutManager(context)
        dataBind.mRecyclerView.adapter = mAdapter
        dataBind.ivClose.setOnClickListener(this)
        dataBind.tvMyInfo.text= bean.keyName
        DataCtrlClass.ApplyNet.getKHInfoHyfl(context,type,bean) {
            if (it != null) {
                mAdapter.setNewInstance(it)
            } else dismiss()
        }
    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_close -> dismiss()
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        checkedTextView?.isChecked = true
        if (!manager.isDestroyed) {
            super.show(manager, tag)
        }
    }

    override fun dismiss() {
        checkedTextView?.isChecked = false
        super.dismiss()
    }

}