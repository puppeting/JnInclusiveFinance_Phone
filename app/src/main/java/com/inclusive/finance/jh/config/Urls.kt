package com.inclusive.finance.jh.config

/**
 * Created by 史忠文
 * on 2017/10/17.
 */
object Urls {
    var APP_ID = ""

          var url = "http://192.168.3.49:8001/jeecg-boot/"
//    var url = "http://192.168.3.89:8082/jeecg-boot/"

//    var url = "http://192.168.3.111:8012/jeecg-boot/"
//    var url = "http://18c47a7a2b69d689.natapp.cc:1290/jeecg-boot/"
//    var url = "http://phjr.gnnsyh.com:9022/jeecg-boot/"

    /**
     * 获取验证码
     */
    val GetCode = url + "sys/randomPhone"

    /**
     * 验证验证码
     */
    val VerifyCode = url + "app/user/verifyVerificationCode"

    /**
     * 4.1.1	微信登录
     */
    val loginWechatApp = url + "app/user/loginWechat"


    /**
     * 2.1.5校验是否注册
     */
    val checkRegister = url + "app/user/verificationPhone"

    /**
     *3.3.2	保存用户信息
     */
    val saveUser = url + "app/user/completeUserInfo"

    /**
     *保存修改联系人
     */
    val updateOrSaveCommonUser = url + "app/order/updateOrSaveCommonUser"

    /**
     *删除联系人
     */
    val deleteCommonUser = url + "app/order/deleteCommonUser"


    /**
     *密码登录
     */
    val loginPwd = url + "sys/login"

    /**
     *强制密码登录
     */
    val loginPwd1 = url + "sys/login1"

    /**
     *登出
     */
    val logout = url + "sys/logout"

    /**
     *密码修改
     */
    val changePasswordForApp = url + "sys/user/changePasswordForApp"

    /**
     *客户管理列表
     */
    val khglList = url + "wd/khgl/list"

    /**
     *客户新增获取界面
     */
    val khglGetNewList = url + "wd/ympz/queryByCode?pageCode=khgl_add"

    /**
     *客户管理列表新增
     */
    val khglAdd = url + "wd/khgl/add"

    /**
     *客户管理列表删除
     */
    val khgldelete = url + "wd/khgl/delete"


    /**
     *获取客户信息
     */
    val khglInfo = url + "wd/sxsq/confirm"


    /**
     *人脸识别
     */
    val faceRecognitionUrl = url + "business/sxsqRlsb/auth"

    /**
     *多图上传
     */
    val uploadFiles = url + "sys/common/uploadBatch"


    /**
     * 获取调查报告type
     */
    val getReportTypeInfo = url + "wd/dcbg/getType"


    /**
     * 准入信息我行数据
     */
    val admitBankData = url + "business/sxsqWhkhxx/list"


    /**
     * 获取首页列表
     */
    val getMainMenuList = url + "sys/permission/getUserPermission4App"

    /**
     * 获取首页待办事项列表
     */
    val get_main_dbsx_list = url + "ygxt/index/toDo"

    /**
     * 获取轨迹点集合
     */
    val get_main_track_list = url + "ygxt/index/mapGuiji"

    /**
     * 获取点聚合
     */
    val get_main_cluster_list = url + "ygxt/index/mapFenbu"


    /**
     * 获取调查报告基本信息表
     */
    val getDCBGBaseInfo = url + "ygxt/dcbg/get"
    val saveDCBGBaseInfo = url + "ygxt/sddc/save"

    /**
     * 获取调查报告信贷历史
     */
    val getDCBGXDLSInfo = url + "wd/trdbqk/list"


    /**
     * 获取调查报告申请贷款信息
     */
    val getDCBGSQDKInfo = url + "wd/sxsqYwsq/sqdkList"


    /**
     * 保存调查报告申请贷款信息
     */
    val saveDCBGSQDKInfo = url + "wd/sxsqYwsq/sqdkEdit"

    /**
     * 获取调查报告共同借款人和担保人的信息
     */
    val getDCBGGTJKInfo = url + "wd/jkdbrxx/list"

    /**
     * 获取调查报告担保人资格检查
     */
    val getDCBGDBRZGInfo = url + "wd/dbrzgjc/list"

    /**
     * 获取调查报告第三方评价
     */
    val getDCBGDDSFPJInfo = url + "wd/dsfpj/list"

    /**
     * 保存调查报告第三方评价
     */
    val saveDCBGDDSFPJInfo = url + "wd/dsfpj/editpo"

    /**
     * 获取调查报告贷款资格审查
     */
    val getDCBGDDKZGInfo = url + "wd/dkzgsc/list"

    /**
     * 保存调查报告贷款资格审查
     */
    val saveDCBGDDKZGInfo = url + "wd/dkzgsc/add"

    /**
     * 获取调查报告申请人财务报表
     */
    val getDCBGCWBBInfo = url + "wd/sqrcwbb/getOne"

    /**
     * 保存调查报告申请人财务报表
     */
    val saveDCBGCWBBInfo = url + "wd/sqrcwbb/add"

    /**
     * 获取调查报告-风险因素分析及调查结论
     */
    val getDCBGFXYSInfo = url + "wd/fxfxdcjl/list"

    /**
     * 保存调查报告-风险因素分析及调查结论
     */
    val saveDCBGFXYSInfo = url + "wd/fxfxdcjl/add"

    /**
     * 获取调查报告-按揭类-基本信息
     */
    val getDCBGAJJBXXInfo = url + "wd/ajjbxx/list"

    /**
     * 保存调查报告-按揭类-基本信息
     */
    val saveDCBGAJJBXXInfo = url + "wd/ajjbxx/add"

    /**
     * 获取调查报告-借款人条件及购房情况认定
     */
    val getDCBGJKRTJGFQKInfo = url + "wd/jkrtjgfqk/list"

    /**
     * 保存调查报告-借款人条件及购房情况认定
     */
    val saveDCBGJKRTJGFQKInfo = url + "wd/jkrtjgfqk/add"

    /**
     * 获取调查报告-首付款支付及房价核实
     */
    val getDCBGsfkzfInfo = url + "wd/sfkzf/list"

    /**
     * 保存调查报告-首付款支付及房价核实
     */
    val saveDCBGsfkzfInfo = url + "wd/sfkzf/add"

    /**
     * 获取调查报告-还款能力
     */
    val getDCBGhknlfInfo = url + "wd/hknl/list"

    /**
     * 保存调查报告-还款能力
     */
    val saveDCBGhknlfInfo = url + "wd/hknl/add"

    /**
     * 获取调查报告-抵押物状况
     */
    val getDCBGdywzkInfo = url + "wd/dywzk/list"

    /**
     * 保存调查报告-抵押物状况
     */
    val saveDCBGdywzkInfo = url + "wd/dywzk/add"

    /**
     * 获取调查报告-面谈情况
     */
    val getDCBGmtqkInfo = url + "wd/mtqk/list"

