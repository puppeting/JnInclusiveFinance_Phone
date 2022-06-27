package com.inclusive.finance.jh

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.inclusive.finance.jh.bean.model.ApplyModel

object IRouter {
    fun go(context: Any?, url: String, vararg bean: Any? = emptyArray()) {
        val aRouter = ARouter.getInstance().build(url)
        aRouter.navigation()
    }

    fun goF(view: View, action: String? = "", vararg bean: Any? = emptyArray()) {
        val actionId = when (action) {
            "action_to_applyActivity" -> {
                R.id.action_to_applyActivity
            }
            "action_to_applyList" -> {
                R.id.action_to_applyList
            }
            "action_to_investigationList" -> {
                R.id.action_to_investigationList
            }
            "action_to_creditApproval" -> {
                R.id.action_to_creditApproval
            }
            "action_to_creditApproval2" -> {
                R.id.action_to_creditApproval2
            }
            "action_to_creditApproval3" -> {
                R.id.action_to_creditApproval3
            }
            "action_to_SignApproval" -> {
                R.id.action_to_SignApproval
            }
            "action_to_creditListFragment" -> {
                R.id.action_to_creditListFragment
            }
            "action_to_jiNianJianCaiJiListFragment" -> {
                R.id.action_to_jiNianJianCaiJiListFragment
            }
            "action_to_JiNianJianYongXinListFragment" -> {
                R.id.action_to_JiNianJianYongXinListFragment
            }
            "action_to_feiXianchangJianYanListFragment" -> {
                R.id.action_to_feiXianchangJianYanListFragment
            }
            "action_to_XianchangJianYanListFragment" -> {
                R.id.action_to_XianchangJianYanListFragment
            }
            "action_to_dh_creditApproval2" -> {
                R.id.action_to_dh_creditApproval2
            }
            "action_to_ShouJianListFragment" -> {
                R.id.action_to_ShouJianListFragment
            }
            "action_to_RiChangXianchangJianYanListFragment" -> {
                R.id.action_to_RiChangXianchangJianYanListFragment
            }
            "action_to_RiChangFeiXianchangJianYanListFragment" -> {
                R.id.action_to_RiChangFeiXianchangJianYanListFragment
            }
            "action_to_RiChangJianYanListFragment" -> {
                R.id.action_to_RiChangJianYanListFragment
            }
            "action_to_FuPinListFragment" -> {
                R.id.action_to_FuPinListFragment
            }
            "action_to_YuShouXinListFragment" -> {
                R.id.action_to_YuShouXinListFragment
            }
            "action_to_LeaveApprovalListFragment" -> {
                R.id.action_to_LeaveApprovalListFragment
            }
            "action_to_VisitListFragment" -> {
                R.id.action_to_VisitListFragment
            }
            "action_to_PreCreditFragment" -> {
                R.id.action_to_PreCreditFragment
            }
            "action_to_SunPreCreditTaskFragment" -> {
                R.id.action_to_SunPreCreditTaskFragment
            }
            "action_to_VisitApprovalFragment" -> {
                R.id.action_to_VisitApprovalFragment
            }
            "action_to_ClockInListFragment" -> {
                R.id.action_to_ClockInListFragment
            }
            "action_to_TrackFragment" -> {
                R.id.action_to_TrackFragment
            }
            "action_to_PointClusterFragment" -> {
                R.id.action_to_PointClusterFragment
            }
            "action_to_PicAddListFragment" -> {
                R.id.action_to_PicAddListFragment
            }
            "action_to_YongXinGuanLiListFragment" -> {
                R.id.action_to_YongXinGuanLiListFragment
            }
            "action_to_QRCodeListFragment" -> {
                R.id.action_to_QRCodeListFragment
            }
            "action_to_ShouLiListFragment" -> {
                R.id.action_to_ShouLiListFragment
            }
            "action_to_XinXiYuanListFragment" -> {
                R.id.action_to_XinXiYuanListFragment
            }
            "action_to_WenJuanDiaoChaListFragment" -> {
                R.id.action_to_WenJuanDiaoChaListFragment
            }
            "action_to_ShouXinPingYiListFragment" -> {
                R.id.action_to_ShouXinPingYiListFragment
            }
            "action_to_EDuBiDuiListFragment" -> {
                R.id.action_to_EDuBiDuiListFragment
            }
            "action_to_SunShine_applyList" -> {
                R.id.action_to_SunShine_applyList
            }
            "action_to_sunshineSignApproval" -> {
                R.id.action_to_sunshineSignApproval
            }
            "action_to_sunshineCreditApproval" -> {
                R.id.action_to_sunshineCreditApproval
            }
            "action_to_RiChangJianYanListFragment2" -> {
                R.id.action_to_RiChangJianYanListFragment2
            }
            "action_to_ShouJianListFragment2" -> {
                R.id.action_to_ShouJianListFragment2
            }
            "action_to_contractFragment" -> {
                R.id.action_to_contractFragment
            }
            else -> {
                null
            }
        }
        goF(view, actionId, *bean)
    }

