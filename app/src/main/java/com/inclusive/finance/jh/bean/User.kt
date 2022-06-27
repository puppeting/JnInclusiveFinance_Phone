package com.inclusive.finance.jh.bean

import com.inclusive.finance.jh.config.Urls

/**
 * Created by 史忠文
 * on 2017/10/17.
 */
open class User(private var id: String = "") : AbsUser() {
    override val userId: String = ""
        get() = if (!userInfo?.id.isNullOrEmpty()) userInfo?.id.toString() else field
    override val screenRecordeAble: Boolean =false
    var telephone: String? = "12834834834"   //用户手机号

    val userName: String?  // 姓名
        get() = userInfo?.realname
    val headUrl: String?
        //头像的url
        get() {
            return Urls.url + userInfo?.avatar
        }
    var title: String? = "" // 头衔
    val positionName: String?  // 职称
        get() = position?.positionName
    val bankBranch: String?  // 支行
        get() = userInfo?.orgName
    val gender: String?  // 性别//  1男 2女
        get() = userInfo?.sex
    val email: String?  // 邮箱
        get() = userInfo?.email
    var infoDone: Boolean = false // 信息是否完整

    val departs: List<Depart>? = ArrayList()
    val multi_depart: Int? = 0
    val position: Position? = Position()
    val token: String? = ""
    val changePwd : String?=""
    val userInfo: UserInfo? = UserInfo()

    data class Depart(
        val address: String? = "",
        val createBy: String? = "",
        val createTime: String? = "",
        val delFlag: String? = "",
        val departName: String? = "",
        val departNameAbbr: String? = "",
        val departNameEn: String? = "",
        val departOrder: String? = "",
        val description: String? = "",
        val fax: String? = "",
        val id: String? = "",
        val memo: String? = "",
        val mobile: String? = "",
        val orgCode: String? = "",
        val orgSign: String? = "",
        val orgType: String? = "",
        val parentId: String? = "",
        val sffs: String? = "",
        val sfnq: String? = "",
        val sfxd: String? = "",
        val status: String? = "",
        val updateBy: String? = "",
        val updateTime: String? = "",
    )

    data class Position(
        val createBy: String? = "",
        val createTime: String? = "",
        val delFlag: String? = "",
        val description: String? = "",
        val id: String? = "",
        val positionCode: String? = "",
        val positionName: String? = "",
        val positionOrder: String? = "",
        val positionType: String? = "",
        val status: String? = "",
        val updateBy: String? = "",
        val updateTime: String? = "",
    )

    data class UserInfo(
        val activitiSync: String? = "",
        val avatar: String? = "",
        val birthday: String? = "",
        val createBy: String? = "",
        val createTime: String? = "",
        val delFlag: String? = "",
        val email: String? = "",
        val id: String? = "",
        val orgCode: String? = "",
        val orgName: String? = "",
        val orgSign: String? = "",
        val password: String? = "",
        val phone: String? = "",
        val realname: String? = "",
        val salt: String? = "",
        val sex: String? = "",
        val status: String? = "",
        val updateBy: String? = "",
        val updateTime: String? = "",
        val userJob: String? = "",
        val userPosition: String? = "",
        val username: String? = "",
        val xdh: String? = "",
        val xdhpassword: String? = "",
        val xnhybj: String? = "",
        val zjhm: String? = "",
        val screenRecordeAble: Boolean? = false,
    )
}
