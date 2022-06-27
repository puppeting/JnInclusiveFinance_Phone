package com.inclusive.finance.jh.bean

import java.io.Serializable


/**
 * 版本信息
 */
class VersionBean :Serializable{
    /**
     * mobileVersion : 2
     * id : 1e0bf1bc25087e578b1a182a801ec7e6
     * versionName : 坚铎科技2.0
     * updateContent : 新发布版本
     * updateSize : 100Mb
     * isForceUpdate : 1
     * packageUrl : files/20190923/weizhegou_pro_android_v1.0.2_2_official_1569221020003.0.2_2_official.apk
     * isUpdate : false
     */
    var mobileVersion: String? = null
    var id: String? = null
    var versionName: String? = null
    var updateContent: String? = null
    var updateSize = 0
    var isForceUpdate: String? = null
    var packageUrl: String? = null
    var md5:String?=null
    var patchUrl:String?=null
    // "is_update":"1",//是否更新 1为更新
    var isUpdate: String? = null
    //        "version_name":"发现新版本",//更新提示/标题
    //        "update_content":"此次为测试更新",//更新内容
    //        "update_size":"0.00",//app大小
    //        "is_force_update":1,//是否强制更新 1为强制更新
    //        "package_url":"https://weizhegou.oss-cn-shanghai.aliyuncs.com/version/weizhegou_pro_android_v1.0.2_2_official.apk"//更新地址
}