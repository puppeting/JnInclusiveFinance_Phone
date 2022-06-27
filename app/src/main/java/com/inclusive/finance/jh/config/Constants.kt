package com.inclusive.finance.jh.config

/**
 * Created by 史忠文
 * on 2017/11/13.
 */
object Constants{
    /**
     * 刷新
     */
    object RefreshState {
        const val STATE_REFRESH = 0
        const val STATE_LOAD_MORE = 1
    }
    /**
     * 登录等
     */
    object SPUtilsConfig {

        /**
         * 账号
         */
        const val  SP_PHONE = "SP_PHONE"
        /**
         * token
         */
        const val  SP_TOKEN = "SP_TOKEN"
        /**
         * user信息 json
         */
        const val  SP_USER = "SP_USER"
        /**
         * 是否设置手势解锁
         */
        const val ISGESTURELOCK_KEY = "isgesturelock_key"
        /**
         * 是否设置指纹解锁
         */
        const val ISFINGERLOCK_KEY = "isfingerlock_key"

        /**
         * 手势密码key
         */
        const val GESTURELOCK_KEY = "gesturelock_key"
    }

    /**
     * 结果回执码
     */
    object Result {
        const val Intent_ClassName = "Intent_ClassName"
        const val Result_Login_Ok = 200
    }

    /**
     * 网络码
     */
    object NetCode {
        const val SUCCESS = 200
    }

    /**
     * 权限请求 requestCode
     */
    object Permission {
        const val Location = 100
        const val Phone = 200
        const val SMS = 300
        const val Camera = 400
    }

    /**
     * 回调
     */
    object BusAction {

        /**
         * 支付完成
         */
        const val Pay_Finish = "Pay_Finish"
        /**
         * 忘记密码碎片切换
         */
        const val Bus_ForgetPwd = "Bus_ForgetPwd"
        /**
         * 登录关闭其他页
         */
        const val Bus_LoginFinish = "Bus_LoginFinish"
        /**
         * 登录跳转到指定fragment
         */
        const val Bus_LoginBackPwd = "Bus_LoginBackPwd"
        /**
         * 登录成功
         */
        const val Bus_LoginSuccess = "Bus_LoginSuccess"
        /**
         * 视频播放返回
         */
        const val Bus_Video_Back = "Bus_Video_Back"
        /**
         * 收藏
         */
        const val Bus_favourite = "Bus_favourite"
        /**
         * 校验CreateOrder数据
         */
        const val Bus_Check_CreateOrder= "Bus_Check_CreateOrder"
        /**
         * 刷新首页数据
         */
        const val Bus_Refresh_Main = "Bus_Refresh_Main"
        /**
         * 返回刷新列表数据
         */
        const val Bus_Refresh_List = "Bus_Refresh_List"
        /**
         * 人脸识别结果
         */
        const val Bus_Face_live = "Bus_Face_live"
        /**
         * 指纹设置
         */
        const val Bus_Finger_Setting = "Bus_Finger_Setting"
    }

    /**
     * 定位
     */
    object Location {
        const val INTENT_ACTION_LOCATION = "intent_action_location"
        const val INTENT_DATA_LOCATION_CITY = "intent_data_location_city"
        const val INTENT_DATA_LOCATION_LONGITUDE = "intent_data_location_longitude"
        const val INTENT_DATA_LOCATION_LATITUDE = "intent_data_location_latitude"
    }
    /**
     * 定位 接收
     */
    const  val Receiver_Location = "Receiver_Location"

}