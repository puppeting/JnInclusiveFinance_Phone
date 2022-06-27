package com.inclusive.finance.jh

import android.content.Context
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.inclusive.finance.jh.adapter.ItemMainAdapter
import com.inclusive.finance.jh.app.MyApplication
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.bean.*
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.pop.ConfirmPop
import com.inclusive.finance.jh.pop.DarkSearchPop
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.auth.RSAUtils
import com.inclusive.finance.jh.utils.net.NetEntity
import com.inclusive.finance.jh.utils.net.callback.DialogCallback
import com.inclusive.finance.jh.utils.net.callback.JsonCallback
import com.inclusive.finance.jh.widget.CustomProgress
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import java.io.File
import java.util.*


object DataCtrlClass {
    /**
     * 获取验证码
     * @param[phone] string	必填	手机号
     * */
    fun getSecurityCode(context: Context?, username: String, phone: String? = "", listener: (errorMsg: NetEntity<Boolean>?) -> Unit) { //        phone	string	必填	手机号
        //        purpose	string	必填	用途：1注册，2忘记密码，3设置支付密码
        val params = HashMap<String, String>()
        params["telephone"] = phone ?: ""
        params["username"] = username
        context?.let {
            OkGo.post<NetEntity<Boolean>>(Urls.GetCode).params(params).tag(this)
                .execute(object : JsonCallback<NetEntity<Boolean>>() {
                    override fun onSuccess(response: Response<NetEntity<Boolean>>) {
                        if (response.body().code == Constants.NetCode.SUCCESS) {
                            listener.invoke(response.body())
                            SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                        } else {
                            listener.invoke(null)
                            SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                        }
                    }

                    override fun onError(response: Response<NetEntity<Boolean>>) {
                        super.onError(response)
                        listener.invoke(null)
                    }

                })
        }
    }

    /**
     * 验证验证码
     * @param[phone] string	必填	手机号
     * */
    fun verifySecurityCode(context: Context?, phone: String, code: String, listener: (it: NetEntity<Boolean>?) -> Unit) { //        phone	string	必填	手机号
        //        purpose	string	必填	用途：1注册，2忘记密码，3设置支付密码
        val params = HashMap<String, String>()
        params["telephone"] = phone
        params["verificationCode"] = code
        context?.let {
            OkGo.post<NetEntity<Boolean>>(Urls.VerifyCode).params(params).tag(this)
                .execute(object : DialogCallback<NetEntity<Boolean>>(it) {
                    override fun onSuccess(response: Response<NetEntity<Boolean>>) {
                        if (response.body().code == Constants.NetCode.SUCCESS) {
                            listener.invoke(response.body())
                        } else {
                            listener.invoke(null)
                            SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                        }
                    }

                    override fun onError(response: Response<NetEntity<Boolean>>) {
                        super.onError(response)
                        listener.invoke(null)
                    }

                })
        }
    }

    /**
     * 登录
     * */
    fun loginWechatApp(context: Context?, wechatUnionid: String, headUrl: String, listener: (userId: User?) -> Unit) { //        user.wechatUnionid=$wechatUnionid&user.headUrl=$headUrl&user.deviceToken=$deviceToken&user.deviceType=$deviceType
        val params = HashMap<String, String>()
        params["wechatUnionid"] = wechatUnionid
        params["headUrl"] = headUrl.replace("http://", "https://") //        params["deviceToken"] = PushAgent.getInstance(context).registrationId
        //        params["deviceType"] = "1"
        context?.let {
            OkGo.post<NetEntity<User>>(Urls.loginWechatApp).params(params).tag(this)
                .execute(object : DialogCallback<NetEntity<User>>(it) {
                    override fun onSuccess(response: Response<NetEntity<User>>) {
                        if (response.body().code == Constants.NetCode.SUCCESS) {
                            listener.invoke(response.body().result)
                        } else {
                            listener.invoke(null)
                            SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                        }
                    }

                    override fun onError(response: Response<NetEntity<User>>) {
                        super.onError(response)
                        listener.invoke(null)
                    }

                })
        }
    }

    /**
     * 登录
     * */
    fun loginWechatAppNoDialog(context: Context?, wechatUnionid: String, headUrl: String, listener: (userId: User?) -> Unit) {
        val params = HashMap<String, String>()
        params["wechatUnionid"] = wechatUnionid
        params["headUrl"] = headUrl.replace("http://", "https://") //        params["deviceToken"] = PushAgent.getInstance(context).registrationId
        //        params["deviceType"] = "1"
        OkGo.post<NetEntity<User>>(Urls.loginWechatApp).params(params).tag(this)
            .execute(object : JsonCallback<NetEntity<User>>() {
                override fun onSuccess(response: Response<NetEntity<User>>) {
                    if (response.body().code == Constants.NetCode.SUCCESS) {
                        listener.invoke(response.body().result)
                    } else {
                        listener.invoke(null)
                    }
                }

                override fun onError(response: Response<NetEntity<User>>) {
                    super.onError(response)
                    listener.invoke(null)
                }

            })
    }

    /**
     * 1验证是否注册
     * */
    fun checkRegister(context: Context?, phone: String, listener: (it: Boolean?) -> Unit) {

        val params = HashMap<String, String>()
        params["telephone"] = phone
        context?.let {
            OkGo.post<NetEntity<Boolean>>(Urls.checkRegister).params(params).tag(this)
                .execute(object : JsonCallback<NetEntity<Boolean>>() {
                    override fun onSuccess(response: Response<NetEntity<Boolean>>) {
                        if (response.body().code == Constants.NetCode.SUCCESS) {
                            listener.invoke(response.body().result)
                        } else {
                            listener.invoke(null)
                        }
                    }

                    override fun onError(response: Response<NetEntity<Boolean>>) {
                        super.onError(response)
                        listener.invoke(null)
                    }

                })
        }
    }

    /**
     *3.3.2	保存用户信息
     * */
    fun saveUser(context: Context, name: String, phone: String, listener: (it: NetEntity<Void>?) -> Unit) { //       user.id=$userId&user.phone=$phone&user.name=$name
        //&user.title=$title&user.companyInfo=$companyInfo
        val params = HashMap<String, String>()
        params["userId"] = MyApplication.loginUserId
        params["telephone"] = phone
        if (name.isNotEmpty()) {
            params["userName"] = name
        }
        OkGo.post<NetEntity<Void>>(Urls.saveUser).params(params).tag(this)
            .execute(object : DialogCallback<NetEntity<Void>>(context) {
                override fun onSuccess(response: Response<NetEntity<Void>>) {
                    if (response.body().code == Constants.NetCode.SUCCESS) {
                        listener.invoke(response.body())
                    } else {
                        listener.invoke(null)
                    }
                }

                override fun onError(response: Response<NetEntity<Void>>) {
                    super.onError(response)
                    listener.invoke(null)
                }

            })
    }

    /**
     *3.3.2	保存用户信息
     * */
    fun updateOrSaveCommonUser(context: Context, travelerId: String, connectName: String, passPortNum: String, idNum: String, listener: (it: NetEntity<Void>?) -> Unit) { //      "connectName": 1,
        //  "connectPhone": 1,
        //  "idNum": 1,
        //  "passPortNum": 1,
        //  "travelerId": 1,
        //  "userType": 1
        val params = HashMap<String, String>()
        params["userId"] = MyApplication.loginUserId
        params["connectName"] = connectName
        params["idNum"] = idNum
        params["passPortNum"] = passPortNum
        params["travelerId"] = travelerId
        OkGo.post<NetEntity<Void>>(Urls.updateOrSaveCommonUser).upJson(Gson().toJson(params))
            .tag(this).execute(object : DialogCallback<NetEntity<Void>>(context) {
                override fun onSuccess(response: Response<NetEntity<Void>>) {
                    if (response.body().code == Constants.NetCode.SUCCESS) {
                        listener.invoke(response.body())
                    } else {
                        listener.invoke(null)
                    }
                }

                override fun onError(response: Response<NetEntity<Void>>) {
                    super.onError(response)
                    listener.invoke(null)
                }

            })
    }

    /**
     *3.3.2	delete用户信息
     * */
    fun deleteCommonUser(context: Context, travelerId: String, listener: (it: NetEntity<Void>?) -> Unit) {
        val params = HashMap<String, String>()
        params["travelerId"] = travelerId
        OkGo.post<NetEntity<Void>>(Urls.deleteCommonUser).params(params).tag(this)
            .execute(object : DialogCallback<NetEntity<Void>>(context) {
                override fun onSuccess(response: Response<NetEntity<Void>>) {
                    if (response.body().code == Constants.NetCode.SUCCESS) {
                        listener.invoke(response.body())
                    } else {
                        listener.invoke(null)
                    }
                }

                override fun onError(response: Response<NetEntity<Void>>) {
                    super.onError(response)
                    listener.invoke(null)
                }

            })
    }

    /**
     * 登录部分
     * */

    object LoginNet {

