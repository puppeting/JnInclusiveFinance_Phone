package com.inclusive.finance.jh.bean

import android.os.Parcel
import android.os.Parcelable

data class ConfigurationBean(
    val current: NodeBean? = null,
    var next: NodeBean? = null,
    val nexts: List<NodeBean>? = null,
    val pre: List<NodeBean>? = null,
    val type: String? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readParcelable(NodeBean::class.java.classLoader), parcel.readParcelable(NodeBean::class.java.classLoader), parcel.createTypedArrayList(NodeBean), parcel.createTypedArrayList(NodeBean), parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(current, flags)
        parcel.writeParcelable(next, flags)
        parcel.writeTypedList(nexts)
        parcel.writeTypedList(pre)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ConfigurationBean> {
        override fun createFromParcel(parcel: Parcel): ConfigurationBean {
            return ConfigurationBean(parcel)
        }

        override fun newArray(size: Int): Array<ConfigurationBean?> {
            return arrayOfNulls(size)
        }
    }

}

data class NodeBean(
    val handler: String? = "",
    val handlerNo: String? = "",
    val handlerList: List<Clrlist>? = ArrayList(),
    val clrpos: String? = "",
    val clrrole: String? = "",
    val clrsql: String? = "",
    val handlerCreditNo: String? = "",
    val clrxzms: String? = "",
    val id: String? = "",
    val endSign: String? = "",
    val nodeNo: String? = "",
    val nodeName: String? = "",
    val nodesql: String? = "",
    val pid: String? = "",
    val sqrbj: String? = "",
    val status: String? = "",
    val item1: String? = "",
    val item2: String? = "",
    val creditAmtCd: String? = "",
    val remarks: String? = "",
    val annex: String? = "",
    val finalAmt: Double? = Double.MIN_VALUE,
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString(), parcel.readString(), parcel.createTypedArrayList(Clrlist), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(handler)
        parcel.writeString(handlerNo)
        parcel.writeTypedList(handlerList)
        parcel.writeString(clrpos)
        parcel.writeString(clrrole)
        parcel.writeString(clrsql)
        parcel.writeString(handlerCreditNo)
        parcel.writeString(clrxzms)
        parcel.writeString(id)
        parcel.writeString(endSign)
        parcel.writeString(nodeNo)
        parcel.writeString(nodeName)
        parcel.writeString(nodesql)
        parcel.writeString(pid)
        parcel.writeString(sqrbj)
        parcel.writeString(status)
        parcel.writeString(item1)
        parcel.writeString(item2)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NodeBean> {
        override fun createFromParcel(parcel: Parcel): NodeBean {
            return NodeBean(parcel)
        }

        override fun newArray(size: Int): Array<NodeBean?> {
            return arrayOfNulls(size)
        }
    }

}

data class Clrlist(
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
    val sex: Int = 0,
    val status: Int = 0,
    val updateBy: String? = "",
    val updateTime: String? = "",
    val userJob: String? = "",
    val userPosition: String? = "",
    val username: String? = "",
    val xdh: String? = "",
    val xdhpassword: String? = "",
    val xnhybj: String? = "",
    val zjhm: String? = "",
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(activitiSync)
        parcel.writeString(avatar)
        parcel.writeString(birthday)
        parcel.writeString(createBy)
        parcel.writeString(createTime)
        parcel.writeString(delFlag)
        parcel.writeString(email)
        parcel.writeString(id)
        parcel.writeString(orgCode)
        parcel.writeString(orgName)
        parcel.writeString(orgSign)
        parcel.writeString(password)
        parcel.writeString(phone)
        parcel.writeString(realname)
        parcel.writeString(salt)
        parcel.writeInt(sex)
        parcel.writeInt(status)
        parcel.writeString(updateBy)
        parcel.writeString(updateTime)
        parcel.writeString(userJob)
        parcel.writeString(userPosition)
        parcel.writeString(username)
        parcel.writeString(xdh)
        parcel.writeString(xdhpassword)
        parcel.writeString(xnhybj)
        parcel.writeString(zjhm)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Clrlist> {
        override fun createFromParcel(parcel: Parcel): Clrlist {
            return Clrlist(parcel)
        }

        override fun newArray(size: Int): Array<Clrlist?> {
            return arrayOfNulls(size)
        }
    }


    data class ConfirmNodeBean(
        var bz: String = "",
        var clr: String = "",
        var clrgh: String = "",
        var sfhb: String = "",
        var creditAmtCd: String = "",
        var finalAmt: Double ?= Double.MIN_VALUE,
        var clrxdh: String = "",
        var clyj: String = "",
        var czlx: String = "",
        var fj: String = "",
        var nodebh: String = "",
        var relationid: String = "",
        var type: String = "",
        var hbyj: String = "",
        var sfzc: String = "",
    )
}