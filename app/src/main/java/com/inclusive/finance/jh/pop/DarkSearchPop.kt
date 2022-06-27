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
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.ScreenUtils
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.adapter.ItemSearchTextAdapter
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.databinding.PopDarkSearchBinding


class DarkSearchPop(var mContext: Context?, var textView: TextView, var item: BaseTypeBean,var type:Int, var listener: (() -> Unit?)? = {}) :
    DialogFragment(), View.OnClickListener {
    override fun onClick(v: View?) {

    }

    override fun onStart() {
        super.onStart()
        val params = dialog?.window?.attributes
        dialog?.setCanceledOnTouchOutside(true)
        params?.width = ScreenUtils.getScreenWidth()
        params?.height = RelativeLayout.LayoutParams.MATCH_PARENT
        params?.gravity = Gravity.TOP
        //高度自己定义
        dialog?.window?.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)

    }

    lateinit var dataBind: PopDarkSearchBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明
        dataBind = PopDarkSearchBinding.inflate(inflater, container, false)
        return dataBind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ItemSearchTextAdapter<Any>()
        adapter.setOnItemClickListener { _, _, position ->
            dataBind.searchView.setQuery(adapter.data[position].toString(),true)
            when (type) {
                BaseTypeBean.TYPE_22 -> {
                    item.valueName=(adapter.data[position] as SearchBean).code+"##"+(adapter.data[position] as SearchBean).name
                }
            }

        }
        dataBind.mRecyclerView.adapter=adapter
        dataBind.searchView.apply {

            onActionViewExpanded()
//            isSubmitButtonEnabled=true
//            findViewById<AppCompatImageView>(R.id.search_go_btn).setImageDrawable(ContextCompat.getDrawable(mContext!!,R.drawable.ic_baseline_check_24))
            queryHint=textView.hint
            setQuery(textView.text,false)

            fun query(query: String) {
                when (type) {
                    BaseTypeBean.TYPE_20 -> {
                        DataCtrlClass.SXSQNet.searchQuery(mContext, query, item.dataKey) {
                            if (it != null) {
                                adapter.setNewInstance(it as ArrayList<Any>)
                            }
                        }
                    }
                    BaseTypeBean.TYPE_22 -> {
                        DataCtrlClass.SXSQNet.searchQuery_fzjg(mContext, query) {
                            if (it != null) {
                                adapter.setNewInstance(it as ArrayList<Any>)
                            }
                        }
                    }
                }

            }
            query(textView.text.toString())
            // 监听搜索框文字变化
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(newText: String): Boolean {
                    textView.text = dataBind.searchView.query
                    dismiss()
                    return false
                }

                override fun onQueryTextChange(query: String): Boolean {
                    query(query)
                    return false
                }


            })
//            setOnMicClickListener(object : SearchLayout.OnMicClickListener {
//                override fun onMicClick() {
//                    textView.text=dataBind.searchView.getTextQuery()
//                    dismiss()
//                }
//            })

        }
    }
    class SearchBean(
        val code: String,
        val id: String,
        val name: String
    ){
        override fun toString(): String {
            return name
        }
    }
}