    fun goF(view: View, @IdRes action: Int? = null, vararg bean: Any? = emptyArray()) {
        val bundle = Bundle()
        val findNavController = Navigation.findNavController(view)
        when (action) {
            R.id.action_to_applyActivity -> {
                bean.forEachIndexed { index, any ->
                    when (index) {
                        0 -> {
                            bundle.putString("creditId", any.toString())
                            bundle.putString("dhId", any.toString())
                            bundle.putString("zfId", any.toString())
                            bundle.putString("ysxId", any.toString())
                            bundle.putString("keyId", any.toString())
                        }
                        1 -> {
                            bundle.putInt("businessType", any as Int)
                        }
                        2 -> {
                            bundle.putBoolean("seeOnly", any as Boolean)
                        }
                    }
                }


            }
            R.id.action_to_navActivity -> {
                bean.forEachIndexed { index, any ->
                    when (index) {
                        0 -> {
                            bundle.putString("title", any.toString())
                        }
                        1 -> {
                            bundle.putString("creditId", any.toString())
                            bundle.putString("dhId", any.toString())
                            bundle.putString("zfId", any.toString())
                            bundle.putString("ysxId", any.toString())
                            bundle.putString("keyId", any.toString())
                        }
                        2 -> {
                            if (any.toString()!="") {
                                bundle.putString("jsonObject", Gson().toJson(any as JsonObject))
                            }
                        }
                        3 -> {
                            bundle.putInt("businessType", any as Int)
                        }
                        4 -> {
                            bundle.putBoolean("seeOnly", any as Boolean)
                        }
                    }
                }


            }

            R.id.action_to_creditApproval -> {
                bundle.putInt("businessType", ApplyModel.BUSINESS_TYPE_SXSP)
            }

            R.id.action_to_SignApproval -> {
                bundle.putInt("businessType", ApplyModel.BUSINESS_TYPE_QPLC)
            }

            R.id.action_to_dh_creditApproval2 -> {
                bundle.putInt("businessType", ApplyModel.BUSINESS_TYPE_DH_ZXSP)
            }
            R.id.action_to_creditApproval2 -> {
                bundle.putInt("businessType", ApplyModel.BUSINESS_TYPE_ZXSP)
            }
            R.id.action_to_SunShine_applyList -> {
                bundle.putInt("businessType", ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY)
            }
            R.id.action_to_sunshineCreditApproval -> {
                bundle.putInt("businessType", ApplyModel.BUSINESS_TYPE_SUNSHINE_ZXSP)
            }

            R.id.action_to_sunshineSignApproval -> {
                bundle.putInt("businessType", ApplyModel.BUSINESS_TYPE_SUNSHINE_QPLC)
            }

        }
        if (action != null) findNavController.navigate(action, bundle)
    }

    fun goF(fragment: Fragment, @IdRes action: Int? = null, isPopUpTo: Boolean, inclusive: Boolean = true, vararg bean: Any?) {
        val bundle = Bundle()
        val findNavController = NavHostFragment.findNavController(fragment)
        if (action != null) if (isPopUpTo) {
            findNavController.navigate(action, bundle, NavOptions.Builder()
                .setPopUpTo(action, inclusive).build())
        } else {
            findNavController.navigate(action)
        }

    }

