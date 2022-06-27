package com.inclusive.finance.jh.utils.net.callback


import com.lzy.okgo.callback.AbsCallback
import com.inclusive.finance.jh.utils.net.AbsNetBean
import okhttp3.Response
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * ================================================
 * 描    述：默认将返回的数据解析成需要的Bean,可以是 BaseBean，String，List，Map
 * ================================================
 */
abstract class JsonCallback<T: AbsNetBean> : AbsCallback<T> {
    private var type: Type? = null
    private var clazz: Class<T>?=null

    constructor()

    constructor(type: Type) {
        this.type = type
    }

    constructor(clazz: Class<T>) {
        this.clazz = clazz
    }

    /**
     * OkGo.<NetEntity>post("").tag(this)
     * .params(new ArrayMap<String></String>, String>())
     * .execute(new DialogCallback<NetEntity>(this) {}
    </NetEntity></NetEntity> */
    @Throws(Throwable::class)
    override fun convertResponse(response: Response): T? {
        if (type == null) {
            if (clazz == null) {
                val genType = javaClass.genericSuperclass
                type = (genType as ParameterizedType).actualTypeArguments[0]
            } else {
                val convert = JsonConvert(clazz)
                return convert.convertResponse(response)
            }
        }

        val convert = JsonConvert<T>(type)
        return convert.convertResponse(response)
    }
}