package com.inclusive.finance.jh.pop


import android.content.Context
import android.net.Uri
import android.view.View
import android.view.animation.Animation
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.databinding.PopLoadingBinding
import com.inclusive.finance.jh.glide.imageloder.GlideApp
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AlphaConfig
import razerdp.util.animation.AnimationHelper

class LoadingPop(context: Context?) : BasePopupWindow(context) {


//    override fun onCreateContentView(): View {
//        return createPopupById(R.layout.pop_confirm)
//    }
    lateinit var dataBind: PopLoadingBinding
    override fun onCreateContentView(): View {
        dataBind= PopLoadingBinding.bind(createPopupById(R.layout.pop_loading))
//        dataBind = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.pop_confirm,null,false)
        return dataBind.root
    }
    init {
        setOutSideDismiss(false)
        isOutSideTouchable=true
        setBackground(null)
//        SZWUtils.loadPhotoImg(context,path,dataBind.image)

//        GlideApp.with(context).load(path).diskCacheStrategy(DiskCacheStrategy.RESULT).centerCrop().into(new GlideDrawableImageViewTarget(imageview));
    }
fun loadPath(path:Uri){
    if (context != null) {
        val with = GlideApp.with(context)
        val options = RequestOptions().transform(CenterCrop(), RoundedCorners(1))
            .error(ContextCompat.getDrawable(context, R.mipmap.icon_error))
        val apply = with.load(path).apply(options).thumbnail(Glide.with(dataBind.image)
            .load(R.drawable.loading))
        apply.into(dataBind.image)
    }
}
    fun setMsg(string: String){
        dataBind.msg.text=string
        dataBind.progressBarCircular.visibility=View.VISIBLE
        dataBind.progressBar.visibility=View.GONE
    }
    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation().withAlpha(AlphaConfig.IN).toShow()
    }

    override fun onCreateDismissAnimation(): Animation {
        return AnimationHelper.asAnimation().withAlpha(AlphaConfig.OUT).toDismiss()
    }
}