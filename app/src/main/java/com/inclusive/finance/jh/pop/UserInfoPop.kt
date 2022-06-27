package com.inclusive.finance.jh.pop


import android.content.Context
import android.content.pm.ActivityInfo
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.KeyboardUtils
import com.bumptech.glide.Glide
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.databinding.PopPersonalInformationBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.ui.MainActivity
import com.inclusive.finance.jh.glide.GlideCacheEngine
import com.inclusive.finance.jh.glide.GlideEngine
import com.inclusive.finance.jh.glide.imageloder.GlideApp
import com.inclusive.finance.jh.utils.SZWUtils
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AlphaConfig
import razerdp.util.animation.AnimationHelper
import java.io.File

class UserInfoPop(context: Context) : BasePopupWindow(context), View.OnFocusChangeListener,
    PresenterClick {
    private var viewModel: MainActivityModel
    var binding: PopPersonalInformationBinding
    var avatarFile: File? = null
    override fun onCreateContentView(): View {
        return createPopupById(R.layout.pop_personal_information)
    }

    override fun onViewCreated(contentView: View) {
        setAdjustInputMethod(true)
    }

    init {
        setOutSideDismiss(false)
        viewModel = ViewModelProvider(context as MainActivity).get(MainActivityModel::class.java)
//        viewModel.userInfo.value = MyApplication.user as User
        binding = PopPersonalInformationBinding.bind(contentView).apply {
            data = viewModel
            lifecycleOwner = context
            presenterClick = this@UserInfoPop
            etName.onFocusChangeListener = this@UserInfoPop
            etPhoneNum.onFocusChangeListener = this@UserInfoPop
            etEmail.onFocusChangeListener = this@UserInfoPop
        }

        initData()
    }

    private fun initData() {
        val load = Glide.with(binding.profileImage).load(
            if (viewModel.userInfo.value?.gender.equals("1")) R.mipmap.icon_sex_man else R.mipmap.icon_sex_woman
        )
        GlideApp.with(context).load(SZWUtils.getIntactUrl(viewModel.userInfo.value?.headUrl)).thumbnail(load).centerInside().into(binding.profileImage)
        GlideApp.with(context).load(SZWUtils.getIntactUrl(viewModel.userInfo.value?.headUrl)).thumbnail(load).centerInside().into(binding.profileImageLager)
    }

    /**
     * 点击非editText处，隐藏键盘
     *
     * */
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = binding.root.findFocus()
            if (SZWUtils.isShouldHideKeyboard(v, event)) {
                KeyboardUtils.hideSoftInput(v)
                binding.tvMyInfo.isFocusable = true
                binding.tvMyInfo.isFocusableInTouchMode = true
                binding.tvMyInfo.requestFocus()
            }
        }
        return super.onInterceptTouchEvent(event)
    }


    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation().withAlpha(AlphaConfig.IN).toShow()
    }

    override fun onCreateDismissAnimation(): Animation {
        return AnimationHelper.asAnimation().withAlpha(AlphaConfig.OUT).toDismiss()
    }

    /**
     * 因为一次只能指定一个editView 对准键盘，所以监听焦点分开设置
     * */
    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (hasFocus) setAdjustInputMode(
            v.id, FLAG_KEYBOARD_ANIMATE_ALIGN or FLAG_KEYBOARD_FORCE_ADJUST or FLAG_KEYBOARD_ALIGN_TO_VIEW
        )
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.profileImage -> {
                // 2020/9/21 选择头像
                PictureSelector.create(context).openGallery(PictureMimeType.ofImage())
                    .selectionMode(PictureConfig.SINGLE)//单选模式
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT)//屏幕方向固定横屏
                    .isSingleDirectReturn(true)//选择后是否直接返回，或返回相册
                    .isCompress(true) //是否压缩
                    .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                    .loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())
                    .minimumCompressSize(80) // 小于多少kb的图片不压缩
                    .compressQuality(80)//压缩质量
                    .synOrAsy(true)//异步
                    .forResult(object : OnResultCallbackListener<LocalMedia?> {
                        override fun onResult(result: List<LocalMedia?>) {
                            var picturePath: String? = result[0]?.compressPath //压缩后图片地址
                            if (picturePath == null) {
                                //原图片地址
                                picturePath = result[0]?.realPath.toString()
                            }
                            avatarFile = File(picturePath)
//                            binding.profileImage.imageURI=avatarFile?.toUri()
//                            binding.profileImageLager.imageURI=avatarFile?.toUri()
                        }

                        override fun onCancel() {}
                    })
            }
            binding.btSave -> {
                // TODO: 2020/9/21 图像判空，上传图像后，上传更改信息，覆盖user

            }
            binding.ivClose -> {
                dismiss()
            }
        }
    }


}