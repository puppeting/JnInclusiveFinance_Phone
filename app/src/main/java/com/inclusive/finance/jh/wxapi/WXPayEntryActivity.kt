package com.inclusive.finance.jh.wxapi


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.hwangjr.rxbus.RxBus
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory


class WXPayEntryActivity : Activity(), IWXAPIEventHandler {

    lateinit var api: IWXAPI
    private var code = ""
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = WXAPIFactory.createWXAPI(this, Urls.APP_ID)
        api.handleIntent(intent, this)
//        val builder = AlertDialog.Builder(this)
//
//        val view = View.inflate(this, R.layout.layout_dialog_bottom, null)
//        val alertDialog = builder.setView(view).setTitle(when (code) {
//            "-1" -> "支付失败"
//            "-2" -> "支付被取消"
//            "0" -> "支付成功"
//            else -> "支付失败"
//        }).show()
//        view.bt_cancel.setOnClickListener { alertDialog.dismiss() }
//        view.bt_confirm.setOnClickListener { alertDialog.dismiss() }
//        alertDialog.setOnDismissListener {
            if ("0" == code) {
                // 2017/1/16 刷新
                RxBus.get().post(Constants.BusAction.Pay_Finish, Constants.BusAction.Pay_Finish)
            }
            finish()
//        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        api.handleIntent(intent, this)
    }

    override fun onReq(req: BaseReq) {}

    override fun onResp(resp: BaseResp) {
        Log.d(String.format("onPayFinish, errCode = %s", resp.errCode), TAG)

        if (resp.type == ConstantsAPI.COMMAND_PAY_BY_WX) {
            //			AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //			builder.setTitle("提示");
            code = resp.errCode.toString()
            //			Toast.makeText(this,  resp.errStr + ";code=" + String.valueOf(resp.errCode), Toast.LENGTH_SHORT).show();
            //			builder.setMessage(getString(R.string.pay_result_callback_msg, resp.errStr +";code=" + String.valueOf(resp.errCode)));
            //			builder.show();
        }
    }

    companion object {

        private val TAG = "MicroMsg.SDKSample.WXPayEntryActivity"
    }
}