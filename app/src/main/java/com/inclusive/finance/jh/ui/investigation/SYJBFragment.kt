package com.inclusive.finance.jh.ui.investigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.inclusive.finance.jh.BR
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentSyjbBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.InformationPop
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act
import java.math.BigDecimal

/**
 * 损益简表
 * */
class SYJBFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentSyjbBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentSyjbBinding.inflate(inflater, container, false).apply {
            presenterClick = this@SYJBFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@SYJBFragment)
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.setItemViewCacheSize(30)
        viewBind.mRecyclerView.adapter=adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            if (!this.adapter.data[position].editable&& this.adapter.data[position].valueName.isNotEmpty())
            InformationPop(context,this.adapter.data[position].valueName).showPopupWindow(view)
        }
    }

    override fun initData() {
        DataCtrlClass.KHGLNet.getBaseTypePoPList(requireActivity(), url = Urls.getSYJB, keyId = viewModel.keyId) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)
                when (viewModel.businessType) {
                    //消费类
                    ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE -> {
                        it.forEach { bean ->
                            when (bean.dataKey) {
                                "lastYear102",
                                "lastYear103",
                                "lastYear104",
                                "lastYear105",
                                "lastYear106",
                                "lastYear107",
                                "lastYear112",
                                "lastYear113",
                                "lastYear114",
                                "lastYear115",
                                "lastYear116",
                                "lastYear117",
                                "lastYear118",
                                "lastYear119",
                                "lastYear120",
                                "lastYear121",
                                "lastYear122",
                                "lastYear123",
                                "lastYear124",
                                "lastYear125",
                                "lastYear126",
                                "lastYear127",
                                "lastYear128",
                                "lastYear130",
                                "curYear102",
                                "curYear103",
                                "curYear104",
                                "curYear105",
                                "curYear106",
                                "curYear107",
                                "curYear112",
                                "curYear113",
                                "curYear114",
                                "curYear115",
                                "curYear116",
                                "curYear117",
                                "curYear118",
                                "curYear119",
                                "curYear120",
                                "curYear121",
                                "curYear122",
                                "curYear123",
                                "curYear124",
                                "curYear125",
                                "curYear126",
                                "curYear127",
                                "curYear128",
                                "curYear130",
                                "curYearForecast102",
                                "curYearForecast103",
                                "curYearForecast104",
                                "curYearForecast105",
                                "curYearForecast106",
                                "curYearForecast107",
                                "curYearForecast112",
                                "curYearForecast113",
                                "curYearForecast114",
                                "curYearForecast115",
                                "curYearForecast116",
                                "curYearForecast117",
                                "curYearForecast118",
                                "curYearForecast119",
                                "curYearForecast120",
                                "curYearForecast121",
                                "curYearForecast122",
                                "curYearForecast123",
                                "curYearForecast124",
                                "curYearForecast125",
                                "curYearForecast126",
                                "curYearForecast127",
                                "curYearForecast128",
                                "curYearForecast130",


                                -> {
                                    bean.addOnPropertyChangedCallback(object :
                                        Observable.OnPropertyChangedCallback() {
                                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                                            if (propertyId == BR.valueName) {
                                                val lastYear102 = SZWUtils.getCalculateCount(it, "lastYear102")
                                                val lastYear103 = SZWUtils.getCalculateCount(it, "lastYear103")
                                                val lastYear104 = SZWUtils.getCalculateCount(it, "lastYear104")
                                                val lastYear105 = SZWUtils.getCalculateCount(it, "lastYear105")
                                                val lastYear106 = SZWUtils.getCalculateCount(it, "lastYear106")
                                                val lastYear107 = SZWUtils.getCalculateCount(it, "lastYear107")
                                                val lastYear112 = SZWUtils.getCalculateCount(it, "lastYear112")
                                                val lastYear113 = SZWUtils.getCalculateCount(it, "lastYear113")
                                                val lastYear114 = SZWUtils.getCalculateCount(it, "lastYear114")
                                                val lastYear115 = SZWUtils.getCalculateCount(it, "lastYear115")
                                                val lastYear116 = SZWUtils.getCalculateCount(it, "lastYear116")
                                                val lastYear117 = SZWUtils.getCalculateCount(it, "lastYear117")
                                                val lastYear118 = SZWUtils.getCalculateCount(it, "lastYear118")
                                                val lastYear119 = SZWUtils.getCalculateCount(it, "lastYear119")
                                                val lastYear120 = SZWUtils.getCalculateCount(it, "lastYear120")
                                                val lastYear121 = SZWUtils.getCalculateCount(it, "lastYear121")
                                                val lastYear122 = SZWUtils.getCalculateCount(it, "lastYear122")
                                                val lastYear123 = SZWUtils.getCalculateCount(it, "lastYear123")
                                                val lastYear124 = SZWUtils.getCalculateCount(it, "lastYear124")
                                                val lastYear125 = SZWUtils.getCalculateCount(it, "lastYear125")
                                                val lastYear126 = SZWUtils.getCalculateCount(it, "lastYear126")
                                                val lastYear127 = SZWUtils.getCalculateCount(it, "lastYear127")
                                                val lastYear128 = SZWUtils.getCalculateCount(it, "lastYear128")
                                                val lastYear130 = SZWUtils.getCalculateCount(it, "lastYear130")
                                                val curYear102 = SZWUtils.getCalculateCount(it, "curYear102")
                                                val curYear103 = SZWUtils.getCalculateCount(it, "curYear103")
                                                val curYear104 = SZWUtils.getCalculateCount(it, "curYear104")
                                                val curYear105 = SZWUtils.getCalculateCount(it, "curYear105")
                                                val curYear106 = SZWUtils.getCalculateCount(it, "curYear106")
                                                val curYear107 = SZWUtils.getCalculateCount(it, "curYear107")
                                                val curYear112 = SZWUtils.getCalculateCount(it, "curYear112")
                                                val curYear113 = SZWUtils.getCalculateCount(it, "curYear113")
                                                val curYear114 = SZWUtils.getCalculateCount(it, "curYear114")
                                                val curYear115 = SZWUtils.getCalculateCount(it, "curYear115")
                                                val curYear116 = SZWUtils.getCalculateCount(it, "curYear116")
                                                val curYear117 = SZWUtils.getCalculateCount(it, "curYear117")
                                                val curYear118 = SZWUtils.getCalculateCount(it, "curYear118")
                                                val curYear119 = SZWUtils.getCalculateCount(it, "curYear119")
                                                val curYear120 = SZWUtils.getCalculateCount(it, "curYear120")
                                                val curYear121 = SZWUtils.getCalculateCount(it, "curYear121")
                                                val curYear122 = SZWUtils.getCalculateCount(it, "curYear122")
                                                val curYear123 = SZWUtils.getCalculateCount(it, "curYear123")
                                                val curYear124 = SZWUtils.getCalculateCount(it, "curYear124")
                                                val curYear125 = SZWUtils.getCalculateCount(it, "curYear125")
                                                val curYear126 = SZWUtils.getCalculateCount(it, "curYear126")
                                                val curYear127 = SZWUtils.getCalculateCount(it, "curYear127")
                                                val curYear128 = SZWUtils.getCalculateCount(it, "curYear128")
                                                val curYear130 = SZWUtils.getCalculateCount(it, "curYear130")
                                                val curYearForecast102 = SZWUtils.getCalculateCount(it, "curYearForecast102")
                                                val curYearForecast103 = SZWUtils.getCalculateCount(it, "curYearForecast103")
                                                val curYearForecast104 = SZWUtils.getCalculateCount(it, "curYearForecast104")
                                                val curYearForecast105 = SZWUtils.getCalculateCount(it, "curYearForecast105")
                                                val curYearForecast106 = SZWUtils.getCalculateCount(it, "curYearForecast106")
                                                val curYearForecast107 = SZWUtils.getCalculateCount(it, "curYearForecast107")
                                                val curYearForecast112 = SZWUtils.getCalculateCount(it, "curYearForecast112")
                                                val curYearForecast113 = SZWUtils.getCalculateCount(it, "curYearForecast113")
                                                val curYearForecast114 = SZWUtils.getCalculateCount(it, "curYearForecast114")
                                                val curYearForecast115 = SZWUtils.getCalculateCount(it, "curYearForecast115")
                                                val curYearForecast116 = SZWUtils.getCalculateCount(it, "curYearForecast116")
                                                val curYearForecast117 = SZWUtils.getCalculateCount(it, "curYearForecast117")
                                                val curYearForecast118 = SZWUtils.getCalculateCount(it, "curYearForecast118")
                                                val curYearForecast119 = SZWUtils.getCalculateCount(it, "curYearForecast119")
                                                val curYearForecast120 = SZWUtils.getCalculateCount(it, "curYearForecast120")
                                                val curYearForecast121 = SZWUtils.getCalculateCount(it, "curYearForecast121")
                                                val curYearForecast122 = SZWUtils.getCalculateCount(it, "curYearForecast122")
                                                val curYearForecast123 = SZWUtils.getCalculateCount(it, "curYearForecast123")
                                                val curYearForecast124 = SZWUtils.getCalculateCount(it, "curYearForecast124")
                                                val curYearForecast125 = SZWUtils.getCalculateCount(it, "curYearForecast125")
                                                val curYearForecast126 = SZWUtils.getCalculateCount(it, "curYearForecast126")
                                                val curYearForecast127 = SZWUtils.getCalculateCount(it, "curYearForecast127")
                                                val curYearForecast128 = SZWUtils.getCalculateCount(it, "curYearForecast128")
                                                val curYearForecast130 = SZWUtils.getCalculateCount(it, "curYearForecast130")

                                                val lastYearAll = BigDecimal(lastYear103 + lastYear104 + lastYear105 + lastYear106 + lastYear107)
                                                val lastYearFull = BigDecimal(lastYear102 + lastYear104 + lastYear107)
                                                val lastYearPayAll = BigDecimal(lastYear116 + lastYear117 + lastYear118 + lastYear119 + lastYear120 + lastYear121 + lastYear122 + lastYear123 + lastYear124 + lastYear125 + lastYear126 + lastYear127 + lastYear128)
                                                val lastYearPayFull = BigDecimal(lastYear112 + lastYear113 + lastYear114 + lastYear115)
                                                val lastYearFamilyFullIncome =lastYearFull.subtract(lastYearPayFull)
                                                val lastYearZYML = BigDecimal(lastYear103 - lastYear116)
                                                val lastYearJLR = lastYearAll.subtract(lastYearPayAll)
                                                    .subtract(BigDecimal(lastYear130))

                                                val curYearAll = BigDecimal(curYear103 + curYear104 + curYear105 + curYear106 + curYear107)
                                                val curYearFull = BigDecimal(curYear102 + curYear104 + curYear107)
                                                val curYearPayAll = BigDecimal(curYear116 + curYear117 + curYear118 + curYear119 + curYear120 + curYear121 + curYear122 + curYear123 + curYear124 + curYear125 + curYear126 + curYear127 + curYear128)
                                                val curYearPayFull = BigDecimal(curYear112 + curYear113 + curYear114 + curYear115)
                                                val curYearFamilyFullIncome = curYearFull.subtract(curYearPayFull)
                                                val curYearZYML = BigDecimal(curYear103 - curYear116)
                                                val curYearJLR = curYearAll.subtract(curYearPayAll)
                                                    .subtract(BigDecimal(curYear130))

                                                val curYearForecastAll = BigDecimal(curYearForecast103 + curYearForecast104 + curYearForecast105 + curYearForecast106 + curYearForecast107)
                                                val curYearForecastFull = BigDecimal(curYearForecast102 + curYearForecast104 + curYearForecast107)
                                                val curYearForecastPayAll = BigDecimal(curYearForecast116 + curYearForecast117 + curYearForecast118 + curYearForecast119 + curYearForecast120 + curYearForecast121 + curYearForecast122 + curYearForecast123 + curYearForecast124 + curYearForecast125 + curYearForecast126 + curYearForecast127 + curYearForecast128)
                                                val curYearForecastPayFull = BigDecimal(curYearForecast112 + curYearForecast113 + curYearForecast114 + curYearForecast115)
                                                val curYearForecastFamilyFullIncome =curYearForecastFull.subtract(curYearForecastPayFull)
                                                val curYearForecastZYML = BigDecimal(curYearForecast103 - curYearForecast116)
                                                val curYearForecastJLR = curYearForecastAll.subtract(curYearForecastPayAll)
                                                    .subtract(BigDecimal(curYearForecast130))

                                                //总收入
                                                SZWUtils.setCalculateCount(it, "lastYear100", lastYearAll)
                                                SZWUtils.setCalculateCount(it, "curYear100", curYearAll)
                                                SZWUtils.setCalculateCount(it, "curYearForecast100", curYearForecastAll)
                                                //年总收入
                                                SZWUtils.setCalculateCount(it, "lastYear101", lastYearFull)
                                                SZWUtils.setCalculateCount(it, "curYear101", curYearFull)
                                                SZWUtils.setCalculateCount(it, "curYearForecast101", curYearForecastFull)
                                                //家庭年净收入
                                                SZWUtils.setCalculateCount(it, "lastYear108", lastYearFamilyFullIncome)
                                                SZWUtils.setCalculateCount(it, "curYear108", curYearFamilyFullIncome)
                                                SZWUtils.setCalculateCount(it, "curYearForecast108", curYearForecastFamilyFullIncome)
                                                //家庭月净收入
                                                SZWUtils.setCalculateCount(it, "lastYear109", if (lastYearFamilyFullIncome > BigDecimal.ZERO) lastYearFamilyFullIncome.divide(BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP) else BigDecimal.ZERO)
                                                SZWUtils.setCalculateCount(it, "curYear109", if (curYearFamilyFullIncome > BigDecimal.ZERO) curYearFamilyFullIncome.divide(BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP) else BigDecimal.ZERO)
                                                SZWUtils.setCalculateCount(it, "curYearForecast109", if (curYearForecastFamilyFullIncome > BigDecimal.ZERO) curYearForecastFamilyFullIncome.divide(BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP) else BigDecimal.ZERO)
                                                //总支出
                                                SZWUtils.setCalculateCount(it, "lastYear110", lastYearPayAll)
                                                SZWUtils.setCalculateCount(it, "curYear110", curYearPayAll)
                                                SZWUtils.setCalculateCount(it, "curYearForecast110", curYearForecastPayAll)
                                                //年总支出
                                                SZWUtils.setCalculateCount(it, "lastYear111", lastYearPayFull)
                                                SZWUtils.setCalculateCount(it, "curYear111", curYearPayFull)
                                                SZWUtils.setCalculateCount(it, "curYearForecast111", curYearForecastPayFull)
                                                //主营毛利润
                                                SZWUtils.setCalculateCount(it, "lastYear129", lastYearZYML)
                                                SZWUtils.setCalculateCount(it, "curYear129", curYearZYML)
                                                SZWUtils.setCalculateCount(it, "curYearForecast129", curYearForecastZYML)
                                                //净利润
                                                SZWUtils.setCalculateCount(it, "lastYear131", lastYearJLR)
                                                SZWUtils.setCalculateCount(it, "curYear131", curYearJLR)
                                                SZWUtils.setCalculateCount(it, "curYearForecast131", curYearForecastJLR)
                                                //月度净利润
                                                SZWUtils.setCalculateCount(it, "lastYear132", if (lastYearJLR > BigDecimal.ZERO) lastYearJLR.divide(BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP) else BigDecimal.ZERO)
                                                SZWUtils.setCalculateCount(it, "curYear132", if (curYearJLR > BigDecimal.ZERO) curYearJLR.divide(BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP) else BigDecimal.ZERO)
                                                SZWUtils.setCalculateCount(it, "curYearForecast132", if (curYearForecastJLR > BigDecimal.ZERO) curYearForecastJLR.divide(BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP) else BigDecimal.ZERO)
                                                //家庭净收入
                                                SZWUtils.setCalculateCount(it, "lastYear133", lastYearAll.subtract(lastYearPayAll))
                                                SZWUtils.setCalculateCount(it, "curYear133", curYearAll.subtract(curYearPayAll))
                                                SZWUtils.setCalculateCount(it, "curYearForecast133", curYearForecastAll.subtract(curYearForecastPayAll))

                                            }
                                        }
                                    })
                                }
                            }
                        }
                    }
                    else -> {
                        it.forEach { bean ->
                            when (bean.dataKey) {
                                "lastYear102",
                                "lastYear103",
                                "lastYear104",
                                "lastYear105",
                                "lastYear106",
                                "lastYear107",
                                "lastYear112",
                                "lastYear113",
                                "lastYear114",
                                "lastYear115",
                                "lastYear116",
                                "lastYear117",
                                "lastYear118",
                                "lastYear119",
                                "lastYear120",
                                "lastYear121",
                                "lastYear122",
                                "lastYear123",
                                "lastYear124",
                                "lastYear125",
                                "lastYear126",
                                "lastYear127",
                                "lastYear128",
                                "lastYear130",
                                "curYear102",
                                "curYear103",
                                "curYear104",
                                "curYear105",
                                "curYear106",
                                "curYear107",
                                "curYear112",
                                "curYear113",
                                "curYear114",
                                "curYear115",
                                "curYear116",
                                "curYear117",
                                "curYear118",
                                "curYear119",
                                "curYear120",
                                "curYear121",
                                "curYear122",
                                "curYear123",
                                "curYear124",
                                "curYear125",
                                "curYear126",
                                "curYear127",
                                "curYear128",
                                "curYear130",
                                "curYearForecast102",
                                "curYearForecast103",
                                "curYearForecast104",
                                "curYearForecast105",
                                "curYearForecast106",
                                "curYearForecast107",
                                "curYearForecast112",
                                "curYearForecast113",
                                "curYearForecast114",
                                "curYearForecast115",
                                "curYearForecast116",
                                "curYearForecast117",
                                "curYearForecast118",
                                "curYearForecast119",
                                "curYearForecast120",
                                "curYearForecast121",
                                "curYearForecast122",
                                "curYearForecast123",
                                "curYearForecast124",
                                "curYearForecast125",
                                "curYearForecast126",
                                "curYearForecast127",
                                "curYearForecast128",
                                "curYearForecast130",


                                -> {
                                    bean.addOnPropertyChangedCallback(object :
                                        Observable.OnPropertyChangedCallback() {
                                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                                            if (propertyId == BR.valueName) {
                                                val lastYear102 = SZWUtils.getCalculateCount(it, "lastYear102")
                                                val lastYear103 = SZWUtils.getCalculateCount(it, "lastYear103")
                                                val lastYear104 = SZWUtils.getCalculateCount(it, "lastYear104")
                                                val lastYear105 = SZWUtils.getCalculateCount(it, "lastYear105")
                                                val lastYear106 = SZWUtils.getCalculateCount(it, "lastYear106")
                                                val lastYear107 = SZWUtils.getCalculateCount(it, "lastYear107")
                                                val lastYear112 = SZWUtils.getCalculateCount(it, "lastYear112")
                                                val lastYear113 = SZWUtils.getCalculateCount(it, "lastYear113")
                                                val lastYear114 = SZWUtils.getCalculateCount(it, "lastYear114")
                                                val lastYear115 = SZWUtils.getCalculateCount(it, "lastYear115")
                                                val lastYear116 = SZWUtils.getCalculateCount(it, "lastYear116")
                                                val lastYear117 = SZWUtils.getCalculateCount(it, "lastYear117")
                                                val lastYear118 = SZWUtils.getCalculateCount(it, "lastYear118")
                                                val lastYear119 = SZWUtils.getCalculateCount(it, "lastYear119")
                                                val lastYear120 = SZWUtils.getCalculateCount(it, "lastYear120")
                                                val lastYear121 = SZWUtils.getCalculateCount(it, "lastYear121")
                                                val lastYear122 = SZWUtils.getCalculateCount(it, "lastYear122")
                                                val lastYear123 = SZWUtils.getCalculateCount(it, "lastYear123")
                                                val lastYear124 = SZWUtils.getCalculateCount(it, "lastYear124")
                                                val lastYear125 = SZWUtils.getCalculateCount(it, "lastYear125")
                                                val lastYear126 = SZWUtils.getCalculateCount(it, "lastYear126")
                                                val lastYear127 = SZWUtils.getCalculateCount(it, "lastYear127")
                                                val lastYear128 = SZWUtils.getCalculateCount(it, "lastYear128")
                                                val lastYear130 = SZWUtils.getCalculateCount(it, "lastYear130")
                                                val curYear102 = SZWUtils.getCalculateCount(it, "curYear102")
                                                val curYear103 = SZWUtils.getCalculateCount(it, "curYear103")
                                                val curYear104 = SZWUtils.getCalculateCount(it, "curYear104")
                                                val curYear105 = SZWUtils.getCalculateCount(it, "curYear105")
                                                val curYear106 = SZWUtils.getCalculateCount(it, "curYear106")
                                                val curYear107 = SZWUtils.getCalculateCount(it, "curYear107")
                                                val curYear112 = SZWUtils.getCalculateCount(it, "curYear112")
                                                val curYear113 = SZWUtils.getCalculateCount(it, "curYear113")
                                                val curYear114 = SZWUtils.getCalculateCount(it, "curYear114")
                                                val curYear115 = SZWUtils.getCalculateCount(it, "curYear115")
                                                val curYear116 = SZWUtils.getCalculateCount(it, "curYear116")
                                                val curYear117 = SZWUtils.getCalculateCount(it, "curYear117")
                                                val curYear118 = SZWUtils.getCalculateCount(it, "curYear118")
                                                val curYear119 = SZWUtils.getCalculateCount(it, "curYear119")
                                                val curYear120 = SZWUtils.getCalculateCount(it, "curYear120")
                                                val curYear121 = SZWUtils.getCalculateCount(it, "curYear121")
                                                val curYear122 = SZWUtils.getCalculateCount(it, "curYear122")
                                                val curYear123 = SZWUtils.getCalculateCount(it, "curYear123")
                                                val curYear124 = SZWUtils.getCalculateCount(it, "curYear124")
                                                val curYear125 = SZWUtils.getCalculateCount(it, "curYear125")
                                                val curYear126 = SZWUtils.getCalculateCount(it, "curYear126")
                                                val curYear127 = SZWUtils.getCalculateCount(it, "curYear127")
                                                val curYear128 = SZWUtils.getCalculateCount(it, "curYear128")
                                                val curYear130 = SZWUtils.getCalculateCount(it, "curYear130")
                                                val curYearForecast102 = SZWUtils.getCalculateCount(it, "curYearForecast102")
                                                val curYearForecast103 = SZWUtils.getCalculateCount(it, "curYearForecast103")
                                                val curYearForecast104 = SZWUtils.getCalculateCount(it, "curYearForecast104")
                                                val curYearForecast105 = SZWUtils.getCalculateCount(it, "curYearForecast105")
                                                val curYearForecast106 = SZWUtils.getCalculateCount(it, "curYearForecast106")
                                                val curYearForecast107 = SZWUtils.getCalculateCount(it, "curYearForecast107")
                                                val curYearForecast112 = SZWUtils.getCalculateCount(it, "curYearForecast112")
                                                val curYearForecast113 = SZWUtils.getCalculateCount(it, "curYearForecast113")
                                                val curYearForecast114 = SZWUtils.getCalculateCount(it, "curYearForecast114")
                                                val curYearForecast115 = SZWUtils.getCalculateCount(it, "curYearForecast115")
                                                val curYearForecast116 = SZWUtils.getCalculateCount(it, "curYearForecast116")
                                                val curYearForecast117 = SZWUtils.getCalculateCount(it, "curYearForecast117")
                                                val curYearForecast118 = SZWUtils.getCalculateCount(it, "curYearForecast118")
                                                val curYearForecast119 = SZWUtils.getCalculateCount(it, "curYearForecast119")
                                                val curYearForecast120 = SZWUtils.getCalculateCount(it, "curYearForecast120")
                                                val curYearForecast121 = SZWUtils.getCalculateCount(it, "curYearForecast121")
                                                val curYearForecast122 = SZWUtils.getCalculateCount(it, "curYearForecast122")
                                                val curYearForecast123 = SZWUtils.getCalculateCount(it, "curYearForecast123")
                                                val curYearForecast124 = SZWUtils.getCalculateCount(it, "curYearForecast124")
                                                val curYearForecast125 = SZWUtils.getCalculateCount(it, "curYearForecast125")
                                                val curYearForecast126 = SZWUtils.getCalculateCount(it, "curYearForecast126")
                                                val curYearForecast127 = SZWUtils.getCalculateCount(it, "curYearForecast127")
                                                val curYearForecast128 = SZWUtils.getCalculateCount(it, "curYearForecast128")
                                                val curYearForecast130 = SZWUtils.getCalculateCount(it, "curYearForecast130")

                                                val lastYearAll = BigDecimal(lastYear103 + lastYear104 + lastYear105 + lastYear106 + lastYear107)
                                                val lastYearFull = BigDecimal(lastYear102 + lastYear104 + lastYear107)
                                                val lastYearPayAll = BigDecimal(lastYear116 + lastYear117 + lastYear118 + lastYear119 + lastYear120 + lastYear121 + lastYear122 + lastYear123 + lastYear124 + lastYear125 + lastYear126 + lastYear127 + lastYear128)
                                                val lastYearPayFull = BigDecimal(lastYear112 + lastYear113 + lastYear114 + lastYear115)
                                                val lastYearFamilyFullIncome =lastYearFull.subtract(lastYearPayFull)
                                                val lastYearZYML = BigDecimal(lastYear103 - lastYear116)
                                                val lastYearJLR = lastYearAll.subtract(lastYearPayAll)
                                                    .subtract(BigDecimal(lastYear130))

                                                val curYearAll = BigDecimal(curYear103 + curYear104 + curYear105 + curYear106 + curYear107)
                                                val curYearFull = BigDecimal(curYear102 + curYear104 + curYear107)
                                                val curYearPayAll = BigDecimal(curYear116 + curYear117 + curYear118 + curYear119 + curYear120 + curYear121 + curYear122 + curYear123 + curYear124 + curYear125 + curYear126 + curYear127 + curYear128)
                                                val curYearPayFull = BigDecimal(curYear112 + curYear113 + curYear114 + curYear115)
                                                val curYearFamilyFullIncome = curYearFull.subtract(curYearPayFull)
                                                val curYearZYML = BigDecimal(curYear103 - curYear116)
                                                val curYearJLR = curYearAll.subtract(curYearPayAll)
                                                    .subtract(BigDecimal(curYear130))

                                                val curYearForecastAll = BigDecimal(curYearForecast103 + curYearForecast104 + curYearForecast105 + curYearForecast106 + curYearForecast107)
                                                val curYearForecastFull = BigDecimal(curYearForecast102 + curYearForecast104 + curYearForecast107)
                                                val curYearForecastPayAll = BigDecimal(curYearForecast116 + curYearForecast117 + curYearForecast118 + curYearForecast119 + curYearForecast120 + curYearForecast121 + curYearForecast122 + curYearForecast123 + curYearForecast124 + curYearForecast125 + curYearForecast126 + curYearForecast127 + curYearForecast128)
                                                val curYearForecastPayFull = BigDecimal(curYearForecast112 + curYearForecast113 + curYearForecast114 + curYearForecast115)
                                                val curYearForecastFamilyFullIncome =curYearForecastFull.subtract(curYearForecastPayFull)
                                                val curYearForecastZYML = BigDecimal(curYearForecast103 - curYearForecast116)
                                                val curYearForecastJLR = curYearForecastAll.subtract(curYearForecastPayAll)
                                                    .subtract(BigDecimal(curYearForecast130))

                                                //总收入
                                                SZWUtils.setCalculateCount(it, "lastYear100", lastYearAll)
                                                SZWUtils.setCalculateCount(it, "curYear100", curYearAll)
                                                SZWUtils.setCalculateCount(it, "curYearForecast100", curYearForecastAll)
                                                //年总收入
                                                SZWUtils.setCalculateCount(it, "lastYear101", lastYearFull)
                                                SZWUtils.setCalculateCount(it, "curYear101", curYearFull)
                                                SZWUtils.setCalculateCount(it, "curYearForecast101", curYearForecastFull)
                                                //家庭年净收入
                                                SZWUtils.setCalculateCount(it, "lastYear108", lastYearFamilyFullIncome)
                                                SZWUtils.setCalculateCount(it, "curYear108", curYearFamilyFullIncome)
                                                SZWUtils.setCalculateCount(it, "curYearForecast108", curYearForecastFamilyFullIncome)
                                                //家庭月净收入
                                                SZWUtils.setCalculateCount(it, "lastYear109", if (lastYearFamilyFullIncome > BigDecimal.ZERO) lastYearFamilyFullIncome.divide(BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP) else BigDecimal.ZERO)
                                                SZWUtils.setCalculateCount(it, "curYear109", if (curYearFamilyFullIncome > BigDecimal.ZERO) curYearFamilyFullIncome.divide(BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP) else BigDecimal.ZERO)
                                                SZWUtils.setCalculateCount(it, "curYearForecast109", if (curYearForecastFamilyFullIncome > BigDecimal.ZERO) curYearForecastFamilyFullIncome.divide(BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP) else BigDecimal.ZERO)
                                                //总支出
                                                SZWUtils.setCalculateCount(it, "lastYear110", lastYearPayAll)
                                                SZWUtils.setCalculateCount(it, "curYear110", curYearPayAll)
                                                SZWUtils.setCalculateCount(it, "curYearForecast110", curYearForecastPayAll)
                                                //年总支出
                                                SZWUtils.setCalculateCount(it, "lastYear111", lastYearPayFull)
                                                SZWUtils.setCalculateCount(it, "curYear111", curYearPayFull)
                                                SZWUtils.setCalculateCount(it, "curYearForecast111", curYearForecastPayFull)
                                                //主营毛利润
                                                SZWUtils.setCalculateCount(it, "lastYear129", lastYearZYML)
                                                SZWUtils.setCalculateCount(it, "curYear129", curYearZYML)
                                                SZWUtils.setCalculateCount(it, "curYearForecast129", curYearForecastZYML)
                                                //净利润
                                                SZWUtils.setCalculateCount(it, "lastYear131", lastYearJLR)
                                                SZWUtils.setCalculateCount(it, "curYear131", curYearJLR)
                                                SZWUtils.setCalculateCount(it, "curYearForecast131", curYearForecastJLR)
                                                //月度净利润
                                                SZWUtils.setCalculateCount(it, "lastYear132", if (lastYearJLR > BigDecimal.ZERO) lastYearJLR.divide(BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP) else BigDecimal.ZERO)
                                                SZWUtils.setCalculateCount(it, "curYear132", if (curYearJLR > BigDecimal.ZERO) curYearJLR.divide(BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP) else BigDecimal.ZERO)
                                                SZWUtils.setCalculateCount(it, "curYearForecast132", if (curYearForecastJLR > BigDecimal.ZERO) curYearForecastJLR.divide(BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP) else BigDecimal.ZERO)
                                                //家庭净收入
                                                SZWUtils.setCalculateCount(it, "lastYear133", lastYearAll.subtract(lastYearPayAll))
                                                SZWUtils.setCalculateCount(it, "curYear133", curYearAll.subtract(curYearPayAll))
                                                SZWUtils.setCalculateCount(it, "curYearForecast133", curYearForecastAll.subtract(curYearForecastPayAll))

                                            }
                                        }
                                    })
                                }
                            }
                        }
                    }
                }

                adapter.setNewInstance(it)
            }
        }
    }

    override fun saveData() {
        DataCtrlClass.KHGLNet.saveBaseTypePoPList(context, Urls.saveSYJB, adapter.data, keyId = viewModel.keyId) {
            if (it != null) {
                if (isAdded)
                (activity as BaseActivity).refreshData()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            viewBind.btTemporarySave -> {
                DataCtrlClass.ApplyNet.saveTemporary(context,Urls.saveTemporary_SYJB, adapter.data, keyId = viewModel.keyId) {
                    if (it != null) {
                        if (isAdded)
                (activity as BaseActivity).refreshData()
                    }
                }
            }
            viewBind.btSave -> {
                saveData()
            }
        }
    }

}