    /**
     * 保存调查报告-面谈情况
     */
    val saveDCBGmtqkInfo = url + "wd/mtqk/add"

    /**
     * 获取调查报告-调查意见
     */
    val getDCBGdcyjInfo = url + "wd/dcyj/list"

    /**
     * 保存调查报告-调查意见
     */
    val saveDCBGdcyjInfo = url + "wd/dcyj/add"

    /**
     * 检查版本
     */
    val checkVersion = url + "system/sysVersion/version"

    /**
     * 是否验证码登录
     */
    val checkCode = url + "system/config/getConfig?id=2"

    /**
     * 上传位置
     */
    val upLocation = url + "common/dwxx/location"


    /**
     * 授信申请-授信申请列表查询
     */
    val sxsqList = url + "ygxt/sxsq/list"
    val sunshineSxsqList = url + "yxxt/sunshineSxsq/list"

    /**
     * 授信申请-检测
     */
    val sxsqCheck = url + "ygxt/sxsq/check"
    val sunshineSxsqCheck = url + "yxxt/sunshineSxsq/check"

    /**
     * 授信申请-申请
     */
    val sxsqAdd = url + "ygxt/sxsq/add"
    val sunshineSxsqAdd = url + "yxxt/sunshineSxsq/add"

    /**
     * 授信申请-预添加
     */
    val sxsqPreAdd = url + "ygxt/sxsq/preAdd"
    val sunshineSxsqPreAdd = url + "yxxt/sunshineSxsq/preAdd"

    /**
     *ocr识别
     */
    val ocrUrl = url + "ygxt/common/idCard"

    /**
     *营业执照识别
     */
    val zhizhaoUrl = url + "common/bd/yyzzOCR"

    /**
     *jjd识别
     */
    val jjdUrl = url + "ygxt/common/jjd"

    /**
     *获取客户详细信息
     */
    val khInfoDetail = url + "ygxt/dkkhxx/get"
    val khInfoDetailTemporaryEdit = url + "ygxt/dkkhxx/temporaryEdit"
    val khInfoDetailEdit = url + "ygxt/dkkhxx/edit"

    /**
     *阳光用信获取客户详细信息
     */
    val sunshineKhInfoDetail = url + "yxxt/sunshineDkkhxx/get"
    val sunshineKhInfoDetailTemporaryEdit = url + "yxxt/sunshineDkkhxx/temporaryEdit"
    val sunshineKhInfoDetailEdit = url + "yxxt/sunshineDkkhxx/edit"

    /**
     *  影像资料
     */
    val getPicList = url + "ygxt/yxzlJc/get"

    /**
     *  影像资料
     */
    val savePicList = url + "ygxt/yxzlJc/save"

    /**
     *  授信申请—自然人担保-增删改查
     */
    val getListDB_ZRRDB = url + "ygxt/zrrdb/list"
    val getEditDB_ZRRDB = url + "ygxt/zrrdb/getPage"
    val getEditDB_DC_ZRRDB = url + "ygxt/zrrdb/getDcPage"
    val saveDB_ZRRDB = url + "ygxt/zrrdb/addoredit"
    val deleteDB_ZRRDB = url + "ygxt/zrrdb/delete"

    /**
     *  阳光用信—自然人担保-增删改查
     */
    val getListDB_sunshine_ZRRDB = url + "sunshine/zrrdb/list"
    val getEditDB_sunshine_ZRRDB = url + "sunshine/zrrdb/getPage"
    val saveDB_sunshine_ZRRDB = url + "sunshine/zrrdb/addoredit"
    val deleteDB_sunshine_ZRRDB = url + "sunshine/zrrdb/delete"

    /**
     *  授信申请—企业担保-增删改查
     */
    val getListDB_QYDB = url + "ygxt/qydb/list"
    val getEditDB_QYDB = url + "ygxt/qydb/getPage"
    val getEditDB_DC_QYDB = url + "ygxt/qydb/getSxdcPage"
    val saveDB_QYDB = url + "ygxt/qydb/addoredit"
    val saveDB_DC_QYDB = url + "ygxt/qydb/addoreditSxdc"
    val deleteDB_QYDB = url + "ygxt/qydb/delete"

    /**
     *  阳光用信-授信申请—企业担保-增删改查
     */
    val getListDB_sunshine_QYDB = url + "sunshine/qydb/list"
    val getEditDB_sunshine_QYDB = url + "sunshine/qydb/getPage"
    val saveDB_sunshine_QYDB = url + "sunshine/qydb/addoredit"
    val deleteDB_sunshine_QYDB = url + "sunshine/qydb/delete"

    /**
     *  授信申请—公司担保-增删改查
     */
    val getListDB_GSDB = url + "ygxt/dbgsdb/list"
    val getEditDB_GSDB = url + "ygxt/dbgsdb/getPage"
    val saveDB_GSDB = url + "ygxt/dbgsdb/addoredit"
    val deleteDB_GSDB = url + "ygxt/dbgsdb/delete"

    /**
     *  阳光用信-授信申请—公司担保-增删改查
     */
    val getListDB_sunshine_GSDB = url + "sunshine/dbgsdb/list"
    val getEditDB_sunshine_GSDB = url + "sunshine/dbgsdb/getPage"
    val saveDB_sunshine_GSDB = url + "sunshine/dbgsdb/addoredit"
    val deleteDB_sunshine_GSDB = url + "sunshine/dbgsdb/delete"

    /**
     *  授信申请—房产抵押-增删改查
     */
    val getListDB_FCDY = url + "ygxt/fcdy/list"
    val getEditDB_FCDY = url + "ygxt/fcdy/getPage"
    val getEditDB_DC_FCDY = url + "ygxt/fcdy/getDcPage"
    val saveDB_FCDY = url + "ygxt/fcdy/addoredit"
    val deleteDB_FCDY = url + "ygxt/fcdy/delete"

    /**
     *  授信申请—房产抵押-增删改查
     */
    val getListDB_sunshine_FCDY = url + "sunshine/fcdy/list"
    val getEditDB_sunshine_FCDY = url + "sunshine/fcdy/getPage"
    val saveDB_sunshine_FCDY = url + "sunshine/fcdy/addoredit"
    val deleteDB_sunshine_FCDY = url + "sunshine/fcdy/delete"

    /**
     *  授信申请—质押。国债担保-增删改查
     */
    val getListDB_ZYDB = url + "ygxt/zydb/list"
    val getEditDB_ZYDB = url + "ygxt/zydb/getPage"
    val saveDB_ZYDB = url + "ygxt/zydb/addoredit"
    val deleteDB_ZYDB = url + "ygxt/zydb/delete"

    /**
     *  授信申请—质押。国债担保-增删改查
     */
    val getListDB_sunshine_ZYDB = url + "sunshine/zydb/list"
    val getEditDB_sunshine_ZYDB = url + "sunshine/zydb/getPage"
    val saveDB_sunshine_ZYDB = url + "sunshine/zydb/addoredit"
    val deleteDB_sunshine_ZYDB = url + "sunshine/zydb/delete"

