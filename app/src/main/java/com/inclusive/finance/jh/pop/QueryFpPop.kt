package com.inclusive.finance.jh.pop


import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.CustomExpandableListAdapter
import com.inclusive.finance.jh.adapter.ItemDownAdapter
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.databinding.PopQueryFpRecyclerBinding
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AlphaConfig
import razerdp.util.animation.AnimationHelper


class QueryFpPop : BasePopupWindow, OnItemClickListener {
    private var jycode: String = ""
    private var lccode: String = ""
    private var dbcode: String = ""
    private var jgcode: String = ""
    lateinit var adapter: ItemDownAdapter<BaseTypeBean.Enum12>
    var bean: BaseTypeBean? = null
    var enums12: LinkedHashMap<String, ArrayList<BaseTypeBean.Enum12>>? = null
    var checkedTextView: View? = null
    var isSingleChecked: Boolean = false
    var listener: ((zhen: String, cun: String, hjbh: String, khmc: String, zjhm: String, zt: String) -> Unit)? = null

    constructor(context: Dialog?, listener: ((cun: String, zhen: String, hjbh: String, khmc: String, zjhm: String, zt: String) -> Unit)? = null) : super(context) {
        init(listener)
    }

    constructor(context: Context?,listener: ((cun: String, zhen: String, hjbh: String, khmc: String, zjhm: String, zt: String) -> Unit)? = null) : super(context) {
        init( listener)
    }

    private fun init(listener: ((cun: String, zhen: String, hjbh: String, khmc: String, zjhm: String, zt: String) -> Unit)?) {
        this.bean = bean
        this.enums12 = enums12
        this.checkedTextView = checkedTextView
        this.isSingleChecked = isSingleChecked
        this.listener = listener
        setOutSideDismiss(true)
        setBackground(0)
        setWidthAsAnchorView(true)

        adapter = ItemDownAdapter()
        adapter.setOnItemClickListener(this)
        var expandableListView = dataBind.root.findViewById<ExpandableListView>(R.id.expandableListView)
        if (expandableListView != null) {
            titleList?.add("机构")
            titleList?.add("流程状态")
            titleList?.add("检验状态")
            titleList?.add("是否待办")
            adapter2 = enums12?.let { CustomExpandableListAdapter(context, titleList as ArrayList<String>, it) }
            expandableListView!!.setAdapter(adapter2)

            expandableListView!!.setOnGroupExpandListener { groupPosition ->
                for (index in 0..titleList?.size!! - 1) {
                    if (index == groupPosition) {

                    } else {
                        expandableListView?.collapseGroup(index)
                    }
                }


            }

            expandableListView!!.setOnGroupCollapseListener { groupPosition ->
            }

            expandableListView!!.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
                Log.e("点击时间", "" + groupPosition + "&&&" + childPosition)
                enums12?.get((titleList as ArrayList<String>)[groupPosition])
                    ?.forEachIndexed { index, enum12 ->
                        enum12.checked = index == childPosition
                    }
                enums12?.let { (adapter2 as CustomExpandableListAdapter?)?.setPoss(it) }
                when ((titleList as ArrayList<String>)[groupPosition]) {
                    "机构" -> {
                        jgcode = enums12?.get((titleList as ArrayList<String>)[groupPosition])
                            ?.get(childPosition)?.keyName.toString()
                        enums12?.get((titleList as ArrayList<String>)[groupPosition])
                    }
                    "流程状态" -> lccode = enums12?.get((titleList as ArrayList<String>)[groupPosition])
                        ?.get(childPosition)?.keyName.toString()
                    "检验状态" -> jycode = enums12?.get((titleList as ArrayList<String>)[groupPosition])
                        ?.get(childPosition)?.keyName.toString()
                    "是否待办" -> dbcode = enums12?.get((titleList as ArrayList<String>)[groupPosition])
                        ?.get(childPosition)?.keyName.toString()
                }
                false
            }
        }
        dataBind?.search?.setOnClickListener {
            isChecked = true
            listener?.invoke(dataBind?.tvZhen?.text.toString(),  dataBind?.tvCun?.text.toString(), dataBind?.tvNum?.text.toString(), dataBind?.tvValue?.text.toString(), dataBind?.tvZjhm?.text.toString(),dataBind?.tvZt?.text.toString())
            dismiss()
        }
        dataBind?.reset?.setOnClickListener {
            isChecked = true
            listener?.invoke("","","","","","")

            dismiss()
        }

    }


    override fun showPopupWindow(anchorView: View?) {
        super.showPopupWindow(anchorView)
    }

    /**
     * 设置透明度(context)
     * @param bgAlpha[0-1] 1表示不透明
     */


    internal var adapter2: ExpandableListAdapter? = null
    var titleList: ArrayList<String>? = arrayListOf()
    lateinit var dataBind: PopQueryFpRecyclerBinding
    override fun onCreateContentView(): View { //        dataBind = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.pop_down_recycler,null,false)
        dataBind = PopQueryFpRecyclerBinding.bind(createPopupById(R.layout.pop_query_fp_recycler))

        return dataBind.root
    }


    val str = arrayListOf<String>()
    val key = arrayListOf<String>()
    var singleCheckindex = -1
    var isChecked = false
    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        str.clear()
        key.clear()
        singleCheckindex = -1

        val keysToString = key.joinToString(",")
        bean?.valueName = keysToString
        val namesToString = str.joinToString(",")
        isChecked = singleCheckindex != -1
        if (isSingleChecked) dismiss()
    }

    override fun onDismiss() {
        listener?.invoke(dataBind?.tvZhen?.text.toString(),  dataBind?.tvCun?.text.toString(), dataBind?.tvNum?.text.toString(), dataBind?.tvValue?.text.toString(), dataBind?.tvZjhm?.text.toString(),dataBind?.tvZt?.text.toString())
        setPopWindowBackgroundAlpha(1f)
        super.onDismiss()
    }

    override fun onCreateShowAnimation(): Animation {
        setPopWindowBackgroundAlpha(0.5f)
        return AnimationHelper.asAnimation().withAlpha(AlphaConfig.IN).toShow()
    }

    override fun onCreateDismissAnimation(): Animation {
        val toDismiss = AnimationHelper.asAnimation().withAlpha(AlphaConfig.OUT).toDismiss()
        toDismiss.duration = 0

        return toDismiss
    }

    private fun setPopWindowBackgroundAlpha(bgAlpha: Float) {
        val lp: WindowManager.LayoutParams = context.window.attributes
        lp.alpha = bgAlpha //[0.0-1.0]
        if (bgAlpha == 1f) {
            context.window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        } else {
            context.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
        context.window.setAttributes(lp)
    }

}