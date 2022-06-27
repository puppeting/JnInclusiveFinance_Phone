package com.inclusive.finance.jh.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.navigation.NavigationBarView
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MainNavigationFragment
import com.inclusive.finance.jh.base.NavigationHost
import com.inclusive.finance.jh.databinding.ActivityMainBinding
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import dagger.hilt.android.AndroidEntryPoint


@Route(path = "/com/MainActivity")
@AndroidEntryPoint
class MainActivity : BaseActivity(), NavigationHost {
    lateinit var navHostFragment: NavHostFragment
    private val TOP_LEVEL_DESTINATIONS = setOf(
        R.id.navigation_main,
        R.id.navigation_backlog,
        R.id.navigation_mine,
        R.id.navigation_applyList,
        R.id.navigation_investigationList,
        R.id.navigation_creditApproval,
        R.id.navigation_creditApproval2,
        R.id.navigation_creditApproval3,
        R.id.navigation_creditListFragment,
        R.id.navigation_jiNianJianCaiJiListFragment,
        R.id.navigation_jiNianJianFeiXianChangJianChaListFragment,
        R.id.navigation_jiNianJianXianChangJianChaListFragment,
        R.id.navigation_JiNianJianYongXinListFragment,
        R.id.navigation_VisitListFragment,
        R.id.navigation_PreCreditFragment,
        R.id.navigation_SunPreCreditTaskFragment,
        R.id.navigation_VisitApprovalFragment,
        R.id.navigation_ClockInListFragment,
        R.id.navigation_TrackFragment,
        R.id.navigation_PointClusterFragment,
        R.id.navigation_PicAddListFragment,
        R.id.navigation_YongXinGuanLiListFragment,
        R.id.navigation_QRCodeListFragment,
        R.id.navigation_ShouLiListFragment,
        R.id.navigation_RiChangJianListFragment,
        R.id.navigation_XinXiYuanListFragment,
        R.id.navigation_WenJuanDiaoChaListFragment,
        R.id.navigation_ShouXinPingYiListFragment,
        R.id.navigation_EDuBiDuiListFragment,
    )

    override fun initToolbar() {
        StatusBarUtil.darkMode(this)
    }

    lateinit var viewBind: ActivityMainBinding
    override fun setInflateBinding() {
        viewBind = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
            .apply {
                lifecycleOwner = this@MainActivity
            }

    }

    override fun init() {
//        Watermark.instance?.show(this, "Fantasy BlogDemo")
//        val findNavController = Navigation.findNavController(this, R.id.my_nav_host_fragment)
        onBackPressedDispatcher.addCallback(this,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (!Navigation.findNavController(this@MainActivity, R.id.my_nav_host_fragment).navigateUp()) {
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.addCategory(Intent.CATEGORY_HOME)
                    startActivity(intent)
                }
            }

        })
        navHostFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
        NavigationUI.setupWithNavController(viewBind.bottomNavigation, navHostFragment.navController)
        viewBind.bottomNavigation.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED


        val  onDestinationChangedListener = NavController
            .OnDestinationChangedListener { _, destination, _ ->
                Log.e("lzp", destination.label.toString())
                if ((destination.label=="MainFragment")
                    || (destination.label=="BacklogFragment")
                    ||  (destination.label=="MineFragment")
                ){
                    viewBind.bottomNavigation.visibility= View.VISIBLE
                }else{
                    viewBind.bottomNavigation.visibility= View.GONE
                }

            }
        // 设置监听
        navHostFragment.navController.addOnDestinationChangedListener(onDestinationChangedListener)
        // 移除监听
//        navHostController.removeOnDestinationChangedListener(onDestinationChangedListener)
    }

    override fun onSupportNavigateUp() =
        Navigation.findNavController(this, R.id.my_nav_host_fragment)
            .navigateUp() || super.onSupportNavigateUp()


    override fun onOptionsItemSelected(item: MenuItem) =
        (NavigationUI.onNavDestinationSelected(item, Navigation.findNavController(this, R.id.my_nav_host_fragment)) || super.onOptionsItemSelected(item))


    override fun onUserInteraction() {
        super.onUserInteraction()
        getCurrentFragment()?.onUserInteraction()
    }

    private fun getCurrentFragment(): MainNavigationFragment? {
        return navHostFragment.childFragmentManager.primaryNavigationFragment as? MainNavigationFragment
    }

    override fun registerToolbarWithNavigation(toolbar: Toolbar) {
        val appBarConfiguration = AppBarConfiguration(TOP_LEVEL_DESTINATIONS, null)
        toolbar.setupWithNavController(Navigation.findNavController(this, R.id.my_nav_host_fragment), appBarConfiguration)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (SZWUtils.isShouldHideKeyboard(v, ev)) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                viewBind.viewOverlay.isFocusable = true
                viewBind.viewOverlay.isFocusableInTouchMode = true
                viewBind.viewOverlay.requestFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    //    /**
    //    登录后刷新数据，
    //     */
    //    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(Constants.BusAction.Bus_LoginSuccess)])
    //    fun loginSuccess(str: String) {
    //        (navHostFragment?.childFragmentManager?.primaryNavigationFragment as? MyBaseFragment)?.refreshData()
    //    }

}