    /**
     *  授信申请—共有权利人-增删改查
     */
    val getListDB_GYQLR = url + "ygxt/gyqlr/list"
    val getEditDB_GYQLR = url + "ygxt/gyqlr/getPage"
    val saveDB_GYQLR = url + "ygxt/gyqlr/addoredit"
    val deleteDB_GYQLR = url + "ygxt/gyqlr/delete"

    /**
     *  阳光用信-共有权利人-增删改查
     */
    val getListDB_sunshine_GYQLR = url + "sunshine/gyqlr/list"
    val getEditDB_sunshine_GYQLR = url + "sunshine/gyqlr/getPage"
    val saveDB_sunshine_GYQLR = url + "sunshine/gyqlr/addoredit"
    val deleteDB_sunshine_GYQLR = url + "sunshine/gyqlr/delete"

    /**
     *  授信调查-其他担保-增删改查
     */
    val getListDB_QTDB = url + "ygxt/qtdb/get"
    val saveListDB_QTDB = url + "ygxt/qtdb/save"


    /**
     *  家庭成员关系-分页列表查询
     */
    val getFamilyList = url + "ygxt/jtcygx/list"

    /**
     *  家庭成员关系-新增自动填充
     */
    val getDskhjdxx = url + "ygxt/jtcygx/getDskhjdxx"

    /**
     *  家庭成员关系-preAdd
     */
    val getFamilyPreAdd = url + "ygxt/jtcygx/preAdd"
    val saveFamilyNumber = url + "ygxt/jtcygx/add"
    val deleteFamilyNumber = url + "ygxt/jtcygx/delete"

    /**
     *  合同签约
     */
    val get_signing_contract_list = url + "ygxt/yxglHtzb/list"
    val get_signing_contract_PreAdd = url + "ygxt/yxglHtzb/preAdd"
    val save_signing_contract = url + "ygxt/yxglHtzb/add"
    val delete_signing_contract = url + "ygxt/yxglHtzb/delete"
    val get_signing_contract_dzy_list = url + "ygxt/yxglHtfsb/listApp"
    val get_signing_contract_dzy_fs_pop = url + "ygxt/yxglHtfsb/selectDbfs"
    val save_signing_contract_dzy_fs_pop = url + "ygxt/yxglHtfsb/get"
    val save_signing_contract_dzy_pop = url + "ygxt/yxglHtfsb/save"
    val edit_signing_contract_dzy_pop = url + "ygxt/yxglHtfsb/edit"
    val delete_signing_contract_dzy_pop = url + "ygxt/yxglHtfsb/delete"
    val get_signing_contract_ht = url + "ygxt/yxglHtzb/generateContract"
    val get_getMb = url + "yxxt/htqy/getMb"

    /**
     *  授信申请单-获取
     */
    val getSXSQD = url + "ygxt/sxsqd/get"

    /**
     *  授信申请单-save
     */
    val saveSXSQD = url + "ygxt/sxsqd/add"

    /**
     *  风险探测-获取Tab选项
     */
    val getRiskTab = url + "ygxt/fxtc/getTab"

    /**
     *  风险探测-获取
     */
    val getRiskList = url + "ygxt/fxtc/get"

    /**
     * 风险探测-获取hfw
     */
    val getRiskHFW = url + "ygxt/fxtc/getHFW"

    /**
     * 征信-任务
     */
    val getList_CreditManager = url + "zx/zxrw/list"
    val getEdit_CreditManager = url + "zx/zxrw/edit"
    val save_CreditManager = url + "zx/zxrw/save"
    val delete_CreditManager = url + "zx/zxrw/delete"

    /**
     * 阳光用信 征信管理
     */
    val getList_sunshine_CreditManager = url + "yxxt/zx/sunshineZxrw/list"
    val getEdit_sunshine_CreditManager = url + "yxxt/zx/sunshineZxrw/edit"
    val save_sunshine_CreditManager = url + "yxxt/zx/sunshineZxrw/save"
    val delete_sunshine_CreditManager = url + "yxxt/zx/sunshineZxrw/delete"
    val submit_sunshine_CreditAnalysisAdd = url + "zx/zx/addSunshine"

    /**
     * 征信-任务
     */
    val get_creditManager_list = url + "yxxt/zx/zxrw/list"
    val get_creditManager_edit = url + "yxxt/zx/zxrw/edit"
    val save_creditManager = url + "yxxt/zx/zxrw/save"
    val delete_creditManager = url + "yxxt/zx/zxrw/delete"

    /**
     *  征信-任务-家庭成员
     */
    val getCreditManagerCyList = url + "zx/zxrw/zxCyList"
    val getCreditManager_sunshine_CyList = url + "yxxt/zx/sunshineZxrw/zxCyList"

    /**
     *人脸识别-校验
     */
    val getCreditManagerFace = url + "ygxt/rlsb/getauth"

    /**
     *人脸识别-校验
     */
    val get_yxLgfkRlsb_face = url + "yxxt/yxLgfkRlsb/getauth"

    /**
     *人脸识别-校验
     */
    val postCreditManagerFace = url + "ygxt/rlsb/auth"

    /**
     *人脸识别-校验
     */
    val save_yxLgfkRlsb_face = url + "yxxt/yxLgfkRlsb/auth"

    /**
     *征信-任务-证件上传-编辑
     */
    val getCreditManagerZJ = url + "zx/zxrw/zjsc/edit"
    val get_sunshine_CreditManagerZJ = url + "yxxt/zx/sunshineZxrw/zjsc/edit"

    /**
     *征信管理-任务-证件上传-编辑
     */
    val get_creditManager_zj = url + "yxxt/zx/zxrw/zjsc/edit"

    /**
     *征信-任务-证件上传-保存
     */
    val saveCreditManagerZJ = url + "zx/zxrw/zjsc/save"
    val save_sunshine_CreditManagerZJ = url + "yxxt/zx/sunshineZxrw/zjsc/save"

    /**
     *征信管理-任务-证件上传-保存
     */
    val save_creditManager_zj = url + "yxxt/zx/zxrw/zjsc/save"

    /**
     *征信-任务-授权书-查询
     */
    val getCreditManagerSQS = url + "ygxt/sqs/list"
    val saveCreditManagerSQS = url + "ygxt/sqs/add"
    val deleteCreditManagerSQS = url + "ygxt/sqs/delete"

    /**
     *阳光用信-任务-授权书-查询
     */
    val saveCreditManager_sunshine_SQS = url + "ygxt/sqs/addSunshine"

    /**
     *授信申请-我行业务-查询汇总信息
     */
    val getList_bank = url + "ygxt/whyw/hzxx"

    /**
     *授信申请-我行业务-查询明细
     */
    val getList_bankDetail = url + "ygxt/whyw/mx"

    /**
     *阳光用信-我行业务-查询汇总信息
     */
    val getList_sunshine_bank = url + "yxxt/SunshineWhyw/hzxx"
    val getList_sunshine_bankDetail = url + "yxxt/SunshineWhyw/mx"

