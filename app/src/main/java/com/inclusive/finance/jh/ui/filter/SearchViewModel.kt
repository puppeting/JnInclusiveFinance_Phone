/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.inclusive.finance.jh.ui.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inclusive.finance.jh.bean.model.ApplyModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
     filtersViewModelDelegate: FiltersViewModelDelegate
) : ViewModel(), FiltersViewModelDelegate by filtersViewModelDelegate {

    private val _isEmpty = MutableStateFlow(true)
    val isEmpty: StateFlow<Boolean> = _isEmpty
    var searchListener:(selectList:MutableList<FilterChip>,searchStr:String,  resultCountCallBack: (count: String)->Unit)->Unit = { mutableList, s ,c-> }
    private var searchJob: Job? = null
    var businessType: Int = ApplyModel.BUSINESS_TYPE_APPLY


    // Override because we also want to show result count when there's a text query.
    private val _showResultCount = MutableStateFlow(false)
    override val showResultCount: StateFlow<Boolean> = _showResultCount

    var textQuery:String



//    private var _filters = MutableStateFlow<MutableList<Filter>>(
//        Collections.emptyList()
//    )
//    private var filters: StateFlow<MutableList<Filter>> = _filters
//    var dataList: MutableList<Filter>
    init {
//        val elements_1 = Filter.SearchFilter(
//            Tag(
//                "9",
//                "客户姓名",
//                hint = "请输入客户姓名或身份证号",
//                valueName = "0",
//                keyName = "0"
//            )
//        )
//        val elements0 = Filter.TagFilter(Tag("0","sfbr" ,"是否本人发起", "0", valueName = "本人发起", keyName = "本人发起", isSingleCheck = false))
//        val elements0_1 = Filter.TagFilter(Tag("10","sfbr" ,"是否本人发起", "0", valueName = "协办复核", keyName = "协办复核", isSingleCheck = false))
//        val elements2 = Filter.TagFilter(Tag("2","sfqp" ,"是否签批", valueName = "是", keyName = "1", isSingleCheck = false))
//        val elements3 = Filter.TagFilter(Tag("3","sfqp" ,"是否签批", valueName = "否", keyName = "0", isSingleCheck = false))
//        val elements4 = Filter.TagFilter(Tag("4","lczt" ,"流程状态", valueName = "全部", keyName = "", isSingleCheck = true))
//        val elements5 = Filter.TagFilter(Tag("5","lczt" ,"流程状态", valueName = "流程中", keyName = "流程中", isSingleCheck = true))
//        val elements6 = Filter.TagFilter(Tag("6","lczt" ,"流程状态", valueName = "待处理", keyName = "待处理", isSingleCheck = true))
//        val elements7 = Filter.TagFilter(Tag("7","lczt" ,"流程状态", valueName = "已完成", keyName = "已完成", isSingleCheck = true))
//        val elements8 = Filter.TagFilter(Tag("8","lczt" ,"流程状态", valueName = "已终止", keyName = "已终止", isSingleCheck = true))
//        dataList = mutableListOf(
////            elements_1,
//            elements0,
//            elements0_1,
//            elements2,
//            elements3,
//            elements4,
//            elements5,
//            elements6,
//            elements7,
//            elements8
//        )
//        viewModelScope.launch {
//            filters.collect {
//                setSupportedFilters(it)
//            }
//        }
//        viewModelScope.launch {
//            delay(500)
//            _filters.value = dataList
//        }

        textQuery= ""
         // Re-execute search when selected filters change
        viewModelScope.launch {
            selectedFilters.collect {
                executeSearch()
            }
        }

    }
    fun onSearchQueryChanged(query: String) {
        val newQuery = query.trim().takeIf { it.length >= 2 } ?: ""
        if (textQuery != newQuery) {
            textQuery = newQuery
            executeSearch()
        }
    }
//    fun onJGMCQueryChanged(query: String) {
//        jgmcQuery = query
//            executeSearch()
//
//    }
    override fun executeSearch() {
        // Cancel any in-flight searches
        searchJob?.cancel()

        val filters = selectedFilters.value
        if (textQuery.isEmpty() && filters.isEmpty()) {
            clearSearchResults()
        }

        searchJob = viewModelScope.launch {
            // The user could be typing or toggling filters rapidly. Giving the search job
            // a slight delay and cancelling it on each call to this method effectively debounces.
            delay(500)
            searchListener.invoke(selectedFilterChips.value,textQuery){
                processSearchResult(it)
            }
        }
    }

    private fun clearSearchResults() {
        // Explicitly set false to not show the "No results" state
        _isEmpty.value = false
        _showResultCount.value = false
        resultCount.value = 0
    }

    private fun processSearchResult(count: String) {
        _showResultCount.value = true
        _isEmpty.value =true
        resultCount.value = try {
            count.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }
}
