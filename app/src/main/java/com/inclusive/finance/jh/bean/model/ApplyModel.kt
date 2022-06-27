package com.inclusive.finance.jh.bean.model

import androidx.lifecycle.ViewModel
import com.inclusive.finance.jh.bean.ApplyCheckBean
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class ApplyModel @Inject constructor(): ViewModel() {
    var applyCheckBean: ApplyCheckBean? = ApplyCheckBean()
    var jsonObject: String? = null //某个被选中的条目
    var creditId: String? = null
    var keyId: String? = null
    var dhId: String? = null
    var zfId: String? = null
    var ysxId: String? = null
    var seeOnly: Boolean? = false
    var businessType = BUSINESS_TYPE_APPLY
    var title: String? = "" //同一页面不同内容，刷新时使用

    companion object {
        const val BUSINESS_TYPE_APPLY = 10 //授信申请
        const val BUSINESS_TYPE_INVESTIGATE = 20 //授信调查
        const val BUSINESS_TYPE_PICADD = 21 //影像补录
        const val BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE = 22 //授信模型-简化
        const val BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE = 23 //授信模型-经营类
        const val BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE = 24 //授信模型-消费类
        const val BUSINESS_TYPE_ZXSP = 30 //征信审批
        const val BUSINESS_TYPE_DH_ZXSP = 31 //贷后征信审批
        const val BUSINESS_TYPE_SXSP = 32 //授信审批
        const val BUSINESS_TYPE_QPLC = 33 //签批流程
        const val BUSINESS_TYPE_ZXFHQZ = 40 //征信复核签字
        const val BUSINESS_TYPE_JNJ_CJ_PERSONAL = 50 //季年检采集-个人
        const val BUSINESS_TYPE_JNJ_YX = 51 //季年检 用信
        const val BUSINESS_TYPE_JNJ_YX_SUBMIT = 52 //季年检 用信 提交-新增flag=2
        const val BUSINESS_TYPE_JNJ_CJ_COMPANY = 60//季年检采集-企业
        const val BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL = 70 //季年检检查-非现场-个人
        const val BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL = 80 //季年检检查-现场-个人
        const val BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY = 90 //季年检检查-现场-企业
        const val BUSINESS_TYPE_SJ = 100 //首检
        const val BUSINESS_TYPE_SJ_PERSONAL = 101 //首检-个人
        const val BUSINESS_TYPE_SJ_COMPANY = 110 //首检-企业
        const val BUSINESS_TYPE_RC = 120 //日常检查
        const val BUSINESS_TYPE_RC_OFF_SITE_PERSONAL = 121 //日常检查非现场-个人
        const val BUSINESS_TYPE_RC_ON_SITE_PERSONAL = 130 //日常检查现场-企业
        const val BUSINESS_TYPE_RC_ON_SITE_COMPANY = 140 //日常检查现场-企业
        const val BUSINESS_TYPE_VISIT = 150 //走访
        const val BUSINESS_TYPE_VISIT_NEW = 160 //走访新增
        const val BUSINESS_TYPE_VISIT_EDIT = 170 //走访修改
        const val BUSINESS_TYPE_PRECREDIT = 180 //预授信
        const val BUSINESS_TYPE_VISIT_Approval = 190 //走访签批
        const val BUSINESS_TYPE_CREDIT_MANAGER = 200 //用信管理
        const val BUSINESS_TYPE_CREDIT_MANAGER_LGFK = 201 //用信管理_离柜放款
        const val BUSINESS_TYPE_CREDIT_MANAGER_ZXGL = 202 //用信管理_征信管理
        const val BUSINESS_TYPE_QRCODE = 210 //二维码列表
        const val BUSINESS_TYPE_SHOLI = 220 //受理列表
        const val BUSINESS_TYPE_INFORMATION_OFFICER = 230 //信息员列表
        const val BUSINESS_TYPE_QUESTIONNAIRE = 240 //问卷调查
        const val BUSINESS_TYPE_CREDIT_REVIEW = 250 //授信评议
        const val BUSINESS_TYPE_COMPARISON_OF_QUOTAS = 260 //额度对比
        const val BUSINESS_TYPE_SUNSHINE_APPLY = 270 //阳光用信申请
        const val BUSINESS_TYPE_SUNSHINE_QPLC = 280  //阳光用信签批流程
        const val BUSINESS_TYPE_SUNSHINE_ZXSP = 290  //阳光用信征信审批
        const val BUSINESS_TYPE_RC2 = 300 //日常检查 2021年
        const val BUSINESS_TYPE_SJ2 = 400 //首检 2021年
        const val BUSINESS_TYPE_HTQY = 500 //合同签约
        const val BUSINESS_TYPE_FUPIN = 600 //扶贫走访

    }
}