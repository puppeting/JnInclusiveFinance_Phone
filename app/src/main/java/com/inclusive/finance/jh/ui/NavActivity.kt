package com.inclusive.finance.jh.ui

import android.content.Context
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.bean.model.NavModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.databinding.ActivityGraphNavBinding
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil

@Route(path = "/com/NavActivity")
class NavActivity : BaseActivity() {
    @Autowired
    @JvmField
    var jsonObject = ""

    @Autowired
    @JvmField
    var seeOnly = false

    @Autowired
    @JvmField
    var businessType = ApplyModel.BUSINESS_TYPE_APPLY

    @Autowired
    @JvmField
    var title = "0"


    @Autowired
    @JvmField
    var creditId = ""
    @Autowired
    @JvmField
    var dhId = ""
    @Autowired
    @JvmField
    var ysxId = ""
    @Autowired
    @JvmField
    var keyId = ""

    override fun initToolbar() {
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, dataBind.actionBarCustom.appBar)
        dataBind.actionBarCustom.toolbar.title = title
    }

    lateinit var dataBind: ActivityGraphNavBinding
    override fun setInflateBinding() {
        dataBind = DataBindingUtil.setContentView<ActivityGraphNavBinding>(this, R.layout.activity_graph_nav)
            .apply {
                lifecycleOwner = this@NavActivity
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.nav_host_fragment)
            .navigateUp() || super.onSupportNavigateUp()
    }

    lateinit var viewModel: NavModel
    override fun init() {
        viewModel = ViewModelProvider(this).get(NavModel::class.java)
        ViewModelProvider(this).get(ApplyModel::class.java).apply {
            title = this@NavActivity.title
            creditId = this@NavActivity.creditId
            dhId = this@NavActivity.dhId
            ysxId = this@NavActivity.ysxId
            keyId = this@NavActivity.keyId
            jsonObject = this@NavActivity.jsonObject
            seeOnly = this@NavActivity.seeOnly
            businessType = this@NavActivity.businessType
            viewModel.title = this@NavActivity.title
            viewModel.creditId = this@NavActivity.creditId
            viewModel.dhId = this@NavActivity.dhId
            viewModel.ysxId = this@NavActivity.ysxId
            viewModel.keyId = this@NavActivity.keyId
            viewModel.jsonObject = this@NavActivity.jsonObject
            viewModel.seeOnly = this@NavActivity.seeOnly
            viewModel.businessType = this@NavActivity.businessType
        }
//        Watermark.instance?.show(this, "Fantasy BlogDemo")
        dataBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            finish()
        }
        val navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navGraph: NavGraph = navHostFragment.navController.navInflater.inflate(R.navigation.nav_graph_single)
        navGraph.setStartDestination(getActionByTitle(title))
        navHostFragment.navController.graph = navGraph
    }

    /**
    登录后刷新数据，
     */
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(Constants.BusAction.Bus_LoginSuccess)])
    fun loginSuccess(str: String) {
        ((supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?)?.childFragmentManager?.primaryNavigationFragment as? MyBaseFragment)?.refreshData()
    }

    private fun getActionByTitle(title: String): Int = when (title) {
        "信息概况" -> {
            R.id.customerInfoFragment
        }
        "风险探测" -> {
            R.id.riskFragment
        }
        "征信授权" -> {
            R.id.creditFragment
        }
        "征信授权书" -> {
            R.id.creditAuthorizationFragment
        }
        "征信授权-资料上传" -> {
            R.id.creditAuthorizationFragment
        }
        "查看征信解析" -> {
            R.id.creditAnalysisFragment
        }
        "司法查询" -> {
            R.id.creditHuiFaWangFragment
        }
        "我行业务" -> {
            R.id.bankDataFragment
        }
        "修改密码" -> {
            R.id.modifyPasswordFragment
        }
        "影像资料" -> {
            R.id.picListFragment
        }
        "离柜放款验证单" -> {
            R.id.LGFKYZDFragment
        }
        "问卷汇总" -> {
            R.id.WenJuanHuiZongFragment
        }
        "授信比对" -> {
            R.id.WenJuanHuiZongFragment
        }
        "额度比对" -> {
            R.id.WenJuanHuiZongFragment
        }
        "共有人权利" -> {
            R.id.GuaranteeBetShareFragment
        }
        "合同签约" -> {
            R.id.contractFragment
        }
        "阳光预授信" -> {
            R.id.addYSXFragment
        }
        "扶贫走访" -> {
            R.id.addFuPinListFragment
        }


        else -> R.id.customerInfoFragment
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (SZWUtils.isShouldHideKeyboard(v, ev)) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                dataBind.viewOverlay.isFocusable = true
                dataBind.viewOverlay.isFocusableInTouchMode = true
                dataBind.viewOverlay.requestFocus()
                (v as EditText).clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
    override fun onDestroy() {
        RxBus.get().post(Constants.BusAction.Bus_Refresh_List, Constants.BusAction.Bus_Refresh_List)
        super.onDestroy()
    }
}