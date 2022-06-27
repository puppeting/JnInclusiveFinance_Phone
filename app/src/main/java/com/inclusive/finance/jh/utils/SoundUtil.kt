package com.inclusive.finance.jh.utils

import android.content.Context
import android.media.SoundPool
import com.inclusive.finance.jh.R

object SoundUtil {
    private var soundPool: SoundPool? = null
    private var soundID = 0

    /**
     * 需要提前初始化，初始化需要一点时间，这个时候play是没有声音的
     * @param context
     */
    fun initSound(context: Context?) {
        if (soundPool == null) {
            soundPool = SoundPool.Builder().build()
            soundID = soundPool?.load(context, R.raw.golden, 1)?:0
        }
    }

    fun playSound() {
        if (soundPool != null) {
            soundPool?.play(soundID, 1f,  //左耳道音量【0~1】
                1f,  //右耳道音量【0~1】
                0,  //播放优先级【0表示最低优先级】
                0,  //循环模式【0循环一次，-1一直循环，其他表示数字+1表示当前数字对应的循环次数】
                1f //播放速度【1正常，范围0~2】
            )
        }
    }
}