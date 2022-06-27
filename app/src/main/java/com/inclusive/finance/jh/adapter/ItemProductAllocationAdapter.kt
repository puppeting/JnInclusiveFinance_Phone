package com.inclusive.finance.jh.adapter

import android.app.Dialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.adapters.ListenerUtil
import androidx.databinding.adapters.TextViewBindingAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.databinding.ItemKVProductAllocationItemBinding
import com.inclusive.finance.jh.pop.DownPop
import com.inclusive.finance.jh.utils.SZWUtils


class ItemProductAllocationAdapter<T : JsonObject>(var dialogPop: Dialog? = null, var editable: Boolean) :
    BaseQuickAdapter<T, BaseViewHolder>(R.layout.item_k_v_product_allocation_item, ArrayList()) {


    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(ItemKVProductAllocationItemBinding.inflate(LayoutInflater.from(context), parent, false).root)
    }

    override fun convert(holder: BaseViewHolder, item: T) {
        DataBindingUtil.getBinding<ItemKVProductAllocationItemBinding>(holder.itemView)?.apply {

            data = this@ItemProductAllocationAdapter.data as java.util.ArrayList<JsonObject>
            json = item
            this.holder=holder
            this.editable = this@ItemProductAllocationAdapter.editable
            val jsonObjectString = SZWUtils.getJsonObjectString(item, "productValue")
            tvValue1.isEnabled = this@ItemProductAllocationAdapter.editable

            val jsonObjectArray = SZWUtils.getJsonObjectArray(item, "enums12")
            val enum12s = Gson().fromJson<ArrayList<BaseTypeBean.Enum12>>(jsonObjectArray, object :
                TypeToken<ArrayList<BaseTypeBean.Enum12>>() {}.type)

            if (jsonObjectString.isNotEmpty()) {
                val list = arrayListOf<String>()
                val split = jsonObjectString.split(",")
                split.forEach { value ->
                    enum12s?.forEach continuing@{
                        if (it.keyName.isNotEmpty()) {
                            if (value == it.keyName) {
                                list.add(it.valueName)
                                return@forEach
                            }
                        } else {
                            list.add(value)
                            return@forEach
                        }

                    }
                }
                tvValue.text = list.joinToString(",")
            } else {
                tvValue.text = jsonObjectString
            }

            val bean = BaseTypeBean().apply {
                valueName = tvValue.text.toString()
            }
            btMinus.setOnClickListener {
                this@ItemProductAllocationAdapter.data.removeAt(holder.adapterPosition)
                notifyItemRemoved(holder.adapterPosition)
            }
            tvValue.setOnClickListener {
                if (dialogPop == null) {
                    DownPop(context, bean = bean, enums12=enum12s, isSingleChecked = true, checkedTextView = tvValue) { k, v, i ->
                        item.addProperty("productValue", k)
                    }.showPopupWindow(tvValue)
                } else {
                    DownPop(dialogPop, bean = bean, enums12=enum12s, isSingleChecked = true, checkedTextView = tvValue) { k, v, i ->
                        item.addProperty("productValue", k)
                    }.showPopupWindow(tvValue)
                }
            }

        }

    }

    companion object {
        var data: ArrayList<JsonObject>? = null

        @JvmStatic
        @BindingAdapter("putJsonData")
        fun putJsonData(view: EditText, jsons: ArrayList<JsonObject>?) {
            data = jsons
        }

        @JvmStatic
        @BindingAdapter("productAllocationText")
        fun setTextString(view: EditText, json: JsonObject?) {
            val oldText = view.text
            val jsonObjectString = SZWUtils.getJsonObjectString(json, "priceValue")
            if (jsonObjectString == oldText.toString()) {
                return
            }
            view.setText(jsonObjectString)
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "productAllocationText", event = "productAllocationTextAttrChanged")
        fun getTextString(view: EditText): JsonObject? {
            val json = data?.get(view.tag as Int)
            json?.addProperty("priceValue", view.text.toString())
            return json
        }

        @JvmStatic
        @BindingAdapter(value = ["android:beforeTextChanged", "android:onTextChanged", "android:afterTextChanged", "productAllocationTextAttrChanged"], requireAll = false)
        fun setTextWatcher(
            view: EditText, before: TextViewBindingAdapter.BeforeTextChanged?,
            on: TextViewBindingAdapter.OnTextChanged?, after: TextViewBindingAdapter.AfterTextChanged?,
            textAttrChanged: InverseBindingListener?,
        ) {
            val newValue: TextWatcher? = if (before == null && after == null && on == null && textAttrChanged == null) {
                null
            } else {
                object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                        before?.beforeTextChanged(s, start, count, after)
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        on?.onTextChanged(s, start, before, count)
                        textAttrChanged?.onChange()
                    }

                    override fun afterTextChanged(s: Editable) {
                        after?.afterTextChanged(s)
                    }
                }
            }
            val oldValue = ListenerUtil.trackListener(view, newValue, R.id.textWatcher)
            if (oldValue != null) {
                view.removeTextChangedListener(oldValue)
            }
            if (newValue != null) {
                view.addTextChangedListener(newValue)
            }
        }
    }

}