    //    fun getMenu(): HashMap<String, MenuBean> {
    //        val map = HashMap<String, MenuBean>()
    //
    //        map["授信全流程"] = MenuBean("授信全流程")
    //        map["授信全流程"]?.mList?.add(MenuBean("授信申请", action = R.id.shouXinShenQingFragment, isSelect = true))
    //        map["授信全流程"]?.mList?.add(MenuBean("授信调查", action = R.id.shouXinDiaoChaFragment))
    //        map["授信全流程"]?.mList?.add(MenuBean("管护权移交", action = R.id.guanHuQuanYiJiaoFragment))
    //        map["授信全流程"]?.mList?.add(MenuBean("管护权移交审批", type = "管护权移交审批", action = R.id.guanHuQuanYiJiaoFragment))
    //        map["授信全流程"]?.mList?.add(MenuBean("授信审批", action = R.id.shouXinShenPiFragment))
    //        map["授信全流程"]?.mList?.add(MenuBean("征信审批", action = R.id.zhengXinShenPiFragment))
    //
    //        map["授信申请"] = MenuBean("授信申请")
    //        map["授信申请"]?.mList?.add(MenuBean("家庭信息", action = R.id.familyInfoFragment, isSelect = true))
    //        map["授信申请"]?.mList?.add(MenuBean("我行数据", action = R.id.myDataDcFragment))
    //        map["授信申请"]?.mList?.add(MenuBean("人脸识别", action = R.id.faceRecognitionFragment))
    //        map["授信申请"]?.mList?.add(MenuBean("授信申请单", action = R.id.sxsqDanQianziFragment))
    //        map["授信申请"]?.mList?.add(MenuBean("现金流", action = R.id.dcMoneyLiuFragment))
    //        map["授信申请"]?.mList?.add(MenuBean("征信授权书", action = R.id.shouQuanShuFragment))
    //        map["授信申请"]?.mList?.add(MenuBean("影像资料", action = R.id.videoMaterialFragment))
    //        map["授信申请"]?.mList?.add(MenuBean("征信查询", action = R.id.creditInquiryFragment))
    //        map["授信申请"]?.mList?.add(MenuBean("汇法网查询", action = R.id.hfwInformationFragment))
    //        map["授信申请"]?.mList?.add(MenuBean("基本信息", action = R.id.basicInformationFragment))
    //        map["授信申请"]?.mList?.add(MenuBean("担保抵押", action = R.id.diyadanbaoFragment))
    //        map["授信申请"]?.mList?.add(MenuBean("初审结果", action = R.id.chuShenFragment))
    //
    //        map["授信调查"] = (MenuBean("授信调查"))
    //        map["授信调查"]?.map?.set("简化经营", ArrayList())
    //        map["授信调查"]?.map?.get("简化经营")?.add(MenuBean("家庭信息", type = "简化经营", action = R.id.familyInfoFragment, isSelect = true))
    //        map["授信调查"]?.map?.get("简化经营")?.add(MenuBean("基本信息", type = "简化经营", action = R.id.basicInformationDcFragment))
    //        map["授信调查"]?.map?.get("简化经营")?.add(MenuBean("系统数据", type = "简化经营", action = R.id.myDataDcFragment))
    //        map["授信调查"]?.map?.get("简化经营")?.add(MenuBean("征信查询", type = "简化经营", action = R.id.creditInquiryFragment))
    //        map["授信调查"]?.map?.get("简化经营")?.add(MenuBean("汇法网查询", type = "简化经营", action = R.id.hfwInformationFragment))
    //        map["授信调查"]?.map?.get("简化经营")?.add(MenuBean("客户评级", type = "简化经营", action = R.id.dcSddcFragment))
    //        map["授信调查"]?.map?.get("简化经营")?.add(MenuBean("资产负债", type = "简化经营", action = R.id.dcZCFZFragment))
    //        map["授信调查"]?.map?.get("简化经营")?.add(MenuBean("担保抵押", type = "简化经营", action = R.id.diyadanbaoFragment))
    //        map["授信调查"]?.map?.get("简化经营")?.add(MenuBean("现金流", type = "简化经营", action = R.id.dcMoneyLiuFragment))
    //        map["授信调查"]?.map?.get("简化经营")?.add(MenuBean("影像资料", type = "简化经营", action = R.id.videoMaterialFragment))
    ////        map["授信调查"]?.map?.get("简化经营")?.add(MenuBean("评级指标", type = "简化经营", action = R.id.dcPjzbFragment))
    //        map["授信调查"]?.map?.get("简化经营")?.add(MenuBean("授信调查", type = "简化经营", action = R.id.shouXinDiaoChaBiaoFragment))
    //        map["授信调查"]?.map?.get("简化经营")?.add(MenuBean("授信结论", type = "简化经营", action = R.id.sxdcInfoDcFragment))
    //        map["授信调查"]?.map?.set("工薪类", ArrayList())
    //        map["授信调查"]?.map?.get("工薪类")?.add(MenuBean("家庭信息", type = "工薪类", action = R.id.familyInfoFragment, isSelect = true))
    ////        map["授信调查"]?.map?.get("工薪类")?.add(MenuBean("基本信息", type = "工薪类", action = R.id.basicInformationDcFragment))
    //        map["授信调查"]?.map?.get("工薪类")?.add(MenuBean("我行数据", type = "工薪类", action = R.id.myDataDcFragment))
    //        map["授信调查"]?.map?.get("工薪类")?.add(MenuBean("征信查询", type = "工薪类", action = R.id.creditInquiryFragment))
    //        map["授信调查"]?.map?.get("工薪类")?.add(MenuBean("汇法网查询", type = "工薪类", action = R.id.hfwInformationFragment))
    ////        map["授信调查"]?.map?.get("工薪类")?.add(MenuBean("年收入情况", type = "工薪类", action = R.id.dcnsrFragment))
    //        map["授信调查"]?.map?.get("工薪类")?.add(MenuBean("客户评级", type = "工薪类", action = R.id.dcSddcFragment))
    //        map["授信调查"]?.map?.get("工薪类")?.add(MenuBean("现金流", type = "工薪类", action = R.id.dcMoneyLiuFragment))
    //        map["授信调查"]?.map?.get("工薪类")?.add(MenuBean("资产负债", type = "工薪类", action = R.id.dcZCFZFragment))
    //        map["授信调查"]?.map?.get("工薪类")?.add(MenuBean("担保抵押", type = "工薪类", action = R.id.diyadanbaoFragment))
    //        map["授信调查"]?.map?.get("工薪类")?.add(MenuBean("授信调查审批表", type = "工薪类", action = R.id.shouXinDiaoChaBiaoFragment))
    //        map["授信调查"]?.map?.get("工薪类")?.add(MenuBean("授信结论", type = "工薪类", action = R.id.sxdcInfoDcFragment))
    //        map["授信调查"]?.map?.get("工薪类")?.add(MenuBean("影像资料", type = "工薪类", action = R.id.videoMaterialFragment))
    ////        map["授信调查"]?.map?.get("工薪类")?.add(MenuBean("评级指标", type = "工薪类", action = R.id.dcPjzbFragment))
    //        map["授信调查"]?.map?.set("经营", ArrayList())
    //        map["授信调查"]?.map?.get("经营")?.add(MenuBean("家庭信息", type = "经营", action = R.id.familyInfoFragment, isSelect = true))
    //        map["授信调查"]?.map?.get("经营")?.add(MenuBean("基本信息", type = "经营", action = R.id.basicInformationDcFragment))
    //        map["授信调查"]?.map?.get("经营")?.add(MenuBean("系统数据", type = "经营", action = R.id.myDataDcFragment))
    //        map["授信调查"]?.map?.get("经营")?.add(MenuBean("征信查询", type = "经营", action = R.id.creditInquiryFragment))
    //        map["授信调查"]?.map?.get("经营")?.add(MenuBean("汇法网查询", type = "经营", action = R.id.hfwInformationFragment))
    //        map["授信调查"]?.map?.get("经营")?.add(MenuBean("客户评级", type = "经营", action = R.id.dcSddcFragment))
    //        map["授信调查"]?.map?.get("经营")?.add(MenuBean("上下游客户", type = "经营", action = R.id.sxykhFragment))
    //        map["授信调查"]?.map?.get("经营")?.add(MenuBean("现金流", type = "经营", action = R.id.dcMoneyLiuFragment))
    //        map["授信调查"]?.map?.get("经营")?.add(MenuBean("损益简表", type = "经营", action = R.id.dcSyjbFragment))
    //        map["授信调查"]?.map?.get("经营")?.add(MenuBean("财务简表", type = "经营", action = R.id.dcCwjbFragment))
    //        map["授信调查"]?.map?.get("经营")?.add(MenuBean("担保抵押", type = "经营", action = R.id.diyadanbaoFragment))
    //        map["授信调查"]?.map?.get("经营")?.add(MenuBean("影像资料", type = "经营", action = R.id.videoMaterialFragment))
    ////        map["授信调查"]?.map?.get("经营")?.add(MenuBean("评级指标",type = "经营", action = R.id.dcPjzbFragment))
    //        map["授信调查"]?.map?.get("经营")?.add(MenuBean("授信调查", type = "经营", action = R.id.shouXinDiaoChaBiaoFragment))
    //        map["授信调查"]?.map?.get("经营")?.add(MenuBean("授信结论", type = "经营", action = R.id.sxdcInfoDcFragment))
    //        map["授信调查"]?.map?.set("农户", ArrayList())
    //        map["授信调查"]?.map?.get("农户")?.add(MenuBean("家庭信息", type = "农户", action = R.id.familyInfoFragment, isSelect = true))
    //        map["授信调查"]?.map?.get("农户")?.add(MenuBean("基本信息", type = "农户", action = R.id.basicInformationDcFragment))
    //        map["授信调查"]?.map?.get("农户")?.add(MenuBean("系统数据", type = "农户", action = R.id.myDataDcFragment))
    //        map["授信调查"]?.map?.get("农户")?.add(MenuBean("征信查询", type = "农户", action = R.id.creditInquiryFragment))
    //        map["授信调查"]?.map?.get("农户")?.add(MenuBean("汇法网查询", type = "农户", action = R.id.hfwInformationFragment))
    //        map["授信调查"]?.map?.get("农户")?.add(MenuBean("客户评级", type = "农户", action = R.id.dcSddcFragment))
    //        map["授信调查"]?.map?.get("农户")?.add(MenuBean("种植、养殖户", type = "农户", action = R.id.dcZYFragment))
    //        map["授信调查"]?.map?.get("农户")?.add(MenuBean("担保抵押", type = "农户", action = R.id.diyadanbaoFragment))
    //        map["授信调查"]?.map?.get("农户")?.add(MenuBean("现金流", type = "农户", action = R.id.dcMoneyLiuFragment))
    //        map["授信调查"]?.map?.get("农户")?.add(MenuBean("影像资料", type = "农户", action = R.id.videoMaterialFragment))
    ////        map["授信调查"]?.map?.get("农户")?.add(MenuBean("评级指标",type = "农户",  action = R.id.dcPjzbFragment))
    //        map["授信调查"]?.map?.get("农户")?.add(MenuBean("授信调查", type = "农户", action = R.id.shouXinDiaoChaBiaoFragment))
    //        map["授信调查"]?.map?.get("农户")?.add(MenuBean("授信结论", type = "农户", action = R.id.sxdcInfoDcFragment))
    //
    //        map["授信审批"] = (MenuBean("授信审批"))
    //
    //        map["待办事项"] = (MenuBean("待办事项"))
    //
    //        map["现场检验"] = (MenuBean("现场检验"))
    //        map["现场检验"]?.mList?.add(MenuBean("基本信息"))
    //        map["现场检验"]?.mList?.add(MenuBean("系统数据"))
    //        map["现场检验"]?.mList?.add(MenuBean("征信查询"))
    //        map["现场检验"]?.mList?.add(MenuBean("汇法网查询"))
    //        map["现场检验"]?.mList?.add(MenuBean("现金流"))
    //        map["现场检验"]?.mList?.add(MenuBean("影像资料"))
    //        map["现场检验"]?.mList?.add(MenuBean("分析结论"))
    //
    //        map["数据采集"] = (MenuBean("数据采集"))
    //        map["数据采集"]?.mList?.add(MenuBean("基本信息"))
    //        map["数据采集"]?.mList?.add(MenuBean("系统数据"))
    //        map["数据采集"]?.mList?.add(MenuBean("征信查询"))
    //        map["数据采集"]?.mList?.add(MenuBean("汇法网查询"))
    //        map["数据采集"]?.mList?.add(MenuBean("分析结论"))
    //
    //        map["小组任务认领"] = (MenuBean("小组任务认领"))
    //
    //        map["审批"] = (MenuBean("审批"))
    //
    //        map["首贷检查"] = (MenuBean("首贷检查"))
    //        map["首贷检查"]?.mList?.add(MenuBean("基本信息"))
    //        map["首贷检查"]?.mList?.add(MenuBean("现金流"))
    //        map["首贷检查"]?.mList?.add(MenuBean("影像资料"))
    //        map["首贷检查"]?.mList?.add(MenuBean("结论"))
    //
    //        map["个贷检查"] = (MenuBean("个贷检查"))
    //        map["个贷检查"]?.mList?.add(MenuBean("基本信息"))
    //        map["个贷检查"]?.mList?.add(MenuBean("征信查询"))
    //        map["个贷检查"]?.mList?.add(MenuBean("汇法网查询"))
    //        map["个贷检查"]?.mList?.add(MenuBean("现金流"))
    //        map["个贷检查"]?.mList?.add(MenuBean("影像资料"))
    //        map["个贷检查"]?.mList?.add(MenuBean("检查结果"))
    //
    //        map["对公贷款检查"] = (MenuBean("对公贷款检查"))
    //        map["对公贷款检查"]?.mList?.add(MenuBean("基本信息"))
    //        map["对公贷款检查"]?.mList?.add(MenuBean("征信查询"))
    //        map["对公贷款检查"]?.mList?.add(MenuBean("汇法网查询"))
    //        map["对公贷款检查"]?.mList?.add(MenuBean("现金流"))
    //        map["对公贷款检查"]?.mList?.add(MenuBean("影像资料"))
    //        map["对公贷款检查"]?.mList?.add(MenuBean("检查结果"))
    //
    //        return map
    //    }
}