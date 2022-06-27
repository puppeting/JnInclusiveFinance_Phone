package com.inclusive.finance.jh.pop


import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.blankj.utilcode.util.SizeUtils
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemListMenuTextAdapter
import com.inclusive.finance.jh.databinding.PopListMenuBinding
import razerdp.basepopup.BasePopupWindow


class BaseListMenuPop : BasePopupWindow {


    var listener: (index: Int) -> Unit?
    lateinit var mAdapter:ItemListMenuTextAdapter<String>
    var datas:MutableList<String>
    constructor(mContext: Context?, datas :MutableList<String>,listener: (index: Int) -> Unit? = {}) : super(mContext) {
        this.datas = datas
        this.listener = listener
        init()
    }


    constructor(mContext: Dialog?,datas :MutableList<String>, listener: (index: Int) -> Unit? = {}) : super(mContext) {
        this.datas = datas
        this.listener = listener
        init()
    }

    private fun init() {
        setBackground(0)
        setPopupGravityMode(GravityMode.RELATIVE_TO_ANCHOR,GravityMode.ALIGN_TO_ANCHOR_SIDE)
        popupGravity=Gravity.RIGHT or Gravity.TOP


        mAdapter= ItemListMenuTextAdapter()
        viewBinding.mRecyclerView.adapter= mAdapter
        viewBinding.mRecyclerView. addItemDecoration(
            DividerItemDecoration(context,DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context,R.drawable.divider_list_menu)!!)
            }
        )
        mAdapter.setNewInstance(datas)
        mAdapter.setOnItemClickListener { adapter, view, position ->
//            SZWUtils.showSnakeBarMsg("点了${datas[position]}")
            listener.invoke(position)
            dismiss()
        }
    }

    lateinit var viewBinding: PopListMenuBinding
    override fun onCreateContentView(): View { //        dataBind = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.pop_down_recycler,null,false)
        viewBinding = PopListMenuBinding.bind(createPopupById(R.layout.pop_list_menu))
        return viewBinding.root
    }


    override fun onPopupLayout(popupRect: Rect, anchorRect: Rect){
        //计算basepopup中心与anchorview中心方位
        //e.g：算出gravity == Gravity.Left，意味着Popup显示在anchorView的左侧
        val gravity = computeGravity(popupRect, anchorRect)
        var verticalCenter = false
        when (gravity and Gravity.VERTICAL_GRAVITY_MASK) {
            Gravity.TOP -> {
                viewBinding.ivArrow.visibility = View.VISIBLE
                //设置箭头水平位置为相对于basepopup居中
                viewBinding.ivArrow.translationX = (popupRect.width() - viewBinding.ivArrow.width shr 1).toFloat()
                //设置箭头垂直位置为相对于basepopup底部
                viewBinding.ivArrow.translationY = (popupRect.height() - viewBinding.ivArrow.height).toFloat()
                //设置旋转角度0度（即箭头朝下，具体根据您的初始切图而定）
                viewBinding.ivArrow.rotation = 0f
            }
            Gravity.BOTTOM -> {
                viewBinding.ivArrow.visibility = View.VISIBLE
                viewBinding.ivArrow.translationX = (popupRect.width() - viewBinding.ivArrow.width shr 1).toFloat()
                viewBinding.ivArrow.translationY = 0F
                viewBinding.ivArrow.rotation = 180f
            }
            Gravity.CENTER_VERTICAL -> verticalCenter = true
        }
        when (gravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
            Gravity.LEFT -> {
                viewBinding.ivArrow.visibility = View.VISIBLE
                viewBinding.ivArrow.translationX = (popupRect.width() - viewBinding.ivArrow.width).toFloat()
                viewBinding.ivArrow.translationY = (popupRect.height() - viewBinding.ivArrow.height shr 1).toFloat()
                viewBinding.ivArrow.rotation = 270f
            }
            Gravity.RIGHT -> {
                viewBinding.ivArrow.visibility = View.VISIBLE
                viewBinding.ivArrow.translationX = 0F
                viewBinding.ivArrow.translationY = ((anchorRect.top-popupRect.top+(viewBinding.ivArrow.height shr 1)+SizeUtils.dp2px(15f))).toFloat()
                viewBinding.ivArrow.rotation = 90f
            }
            Gravity.CENTER_HORIZONTAL ->                 //如果basepopup与anchorview中心对齐，则隐藏箭头
                viewBinding.ivArrow.visibility = if (verticalCenter) View.INVISIBLE else View.VISIBLE
        }
    }
}