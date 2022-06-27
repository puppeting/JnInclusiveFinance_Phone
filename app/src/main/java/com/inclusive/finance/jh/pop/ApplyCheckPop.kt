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
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.PopBaseTypeBinding
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import org.jetbrains.anko.support.v4.act

class ApplyCheckPop(var mContext: Context?, fragment: MyBaseFragment, var businessType: Int? = 0, var keyId: String = "", var listener: (id: String) -> Unit) :
    DialogFragment(), View.OnClickListener {
    var adapter: ItemBaseTypeAdapter<BaseTypeBean> = ItemBaseTypeAdapter(fragment)
    var bean: ArrayList<BaseTypeBean>? = null
    var isChecked = false
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_close, R.id.tv_cancel -> {
                dismiss()
            }
            R.id.tv_ok -> {
                bean?.forEach {
                    if (it.requireable && it.editable) if (it.valueName.isEmpty()) {
                        SZWUtils.showSnakeBarMsg(dataBind.root, "请补充" + it.keyName)
                        return
                    }
                }
                when (businessType) {
                    ApplyModel.BUSINESS_TYPE_VISIT -> {
                        DataCtrlClass.VisitNet.preCredit_add(mContext, saveUrl, keyId, contentView = dataBind.root) {
                            if (it != null) {
                                listener.invoke(it)
                                dismiss()
                            }
                        }

                    }
                    else -> {
                        if (!isChecked) DataCtrlClass.SXSQNet.sxsqCheck(mContext, checkUrl, bean?.firstOrNull { it.dataKey == "idCardFront" }?.picUrl
                            ?: "", bean?.firstOrNull { it.dataKey == "idCardBack" }?.picUrl
                            ?: "", bean?.firstOrNull { it.dataKey == "idenNo" }?.valueName
                            ?: "", bean?.firstOrNull { it.dataKey == "name" }?.valueName
                            ?: "", bean?.firstOrNull { it.dataKey == "certsigndate" }?.valueName
                            ?: "", bean?.firstOrNull { it.dataKey == "certmaturiy" }?.valueName
                            ?: "", bean?.firstOrNull { it.dataKey == "hjAddr" }?.valueName
                            ?: "", contentView = dataBind.root) {
                            if (it != null) {
                                adapter.setNewInstance(it)
                                isChecked = true
                            }
                        } else {
                            DataCtrlClass.SXSQNet.sxsqAdd(mContext, saveUrl, bean?.firstOrNull { it.dataKey == "idCardFront" }?.picUrl
                                ?: "", bean?.firstOrNull { it.dataKey == "idCardBack" }?.picUrl
                                ?: "", bean?.firstOrNull { it.dataKey == "idenNo" }?.valueName
                                ?: "", bean?.firstOrNull { it.dataKey == "name" }?.valueName
                                ?: "", bean?.firstOrNull { it.dataKey == "certsigndate" }?.valueName
                                ?: "", bean?.firstOrNull { it.dataKey == "certmaturiy" }?.valueName
                                ?: "", bean?.firstOrNull { it.dataKey == "hjAddr" }?.valueName
                                ?: "", contentView = dataBind.root) {
                                if (it != null) {
                                    listener.invoke(it)
                                    dismiss()
                                }
                            }
                        }
                    }


                }
            }
        }
    }

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

    lateinit var dataBind: PopBaseTypeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明

        dataBind = PopBaseTypeBinding.inflate(inflater, container, false)
        return dataBind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataBind.tvTitle.text = "添加申请"
        adapter.dialogPop = dialog
//        dataBind.mRecyclerView.layoutManager = GridLayoutManager(mContext, 6)
        dataBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        dataBind.mRecyclerView.setItemViewCacheSize(200)
        dataBind.mRecyclerView.adapter = adapter
        dataBind.mRecyclerView.setHasFixedSize(false)
        dataBind.tvCancel.setOnClickListener(this)
        dataBind.ivClose.setOnClickListener(this)
        dataBind.tvOk.setOnClickListener(this)
    }

    var getUrl = ""
    var checkUrl = ""
    var saveUrl = ""
    override fun show(manager: FragmentManager, tag: String?) {
        when (businessType) {
            ApplyModel.BUSINESS_TYPE_VISIT -> {
                getUrl = Urls.get_visit_preCredit_check
                saveUrl = Urls.save_visit_preCredit_add
            }
            ApplyModel.BUSINESS_TYPE_PRECREDIT -> {
                getUrl = Urls.get_preCredit_new
                checkUrl = Urls.check_preCredit_new
                saveUrl = Urls.save_preCredit_new
            }
            ApplyModel.BUSINESS_TYPE_APPLY -> {
                getUrl = Urls.sxsqPreAdd
                checkUrl = Urls.sxsqCheck
                saveUrl = Urls.sxsqAdd
            }
            ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY -> {
                getUrl = Urls.sunshineSxsqPreAdd
                checkUrl = Urls.sunshineSxsqCheck
                saveUrl = Urls.sunshineSxsqAdd
            }
        }
        DataCtrlClass.KHGLNet.getBaseTypePoPList(mContext, getUrl, "", null, keyId = keyId) {
            if (it != null) {
                adapter.setNewInstance(it)
                bean = it
                if (!manager.isDestroyed) {
                    super.show(manager, tag)
                }
            }
        }
    }

}