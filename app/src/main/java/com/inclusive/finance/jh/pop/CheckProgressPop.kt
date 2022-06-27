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
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ScreenUtils
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.CheckProgressAdapter
import com.inclusive.finance.jh.databinding.PopJdlsBinding
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil

/**
 * 查看进度
 */
class CheckProgressPop(var mContext: Context?, var keyId: String?="",  var businessType: Int?=0, var type: String) :
    DialogFragment(), View.OnClickListener {

    var mAdapter: CheckProgressAdapter = CheckProgressAdapter()

    //    override fun onCreateContentView(): View {
    //        return createPopupById(R.layout.pop_hyfl)
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

    lateinit var dataBind: PopJdlsBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明

        dataBind = PopJdlsBinding.inflate(inflater, container, false)
        return dataBind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataBind.mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        dataBind.mRecyclerView.adapter = mAdapter
        dataBind.ivClose.setOnClickListener(this)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        DataCtrlClass.SXSPNet.getList_flowHis(mContext,  keyId=keyId,type=type,businessType=businessType) {
            if (it != null) {
                mAdapter.setNewInstance(it)
                if (it.size > 0) {
                    if (!manager.isDestroyed) {
                        super.show(manager, tag)
                    }
                } else SZWUtils.showSnakeBarMsg("暂无进度")
            }
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_close -> dismiss()
        }
    }


}