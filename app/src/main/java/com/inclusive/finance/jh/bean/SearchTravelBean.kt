package com.inclusive.finance.jh.bean

import java.util.*

/**
 * Created by 史忠文
 * on 2017/6/6.
 */
open class SearchTravelBean(var name: String = "", var date: Date? = null,   var id: Long = 0) {
    var code:String ?=null
    var image:String ?=null
    var pCode:String ?=null
    var haveChild:String ?=null
    var isFavorite=false
    var list:MutableList<SearchTravelBean>?=null
    companion object {
        val primaryKey = "searchContent"
        val DateKey = "date"
    }
}
