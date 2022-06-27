package com.inclusive.finance.jh.utils.net

//@HttpResponse(parser = JsonResponseParser.class)//每一个实体类必须加这句话，别忘了在baseactivity里面 初始化注解 x.view().inject(this);
class NetEntity<T> : AbsNetBean {
    override var code = 500
    override var msg: String? =""
        get() {
            return if (field.isNullOrEmpty()&&!message.isNullOrEmpty()) message
            else field?:"msg:null"
        }
    var message :String ?= "message:null"
    var item :String ?= "message:null"
    var result: T? = null
}
