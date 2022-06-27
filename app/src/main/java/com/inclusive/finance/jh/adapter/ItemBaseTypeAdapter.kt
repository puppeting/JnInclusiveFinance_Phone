package com.inclusive.finance.jh.adapter

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.bigkoo.pickerview.view.TimePickerView
import com.blankj.utilcode.util.*
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.flyco.roundview.RoundLinearLayout
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.BR
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.base.permissionLocationWithPermissionCheck
import com.inclusive.finance.jh.bean.BaseListBean
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.PicBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.*
import com.inclusive.finance.jh.glide.GlideCacheEngine
import com.inclusive.finance.jh.glide.GlideEngine
import com.inclusive.finance.jh.interfaces.OnRichLayoutChange
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.utils.EditTextRegex
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.widget.ClearPwdEditText
import com.inclusive.finance.jh.widget.aaInfographicsLib.aaChartCreator.*
import com.inclusive.finance.jh.widget.aaInfographicsLib.aaOptionsModel.AALabels
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.style.PictureParameterStyle
import com.tencent.smtt.sdk.WebView
import com.umeng.umcrash.UMCrash
import org.jetbrains.anko.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ItemBaseTypeAdapter<T : BaseTypeBean>(var fragment: MyBaseFragment) :
    BaseMultiItemQuickAdapter<T, BaseViewHolder>(arrayListOf<T>()) {
    var onRichLayoutChangeListener: OnRichLayoutChange? = null
    var dialogPop: Dialog? = null //下拉窗是在dialog中使用
    var parentFrom: Int = 0 //表示在何处使用的adapter，0列表中，1pop中 暂时只为嵌套列表按钮使用
    var subscribe: ((adapter: ItemBaseTypeAdapter<BaseTypeBean>, data: ArrayList<BaseTypeBean>, rootView: View) -> Unit?)? = null
    var keyId: String? = "" //列表内按钮和弹出窗时使用
    var type: String? = "" //图片上传时需要赋值使用
    var textListItemConfig: (parentItem: T?, parentPosition: Int, sonItem: JsonObject?, sonPosition: Int) -> ItemBaseListCardAdapter.TextListItemConfig = { parentItem, parentPosition, sonItem, sonPosition ->
        ItemBaseListCardAdapter.TextListItemConfig()
    }

    init {
        addItemType(BaseTypeBean.TYPE_1, R.layout.layout_number_title)
        addItemType(BaseTypeBean.TYPE_2, R.layout.item_k_v_edit_text)
        addItemType(BaseTypeBean.TYPE_3, R.layout.item_k_v_down)
        addItemType(BaseTypeBean.TYPE_4, R.layout.item_base_list_card_list)
        addItemType(BaseTypeBean.TYPE_5, R.layout.item_k_v_single_pic)
        addItemType(BaseTypeBean.TYPE_6, R.layout.item_k_v_multiple_pic)
        addItemType(BaseTypeBean.TYPE_7, R.layout.item_k_v_down)
        addItemType(BaseTypeBean.TYPE_8, R.layout.item_k_v_down)
        addItemType(BaseTypeBean.TYPE_9, R.layout.item_k_v_rich_text)
        addItemType(BaseTypeBean.TYPE_10, R.layout.item_k_v_single_pic)
        addItemType(BaseTypeBean.TYPE_11, R.layout.item_k_v_single_pic)
        addItemType(BaseTypeBean.TYPE_12, R.layout.item_k_v_contract)
        addItemType(BaseTypeBean.TYPE_13, R.layout.item_k_v_mark)
        addItemType(BaseTypeBean.TYPE_14, R.layout.item_k_v_assess)
        addItemType(BaseTypeBean.TYPE_15, R.layout.item_k_v_edit_text)
        addItemType(BaseTypeBean.TYPE_16, R.layout.item_k_v_text_horizontal)
        addItemType(BaseTypeBean.TYPE_17, R.layout.item_k_v_down)
        addItemType(BaseTypeBean.TYPE_18, R.layout.item_k_v_chart)
        addItemType(BaseTypeBean.TYPE_19, R.layout.item_k_v_location)
        addItemType(BaseTypeBean.TYPE_20, R.layout.item_k_v_text)
        addItemType(BaseTypeBean.TYPE_21, R.layout.item_k_v_down)
        addItemType(BaseTypeBean.TYPE_22, R.layout.item_k_v_down)
        addItemType(BaseTypeBean.TYPE_23, R.layout.item_k_v_down)
        addItemType(BaseTypeBean.TYPE_24, R.layout.item_k_v_down)
        addItemType(BaseTypeBean.TYPE_25, R.layout.item_k_v_down_edit)
        addItemType(BaseTypeBean.TYPE_26, R.layout.item_k_v_edit_add)
        addItemType(BaseTypeBean.TYPE_27, R.layout.item_k_v_product_allocation)
        addItemType(BaseTypeBean.TYPE_28, R.layout.item_k_v_edit_text_vertical)
        addItemType(BaseTypeBean.TYPE_29, R.layout.layout_text_title)

//        setGridSpanSizeLookup { _, _, position ->
//            if (data[position].visibility) data[position].spanSize else 0
//        }
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            BaseTypeBean.TYPE_1 -> {
                BaseViewHolder(LayoutNumberTitleBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_2 -> {
                BaseViewHolder(ItemKVEditTextBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_3 -> {
                BaseViewHolder(ItemKVDownBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_4 -> {
                BaseViewHolder(ItemBaseListCardListBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_5 -> {
                BaseViewHolder(ItemKVSinglePicBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_6 -> {
                BaseViewHolder(ItemKVMultiplePicBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_7 -> {
                BaseViewHolder(ItemKVDownBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_8 -> {
                BaseViewHolder(ItemKVDownBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_9 -> {
                BaseViewHolder(ItemKVRichTextBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_10 -> {
                BaseViewHolder(ItemKVSinglePicBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_11 -> {
                BaseViewHolder(ItemKVSinglePicBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_12 -> {
                BaseViewHolder(ItemKVContractBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_13 -> {
                BaseViewHolder(ItemKVMarkBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_14 -> {
                BaseViewHolder(ItemKVAssessBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_15 -> {
                BaseViewHolder(ItemKVEditTextBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_16 -> {
                BaseViewHolder(ItemKVTextHorizontalBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_17 -> {
                BaseViewHolder(ItemKVDownBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_18 -> {
                BaseViewHolder(ItemKVChartBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_19 -> {
                BaseViewHolder(ItemKVLocationBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_20 -> {
                BaseViewHolder(ItemKVTextBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_21 -> {
                BaseViewHolder(ItemKVDownBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_22 -> {
                BaseViewHolder(ItemKVDownBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_23 -> {
                BaseViewHolder(ItemKVDownBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_24 -> {
                BaseViewHolder(ItemKVDownBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_25 -> {
                BaseViewHolder(ItemKVDownEditBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_26 -> {
                BaseViewHolder(ItemKVEditAddBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_27 -> {
                BaseViewHolder(ItemKVProductAllocationBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_28 -> {
                BaseViewHolder(ItemKVEditTextVerticalBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            BaseTypeBean.TYPE_29 -> {
                BaseViewHolder(LayoutTextTitleBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
            else -> {
                BaseViewHolder(ItemKVEditTextBinding.inflate(LayoutInflater.from(context), parent, false).root)
            }
        }

    }

    override fun convert(holder: BaseViewHolder, item: T) {
        val layoutParams = holder.itemView.layoutParams
        if (item.visibility) {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT // 这里注意使用自己布局的根布局类型
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT // 这里注意使用自己布局的根布局类型
            holder.itemView.visibility = View.VISIBLE
        } else {
            holder.itemView.visibility = View.GONE
            layoutParams.height = 0
            layoutParams.width = 0
        }
        holder.itemView.layoutParams = layoutParams
        when (holder.itemViewType) {
            BaseTypeBean.TYPE_1 -> {
                DataBindingUtil.getBinding<LayoutNumberTitleBinding>(holder.itemView)?.apply {
                    data = item
                    this.holder = holder
                    promptInformation.setOnClickListener {
                        if (dialogPop == null) {
                            InformationPop(context, item.promptInformation).showPopupWindow(it)
                        } else {
                            InformationPop(dialogPop, item.promptInformation).showPopupWindow(it)
                        }
                    }
                }
            }
            BaseTypeBean.TYPE_2 -> {
                DataBindingUtil.getBinding<ItemKVEditTextBinding>(holder.itemView)?.apply {
                    tvValue.inputType = item.inputType //必须在第一行设置，不然会出问题，原因不详
                    data = item
                    this.holder = holder
                    this.recyclerView = this@ItemBaseTypeAdapter.recyclerView
//                    tvValue.isEnabled = item.editable
                    EditTextRegex.textRegex(errorText, tvValue, item.regex, item.regexErrorMsg)
                    promptInformation.setOnClickListener {
                        if (dialogPop == null) {
                            InformationPop(context, item.promptInformation).showPopupWindow(it)
                        } else {
                            InformationPop(dialogPop, item.promptInformation).showPopupWindow(it)
                        }
                    }
                    if (!item.editable) {
                        tvValue.isCursorVisible = false
                        tvValue.isFocusable = false
                        tvValue.isFocusableInTouchMode = false
                        tvValue.setOnClickListener { root.performClick() }
                    } else {
                        tvValue.isCursorVisible = true
                        tvValue.isFocusable = true
                        tvValue.isFocusableInTouchMode = true
                        tvValue.setOnClickListener(null)
                    }
                    //                    tvValue.setOnClickListener {
                    //                        EditPop(context, item.inputType
                    //                            ?: 0, tvKey.text.toString(), it as TextView, item.regex, item.regexErrorMsg).showPopupWindow()
                    //                    }
                }
            }
            BaseTypeBean.TYPE_3 -> {
                DataBindingUtil.getBinding<ItemKVDownBinding>(holder.itemView)?.apply {
                    data = item
                    this.recyclerView = this@ItemBaseTypeAdapter.recyclerView
                    if (item.valueName.isNotEmpty()) {
                        val list = arrayListOf<String>()
                        val split = item.valueName.split(",")
                        split.forEach { value ->
                            item.enums12?.forEach continuing@{
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
                        tvValue.text = item.valueName
                    }
                    promptInformation.setOnClickListener {
                        if (dialogPop == null) {
                            InformationPop(context, item.promptInformation).showPopupWindow(it)
                        } else {
                            InformationPop(dialogPop, item.promptInformation).showPopupWindow(it)
                        }
                    }
                    tvValue.setOnClickListener {
                        if (dialogPop == null) {
                            DownPop(context, bean = item, item.enums12, isSingleChecked = item.isSingleChecked, checkedTextView = tvValue).showPopupWindow(tvValue)
                        } else {
                            DownPop(dialogPop, bean = item, item.enums12, isSingleChecked = item.isSingleChecked, checkedTextView = tvValue).showPopupWindow(tvValue)
                        }
                    }
                }
            }
            BaseTypeBean.TYPE_4 -> {
                DataBindingUtil.getBinding<ItemBaseListCardListBinding>(holder.itemView)?.apply {
//                    mRecyclerView.layoutParams.height = SizeUtils.dp2px(32f) * (item.listBean?.list?.size
//                        ?: 0)
                    if (!item.editable) {
                        if (item.listBean?.actionTypes?.contains("4") == true) {
                            if (item.listBean?.actionTypes?.length ?: 0 > 1) {
                                item.listBean?.actionTypes = "3,4"
                            }
                        } else if (item.listBean?.actionTypes?.length ?: 0 > 0) {
                            item.listBean?.actionTypes = "3"
                        }
                    }
                    val adapter = ItemBaseListCardAdapter<JsonObject>(fragment)
                    adapter.keyId = keyId //列表内按钮和弹出窗时使用
                    adapter.subscribe = subscribe
                    mRecyclerView.adapter = adapter
                    adapter.setListData(data = item, list = item.listBean?.list)
                    adapter.textListItemConfigFun = { sonItem, sonPosition ->
                        textListItemConfig.invoke(item, holder.bindingAdapterPosition, sonItem, sonPosition)
                    }
                    if (parentFrom != 0) {
                        editLay.removeAllViews()
                        val actionTypes = item.listBean?.actionTypes?.split(",")
                        actionTypes?.forEach {
                            if (it == "0") {
                                val chip: View = DataBindingUtil.inflate<ViewChipBinding>(LayoutInflater.from(context), R.layout.view_chip, null, false)
                                    .apply {
                                        data = "新增"
                                        chip.setOnClickListener {
                                            newBtnClick(item)
                                        }
                                    }.root
                                editLay.addView(chip)
                            }
                            //                    iniActionButton(item, editLay)
                        }
                    }


                }
            }
            BaseTypeBean.TYPE_5 -> {
                DataBindingUtil.getBinding<ItemKVSinglePicBinding>(holder.itemView)?.apply {
                    data = item
                    this.recyclerView = this@ItemBaseTypeAdapter.recyclerView
                    imgValue.setOnClickListener {
                        selectPic(item) {
                            notifyItemChanged(holder.adapterPosition)
                        }
                    }
                }
            }
            BaseTypeBean.TYPE_6 -> {
                DataBindingUtil.getBinding<ItemKVMultiplePicBinding>(holder.itemView)?.apply {
                    data = item
                    this.recyclerView = this@ItemBaseTypeAdapter.recyclerView
                    val adapter = ItemPicAdapter<PicBean>()
                    mRecyclerView.adapter = adapter
                    mRecyclerView.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.HORIZONTAL)
                    val firstOrNull = item.picList?.firstOrNull { it.filePath.isNullOrEmpty() }
                    if (firstOrNull != null) item.picList?.remove(firstOrNull) //移除一张空的
                    adapter.setNewInstance(item.picList)
                    adapter.setOnItemClickListener { _, _, position ->
                        selectPic(item, position = position, seeOnly = true)
                    }
                    mRecyclerView.visibility = if (item.picList?.size ?: 0 > 0) View.VISIBLE else View.GONE //                    if (item.picList?.size ?: 0 > 0) {
                    //                        imgValue.setImageURI(SZWUtils.getPicUrl(item.picList?.get(0)?.filePath))
                    //                        imgValue.setOnClickListener {
                    //                            selectPic(item, seeOnly = true)
                    //                        }
                    //                    }
                    btEdit.text = if (item.editable) "编辑" else "查看"
                    btEdit.setOnClickListener {
                        MultiplePicEditPop(context, this@ItemBaseTypeAdapter, item, seeOnly = !item.editable) {
                            notifyItemChanged(holder.adapterPosition)
                        }.show(fragment.childFragmentManager, "adapter")

                    }
                }
            }
            BaseTypeBean.TYPE_7 -> {
                DataBindingUtil.getBinding<ItemKVDownBinding>(holder.itemView)?.apply {
                    data = item //时间选择器
                    tvValue.text = item.valueName
                    tvValue.setOnClickListener {
                        var formatter =   SimpleDateFormat("yyyy-MM-dd");
                        var selectedDate = Calendar.getInstance();//系统当前时间

//                        try {
//                            var start1 = "2020-02-28";//格式必须与formatter的格式⼀致
//                            var date = formatter.parse(start1);
//                            selectedDate.setTime(date);//指定控件初始值显⽰哪⼀天
//                        }catch ( e:Exception){
//                        }
//                        var m=PickerOptions(PickerOptions.TYPE_PICKER_OPTIONS)
//                        m.startDate=startDate
//                        m.endDate=endDate
                        if(item.regex!=""&&item.regex!=null){
                            var ms1=item.regex?.split(" ")
                            var ms2=item.regexErrorMsg?.split(" ")

                            var ms=ms1?.get(0)?.split(",")
                        var r1=ms?.get(0).toString().split("-")
                            var r2=ms2?.get(0).toString().split("-")

                            var startDate = Calendar.getInstance();//控件起始时间
                            startDate.set(r1.get(0).toInt(), r1.get(1).toInt()-1, r1.get(2).toInt());//
                            var endDate = Calendar.getInstance();//控件截⽌时间
                            endDate.set(r2.get(0).toInt(), r2.get(1).toInt()-1, r2.get(2).toInt());//
                            val pvTime: TimePickerView = TimePickerBuilder(context) { date, _ ->
                                item.valueName = TimeUtils.date2String(date, SimpleDateFormat("yyyy-MM-dd", Locale.CHINA))
                                tvValue.text = item.valueName
                            }.setRangDate(startDate,endDate).isDialog(true) .build()


                            pvTime.dialog.window?.setGravity(Gravity.BOTTOM)
                            pvTime.dialog.window?.setWindowAnimations(R.style.picker_view_slide_anim)
                            pvTime.setOnDismissListener { tvValue.isChecked = false }
                            tvValue.isChecked = true

                            pvTime.setDate(SZWUtils.getCalender(item.valueName)) //注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
                            // pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
                            pvTime.show()

                        }else{
                            val pvTime: TimePickerView = TimePickerBuilder(context) { date, _ ->
                                item.valueName = TimeUtils.date2String(date, SimpleDateFormat("yyyy-MM-dd", Locale.CHINA))
                                tvValue.text = item.valueName
                            }.isDialog(true) .build()
                            pvTime.dialog.window?.setGravity(Gravity.BOTTOM)
                            pvTime.dialog.window?.setWindowAnimations(R.style.picker_view_slide_anim)
                            pvTime.setOnDismissListener { tvValue.isChecked = false }
                            tvValue.isChecked = true

                            pvTime.setDate(SZWUtils.getCalender(item.valueName)) //注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
                            // pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
                            pvTime.show()
                        }

                    }
                }
            }
            BaseTypeBean.TYPE_8,
            BaseTypeBean.TYPE_21,
            BaseTypeBean.TYPE_23,
            BaseTypeBean.TYPE_24,
            -> {
                DataBindingUtil.getBinding<ItemKVDownBinding>(holder.itemView)?.apply {
                    if (item.valueName.contains("##")) {
                        val split = item.valueName.split("##", limit = 2)
                        tvValue.text = split[1]
                    } else {
                        tvValue.text = item.valueName
                    }

                    data = item

                    //行业分类·
                    tvValue.setOnClickListener {
                        HangYeFenLeiPop(context, item, tvValue, holder.itemViewType).show(fragment.childFragmentManager, "adapter")
                    }

                }
            }
            BaseTypeBean.TYPE_9 -> {
                DataBindingUtil.getBinding<ItemKVRichTextBinding>(holder.itemView)?.apply {
                    data = item
                    if ((holder.itemView as LinearLayout).childCount > 1)

                        (holder.itemView as LinearLayout).removeAllViews()
                    val horizontalScrollView = HorizontalScrollView(context)
                    val radioGroup = RadioGroup(context)
                    horizontalScrollView.addView(radioGroup)
                    radioGroup.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, SizeUtils.dp2px(35f))
                    radioGroup.background = ContextCompat.getDrawable(context, R.drawable.shape_radiogroup)
                    radioGroup.orientation = LinearLayout.HORIZONTAL
                    (holder.itemView as LinearLayout).addView(horizontalScrollView, 0)
                    (holder.itemView as LinearLayout).addView(RoundLinearLayout(context).apply {
                        setPadding(SizeUtils.dp2px(8f), SizeUtils.dp2px(1f), SizeUtils.dp2px(8f), SizeUtils.dp2px(1f))
                        delegate.strokeColor = ContextCompat.getColor(context, R.color.line)
                        delegate.backgroundColor = ContextCompat.getColor(context, R.color.white)
                        delegate.strokeWidth = 1
                        delegate.cornerRadius_BL = SizeUtils.dp2px(8f)
                        delegate.cornerRadius_BR = SizeUtils.dp2px(8f)
                        addView(WebView(context).apply { loadDataWithBaseURL(null, item.richTextList?.firstOrNull { it.checked }?.valueName?.trimIndent(), "text/html", "utf-8", null) })
                    })
                    item.richTextList?.forEachIndexed { index, richBean ->
                        if (richBean.absKey()
                                .isNotEmpty()) radioGroup.addView((DataBindingUtil.inflate<LayoutRichtextRadiobuttonBinding>(LayoutInflater.from(context), R.layout.layout_richtext_radiobutton, null, false).root as RadioButton).apply {
                            text = richBean.absKey()
                            isChecked = richBean.checked
                        })
                    }
                    radioGroup.setOnCheckedChangeListener { group, checkedId ->
                        val indexOfChild = group.indexOfChild(group.findViewById(checkedId))
                        val richText = item.richTextList?.get(indexOfChild)
                        onRichLayoutChangeListener?.onClick(richText?.absKey() ?: "")
                    }
                }
            }
            BaseTypeBean.TYPE_10 -> {
                DataBindingUtil.getBinding<ItemKVSinglePicBinding>(holder.itemView)?.apply {
                    data = item
                    if (item.editable) imgValue.setOnClickListener {
                        QianZiBanPop(context) { code, path ->
                            upPicData(arrayListOf(FileUtils.getFileByPath(path)), item) {
                                notifyItemChanged(holder.adapterPosition)
                            }
                        }.show(fragment.childFragmentManager, "adapter")
                    }

                }
            }
            BaseTypeBean.TYPE_11 -> {
                DataBindingUtil.getBinding<ItemKVSinglePicBinding>(holder.itemView)?.apply {
                    data = item
                    imgValue.setOnClickListener {
                        selectPic(item) {
                            notifyItemChanged(holder.adapterPosition)
                        }
                    }

                }
            }
            BaseTypeBean.TYPE_12 -> {
                DataBindingUtil.getBinding<ItemKVContractBinding>(holder.itemView)?.apply {
                    data = item
                    checkbox.isChecked = item.checked
                    checkbox.isClickable = item.editable
                    btContract.setOnClickListener { // TODO: 2020/11/5 跳网页
                    }
                }
            }
            BaseTypeBean.TYPE_13 -> {
                DataBindingUtil.getBinding<ItemKVMarkBinding>(holder.itemView)?.apply {
                    data = item
                    if (constraintLay.childCount > 1) constraintLay.removeViews(1, constraintLay.childCount - 1)
                    item.valueName.split(",").forEachIndexed { index, s ->
                        val imageView = ImageView(context)
                        imageView.layoutParams = ViewGroup.LayoutParams(SizeUtils.dp2px(45f), SizeUtils.dp2px(45f))
                        imageView.id = View.generateViewId()
                        SZWUtils.loadPhotoImg(context, s, imageView)
                        constraintLay.addView(imageView)
                        flow.addView(imageView)
                    }
                }

            }
            BaseTypeBean.TYPE_14 -> {
                DataBindingUtil.getBinding<ItemKVAssessBinding>(holder.itemView)?.apply {
                    data = item

                }

            }
            BaseTypeBean.TYPE_15 -> {
                DataBindingUtil.getBinding<ItemKVEditTextBinding>(holder.itemView)?.apply {
                    data = item
                }
            }
            BaseTypeBean.TYPE_16 -> {
                DataBindingUtil.getBinding<ItemKVTextHorizontalBinding>(holder.itemView)?.apply {
                        data = item
                        tvValue.setOnClickListener {
                            EditPop(context, item.inputType, tvKey.text.toString(), it as TextView, item.regex, item.regexErrorMsg).showPopupWindow()
                        }
                    }
            }
            BaseTypeBean.TYPE_17 -> {
                DataBindingUtil.getBinding<ItemKVDownBinding>(holder.itemView)?.apply {
                    data = item //时间选择器
                    tvValue.text = item.valueName //条件选择器
                    //条件选择器
                    tvValue.setOnClickListener {
                        val pvOptions: OptionsPickerView<BaseTypeBean.Enum12> = OptionsPickerBuilder(context) { options1, options2, options3, v -> //返回的分别是三个级别的选中位置
                            val tx: String = "${
                                item.options1Items?.get(options1)?.pickerViewText
                            }-${
                                item.options2Items?.get(options1)?.get(options2)?.pickerViewText
                            }${
                                if (!item.options3Items.isNullOrEmpty()) {
                                    "-" + item.options3Items?.get(options1)?.get(options2)
                                        ?.get(options3)?.pickerViewText
                                } else ""
                            }"
                            item.optionsPosition?.clear()
                            item.optionsPosition?.add(options1)
                            item.optionsPosition?.add(options2)
                            item.optionsPosition?.add(options3)
                            tvValue.text = tx
                            item.valueName = tx
                        }.isDialog(true).build()
                        pvOptions.dialog.window?.setGravity(Gravity.BOTTOM)
                        pvOptions.dialog.window?.setWindowAnimations(R.style.picker_view_slide_anim)
                        pvOptions.setOnDismissListener { tvValue.isChecked = false }
                        tvValue.isChecked = true
                        pvOptions.setPicker(item.options1Items, item.options2Items, item.options3Items)
                        if (!item.optionsPosition.isNullOrEmpty()) {
                            pvOptions.setSelectOptions(
                                item.optionsPosition?.get(0)
                                    ?: 0, if (item.optionsPosition?.size ?: 0 > 1) item.optionsPosition?.get(1)
                                    ?: 0 else 0, if (item.optionsPosition?.size ?: 0 > 2) item.optionsPosition?.get(2)
                                    ?: 0 else 0
                            )
                        }
                        pvOptions.show()
                    }
                }
            }

            BaseTypeBean.TYPE_18 -> {
                DataBindingUtil.getBinding<ItemKVChartBinding>(holder.itemView)?.apply {
                    data = item
                    initLine(this, item)
                }
            }
            BaseTypeBean.TYPE_19 -> {
                DataBindingUtil.getBinding<ItemKVLocationBinding>(holder.itemView)?.apply {
                    data = item
                    val model = ViewModelProvider(context as BaseActivity).get(ApplyModel::class.java)
                    viewModel = model
                    var split: List<String>? = arrayListOf()
                    if (item.valueName.contains("||")) {
                        split = item.valueName.split("||", limit = 2)
                        item.locationValueName = split[0]
                    } else {
                        item.locationValueName = item.valueName
                    } //                    tvValue.isEnabled = item.editable

                    if (!item.editable) {
                        tvValue.isCursorVisible = false
                        tvValue.isFocusable = false
                        tvValue.isFocusableInTouchMode = false
                        tvValue.setOnClickListener {
                            if (AppUtils.isAppInstalled("com.baidu.BaiduMap")) {
                                if (split?.size ?: 0 > 1) {
                                    val uri = Uri.parse("baidumap://map/direction?destination=latlng:" + split?.get(1) + "|name:" + split?.get(0) + "&mode=driving")
                                    //val uri = Uri.parse("baidumap://map/direction?destination=" + item.valueName + "&mode=driving")
                                    ActivityUtils.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                } else { //                                    SZWUtils.showSnakeBarMsg("暂未获取到位置信息")
                                    val uri = Uri.parse("baidumap://map/direction?destination=" + item.valueName + "&mode=driving")
                                    ActivityUtils.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                }
                            } else {
                                SZWUtils.showSnakeBarMsg("您尚未安装百度地图")
                            }

                        } //                        tvValue.keyListener = null
                    }
                    map.visibility = if (item.locationAble == true) View.VISIBLE else View.GONE
                    if (model.seeOnly != true) map.setOnClickListener {
                        (fragment.context as BaseActivity).permissionLocationWithPermissionCheck {
                            try {
                                RxBus.get().register(this@ItemBaseTypeAdapter)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            /**
                             * 腾讯选点  已弃用
                             * */ //                            ARouter.getInstance().build("/com/MyWebActivity")
                            //                                //                        .withString(Intent_WebUrl, "http://192.168.3.32:8081/onlinePreview?url=http%3A%2F%2F212.129.130.163%3A3000%2Ftscepdf.pdf&officePreviewType=pdf")
                            //                                //                        .withString(Intent_WebUrl, "http://debugtbs.qq.com")
                            //                                //                            .withString(MyWebActivity.Intent_WebUrl, "https://mapapi.qq.com/web/mapComponents/locationPicker/v/index.html?search=1&type=0&backurl=http%3A%2F%2F3gimg.qq.com%2Flightmap%2Fcomponents%2FlocationPicker2%2Fback.html&key=RUNBZ-DNHRP-DOIDM-VSB2Q-RE4M2-CMBQG&referer= ")
                            //                                .withString(MyWebActivity.Intent_WebUrl, "https://apis.map.qq.com/tools/locpicker?search=0&type=0&backurl=https://3gimg.qq.com/lightmap/components/locationPicker2/back.html?position=${holder.adapterPosition}&key=RUNBZ-DNHRP-DOIDM-VSB2Q-RE4M2-CMBQG&referer= &mapdraggable=0")
                            //                                .withBoolean("isPDF", false)
                            //                                .withString(MyWebActivity.Intent_WebTitle, "地图选址").navigation()
                            /**
                             * 百度选点
                             * */
                            ARouter.getInstance().build("/com/MyMapActivity")
                                .withInt("position", holder.adapterPosition).navigation()
                        }
                    }
                }
            }
            BaseTypeBean.TYPE_20 -> {
                DataBindingUtil.getBinding<ItemKVTextBinding>(holder.itemView)?.apply {
                    data = item
                    this.holder = holder
                    this.recyclerView = this@ItemBaseTypeAdapter.recyclerView
                    tvValue.setOnClickListener {
                        DarkSearchPop(context, it as TextView, item, BaseTypeBean.TYPE_20).show(fragment.childFragmentManager, "adapter")
                    }
                }
            }


            BaseTypeBean.TYPE_22 -> {
                DataBindingUtil.getBinding<ItemKVDownBinding>(holder.itemView)?.apply {
                    if (item.valueName.contains("##")) {
                        val split = item.valueName.split("##", limit = 2)
                        tvValue.text = split[1]
                    } else {
                        tvValue.text = item.valueName
                    }

                    data = item
                    this.recyclerView = this@ItemBaseTypeAdapter.recyclerView
                    tvValue.setOnClickListener {
                        DarkSearchPop(context, it as TextView, item, BaseTypeBean.TYPE_22).show(fragment.childFragmentManager, "adapter")
                    }
                }
            }
            BaseTypeBean.TYPE_25 -> {
                DataBindingUtil.getBinding<ItemKVDownEditBinding>(holder.itemView)?.apply {
                    data = item
                    tvValue.inputType = item.inputType //必须在第一行设置，不然会出问题，原因不详
                    data = item
                    this.recyclerView = this@ItemBaseTypeAdapter.recyclerView
                    tvValue.isEnabled = item.editable
                    EditTextRegex.textRegex(null, tvValue, item.regex, item.regexErrorMsg)

                    this.recyclerView = this@ItemBaseTypeAdapter.recyclerView
                    if (item.valueName.isNotEmpty()) {
                        val list = arrayListOf<String>()
                        val split = item.valueName.split(",")
                        split.forEach { value ->
                            item.enums12?.forEach continuing@{
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
                        downValue.text = list.joinToString(",")
                    } else {
                        downValue.text = item.valueName
                    }
                    downValue.setOnClickListener {
                        if (dialogPop == null) {
                            DownPop(context, bean = item, item.enums12, isSingleChecked = item.isSingleChecked, checkedTextView = downValue).showPopupWindow(downValue)
                        } else {
                            DownPop(dialogPop, bean = item, item.enums12, isSingleChecked = item.isSingleChecked, checkedTextView = downValue).showPopupWindow(downValue)
                        }
                    }
                    btChange.setOnClickListener {
                        tvValue.visibility = if (tvValue.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                        downValue.visibility = if (downValue.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                    }
                }
            }
            BaseTypeBean.TYPE_26 -> {
                DataBindingUtil.getBinding<ItemKVEditAddBinding>(holder.itemView)?.apply {
                    data = item
                    this.recyclerView = this@ItemBaseTypeAdapter.recyclerView
                    if (constraintLay.childCount > 3) constraintLay.removeViews(3, constraintLay.childCount - 3)
                    val viewBean = arrayListOf<String>()
                    val split = item.valueName.split(",")
                    if (split.isNullOrEmpty()) {
                        viewBean.add("")
                    } else {
                        viewBean.addAll(0, split)
                    }
                    viewBean.forEachIndexed { index, str ->
                        val view = DataBindingUtil.inflate<ViewEditAddBinding>(LayoutInflater.from(context), R.layout.view_edit_add, null, false)
                            .apply {
                                data = item
                                this.recyclerView = this@ItemBaseTypeAdapter.recyclerView
                            }.root
                        view.id = View.generateViewId()
                        ((view as RoundLinearLayout).getChildAt(0) as EditText).setText(str)
                        (view.getChildAt(0) as EditText).doAfterTextChanged {
                            resetValueData(item)
                        }
                        if (index == 0) {
                            (view.getChildAt(1) as AppCompatImageView).visibility = View.INVISIBLE
                        } else {
                            (view.getChildAt(1) as AppCompatImageView).setOnClickListener {
                                constraintLay.removeView(view)
                                flow.removeView(view)
                                resetValueData(item)
                                holder.itemView.requestFocus()
                                holder.itemView.clearFocus()
                                KeyboardUtils.hideSoftInput(holder.itemView)
                            }
                        }
                        constraintLay.addView(view)
                        flow.addView(view)
                        if (index > 0 && str.isEmpty()) (view.getChildAt(0) as EditText).requestFocus()
                    }
                    btAdd.setOnClickListener {
                        if ((!item.valueName.split(",").lastOrNull().isNullOrEmpty())) {
                            item.valueName = item.valueName.plus(",")
                            notifyItemChanged(holder.adapterPosition)
                        }
                    }
                }
            }
            BaseTypeBean.TYPE_27 -> {
                DataBindingUtil.getBinding<ItemKVProductAllocationBinding>(holder.itemView)?.apply {
                        data = item
                        if (item.valueName.isNotEmpty()) {
                            val list = arrayListOf<String>()
                            val split = item.valueName.split(",")
                            split.forEach { value ->
                                item.enums12?.forEach continuing@{
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
                            tvValue.text = item.valueName
                        }
                        tvValue.setOnClickListener {
                            if (dialogPop == null) {
                                DownPop(context, bean = item, item.enums12, isSingleChecked = item.isSingleChecked, checkedTextView = tvValue).showPopupWindow(tvValue)
                            } else {
                                DownPop(dialogPop, bean = item, item.enums12, isSingleChecked = item.isSingleChecked, checkedTextView = tvValue).showPopupWindow(tvValue)
                            }
                        }

                        if (item.editable) {
                            item.addOnPropertyChangedCallback(object :
                                Observable.OnPropertyChangedCallback() {
                                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                                    if (propertyId == BR.valueName) {
                                        if (item.valueName != "1") {
                                            btAdd.visibility = View.GONE
                                            mRecyclerView.visibility = View.GONE
                                        } else {
                                            btAdd.visibility = View.VISIBLE
                                            mRecyclerView.visibility = View.VISIBLE
                                        }
                                    }
                                }
                            })
                            if (item.valueName != "1") {
                                btAdd.visibility = View.GONE
                                mRecyclerView.visibility = View.GONE
                            } else {
                                btAdd.visibility = View.VISIBLE
                                mRecyclerView.visibility = View.VISIBLE
                            }
                        } else {
                            btAdd.visibility = View.GONE
                        }

                        this.recyclerView = this@ItemBaseTypeAdapter.recyclerView
                        val itemProductAllocationAdapter = ItemProductAllocationAdapter<JsonObject>(dialogPop, item.editable)
                        mRecyclerView.adapter = itemProductAllocationAdapter
                        if (item.listBean == null) {
                            item.listBean = BaseListBean(false, "", arrayListOf(), arrayListOf())
                            item.listBean?.list?.add(JsonObject())
                        }
                        itemProductAllocationAdapter.setNewInstance(item.listBean?.list)
                        btAdd.setOnClickListener {
                            val lastOrNull = item.listBean?.list?.lastOrNull()
                            if (lastOrNull != null && SZWUtils.getJsonObjectString(lastOrNull, "productValue")
                                    .isNotEmpty() && SZWUtils.getJsonObjectString(lastOrNull, "priceValue")
                                    .isNotEmpty()) {
                                val deepCopy = item.listBean?.list?.get(0)?.deepCopy()
                                deepCopy?.addProperty("productValue", "")
                                deepCopy?.addProperty("priceValue", "")
                                item.listBean?.list?.add(deepCopy ?: JsonObject())
                            } else if (lastOrNull != null) {
                                val findViewByPosition = mRecyclerView.layoutManager?.findViewByPosition(
                                    item.listBean?.list?.indexOf(lastOrNull) ?: 0
                                )
                                findViewByPosition?.startAnimation(ClearPwdEditText.shakeAnimation(3))
                            }
                            notifyItemChanged(holder.adapterPosition)
                        }
                    }
            }
            BaseTypeBean.TYPE_28 -> {
                DataBindingUtil.getBinding<ItemKVEditTextVerticalBinding>(holder.itemView)?.apply {
                    tvValue.inputType = item.inputType //必须在第一行设置，不然会出问题，原因不详
                    data = item
                    this.holder = holder
                    this.recyclerView = this@ItemBaseTypeAdapter.recyclerView
//                    tvValue.isEnabled = item.editable
                    EditTextRegex.textRegex(errorText, tvValue, item.regex, item.regexErrorMsg)
                    promptInformation.setOnClickListener {
                        if (dialogPop == null) {
                            InformationPop(context, item.promptInformation).showPopupWindow(it)
                        } else {
                            InformationPop(dialogPop, item.promptInformation).showPopupWindow(it)
                        }
                    }
                    if (!item.editable) {
                        tvValue.isCursorVisible = false
                        tvValue.isFocusable = false
                        tvValue.isFocusableInTouchMode = false
                        tvValue.setOnClickListener { root.performClick() }
                    } else {
                        tvValue.isCursorVisible = true
                        tvValue.isFocusable = true
                        tvValue.isFocusableInTouchMode = true
                        tvValue.setOnClickListener(null)
                    }
                    //                    tvValue.setOnClickListener {
                    //                        EditPop(context, item.inputType
                    //                            ?: 0, tvKey.text.toString(), it as TextView, item.regex, item.regexErrorMsg).showPopupWindow()
                    //                    }
                }
            }
            BaseTypeBean.TYPE_29 -> {
                DataBindingUtil.getBinding<LayoutTextTitleBinding>(holder.itemView)?.apply {
                    data = item
                    this.holder = holder
                }
            }
        }
    }
    /**
     * 类型 4 新增点击
     * */
    fun newBtnClick(item: T,  subscribe: ((adapter: ItemBaseTypeAdapter<BaseTypeBean>, data: ArrayList<BaseTypeBean>, rootView: View) -> Unit?)? = null) {
        BaseTypePop(context, fragment, "新增", Urls.url + item.listBean?.getUrl, Urls.url + item.listBean?.saveUrl, null, keyId = keyId, subscribe = subscribe) { adapter, resultStr ->
            fragment.refreshData(130)
        }.show(fragment.childFragmentManager, "adapter")
    }


    /**
     * 类型36 重置数据
     * */
    private fun ItemKVEditAddBinding.resetValueData(item: T) {
        val values = arrayListOf<String>()
        constraintLay.forEach { view ->
            if (view is ViewGroup) {
                view.forEach { editView ->
                    if (editView is EditText) {
                        values.add(editView.text.toString())
                    }
                }
            }

        }
        item.valueName = values.joinToString(",")
    }

    @Deprecated("腾讯地图选点方式，改用百度了")
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = arrayOf(Tag("BackUrl")))
    fun backUrl(url: String) {
        val index = getQueryStr(url, "position").toInt()
        data[index].valueName = getQueryStr(url, "addr") + "||" + getQueryStr(url, "latng")
        notifyItemChanged(index)
        try {
            RxBus.get().unregister(this@ItemBaseTypeAdapter)
        } catch (e: Exception) {
            e.printStackTrace()
        } //        mData.forEachIndexed { index, it ->
        //            if (it.itemType == BaseTypeBean.TYPE_19) {
        //                it.valueName = getQueryStr(url, "name") + getQueryStr(url, "addr") + "||" + getQueryStr(url, "latng")
        //                notifyItemChanged(index)
        //            }
        //        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = arrayOf(Tag("location")))
    fun location(location: HashMap<String, String>) {
        val index = location["position"]?.toInt() ?: 0
        data[index].valueName = "${location["address"]}||${location["latLng"]}"
        notifyItemChanged(index)
        try {
            RxBus.get().unregister(this@ItemBaseTypeAdapter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getQueryStr(url: String, str: String): String {
        val matches = RegexUtils.getMatches("(^|)$str=([^&]*)(&|$)", url)
        return try {
            if (matches.size > 0) {
                EncodeUtils.urlDecode(matches[0].split("=")[1].replace("&", ""))
            } else ""
        } catch (e: Exception) {
            "地址选取错误。请重试"
        }
    }

    /**
     * 初始化图表
     * */
    private fun initLine(binding: ItemKVChartBinding, item: T) {
        val titleList = SZWUtils.getJsonObjectArray(JsonParser.parseString(item.valueName).asJsonObject, "month")
            .toList()
        val chartIndex = SZWUtils.getJsonObjectArray(JsonParser.parseString(item.valueName).asJsonObject, "chartIndex")
            .toList()
        val moneyOut = SZWUtils.getJsonObjectArray(JsonParser.parseString(item.valueName).asJsonObject, "近一年现金流流出")
            .toList()
        val moneyIn = SZWUtils.getJsonObjectArray(JsonParser.parseString(item.valueName).asJsonObject, "近一年现金流流入")
            .toList()
        val unit = SZWUtils.getJsonObjectString(JsonParser.parseString(item.valueName).asJsonObject, "company")

        val prop = when {
            chartIndex.isNullOrEmpty() -> {
                arrayOf(
                    AASeriesElement().name("近一年现金流流出")
                        .data(Array(moneyOut.size) { i -> moneyOut[i].asDouble })
                        .color("#23bf86"), AASeriesElement().name("近一年现金流流入")
                        .data(Array(moneyIn.size) { i -> moneyIn[i].asDouble }).color("#cc6bcc")

                )
            }
            else -> {
                val list = arrayListOf<AASeriesElement>()
                chartIndex.forEachIndexed { index, jsonElement ->
                    val toList = SZWUtils.getJsonObjectArray(JsonParser.parseString(item.valueName).asJsonObject, jsonElement.asString)
                        .toList()
                    list.add(
                        AASeriesElement().name(if (jsonElement.isJsonNull) "" else jsonElement.asString)
                            .data(Array(toList.size) { i -> if (toList[i].isJsonNull) "" else toList[i].asDouble })
                            .color(
                                when (index) {
                                    0 -> "#FDA7A7"
                                    1 -> "#AC89FF"
                                    2 -> "#FDDC61"
                                    3 -> "#00FFCC"
                                    4 -> "#FF33DD"
                                    5 -> "#90FF84"
                                    6 -> "#1976D2"
                                    7 -> "#4DFFEA"
                                    8 -> "#388E3C"
                                    else -> "#FDA7A7"
                                }
                            )
                    )
                }
                list.toTypedArray()
            }
        }
        val aaChartModel = AAChartModel().chartType(AAChartType.Line) //                                    .title("title")
            //                                    .subtitle("subtitle")
            .backgroundColor("#fff").dataLabelsEnabled(true).yAxisGridLineWidth(1f).yAxisTitle("")

            .categories(Array(titleList.size) { i -> titleList[i].asString })
            .series(prop) //        aaChartModel.markerSymbol(AAChartSymbolType.Circle)
        aaChartModel.tooltipValueSuffix = "元"/*图表视图对象调用图表模型对象,绘制最终图形*/
        val aaOptions = AAOptionsConstructor.configureChartOptions(aaChartModel)
        val aaYAxisLabels = AALabels().formatter(
            """
    function () {
        if(Math.abs(this.value)<10000){
            return Math.abs(this.value)+ '（${if (unit == "人") "人" else "元"}）';
        }else {
            return (Math.abs(this.value) / 10000) + '（${if (unit == "人") "万人" else "万元"}）';
        }

    }
                        """.trimIndent()
        )
        aaYAxisLabels.staggerLines = 6
        aaOptions.yAxis?.labels(aaYAxisLabels) //                            aAChartView?.aa_drawChartWithChartModel(aaChartModel);
        binding.aAChartView.aa_drawChartWithChartOptions(aaOptions)
        binding.aAChartView.callBack = object : AAChartView.AAChartViewCallBack {
            override fun chartViewDidFinishLoad(aaChartView: AAChartView) {

            }

            override fun chartViewMoveOverEventMessage(aaChartView: AAChartView, messageModel: AAMoveOverEventMessageModel) {
            }
        }
    }

    /**
     * 图上传/查看
     */
    fun selectPic(item: T, seeOnly: Boolean? = false, position: Int? = 0, listener: (() -> Unit)? = null) {
        (fragment.activity as BaseActivity).permissionCamera(null, 100, false) {
            if (item.editable && seeOnly != true) {
                val pictureSelector = PictureSelector.create(fragment)
                val openSelector = if (item.isGallery) pictureSelector.openGallery(PictureMimeType.ofImage())
                else pictureSelector.openCamera(PictureMimeType.ofImage())

                openSelector.selectionMode(if (item.picCount > 1) PictureConfig.MULTIPLE else PictureConfig.SINGLE)
                openSelector.imageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
                    .maxSelectNum(item.picCount).minSelectNum(1) // 最小选择数量
                    .isSingleDirectReturn(true)
                    .isCamera(true) //                    .isUseCustomCamera(true)//是否使用自定义相机
                    //                    .setButtonFeatures(CustomCameraView.BUTTON_STATE_ONLY_CAPTURE)//自定相机是否单独拍照、录像
                    .isCompress(true) //是否压缩.imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java.loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())
                    .minimumCompressSize(80) // 小于多少kb的图片不压缩
                    //                    .setOutputCameraPath(SZWUtils.createCustomCameraOutPath(context))
                    .loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine()) // 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT) // 设置相册Activity方向，不设置默认使用系统
                    .compressQuality(80).synOrAsy(true)
                    .forResult(object : OnResultCallbackListener<LocalMedia?> {
                        override fun onResult(result: List<LocalMedia?>) {
                            if (ObjectUtils.isEmpty(result) || result.isEmpty()) {
                                return
                            }
                            when {
                                item.picCount == 1 && item.valueHint.contains("身份证") -> {
                                    var picturePath: String? = result[0]?.compressPath
                                    if (picturePath == null) {
                                        picturePath = result[0]?.realPath.toString()
                                    }
                                    val mFile = File(picturePath)
                                    iDCardUpData(item, mFile) {
                                        listener?.invoke()
                                    }
                                }
                                item.picCount == 1 && item.valueHint.contains("营业执照") -> {
                                    var picturePath: String? = result[0]?.compressPath
                                    if (picturePath == null) {
                                        picturePath = result[0]?.realPath.toString()
                                    }
                                    val mFile = File(picturePath)
                                    zhizhaoUpData(item, "" + item.picUrl, mFile) {
                                        listener?.invoke()
                                    }
                                }
                                item.valueHint.contains("人脸识别") -> {
                                    var picturePath: String? = result[0]?.compressPath
                                    if (picturePath == null) {
                                        picturePath = result[0]?.realPath.toString()
                                    }
                                    val mFile = File(picturePath)
                                    faceUpData(item, mFile) {
                                        listener?.invoke()
                                    }
                                }
                                item.valueHint.contains("借据") -> {
                                    var picturePath: String? = result[0]?.compressPath
                                    if (picturePath == null) {
                                        picturePath = result[0]?.realPath.toString()
                                    }
                                    val mFile = File(picturePath)
                                    JJDUpData(item, mFile) {
                                        listener?.invoke()
                                    }
                                }
                                else -> {
                                    val files = ArrayList<File>()
                                    result.forEach {
                                        var picturePath = it?.compressPath ?: ""
                                        if (picturePath.isEmpty()) {
                                            picturePath = it?.realPath.toString()
                                        }
                                        files.add(File(picturePath))
                                    }
                                    upPicData(files, item) {
                                        listener?.invoke()
                                    }
                                }
                            }

                        }

                        override fun onCancel() {
                        }
                    }) //PictureConfig.CHOOSE_REQUEST

            } else {
                val picData = if (item.picUrl.isNullOrEmpty()) null else item.picUrl?.split(",")
                val medias: MutableList<LocalMedia> = ArrayList()
                picData?.forEach {
                    val localMedia = LocalMedia()
                    localMedia.path = SZWUtils.getIntactUrl(it)
                    if (it.isNotEmpty()) medias.add(localMedia)
                }
                if (picData == null && item.picList?.size ?: 0 > 0) {
                    item.picList?.forEach {
                        val localMedia = LocalMedia()
                        localMedia.path = SZWUtils.getIntactUrl(it.filePath)
                        if (!it.filePath.isNullOrEmpty()) medias.add(localMedia)
                    }
                }
                val pictureParameterStyle = PictureParameterStyle() //                            pictureParameterStyle.pictureExternalPreviewGonePreviewDelete = !viewModel.getSeeOnly()
                if (medias.size > 0) PictureSelector.create(fragment)
                    .themeStyle(R.style.picture_default_style)
                    .setPictureStyle(pictureParameterStyle)
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT)
                    .isNotPreviewDownload(true)
                    .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                    .openExternalPreview(position ?: 0, medias)
            }

        }
    }

    /**
     * 身份证识别+上传
     */
    private fun iDCardUpData(item: T, mFile: File, listener: () -> Unit) {
        val isFront = item.valueHint.contains("正面")
        DataCtrlClass.upDataOCR(fragment.context, if (isFront) "front" else "back", mFile, recyclerView) {
            if (it != null) {
                if (it.image_status == "normal") {
                    if (isFront) {
                        data.forEach { bean ->
                            when {
                                bean.valueHint.contains("姓名") -> {
                                    bean.valueName = it.words_result?.姓名?.words ?: ""
                                }
                                bean.valueHint.contains("身份证号") -> {
                                    bean.valueName = it.words_result?.公民身份号码?.words ?: ""
                                }
                                bean.valueHint.contains("地址") -> {
                                    bean.valueName = it.words_result?.住址?.words ?: ""
                                }
                            }
                        }
                    } else {
                        data.forEachIndexed { index, bean ->
                            when {
                                bean.valueHint.contains("开始") -> {

                                    if (ObjectUtils.isNotEmpty(it.words_result?.签发日期?.words)) {
                                        if ("长期" != it.words_result?.签发日期?.words) {
                                            try {
                                                val data = it.words_result?.签发日期?.words
                                                val year = data?.substring(0, 4)
                                                val month = data?.substring(4, 6)
                                                val day = data?.substring(6, 8)
                                                val str = "$year-$month-$day"
                                                bean.valueName = str
                                            } catch (e: Exception) {
                                                UMCrash.generateCustomLog(e, "UmengException")
                                            }
                                        } else {
                                            bean.valueName = ""
                                        }
                                    }
                                    notifyItemChanged(index)
                                }
                                bean.valueHint.contains("截止") -> {
                                    if (ObjectUtils.isNotEmpty(it.words_result?.失效日期?.words)) {
                                        if ("长期" != it.words_result?.失效日期?.words) {
                                            try {
                                                val data = it.words_result?.失效日期?.words
                                                val year = data?.substring(0, 4)
                                                val month = data?.substring(4, 6)
                                                val day = data?.substring(6, 8)
                                                val str = "$year-$month-$day"
                                                bean.valueName = str
                                            } catch (e: Exception) {
                                                UMCrash.generateCustomLog(e, "UmengException")
                                            }
                                        } else {
                                            bean.valueName = "2099-12-31"
                                        }
                                    }
                                    notifyItemChanged(index)
                                }

                            }
                        }
                    }
                    upPicData(arrayListOf(mFile), item) {
                        listener.invoke()
                    }
                } else {
                    SZWUtils.showSnakeBarMsg(recyclerView, if (isFront) "身份证正面证识别失败" else "身份证背面证识别失败")
                }
            }
        }
    }

    /**
     * 借据号+上传
     */
    private fun JJDUpData(item: T, mFile: File, listener: () -> Unit) {
        DataCtrlClass.upDataJJD(fragment.context, mFile, recyclerView) {
            if (it != null) {
                data.forEach { bean ->
                    when {
                        bean.valueHint.contains("借据编号") -> {
                            bean.valueName = it
                        }
                    }
                }
                upPicData(arrayListOf(mFile), item) {
                    listener.invoke()
                }
            }
        }
    }
    /**
     * 营业执照识别+上传
     */
    private fun zhizhaoUpData(item: T, mm: String, mFile: File, listener: () -> Unit) {
//        val isFront = item.valueHint.contains("正面")
        DataCtrlClass.zhiZhaoOCR(fragment.context, mm, mFile, recyclerView) {
            if (it != null) {
//                if (it.image_status == "normal") {
//                    if (isFront) {
                data.forEachIndexed {index, bean ->
                    when {
                        bean.keyName.contains("统一社会信用代码") -> {
                            bean.valueName = it.tyshxydm ?: ""
                        }
                        bean.keyName.contains("名称") -> {
                            bean.valueName = it.dwmc ?: ""
                        }
                        bean.keyName.contains("经营名称") -> {
                            bean.valueName = it.dwmc ?: ""
                        }
                        bean.keyName.trim()=="类型" -> {
                            bean.valueName = it.lx ?: ""
                        }
//                        bean.keyName.contains("商户类型") -> {
//                            bean.valueName = it.lx ?: ""
//                        }
//                        bean.keyName.contains("住所") -> {
//                            bean.valueName = it.dz ?: ""
//                        }
                        bean.keyName.contains("经营场所") -> {
                            bean.valueName = it.dz ?: ""
                        }
                        bean.keyName.contains("营业执照号码") -> {
                            bean.valueName = it.tyshxydm ?: ""
                        }

//                        bean.keyName.contains("税务登记证号码（国税）") -> {
//                            bean.valueName = it.tyshxydm ?: ""
//                        }
//                        bean.keyName.contains("税务登记证号码（地税）") -> {
//                            bean.valueName = it.tyshxydm ?: ""
//                        }
                        bean.keyName.contains("法定代表人") -> {
                            bean.valueName = it.fr ?: ""
                        }
                        bean.keyName.contains("注册资本") -> {
                            bean.valueName = it.zczb ?: ""
                        }
                        bean.keyName.contains("成立日期") -> {
                            bean.dataValue = it.clrq
                            notifyItemChanged(index)
                        }
                        bean.keyName.contains("营业执照登记时间") -> {
                            bean.dataValue = it.clrq
                            notifyItemChanged(index)

                        }

                        bean.keyName.contains("营业期限") -> {
                            bean.valueName = it.yyqx ?: ""
                        }
                        bean.keyName.contains("经营范围") -> {
                            bean.valueName = it.jyfw ?: ""
                        }
                        bean.keyName.contains("营业执照地址") -> {
                            bean.valueName = it.dz ?: ""
                        }
                    }
                }

                upPicData(arrayListOf(mFile), item) {
                    listener.invoke()
                }
//                } else {
////                    SZWUtils.showSnakeBarMsg(recyclerView, if (isFront) "身份证正面证识别失败" else "身份证背面证识别失败")
//                }
            }
        }
    }
    /**
     * 人脸识别+上传
     */
    private fun faceUpData(item: T, mFile: File, listener: () -> Unit) {
        val js = if (item.valueHint.contains("配偶")) "配偶" else "本人"
        val ymbs = when {
            item.valueHint.contains("BR") -> "BR"
            item.valueHint.contains("PO") -> "PO"
            else -> "BR"
        }
        DataCtrlClass.upDataFace(context, keyId = keyId ?: "", js, ymbs, mFile) {
            if (it != null) {
                if (it.conclusion == "0") {
                    item.picUrl = it.headPhotoAddr
                    listener.invoke()
                } else {
                    SZWUtils.showSnakeBarMsg("人脸识别不一致，请重新上传")
                }
            }
        }
    }

    private fun upPicData(files: ArrayList<File>, item: T, listener: () -> Unit) { // 上传身份证
        DataCtrlClass.uploadFiles(context, keyId = keyId, type = type, files = files) {
            if (it != null) {
                if (it.size == 1) {
                    item.picUrl = it[0].filePath
                }
                if (item.picList?.size ?: 0 > 1) item.picList?.addAll(1, it)
                else item.picList?.addAll(it)
                listener.invoke()
            }
        }

    }

}

class ItemPicAdapter<T : PicBean> :
    BaseQuickAdapter<T, BaseViewHolder>(R.layout.item_pic, ArrayList<T>()) { //    override fun getItemCount(): Int {
    //        return 6
    //    }

    override fun convert(helper: BaseViewHolder, item: T) {
        val imageView = helper.itemView.findViewById<ImageView>(R.id.img)
        SZWUtils.loadPhotoImg(context, item.filePath, imageView, 15)

    }

}