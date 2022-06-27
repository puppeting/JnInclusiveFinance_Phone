package com.inclusive.finance.jh.ui.apply.credit

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import cn.cloudwalk.libproject.entity.LiveInfo
import cn.cloudwalk.util.FileUtil
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ObjectUtils
import com.google.android.material.chip.ChipGroup
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentCreditFaceBinding
import com.inclusive.finance.jh.glide.GlideCacheEngine
import com.inclusive.finance.jh.glide.GlideEngine
import com.inclusive.finance.jh.glide.imageloder.GlideApp
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.ConfirmPop
import com.inclusive.finance.jh.utils.SZWUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import org.jetbrains.anko.support.v4.act
import java.io.File

/**
 * 人脸识别
 * */
class CreditFaceFragment : MyBaseFragment(), PresenterClick {
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentCreditFaceBinding
    var faceTemp=0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentCreditFaceBinding.inflate(inflater, container, false).apply {
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            presenterClick = this@CreditFaceFragment
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_LGFK -> {
                viewBind.bottomLayout.visibility = if (viewModel.seeOnly==true) View.GONE else View.VISIBLE
            }
            else -> {
                viewBind.bottomLayout.visibility = View.GONE
            }
        }
    }

var getUrl=""
var saveUrl=""
    override fun initData() {
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_LGFK -> {
                getUrl=Urls.get_yxLgfkRlsb_face
                saveUrl=Urls.save_yxLgfkRlsb_face

            }
            ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_ZXGL -> {
                getUrl=Urls.getCreditManagerFace
                saveUrl=Urls.postCreditManagerFace
            }
            ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY,
            ApplyModel.BUSINESS_TYPE_SUNSHINE_ZXSP,
            -> {
                getUrl=Urls.getCreditManagerFace
                saveUrl=Urls.postCreditManagerFace
            }
            else -> {
                getUrl=Urls.getCreditManagerFace
                saveUrl=Urls.postCreditManagerFace
            }
        }
        DataCtrlClass.ApplyNet.getCreditManagerFace(requireActivity(),getUrl, viewModel.keyId, viewModel.jsonObject) {
            if (it != null && isAdded) {
                GlideApp.with(act).load(SZWUtils.getIntactUrl(it.headPhotoAddr))
                    .placeholder(R.mipmap.icon_sex_man).into(viewBind.img)
                GlideApp.with(act).load(SZWUtils.getIntactUrl(it.txdz))
                    .placeholder(R.mipmap.icon_sex_man).into(viewBind.img2)
                if (!it.conclusion.isNullOrEmpty()) {
                    viewBind.tvName.text = it.conclusion
                }
            }
        }
    }
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(Constants.BusAction.Bus_Face_live)])
    fun onOrcBack(liveInfo: LiveInfo?){
        val path = SZWUtils.createCustomCameraOutPath(context)
        FileUtils.createOrExistsDir(path)
        val buffer = StringBuffer()
        val pathName = buffer.append(path).append(AppUtils.getAppName()).append(".")
            .append(System.currentTimeMillis()).append(".jpg").toString()
        FileUtil.writeByteArrayToFile(liveInfo?.clipedBestFace,pathName)
        val mFile =FileUtils.getFileByPath(pathName)
        DataCtrlClass.postCreditManagerFace(context,saveUrl, viewModel.creditId, liveInfo?.hackParams,viewModel.jsonObject, mFile) {
            if (it != null) {
                GlideApp.with(act).load(SZWUtils.getIntactUrl(it.headPhotoAddr))
                    .placeholder(R.mipmap.icon_sex_man).into(viewBind.img)

                viewBind.tvName.text = it.conclusion
                if(it.conclusion == "未通过"){
                    faceTemp++
                    if(faceTemp==3||faceTemp>3){
                        ConfirmPop(context, "活体检测失败3次，是否开启人工审核（如需开启请拍摄客户经理与客户本人合影）") { confirm ->
                            if (confirm) {
                                creditface()
                            }else{
                                faceTemp=0
                            }

                        }.setCancelText("否").setConfirmText("是").show(childFragmentManager, this.javaClass.name)
//                        viewBind.tvName.text = "未通过(请再次点击拍摄客户经理与客户本人合影)"
                    }
                }
                if (isAdded) (activity as BaseActivity).refreshData()
            }
        }
    }
    override fun onClick(v: View?) {
        when (v) {
            viewBind.img, viewBind.tvName -> {
//                if ((viewBind.tvName.text != "一致" && viewBind.tvName.text != "通过") && viewModel.seeOnly == false) {
                if (viewModel.seeOnly == false) {
                    (context as BaseActivity).permissionCamera(null, 100, false) {
                        when (viewModel.businessType) {
                            ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_LGFK -> {
                                creditface()
                            }
                            else ->{
                                if(faceTemp>3||faceTemp==3){
                                    creditface()
                                }else {
                                    ARouter.getInstance().build("/com/FaceActivity").navigation()
                                }
                            }
                        }

//                        ARouter.getInstance().build("/com/FaceActivity").navigation()
//                        val pictureSelector = PictureSelector.create(this)
//                        val openSelector = pictureSelector.openCamera(PictureMimeType.ofImage())
//                        openSelector.selectionMode(PictureConfig.SINGLE)
//                        openSelector.imageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
//                            .isSingleDirectReturn(true).isCamera(true).isCompress(true) //是否压缩.imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java.loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())
//                            .minimumCompressSize(80) // 小于多少kb的图片不压缩
//                            .loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine()) // 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
//                            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT) // 设置相册Activity方向，不设置默认使用系统
//                            .compressQuality(80).synOrAsy(true).forResult(object : OnResultCallbackListener<LocalMedia?> {
//                                override fun onResult(result: List<LocalMedia?>) {
//                                    if (ObjectUtils.isEmpty(result) || result.isEmpty()) {
//                                        return
//                                    }
//                                    var picturePath: String? = result[0]?.compressPath
//                                    if (picturePath == null) {
//                                        picturePath = result[0]?.realPath.toString()
//                                    }
//                                    val mFile = File(picturePath)
//                                    DataCtrlClass.postCreditManagerFace(context = context, url = saveUrl, keyId = viewModel.creditId, imgBase64 = "liveInfo?.hackParams", json = viewModel.jsonObject, file = mFile) {
//                                        if (it != null) {
//                                            GlideApp.with(act).load(SZWUtils.getIntactUrl(it.headPhotoAddr))
//                                                .placeholder(R.mipmap.icon_sex_man).into(viewBind.img)
//                                            viewBind.tvName.text = it.conclusion
//                                            if (isAdded) (activity as BaseActivity).refreshData()
//                                        }
//                                    }
//                                }
//
//                                override fun onCancel() {
//                                }
//                            }) //PictureConfig.CHOOSE_REQUEST

                    }
                }
            }
            viewBind.btNext -> {
                if (viewModel.applyCheckBean?.completeCheckBean?.lgfkSecondLevelBean?.rlsbCheck == true) {
                    ActivityCompat.requireViewById<ChipGroup>(act, R.id.chipGroup)
                        .check(R.id.bt_lylx)
                }
            }
        }
    }
    private fun creditface() {
        val pictureSelector = PictureSelector.create(this)
        val openSelector =
            pictureSelector.openCamera(PictureMimeType.ofImage())
        openSelector.selectionMode(PictureConfig.SINGLE)
        openSelector.imageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
            .isSingleDirectReturn(true).isCamera(true)
            .isCompress(true) //是否压缩.imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java.loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())
            .minimumCompressSize(80) // 小于多少kb的图片不压缩
            .loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine()) // 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE) // 设置相册Activity方向，不设置默认使用系统
            .compressQuality(80).synOrAsy(true)
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: List<LocalMedia?>) {
                    if (ObjectUtils.isEmpty(result) || result.isEmpty()) {
                        return
                    }
                    var picturePath: String? = result[0]?.compressPath
                    if (picturePath == null) {
                        picturePath = result[0]?.realPath.toString()
                    }
                    val mFile = File(picturePath)
                    DataCtrlClass.postCreditManagerFace(//liveInfo?.hackParams
                        context = context,
                        url = saveUrl,
                        keyId = viewModel.creditId,
                        imgBase64 = "",
                        json = viewModel.jsonObject,
                        file = mFile
                    ) {
                        if (it != null) {
                            GlideApp.with(act)
                                .load(SZWUtils.getIntactUrl(it.headPhotoAddr))
                                .placeholder(R.mipmap.icon_sex_man)
                                .into(viewBind.img)
                            if(it.conclusion == "未通过"){
                                faceTemp++
                            }
                            viewBind.tvName.text = it.conclusion
                            if (isAdded) (activity as BaseActivity).refreshData()
                        }
                    }
                }

                override fun onCancel() {
                }
            }) //PictureConfig.CHOOSE_REQUEST
    }
}