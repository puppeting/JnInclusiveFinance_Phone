package com.inclusive.finance.jh.bean

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import androidx.databinding.Bindable
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.contrarywind.interfaces.IPickerViewData
import com.inclusive.finance.jh.BR
import com.inclusive.finance.jh.ui.ApplyActivity.Companion.ClassType.XXGK
import java.lang.Exception


open class BaseTypeBean(val layoutType: Int = TYPE_1) : MultiItemEntity, KeyAndValueBean() {

    override val itemType: Int
        get() = layoutType
    private val field = "" //字段名称
    var visibility = true //是否显示，默认true
    val model: String? = null //后台使用
    val dataKey = "" //后台使用
    var dataValue: String? = "" //后台使用
    var keyName = "" //名称
    val valueHint = "" //提示文字 也作为匹配判断文字如ocr识别
    var listBean: BaseListBean? = null //列表类型信息
    var options1Items: List<Enum12>? = null
    var options2Items: List<List<Enum12>>? = null
    var options3Items: List<List<List<Enum12>>>? = null
    var optionsPosition: ArrayList<Int>? = null
    var regex: String? = "" // 输入框 正则校验内容
    var regexErrorMsg: String? = "" // 正则校验错误提示
    var houseNumberPage: MlistBean? = null

    val haveValue: Boolean
        get() {
            return valueName.isNotEmpty() || !picUrl.isNullOrEmpty() || !picList.isNullOrEmpty()
        }

    @Bindable
    var valueName = ""
        //数值
        get() {
            return if (dataValue.isNullOrEmpty()) {
                field
            } else {
                dataValue ?: ""
            }
        }
        set(value) {
            field = value
            dataValue = value
            notifyPropertyChanged(BR.valueName)
        }

    var locationValueName = ""
        set(value) {
            field = value
            valueName = if (valueName.contains("||")) {
                val split = valueName.split("||", limit = 2)
                value + "||" + split[1]
            } else {
                value
            }
            notifyPropertyChanged(BR.valueName)
        }
    var ems: Int? = Int.MAX_VALUE // 输入框最大输入字符数

    @Bindable
    var editable = false //可否编辑
        set(value) {
            field = value
            notifyPropertyChanged(BR.editable)
        }
    val inputType: Int = 8194 //编辑类型 。数字或文本
    var spanSize = 1 //跨多少列。不跨则为1
    val minLines = 1 //最小行数，编辑框高度设定
    val width = ViewGroup.LayoutParams.MATCH_PARENT// 宽度 wrap 为控件宽度/6*spanSize
    val height = ViewGroup.LayoutParams.MATCH_PARENT// 宽度 wrap 为180
    var layoutGravity = Gravity.START//权重。布局相对位置。
    val titleOnly = false //是否只显示keyName

    @Bindable
    var checked = false
        //目前仅在同意阅读处使用：征信授权
        set(value) {
            field = value
            dataValue = if (value) "已选中" else ""
            notifyPropertyChanged(BR.checked)
        }
        get() {
            return !dataValue.isNullOrEmpty()
        }
    var isSingleChecked = true //是否单选

    @Bindable
    var requireable = false //是否必填项
        set(value) {
            field = value
            notifyPropertyChanged(BR.requireable)
        }
    var isSingleLine = false //是否单行显示
    var enums12: ArrayList<Enum12>? = null //枚举值 。若有下拉
    val richTextList: ArrayList<RichBean>? = null //多个富文本控件数据，如各种授权书

