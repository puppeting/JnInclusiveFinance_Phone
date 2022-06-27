package com.inclusive.finance.jh.ui.apply.credit

import android.Manifest
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import cn.cloudwalk.libproject.Builder
import cn.cloudwalk.libproject.LiveStartActivity
import cn.cloudwalk.libproject.base.CwBaseActivity
import cn.cloudwalk.libproject.callback.FrontDetectCallback
import cn.cloudwalk.libproject.callback.NoDoubleClickListener
import cn.cloudwalk.libproject.config.CwLiveConfig
import cn.cloudwalk.libproject.entity.LiveInfo
import cn.cloudwalk.sdk.FaceInterface
import cn.cloudwalk.util.RootUtil
import com.alibaba.android.arouter.facade.annotation.Route
import com.hwangjr.rxbus.RxBus
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.config.Constants.BusAction.Bus_Face_live
import java.util.*
@Route(path = "/com/FaceActivity")
class FaceActivity : CwBaseActivity() {
     override fun hasActionBar(): Boolean {
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face)
        initView()
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
        }
        if (RootUtil.isDeviceRooted()) {
            Toast.makeText(this, "此设备可能为Root设备或模拟器设备", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Builder.clear(true)
    }

    private fun initView() {
//        val mTvVersion: TextView = findViewById(R.id.tvVersion)
//        mTvVersion.text = BuildConfig.VERSION_NAME
        findViewById<TextView>(R.id.tvLiveDetect).setOnClickListener(noDoubleClickListener)
    }

    private val noDoubleClickListener: NoDoubleClickListener = object : NoDoubleClickListener() {
        override fun onNoDoubleClick(v: View) {
            val id = v.id
            if (id == R.id.tvLiveDetect) {
                startLive()
            }
        }
    }
    var mSettingLicense = "MzEyMTE1bm9kZXZpY2Vjd2F1dGhvcml6Zb/k5ubl5+bq/+bg5efl5uf/5ubl4Obg5Yjm5uvl5ubrkeXm5uvl5uai6+Xm5uvl5uTm6+Xm5uDm1efr5+vn6+er4Ofr5+vn6+/n5+bm5uXm"

    /**
     * 启动活体检测
     */
    private fun startLive() {
        val actionList = ArrayList<Int?>()
        actionList.add(FaceInterface.LivessFlag.LIVE_HEAD_LEFT_T)
        actionList.add(FaceInterface.LivessFlag.LIVE_HEAD_RIGHT_T)
        actionList.add(FaceInterface.LivessFlag.LIVE_MOUTH_OPEN_T)
        actionList.add(FaceInterface.LivessFlag.LIVE_EYE_BLINK_T)
        Collections.shuffle(actionList)


        // 创建活体配置项
        CwLiveConfig() // cw授权码，必须设置
            .licence(mSettingLicense) // 摄像头选择
            .facing(Camera.CameraInfo.CAMERA_FACING_FRONT) // 防Hack方式，默认使用后端
            //                .hackMode(App.cwDemoConfig.cwLiveConfig.getHackMode())
            // 注册动作列表，仅支持张嘴、眨眼、左转、右转
            .actionList(actionList) // 动作组数
            .actionGroup(1) // 每组动作数量,不能超过注册动作列表的长度
            // 最多为四个动作
            .actionCount(2) // 是否使用随机动作，默认随机
            //                .randomAction(App.cwDemoConfig.cwLiveConfig.isRandomAction())
            // 准备阶段倒计时，单位：秒
            // 传0则不适用倒计时，默认为不使用
            //                .prepareStageTimeout(App.cwDemoConfig.cwLiveConfig.getPrepareStageTimeout())
            // 动作阶段倒计时，单位：秒
            // 传0则不适用倒计时，默认为8s
            //                .actionStageTimeout(App.cwDemoConfig.cwLiveConfig.getActionStageTimeout())
            // 是否播放提示音，默认播放 暂时无效
            .playSound(true) // 是否显示结果页，默认显示
            .showResultPage(false) // 是否显示准备页面。默认显示
            .showReadyPage(false) // 是否返回每个动作通过的图片，默认为false
            // 通过 ResultCallback 返回 暂时无效
            .returnActionPic(false) // 是否保存算法日志，传入保存算法日志的路径，传null或空则不保存
            // 算法日志仅供调试使用，正式发版请关闭或者做成开关的形式
            //                .saveLogoPath(App.cwDemoConfig.cwLiveConfig.getSaveLogoPath())
            // 后端hack图和最佳人脸压缩比例 0~100，越大质量越高
            .imageCompressionRatio(80) // 前端活体检测回调接口
            .frontDetectCallback(frontDetectCallback) //                .frontLiveCallback(new FrontLiveCallback() {
            //                    @Override
            //                    public void onFrontLivessFinished(byte[] bytes, String s, byte[] bytes1, String s1, byte[] bytes2, boolean b) {
            //                        if (b) {
            //                            String imgBestBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
            //                            String imgNextBase64 = Base64.encodeToString(bytes1, Base64.NO_WRAP);
            //                            String strFaceInfo = imgBestBase64 + "," + s + "_" + imgNextBase64 + "," + s1;
            //                            Log.e("===========1111", strFaceInfo);
            //                        }
            //                    }
            //                })
            // 开始活体检测
            .startActivty(context, LiveStartActivity::class.java)
    }

    /**
     * 前端检测接口回调
     */
    private val frontDetectCallback: FrontDetectCallback = object : FrontDetectCallback {
        /**
         * 前端活体完成回调
         * @param liveInfo  前端活体检测信息
         */
        override  fun onLivenessSuccess(liveInfo: LiveInfo?) {
            if (liveInfo == null) {
                Builder.setFaceResult(this@FaceActivity, Builder.FACE_LIVE_FAIL)
                return
            }
            Builder.setFaceResult(context, Builder.FACE_LIVE_PASS)
            val hackParams: String = liveInfo.hackParams
            Log.e("tag", hackParams)
//            FileUtil.writeByteArrayToFile(liveInfo.bestFace,"")
//            runOnUiThread {findViewById<ImageView>(R.id.img).setImageBitmap(ImageUtils.bytes2Bitmap(liveInfo.bestFace)) }
            RxBus.get().post(Bus_Face_live,liveInfo)
            finish()
        }

        /**
         * 前端活体失败回调
         * @param errorCode 检测失败的错误码
         */
        override  fun onLivenessFail(errorCode: Int) { // toast提示信息
            if (Thread.currentThread() === Looper.getMainLooper().thread) {
                toastLiveFailMessage(errorCode)
            } else {
                runOnUiThread { toastLiveFailMessage(errorCode) }
            }
        }

        /**
         * 用户取消回调
         */
        override  fun onLivenessCancel(resultCode: Int) {
            runOnUiThread(Runnable {
                Toast.makeText(context, (if (resultCode == 0) "准备页面" else "活体页面") + "用户取消检测：", Toast.LENGTH_SHORT)
                    .show()
            })
        }
    }

    /**
     * 活检失败提示信息
     *
     * @param errorCode 错误码
     */
    private fun toastLiveFailMessage(errorCode: Int) {
        when (errorCode) {
            FaceInterface.CW_LivenessCode.CW_FACE_ACTION_NOT_STANDARD -> Toast.makeText(context, "未按提示做出相应动作：$errorCode", Toast.LENGTH_SHORT)
                .show()
            FaceInterface.CW_LivenessCode.CW_FACE_LIVENESS_NOPEOPLE -> Toast.makeText(context, "没有检测到人脸：$errorCode", Toast.LENGTH_SHORT)
                .show()
            FaceInterface.CW_LivenessCode.CW_FACE_LIVENESS_FACEDEC_ERR -> Toast.makeText(context, "人脸验证失败：$errorCode", Toast.LENGTH_SHORT)
                .show()
            FaceInterface.CW_LivenessCode.CW_FACE_LIVENESS_OCK_AND_TOBACK -> Toast.makeText(context, "锁屏或退出后台：$errorCode", Toast.LENGTH_SHORT)
                .show()
            FaceInterface.CW_LivenessCode.CW_FACE_LIVENESS_AUTH_ERROR -> Toast.makeText(context, "SDK初始化失败，请检查授权码", Toast.LENGTH_SHORT)
                .show()
            else -> Toast.makeText(context, "errorCode = $errorCode", Toast.LENGTH_SHORT)
                .show()
        }
    }
}