        /**
         * 登录
         * */
        fun loginPwd(context: BaseActivity?, phone: String, password: String, code: String, isForcibly: Boolean? = false, listener: (userId: User?) -> Unit) {
            val params = HashMap<String, Any>() //            params["username"] = phone //            params["password"] = password
            params["loginData"] = RSAUtils.encryptByPublicKey("$phone#jdkj#$password#jdkj#$code")
            params["clientId"] = Settings.System.getString(context?.contentResolver, Settings.Secure.ANDROID_ID);
            params["remember_me"] = true //            params["deviceToken"] = PushAgent.getInstance(context).registrationId
            //            params["deviceType"] = "1"
            val loginPwdUrl = if (isForcibly == true) Urls.loginPwd1 else Urls.loginPwd
            context?.let { it ->
                OkGo.post<NetEntity<User>>(loginPwdUrl).upJson(Gson().toJson(params)).tag(this)
                    .execute(object : DialogCallback<NetEntity<User>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<User>>) {
                            when (response.body().code) {
                                Constants.NetCode.SUCCESS -> {
                                    listener.invoke(response.body().result)
                                }
                                556 -> {
                                    ConfirmPop(context, response.body()?.msg.toString()) { confirm ->
                                        if (confirm) {
                                            loginPwd(context, phone, password, code = code, isForcibly = true, listener)
                                        }
                                    }.show(context.supportFragmentManager, this.javaClass.name)
                                }
                                else -> {
                                    listener.invoke(null)
                                    SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                                }
                            }
                        }

                        override fun onError(response: Response<NetEntity<User>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }


        /**
         * 退出登录
         * */
        fun logout(context: Context?, listener: (userId: String?) -> Unit) {
            context?.let { it ->
                OkGo.post<NetEntity<Any>>(Urls.logout).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            when (response.body().code) {
                                Constants.NetCode.SUCCESS -> {
                                    listener.invoke(response.body()?.msg.toString())
                                }
                                else -> {
                                    listener.invoke(null)
                                    SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                                }
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 修改密码
         * */
        fun changePasswordForApp(context: Context?, id: String, username: String, password: String, rootView: View?, listener: (userId: String?) -> Unit) {
            val params = HashMap<String, String>()
            params["id"] = id
            params["username"] = username
            params["password"] = password
            context?.let {
                OkGo.post<NetEntity<Any>>(Urls.changePasswordForApp).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("")
                                SZWUtils.showSnakeBarSuccess(rootView, response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(rootView, response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
    }

    /**
     * 首页部分
     * */
    object MainNet {
        /**
         * 获取获取首页待办事项
         */
        fun get_main_dbsx_list(context: Context?, listener: (it: ArrayList<JsonObject>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<NetEntity<ArrayList<JsonObject>>>(Urls.get_main_dbsx_list).params(params)
                    .tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<JsonObject>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取轨迹
         */
        fun get_main_track_list(context: Context?, listener: (it: ArrayList<JsonObject>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<NetEntity<ArrayList<JsonObject>>>(Urls.get_main_track_list).params(params)
                    .tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<JsonObject>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取聚合点
         */
        fun get_main_cluster_list(context: Context?, listener: (it: ArrayList<JsonObject>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<NetEntity<ArrayList<JsonObject>>>(Urls.get_main_cluster_list)
                    .params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<JsonObject>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
    }

    /**
     * 客户管理
     * */

    object KHGLNet {

        /**
         * 获取客户管理列表
         */
        fun getKHGLList(context: Context?, pageNum: Int, con: String, listener: (it: BaseListBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "30"
            params["con"] = con
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.khglList).params(params)
                    .tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 征信-任务-家庭成员
         */
        fun getCreditManagerCyList(context: Context?, url: String? = "", keyId: String? = "", listener: (it: ArrayList<JsonObject>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<JsonObject>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<JsonObject>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
        /**
         * 获取BaseTypePop信息 签约合同
         */
        fun getBaseTypePoPList2(context: Context?, url: String? = "", flag: String? = "", jsonObject: JsonObject? = null, keyId: String? = "", mId: String? = "", contractType: String? = "", businessType: String? = "", listener: (it: ArrayList<BaseTypeBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            if (jsonObject != null) params["bean"] = Gson().toJson(jsonObject)
            params["id"] =  mId ?: ""
            params["flag"] = flag ?: ""
            params["contractType"] = contractType ?: ""
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["zfId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""

            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(SZWUtils.getIntactUrl(url))
                    .params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
        /**
         * 获取BaseTypePop信息
         */
        fun getBaseTypePoPList(context: Context?, url: String? = "", flag: String? = "", jsonObject: JsonObject? = null, keyId: String? = "", businessType: String? = "", listener: (it: ArrayList<BaseTypeBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            if (jsonObject != null) params["bean"] = Gson().toJson(jsonObject)
            params["id"] = SZWUtils.getJsonObjectString(jsonObject, "id")
            params["flag"] = flag ?: ""
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["zfId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""

            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(SZWUtils.getIntactUrl(url))
                    .params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
        /**
         * 保存BaseTypePop信息  2021首检保存
         */
        fun saveBaseTypePoPList2(context: Context?, url: String? = "", state: String? = "",json: List<BaseTypeBean>? = arrayListOf(), keyId: String? = "", businessType: String? = "", jsonObject: JsonObject? = null, idenNo: String? = "", contentView: View? = null, listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            if (jsonObject != null) params["bean"] = Gson().toJson(jsonObject)
            json?.forEach {
                if (it.requireable && it.editable) if (!it.haveValue) {
                    if (context != null) {
                        SZWUtils.showSnakeBarMsg(contentView, "请补充" + if (it.keyName.isEmpty()) it.valueHint else it.keyName)
                    }
                    return
                }
            }
            params["idenNo"] = idenNo ?: ""
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["zfId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["status"] = state ?: ""

            params["businessType"] = businessType ?: ""
            params["id"] = keyId?: ""
             params["json"] = Gson().toJson(json)
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(
                                    (if (response.body().result?.toString()?.isNotEmpty() == true) {
                                        response.body().result.toString()
                                    } else "保存成功").toString()
                                )
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(contentView, response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
        /**
         * 保存BaseTypePop信息 合同签约
         */
        fun saveBaseTypePoPList2(context: Context?, url: String? = "", json: List<BaseTypeBean>? = arrayListOf(), keyId: String? = "", mId: String? = "", contractType: String? = "",businessType: String? = "", jsonObject: JsonObject? = null, idenNo: String? = "", contentView: View? = null, listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            if (jsonObject != null) params["bean"] = Gson().toJson(jsonObject)
            json?.forEach {
                if (it.requireable && it.editable) if (!it.haveValue) {
                    if (context != null) {
                        SZWUtils.showSnakeBarMsg(contentView, "请补充" + if (it.keyName.isEmpty()) it.valueHint else it.keyName)
                    }
                    return
                }
            }
            params["creditId"] = keyId ?: ""

            params["id"] = mId ?: ""
            params["contractType"] = contractType ?: "" //共有权利人新增时使用。代表担保人id
            params["json"] = Gson().toJson(json)
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(
                                    (if (response.body().result?.toString()?.isNotEmpty() == true) {
                                        response.body().result.toString()
                                    } else "保存成功").toString()
                                )
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(contentView, response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
        /**
         * 保存BaseTypePop信息 扶贫走访
         */
        fun saveBaseTypeJtxx(context: Context?, url: String? = "", khbh:String,json: List<BaseTypeBean>? = arrayListOf(), keyId: String? = "", mId: String? = "", contractType: String? = "",businessType: String? = "", jsonObject: JsonObject? = null, idenNo: String? = "", contentView: View? = null, listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            if (jsonObject != null) params["bean"] = Gson().toJson(jsonObject)
            json?.forEach {
                if (it.requireable && it.editable) if (!it.haveValue) {
                    if (context != null) {
                        SZWUtils.showSnakeBarMsg(contentView, "请补充" + if (it.keyName.isEmpty()) it.valueHint else it.keyName)
                    }
                    return
                }
            }
            params["creditId"] = keyId ?: ""
            params["hjbh"] = khbh ?: ""
            params["id"] = mId ?: ""
            params["contractType"] = contractType ?: "" //共有权利人新增时使用。代表担保人id
            params["json"] = Gson().toJson(json)
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(
                                    (if (response.body().result?.toString()?.isNotEmpty() == true) {
                                        response.body().result.toString()
                                    } else "保存成功").toString()
                                )
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(contentView, response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 保存BaseTypePop信息
         */
        fun saveBaseTypePoPList(context: Context?, url: String? = "", json: List<BaseTypeBean>? = arrayListOf(), keyId: String? = "", businessType: String? = "", jsonObject: JsonObject? = null, idenNo: String? = "", contentView: View? = null, listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            if (jsonObject != null) params["bean"] = Gson().toJson(jsonObject)
            json?.forEach {
                if (it.requireable && it.editable) if (!it.haveValue) {
                    if (context != null) {
                        SZWUtils.showSnakeBarMsg(contentView, "请补充" + if (it.keyName.isEmpty()) it.valueHint else it.keyName)
                    }
                    return
                }
            }
            params["idenNo"] = idenNo ?: ""
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["zfId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""
            params["id"] = SZWUtils.getJsonObjectString(jsonObject, "id")
            params["dzyId"] = SZWUtils.getJsonObjectString(jsonObject, "id") //共有权利人新增时使用。代表担保人id
            params["json"] = Gson().toJson(json)
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(
                                    (if (response.body().result?.toString()?.isNotEmpty() == true) {
                                        response.body().result.toString()
                                    } else "保存成功").toString()
                                )
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(contentView, response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 删除BaseTypePop信息
         */
        fun deleteBaseTypePoPList(context: Context?, url: String? = "", keyId: String? = "", jsonObject: JsonObject? = null, listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            if (jsonObject != null) params["bean"] = Gson().toJson(jsonObject)
            params["id"] = SZWUtils.getJsonObjectString(jsonObject, "id")
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 删除客户列表信息
         */
        fun khglDeleteById(context: Context?, id: String? = "", listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["id"] = id ?: ""
            context?.let {
                OkGo.post<NetEntity<String>>(Urls.khgldelete).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<String>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<String>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<String>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 信贷同步
         */
        fun khglXDTB(context: Context?, url: String, creditId: String? = "", listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = creditId ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         *确认客户
         */
        fun khglGetById(context: Context?, id: String? = "", listener: (it: KehuBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["id"] = id ?: ""
            context?.let {
                OkGo.post<NetEntity<KehuBean>>(Urls.khglInfo).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<KehuBean>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<KehuBean>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                            SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                        }

                        override fun onError(response: Response<NetEntity<KehuBean>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
    }

    /**
     * 授信申请
     * */
    object SXSQNet {
        /**
         * 获取授信申请列表
         */
        fun getSXSQList(context: Context?, url: String, pageNum: Int, idenNo: String, admitType: String, processStatus: String, applyType: String, listener: (it: BaseListBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["idenNo"] = idenNo
            params["admitType"] = admitType
            params["applyType"] = applyType
            params["processStatus"] = processStatus
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取影像资料补录列表
         */
        fun getPicAddList(context: Context?, pageNum: Int, idenNo: String, processStatus: String, listener: (it: BaseListBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["idenNo"] = idenNo
            params["status"] = processStatus
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_picAdd_list).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 结束影像资料补录
         */
        fun picAddEnd(context: Context?, id: String? = "", listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["id"] = id ?: ""
            context?.let {
                OkGo.post<NetEntity<String>>(Urls.end_picAdd_list).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<String>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<String>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<String>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 强制结束影像资料补录
         */
        fun picAddFinish(context: Context?, url: String, id: String? = "", listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = id ?: ""
            context?.let {
                OkGo.post<NetEntity<String>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<String>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<String>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<String>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 取消申请
         */
        fun deleteApply(context: Context?, bean: Clrlist.ConfirmNodeBean, listener: (it: String?) -> Unit) {
            context?.let {
                OkGo.post<NetEntity<Any>>(Urls.saveSXSP).upJson(Gson().toJson(bean)).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         *模糊查询
         */
        fun searchQuery(context: Context?, query: String, key: String, listener: (it: ArrayList<String>?) -> Unit) {
            val params = HashMap<String, String>()
            params["parm"] = key
            params["value"] = query
            context?.let {
                OkGo.post<NetEntity<ArrayList<String>>>(Urls.searchQuery).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<String>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<String>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<String>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         *特殊模糊查询 行政机关
         */
        fun searchQuery_fzjg(context: Context?, query: String, listener: (it: ArrayList<DarkSearchPop.SearchBean>?) -> Unit) {
            val params = HashMap<String, String>()
            params["name"] = query
            context?.let {
                OkGo.post<NetEntity<ArrayList<DarkSearchPop.SearchBean>>>(Urls.searchQuery_fzjg)
                    .params(params).tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<DarkSearchPop.SearchBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<DarkSearchPop.SearchBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<DarkSearchPop.SearchBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 风险探测-获取
         */
        fun getRiskList(context: Context?, url: String, keyId: String? = "", parm: String? = "", businessType: String? = "", listener: (it: ArrayList<JsonObject>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["json"] = parm ?: ""
            params["dhId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<JsonObject>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<JsonObject>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }


        /**
         * 授信申请-检测
         */
        fun sxsqCheck(context: Context?, url: String, idCardFront: String, idCardBack: String, idenNo: String, name: String, certsigndate: String, certmaturiy: String, hjAddr: String, contentView: View?, listener: (it: ArrayList<BaseTypeBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["idCardFront"] = idCardFront
            params["idCardBack"] = idCardBack
            params["idenNo"] = idenNo
            params["name"] = name
            params["certsigndate"] = certsigndate
            params["certmaturiy"] = certmaturiy
            params["hjAddr"] = hjAddr
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                                SZWUtils.showSnakeBarSuccess(contentView, response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(contentView, response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 授信申请-添加申请
         */
        fun sxsqAdd(context: Context?, url: String, idCardFront: String, idCardBack: String, idenNo: String, name: String, certsigndate: String, certmaturiy: String, hjAddr: String, contentView: View?, listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["idCardFront"] = idCardFront
            params["idCardBack"] = idCardBack
            params["idenNo"] = idenNo
            params["name"] = name
            params["certsigndate"] = certsigndate
            params["certmaturiy"] = certmaturiy
            params["hjAddr"] = hjAddr
            context?.let {
                OkGo.post<NetEntity<String>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<String>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<String>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(contentView, response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<String>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取征信PDF
         */
        fun getPDF(context: Context?, creditId: String? = "", json: JsonObject? = null, listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = creditId ?: ""
            params["json"] = Gson().toJson(json)
            context?.let {
                OkGo.post<NetEntity<String>>(Urls.getPDF).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<String>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<String>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<String>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
    }

    /**
     * 授信调查
     * */
    object SXDCNet {
        /**
         * 获取授信调查列表
         */
        fun getSXDCList(context: Context?, pageNum: Int, idenNo: String, processStatus: String? = "", listener: (it: BaseListBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "30"
            params["idenNo"] = idenNo
            params["processStatus"] = processStatus ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.getList_sxdc).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 你说我贷-查询
         */
        fun getVodUrl(context: Context?, url: String, keyId: String? = "", listener: (it: JsonObject?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            context?.let {
                OkGo.post<NetEntity<JsonObject>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<JsonObject>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<JsonObject>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<JsonObject>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 你说我贷-添加
         */
        fun saveVodUrl(context: Context?, url: String, keyId: String? = "", imgUrl: String? = "", listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["id"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["imgUrl"] = imgUrl ?: ""
            params["videoPath"] = imgUrl ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) { //                                listener.invoke(response.body().result)
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 资产负债 引入征信
         */
        fun queryZxInfo(context: Context?, creditId: String? = "", listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = creditId ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(Urls.queryZxInfo).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) { //                                listener.invoke(response.body().result)
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 评价指标-获取指标
         */
        fun getPJZB(context: Context?, url: String, keyId: String? = "", listener: (it: TargetBeanList?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            context?.let {
                OkGo.post<NetEntity<TargetBeanList>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<TargetBeanList>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<TargetBeanList>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<TargetBeanList>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取BaseTypePop信息
         */
        fun getSDDCList(context: Context?, url: String? = "", flag: String? = "", jsonObject: JsonObject? = null, keyId: String? = "", businessType: String? = "", listener: (it: NetEntity<ArrayList<SDDCBean>>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            if (jsonObject != null) params["bean"] = Gson().toJson(jsonObject)
            params["id"] = SZWUtils.getJsonObjectString(jsonObject, "id")
            params["flag"] = flag ?: ""
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["zfId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""

            context?.let {
                OkGo.post<NetEntity<ArrayList<SDDCBean>>>(SZWUtils.getIntactUrl(url)).params(params)
                    .tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<SDDCBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<SDDCBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<SDDCBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }


    }


    /**
     * 授信审批
     * */
    object SXSPNet {
        /**
         * 获取授信审批列表
         */
        fun getSXSPList(context: Context?, url: String?, pageNum: Int, idenNo: String, processStatus: String, listener: (it: BaseListBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "30"
            params["idenNo"] = idenNo
            params["processStatus"] = processStatus
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 流程-获取历史信息
         */
        fun getList_flowHis(context: Context?, keyId: String?, type: String? = "", businessType: Int?, listener: (it: ArrayList<ProcessHistort>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val url = when (businessType) {
                ApplyModel.BUSINESS_TYPE_APPLY,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE,
                ApplyModel.BUSINESS_TYPE_ZXSP,
                ApplyModel.BUSINESS_TYPE_SXSP,
                ApplyModel.BUSINESS_TYPE_QPLC,
                ApplyModel.BUSINESS_TYPE_ZXFHQZ,
                ApplyModel.BUSINESS_TYPE_SUNSHINE_QPLC,
                ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY,
                -> {
                    Urls.getList_flowHis
                }
                ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL,
                ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY,
                ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL,
                ApplyModel.BUSINESS_TYPE_INFORMATION_OFFICER,
                ApplyModel.BUSINESS_TYPE_QUESTIONNAIRE,
                ApplyModel.BUSINESS_TYPE_CREDIT_REVIEW,
                ApplyModel.BUSINESS_TYPE_COMPARISON_OF_QUOTAS,
                -> {
                    Urls.get_jnj_flowHis
                }
                ApplyModel.BUSINESS_TYPE_SJ_COMPANY,
                ApplyModel.BUSINESS_TYPE_SJ_PERSONAL,
                ApplyModel.BUSINESS_TYPE_SJ,
                ApplyModel.BUSINESS_TYPE_RC,
                ApplyModel.BUSINESS_TYPE_JNJ_YX,
                -> { //                    Urls.get_jnj_flowHis
                    Urls.get_sj_flowHis
                }
                ApplyModel.BUSINESS_TYPE_PRECREDIT,
                -> {
                    Urls.get_preCredit_flowHis
                }
                ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER,
                -> {
                    Urls.get_yxgl_flowHis
                }

                else -> {
                    ""
                }
            }
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["type"] = type ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<ProcessHistort>>>(url).params(params).tag(this)
                    .execute(object :
                        DialogCallback<NetEntity<ArrayList<ProcessHistort>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<ProcessHistort>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<ProcessHistort>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 流程-获取历史信息
         */
        fun getSuggestion(context: Context?, keyId: String?, type: String? = "", businessType: Int?, listener: (it: JsonObject?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["type"] = type ?: ""
            val url = when (businessType) {
                ApplyModel.BUSINESS_TYPE_JNJ_YX_SUBMIT,
                -> { //                    Urls.save_jnj_flowConfiguration
                    Urls.get_sj_flowSuggestion
                }
                else -> Urls.getSuggestion
            }
            context?.let {
                OkGo.post<NetEntity<JsonObject>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<JsonObject>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<JsonObject>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<JsonObject>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取z信审批列表
         */
        fun getZXSPList(context: Context?, pageNum: Int, url: String, con: String, processStatus: String, listener: (it: BaseListBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "30"
            params["con"] = con
            params["processStatus"] = processStatus
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取征信复核签字列表
         */
        fun getZXFHQZList(context: Context?, pageNum: Int, con: String, processStatus: String? = "", listener: (it: BaseListBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "30"
            params["con"] = con
            params["processStatus"] = processStatus ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.getZXFHQZList).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取待办事项列表
         */
        fun getSXSPById(context: Context?, keyId: String? = "", businessType: Int? = 0, type: String? = "", listener: (it: ConfigurationBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val url = when (businessType) {
                ApplyModel.BUSINESS_TYPE_APPLY,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE,
                ApplyModel.BUSINESS_TYPE_ZXSP,
                ApplyModel.BUSINESS_TYPE_SXSP,
                ApplyModel.BUSINESS_TYPE_QPLC,
                ApplyModel.BUSINESS_TYPE_ZXFHQZ,
                ApplyModel.BUSINESS_TYPE_SUNSHINE_QPLC,
                -> {
                    Urls.getSXSPListById
                }
                ApplyModel.BUSINESS_TYPE_JNJ_CJ_PERSONAL,
                ApplyModel.BUSINESS_TYPE_JNJ_CJ_COMPANY,
                ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY,
                ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL,
                ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL,
                ApplyModel.BUSINESS_TYPE_SJ_COMPANY,
                ApplyModel.BUSINESS_TYPE_SJ_PERSONAL,
                ApplyModel.BUSINESS_TYPE_SJ,
                ApplyModel.BUSINESS_TYPE_RC,
                ApplyModel.BUSINESS_TYPE_JNJ_YX,
                -> { //                    Urls.get_jnj_flowConfiguration
                    Urls.get_sj_flowConfiguration
                }
                ApplyModel.BUSINESS_TYPE_JNJ_YX_SUBMIT,
                -> {
                    Urls.get_sj_flowSuggestion

                }
                ApplyModel.BUSINESS_TYPE_PRECREDIT,
                -> {
                    Urls.get_preCredit_flowConfiguration
                }
                ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER,
                -> {
                    Urls.get_yxgl_flowConfiguration
                }
                ApplyModel.BUSINESS_TYPE_INFORMATION_OFFICER,
                ApplyModel.BUSINESS_TYPE_QUESTIONNAIRE,
                ApplyModel.BUSINESS_TYPE_CREDIT_REVIEW,
                ApplyModel.BUSINESS_TYPE_COMPARISON_OF_QUOTAS,
                -> {
                    Urls.get_xxy_flowConfiguration
                }
                ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY,
                -> {
                    Urls.getSXSPListById_sunshine
                }
                else -> {
                    ""
                }
            }
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["type"] = type ?: ""
            context?.let {
                OkGo.post<NetEntity<ConfigurationBean>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ConfigurationBean>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ConfigurationBean>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ConfigurationBean>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 保存待办事项列表
         */
        fun saveSXSP(context: Context?, bean: Clrlist.ConfirmNodeBean, contentView: View? = null, businessType: Int? = 0, listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val url = when (businessType) {
                ApplyModel.BUSINESS_TYPE_APPLY,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE,
                ApplyModel.BUSINESS_TYPE_ZXSP,
                ApplyModel.BUSINESS_TYPE_SXSP,
                ApplyModel.BUSINESS_TYPE_QPLC,
                ApplyModel.BUSINESS_TYPE_ZXFHQZ,
                ApplyModel.BUSINESS_TYPE_SUNSHINE_QPLC,
                -> {
                    Urls.saveSXSP
                }
                ApplyModel.BUSINESS_TYPE_JNJ_CJ_PERSONAL,
                ApplyModel.BUSINESS_TYPE_JNJ_CJ_COMPANY,
                ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY,
                ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL,
                ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL,
                ApplyModel.BUSINESS_TYPE_SJ_PERSONAL,
                ApplyModel.BUSINESS_TYPE_SJ_COMPANY,
                ApplyModel.BUSINESS_TYPE_SJ,
                ApplyModel.BUSINESS_TYPE_RC,
                ApplyModel.BUSINESS_TYPE_JNJ_YX,
                -> { //                    Urls.save_jnj_flowConfiguration
                    Urls.save_sj_flowConfiguration
                }
                ApplyModel.BUSINESS_TYPE_JNJ_YX_SUBMIT,
                -> { //                    Urls.save_jnj_flowConfiguration
                    Urls.save_sj_flowSuggestion
                }
                ApplyModel.BUSINESS_TYPE_PRECREDIT,
                -> {
                    Urls.save_preCredit_flowConfiguration
                }
                ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER,
                -> {
                    Urls.save_yxgl_flowConfiguration
                }
                ApplyModel.BUSINESS_TYPE_INFORMATION_OFFICER,
                ApplyModel.BUSINESS_TYPE_QUESTIONNAIRE,
                ApplyModel.BUSINESS_TYPE_CREDIT_REVIEW,
                ApplyModel.BUSINESS_TYPE_COMPARISON_OF_QUOTAS,
                -> {
                    Urls.save_xxy_flowConfiguration
                }
                ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY,
                -> {
                    Urls.saveSXSP_sunshine
                }
                else -> {
                    ""
                }
            }
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).upJson(Gson().toJson(bean)).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(contentView, response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 保存签署意见
         */
        fun saveSignSuggestion(context: Context?, keyId: String? = "", path: String? = "", annex: String? = "", sfzc: String? = "", operation: String? = "", contentView: View? = null, businessType: Int? = 0, listener: (it: String?) -> Unit) { //    String creditId, String suggestion, String annex
            val url = when (businessType) {
                ApplyModel.BUSINESS_TYPE_APPLY,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE,
                ApplyModel.BUSINESS_TYPE_ZXSP,
                ApplyModel.BUSINESS_TYPE_SXSP,
                ApplyModel.BUSINESS_TYPE_QPLC,
                ApplyModel.BUSINESS_TYPE_ZXFHQZ,
                ApplyModel.BUSINESS_TYPE_SUNSHINE_QPLC,
                -> {
                    Urls.setSuggestion
                }
                ApplyModel.BUSINESS_TYPE_JNJ_YX_SUBMIT,
                -> {
                    Urls.save_sj_flowSuggestion

                }
                else -> {
                    ""
                }
            }
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["suggestion"] = annex ?: ""
            params["annex"] = path ?: ""
            params["sfzc"] = sfzc ?: ""
            params["operation"] = operation ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(contentView, response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 保存待办事项列表
         */
        fun saveZXSP(context: Context?, url: String, id: String, result: String, comment: String, rootView: View, listener: (it: String?) -> Unit) { //     入参：id(选择审批的数据id)  result:(同意传agree  不同意传reject)  comment:审批的意见
            val params = HashMap<String, String>()
            params["id"] = id
            params["ids"] = id
            params["result"] = result
            params["comment"] = comment
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this).execute(object :
                        DialogCallback<NetEntity<Any>>(it, true, rootView = rootView) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(rootView, response.body()?.msg.toString())
                            }

                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }


        /**
         * 流程更改
         */
        fun applyChangeFlow(context: Context?, url: String, keyId: String? = "", listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
    }


    /**
     * 季年检
     * */
    object JNJNet {
        /**
         * 季年检采集列表
         */
        fun getJNJCJList(
            context: Context?, pageNum: Int, idenNo: String,
            status_khlx: String,
            status_jclx: String,
            status_rwmc: String, listener: (it: BaseListBean?) -> Unit,
        ) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["idenNo"] = idenNo
            params["custType"] = status_khlx
            params["checkType"] = status_jclx
            params["taskId"] = status_rwmc
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.getJNJSJCJList).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 首检列表
         */
        fun getSJList(
            context: Context?, pageNum: Int, idenNo: String,
            status_khlx: String,
            status_jczt: String, listener: (it: BaseListBean?) -> Unit,
        ) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["idenNo"] = idenNo
            params["custType"] = status_khlx
            params["jczt"] = status_jczt
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.getSJList).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }


        /**
         * 季年检采集任务名称
         */
        fun getJNJCJRWMC(context: Context?, listener: (it: ArrayList<BaseTypeBean.Enum12>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean.Enum12>>>(Urls.getJNJSJCJRWMC)
                    .params(params).tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean.Enum12>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean.Enum12>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean.Enum12>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 季年检非现场列表
         */
        fun getJNJJCOffList(
            context: Context?, pageNum: Int, idenNo: String,
            status_khlx: String,
            status_jclx: String,
            status_rwmc: String, listener: (it: BaseListBean?) -> Unit,
        ) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["idenNo"] = idenNo
            params["custType"] = status_khlx
            params["checkType"] = status_jclx
            params["taskId"] = status_rwmc
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.getJNJJCOffList).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 季年检现场列表
         */
        fun getJNJJCOnList(
            context: Context?, pageNum: Int, idenNo: String,
            status_khlx: String,
            status_jclx: String,
            status_rwmc: String, listener: (it: BaseListBean?) -> Unit,
        ) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["idenNo"] = idenNo
            params["custType"] = status_khlx
            params["checkType"] = status_jclx
            params["taskId"] = status_rwmc
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.getJNJJCOnList).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 提交
         */
        fun submit(context: Context?, url: String, dhId: String? = "", businessType: String? = "", listener: (it: Unit?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["dhId"] = dhId ?: ""
            params["businessType"] = businessType ?: ""
            context?.let {
                val jsonArray = JsonArray()
                jsonArray.add(dhId)
                OkGo.post<NetEntity<Any>>(url).upJson(Gson().toJson(jsonArray)).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(Unit)
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 季年检风险探测保存
         */
        fun saveJNJfxtc(context: Context?, url: String, keyId: String? = "", idenNo: String? = "", xxsm: String? = "", listener: (it: Unit?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["zfId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["idenNo"] = idenNo ?: ""
            params["xxsm"] = xxsm ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(Unit)
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 会办意见保存dhxt/flow/setSuggestion
         */
        fun saveHBYJ(context: Context?, dhId: String, suggestion: String, listener: (it: String?) -> Unit? = {}) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["dhId"] = dhId
            params["suggestion"] = suggestion
            context?.let {
                OkGo.post<NetEntity<Any>>(Urls.save_jnj_jc_hbyj).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 季年检-管护权移交
         */
        fun get_jnj_ghq_bm(context: Context?, bmUrl: String, listener: (it: ArrayList<JsonObject>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<NetEntity<ArrayList<JsonObject>>>(bmUrl).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<JsonObject>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 季年检-合同类型
         */
        fun get_hetong(context: Context?, bmUrl: String, listener: (it: ArrayList<String>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<NetEntity<ArrayList<String>>>(bmUrl).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<String>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<String>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<String>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
        /**
         * 季年检-管护权移交
         */
        fun get_jnj_ghq_ghr(context: Context?, ghrUrl: String, orgCode: String, listener: (it: ArrayList<JsonObject>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["orgCode"] = orgCode
            context?.let {
                OkGo.post<NetEntity<ArrayList<JsonObject>>>(ghrUrl).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<JsonObject>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 贷后 管护权移交
         */
        fun save_jnj_ghq(context: Context?, url: String, json: JsonObject, rootView: View?, listener: (it: String?) -> Unit) {
            context?.let {
                OkGo.post<NetEntity<Any>>(url).upJson(Gson().toJson(json)).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(rootView, response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }


        /**
         * 季年检用信列表
         */
        fun getJNJYXList(context: Context?, pageNum: Int, searchStr: String, orgCode: String, status: String, checkResult: String,daibanstatus : String, listener: (it: BaseListBean?) -> Unit) { //       pageNo
            //        pageSize
            //        orgCode：机构
            //        idenNo：客户名称或身份证号
            //        status：状态，下拉框（默认异常处理中，01检验中；02异常处理中；03流程中；04完成）
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
//            params["idenNo"] = idenNo
//            params["managerName"] = managerName
            params["searchStr"] = searchStr
            params["orgCode"] = orgCode
            params["status"] = status
            params["checkResult"] = checkResult
            params["flag"] = daibanstatus
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_jnj_yx_list).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

    }

    /**
     * 首检
     * */
    object SJNet {
        /**
         * 机构名称
         */
        fun getJGMCList(context: Context?, listener: (it: ArrayList<JsonObject>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<NetEntity<ArrayList<JsonObject>>>(Urls.get_sj_jg).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<JsonObject>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }


        /**
         * 首检列表
         */
        fun getSJList(context: Context?, pageNum: Int, searchStr: String, orgCode: String, status: String, checkResult: String, daibanstatus: String, listener: (it: BaseListBean?) -> Unit) { //       pageNo
            //        pageSize
            //        orgCode：机构
            //        idenNo：客户名称或身份证号
            //        searchStr：客户名称或身份证号
            //        status：状态，下拉框（默认异常处理中，01检验中；02异常处理中；03流程中；04完成）
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
//            params["idenNo"] = idenNo
//            params["managerName"] = managerName
            params["searchStr"] = searchStr
            params["orgCode"] = orgCode
            params["status"] = status
            params["checkResult"] = checkResult
            params["flag"] = daibanstatus
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_sj_list).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
        /**
         * 首检列表2021
         */
        fun getSJList2(context: Context?, pageNum: Int, searchStr: String,searchStr2: String,searchStr3: String, orgCode: String, status: String, checkResult: String, daibanstatus: String, listener: (it: BaseListBean?) -> Unit) { //       pageNo
            //        pageSize
            //        orgCode：机构
            //        idenNo：客户名称或身份证号
            //        searchStr：客户名称或身份证号
            //        status：状态，下拉框（默认异常处理中，01检验中；02异常处理中；03流程中；04完成）
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
//            params["idenNo"] = idenNo
//            params["managerName"] = managerName
            params["managerName"] = searchStr3
            params["custName"] = searchStr
            params["idenNo"] = searchStr2

            params["orgCode"] = orgCode
            params["status"] = status
            params["checkResult"] = checkResult
            params["item1"] = daibanstatus
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_sj_list2).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
    }

    /**
     * 日常捡
     * */
    object RCJNet {
        /**
         * 日常捡 非现场列表
         */
        fun getRCJFList(
            context: Context?, pageNum: Int, idenNo: String,
            status_khlx: String,
            status_jczt: String,
            status_ztlx: String, listener: (it: BaseListBean?) -> Unit,
        ) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["idenNo"] = idenNo
            params["custType"] = status_khlx
            params["dataType"] = status_jczt
            params["status"] = status_ztlx
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.getRCJFList).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
        /**
         * 日常捡 获取是否批量提交
         */
        fun getRCBatchFlag(
            context: Context?, listener: (it: String?) -> Unit,
        ) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<String>(Urls.get_rc_batchFlag)
                    .tag(this).execute(object : StringCallback() {
                        override fun onSuccess(response: Response<String>?) {
                             Log.e("ddddd",""+response?.message())
                             var bean=GsonUtils.fromJson<NetEntity<String>>(response?.body(),NetEntity::class.java)
                            listener.invoke(bean.message)

                        }


                    })

            }
        }
        /**
         * 机构名称
         */
        fun getJGMCList(context: Context?, listener: (it: ArrayList<JsonObject>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<NetEntity<ArrayList<JsonObject>>>(Urls.get_rc_jg).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<JsonObject>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 批量提交
         */
        fun plSubmit(context: Context?, url: String, keyIds: String? = "", type: String? = "",listener: (it: Int?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["dhId"] = keyIds ?: ""
            params["type"] = type?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(Constants.NetCode.SUCCESS)
                             } else {
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 批量提交
         */
        fun plhandleFlowBatch(context: Context?, url: String, keyIds: String? = "", suggestionid: String? = "", bz: String? = "", fj: String? = "", type:String?="",listener: (it: Int?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["relationid"] = keyIds ?: ""
            params["suggestionid"] = suggestionid ?: ""
            params["bz"] = bz ?: ""
            params["fj"] = fj ?: ""
            params["type"] = type?:""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).upJson(Gson().toJson(params)).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(Constants.NetCode.SUCCESS)
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 日常检查列表
         */
        fun getSJList(context: Context?, pageNum: Int, searchStr: String, orgCode: String, status: String, checkResult: String, daibanstatus: String, listener: (it: BaseListBean?) -> Unit) { //       pageNo
            //        pageSize
            //        orgCode：机构
            //        idenNo：客户名称或身份证号
            //        status：状态，下拉框（默认异常处理中，01检验中；02异常处理中；03流程中；04完成）
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
//            params["idenNo"] = idenNo
//            params["managerName"] = managerName
            params["searchStr"] = searchStr
            params["orgCode"] = orgCode
            params["status"] = status
            params["checkResult"] = checkResult
            params["flag"] = daibanstatus

            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_rc_list).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
        /**
         * 日常检查列表2021
         */
        fun getSJList2(context: Context?, pageNum: Int, searchStr: String,searchStr2: String, searchStr3: String, orgCode: String, status: String, checkResult: String, daibanstatus: String, listener: (it: BaseListBean?) -> Unit) { //       pageNo
            //        pageSize
            //        orgCode：机构
            //        idenNo：客户名称或身份证号
            //        status：状态，下拉框（默认异常处理中，01检验中；02异常处理中；03流程中；04完成）
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
//            params["idenNo"] = idenNo
            params["managerName"] = searchStr3
            params["custName"] = searchStr
            params["idenNo"] = searchStr2
            params["orgCode"] = orgCode
            params["status"] = status
            params["checkResult"] = checkResult
            params["item1"] = daibanstatus

            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_rc_list2).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
    }

    /**
     * 走访
     * */
    object VisitNet {
        /**
         * 走访列表
         */
        fun getVisitList(
            context: Context?, pageNum: Int, custName: String, khdz: String,
            tjzt: String,
            taskId: String, khjlgh: String, listener: (it: BaseListBean?) -> Unit,
        ) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["custName"] = custName
            params["khdz"] = khdz
            params["tjzt"] = tjzt
            params["khjlgh"] = khjlgh
            params["taskId"] = taskId
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_visit_list).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 预授信列表
         */
        fun getPreCreditList(
            context: Context?, pageNum: Int, idenNo: String,
            processStatus: String,
            listener: (it: BaseListBean?) -> Unit,
        ) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["idenNo"] = idenNo
            params["processStatus"] = processStatus
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_preCredit_list)
                    .params(params).tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 打卡列表
         */
        fun getClockInList(
            context: Context?, pageNum: Int, orgName: String,
            operator: String,
            listener: (it: BaseListBean?) -> Unit,
        ) { //       orgName、operator
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["orgName"] = orgName
            params["operator"] = operator
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_clockin_list).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 走访审批列表
         */
        fun get_visit_approval_list(
            context: Context?, pageNum: Int, idenNo: String,
            spFlag: String,
            listener: (it: BaseListBean?) -> Unit,
        ) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["idenNo"] = idenNo
            params["spFlag"] = spFlag
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_visit_approval_list)
                    .params(params).tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 任务名称
         */
        fun getVisitRWMC(context: Context?, listener: (it: ArrayList<BaseTypeBean.Enum12>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["status"] = "1"
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean.Enum12>>>(Urls.get_visit_rwmc)
                    .params(params).tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean.Enum12>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean.Enum12>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean.Enum12>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 任务名称
         */
        fun getVisitKHJL(context: Context?, listener: (it: ArrayList<BaseTypeBean.Enum12>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean.Enum12>>>(Urls.get_visit_khjl)
                    .params(params).tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean.Enum12>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean.Enum12>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean.Enum12>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 检查基本信息完善
         */
        fun getVisitCheckJbxx(context: Context?, zfId: String, listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["zfId"] = zfId
            context?.let {
                OkGo.post<NetEntity<Any>>(Urls.get_visit_checkJbxx).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 提交
         */
        fun visitSubmit(context: Context?, url: String, keyIds: String? = "", listener: (it: Unit?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["ids"] = keyIds ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(Unit)
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取管户人
         */
        fun get_visit_kh_ghr(context: Context?, listener: (it: ArrayList<JsonObject>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<NetEntity<ArrayList<JsonObject>>>(Urls.get_visit_kh_ghr).params(params)
                    .tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<JsonObject>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 走访 管护权移交
         */
        fun save_visit_ghq(context: Context?, url: String, json: JsonObject, rootView: View?, listener: (it: String?) -> Unit) {
            context?.let {
                OkGo.post<NetEntity<Any>>(url).upJson(Gson().toJson(json)).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(rootView, response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }


        /**
         * 预授信-添加
         */
        fun preCredit_add(context: Context?, url: String, keyId: String, contentView: View?, listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["zfId"] = keyId
            context?.let {
                OkGo.post<NetEntity<String>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<String>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<String>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(contentView, response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<String>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 保存BaseTypePop信息
         */
        fun saveEDCS(context: Context?, url: String? = "", keyId: String? = "", businessType: String? = "", ysxed: String? = "", contentView: View? = null, listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["ysxId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""
            params["ysxed"] = ysxed ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(
                                    (if (response.body().result?.toString()?.isNotEmpty() == true) {
                                        response.body().result.toString()
                                    } else "保存成功").toString()
                                )
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(contentView, response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
    }

    /**
     * 用信管理
     * */
    object CreditManagementNet {
        /**
         * 获取用信管理列表
         */
        fun getYXGLList(context: Context?, pageNum: Int, idenNo: String, userName: String, processStatus: String, listener: (it: BaseListBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["idenNo"] = idenNo
            params["userName"] = userName
            params["status"] = processStatus
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_yxgl_list).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取录音录像列表
         */
        fun getLYLXList(context: Context?, url: String, keyId: String, pageNum: Int, custName: String, processStatus: String, listener: (it: BaseListBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["custName"] = custName
            params["yxId"] = keyId
            params["uploadFlag"] = processStatus
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 联网核查
         */
        fun getLWHCList(context: Context?, url: String, keyId: String? = "", dbrid: String? = "", businessType: String? = "", listener: (it: ArrayList<BaseTypeBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["yxId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())

                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 合同签约列表
         */
        fun getHTQYListInfo(context: Context?, url: String, keyId: String? = "", businessType: String? = "", listener: (it: BaseListBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["yxId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 删除合同签约列表
         */
        fun deleteById(context: Context?, url: String, id: String? = "", keyId: String? = "", businessType: String? = "", listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["id"] = id ?: ""
            params["yxId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取BaseTypePop信息
         */
        fun getDZYPoPList(context: Context?, url: String? = "", flag: String? = "", jsonObject: JsonObject? = null, keyId: String? = "", mainContractNo: String? = "", htId: String? = "", businessType: String? = "", listener: (it: ArrayList<BaseTypeBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            if (jsonObject != null) params["bean"] = Gson().toJson(jsonObject)
            params["id"] = SZWUtils.getJsonObjectString(jsonObject, "id")
            params["flag"] = flag ?: ""
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["zfId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["htId"] = htId ?: ""
            params["mainContractNo"] = mainContractNo ?: ""
            params["businessType"] = businessType ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(SZWUtils.getIntactUrl(url))
                    .params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 保存BaseTypePop信息
         */
        fun saveDZYPoPList(context: Context?, url: String? = "", json: List<BaseTypeBean>? = arrayListOf(), keyId: String? = "", htId: String? = "", businessType: String? = "", jsonObject: JsonObject? = null, idenNo: String? = "", contentView: View? = null, listener: (it: ArrayList<BaseTypeBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            if (jsonObject != null) params["bean"] = Gson().toJson(jsonObject)
            json?.forEach {
                if (it.requireable && it.editable) if (!it.haveValue) {
                    if (context != null) {
                        SZWUtils.showSnakeBarMsg(contentView, "请补充" + if (it.keyName.isEmpty()) it.valueHint else it.keyName)
                    }
                    return
                }
            }
            params["idenNo"] = idenNo ?: ""
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["zfId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["htId"] = htId ?: ""
            params["businessType"] = businessType ?: ""
            params["id"] = SZWUtils.getJsonObjectString(jsonObject, "id")
            params["dzyId"] = SZWUtils.getJsonObjectString(jsonObject, "id") //共有权利人新增时使用。代表担保人id
            params["json"] = Gson().toJson(json)
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                                SZWUtils.showSnakeBarSuccess(contentView, response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(contentView, response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        fun saveDZYPoPListString(
            context: Context?, url: String? = "", json: List<BaseTypeBean>? = arrayListOf(), keyId: String? = "", htId: String? = "", businessType: String? = "", jsonObject: JsonObject? = null, idenNo: String? = "", contentView: View? = null,
            listener: (it: String?) -> Unit,
        ) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            if (jsonObject != null) params["bean"] = Gson().toJson(jsonObject)
            json?.forEach {
                if (it.requireable && it.editable) if (!it.haveValue) {
                    if (context != null) {
                        SZWUtils.showSnakeBarMsg(contentView, "请补充" + if (it.keyName.isEmpty()) it.valueHint else it.keyName)
                    }
                    return
                }
            }
            params["idenNo"] = idenNo ?: ""
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["zfId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["htId"] = htId ?: ""
            params["businessType"] = businessType ?: ""
            params["id"] = SZWUtils.getJsonObjectString(jsonObject, "id")
            params["dzyId"] = SZWUtils.getJsonObjectString(jsonObject, "id") //共有权利人新增时使用。代表担保人id
            params["json"] = Gson().toJson(json)
            context?.let {
                OkGo.post<NetEntity<String>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<String>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<String>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(contentView, response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<String>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 生成合同
         */
        fun getHTPDF(context: Context?, url: String? = "", listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<NetEntity<String>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<String>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<String>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<String>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

    }

    /**
     * 申请部分
     * */

    object ApplyNet {

        /**
         * 获取客户详细信息
         */
        fun getKHInfo(context: Context?, url: String, keyId: String? = "", idenNo: String? = "", businessType: String? = "", json: String? = "", listener: (it: ArrayList<BaseTypeBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["id"] = keyId ?: ""
            params["idenNo"] = idenNo ?: ""
            params["businessType"] = businessType ?: ""
            params["json"] = json ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                            SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                        }

                    })
            }
        }

        /**
         * 暂存信息
         */
        fun saveTemporary(context: Context?, url: String, json: List<BaseTypeBean>? = arrayListOf(), keyId: String? = "", businessType: String? = "", jsonObject: JsonObject? = null, idenNo: String? = "", listener: (it: Any?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            if (jsonObject != null) params["bean"] = Gson().toJson(jsonObject)
            params["idenNo"] = idenNo ?: ""
            params["dhId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["zfId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""
            params["json"] = Gson().toJson(json)
            params["creditId"] = keyId ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取行业分类
         */
        fun getKHInfoHyfl(context: Context?, type: Int, bean: BaseTypeBean, listener: (it: ArrayList<HangYeFenLeiBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val url = when (type) {
                BaseTypeBean.TYPE_8 -> {
                    Urls.khInfoCategory
                }
                BaseTypeBean.TYPE_21 -> {
                    Urls.khInfoZYFL
                }
                BaseTypeBean.TYPE_23 -> {
                    Urls.get_visit_hyfl
                }
                BaseTypeBean.TYPE_24 -> {
                    bean.treeUrl
                }
                else -> {
                    ""
                }
            }
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<NetEntity<ArrayList<HangYeFenLeiBean>>>(SZWUtils.getIntactUrl(url))
                    .params(params).tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<HangYeFenLeiBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<HangYeFenLeiBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<HangYeFenLeiBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取征信授权页面
         */
        fun getCreditAuthorizationInfo(context: Context?, url: String? = "", keyId: String? = "", json: String? = "", businessType: String? = "", listener: (it: ArrayList<BaseTypeBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["json"] = json ?: ""
            params["id"] = SZWUtils.getJsonObjectString(
                JsonParser.parseString(
                    json ?: "{}"
                ).asJsonObject, "id"
            )
            params["dhId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 删除征信授权页面
         */
        fun deleteCreditAuthorizationInfo(context: Context?, url: String? = "", creditId: String? = "", json: String? = "", listener: (it: ArrayList<BaseTypeBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = creditId ?: ""
            params["json"] = json ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取调查报告。分类
         */
        fun getReportTypeInfo(context: Context?, creditId: String? = "", listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = creditId ?: ""
            context?.let {
                OkGo.post<NetEntity<String>>(Urls.getReportTypeInfo).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<String>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<String>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<String>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取风险探测分类
         */
        fun getRiskTypeInfo(context: Context?, url: String, keyId: String? = "", businessType: String? = "", listener: (it: ArrayList<JsonObject>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<JsonObject>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<JsonObject>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }


        /**
         * 获取家庭成员
         */
        fun getFamilyListInfo(context: Context?, url: String, keyId: String? = "", businessType: String? = "", listener: (it: BaseListBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取家庭成员
         */
        fun getDskhjdxx(context: Context?, IDCard: String? = "", rootView: View? = null, listener: (it: JsonObject?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["idenNo"] = IDCard ?: ""
            context?.let {
                OkGo.post<NetEntity<JsonObject>>(Urls.getDskhjdxx).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<JsonObject>>(it, true, rootView) {
                        override fun onSuccess(response: Response<NetEntity<JsonObject>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<JsonObject>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
        /**
         * 征信-任务-获取审批人
         */
        fun getSpUser(context: Context?, url: String, listener: (it: BaseListBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()

            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 征信-任务-列表
         */
        fun getCreditManagerList(context: Context?, url: String, keyId: String? = "", businessType: String? = "", listener: (it: BaseListBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取准入信息我行数据
         */
        fun getAdmitBankData(context: Context?, creditId: String? = "", js: String? = "", dbrid: String? = "", listener: (it: ArrayList<BaseTypeBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = creditId ?: ""
            params["dbrid"] = dbrid ?: ""
            params["js"] = js ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.admitBankData).params(params)
                    .tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取准入信息我行数据
         */
        fun getListBank(context: Context?, url: String? = "", keyId: String? = "", json: String? = "", businessType: String? = "", listener: (it: ArrayList<BaseTypeBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["json"] = json ?: ""
            params["dhId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
        /**
         * 获取家庭成员
         */
        fun getJiaTing(context: Context?, url: String? = "", keyId: String? = "", json: String? = "",  listener: (it: ArrayList<JtxxBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["json"] = json ?: ""
            params["dhId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
             context?.let {
                OkGo.post<NetEntity<ArrayList<JtxxBean>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<JtxxBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<JtxxBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<JtxxBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取准入信息汇法网
         */
        fun getRiskHFW(context: Context?, url: String, creditId: String? = "", json: String? = "", dhId: String? = "", businessType: String? = "", listener: (it: ArrayList<BaseTypeBean>?, msg: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = creditId ?: ""
            params["json"] = json ?: ""
            params["dhId"] = dhId ?: ""
            params["businessType"] = businessType ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result, null)
                            } else {
                                listener.invoke(null, response.body()?.msg.toString())
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null, null)
                        }

                    })
            }
        }


        /**
         * /**
         * 征信-添加征信任务
        */
        val creditAnalysisAdd = url + "zx/zx/add"
         */
        fun creditAnalysisAdd(context: Context?, url: String, creditId: String? = "", json: String? = "", dhId: String? = "", businessType: String? = "", listener: (it: Unit?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = creditId ?: ""
            params["json"] = json ?: ""
            params["dhId"] = dhId ?: ""
            params["businessType"] = businessType ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(Unit)
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }


        /**
         * 征信查询
         */
        fun creditAnalysisList(context: Context?, url: String, creditId: String? = "", json: String? = "", dhId: String? = "", businessType: String? = "", listener: (it: ArrayList<BaseTypeBean>?, msg: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = creditId ?: ""
            params["json"] = json ?: ""
            params["dhId"] = dhId ?: ""
            params["businessType"] = businessType ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result, null)
                            } else {
                                listener.invoke(null, response.body()?.msg.toString())
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null, null)
                        }

                    })
            }
        }

        /**
         * 获取-影像资料
         */
        fun getPicList(context: Context?, url: String, keyId: String? = "", dbrid: String? = "", businessType: String? = "", listener: (it: ArrayList<BaseTypeBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["zfId"] = keyId ?: ""
            params["dbrid"] = dbrid ?: ""
            params["dhId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())

                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取申请各项状态
         */
        fun getApplyCheck(context: Context?, creditId: String? = "", dhId: String? = "", zfId: String? = "", listener: (it: ComleteCheckBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = creditId ?: ""
            params["dhId"] = dhId ?: ""
            params["zfId"] = zfId ?: ""
            context?.let {
                OkGo.post<NetEntity<ComleteCheckBean>>(Urls.getApplyCheck).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ComleteCheckBean>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ComleteCheckBean>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ComleteCheckBean>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取担保-各种列表
         */
        fun getApplyDBList(context: Context?, pageNum: Int, url: String, creditId: String?, listener: (it: BaseListBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "30"
            params["creditId"] = creditId ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取担保-共借人各种列表
         */
        fun getApplyDBShareList(context: Context?, pageNum: Int, url: String, id: String?, listener: (it: BaseListBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "30"
            params["id"] = id ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取担保-自然人-删除担保人
         */
        fun applyDBDeleteById(context: Context?, url: String, id: String? = "", keyId: String? = "", businessType: String? = "", listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["id"] = id ?: ""
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["zfId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取担保人人脸识别信息
         * */
        fun getCreditManagerFace(context: Context?, url: String, keyId: String?, json: String?, listener: (faceBean: FaceBean?) -> Unit) { //        creditId:
            //        id:
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["json"] = json ?: ""
            context?.let {
                OkGo.post<NetEntity<FaceBean>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<FaceBean>>(it) {
                        override fun onSuccess(response: Response<NetEntity<FaceBean>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<FaceBean>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
    }

    /**
     *营销部分
     *
     * */
    object YXNet {

        /**
         * 二维码列表
         */
        fun getQrcodeList(
            context: Context?, pageNum: Int, qrManager: String,
            listener: (it: BaseListBean?) -> Unit,
        ) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["qrManager"] = qrManager
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_qrcode_list).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 受理列表
         */
        fun getShouliList(
            context: Context?, pageNum: Int, idenNo: String, acceptManager: String, statue: String,
            listener: (it: BaseListBean?) -> Unit,
        ) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["idenNo"] = idenNo
            params["status"] = statue
            params["acceptManager"] = acceptManager
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_shouli_list).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 二维码受理确认处理
         */
        fun updateTime(context: Context?, url: String? = "", id: String, listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["id"] = id
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("处理成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 删除qrcode列表
         */
        fun deleteQrcodeList(context: Context?, url: String? = "", jsonObject: JsonObject? = null, listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            if (jsonObject != null) params["bean"] = Gson().toJson(jsonObject)
            params["id"] = SZWUtils.getJsonObjectString(jsonObject, "id")
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("删除成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 新增qrcode列表
         */
        fun addQrcodeList(context: Context?, url: String? = "", workNoList: String? = null, listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["workNoList"] = workNoList ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 信息员列表
         */
        fun getXXYList(context: Context?, pageNum: Int, custName: String, gsjgCode: String, status: String, township: String, listener: (it: BaseListBean?) -> Unit) { //       pageNo
            //       gsjgCode：机构下拉框，参照季年检列表页的机构下拉
            //       custName：姓名
            //       status：全部：空， 01:有效， 02:无效 ，03:流程中， 默认全部
            //
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["custName"] = custName
            params["gsjgCode"] = gsjgCode
            params["status"] = status
            params["township"] = township
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_xxy_list).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 删除信息员列表
         */
        fun deleteById(context: Context?, url: String, id: String? = "", listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["id"] = id ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 阳光预授信任务列表
         */
        fun getYGYSXRWList(context: Context?, pageNum: Int, custName: String, orgCode: String, rwid: String, status: String, listener: (it: BaseListBean?) -> Unit) { //       pageNo
            //      orgCode：机构下拉，和季年检列表也机构下拉一致
            //      id: 任务下拉中的：keyName
            //      status：是否结束，0:否  |  1:是 |  全部：空，默认全部
            //
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["custName"] = custName
            params["id"] = rwid
            params["orgCode"] = orgCode
            params["status"] = status
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_ygysxrw_list).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 阳光预授信任务名称
         */
        fun getYGYSXRWMC(context: Context?, orgCode: String, listener: (it: ArrayList<BaseTypeBean.Enum12>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["status"] = "1"
            params["orgCode"] = orgCode
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean.Enum12>>>(Urls.get_ygysxrw_rw)
                    .params(params).tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean.Enum12>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean.Enum12>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean.Enum12>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 重置阳光预授信任务
         */
        fun remarkById(context: Context?, url: String, id: String? = "", listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["id"] = id ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }


        /**
         * 问券调查任务名称
         * 评议任务
         */
        fun getRWMC(context: Context?, url: String, listener: (it: ArrayList<BaseTypeBean.Enum12>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean.Enum12>>>(url).params(params).tag(this)
                    .execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean.Enum12>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean.Enum12>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean.Enum12>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }


        /**
         *问卷调查列表
         */
        fun getUnityList(context: Context?, url: String, pageNum: Int, taskId: String, status: String? = "", listener: (it: BaseListBean?) -> Unit) { //       pageNo
            //      pageNo
            //      pageSize
            //      taskId：任务id，任务下拉选择的
            //
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "30"
            params["taskId"] = taskId
            params["status"] = status ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                            SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                        }

                    })
            }
        }

        /**
         * 问卷调查 生成
         */
        fun newWJDC(context: Context?, url: String, taskId: String? = "", listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["taskId"] = taskId ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 问卷调查 删除
         */
        fun deleteWJDC(context: Context?, url: String, taskId: String? = "", listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["taskId"] = taskId ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 阳光预授信-任务-删除
         */
        fun deleteYGYSX(context: Context?, url: String, taskId: String? = "", listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["id"] = taskId ?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }


        /**
         * 获取信息员照片
         */
        fun getXXYInfo(context: Context?, url: String, keyId: String? = "", type: String? = "", listener: (it: ArrayList<BaseTypeBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
//            type：edit
            val params = HashMap<String, String>()
            params["taskId"] = keyId ?: ""
            params["type"] = type ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                            SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                        }

                    })
            }
        }

        /**
         * 保存信息员照片
         */
        fun saveXXYList(context: Context?, url: String? = "", keyId: String? = "", json: List<BaseTypeBean>? = arrayListOf(), listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["taskId"] = keyId ?: ""
            params["json"] = Gson().toJson(json)
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(
                                    (if (response.body().result?.toString()?.isNotEmpty() == true) {
                                        response.body().result.toString()
                                    } else "保存成功").toString()
                                )
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取问卷调查户列表
         */
        fun getWJDCInfo(context: Context?, url: String, keyId: String? = "", houseNumber: String? = "", oldHouseNumber: String? = "", listener: (it: ArrayList<BaseTypeBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            //            type：edit
            val params = HashMap<String, String>()
            params["taskId"] = keyId ?: ""
            params["houseNumber"] = houseNumber ?: ""

            params["oldHouseNumber"] = oldHouseNumber ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                var mrest = response.body().result
                                listener.invoke(mrest)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                            SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                        }

                    })
            }
        }


        /**
         * 获取评议签字
         */
        fun getPYQZInfo(context: Context?, url: String, keyId: String? = "", listener: (it: ArrayList<BaseTypeBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            //            type：edit
            val params = HashMap<String, String>()
            params["taskId"] = keyId ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                            SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                        }

                    })
            }
        }

        /**
         * 保存评议签字
         */
        fun savePYQZInfo(context: Context?, url: String? = "", keyId: String? = "", json: List<BaseTypeBean>? = arrayListOf(), listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["taskId"] = keyId ?: ""
            params["json"] = Gson().toJson(json)
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(
                                    (if (response.body().result?.toString()?.isNotEmpty() == true) {
                                        response.body().result.toString()
                                    } else "保存成功").toString()
                                )
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

    }

    /**
     * 系统管理
     * */
    object SystemManagerNet {
        /**
         * 获取请假审批列表
         */
        fun get_main_qingjia_list(context: Context?, pageNum: Int, status: String, approveRet: String, listener: (it: ArrayList<BaseTypeBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["status"] = status
            params["approveRet"] = approveRet
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_main_qingjia_list)
                    .params(params).tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 请假审批
         */
        fun save_main_leaveApprove(context: Context?, id: String, approveRet: String, conclusion: String, listener: (it: String?) -> Unit) { //  approveRet：审批结果(必传)     通过 | 否决
            //conclusion：审批意见
            val params = HashMap<String, String>()
            params["id"] = id
            params["approveRet"] = approveRet
            params["conclusion"] = conclusion
            context?.let {
                OkGo.post<NetEntity<Any>>(Urls.save_main_leaveApprove).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("保存成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }

                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * delete请假审批
         */
        fun delete_main_leaveApprove(context: Context?, id: String, listener: (it: String?) -> Unit) { //  approveRet：审批结果(必传)     通过 | 否决
            //conclusion：审批意见
            val params = HashMap<String, String>()
            params["id"] = id //            params["ids"] = Gson().toJson(ids)
            context?.let {
                OkGo.post<NetEntity<Any>>(Urls.delete_main_leaveApprove).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke("删除成功")
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }

                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

    }
    /**
     * 公共部分
     * */

    /**
     * 获取版本信息
     */
    fun getVersion(context: Context?, listener: (it: VersionBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
        val params = HashMap<String, String>()
        params["version"] = AppUtils.getAppVersionCode().toString() + ""
        context?.let {
            OkGo.get<NetEntity<VersionBean>>(Urls.checkVersion).params(params).tag(this)
                .execute(object : DialogCallback<NetEntity<VersionBean>>(it, false) {
                    override fun onSuccess(response: Response<NetEntity<VersionBean>>) {
                        if (response.body().code == Constants.NetCode.SUCCESS) {
                            listener.invoke(response.body().result)
                        } else {
                            listener.invoke(null)
                        }
                    }

                    override fun onError(response: Response<NetEntity<VersionBean>>) {
                        super.onError(response)
                        listener.invoke(null)
                    }

                })
        }
    }

    /**
     * 获取版本信息
     */
    fun getshowCode(context: Context?, listener: (it: Boolean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
        val params = HashMap<String, String>() //        params["version"] = AppUtils.getAppVersionCode().toString() + ""
        context?.let {
            OkGo.post<NetEntity<Boolean>>(Urls.checkCode).params(params).tag(this)
                .execute(object : DialogCallback<NetEntity<Boolean>>(it, false) {
                    override fun onSuccess(response: Response<NetEntity<Boolean>>) {
                        if (response.body().code == Constants.NetCode.SUCCESS) {
                            listener.invoke(response.body().result)
                        } else {
                            listener.invoke(null)
                        }
                    }

                    override fun onError(response: Response<NetEntity<Boolean>>) {
                        super.onError(response)
                        listener.invoke(null)
                    }

                })
        }
    }

    /**
     * 上传位置
     */
    fun upLocation(context: Context?, lat: String, lon: String, address: String, listener: (it: String?) -> Unit) { //     dwJd：定位经度
        //dwWd：定位维度
        //dwAddr：定位地址
        val params = HashMap<String, String>()
        params["dwJd"] = lon
        params["dwWd"] = lat
        params["dwAddr"] = address
        context?.let {
            OkGo.post<NetEntity<Any>>(Urls.upLocation).upJson(Gson().toJson(params)).tag(this)
                .execute(object : DialogCallback<NetEntity<Any>>(it, false) {
                    override fun onSuccess(response: Response<NetEntity<Any>>) {
                        if (response.body().code == Constants.NetCode.SUCCESS) {
                            listener.invoke("成功")
                        } else {
                            listener.invoke(null)
                        }
                    }

                    override fun onError(response: Response<NetEntity<Any>>) {
                        super.onError(response)
                        listener.invoke(null)
                    }

                })
        }
    }

    /**
     * 获取获取首页列表
     */
    fun getMainMenuList(context: Context?, listener: (it: ArrayList<ItemMainAdapter.MainHeader>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
        val params = HashMap<String, String>()
        context?.let {
            OkGo.post<NetEntity<ArrayList<ItemMainAdapter.MainHeader>>>(Urls.getMainMenuList)
                .params(params).tag(this).execute(object :
                    DialogCallback<NetEntity<ArrayList<ItemMainAdapter.MainHeader>>>(it, false) {
                    override fun onSuccess(response: Response<NetEntity<ArrayList<ItemMainAdapter.MainHeader>>>) {
                        if (response.body().code == Constants.NetCode.SUCCESS) {
                            listener.invoke(response.body().result)
                        } else {
                            listener.invoke(null)
                        }
                    }

                    override fun onError(response: Response<NetEntity<ArrayList<ItemMainAdapter.MainHeader>>>) {
                        super.onError(response)
                        listener.invoke(null)
                    }

                })
        }
    }

    /**
     * 获取获取首页走访
     */
    fun getMainVisitList(context: Context?, listener: (it: ArrayList<JsonObject>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
        val params = HashMap<String, String>()
        context?.let {
            OkGo.post<NetEntity<ArrayList<JsonObject>>>(Urls.getMainMenuList).params(params)
                .tag(this)
                .execute(object : DialogCallback<NetEntity<ArrayList<JsonObject>>>(it, false) {
                    override fun onSuccess(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                        if (response.body().code == Constants.NetCode.SUCCESS) {
                            listener.invoke(response.body().result)
                        } else {
                            listener.invoke(null)
                        }
                    }

                    override fun onError(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                        super.onError(response)
                        listener.invoke(null)
                    }

                })
        }
    }

    /**
     * 下载pdf文件
     * */
    fun downloadPDF(context: Context, webUrl: String, listener: (it: String?) -> Unit) {
        CustomProgress.show(context, "加载中", false, null)
        val appDir = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + "download" + File.separator
        )
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        val appName: String = webUrl.substring(webUrl.lastIndexOf("/") + 1, webUrl.length)
        context.let {
            OkGo.get<File>(webUrl).tag(this)
                .execute(object : FileCallback(appDir.absolutePath, appName) {
                    override fun onSuccess(response: Response<File>?) {
                        CustomProgress.disMiss()
                        listener.invoke(response?.body()?.absolutePath)
                    }

                    override fun onError(response: Response<File>?) {
                        CustomProgress.disMiss()
                    }

                })
        }
    }

    /**
     * 证件识别
     * */
    fun upDataOCR(context: Context?, side: String, file: File, rootView: View? = null, listener: (idCardBean: IDCardBean?) -> Unit) {
        val params = HashMap<String, String>()
        params["side"] = side
        context?.let {
            OkGo.post<NetEntity<IDCardBean>>(Urls.ocrUrl).params(params).params("file", file)
                .tag(this).execute(object : DialogCallback<NetEntity<IDCardBean>>(it) {
                    override fun onSuccess(response: Response<NetEntity<IDCardBean>>) {
                        if (response.body().code == Constants.NetCode.SUCCESS) {
                            listener.invoke(response.body().result)
                        } else {
                            listener.invoke(null)
                            SZWUtils.showSnakeBarError(rootView, response.body()?.msg.toString())
                        }
                    }

                    override fun onError(response: Response<NetEntity<IDCardBean>>) {
                        super.onError(response)
                        listener.invoke(null)
                    }

                })
        }
    }
    /**
     * 执照识别
     * */
    fun zhiZhaoOCR(
        context: Context?,
        side: String,
        file: File,
        rootView: View? = null,
        listener: (idCardBean: ZhiZhaoBean?) -> Unit
    ) {
        val params = HashMap<String, String>()
        params["yyzzdz"] = side
        context?.let {
            OkGo.post<NetEntity<ZhiZhaoBean>>(Urls.zhizhaoUrl).params(params).params("file", file)
                .tag(this).execute(object : DialogCallback<NetEntity<ZhiZhaoBean>>(it) {
                    override fun onSuccess(response: Response<NetEntity<ZhiZhaoBean>>) {
                        if (response.body().code == Constants.NetCode.SUCCESS) {
                            listener.invoke(response.body().result)
                        } else {
                            listener.invoke(null)
                            SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                        }
                    }

                    override fun onError(response: Response<NetEntity<ZhiZhaoBean>>) {
                        super.onError(response)
                        listener.invoke(null)
                    }

                })
        }
    }
    /**
     * 借据号识别
     * */
    fun upDataJJD(context: Context?, file: File, rootView: View? = null, listener: (id: String?) -> Unit) {
        val params = HashMap<String, String>() //        params["side"] = side
        context?.let {
            OkGo.post<NetEntity<String>>(Urls.jjdUrl).params(params).params("file", file).tag(this)
                .execute(object : DialogCallback<NetEntity<String>>(it) {
                    override fun onSuccess(response: Response<NetEntity<String>>) {
                        if (response.body().code == Constants.NetCode.SUCCESS) {
                            listener.invoke(response.body().result)
                        } else {
                            listener.invoke(null)
                            SZWUtils.showSnakeBarError(rootView, response.body()?.msg.toString())
                        }
                    }

                    override fun onError(response: Response<NetEntity<String>>) {
                        super.onError(response)
                        listener.invoke(null)
                    }

                })
        }
    }

    /**
     * 人脸识别
     * */
    fun upDataFace(context: Context?, keyId: String, js: String, ymbs: String, file: File, listener: (faceBean: FaceBean?) -> Unit) { //        creditId:
        //        js:(本人  或   配偶)
        //        ymbs:(本人授权书页面所有人脸识别传 BR  配偶授权书页面所有人脸识别传PO)
        val params = HashMap<String, String>()
        params["creditId"] = keyId
        params["js"] = js
        params["ymbs"] = ymbs
        context?.let {
            OkGo.post<NetEntity<FaceBean>>(Urls.faceRecognitionUrl).params(params)
                .params("file", file).tag(this)
                .execute(object : DialogCallback<NetEntity<FaceBean>>(it) {
                    override fun onSuccess(response: Response<NetEntity<FaceBean>>) {
                        if (response.body().code == Constants.NetCode.SUCCESS) {
                            listener.invoke(response.body().result)
                        } else {
                            listener.invoke(null)
                            SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                        }
                    }

                    override fun onError(response: Response<NetEntity<FaceBean>>) {
                        super.onError(response)
                        listener.invoke(null)
                    }

                })
        }
    }

    /**
     * 担保人人脸识别
     * */
    fun postCreditManagerFace(context: Context?, url: String, keyId: String?, imgBase64: String?, json: String?, file: File, listener: (faceBean: FaceBean?) -> Unit) { //        creditId:
        //        id:
        val params = HashMap<String, String>()
        params["creditId"] = keyId ?: ""
        params["yxId"] = keyId ?: ""
        params["imgBase64"] = imgBase64 ?: ""
        params["json"] = json ?: ""
        context?.let {
            OkGo.post<NetEntity<FaceBean>>(url).params(params).params("file", file).tag(this)
                .execute(object : DialogCallback<NetEntity<FaceBean>>(it) {
                    override fun onSuccess(response: Response<NetEntity<FaceBean>>) {
                        if (response.body().code == Constants.NetCode.SUCCESS) {
                            listener.invoke(response.body().result)
                            Thread {
                                val picturesPath = it.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                                FileUtils.deleteAllInDir(picturesPath)
                            }.start()
                        } else {
                            listener.invoke(null)
                            SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                        }
                    }

                    override fun onError(response: Response<NetEntity<FaceBean>>) {
                        super.onError(response)
                        listener.invoke(null)
                    }

                })
        }
    }

    /**
     * 多图上传
     * */
    fun uploadFiles(context: Context?, keyId: String? = "", type: String? = "", files: ArrayList<File>, listener: (urls: ArrayList<PicBean>?) -> Unit) {
        val params = HashMap<String, String>()
        params["creditId"] = keyId ?: ""
        params["dhId"] = keyId ?: ""
        params["type"] = type ?: ""
        context?.let {
            OkGo.post<NetEntity<ArrayList<PicBean>>>(Urls.uploadFiles).params(params)
                .addFileParams("files", files).tag(this)
                .execute(object : DialogCallback<NetEntity<ArrayList<PicBean>>>(it) {
                    override fun onSuccess(response: Response<NetEntity<ArrayList<PicBean>>>) {
                        if (response.body().code == Constants.NetCode.SUCCESS) {
                            listener.invoke(response.body().result)
                            Thread {
                                val picturesPath = it.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                                FileUtils.deleteAllInDir(picturesPath)
                            }.start()
                        } else {
                            listener.invoke(null)
                            SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                        }
                    }

                    override fun onError(response: Response<NetEntity<ArrayList<PicBean>>>) {
                        super.onError(response)
                        listener.invoke(null)
                    }

                })
        }
    }
    /**
     * 合同签约
     * */
    object HTQYNet {
        /**
         * 合同签约列表
         */
        fun getHTQYList(context: Context?, url: String, pageNum: Int, idenNo: String, listener: (it: BaseListBean?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["idenNo"] = idenNo
//            params["admitType"] = admitType
//            params["applyType"] = applyType
//            params["processStatus"] = processStatus
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
        /**
         * 合同预览
         */
        fun getYhht(context: Context?, url: String,  idenNo: String, listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()

            params["id"] = idenNo
//            params["admitType"] = admitType
//            params["applyType"] = applyType
//            params["processStatus"] = processStatus
            context?.let {
                OkGo.post<NetEntity<String>>(url).params(params).tag(this)
                    .execute(object :
                        DialogCallback<NetEntity<String>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<String>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<String>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
    }

    /**
     * 阳光预授信
     * */
    object YGYSXNet {
        /**
         * 日常捡 非现场列表
         */
        fun getRCJFList(
            context: Context?, pageNum: Int, idenNo: String,
            status_khlx: String,
            status_jczt: String,
            status_ztlx: String, listener: (it: BaseListBean?) -> Unit,
        ) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["idenNo"] = idenNo
            params["custType"] = status_khlx
            params["dataType"] = status_jczt
            params["status"] = status_ztlx
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.getRCJFList).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
        /**
         * 日常捡 获取是否批量提交
         */
        fun getRCBatchFlag(
            context: Context?, listener: (it: String?) -> Unit,
        ) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<String>(Urls.get_rc_batchFlag)
                    .tag(this).execute(object : StringCallback() {
                        override fun onSuccess(response: Response<String>?) {
                            Log.e("ddddd",""+response?.message())
                            var bean=GsonUtils.fromJson<NetEntity<String>>(response?.body(),NetEntity::class.java)
                            listener.invoke(bean.message)

                        }


                    })

            }
        }
        /**
         * 机构名称
         */
        fun getJGMCList(context: Context?, listener: (it: ArrayList<JsonObject>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<NetEntity<ArrayList<JsonObject>>>(Urls.get_rc_jg).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<JsonObject>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 批量提交
         */
        fun plSubmit(context: Context?, url: String, keyIds: String? = "", type: String? = "",listener: (it: Int?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["dhId"] = keyIds ?: ""
            params["type"] = type?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(Constants.NetCode.SUCCESS)
                            } else {
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 批量提交
         */
        fun plhandleFlowBatch(context: Context?, url: String, keyIds: String? = "", suggestionid: String? = "", bz: String? = "", fj: String? = "", type:String?="",listener: (it: Int?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["relationid"] = keyIds ?: ""
            params["suggestionid"] = suggestionid ?: ""
            params["bz"] = bz ?: ""
            params["fj"] = fj ?: ""
            params["type"] = type?:""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).upJson(Gson().toJson(params)).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(Constants.NetCode.SUCCESS)
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 阳光预授信列表
         */
        fun getYGYSXList(context: Context?, pageNum: Int, khmc: String, ssxzc: String, grp: String, isOnline: String, zjhm: String, listener: (it: BaseListBean?) -> Unit) { //       pageNo
            //        pageSize
            //        orgCode：机构
            //        idenNo：客户名称或身份证号
            //        status：状态，下拉框（默认异常处理中，01检验中；02异常处理中；03流程中；04完成）
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
//            params["idenNo"] = idenNo
//            params["managerName"] = managerName
            params["khmc"] = khmc
            params["ssxzc"] = ssxzc
            params["grp"] = grp
//            params["isOnline"] = isOnline
            params["zjhm"] = zjhm

            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_sunCredit_listApp).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
        /**
         * 保存BaseTypePop信息
         */
        fun saveBaseTypePoPList(context: Context?, url: String? = "", json: List<BaseTypeBean>? = arrayListOf(), keyId: String? = "", businessType: String? = "", jsonObject: JsonObject? = null, idenNo: String? = "", contentView: View? = null, listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            if (jsonObject != null) params["bean"] = Gson().toJson(jsonObject)
            json?.forEach {
                if (it.requireable && it.editable) if (!it.haveValue) {
                    if (context != null) {
                        SZWUtils.showSnakeBarMsg(contentView, "请补充" + if (it.keyName.isEmpty()) it.valueHint else it.keyName)
                    }
                    return
                }
            }
            params["idenNo"] = idenNo ?: ""
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["zfId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""
            params["id"] = keyId?: ""
            params["dzyId"] = SZWUtils.getJsonObjectString(jsonObject, "id") //共有权利人新增时使用。代表担保人id
            params["json"] = Gson().toJson(json)
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(
                                    (if (response.body().result?.toString()?.isNotEmpty() == true) {
                                        response.body().result.toString()
                                    } else "保存成功").toString()
                                )
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(contentView, response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取-
         */
        fun getPicList(context: Context?, url: String, keyId: String? = "", dbrid: String? = "", businessType: String? = "", listener: (it: ArrayList<BaseTypeBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["zfId"] = keyId ?: ""
            params["dbrid"] = dbrid ?: ""
            params["dhId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["id"] = keyId ?: ""

            params["businessType"] = businessType ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())

                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }


        /**
         * 日常检查列表2021
         */
        fun getSJList2(context: Context?, pageNum: Int, searchStr: String,searchStr2: String, searchStr3: String, orgCode: String, status: String, checkResult: String, daibanstatus: String, listener: (it: BaseListBean?) -> Unit) { //       pageNo
            //        pageSize
            //        orgCode：机构
            //        idenNo：客户名称或身份证号
            //        status：状态，下拉框（默认异常处理中，01检验中；02异常处理中；03流程中；04完成）
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
//            params["idenNo"] = idenNo
            params["managerName"] = searchStr3
            params["custName"] = searchStr
            params["idenNo"] = searchStr2
            params["orgCode"] = orgCode
            params["status"] = status
            params["checkResult"] = checkResult
            params["item1"] = daibanstatus

            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_rc_list2).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
    }
    /**
     * 扶贫走访
     * */
    object FPZFNet {
        /**
         * 日常捡 非现场列表
         */
        fun getRCJFList(
            context: Context?, pageNum: Int, idenNo: String,
            status_khlx: String,
            status_jczt: String,
            status_ztlx: String, listener: (it: BaseListBean?) -> Unit,
        ) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
            params["idenNo"] = idenNo
            params["custType"] = status_khlx
            params["dataType"] = status_jczt
            params["status"] = status_ztlx
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.getRCJFList).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
        /**
         * 日常捡 获取是否批量提交
         */
        fun getRCBatchFlag(
            context: Context?, listener: (it: String?) -> Unit,
        ) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<String>(Urls.get_rc_batchFlag)
                    .tag(this).execute(object : StringCallback() {
                        override fun onSuccess(response: Response<String>?) {
                            Log.e("ddddd",""+response?.message())
                            var bean=GsonUtils.fromJson<NetEntity<String>>(response?.body(),NetEntity::class.java)
                            listener.invoke(bean.message)

                        }


                    })

            }
        }
        /**
         * 机构名称
         */
        fun getJGMCList(context: Context?, listener: (it: ArrayList<JsonObject>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            context?.let {
                OkGo.post<NetEntity<ArrayList<JsonObject>>>(Urls.get_rc_jg).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<JsonObject>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<JsonObject>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 批量提交
         */
        fun plSubmit(context: Context?, url: String, keyIds: String? = "", type: String? = "",listener: (it: Int?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["dhId"] = keyIds ?: ""
            params["type"] = type?: ""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(Constants.NetCode.SUCCESS)
                            } else {
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 批量提交
         */
        fun plhandleFlowBatch(context: Context?, url: String, keyIds: String? = "", suggestionid: String? = "", bz: String? = "", fj: String? = "", type:String?="",listener: (it: Int?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["relationid"] = keyIds ?: ""
            params["suggestionid"] = suggestionid ?: ""
            params["bz"] = bz ?: ""
            params["fj"] = fj ?: ""
            params["type"] = type?:""
            context?.let {
                OkGo.post<NetEntity<Any>>(url).upJson(Gson().toJson(params)).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(Constants.NetCode.SUCCESS)
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
        /**
         * 扶贫走访列表
         */
        fun getZFJTXXList(context: Context?, pageNum: Int, hjbh: String, hm: String, grp: String, isOnline: String, zjhm: String, listener: (it: BaseListBean?) -> Unit) { //       pageNo
            //        pageSize
            //        orgCode：机构
            //        idenNo：客户名称或身份证号
            //        status：状态，下拉框（默认异常处理中，01检验中；02异常处理中；03流程中；04完成）
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
//            params["idenNo"] = idenNo
//            params["managerName"] = managerName
            params["id"] = hjbh
//            params["hm"] = hm
////            params["grp"] = grp
////            params["isOnline"] = isOnline
//            params["zjhm"] = zjhm

            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_fpf_listJtApp).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 扶贫走访列表
         */
        fun getYGYSXList(context: Context?, pageNum: Int, zhen: String,cun: String, hjbh: String, khmc: String,  zjhm: String,  zt: String,listener: (it: BaseListBean?) -> Unit) { //       pageNo
            //        pageSize
            //        orgCode：机构
            //        idenNo：客户名称或身份证号
            //        status：状态，下拉框（默认异常处理中，01检验中；02异常处理中；03流程中；04完成）
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
//            params["idenNo"] = idenNo
//            params["managerName"] = managerName
            params["ssxzc"] = cun
            params["ssxzz"] = zhen
//            params["grp"] = grp
//            params["isOnline"] = isOnline
            params["hjbh"] = hjbh
            params["khmc"] = khmc
            params["zjhm"] = zjhm
//            params[""] = zt

            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_fpf_listApp).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
        /**
         * 保存BaseTypePop信息
         */
        fun saveBaseTypePoPList(context: Context?, url: String? = "", json: List<BaseTypeBean>? = arrayListOf(), keyId: String? = "", businessType: String? = "", jsonObject: JsonObject? = null, idenNo: String? = "", contentView: View? = null, listener: (it: String?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            if (jsonObject != null) params["bean"] = Gson().toJson(jsonObject)
            json?.forEach {
                if (it.requireable && it.editable) if (!it.haveValue) {
                    if (context != null) {
                        SZWUtils.showSnakeBarMsg(contentView, "请补充" + if (it.keyName.isEmpty()) it.valueHint else it.keyName)
                    }
                    return
                }
            }
            params["idenNo"] = idenNo ?: ""
            params["creditId"] = keyId ?: ""
            params["dhId"] = keyId ?: ""
            params["zfId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["ysxId"] = keyId ?: ""
            params["businessType"] = businessType ?: ""
            params["id"] = keyId?: ""
            params["dzyId"] = SZWUtils.getJsonObjectString(jsonObject, "id") //共有权利人新增时使用。代表担保人id
            params["json"] = Gson().toJson(json)
            context?.let {
                OkGo.post<NetEntity<Any>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<Any>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<Any>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(
                                    (if (response.body().result?.toString()?.isNotEmpty() == true) {
                                        response.body().result.toString()
                                    } else "保存成功").toString()
                                )
                                SZWUtils.showSnakeBarSuccess(response.body()?.msg.toString())
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(contentView, response.body()?.msg.toString())
                            }
                        }

                        override fun onError(response: Response<NetEntity<Any>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }

        /**
         * 获取-
         */
        fun getPicList(context: Context?, url: String, keyId: String? = "", dbrid: String? = "", businessType: String? = "", listener: (it: ArrayList<BaseTypeBean>?) -> Unit) { //       orderId=$orderId&prePay =$prePay
            val params = HashMap<String, String>()
            params["creditId"] = keyId ?: ""
            params["zfId"] = keyId ?: ""
            params["dbrid"] = dbrid ?: ""
            params["dhId"] = keyId ?: ""
            params["yxId"] = keyId ?: ""
            params["id"] = keyId ?: ""

            params["businessType"] = businessType ?: ""
            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(url).params(params).tag(this)
                    .execute(object : DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, true) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result)
                            } else {
                                listener.invoke(null)
                                SZWUtils.showSnakeBarError(response.body()?.msg.toString())

                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }


        /**
         * 日常检查列表2021
         */
        fun getSJList2(context: Context?, pageNum: Int, searchStr: String,searchStr2: String, searchStr3: String, orgCode: String, status: String, checkResult: String, daibanstatus: String, listener: (it: BaseListBean?) -> Unit) { //       pageNo
            //        pageSize
            //        orgCode：机构
            //        idenNo：客户名称或身份证号
            //        status：状态，下拉框（默认异常处理中，01检验中；02异常处理中；03流程中；04完成）
            val params = HashMap<String, String>()
            params["pageNo"] = pageNum.toString() + ""
            params["pageSize"] = "15"
//            params["idenNo"] = idenNo
            params["managerName"] = searchStr3
            params["custName"] = searchStr
            params["idenNo"] = searchStr2
            params["orgCode"] = orgCode
            params["status"] = status
            params["checkResult"] = checkResult
            params["item1"] = daibanstatus

            context?.let {
                OkGo.post<NetEntity<ArrayList<BaseTypeBean>>>(Urls.get_rc_list2).params(params)
                    .tag(this).execute(object :
                        DialogCallback<NetEntity<ArrayList<BaseTypeBean>>>(it, false) {
                        override fun onSuccess(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            if (response.body().code == Constants.NetCode.SUCCESS) {
                                listener.invoke(response.body().result?.get(0)?.listBean)
                            } else {
                                listener.invoke(null)
                            }
                        }

                        override fun onError(response: Response<NetEntity<ArrayList<BaseTypeBean>>>) {
                            super.onError(response)
                            listener.invoke(null)
                        }

                    })
            }
        }
    }
}


