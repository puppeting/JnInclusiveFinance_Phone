package com.inclusive.finance.jh.bean

class IDCardBack {
    /**
     * log_id : 6790051746250251614
     * words_result_num : 3
     * image_status : normal
     * words_result : {"失效日期":{"location":{"width":239,"top":786,"left":888,"height":39},"words":"20230115"},"签发机关":{"location":{"width":476,"top":675,"left":610,"height":45},"words":"徐州市公安局泉山分局"},"签发日期":{"location":{"width":230,"top":785,"left":609,"height":41},"words":"20130115"}}
     */
    var log_id: Long = 0
    var words_result_num = 0
    var image_status: String? = null
    var words_result: WordsResultBean? = null

    class WordsResultBean
}