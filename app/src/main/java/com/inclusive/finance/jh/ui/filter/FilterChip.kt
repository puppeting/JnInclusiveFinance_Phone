

package com.inclusive.finance.jh.ui.filter

import android.graphics.Color

/** Wrapper model for showing [Filter] as a chip in the UI. */
data class FilterChip(
    val filter: Filter,
    var isSelected: Boolean,
    var isSingleCheck: Boolean=false,
    val categoryLabel: String ="",
    val color: Int ?= Color.parseColor("#4768fd"), // @color/indigo
    val selectedTextColor: Int = Color.WHITE,
    val textResId: Int = 0,
    val text: String = ""
)

fun Filter.asChip(isSelected: Boolean): FilterChip = when (this) {
    is Filter.TagFilter -> FilterChip(
        filter = this,
        isSelected = isSelected,
        color = tag.color,
        text = tag.valueName,
        isSingleCheck = tag.isSingleCheck,
        selectedTextColor = tag.fontColor ?: Color.TRANSPARENT,
        categoryLabel = tag.filterCategoryLabel()
    )
    is Filter.SearchFilter -> FilterChip(
        filter = this,
        isSelected = isSelected,
        color = tag.color,
        text = tag.valueName,
        isSingleCheck = tag.isSingleCheck,
        selectedTextColor = tag.fontColor ?: Color.TRANSPARENT,
        categoryLabel = tag.filterCategoryLabel()
    )
    Filter.MyScheduleFilter -> FilterChip(
        filter = this,
        isSelected = isSelected,
        textResId = 0,
        categoryLabel = "R.string.category_heading_dates"
    )
}
private fun Tag.filterCategoryLabel(): String = when (this.category) {
    Tag.CATEGORY_KHXM -> "客户姓名"
    Tag.CATEGORY_SFBR -> "是否本人发起"
    Tag.CATEGORY_LCZT -> "流程状态"
    Tag.CATEGORY_SFQP -> "是否签批"
    Tag.CATEGORY_JG -> "机构"
    Tag.CATEGORY_JYZT -> "检验状态"
    Tag.CATEGORY_SFDB -> "是否待办"
    else -> ""
}