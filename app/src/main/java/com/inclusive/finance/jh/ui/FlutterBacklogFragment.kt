//package com.inclusive.finance.gn.ui
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import androidx.core.view.marginBottom
//import androidx.databinding.DataBindingUtil
//import androidx.fragment.app.FragmentManager
//import androidx.lifecycle.ViewModelProvider
//import com.blankj.utilcode.util.SPUtils
//import com.inclusive.finance.gn.R
//import com.inclusive.finance.gn.adapter.ItemBacklogAdapter
//import com.inclusive.finance.gn.base.MyBaseFragment
//import com.inclusive.finance.gn.bean.model.MainActivityModel
//import com.inclusive.finance.gn.config.Constants.SPUtilsConfig
//import com.inclusive.finance.gn.databinding.FlutterFragmentBacklogBinding
//import com.inclusive.finance.gn.databinding.FragmentBacklogBinding
//import com.inclusive.finance.gn.utils.SZWUtils
//import io.flutter.embedding.android.FlutterFragment
//import io.flutter.embedding.android.TransparencyMode
//import io.flutter.plugin.common.MethodChannel
//import org.jetbrains.anko.support.v4.act
//
//class FlutterBacklogFragment : MyBaseFragment() {
//    lateinit var viewModel: MainActivityModel
//    private var viewBind: FlutterFragmentBacklogBinding? = null
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        viewModel = ViewModelProvider(act).get(MainActivityModel::class.java)
//        if (viewBind == null) {
//            viewBind = FlutterFragmentBacklogBinding.inflate(inflater, container, false).apply {
//                lifecycleOwner = viewLifecycleOwner
//            }
//        }
//        val build = FlutterFragment.withCachedEngine("defaultEngineId").transparencyMode(TransparencyMode.transparent).build<FlutterFragment>()
//        val fragmentManager: FragmentManager = childFragmentManager
//        var flutterFragment = fragmentManager.findFragmentByTag("TAG_FLUTTER_FRAGMENT")
//        if (flutterFragment == null) {
//            flutterFragment = build
//            fragmentManager
//                .beginTransaction()
//                .add(R.id.frameLayout, flutterFragment, "TAG_FLUTTER_FRAGMENT").commit()
//            val channel = MethodChannel(
//                SZWUtils.flutterEngine(requireContext(), "defaultEngineId", "").dartExecutor,
//                "com.flutter.guide.MethodChannel"
//            );
//            channel.invokeMethod("token",SPUtils.getInstance().getString(SPUtilsConfig.SP_TOKEN))
//        }
//
//        return viewBind?.root ?: View(context)
//
//    }
//
//}