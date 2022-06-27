package com.inclusive.finance.jh.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.inclusive.finance.jh.bean.LocationBean
import com.inclusive.finance.jh.config.Constants
import com.hwangjr.rxbus.RxBus

class MyLocationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null)
            return
        if (intent.action == Constants.Location.INTENT_ACTION_LOCATION) {
            RxBus.get().register(this)
            val entity = LocationBean()
            entity.city = intent.getStringExtra(Constants.Location.INTENT_DATA_LOCATION_CITY)
            entity.latitude = intent.getStringExtra(Constants.Location.INTENT_DATA_LOCATION_LATITUDE)
            entity.longitude = intent.getStringExtra(Constants.Location.INTENT_DATA_LOCATION_LONGITUDE)
            RxBus.get().post(Constants.Receiver_Location, entity)
        }
    }
}// end receiver