    /**
     *授信调查-授信调查列表查询
     */
    val getList_sxdc = url + "ygxt/sxdc/list"

    /**
     *初审结果-获取
     */
    val getList_csjg = url + "ygxt/csjg/get"

    /**
     *初审结果-添加或修改
     */
    val save_csjg = url + "ygxt/csjg/add"

    /**
     *签批任务-预发起
     */
    val get_csjg_approval = url + "ygxt/sxsqQp/preAdd"

    /**
     *签批任务-预发起
     */
    val save_sunshine_approval = url + "yxxt/sunshineSxsqQp/add"

    /**
     *阳光用信-签批任务-预发起
     */
    val get_sunshine_approval = url + "yxxt/sunshineSxsqQp/preAdd"

    /**
     *阳光用信-签批任务-预发起
     */
    val save_csjg_approval = url + "ygxt/sxsqQp/add"

    /**
     * 获取授信审批列表的审批信息
     */
    val getSXSPListById = url + "ygxt/flow/getConfiguration"
    val getSXSPListById_sunshine = url + "yxxt/sunshine/flow/getConfigurationYx"

    /**
     * 保存授信审批列表的审批信息
     */
    val saveSXSP = url + "ygxt/flow/handleFlow"
    val saveSXSP_sunshine = url + "yxxt/sunshine/flow/handleFlow"

    /**
     * 保存签署意见
     */
    val setSuggestion = url + "ygxt/flow/setSuggestion"
    val getSuggestion = url + "ygxt/flow/getSuggestion"

    /**
     * 模糊查询
     */
    val searchQuery = url + "ygxt/codeValue/query"

    /**
     * 特殊模糊查询  行政机关
     */
    val searchQuery_fzjg = url + "common/area/list"

    /**
     * 你说我贷-查询
     */
    val getVodUrl = url + "ygxt/nswd/list"

    /**
     * 用心申请录音录像
     */
    val getyxLgfkLylxUrl = url + "yxxt/yxLgfkLylx/get"

    /**
     * 你说我贷-add
     */
    val saveVodUrl = url + "ygxt/nswd/add"

    /**
     * 你说我贷-add
     */
    val savelylxVodUrl = url + "yxxt/yxLylx/addVideoPath"

    /**
     * 你说我贷-add
     */
    val saveyxLgfkLylxVodUrl = url + "yxxt/yxLgfkLylx/save"

    /**
     *授信调查_实地调查-查询
     */
    val getSDDC = url + "ygxt/sddc/list"
    val saveSDDC = url + "ygxt/sddc/addoredit"
    val saveTemporary_SDDC = url + "ygxt/sddc/zancun"

    /**
     *阳光用信-_实地调查-查询
     */
    val get_sunshine_SDDC = url + "yxxt/sunshineSddc/list"
    val save_sunshine_SDDC = url + "yxxt/sunshineSddc/addoredit"
    val saveTemporary_sunshine_SDDC = url + "yxxt/sunshineSddc/zancun"

    /**
     *履历收入-查询
     */
    val getLLSR = url + "ygxt/llsr/list"

    /**
     *上下游客户-查询
     */
    val getSXYKH = url + "ygxt/sxykh/list"

    /**
     *上下游客户-add
     */
    val saveSXYKH = url + "ygxt/sxykh/addoredit"

    /**
     *授信调查_资产负债-查询
     */
    val getZCFZ = url + "ygxt/zcfz/list"

    /**
     *授信调查_损益简表-查询
     */
    val getSYJB = url + "ygxt/syjb/list"

    /**
     *授信调查_损益简表-add
     */
    val saveSYJB = url + "ygxt/syjb/addoredit"

    /**
     *评价指标-获取指标
     */
    val getPJZB = url + "ygxt/zbpz/get"

    /**
     *评价指标-获取指标
     */
    val get_preCredit_pjzb = url + "yxxt/pjzb/get"

    /**
     *信贷同步
     */
    val khglxdtb = url + "ygxt/xdtb/syncById"
    val khglxdtb_sunshine = url + "yxxt/sunshineXdtb/syncById"

    /**
     *授信审批
     */
    val getList_sxsp = url + "ygxt/sxsp/list"

    /**
     *签批任务-签批列表查询
     */
    val getList_sxqp = url + "ygxt/sxsqQp/list"
    val getList_sunshine_sxqp = url + "yxxt/sunshineSxsqQp/list"

    /**
     *流程-获取历史信息
     */
    val getList_flowHis = url + "ygxt/flow/getFlowHis"

    /**
     * 获取申请check状态
     */
    val getApplyCheck = url + "ygxt/dataStatus/getDataStatus"

    /**
     * 财务简表
     */
    val getList_cwjb = url + "ygxt/zcfz/cwlist"

    /**
     *获取行业分类
     */
    val khInfoCategory = url + "common/industry/queryTreeList"

    /**
     *获取行业分类
     */
    val khInfoZYFL = url + "common/zyfl/listTree"

    /**
     * 征信-添加征信任务
     */
    val creditAnalysisAdd = url + "zx/zx/add"

    /**
     * 获取征信审批人
     */
    val querySpUser = url + "zx/zx/querySpUser"

    /**
     * 征信-添加征信任务
     */
    val creditAnalysisAdd2 = url + "zx/zx/toShenpi"

    /**
     * 征信-查询
     */
    val creditAnalysisList = url + "zx/zx/list"

    /**
     * 获取z信审批列表
     */
    val getZXSPList = url + "zx/zx/spList"
    val getZXSP_sunshine_List = url + "zx/zx/spListSunshine"

    /**
     * 获取z贷后信审批列表
     */
    val getDHZXSPList = url + "dhxt/zx/spList"

    /**
     * 获取征信复核签字列表
     */
    val getZXFHQZList = url + "ygxt/sqs/sqsList"

    /**
     * 保存z信审批列表的审批信息
     */
    val saveZXSP = url + "zx/zx/zxrwsp"
    val saveZXSP_sunshine = url + "zx/zx/zxrwspSunshine"

    /**
     * 保存z信审批列表的审批信息
     */
    val save_visit_approval_sp = url + "yxxt/zfDrsj/spList"

    /**
     * 授信结论-获取
     */
    val getDCjl = url + "ygxt/sxjl/get"
    val saveDCJL = url + "ygxt/sxjl/add"
    val saveTemporary_DCJL = url + "ygxt/sxjl/zancun"

    /**
     * 阳光用信-授信结论-获取
     */
    val get_sunshine_DCjl = url + "yxxt/sunshineSxjl/get"
    val save_sunshine_DCJL = url + "yxxt/sunshineSxjl/add"
    val saveTemporary_sunshine_DCJL = url + "yxxt/sunshineSxjl/zancun"

    /**
     * 工商信息-分页列表查询
     */
    val getGSXX = url + "ygxt/gsxx/list"
    val get_pop_GSXX = url + "ygxt/gsxx/get"
    val save_pop_GSXX = url + "ygxt/gsxx/add"
    val getSunshineGSXX = url + "yxxt/sunshineGsxx/list"

