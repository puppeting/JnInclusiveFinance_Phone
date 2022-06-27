package com.inclusive.finance.jh.bean

class IDCardBean {
    /**
     * log_id : 2990751065948286558
     * image_status : normal
     * words_result_num : 6
     * words_result : {"住址":{"location":{"width":116,"top":612,"height":488,"left":552},"words":"江苏省徐州市泉山区民乐园20号楼2单元301室"},"出生":{"location":{"width":52,"top":724,"height":375,"left":460},"words":"19910423"},"姓名":{"location":{"width":52,"top":943,"height":154,"left":289},"words":"展延蕊"},"公民身份号码":{"location":{"width":67,"top":263,"height":647,"left":777},"words":"320311199104237629"},"性别":{"location":{"width":41,"top":1058,"height":35,"left":389},"words":"女"},"民族":{"location":{"width":35,"top":833,"height":30,"left":381},"words":"回"}}
     */
    var log_id: Long = 0
    var image_status: String? = null
    var words_result_num = 0
    var words_result: WordsResultBean? = null
    class WordsResultBean {
        /**
         * 住址 : {"location":{"width":116,"top":612,"height":488,"left":552},"words":"江苏省徐州市泉山区民乐园20号楼2单元301室"}
         * 出生 : {"location":{"width":52,"top":724,"height":375,"left":460},"words":"19910423"}
         * 姓名 : {"location":{"width":52,"top":943,"height":154,"left":289},"words":"展延蕊"}
         * 公民身份号码 : {"location":{"width":67,"top":263,"height":647,"left":777},"words":"320311199104237629"}
         * 性别 : {"location":{"width":41,"top":1058,"height":35,"left":389},"words":"女"}
         * 民族 : {"location":{"width":35,"top":833,"height":30,"left":381},"words":"回"}
         */
        var 住址: 住址Bean? = null
        var 出生: 出生Bean? = null
        var 姓名: 姓名Bean? = null
        var 公民身份号码: 公民身份号码Bean? = null
        var 性别: 性别Bean? = null
        var 民族: 民族Bean? = null
        /**
         * 失效日期 : {"location":{"width":239,"top":786,"left":888,"height":39},"words":"20230115"}
         * 签发机关 : {"location":{"width":476,"top":675,"left":610,"height":45},"words":"徐州市公安局泉山分局"}
         * 签发日期 : {"location":{"width":230,"top":785,"left":609,"height":41},"words":"20130115"}
         */
        var 失效日期: 失效日期Bean? = null
        var 签发机关: 签发机关Bean? = null
        var 签发日期: 签发日期Bean? = null


        class 失效日期Bean {
            /**
             * location : {"width":239,"top":786,"left":888,"height":39}
             * words : 20230115
             */
            var location: LocationBean? = null
            var words: String? = null

            class LocationBean {
                /**
                 * width : 239
                 * top : 786
                 * left : 888
                 * height : 39
                 */
                var width = 0
                var top = 0
                var left = 0
                var height = 0
            }
        }

        class 签发机关Bean {
            /**
             * location : {"width":476,"top":675,"left":610,"height":45}
             * words : 徐州市公安局泉山分局
             */
            var location: LocationBeanX? = null
            var words: String? = null

            class LocationBeanX {
                /**
                 * width : 476
                 * top : 675
                 * left : 610
                 * height : 45
                 */
                var width = 0
                var top = 0
                var left = 0
                var height = 0
            }
        }

        class 签发日期Bean {
            /**
             * location : {"width":230,"top":785,"left":609,"height":41}
             * words : 20130115
             */
            var location: LocationBeanXX? = null
            var words: String? = null

            class LocationBeanXX {
                /**
                 * width : 230
                 * top : 785
                 * left : 609
                 * height : 41
                 */
                var width = 0
                var top = 0
                var left = 0
                var height = 0
            }
        }
        class 住址Bean {
            /**
             * location : {"width":116,"top":612,"height":488,"left":552}
             * words : 江苏省徐州市泉山区民乐园20号楼2单元301室
             */
            var location: LocationBean? = null
            var words: String? = null

            class LocationBean {
                /**
                 * width : 116
                 * top : 612
                 * height : 488
                 * left : 552
                 */
                var width = 0
                var top = 0
                var height = 0
                var left = 0
            }
        }

        class 出生Bean {
            /**
             * location : {"width":52,"top":724,"height":375,"left":460}
             * words : 19910423
             */
            var location: LocationBeanX? = null
            var words: String? = null

            class LocationBeanX {
                /**
                 * width : 52
                 * top : 724
                 * height : 375
                 * left : 460
                 */
                var width = 0
                var top = 0
                var height = 0
                var left = 0
            }
        }

        class 姓名Bean {
            /**
             * location : {"width":52,"top":943,"height":154,"left":289}
             * words : 展延蕊
             */
            var location: LocationBeanXX? = null
            var words: String? = null

            class LocationBeanXX {
                /**
                 * width : 52
                 * top : 943
                 * height : 154
                 * left : 289
                 */
                var width = 0
                var top = 0
                var height = 0
                var left = 0
            }
        }

        class 公民身份号码Bean {
            /**
             * location : {"width":67,"top":263,"height":647,"left":777}
             * words : 320311199104237629
             */
            var location: LocationBeanXXX? = null
            var words: String? = null

            class LocationBeanXXX {
                /**
                 * width : 67
                 * top : 263
                 * height : 647
                 * left : 777
                 */
                var width = 0
                var top = 0
                var height = 0
                var left = 0
            }
        }

        class 性别Bean {
            /**
             * location : {"width":41,"top":1058,"height":35,"left":389}
             * words : 女
             */
            var location: LocationBeanXXXX? = null
            var words: String? = null

            class LocationBeanXXXX {
                /**
                 * width : 41
                 * top : 1058
                 * height : 35
                 * left : 389
                 */
                var width = 0
                var top = 0
                var height = 0
                var left = 0
            }
        }

        class 民族Bean {
            /**
             * location : {"width":35,"top":833,"height":30,"left":381}
             * words : 回
             */
            var location: LocationBeanXXXXX? = null
            var words: String? = null

            class LocationBeanXXXXX {
                /**
                 * width : 35
                 * top : 833
                 * height : 30
                 * left : 381
                 */
                var width = 0
                var top = 0
                var height = 0
                var left = 0
            }
        }
    }
}