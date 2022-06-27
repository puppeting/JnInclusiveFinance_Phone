package com.inclusive.finance.jh.bean

data class TargetBeanList(
    val coreTarget: ArrayList<TargetBean>,
    val target: ArrayList<TargetBean>
)
data class TargetBean(
    val content: String,
    val full: String,
    val name: String,
    val project: String,
    val score: String,
    val target: String,
    val xh: String
)