    /**
     *征信-查看征信PDF
     */
    val getPDF = url + "zx/zx/queryZxPdf"

    /**
     *现金流
     */
    val getlistGtjkr = url + "ygxt/xjlhz/listGtjkr"

    /**
     *现金流
     */
    val getXJL = url + "ygxt/xjlhz/list"

    /**
     *授信调查_损益简表-暂存
     */
    val saveTemporary_SYJB = url + "ygxt/syjb/zancun"


    /**
     *授信调查_资产负债-引入征信
     */
    val queryZxInfo = url + "ygxt/zcfz/queryZxInfo"

    /**
     *季年检-数据采集-分页列表查询
     */
    val getJNJSJCJList = url + "dhxt/jnj/sjcjList"

    /**
     *季年检-非现场检验-分页列表查询
     */
    val getJNJJCOffList = url + "dhxt/jnj/fxcjyList"

    /**
     *季年检-现场检验-分页列表查询
     */
    val getJNJJCOnList = url + "dhxt/jnj/xcjyList"

    /**
     *季年检-数据采集-任务名称
     */
    val getJNJSJCJRWMC = url + "dhxt/rwxx/dropDownAndro"

    /**
     *走访信息概况
     */
    val get_visit_xxgk = url + "znyx/jtcygxZnyx/queryJbxxAD"
    val save_visit_xxgk = url + "znyx/jtcygxZnyx/saveJbxxAD"
    val temporary_visit_xxgk = url + "znyx/jtcygxZnyx/queryJbxx"

    /**
     *季年检-信息概况
     */
    val get_jnj_cj_personal_xxgk = url + "dhxt/xxgk/list"
    val save_jnj_cj_personal_xxgk = url + "dhxt/xxgk/edit"
    val temporary_jnj_cj_personal_xxgk = url + "dhxt/xxgk/temporaryEdit"
    val get_jnj_company_xxgk_frjgjrcy = url + "dhxt/frjgjrcy/queryXxgk"
    val save_jnj_company_xxgk_frjgjrcy = url + "dhxt/frjgjrcy/edit"
    val temporary_jnj_company_xxgk_frjgjrcy = url + "dhxt/frjgjrcy/temporaryEdit"

    /**
     *季年检-影像资料
     */
    val get_jnj_cj_personal_yxzl = url + "dhxt/dhYxzlgx/get"
    val save_jnj_cj_personal_yxzl = url + "dhxt/dhYxzlgx/save"

    /**
     *走访-影像资料
     */
    val get_visit_yxzl = url + "yxxt/zfYxzlgx/get"
    val save_visit_yxzl = url + "yxxt/zfYxzlgx/save"

    /**
     *用信管理-影像资料
     */
    val get_yxgl_yxzl = url + "ygxt/yxzlJc/getForYx"
    val save_yxgl_yxzl = url + "ygxt/yxzlJc/saveForYx"

    /**
     *阳光用信-影像资料
     */
    val get_sunshine_yxzl = url + "yxxt/sunshineYxzlJc/get"
    val save_sunshine_yxzl = url + "yxxt/sunshineYxzlJc/save"

    /**
     *季年检-现金流
     */
    val get_jnj_cj_personal_xjl = url + "dhxt/dhXJLhzFix/list"

    /**
     *季年检-家庭成员
     */
    val get_jnj_cj_personal_jtcy = url + "dhxt/jtcygxDh/list"
    val get_jnj_cj_personal_jtcy_pop = url + "dhxt/jtcygxDh/preAdd"
    val save_jnj_cj_personal_jtcy_pop = url + "dhxt/jtcygxDh/add"
    val delete_jnj_cj_personal_jtcy_pop = url + "dhxt/jtcygxDh/delete"

    /**
     *走访-家庭成员
     */
    val get_visit_jtcy = url + "znyx/jtcygxZnyx/listAD"
    val get_visit_jtcy_pop = url + "znyx/jtcygxZnyx/preAdd"
    val save_visit_jtcy_pop = url + "znyx/jtcygxZnyx/add"
    val delete_visit_jtcy_pop = url + "znyx/jtcygxZnyx/delete"

    /**
     *阳光用信-家庭成员
     */
    val get_sunshine_jtcy = url + "yxxt/SunshineJtcygx/list"
    val get_sunshine_jtcy_pop = url + "yxxt/SunshineJtcygx/preAdd"
    val save_sunshine_jtcy_pop = url + "yxxt/SunshineJtcygx/add"
    val delete_sunshine_jtcy_pop = url + "yxxt/SunshineJtcygx/delete"

    /**
     *季年检-基本信息
     */
    val get_jnj_cj_personal_jbxx = url + "dhxt/jbxxFix/getJbxxFix"
    val save_jnj_cj_personal_jbxx = url + "dhxt/jbxxFix/saveJbxxFix"

    /**
     *季年检-我行业务
     */
    val get_jnj_cj_personal_whyw = url + "dhxt/whyw/hzxx"
    val get_jnj_cj_personal_whyw_mx = url + "dhxt/whyw/mx"

    /**
     *走访-我行业务
     */
    val get_visit_whyw = url + "znyx/whywZnyx/hzxx"
    val get_visit_whyw_mx = url + "znyx/whywZnyx/hzxx"

    /**
     *季年检-风险探测
     */
    val get_jnj_cj_personal_fxtc_tab = url + "dhxt/dhFxtc/getTab"
    val get_jnj_cj_personal_fxtc_list = url + "dhxt/dhFxtc/get"
    val get_jnj_cj_personal_fxtc_hfw = url + "dhxt/dhFxtc/getHFW"

    /**
     *走访-风险探测z
     */
    val get_visit_fxtc_tab = url + "znyx/zbyxFxtc/getTab"
    val get_visit_fxtc_list = url + "znyx/zbyxFxtc/get"
    val save_visit_fxtc_list = url + "znyx/zbyxFxtc/save"
    val get_visit_fxtc_hfw = url + "znyx/zbyxFxtc/getHFW"

    /**
     *阳光用信-风险探测z
     */
    val get_sunshine_fxtc_tab = url + "yxxt/SunshineFxtc/getTab"
    val get_sunshine_fxtc_list = url + "yxxt/SunshineFxtc/get"
    val save_sunshine_fxtc_list = url + "yxxt/SunshineFxtc/save"
    val get_sunshine_fxtc_hfw = url + "yxxt/SunshineFxtc/getHFW"

    /**
     *季年检-征信管理
     */
    val get_jnj_cj_personal_zxgl_list = url + "dhxt/zxrw/list"
    val submit_jnj_cj_personal_zxgl = url + "dhxt/zx/add"

    /**
     *季年检-征信管理-授权书
     */
    val get_jnj_cj_personal_zxgl_sqs = url + "dhxt/zx/querySqxxAd"
    val save_jnj_cj_personal_zxgl_sqs = url + "dhxt/zx/saveSqxxAd"

