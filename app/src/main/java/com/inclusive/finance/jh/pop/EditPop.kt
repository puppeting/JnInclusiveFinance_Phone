package com.inclusive.finance.jh.pop


import android.content.Context
import android.text.InputType
import android.view.View
import android.view.animation.Animation
import android.widget.EditText
import android.widget.TextView
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.SizeUtils
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.databinding.PopBianjibanBinding
import com.inclusive.finance.jh.utils.SZWUtils
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AlphaConfig
import razerdp.util.animation.AnimationHelper

class EditPop(context: Context?, val inputType: Int = InputType.TYPE_CLASS_TEXT, val title: String, var view: TextView?=null,var regex:String?="",var regexErrorMsg:String?="",var listener:(str:String)->Unit?={} ) :
    BasePopupWindow(context), View.OnClickListener {

    override fun onClick(v: View?) {
        KeyboardUtils.hideSoftInput(findViewById<EditText>(R.id.tv))
        when (v) {
            dataBind.ivClose -> {
                dismiss()
            }
            dataBind.tvOk -> {
                val text = findViewById<EditText>(R.id.tv).text
                listener.invoke(text.toString())
                if (regex.isNullOrEmpty()){
                    view?.text = text
                    dismiss()
                }else{
                    if (RegexUtils.isMatch(regex,text)) {
                        view?.text = text
                        dismiss()
                    }else{
                        SZWUtils.showSnakeBarMsg(contentView = dataBind.root,regexErrorMsg?:"")
                    }

                }

            }
            else -> {
            }
        }
    }

    //    override fun onCreateContentView(): View {
    //        return createPopupById(R.layout.pop_bianjiban)
    //    }
    lateinit var dataBind: PopBianjibanBinding
    override fun onCreateContentView(): View {
        dataBind= PopBianjibanBinding.bind(createPopupById(R.layout.pop_bianjiban))
//        dataBind = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pop_bianjiban, null, false)
        return dataBind.root
    }

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        setAdjustInputMethod(true)
        setAutoShowInputMethod(findViewById(R.id.tv), true)
        setAdjustInputMode(R.id.tv, BasePopupWindow.FLAG_KEYBOARD_ALIGN_TO_ROOT or BasePopupWindow.FLAG_KEYBOARD_ANIMATE_ALIGN or BasePopupWindow.FLAG_KEYBOARD_FORCE_ADJUST)
    }

    init {
        setOutSideDismiss(false)
        //        StatusBarUtil.setMargin(context, contentView.layout_bottom)
        //        SZWUtils.setMargin(contentView.layout_bottom, 8f)
        findViewById<TextView>(R.id.tv_title).text = if (title.isEmpty()) {
            "编辑"
        }else title
        val tv = findViewById<EditText>(R.id.tv)
        tv.setText(view?.text)
        if (inputType != 8194) {
            tv.inputType = (inputType or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
            tv.maxHeight = SizeUtils.dp2px(120f)
            tv.minHeight = SizeUtils.dp2px(120f)
        } else {
            tv.inputType = inputType
//            tv.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        view?.text?.length?.let { tv.setSelection(it) }
        //        tv.postDelayed({KeyboardUtils.showSoftInput(tv)},200)
        findViewById<View>(R.id.iv_close).setOnClickListener(this)
        findViewById<View>(R.id.tv_ok).setOnClickListener(this)
    }

    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation().withAlpha(AlphaConfig.IN).toShow()
    }

    override fun onCreateDismissAnimation(): Animation {
        return AnimationHelper.asAnimation().withAlpha(AlphaConfig.OUT).toDismiss()
    }

}