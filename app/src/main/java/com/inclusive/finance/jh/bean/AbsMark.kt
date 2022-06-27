package com.inclusive.finance.jh.bean

/**
 * Created by 史忠文
 * on 2017/11/29.
 */
abstract class AbsMark{
    open fun getMarkId():String=""
    open fun getMarkType():String=""
    open  fun getAdUri():String = ""
}