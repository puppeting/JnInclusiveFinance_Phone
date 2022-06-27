package com.inclusive.finance.jh.pop


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
import com.blankj.utilcode.util.ScreenUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.inclusive.finance.jh.adapter.ItemDownAdapter
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.databinding.PopDownRecyclerBinding
import com.inclusive.finance.jh.utils.StatusBarUtil


class DownPop2(var bean: BaseTypeBean?, var enums12: ArrayList<BaseTypeBean.Enum12>?, var checkedTextView: AppCompatCheckedTextView?, var isSingleChecked: Boolean, var listener: ((key: String, value: String, position: Int) -> Unit)?) : DialogFragment(), OnItemClickListener {
    lateinit var adapter: ItemDownAdapter<BaseTypeBean.Enum12>





//    private fun init(bean: BaseTypeBean?, enums12: ArrayList<BaseTypeBean.Enum12>?, checkedTextView: AppCompatCheckedTextView?, isSingleChecked: Boolean, listener: ((key: String, value: String, position: Int) -> Unit)?) {
//        this.bean = bean
//        this.enums12 = enums12
//        this.checkedTextView = checkedTextView
//        this.isSingleChecked = isSingleChecked
//        this.listener = listener
//        val split = bean?.valueName?.split(",")
//        enums12?.forEach { enum ->
//            val firstOrNull = split?.firstOrNull {
//                if (enum.keyName.isNotEmpty()) {
//                    it == enum.keyName
//                } else {
//                    it == enum.valueName
//                }
//            }
//            enum.checked = !firstOrNull.isNullOrEmpty()
//        }
//        adapter = ItemDownAdapter()
//        dataBind.mRecyclerView.adapter = adapter
//        adapter.setNewInstance(enums12)
//        adapter.setOnItemClickListener(this)
//    }




    //    override fun onCreateContentView(): View {
    //        return createPopupById(R.layout.pop_down_recycler)
    //    }
    lateinit var dataBind: PopDownRecyclerBinding
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

        dataBind = PopDownRecyclerBinding.inflate(inflater, container, false)
        return dataBind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
        listener?.invoke(key.joinToString(","), str.joinToString(","), singleCheckindex)
        checkedTextView?.isChecked = false
        dismiss()
    }









}