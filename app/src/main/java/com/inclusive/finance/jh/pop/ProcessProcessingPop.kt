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
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ObjectUtils
import com.blankj.utilcode.util.ScreenUtils
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.CheckProgressAdapter
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.Clrlist
import com.inclusive.finance.jh.bean.ConfigurationBean
import com.inclusive.finance.jh.bean.NodeBean
import com.inclusive.finance.jh.databinding.DialogApprovalBinding
import com.inclusive.finance.jh.glide.imageloder.GlideApp
import com.inclusive.finance.jh.utils.EditTextRegex
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil

class ProcessProcessingPop(
    var mContext: Context?,
    var data: ConfigurationBean,
    var keyId: String? = "",
    var businessType: Int = 0,
    var listener: () -> Unit
) :
    DialogFragment(), View.OnClickListener {
    var mAdapter: CheckProgressAdapter = CheckProgressAdapter()
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.iv_close, R.id.tv_cancel -> dismiss()
            R.id.iv_qianzi -> {
                QianZiBanPop(mContext) { requestCode: Int, url: String ->
                    DataCtrlClass.uploadFiles(
                        mContext,
                        keyId = keyId,
                        type = "",
                        files = arrayListOf(FileUtils.getFileByPath(url))
                    ) { urls ->
                        if (urls != null) {
                            GlideApp.with(this).load(SZWUtils.getIntactUrl(urls[0].filePath)).centerInside().into(dataBind.ivQianzi)

                            confirmNodeBean.fj = urls[0].filePath ?: ""
                        }
                    }

                }.apply {
                    requestCode = 200
                }.show(childFragmentManager, this.javaClass.name)
            }
            R.id.tv_temp -> {
                confirmNodeBean.sfzc="1"
                save()
            }
            R.id.tv_ok -> {
                //                if (!isAdmin && ObjectUtils.isEmpty(confirmNodeBean.fj)) {

                confirmNodeBean.sfzc="0"
                save()
            }
        }
    }

    private fun save() {
        confirmNodeBean.bz = dataBind.etMs.text.toString().trim { it <= ' ' }
        confirmNodeBean.finalAmt = dataBind.zzqdsxed.text.toString().toDoubleOrNull()
        confirmNodeBean.relationid = keyId ?: ""

        if (confirmNodeBean.sfzc=="0") {
            if (ObjectUtils.isEmpty(confirmNodeBean.fj)) {
                SZWUtils.showSnakeBarMsg(contentView = dataBind.root, "未签名")
                return
            }
            if (dataBind.nodeNameLay.isShown && dataBind.nodeName.text.isNullOrEmpty()) {
                SZWUtils.showSnakeBarMsg(contentView = dataBind.root, "未选择节点")
                return
            }
            if (ObjectUtils.isEmpty(confirmNodeBean.bz)) {
                SZWUtils.showSnakeBarMsg(contentView = dataBind.root, "未填写审批意见")
                return
            }
            if (ObjectUtils.isEmpty(confirmNodeBean.sfhb) && dataBind.huiBanLay.visibility == View.VISIBLE) {
                SZWUtils.showSnakeBarMsg(contentView = dataBind.root, "请选择是否会办")
                return
            }
        }
        DataCtrlClass.SXSPNet.saveSXSP(
            mContext,
            confirmNodeBean,
            businessType = businessType,
            contentView = dataBind.root
        ) {
            if (it != null) {
                dismiss()
            }
        }
    }

    override fun dismiss() {
        listener.invoke()
        super.dismiss()
    }

    //    override fun onViewCreated(contentView: View) {
    //        super.onViewCreated(contentView)
    //        setAdjustInputMethod(true)
    //        //        setAutoShowInputMethod(findViewById(R.id.tv), true)
