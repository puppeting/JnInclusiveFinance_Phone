/*
 * Copyright 2020 Google LLC
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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import java.util.*

/**
 * Interface to add filters functionality to a screen through a ViewModel.
 */
interface FiltersViewModelDelegate {

    /** The full list of filter chips. */
    val filterChips: Flow<MutableList<FilterChip>>

    /** The list of selected filters. */
    val selectedFilters: StateFlow<MutableList<Filter>>

    /** The MutableList of selected filter chips. */
    val selectedFilterChips: StateFlow<MutableList<FilterChip>>

    /** True if there are any selected filters. */
    val hasAnyFilters: StateFlow<Boolean>

    /** Number of results from applying filters. Can be set by implementers. */
    val resultCount: MutableStateFlow<Int>

    /** Whether to show the result count instead of the "Filters" header. */
    val showResultCount: StateFlow<Boolean>

    /** Set the MutableList of filters. */
    fun setSupportedFilters(filters: MutableList<Filter>)

    /** Set the selected state of the filter. Must be one of the supported filters. */
    fun toggleFilter(filter: Filter, enabled: Boolean, singleCheck: Boolean = false)

    /** Clear all selected filters. */
    fun clearFilters()
    fun executeSearch() {}

}

class FiltersViewModelDelegateImpl(externalScope: CoroutineScope) : FiltersViewModelDelegate {
    private val _filterChips = MutableStateFlow<MutableList<FilterChip>>(Collections.emptyList())
    override val filterChips: Flow<MutableList<FilterChip>> = _filterChips

    private val _selectedFilters = MutableStateFlow<MutableList<Filter>>(Collections.emptyList())
    override val selectedFilters: StateFlow<MutableList<Filter>> = _selectedFilters

    private val _selectedFilterChips =
        MutableStateFlow<MutableList<FilterChip>>(Collections.emptyList())
    override val selectedFilterChips: StateFlow<MutableList<FilterChip>> = _selectedFilterChips
    override val hasAnyFilters = selectedFilterChips
        .map { it.isNotEmpty() }
        .stateIn(externalScope, SharingStarted.Lazily, false)

    override val resultCount = MutableStateFlow(0)

    // Default behavior: show count when there are active filters.
    override val showResultCount = hasAnyFilters
//    override val showResultCount = MutableStateFlow(false)

    // State for internal logic
    private var _filters = mutableListOf<Filter>()
    private val _selectedFiltersList = mutableSetOf<Filter>()
    private var _filterChipsList = mutableListOf<FilterChip>()
    private var _selectedFilterChipsList = mutableListOf<FilterChip>()


    override fun setSupportedFilters(filters: MutableList<Filter>) {
        // Remove orphaned filters
        val selectedChanged = _selectedFiltersList.removeIf { it !in filters }
//        _filters = filters.toMutableList()
        _filters = filters
        _filterChipsList = _filters.mapTo(mutableListOf()) {
            it.asChip(it in _selectedFiltersList)
        }

        if (selectedChanged) {
            _selectedFilterChipsList = _filterChipsList.filterTo(mutableListOf()) { it.isSelected }
        }
        publish(selectedChanged)
    }

    private fun publish(selectedChanged: Boolean) {
        _filterChips.value = _filterChipsList
        if (selectedChanged) {
            _selectedFilters.value = _selectedFiltersList.toMutableList()
            _selectedFilterChips.value = _selectedFilterChipsList
        }
    }

    override fun toggleFilter(filter: Filter, enabled: Boolean, singleCheck: Boolean) {
        if (filter !in _filters) {
            throw IllegalArgumentException("Unsupported filter: $filter")
        }
        val changed = when {
            enabled -> {
                if (singleCheck) {
                    _selectedFiltersList.removeIf {
                        it.asChip(true).categoryLabel == filter.asChip(
                            true
                        ).categoryLabel
                    }
                }
                _selectedFiltersList.add(filter)
            }
            else -> {
                _selectedFiltersList.remove(filter)
            }
        }
        if (changed) {
            _selectedFilterChipsList =
                _selectedFiltersList.mapTo(mutableListOf()) { it.asChip(true) }
            val index = _filterChipsList.indexOfFirst { it.filter == filter }
            //            _filterChipsList[index] = filter.asChip(enabled)
            _filterChipsList =
                _filterChipsList.mapIndexedTo(mutableListOf()) { _index, filterChip ->
//                //单选
                    if (singleCheck) {
                        if (filterChip.filter in _selectedFiltersList) {
                            filterChip.filter.asChip(true)
                        } else {
                            filterChip.filter.asChip(false)
                        }
                    } else {
                        if (index == _index) filter.asChip(enabled) else filterChip
                    }
                }
            publish(true)
        }
    }

    override fun clearFilters() {
        if (_selectedFiltersList.isNotEmpty()) {
            resultCount.value = 0
            _selectedFiltersList.clear()
            _selectedFilterChipsList.clear()
            _filterChipsList = _filterChipsList.mapTo(mutableListOf()) {
                if (it.isSelected) it.copy(isSelected = false) else it
            }
            publish(true)
        }
    }

}
