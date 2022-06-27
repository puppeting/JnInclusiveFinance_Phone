package com.inclusive.finance.jh.bean

import java.util.*

class XjlBean {
    /**
     * 近一年现金流入 : [0,0,0,0,0,0,0,0,0,0,0,0]
     * 近一年流入月份 : ["-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1"]
     * 近一年现金流出 : [0,0,0,0,0,0,0,0,0,0,0,0]
     * max : 0.0
     * 流出总计 : .00
     * xjlpl : {"xjlfx":null,"sxid":null,"id":null,"xjlms":null}
     * 流入总计 : .00
     * 近一年流出月份 : ["-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1","-1"]
     * desc : 现金流分析：
     */
    var max = 0.0
    private val 流出总计: String? = null
    private val 流入总计: String? = null
    var desc: String? = null
    private var 近一年现金流入: ArrayList<Double>? = null
    private var 近一年现金流出: ArrayList<Double>? = null

    /**
     * xjlfx : null
     * sxid : null
     * id : null
     * xjlms : null
     */
    var xjlfx: String? = null
    var sxid: String? = null
    var id: String? = null
    var xjlms: String? = null
}