    /**
     *季年检-征信管理-征信解析查询
     */
    val get_jnj_cj_personal_zxgl_zxjx = url + "dhxt/zx/list"

    /**
     *季年检-企业用电
     */
    val get_jnj_cj_personal_qyyd = url + "dhxt/jnjQyyd/list"

    /**
     *季年检-企业信息采集
     */
    val get_jnj_cj_personal_qyxxcj = url + "dhxt/qyxxcj/list"
    val save_jnj_cj_personal_qyxxcj = url + "dhxt/qyxxcj/add"
    val get_jnj_cj_company_qyxxcj_hfw = url + "dhxt/qyxxcj/getHfw"

    /**
     *季年检-企业员工数
     */
    val get_jnj_cj_personal_qyygs = url + "dhxt/jnjQyygs/list"

    /**
     *季年检-企业月工资
     */
    val get_jnj_cj_personal_qyygz = url + "dhxt/jnjQyygz/list"

    /**
     *季年检-非现场经验结论
     */
    val get_jnj_jc_offSite_jyjl = url + "dhxt/jnjFxcjyjl/get"
    val save_jnj_jc_offSite_jyjl = url + "dhxt/jnjFxcjyjl/add"

    /**
     *季年检-现场经验结论
     */
    val get_jnj_jc_onSite_jyjl = url + "dhxt/jnjXcjyjl/get"
    val save_jnj_jc_onSite_jyjl = url + "dhxt/jnjXcjyjl/add"

    /**
     *季年检-企业问券调查表
     */
    val get_jnj_jc_onSite_wqdc = url + "dhxt/jnjQywjdcb/get"
    val save_jnj_jc_onSite_wqdc = url + "dhxt/jnjQywjdcb/add"

    /**
     *季年检-对公现场检验
     */
    val get_jnj_jc_company_xcjyb = url + "dhxt/xcjybDg/get"
    val save_jnj_jc_company_xcjyb = url + "dhxt/xcjybDg/save"

    /**
     *季年检-对私现场检验
     */
    val get_jnj_jc_personal_xcjyb = url + "dhxt/xcjybDs/get"
    val save_jnj_jc_personal_xcjyb = url + "dhxt/xcjybDs/save"

    /**
     *季年检-法人及关键成员
     */
    val get_jnj_frjgjrcy = url + "dhxt/frjgjrcy/list"
    val get_jnj_frjgjrcy_pop = url + "dhxt/frjgjrcy/preAdd"
    val save_jnj_frjgjrcy_pop = url + "dhxt/frjgjrcy/add"
    val delete_jnj_frjgjrcy_pop = url + "dhxt/frjgjrcy/delete"

    /**
     *季年检-采集提交
     */
    val get_jnj_cj_submit = url + "dhxt/flow/submitCollect"

    /**
     *走访提交
     */
    val get_visit_submit = url + "yxxt/zfDrsj/submit"

    /**
     *季年检-会办意见提交
     */
    val save_jnj_jc_hbyj = url + "dhxt/flow/setSuggestion"

    /**
     * 季年检-获取审批列表的审批信息
     */
    val get_jnj_flowConfiguration = url + "dhxt/flow/getConfiguration"
    val save_jnj_flowConfiguration = url + "dhxt/flow/handleFlow"

    /**
     * 预授信-获取审批列表的审批信息
     */
    val get_preCredit_flowConfiguration = url + "yxxt/flow/getConfiguration"
    val save_preCredit_flowConfiguration = url + "yxxt/flow/handleFlow"

    /**
     * 用信管理审批
     */
    val get_yxgl_flowConfiguration = url + "yxgl/flow/getConfiguration"
    val save_yxgl_flowConfiguration = url + "yxgl/flow/handleFlow"

    /**
     *季年检-获取历史信息
     */
    val get_jnj_flowHis = url + "dhxt/flow/getFlowHis"

    /**
     *季年检-获取历史信息
     */
    val get_preCredit_flowHis = url + "yxxt/flow/getFlowHis"

    /**
     *用信管理-获取历史信息
     */
    val get_yxgl_flowHis = url + "yxgl/flow/getFlowHis"

    /**
     *季年检-保存风险探测
     */
    val save_jnj_fxtc = url + "dhxt/dhFxtc/save"
    val save_yg_fxtc = url + "ygxt/fxtc/save"

    /**
     *季年检-管护权移交
     */
    val get_jnj_ghq_bm = url + "dhxt/jnj/getAllDepart"
    val get_jnj_ghq_ghr = url + "dhxt/jnj/queryUsers"
    val save_jnj_ghq_ghr = url + "dhxt/jnj/updateCjrForManager"
    val save_jnj_ghq_ghr_sj = url + "dhxt/sj/updateManager"
    val save_jnj_ghq_ghr_rcj = url + "dhxt/rcjc/updateManager"

    /**
     *走访，客户分配
     */
    val get_visit_kh_ghr = url + "yxxt/zfDrsj/queryUsers"
    val save_visit_kh_ghr = url + "yxxt/zfDrsj/updateCjrForManager"
    val save_visit_ghq_ghr = url + "yxxt/zfDrsj/yjManager"

    /**
     *首检列表
     */
    val getSJList = url + "dhxt/sj/sjList"

    /**
     *日常检非现场列表
     */
    val getRCJFList = url + "dhxt/rcjc/list"

    /**
     *首检 现场经验结论
     */
    val get_sj_jyjl = url + "dhxt/sjJyjl/get"
    val save_sj_jyjl = url + "dhxt/sjJyjl/add"

    /**
     * 日常捡非现场现场经验结论
     */
    val get_rcj_jyjl = url + "dhxt/rcjcFxcjyjl/get"
    val save_rcj_jyjl = url + "dhxt/rcjcFxcjyjl/add"

    /**
     *请假
     */
    val get_main_qingjia = url + "system/leave/get"
    val save_main_qingjia = url + "system/leave/add"

    /**
     *请假审批列表
     */
    val get_main_qingjia_list = url + "system/leave/list"
    val save_main_leaveApprove = url + "system/leave/approve"
    val delete_main_leaveApprove = url + "system/leave/delete"

    /**
     *走访列表
     */
    val get_visit_list = url + "yxxt/zfDrsj/list2"
    val get_visit_rwmc = url + "yxxt/zfRwxx/dropDownApp"
    val get_visit_khjl = url + "sys/user/getUserByOrgCode"

    /**
     *检查基本信息是否完善
     */
    val get_visit_checkJbxx = url + "yxxt/zfJbxx/checkJbxx"

    /**
     *走访-基本信息
     */
    val get_visit_jbxx = url + "yxxt/zfJbxx/get"
    val save_visit_jbxx = url + "yxxt/zfJbxx/save"

    /**
     *走访-行业分类
     */
    val get_visit_hyfl = url + "common/industry/queryTreeListZf"

    /**
     *预授信列表
     */
    val get_preCredit_list = url + "yxxt/ysx/listApp"

