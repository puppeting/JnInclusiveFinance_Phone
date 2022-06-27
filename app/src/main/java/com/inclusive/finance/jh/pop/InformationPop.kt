package com.inclusive.finance.jh.pop


import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.databinding.PopInformationBinding
import com.inclusive.finance.jh.utils.SZWUtils
import razerdp.basepopup.BasePopupWindow


class InformationPop : BasePopupWindow {


    val title: String
    var listener: (confirm: Boolean) -> Unit?

    constructor(mContext: Context?, title: String, listener: (confirm: Boolean) -> Unit? = {}) : super(mContext) {
        this.title = title
        this.listener = listener
        init()
    }


    constructor(mContext: Dialog?, title: String, listener: (confirm: Boolean) -> Unit? = {}) : super(mContext) {
        this.title = title
        this.listener = listener
        init()
    }

    /**
     * 复制内容到剪贴板
     *
     * @param content
     * @param context
     */
    fun copyContentToClipboard(content: String?, context: Context) { //获取剪贴板管理器：
        val cm: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager // 创建普通字符型ClipData
        val mClipData = ClipData.newPlainText("Label", content) // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData)
        SZWUtils.showSnakeBarMsg("已复制至剪贴板")
    }
    private fun init() {
        setBackground(0)
        dataBind.tvTitle.text = title
        dataBind.tvTitle.setOnClickListener {
            copyContentToClipboard(title,context)
        }
    }
    //    override fun onCreateContentView(): View {
//        return createPopupById(R.layout.pop_confirm)
//    }
    lateinit var dataBind: PopInformationBinding
    override fun onCreateContentView(): View { //        dataBind = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.pop_down_recycler,null,false)
        dataBind = PopInformationBinding.bind(createPopupById(R.layout.pop_information))
        return dataBind.root
    }
}