package com.inclusive.finance.jh.widget.record

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.TimeUtils
import com.hwangjr.rxbus.RxBus
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.databinding.ActivityRecordBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.ConfirmPop
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import com.inclusive.finance.jh.R


@Route(path = "/com/RecordActivity")
class RecordActivity : BaseActivity(), PresenterClick {
    lateinit var viewBind: ActivityRecordBinding
    override fun initToolbar() {
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.toolbar.title="面谈面签"
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun setInflateBinding() {
        viewBind = DataBindingUtil.setContentView<ActivityRecordBinding>(this, R.layout.activity_record)
            .apply {
                presenterClick = this@RecordActivity
                lifecycleOwner = this@RecordActivity
            }
    }

    private var camera: Camera? = null
    private var mVideoCapture: VideoCapture? = null
    private var mProcessCameraProvider: ProcessCameraProvider? = null
    private var mPreview: Preview? = null
    private val permissions = arrayOf<String>(
        Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val REQUEST_PERMISSIONS = 1001
    private var mIsRecording = false
    private var cameraDirection = CameraSelector.LENS_FACING_BACK
    private var videoUri: Uri? = null
    private val TAG = "VideoRecodeActivity"
    private var mLastClickTime: Long = 0
    private val TIME_INTERVAL = 150L

    override fun init() {
        startstartCamera()
    }

    private fun startstartCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED -> {
                    requestPermissions(permissions, REQUEST_PERMISSIONS)
                }
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED -> {
                    requestPermissions(permissions, REQUEST_PERMISSIONS)
                }
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED -> {
                    requestPermissions(permissions, REQUEST_PERMISSIONS)
                }
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED -> {
                    requestPermissions(permissions, REQUEST_PERMISSIONS)
                }
                else -> {
                    startCamera()
                }
            }
        } else {
            startCamera()
        }
    }

    /**
     *  Detecting the most suitable aspect ratio for current dimensions
     *
     *  @param width - preview width
     *  @param height - preview height
     *  @return suitable aspect ratio
     */

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({ // Used to bind the lifecycle of cameras to the lifecycle owner
            mProcessCameraProvider = cameraProviderFuture.get() // The display information
            val metrics = DisplayMetrics().also { viewBind.mPreviewView.display.getRealMetrics(it) } // The ratio for the output image and preview
            val aspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels) // The display rotation
            val rotation = viewBind.mPreviewView.display.rotation // Preview
            mPreview = Preview.Builder() //                .setTargetAspectRatio(RATIO_16_9) // set the camera aspect ratio
                .setTargetRotation(rotation) // set the camera rotation
                .setTargetResolution(Size(1080, 1440)).build() // Paste image capture code here!
            mVideoCapture = VideoCapture.Builder() //                .setTargetAspectRatio(RATIO_16_9)
                .setTargetRotation(rotation).setTargetResolution(Size(1080, 1440)).build()
            val cameraSelector = CameraSelector.Builder().requireLensFacing(cameraDirection).build()
            try { // Unbind use cases before rebinding
                mProcessCameraProvider?.unbindAll()

                // Bind use cases to camera
                camera = mProcessCameraProvider?.bindToLifecycle(
                    this, cameraSelector, mPreview, mVideoCapture
                )
                mPreview?.setSurfaceProvider(viewBind.mPreviewView.surfaceProvider)

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this)) // 创建一个名为 listener 的回调函数，当手势事件发生时会调用这个回调函数
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean { // 获取当前的摄像头的缩放比例
                val currentZoomRatio: Float = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1F

                // 获取用户捏拉手势所更改的缩放比例
                val delta = detector.scaleFactor

                // 更新摄像头的缩放比例
                camera?.cameraControl?.setZoomRatio(currentZoomRatio * delta)
                return true
            }
        } // 将 PreviewView 的触摸监听器绑定到缩放手势监听器上
        val scaleGestureDetector = ScaleGestureDetector(this, listener)

        // 将 PreviewView 的触摸事件传递给缩放手势监听器上
        viewBind.mPreviewView.setOnTouchListener { v, event ->
            v.performClick()
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mLastClickTime = System.currentTimeMillis()
                }
                MotionEvent.ACTION_UP -> {
                    if ((System.currentTimeMillis() - mLastClickTime) < TIME_INTERVAL) {
                        onTouch(event.x, event.y)
                    }

                }
            }
            scaleGestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }
    }

    /**
     * 手动对焦
     * */
    fun onTouch(x: Float, y: Float) { // 创建 MeteringPoint，命名为 factory
        val factory = viewBind.mPreviewView.meteringPointFactory

        // 将 UI 界面的坐标转换为摄像头传感器的坐标
        val point = factory.createPoint(x, y)

        // 创建对焦需要用的 action
        val action = FocusMeteringAction.Builder(point).build()

        // 执行所创建的对焦 action
        camera?.cameraControl?.startFocusAndMetering(action)
    }

    @SuppressLint("RestrictedApi")
    private fun startRecorder() {
        val dirFile = File(SZWUtils.createCustomMoviesOutPath(this))
        if (!dirFile.exists()) {
            val mkdir: Boolean = dirFile.mkdir()
            Log.e(TAG, "startRecorder: mkdir：$mkdir")
        }
        val file = File(dirFile, System.currentTimeMillis().toString() + ".mp4")
        if (!file.exists()) {
            try {
                val newFile: Boolean = file.createNewFile()
                Log.e(TAG, "startRecorder: newFile：$newFile")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mVideoCapture?.startRecording(VideoCapture.OutputFileOptions.Builder(file)
            .build(), ContextCompat.getMainExecutor(this), object :
            VideoCapture.OnVideoSavedCallback {

            override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                Log.e(TAG, "onVideoSaved: $file")
                Toast.makeText(this@RecordActivity, "录制结束", Toast.LENGTH_SHORT).show()
                videoUri = outputFileResults.savedUri
            }

            override fun onError(
                videoCaptureError: Int, @NonNull message: String, @Nullable cause: Throwable?
            ) {
                Log.e(TAG, "onError: $videoCaptureError,$message")
                Toast.makeText(
                    this@RecordActivity, "录制出错：code：$videoCaptureError,$message", Toast.LENGTH_SHORT
                ).show()
                mIsRecording = false
            }
        })
    }

    override fun onDestroy() {
//        stopRecorder()
        super.onDestroy()
    }

    @SuppressLint("RestrictedApi")
    private fun stopRecorder() {
        if (mVideoCapture != null) {
            mVideoCapture?.stopRecording()
            mProcessCameraProvider?.unbindAll()
        }
        mIsRecording = false
        mRunnable?.let { timeHandler?.removeCallbacks(it) }
        mRunnable = null
    }

    private fun submit() {
        ConfirmPop(this, "录制完成") {
            if (it) {
                RxBus.get().post("videoUri", videoUri)
                finish()
            } else {
                viewBind.videoTime.text = "00:00:00"
                startCamera()
            }
        }.setCancelText("重录").setConfirmText("提交").show(supportFragmentManager, this.TAG)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "请到设置中打开应用的相机权限", Toast.LENGTH_SHORT).show()
                    return
                }
                if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "请到设置中打开应用的录音权限", Toast.LENGTH_SHORT).show()
                    return
                }
                if (grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "请到设置中打开应用的存储读权限", Toast.LENGTH_SHORT).show()
                    return
                }
                if (grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "请到设置中打开应用的存储写权限", Toast.LENGTH_SHORT).show()
                    return
                }
                startCamera()
            }
        }
    }

    inner class TimeRunnable : Runnable {
        val l = SimpleDateFormat("HH:mm:ss", Locale.CHINA).parse("00:00:00")?.time ?: 0
        var time: Long = l

        override fun run() {
            viewBind.videoTime.text = TimeUtils.millis2String(time, SimpleDateFormat("HH:mm:ss", Locale.CHINA))
            if ((time - l) == 60000L) {
                viewBind.record.performClick()
                return
            }
            timeHandler?.postDelayed(this, 1000)
            time += 1000


        }
    }

    val timeHandler = Looper.myLooper()?.let { Handler(it) }
    private var mRunnable: TimeRunnable? = null
    override fun onClick(v: View?) {
        when (v) {
            viewBind.recordChange -> {
                cameraDirection = if (cameraDirection == CameraSelector.LENS_FACING_FRONT) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT

                startCamera()
            }
            viewBind.record -> {
                if (!mIsRecording) {
                    viewBind.recordChange.visibility = View.GONE
                    mIsRecording = true
                    if (mRunnable == null) {
                        mRunnable = TimeRunnable()
                        mRunnable?.let { timeHandler?.postDelayed(it, 0) }

                    }
                    startRecorder()
                } else if (mIsRecording) {
                    viewBind.recordChange.visibility = View.GONE
                    stopRecorder()
                    submit()
                }
                viewBind.record.setImageResource(if (mIsRecording) R.mipmap.ic_record_stop else R.mipmap.ic_record)
            }
            viewBind.recordSelect -> {
                choiceVideo()
            }
            else -> {
            }
        }
    }

    /**
     * 从相册中选择视频
     */
    var systemVideo = registerForActivityResult(ActivityResultContracts.GetContent()) {
        //视频文件路径
        videoUri = it
        if (videoUri==null) {
            return@registerForActivityResult
        }
        RxBus.get().post("videoUri", videoUri)
        finish()
    }

    private fun choiceVideo() {
        systemVideo.launch("video/*")
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val RATIO_4_3_VALUE = 4.0 / 3.0 // aspect ratio 4x3
        private const val RATIO_16_9_VALUE = 16.0 / 9.0 // aspect ratio 16x9
    }
}