    /**
     *签到列表
     */
    val get_clockin_list = url + "yxxt/clockin/list"
    val get_clockin_pop_new = url + "yxxt/clockin/get?type=1"
    val get_clockin_pop_edit = url + "yxxt/clockin/get?type=3"
    val get_clockin_pop_end = url + "yxxt/clockin/get?type=2"
    val save_clockin_pop_new = url + "yxxt/clockin/save?type=1"
    val save_clockin_pop_edit = url + "yxxt/clockin/save?type=3"
    val save_clockin_pop_end = url + "yxxt/clockin/save?type=2"

    /**
     * 走访 审批
     */
    val get_visit_approval_list = url + "yxxt/zfDrsj/yjListAD"

    /**
     *预授信check
     */
    val get_preCredit_new = url + "yxxt/ysx/preAdd"
    val check_preCredit_new = url + "yxxt/ysx/check"
    val save_preCredit_new = url + "yxxt/ysx/newAdd"
    val get_visit_preCredit_check = url + "yxxt/zfDrsj/ysx"
    val save_visit_preCredit_add = url + "yxxt/ysx/add"

    /**
     *预授信基本信息
     */
    val get_preCredit_jbxx = url + "yxxt/ysxJbxx/get"
    val save_preCredit_jbxx = url + "yxxt/ysxJbxx/edit"

    /**
     *预授信 经营信息
     */
    val get_preCredit_jyxx = url + "yxxt/ysxJyxx/get"
    val save_preCredit_jyxx = url + "yxxt/ysxJyxx/save"

    /**
     *预授信 信用情况
     */
    val get_preCredit_xyqk = url + "yxxt/ysxXyqk/get"
    val save_preCredit_xyqk = url + "yxxt/ysxXyqk/save"

    /**
     *预授信 额度测算
     */
    val get_preCredit_edcs = url + "yxxt/ysxEdcs/get"
    val save_preCredit_edcs = url + "yxxt/ysxEdcs/set"

    /**
     *影像资料补录提交
     */
    val get_picAdd_pop = url + "ygxt/yxzlSupplement/getPreNode?type=0"
    val save_picAdd_pop = url + "ygxt/yxzlSupplement/add"

    /**
     *影像资料补录列表
     */
    val get_picAdd_list = url + "ygxt/yxzlSupplement/listAPP"
    val end_picAdd_list = url + "ygxt/yxzlSupplement/complete"
    val finish_picAdd_list = url + "ygxt/yxzlSupplement/constraintComplete"

    /**
     *影像资料补录提交
     */
    val get_clr_yj_pop = url + "sys/user/queryUserByOrgCodeForPhjr"
    val save_clr_yj_pop = url + "ygxt/flowTurnOver/turnOver"

    /**
     *用信管理列表
     */
    val get_yxgl_list = url + "yxxt/yxgl/listApp"
    val get_yxgl_pop_new = url + "yxxt/yxgl/preAdd"
    val save_yxgl_pop_new = url + "yxxt/yxgl/add"

    /**
     *用信管理列表
     */
    val get_yxLylx_list = url + "yxxt/yxLylx/listApp"
    val get_yxLylx_pop_new = url + "yxxt/yxLylx/preAdd"
    val save_yxLylx_pop_new = url + "yxxt/yxLylx/add"
    val delete_yxLylx_pop_new = url + "yxxt/yxLylx/delete"

    /**
     *用信管理列表
     */
    val get_yxLwhc = url + "yxxt/yxLwhc/get"
    val save_yxLwhc = url + "yxxt/yxLwhc/inspect"

    /**
     *借据上传
     */
    val get_yxJjsc = url + "yxxt/yxJjsc/get"
    val save_yxJjsc = url + "yxxt/yxJjsc/save"

    /**
     *离柜放款验证单
     */
    val get_yxLgfkyzd = url + "yxxt/yxLgfkyzd/get"

    /**
     *二维码列表
     */
    val get_qrcode_list = url + "yxxt/yxQr/listApp"
    val delete_qrcode_list = url + "yxxt/yxQr/delete"
    val add_qrcode_list = url + "yxxt/yxQr/add"
    val get_qrcode_bm = url + "yxxt/yxQr/getDepart"
    val get_qrcode_ghr = url + "yxxt/yxQr/getUser"

    /**
     *受理列表
     */
    val get_shouli_list = url + "yxxt/yxLoanQr/listApp"
    val get_getDepartDropdown_pop = url + "yxxt/yxLoanQr/getDepartDropdown"
    val get_getUserDropdown_pop = url + "yxxt/yxLoanQr/getUserDropdown"
    val save_shouli_pop = url + "yxxt/yxLoanQr/turnOver"
    val save_update_time = url + "yxxt/yxLoanQr/appBusinessAccept"

    /**
     *新首检
     */
    val get_sj_jg = url + "dhxt/firstCheck/getDepart"
    val get_sj_list = url + "dhxt/firstCheck/listApp"
    val temporary_sj_list = url + "dhxt/firstCheck/saveAppTemp"
    val save_sj_jbqk = url + "dhxt/firstCheck/saveApp"
    val get_sj_jbqk = url + "dhxt/firstCheck/getApp"
    val get_sj_jcqk = url + "dhxt/firstCheck/checkInfo"
    val save_sj_jcqk = url + "dhxt/firstCheck/saveCheck"
    val get_sj_jyjl2 = url + "dhxt/firstCheck/conclusion"
    val save_sj_jyjl2 = url + "dhxt/firstCheck/saveConclusion"
    val get_sj_picList = url + "dhxt/dhImage/getApp"
    val save_sj_picList = url + "dhxt/dhImage/saveApp"
    val get_sj_flowConfiguration = url + "dhxt/new/flow/getConfiguration"
    val save_sj_flowConfiguration = url + "dhxt/new/flow/handleFlow"
    val get_sj_flowHis = url + "dhxt/new/flow/getFlowHis"
    val get_sj_flowSuggestion = url + "dhxt/new/flow/getSuggestion"
    val save_sj_flowSuggestion = url + "dhxt/new/flow/setSuggestion"

    /**
     * 首检日常检2021
     * **/
    val get_sj_list2 = url + "dhxt/dhglxtFirstCheck/listApp"
    val get_rc_list2 = url + "dhxt/dhglxtDailyCheck/listApp"
    val get_queryByIdApp = url + "dhxt/dhglxtFirstCheck/queryByIdApp"
    val save_editApp = url + "dhxt/dhglxtFirstCheck/editApp"

    val get_dhglxtDailyCheck_queryByIdApp = url + "dhxt/dhglxtDailyCheck/queryByIdApp"
    val save_dhglxtDailyCheck_editApp = url + "dhxt/dhglxtDailyCheck/editApp"

    /**
     *新日常捡
     */
    val get_rc_jg = url + "dhxt/firstCheck/getDepart"
    val get_rc_list = url + "dhxt/dailyCheck/listApp"
    val get_rc_jbqk = url + "dhxt/dailyCheck/getApp"
    val temporary_rc_list = url + "dhxt/dailyCheck/saveAppTemp"
    val save_rc_jbqk = url + "dhxt/dailyCheck/saveApp"
    val get_rc_batchFlag = url + "dhxt/dailyCheck/batchFlag"

