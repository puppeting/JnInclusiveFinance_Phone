package com.inclusive.finance.jh.pop


import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatCheckedTextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemDownAdapter
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.databinding.PopDownRecyclerBinding
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AlphaConfig
import razerdp.util.animation.AnimationHelper


class DownPop : BasePopupWindow, OnItemClickListener {
    lateinit var adapter: ItemDownAdapter<BaseTypeBean.Enum12>
    var bean: BaseTypeBean? = null
    var enums12: ArrayList<BaseTypeBean.Enum12>? = null
    var checkedTextView: AppCompatCheckedTextView? = null
    var isSingleChecked: Boolean = false
    var listener: ((key: String, value: String, position: Int) -> Unit)? = null

    constructor(context: Dialog?, bean: BaseTypeBean? = null, enums12: ArrayList<BaseTypeBean.Enum12>?, checkedTextView: AppCompatCheckedTextView?, isSingleChecked: Boolean = true, listener: ((key: String, value: String, position: Int) -> Unit)? = null) : super(context) {
        init(bean, enums12, checkedTextView, isSingleChecked, listener)
    }

    constructor(context: Context?, bean: BaseTypeBean? = null, enums12: ArrayList<BaseTypeBean.Enum12>?, checkedTextView: AppCompatCheckedTextView?, isSingleChecked: Boolean = true, listener: ((key: String, value: String, position: Int) -> Unit)? = null) : super(context) {
        init(bean, enums12, checkedTextView, isSingleChecked, listener)
    }

    private fun init(bean: BaseTypeBean?, enums12: ArrayList<BaseTypeBean.Enum12>?, checkedTextView: AppCompatCheckedTextView?, isSingleChecked: Boolean, listener: ((key: String, value: String, position: Int) -> Unit)?) {
        this.bean = bean
        this.enums12 = enums12
        this.checkedTextView = checkedTextView
        this.isSingleChecked = isSingleChecked
        this.listener = listener
        setOutSideDismiss(true)
        setBackground(0)
        setWidthAsAnchorView(true)
        val split = bean?.valueName?.split(",")
        enums12?.forEach { enum ->
            val firstOrNull = split?.firstOrNull {
                if (enum.keyName.isNotEmpty()) {
                    it == enum.keyName
                } else {
                    it == enum.valueName
                }
            }
            enum.checked = !firstOrNull.isNullOrEmpty()
        }
        adapter = ItemDownAdapter()
        dataBind.mRecyclerView.adapter = adapter
        adapter.setNewInstance(enums12)
        adapter.setOnItemClickListener(this)
    }


    override fun showPopupWindow(anchorView: View?) {
        checkedTextView?.isChecked = true
        super.showPopupWindow(anchorView)
    }

    //    override fun onCreateContentView(): View {
    //        return createPopupById(R.layout.pop_down_recycler)
    //    }
    lateinit var dataBind: PopDownRecyclerBinding
    override fun onCreateContentView(): View { //        dataBind = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.pop_down_recycler,null,false)
        dataBind = PopDownRecyclerBinding.bind(createPopupById(R.layout.pop_down_recycler))
        return dataBind.root
    }
    val str = arrayListOf<String>()
    val key = arrayListOf<String>()
    var singleCheckindex= -1
    var isChecked=false
    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        str.clear()
        key.clear()
        singleCheckindex= -1
        if (isSingleChecked) {
            enums12?.forEach { it.checked = false }
        }
        enums12?.get(position)?.checked = enums12?.get(position)?.checked?.not() ?: false
        enums12?.forEachIndexed { index, enum12 ->
            if (enum12.checked) {
                str.add(enum12.valueName)
                key.add((if (enum12.keyName.isNotEmpty()) enum12.keyName else enum12.valueName))
                singleCheckindex=index
            }
        } //        if (str.isNotEmpty()) str = str.substring(0, str.length - 1)

        val keysToString = key.joinToString(",")
        bean?.valueName = keysToString
        val namesToString = str.joinToString(",")
        checkedTextView?.text = namesToString
        isChecked=singleCheckindex!=-1
        if (isSingleChecked) dismiss()
    }

    override fun onDismiss() {
        if (isChecked)
        listener?.invoke(key.joinToString(","), str.joinToString(","), singleCheckindex)
        checkedTextView?.isChecked = false
        super.onDismiss()
    }

    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation().withAlpha(AlphaConfig.IN).toShow()
    }

    override fun onCreateDismissAnimation(): Animation {
        val toDismiss = AnimationHelper.asAnimation().withAlpha(AlphaConfig.OUT).toDismiss()
        toDismiss.duration = 0
        return toDismiss
    }


}