    val picCount = 1 //图片选取数量 14、15
    var isGallery = false //是否展开图册14、15
    var picUrl: String? = "" //多图,隔开14、15
        set(value) {
            field = value
            valueName = if (!value.isNullOrEmpty()) "已有图片" else ""
            notifyPropertyChanged(BR.checked)
        }
    var picList: ArrayList<PicBean>? = null //多个图片的列表
        get() {
            return if (field == null) arrayListOf() else field
        }
    var picBgUrl: String? = "" //背景图14、15
    var picDeleteUrl: String? = "" //删除链接15
    var treeUrl: String? = "" //树形菜单地址
    var button: Boolean? = false //请假审批列表使用
    var locationAble: Boolean? = true //定位 导航  组件是否可以 选点
    var varValueNameColor: String? = "" //可编辑值颜色
    val varValueNameColorInt  //可编辑值颜色
        get() = if (varValueNameColor.isNullOrEmpty()) {
            Color.parseColor("#333333")
        } else Color.parseColor(varValueNameColor)
    var valValueNameColor: String? = "" //不可编辑值颜色
    val valValueNameColorInt  //不可编辑值颜色
        get() = if (valValueNameColor.isNullOrEmpty()) {
            Color.parseColor("#A1A1A1")
        } else Color.parseColor(valValueNameColor)
    var varKeyNameColor: String? = "" //可编辑名称颜色
    val varKeyNameColorInt   //可编辑名称颜色
        get() = if (varKeyNameColor.isNullOrEmpty()) {
            Color.parseColor("#333333")
        } else Color.parseColor(varKeyNameColor)
    var valKeyNameColor: String? = "" //不可编辑名称颜色
    val valKeyNameColorInt   //不可编辑名称颜色
        get() = if (valKeyNameColor.isNullOrEmpty()) {
            Color.parseColor("#A1A1A1")
        } else Color.parseColor(valKeyNameColor)
    var promptInformation = "" //提示信息。如果需要的话

    fun convertStringToFloat(value: String?): Float {
        return if (!value.isNullOrEmpty()) {
            try {
                value.toFloat()
            } catch (e: Exception) {
                e.printStackTrace()
                0.toFloat()
            }
        } else 0.toFloat()
    }

    override fun absKey() = keyName
    override fun absValue() = valueName
    override fun absCheck() = checked

    open class Enum12 : KeyAndValueBean, IPickerViewData {
        var id: Enum<*> = XXGK
        var keyName = "" //名称
        var valueName = "" //数值
        var textColor = "#333333" //数值

        constructor() : super()
        constructor(id: Enum<*> = XXGK, valueName: String = "") : super() {
            this.id = id
            this.valueName = valueName
        }

        @Bindable
        var checked = false
            set(value) {
                field = value
                notifyPropertyChanged(BR.checked)
            }

        override fun absKey() = keyName
        override fun absValue() = valueName
        override fun absCheck() = checked
        override fun getPickerViewText() = valueName
    }

    open class RichBean : KeyAndValueBean() {
        private val keyName: String? = "" //名称
        var valueName = "" //数值

        @Bindable
        var checked = false
            set(value) {
                field = value
                notifyPropertyChanged(BR.checked)
            }

        override fun absKey() = keyName ?: ""
        override fun absValue() = valueName
        override fun absCheck() = checked
    }

    companion object {

        const val TYPE_1 = 10 //数字抬头布局
        const val TYPE_2 = 11 //普通文本框
        const val TYPE_3 = 12 //下拉选择框
        const val TYPE_4 = 13 //列表类型
        const val TYPE_5 = 14 //单图
        const val TYPE_6 = 15 //多图
        const val TYPE_7 = 16 //时间选择
        const val TYPE_8 = 17 //特殊**行业分类，不限级列表 只可选子集。
        const val TYPE_9 = 18 //征信授权书之类的富文本
        const val TYPE_10 = 19 //签字板
        const val TYPE_11 = 20 //人脸识别
        const val TYPE_12 = 21 //协议
        const val TYPE_13 = 22 //中间业务：水火电费等银联支付宝等
        const val TYPE_14 = 23 //评价星星
        const val TYPE_15 = 24 //只显示一行文字
        const val TYPE_16 = 25 //普通文本框 横向
        const val TYPE_17 = 26 //地点级联选择
        const val TYPE_18 = 27 //现金流图表
        const val TYPE_19 = 28 //地图选址+点击导航
        const val TYPE_20 = 29 //模糊检索输入框
        const val TYPE_21 = 30 //特殊**职业分类，不限级列表 只可选子集。
        const val TYPE_22 = 31 //特殊模糊检索输入框 (发证机关)
        const val TYPE_23 = 32 //特殊**行业分类，不限级列表 ,各级别均可选择。
        const val TYPE_24 = 33 //分类，不限级列表 ,各级别均可选择。
        const val TYPE_25 = 34 //既能输入有能切换选择框
        const val TYPE_26 = 35 //带加号的输入框
        const val TYPE_27 = 36 //产品配置
        const val TYPE_28 = 37 //普通文本框- 垂直布局
        const val TYPE_29 = 38 //普通标题

    }

    class MlistBean {
        var TOTAL = ""
        var NUM = ""
    }
}