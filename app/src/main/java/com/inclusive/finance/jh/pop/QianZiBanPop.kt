package com.inclusive.finance.jh.pop


import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.graphics.get
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ScreenUtils
import com.inclusive.finance.jh.databinding.PopQianzibanBinding
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil


class QianZiBanPop(mContext: Context?, var requestCode: Int = 0, var callback: (requestCode: Int, url: String) -> Unit) :
    DialogFragment(), View.OnClickListener {

    override fun onClick(v: View?) {
        when (v) {
            dataBind.ivClose -> {
                dismiss()
            }
            dataBind.tvClear -> {
                dataBind.signView.clear()
            }
            dataBind.tvOk -> {
//                contentView.postDelayed({ dismiss() }, 500)
                val path = SZWUtils.createCustomCameraOutPath(context)
                FileUtils.createOrExistsDir(path)
                //bitmap转签名文件
                val buffer = StringBuffer()
                val pathName = buffer.append(path).append(AppUtils.getAppName()).append(".")
                    .append(System.currentTimeMillis()).append(".jpg").toString()
                dataBind.signView.save(pathName, false, 1)
                if (imgJudge(pathName)){
                    callback.invoke(requestCode, pathName)
                    dismiss()
                }else{
                    SZWUtils.showSnakeBarError(dataBind.root,"未获取到签字信息")
                }

            }
            else -> {
            }
        }
    }


    fun imgJudge(path:String): Boolean {
        /**
         * 定义一个RGB的数组，因为图片的RGB模式是由三个 0-255来表示的 比如白色就是(255,255,255)
         */
        val rgb = intArrayOf(255, 255, 255)
        val bi = BitmapFactory.decodeFile(path)

        /**
         * 得到图片的长宽
         */
        val width: Int = bi.getWidth()
        val height: Int = bi.getHeight()
        val minx: Int = 0
        val miny: Int = 0
        var count = 0
        for (i in minx until width step 10) {

            if (count > 10) {
                break
            }
            for (j in miny until height) {
                if (count > 10) {
                    break
                }
                /**
                 * 得到指定像素（i,j)上的RGB值，
                 */
                val pixel: Int = bi[i, j]
                /**
                 * 分别进行位操作得到 r g b上的值
                 */
                rgb[0] = pixel and 0xff0000 shr 16
                rgb[1] = pixel and 0xff00 shr 8
                rgb[2] = pixel and 0xff
                /**
                 * 进行换色操作，我这里是要把蓝底换成白底，那么就判断图片中rgb值是否在蓝色范围的像素
                 */
                if (rgb[0] == 51 && rgb[1] == 51 && rgb[2] == 51) {
                    count++
                }
            }


        }
        return count > 10
    }
    override fun onStart() {
        super.onStart()
        StatusBarUtil.immersive(dialog?.window)
        //        setStyle(STYLE_NO_TITLE,R.style.MyDialog)
        val params = dialog?.window?.attributes
        dialog?.setCanceledOnTouchOutside(false)
        params?.width = ScreenUtils.getScreenWidth()
        params?.height = RelativeLayout.LayoutParams.MATCH_PARENT
        params?.gravity = Gravity.CENTER
        //高度自己定义
        dialog?.window?.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)

    }
    lateinit var dataBind: PopQianzibanBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明

        dataBind = PopQianzibanBinding.inflate(inflater, container, false)
        return dataBind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataBind.tvClear.setOnClickListener(this)
        dataBind.ivClose.setOnClickListener(this)
        dataBind.tvOk.setOnClickListener(this)
    }
}