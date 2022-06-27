package com.inclusive.finance.jh.adapter.binding

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.appcompat.widget.Toolbar
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout

import androidx.databinding.BindingAdapter
import com.blankj.utilcode.util.SizeUtils
import com.flyco.roundview.RoundFrameLayout
import com.flyco.roundview.RoundLinearLayout
import com.flyco.roundview.RoundTextView
import com.google.android.material.textfield.TextInputLayout
import com.inclusive.finance.jh.utils.SZWUtils
import com.umeng.umcrash.UMCrash



//获取自定义属性
@BindingAdapter("PhotoImageUrl")
fun setPhotoImageUrl(imgView: ImageView?, data: String) {
    SZWUtils.loadPhotoImg(imgView?.context, data, imgView)
}

//获取自定义属性
@BindingAdapter("tint")
fun setActualImageUri(imgView: ImageView?, data: Int) {
    imgView?.imageTintList = ColorStateList.valueOf(data)
} //获取自定义属性

@BindingAdapter("rv_backgroundColor")
fun setRvBackgroundColor(roundLinearLayout: RoundLinearLayout?, data: Int) {
    roundLinearLayout?.delegate?.backgroundColor = data
} //获取自定义属性

@BindingAdapter("rv_strokeColor")
fun setRvStrokeColor(roundLinearLayout: RoundLinearLayout?, data: Int) {
    roundLinearLayout?.delegate?.strokeColor = data
}  //获取自定义属性

@BindingAdapter("rv_strokeColor")
fun setRvStrokeColor(roundLinearLayout: RoundTextView?, data: Int) {
    roundLinearLayout?.delegate?.strokeColor = data
}  //获取自定义属性

@BindingAdapter("rv_strokeColor")
fun setRvStrokeColor(roundLinearLayout: RoundFrameLayout?, data: Int) {
    roundLinearLayout?.delegate?.strokeColor = data
} //获取自定义属性

@BindingAdapter("rv_backgroundColor")
fun setRvBackgroundColor(roundLinearLayout: RoundTextView?, data: Int) {
    roundLinearLayout?.delegate?.backgroundColor = data
} //获取自定义属性

@RequiresApi(Build.VERSION_CODES.M)
@SuppressLint("UseCompatTextViewDrawableApis")
@BindingAdapter("drawableTint")
fun setTextViewDrawableTint(textView: TextView?, data: Int) {
    textView?.compoundDrawableTintList = ColorStateList.valueOf(data)
}

@BindingAdapter("drawableStartCompat")
fun setTextViewDrawableStart(textView: TextView?, data: Drawable?) {
    textView?.setCompoundDrawablesRelativeWithIntrinsicBounds(data, textView.compoundDrawables[1], textView.compoundDrawables[2], textView.compoundDrawables[3])
}@BindingAdapter("drawableEndCompat")
fun setTextViewDrawableEnd(textView: TextView?, data: Drawable?) {
    textView?.setCompoundDrawablesRelativeWithIntrinsicBounds(textView.compoundDrawables[0], textView.compoundDrawables[1], data, textView.compoundDrawables[3])
}

@BindingAdapter("layout_marginTop")
fun setConstraintLayoutMarginTop(constraintLayout: ConstraintLayout?, data: Float) {
    (constraintLayout?.layoutParams as ViewGroup.MarginLayoutParams).topMargin = SizeUtils.dp2px(data)
}

@BindingAdapter("layout_marginTop")
fun setFrameLayoutMarginTop(view: FrameLayout?, data: Float) {
    (view?.layoutParams as ViewGroup.MarginLayoutParams).topMargin = SizeUtils.dp2px(data)
}

@BindingAdapter("layout_marginStart")
fun setTextMarginStart(textView: TextView?, data: Float) {
    (textView?.layoutParams as ConstraintLayout.LayoutParams).leftMargin = SizeUtils.dp2px(data)
}

@BindingAdapter("layout_marginEnd")
fun setTextMarginEnd(textView: TextView?, data: Float) {
    (textView?.layoutParams as ConstraintLayout.LayoutParams).rightMargin = SizeUtils.dp2px(data)
}

@BindingAdapter("layout_marginStart")
fun setTextMarginStart(textView: RoundLinearLayout?, data: Float) {
    (textView?.layoutParams as ConstraintLayout.LayoutParams).leftMargin = SizeUtils.dp2px(data)
}

@BindingAdapter("layout_marginEnd")
fun setTextMarginEnd(textView: RoundLinearLayout?, data: Float) {
    (textView?.layoutParams as ConstraintLayout.LayoutParams).rightMargin = SizeUtils.dp2px(data)
}

@BindingAdapter("layout_marginStart")
fun setTextMarginStart(textView: TextInputLayout?, data: Float) {
    (textView?.layoutParams as ConstraintLayout.LayoutParams).leftMargin = SizeUtils.dp2px(data)
}

@BindingAdapter("layout_marginEnd")
fun setTextMarginEnd(textView: TextInputLayout?, data: Float) {
    (textView?.layoutParams as ConstraintLayout.LayoutParams).rightMargin = SizeUtils.dp2px(data)
}

@BindingAdapter("title")
fun setToolbarTitle(textView: Toolbar?, data: String) {
    textView?.title = data
}

@BindingAdapter("layout_width")
fun setConstraintLayoutWidth(view: ConstraintLayout?, data: Int) {
    view?.layoutParams?.width = data
}

@BindingAdapter("layout_height")
fun setConstraintLayoutHeight(view: ConstraintLayout?, data: Int) {
    view?.layoutParams?.height = data
}

@BindingAdapter("layout_height")
fun setPreviewViewHeight(view: PreviewView?, data: Int) {
    view?.layoutParams?.height = data
}
@BindingAdapter("layout_height")
fun setFrameLayoutHeight(view: FrameLayout?, data: Int) {
    view?.layoutParams?.height = data
}

@BindingAdapter("layout_width")
fun setRoundLinearLayoutWidth(view: RoundLinearLayout?, data: Int) {
    view?.layoutParams?.width = data
}

@BindingAdapter("layout_gravity")
fun setConstraintLayoutLayGravity(view: ConstraintLayout, data: Int) {
    try {
        val layoutParams = view.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = data
    } catch (e: Exception) {
        UMCrash.generateCustomLog(e, "UmengException")
        e.printStackTrace()
    }
}
@BindingAdapter("textColor")
fun setEditTextColor(view: EditText, data: Int) {
    view.setTextColor(data)
}
@BindingAdapter("textColor")
fun setEditTextColor(view: TextView, data: Int) {
    view.setTextColor(data)
}
@BindingAdapter("textColor")
fun setEditTextColor(view: AppCompatCheckedTextView, data: Int) {
    view.setTextColor(data)
}


@BindingAdapter("goneUnless")
fun goneUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}