    /**
     *阳光预授信
     */
    val get_sunCredit_listApp = url + "yxxt/sunCredit/listApp"
    val save_sunCredit = url + "yxxt/sunCredit/add"

    val get_sunCredit = url + "yxxt/sunCredit/get"
    /**
     *扶贫走饭
     */
    val get_fpf_listApp = url + "yxxt/fpf/listApp"
    val save_fpf = url + "yxxt/fpf/add"

    val get_fpf = url + "yxxt/fpf/preAdd"
    val get_fpf_listJtApp = url + "yxxt/fpf/listJtApp"
    val get_fpf_getHxx = url + "yxxt/fpf/getHxx"
    val get_fpf_geteditHxx = url + "yxxt/fpf/editHxx"
    val get_fpf_getJtxx = url + "yxxt/fpf/getJtxx"
    val save_fpf_addHxx = url + "yxxt/fpf/addJtxx"
    val save_fpf_delJtxx = url + "yxxt/fpf/delJtxx"
    val get_fpf_getZFxx = url + "yxxt/fpf/getZFxx"
    val save_fpf_getZFxx = url + "yxxt/fpf/editZfxx"



    /**
     * 获取调查报告
     */
    val get_sxdc_dcbg = url + "ygxt/dcbg/get"
    val getDCBGPREVIEW = url + "ygxt/dcbg/preview"


    /**
     * 授信审批，流程更改
     */
    val get_sxsp_changeFlow = url + "ygxt/flow/changeFlow"


    /**
     *季年检用信列表
     */
    val get_jnj_yx_list = url + "dhxt/yearCheckJbxx/listApp"
    val get_jnj_yx_zxgl_list = url + "dhxt/zxrw/list"
    val get_jnj_yx_jbxx = url + "dhxt/yearCheckJbxx/getApp"
    val save_jnj_yx_jbxx = url + "dhxt/yearCheckJbxx/saveApp"
    val temporary_jnj_yx_jbxx = url + "dhxt/yearCheckJbxx/saveAppTemp"
    val dhxtnewFlow = url + "dhxt/new/flow/getConfigurationBatch"
    val handleFlowBatchFlow = url + "dhxt/new/flow/handleFlowBatch"


    /**
     *信息員
     */
    val get_xxy_list = url + "yxxt/sunshineXxyb/listApp"
    val get_xxy_pop = url + "yxxt/sunshineXxyb/get"
    val save_xxy_pop = url + "yxxt/sunshineXxyb/save"
    val delete_xxy_pop = url + "yxxt/sunshineXxyb/delete"
    val get_xxy_flowConfiguration = url + "yxxt/sunshine/flow/getConfiguration"
    val save_xxy_flowConfiguration = url + "yxxt/sunshine/flow/handleFlow"

    /**
     *阳光预授信任务
     */
    val get_ygysxrw_list = url + "yxxt/sunshineTask/listApp"
    val remake_ygysxrw = url + "yxxt/sunshineTask/remake"
    val delete_ygysxrw = url + "yxxt/sunshineTask/delete"
    val get_ygysxrw_rw = url + "yxxt/sunshineTask/dropDown"
    val get_ygysxrw_pop = url + "yxxt/sunshineTask/get"
    val save_ygysxrw_pop = url + "yxxt/sunshineTask/save"

    /**
     *问卷调查
     */
    val get_wjdc_rw = url + "yxxt/sunshineTask/dropDownForWjdc"//问卷调查 - 任务
    val get_wjdc_list = url + "yxxt/sunshineWjdcb/list" //问卷调查 -  列表
    val new_wjdc_list = url + "yxxt/sunshineWjdcb/init" //问卷调查 - 生成问卷
    val delete_wjdc_list = url + "yxxt/sunshineWjdcb/delete" //问卷调查 - 删除问卷
    val get_wjdc_xxy = url + "yxxt/sunshineXxybImage/get" //问卷调查 - 点击开始问卷-信息员照片
    val save_wjdc_xxy = url + "yxxt/sunshineXxybImage/save" //问卷调查 - 点击开始问卷-信息员照片
    val get_wjdc_h_list = url + "yxxt/sunshineWjdcb/question" //问卷调查 - 问卷列表
    val get_wjdc_hz = url + "yxxt/sunshineWjdcb/collect" //问卷调查 - 问卷列表

    /**
     *授信评议
     */
    val get_sxpy_rw = url + "yxxt/sunshineTask/dropDownForSxpy"//授信评议 - 任务
    val get_sxpy_list = url + "yxxt/sunshineSxpyb/listApp" //授信评议 -  列表
    val new_sxpy_list = url + "yxxt/sunshineSxpyb/init" //授信评议 - 生成评议
    val delete_sxpy_list = url + "yxxt/sunshineWjdcb/delete" //授信评议 - 删除评议
    val get_sxpy_xxy = url + "yxxt/sunshineXxybImage/getSxsp" //授信评议 - 点击开始评议-信息员照片
    val save_sxpy_xxy = url + "yxxt/sunshineXxybImage/saveSxsp" //授信评议 - 点击开始评议-信息员照片
    val get_sxpy_h_list = url + "yxxt/sunshineSxpyb/appraise" //授信评议 - 评议列表
    val get_sxpy_hz = url + "yxxt/sunshineWjdcb/collect" //授信评议 - 评议列表
    val get_sxpy_qz = url + "yxxt/sunshineSign/getSxpy" //授信评议 - 评议签字
    val save_sxpy_qz = url + "yxxt/sunshineSign/saveSxpy" //授信评议 - 评议签字
    val get_sxpy_db_list = url + "yxxt/sunshineSxbdb/listApp" //授信评议 - 评议对比列表
    val get_sxpy_db_pop = url + "yxxt/sunshineSxbdb/get" //授信评议 - 获取
    val save_sxpy_db_pop = url + "yxxt/sunshineSxbdb/save" //授信评议 - 存储

    /**
     *额度对比
     */
    val get_eddb_list = url + "yxxt/sunshineSxbdb/edbdListApp" //授信评议 -  列表
    val get_eddb_qz = url + "yxxt/sunshineSign/getEdbd" //授信评议 -  签字
    val save_eddb_qz = url + "yxxt/sunshineSign/saveEdbd" //授信评议 -签字

    /**
     * 合同签约列表查询
     */
    val htqy_sxlisApp = url + "yxxt/htqy/sxlisApp"
    val htqy_listApp = url + "yxxt/htqy/listApp"
    val htqy_add = url + "yxxt/htqy/add"
    val htqy_get = url + "yxxt/htqy/get"
    val htqy_delete = url + "yxxt/htqy/delete"
    val htqy_previewApp = url + "yxxt/htqy/previewApp"
    val getJtxx = url + "ygxt/whyw/jtxx"


}