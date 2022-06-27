package com.inclusive.finance.jh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ObjectUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.JsonObject
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.app.MyApplication
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.User
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentMineBinding
import com.inclusive.finance.jh.glide.GlideEngine
import com.inclusive.finance.jh.glide.imageloder.GlideApp
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.utils.SZWUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import org.jetbrains.anko.support.v4.act
import java.io.File
import java.util.*


/**
 *个人中心
 * */
class MineFragment : MyBaseFragment(), PresenterClick, OnRefreshLoadMoreListener {
    private var accountPop: AccountPop? = null
    lateinit var viewModel: MainActivityModel
    private var viewBind: FragmentMineBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act)[MainActivityModel::class.java]
        if (viewBind == null) {
            viewBind = FragmentMineBinding.inflate(inflater, container, false).apply {
                presenterClick = this@MineFragment
                lifecycleOwner = viewLifecycleOwner
            }
        }

        return viewBind?.root ?: View(context)
    }

    override fun initView() {
//        StatusBarUtil.setPaddingSmart(act, viewBind?.actionBarCustom?.appBar)
//        viewBind?.actionBarCustom?.toolbar?.title = "个人中心"
        viewBind?.userName?.text= (MyApplication.user as User).userInfo?.realname?:""
        viewBind?.unit?.text= (MyApplication.user as User).userInfo?.orgName?:""
        viewBind?.header?.let {
            GlideApp.with(this).load(SZWUtils.getIntactUrl( (MyApplication.user as User).userInfo?.avatar?:"")).circleCrop().centerInside().into(it)
        }
    }

//
//    /**
//    返回后刷新数据，
//     */
//    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(Constants.BusAction.Bus_Refresh_List)])
//    fun backRefresh(str: String) {
//       viewBind?.userName?.text= (MyApplication.user as User).userInfo?.realname?:""
//    }

    /**
    登录后刷新数据，
     */
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(Constants.BusAction.Bus_LoginSuccess)])
    fun loginSuccess(str: String) {
        initView()
    }
    override fun onClick(v: View?) {
        when (v) {
            viewBind?.btSetting -> {
                AccountPop().show(parentFragmentManager,"mineFragment")
            }
            viewBind?.btAskForLeave -> {
                BaseTypePop(context, this@MineFragment, "请假", getUrl = Urls.get_main_qingjia, saveUrl = Urls.save_main_qingjia,json = JsonObject()).show(childFragmentManager, this.javaClass.name)
            }
            viewBind?.btExit -> {
                DataCtrlClass.LoginNet.logout(requireActivity()){
                    if (it!=null) {
                        SPUtils.getInstance().put(Constants.SPUtilsConfig.ISGESTURELOCK_KEY, false)
                        SPUtils.getInstance().put(Constants.SPUtilsConfig.ISFINGERLOCK_KEY, false)
                        ARouter.getInstance().build("/com/LoginPasswordActivity")
                            .withTransition(R.anim.slide_in_bottom, android.R.anim.fade_out)
                            .navigation(context)
                    }
                }
            }
            viewBind?.header -> {
                SZWUtils.showSnakeBarError("暂未开发")
                val pictureSelector = PictureSelector.create(this)
                val openSelector = pictureSelector.openGallery(PictureMimeType.ofImage())

                openSelector.selectionMode(PictureConfig.SINGLE)
                openSelector.imageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
                    .isSingleDirectReturn(true)
                    .isCamera(true) //                    .isUseCustomCamera(true)//是否使用自定义相机
                    //                    .setButtonFeatures(CustomCameraView.BUTTON_STATE_ONLY_CAPTURE)//自定相机是否单独拍照、录像
                    .isCompress(true) //是否压缩.imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java.loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())
                    .minimumCompressSize(80) // 小于多少kb的图片不压缩
                    //                    .setOutputCameraPath(SZWUtils.createCustomCameraOutPath(context))
                    .compressQuality(80).synOrAsy(true)
                    .forResult(object : OnResultCallbackListener<LocalMedia?> {
                        override fun onResult(result: List<LocalMedia?>) {
                            if (ObjectUtils.isEmpty(result) || result.isEmpty()) {
                                return
                            }
                            val files = ArrayList<File>()
                            result.forEach {
                                var picturePath = it?.compressPath ?: ""
                                if (picturePath.isEmpty()) {
                                    picturePath = it?.realPath.toString()
                                }
                                files.add(File(picturePath))
                            }
                            viewBind?.header?.let {
                                GlideApp.with(this@MineFragment).load(files[0]).circleCrop()
                                    .centerInside().into(it)
                            }
                        }

                        override fun onCancel() {
                        }
                    }) //PictureConfig.CHOOSE_REQUEST
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
    }


}