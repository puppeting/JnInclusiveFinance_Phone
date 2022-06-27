package com.inclusive.finance.jh.ui.investigation

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bin.david.form.core.TableConfig
import com.bin.david.form.data.CellInfo
import com.bin.david.form.data.column.Column
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat
import com.bin.david.form.data.format.draw.MultiLineDrawFormat
import com.bin.david.form.data.style.FontStyle
import com.bin.david.form.data.table.TableData
import com.blankj.utilcode.util.ScreenUtils
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.TargetBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.*
import com.inclusive.finance.jh.interfaces.PresenterClick
import org.jetbrains.anko.support.v4.act


/**
 * 评级指标
 * */
class PJZBFragment : MyBaseFragment(), PresenterClick {
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentPjzbBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentPjzbBinding.inflate(inflater, container, false).apply {
            presenterClick = this@PJZBFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {


    }

    override fun initData() {
        val getUrl= when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_PRECREDIT -> {
                Urls.get_preCredit_pjzb
            }
            else -> {
                Urls.getPJZB
            }
        }
        DataCtrlClass.SXDCNet.getPJZB(requireActivity(),getUrl, keyId = viewModel.keyId) {
            if (it != null) {
                /**
                 * 得分 : 2
                 * 项目 : 个人情况
                 * 满分 : 4
                 * 指标定义或计算公式 : 【18,28），1分、【28,35），2分、【35,50）岁，4分、【50,60）岁,3分、≥60，0分
                 * 指标 : 29
                 * 关键指标名称 : 年龄
                 */
                //普通列
                /**
                 * 得分 : 2
                 * 项目 : 个人情况
                 * 满分 : 4
                 * 指标定义或计算公式 : 【18,28），1分、【28,35），2分、【35,50）岁，4分、【50,60）岁,3分、≥60，0分
                 * 指标 : 29
                 * 关键指标名称 : 年龄
                 */
                val with = (ScreenUtils.getScreenWidth()) / 40
                //普通列
                val column1 = Column<String>("大项", "project")
                column1.isAutoMerge = true
                val column2 = Column<String>("序号", "xh")
                val column3 = Column<String>("风险类型", "name")
                val column4 = Column<String?>("内容", "content")
                val column5 = Column<String>("满分", "full")
                val column6 = Column<String>("指标", "target")
                val column7 = Column<String>("得分", "score")
                val column = Column<String>("自然人评级表", column1, column2, column3, column4, column5, column6, column7)

                //字体样式改变
                fun setTXTSize(cellInfo: CellInfo<String>, paint: Paint, config: TableConfig) {
                    when (cellInfo.value) {
                        "核心指标", "序号", "项目", "内容", "满分", "指标", "得分" -> {
                            paint.textSize = 44f
                            paint.typeface = Typeface.DEFAULT_BOLD
                        }
                        else -> {
                            paint.textSize = config.contentStyle.textSize.toFloat() * config.zoom
                            paint.typeface = Typeface.DEFAULT
                        }
                    }
                }
                val multiLineDrawFormat = object : MultiLineDrawFormat<String>(activity, with * 1) {
                    override fun setTextPaint(config: TableConfig, cellInfo: CellInfo<String>, paint: Paint) {
                        super.setTextPaint(config, cellInfo, paint)
                        setTXTSize(cellInfo, paint, config)

                    }
                }
                val multiLineDrawFormat2 = object :
                    MultiLineDrawFormat<String>(activity, with * 4) {
                    override fun setTextPaint(config: TableConfig, cellInfo: CellInfo<String>, paint: Paint) {
                        super.setTextPaint(config, cellInfo, paint)
                        setTXTSize(cellInfo, paint, config)
                    }


                }
                column1.drawFormat = multiLineDrawFormat
                column2.drawFormat = multiLineDrawFormat
                column3.drawFormat = multiLineDrawFormat
                column4.drawFormat = multiLineDrawFormat2
                column5.drawFormat = multiLineDrawFormat
                column6.drawFormat = multiLineDrawFormat
                column7.drawFormat = multiLineDrawFormat
                //                val column10 = Column<String>("自然人评级表", column1, column2, column3, column4, column5, column6, column7)

                val coreTarget = it.target
                if (!it.coreTarget.isNullOrEmpty()) {
                    coreTarget.add(TargetBean("", "", "", "", "", "", ""))
                    coreTarget.add(TargetBean(" ", " ", " ", " ", " ", " ", " "))
                    coreTarget.add(TargetBean("核心指标", "", "", "", "", "", ""))
                    coreTarget.add(TargetBean("内容", "满分", "项目", "", "得分", "指标", "序号").apply {})
                    coreTarget.addAll(it.coreTarget)
                }
                //表格数据 datas是需要填充的数据
                val tableData: TableData<TargetBean> = TableData("自然人评级表", coreTarget, column)
                tableData.isShowCount = false
                //table.setZoom(true,3);是否缩放
                //table.setZoom(true,3);是否缩放
                viewBind.table.config.isShowXSequence = false
                viewBind.table.config.isShowYSequence = false
                viewBind.table.config.isFixedTitle = false
                viewBind.table.config.isShowTableTitle=false

                val value = object : FontStyle() {
                    override fun fillPaint(paint: Paint) {
                        paint.typeface = Typeface.DEFAULT_BOLD
                        super.fillPaint(paint)
                    }

                }
                value.textSize = 44
                //标题字体
                viewBind.table.config.columnTitleStyle= value
                //设置列标题背景
                //                viewBind.table.config.contentCellBackgroundFormat = object :
                //                    ICellBackgroundFormat<CellInfo<Any>> {
                //                    override fun drawBackground(canvas: Canvas, rect: Rect, cellInfo: CellInfo<Any>, paint: Paint) {
                //                        if (cellInfo.value == "核心指标") {
                //                            paint.color = ContextCompat.getColor(act, R.color.colorPrimary)
                //                            canvas.drawRect(rect, paint)
                //                        }
                //                    }
                //
                //                    override fun getTextColor(cellInfo: CellInfo<Any>): Int {
                //                        return if (cellInfo.value.contains("____")) {
                //                            ContextCompat.getColor(act, R.color.colorPrimary)
                //                        } else {
                //                            0
                //                        }
                //                    }
                //                }
//                tableData.xSequenceFormat = object : LetterSequenceFormat() {
//                    override fun draw(canvas: Canvas?, sequence: Int, rect: Rect?, config: TableConfig?) {
//                        //字体缩放
//                        val paint = config!!.paint
//                        paint.textSize = 22f
//                        paint.textAlign = Paint.Align.CENTER
//                        canvas!!.drawText(format(sequence + 1), rect!!.centerX()
//                            .toFloat(), DrawUtils.getTextCenterY(rect!!.centerY(), paint), paint)
//                    }
//                }
                // 改某一行背景色
                viewBind.table.config.contentCellBackgroundFormat = object :
                    BaseCellBackgroundFormat<CellInfo<*>>() {
                    override fun getBackGroundColor(cellInfo: CellInfo<*>): Int {
                        return if (cellInfo.value  ==" ") {
                            ContextCompat.getColor(act, R.color.MaterialGrey400)
                        } else TableConfig.INVALID_COLOR
                    }
                }
                viewBind.table.setTableData(tableData)
                if (isAdded)
                (activity as BaseActivity).refreshData()
            }
        }
    }


    override fun onClick(v: View?) {

    }

}