//            setAdjustInputMode(R.id.et_ms, BasePopupWindow.FLAG_KEYBOARD_ALIGN_TO_ROOT or BasePopupWindow.FLAG_KEYBOARD_ANIMATE_ALIGN or BasePopupWindow.FLAG_KEYBOARD_FORCE_ADJUST)
    //    }
    lateinit var dataBind: DialogApprovalBinding
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
        dialog?.window?.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )

    }

    override fun show(manager: FragmentManager, tag: String?) {
//        DataCtrlClass.SXSPNet.getList_flowHis(
//            mContext,
//            keyId = keyId,
//            type = data.type,
//            businessType = businessType
//        ) {
//            if (it != null) {
//                dataBind.mRecyclerView.layoutManager = LinearLayoutManager(mContext)
//                dataBind.mRecyclerView.adapter = mAdapter
//                mAdapter.setNewInstance(it)
//                if (it.size <= 0) SZWUtils.showSnakeBarMsg(dataBind.root, "暂无进度")
//            }
//        }
        if (!manager.isDestroyed) {
            super.show(manager, tag)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明

        dataBind = DialogApprovalBinding.inflate(inflater, container, false)
        return dataBind.root
    }

    var confirmNodeBean = Clrlist.ConfirmNodeBean()
    //    private var isAdmin = false //是否总经理

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataBind.ivClose.setOnClickListener(this)
        dataBind.tvCancel.setOnClickListener(this)
        dataBind.ivQianzi.setOnClickListener(this)
        dataBind.tvTemp.setOnClickListener(this)
        dataBind.tvOk.setOnClickListener(this)
        confirmNodeBean.type = data.type ?: ""
        if (ObjectUtils.isNotEmpty(data.current?.nodeName)) {
            dataBind.tvMyInfo.text = data.current?.nodeName
        }
        if (ObjectUtils.isNotEmpty(data.current?.remarks)) {
            dataBind.etMs.setText(data.current?.remarks)
        }
        if (ObjectUtils.isNotEmpty(data.current?.annex)) {
            GlideApp.with(this).load(SZWUtils.getIntactUrl(data.current?.annex)).centerInside().into(dataBind.ivQianzi)

            confirmNodeBean.fj=data.current?.annex?:""
        }

        //        if ("
        //        分发岗".equals(tiJiaoBean.getCurrentTaskName())) {
        //            ivQianzi.setVisibility(View.GONE);
        //        }

        //        if (data.current?.nodeName?.contains("总经理") == true) {
        //            dataBind.layoutQm.visibility = View.GONE
        //            isAdmin = true
        //        }
        dataBind.rb2.visibility = if (data.pre == null) View.GONE else View.VISIBLE
        dataBind.rb3.visibility = if (businessType < 50) View.VISIBLE else View.GONE
        if (!data.nexts.isNullOrEmpty()) {
            data.next = data.nexts?.get(0)
        }
        dataBind.radioGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.rb1 -> {
                    initNextDownView(data.nexts) {
                        initR1()
                    }
                    initR1()
                }
                R.id.rb2 -> {
                    dataBind.tvNl.text = "下环节审核人员:"
                    dataBind.rlSelect.visibility = View.GONE
                    dataBind.nodeNameLay.visibility = View.VISIBLE
                    dataBind.huiBanLay.visibility = View.GONE
                    dataBind.hbyjLay.visibility = View.GONE
                    dataBind.sxedLay.visibility = View.GONE
                    dataBind.zzqdsxedLay.visibility = View.GONE
                    initPreDownView(data.pre)
                }
                R.id.rb3 -> {
                    //  czlx reject
                    confirmNodeBean.czlx = "reject"
                    dataBind.rlSelect.visibility = View.GONE
                    dataBind.nodeNameLay.visibility = View.GONE
                    dataBind.huiBanLay.visibility = View.GONE
                    dataBind.hbyjLay.visibility = View.GONE
                    dataBind.sxedLay.visibility = View.GONE
                    dataBind.zzqdsxedLay.visibility = View.GONE
                }
                else -> {
                }


            }
        }
        EditTextRegex.textRegex(view = dataBind.etMs)
        dataBind.radioGroup.check(R.id.rb1)
    }

    private fun initR1() {
        if (data.next?.handlerNo != null || data.next?.handlerList?.size ?: 0 > 0) {
            dataBind.rlSelect.visibility = View.VISIBLE
            dataBind.ddvXb.text = data.next?.handler
        } else {
            dataBind.rlSelect.visibility = View.GONE
        }


        if (data.next?.handlerNo != null) {
            dataBind.ddvXb.text = data.next?.handler
            dataBind.ddvXb.isClickable = false
            confirmNodeBean.clrgh = data.next?.handlerNo.toString()
        } else if (data.next?.handlerNo == null && data.next?.handlerList?.size ?: 0 > 0) {
            initDownView(data.next)
        } //下一节点   next node name
        dataBind.nodeNameLay.visibility = View.VISIBLE
        dataBind.nodeName.text = data.next?.nodeName
        if (data.next?.endSign == "1") {
            dataBind.tvNl.text = "当前环节审核人员:"
        } else {
            dataBind.tvNl.text = "下环节审核人员:"
        }

        confirmNodeBean.nodebh = data.next?.nodeNo.toString() // czlx agree
        confirmNodeBean.czlx = "agree"


        //贷后增加
        //是否会办
        dataBind.huiBanLay.visibility = View.GONE
        dataBind.hbyjLay.visibility = View.GONE
        dataBind.sxedLay.visibility = View.GONE
        dataBind.zzqdsxedLay.visibility = View.GONE
        if (!data.next?.item1.isNullOrEmpty() && data.next?.item1 == "1") {
            dataBind.huiBanLay.visibility = View.VISIBLE
            initHuiBanDownView()
        } else {
            dataBind.huiBanLay.visibility = View.GONE
        }
        if (data.current?.nodeNo == "xc-task-2") {
            dataBind.sxedLay.visibility = View.VISIBLE
            dataBind.zzqdsxedLay.visibility = View.VISIBLE
            dataBind.zzqdsxed.inputType = 8194
            dataBind.zzqdsxed.setText(data.current?.finalAmt.toString())
            initSXEDDownView(data.current)
        }
        if (!data.current?.item2.isNullOrEmpty()) {
            dataBind.hbyjLay.visibility = View.VISIBLE
            dataBind.hbyj.text = data.current?.item2
            confirmNodeBean.hbyj = data.current?.item2 ?: ""
        }
    }

    //授信额度
    private fun initSXEDDownView(current: NodeBean?) {
        //        01 调增
        //        02 调减
        //        03 维持
        //        04 取消
        val dataList = ArrayList<BaseTypeBean.Enum12>()
        dataList.add(BaseTypeBean.Enum12().apply {
            valueName = "调增"
            keyName = "01"
        })
        dataList.add(BaseTypeBean.Enum12().apply {
            valueName = "调减"
            keyName = "02"
        })
        dataList.add(BaseTypeBean.Enum12().apply {
            valueName = "维持"
            keyName = "03"
        })
        dataList.add(BaseTypeBean.Enum12().apply {
            valueName = "取消"
            keyName = "04"
        })
        dataBind.sxed.text = dataList.firstOrNull { it.keyName == current?.creditAmtCd }?.valueName
        confirmNodeBean.creditAmtCd = current?.creditAmtCd ?: ""
        dataBind.sxed.setOnClickListener {
            DownPop(
                dialog,
                enums12 = dataList,
                checkedTextView = it as AppCompatCheckedTextView,
                isSingleChecked = true
            ) { key, value, position ->
                confirmNodeBean.creditAmtCd = key
            }.showPopupWindow(it)
        }
    }

    private fun initHuiBanDownView() {
        val dataList = ArrayList<BaseTypeBean.Enum12>()
        dataList.add(BaseTypeBean.Enum12().apply {
            valueName = "是"
            keyName = "1"
        })
        dataList.add(BaseTypeBean.Enum12().apply {
            valueName = "否"
            keyName = "2"
        })
        dataBind.huiban.setOnClickListener {
            DownPop(
                dialog,
                enums12 = dataList,
                checkedTextView = it as AppCompatCheckedTextView,
                isSingleChecked = true
            ) { key, value, position ->
                confirmNodeBean.sfhb = key
            }.showPopupWindow(it)
        }
    }

    private fun initDownView(pre: NodeBean?) {
        val dataList = ArrayList<BaseTypeBean.Enum12>()
        dataBind.ddvXb.isClickable = true
        dataList.clear()
        pre?.handlerList?.forEach {
            dataList.add(BaseTypeBean.Enum12().apply {
                valueName = it.realname + it.username
                keyName = it.username.toString()
            })
        }
        if (dataList.size > 0) dataBind.ddvXb.setOnClickListener {
            DownPop(
                dialog,
                enums12 = dataList,
                checkedTextView = it as AppCompatCheckedTextView,
                isSingleChecked = true
            ) { key, value, position ->
                confirmNodeBean.clrgh = key
            }.showPopupWindow(it)
        }
    }

    private fun initPreDownView(pre: List<NodeBean>?) {
        val dataList = ArrayList<BaseTypeBean.Enum12>()
        dataBind.nodeName.isClickable = true
        dataBind.nodeName.text = ""
        dataList.clear()
        pre?.forEach {
            dataList.add(BaseTypeBean.Enum12().apply {
                valueName = it.nodeName ?: ""
                keyName = it.nodeName ?: ""
            })
        }
        if (dataList.size > 0) dataBind.nodeName.setOnClickListener {
            DownPop(
                dialog,
                enums12 = dataList,
                checkedTextView = it as AppCompatCheckedTextView,
                isSingleChecked = true
            ) { key, value, position ->
                dataBind.rlSelect.visibility = View.VISIBLE
                dataBind.ddvXb.text = pre?.get(position)?.handler
                dataBind.ddvXb.isClickable = false
                //下一节点   pre node name
                confirmNodeBean.clrgh = pre?.get(position)?.handlerNo.toString()

                confirmNodeBean.nodebh = pre?.get(position)?.nodeNo.toString()
                //  czlx returnTo
                confirmNodeBean.czlx = "returnTo"
                initDownView(pre?.get(position))
            }.showPopupWindow(it)
        }
    }

    private fun initNextDownView(next: List<NodeBean>?, listener: () -> Unit) {
        val dataList = ArrayList<BaseTypeBean.Enum12>()
        dataList.clear()
        next?.forEach {
            dataList.add(BaseTypeBean.Enum12().apply {
                valueName = it.nodeName ?: ""
                keyName = it.nodeName ?: ""
            })
        }
        if (dataList.size > 0) dataBind.nodeName.setOnClickListener {
            DownPop(
                dialog,
                enums12 = dataList,
                checkedTextView = it as AppCompatCheckedTextView,
                isSingleChecked = true
            ) { key, value, position ->
                data.next = data.nexts?.get(position)
                listener.invoke()
            }.showPopupWindow(it)
        }
    }

}