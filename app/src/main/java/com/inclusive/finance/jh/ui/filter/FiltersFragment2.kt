/*
 * Copyright 2018 Google LLC
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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.view.*
import androidx.databinding.ObservableFloat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.blankj.utilcode.util.ScreenUtils
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.material.bottomsheet.BottomSheetBehavior
 import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.databinding.FragmentFilters2Binding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

/**
 * Fragment that shows the list of filters for the Schedule
 */
@AndroidEntryPoint
class FiltersFragment2(var fragment:Fragment, var searchListener:(selectList:MutableList<FilterChip>, searchStr:String, searchStr2:String,searchStr3:String, resultCountCallBack: (count: String)->Unit)->Unit) : BottomSheetDialogFragment() {
    companion object {
        // Threshold for when the filter sheet content should become invisible.
        // This should be a value between 0 and 1, coinciding with a point between the bottom
        // sheet's collapsed (0) and expanded (1) states.
        private const val ALPHA_CONTENT_START = 0.1f

        // Threshold for when the filter sheet content should become visible.
        // This should be a value between 0 and 1, coinciding with a point between the bottom
        // sheet's collapsed (0) and expanded (1) states.
        private const val ALPHA_CONTENT_END = 0.3f
    }

    private lateinit var viewModel: SearchViewModel2

    private lateinit var filterAdapter: FiltersAdapter

    private lateinit var binding: FragmentFilters2Binding

    private lateinit var behavior: BottomSheetBehavior<*>

    private var contentAlpha = ObservableFloat(1f)

    private val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (::behavior.isInitialized && behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }
    private var pendingSheetState = BottomSheetBehavior.STATE_HIDDEN



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilters2Binding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            contentAlpha = this@FiltersFragment2.contentAlpha
        }

        // Pad the bottom of the RecyclerView so that the content scrolls up above the nav bar
        binding.recyclerviewFilters.doOnApplyWindowInsets { v, insets, padding ->
            val systemInsets = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            v.updatePadding(bottom = padding.bottom + systemInsets.bottom)
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(fragment).get(SearchViewModel2::class.java)
        viewModel.searchListener=searchListener
        binding.viewModel = viewModel

        launchAndRepeatWithViewLifecycle {
            viewModel.filterChips.collect {
                filterAdapter.setNew(it)
            }
        }

        binding.searchView.apply {
            setQuery(viewModel.textQuery,false)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    dismissKeyboard(this@apply)
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.onSearchQueryChanged(newText)
                    return false
                }
            })

            // Set focus on the SearchView and open the keyboard
            setOnQueryTextFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    showKeyboard(view.findFocus())
                }
            }
            requestFocus()
        }
        binding.searchView2.apply {
            setQuery(viewModel.textQuery2,false)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    dismissKeyboard(this@apply)
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.onSearchQueryChanged2(newText)
                    return false
                }
            })

            // Set focus on the SearchView and open the keyboard
            setOnQueryTextFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    showKeyboard(view.findFocus())
                }
            }
            requestFocus()
        }
        binding.searchView3.apply {
            setQuery(viewModel.textQuery3,false)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    dismissKeyboard(this@apply)
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.onSearchQueryChanged3(newText)
                    return false
                }
            })

            // Set focus on the SearchView and open the keyboard
            setOnQueryTextFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    showKeyboard(view.findFocus())
                }
            }
            requestFocus()
        }
        behavior = BottomSheetBehavior.from(requireDialog().requireViewById(com.google.android.material.R.id.design_bottom_sheet))
        behavior.skipCollapsed = true
        behavior.peekHeight = 0
        behavior.isHideable = true
        filterAdapter = FiltersAdapter(viewModel)
        filterAdapter.setDiffCallback(FilterChipAndHeadingDiff)
        binding.scrollView.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            binding.filtersHeaderShadow.isActivated = binding.scrollView.canScrollVertically(-1)
        }

        binding.recyclerviewFilters.apply {
            adapter = filterAdapter
            setHasFixedSize(false)
            itemAnimator = null
            addItemDecoration(
                FlexboxItemDecoration(context).apply {
                    setDrawable(context.getDrawable(R.drawable.divider_empty_margin_small))
                    setOrientation(FlexboxItemDecoration.VERTICAL)
                }
            )
        }

        // Update the peek and margins so that it scrolls and rests within sys ui
        val peekHeight = behavior.peekHeight
        val marginBottom = binding.root.marginBottom
        binding.filterSheet.layoutParams.height = ScreenUtils.getScreenHeight() / 5 * 4
        binding.root.doOnApplyWindowInsets { v, insets, _ ->
            val gestureInsets = insets.getInsets(WindowInsetsCompat.Type.systemGestures())
            // Update the peek height so that it is above the navigation bar
            behavior.peekHeight = gestureInsets.bottom + peekHeight

            v.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = marginBottom + gestureInsets.top
            }
        }

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                updateFilterContentsAlpha(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                updateBackPressedCallbackEnabled(newState)
            }
        })
         binding.collapseArrow.setOnClickListener {
            behavior.state =
                if (behavior.skipCollapsed) BottomSheetBehavior.STATE_HIDDEN else BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.filterSheet.doOnLayout {
            val slideOffset = when (behavior.state) {
                BottomSheetBehavior.STATE_EXPANDED -> 1f
                BottomSheetBehavior.STATE_COLLAPSED -> 0f
                else /*BottomSheetBehavior.STATE_HIDDEN*/ -> -1f
            }
            updateFilterContentsAlpha(slideOffset)
        }
        behavior.state = pendingSheetState
        pendingSheetState = BottomSheetBehavior.STATE_HIDDEN
        updateBackPressedCallbackEnabled(behavior.state)
    }

    override fun onPause() {
        dismissKeyboard(binding.searchView)
        super.onPause()
    }



    private fun createStateForView(view: View) = ViewPaddingState(
        view.paddingLeft,
        view.paddingTop,
        view.paddingRight,
        view.paddingBottom,
        view.paddingStart,
        view.paddingEnd
    )

    data class ViewPaddingState(
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int,
        val start: Int,
        val end: Int
    )

    fun View.doOnApplyWindowInsets(f: (View, WindowInsetsCompat, ViewPaddingState) -> Unit) {
        // Create a snapshot of the view's padding state
        val paddingState = createStateForView(this)
        ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
            f(v, insets, paddingState)
            insets
        }
        requestApplyInsetsWhenAttached()
    }

    /**
     * Call [View.requestApplyInsets] in a safe away. If we're attached it calls it straight-away.
     * If not it sets an [View.OnAttachStateChangeListener] and waits to be attached before calling
     * [View.requestApplyInsets].
     */
    fun View.requestApplyInsetsWhenAttached() {
        if (isAttachedToWindow) {
            requestApplyInsets()
        } else {
            addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    v.requestApplyInsets()
                }

                override fun onViewDetachedFromWindow(v: View) = Unit
            })
        }
    }

    // In order to acquire the behavior associated with this sheet, we need to be attached to the
    // view hierarchy of our parent, otherwise we get an exception that our view is not a child of a
    // CoordinatorLayout. Therefore we do most initialization here instead of in onViewCreated().
    private fun dismissKeyboard(view: View) {
        ViewCompat.getWindowInsetsController(view)?.hide(WindowInsetsCompat.Type.ime())
    }
    private fun showKeyboard(view: View) {
        ViewCompat.getWindowInsetsController(view)?.show(WindowInsetsCompat.Type.ime())
    }

    private fun updateFilterContentsAlpha(slideOffset: Float) {
        // Since the content is visible behind the navigation bar, apply a short alpha transition.
        contentAlpha.set(
            slideOffsetToAlpha(slideOffset, ALPHA_CONTENT_START, ALPHA_CONTENT_END)
        )
    }

    /**
     * Map a slideOffset (in the range `[-1, 1]`) to an alpha value based on the desired range.
     * For example, `slideOffsetToAlpha(0.5, 0.25, 1) = 0.33` because 0.5 is 1/3 of the way between
     * 0.25 and 1. The result value is additionally clamped to the range `[0, 1]`.
     */
    fun slideOffsetToAlpha(value: Float, rangeMin: Float, rangeMax: Float): Float {
        return ((value - rangeMin) / (rangeMax - rangeMin)).coerceIn(0f, 1f)
    }

    /**
     * Launches a new coroutine and repeats `block` every time the Fragment's viewLifecycleOwner
     * is in and out of `minActiveState` lifecycle state.
     */
    inline fun Fragment.launchAndRepeatWithViewLifecycle(
        minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
        crossinline block: suspend CoroutineScope.() -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
                block()
            }
        }
    }

    private fun updateBackPressedCallbackEnabled(state: Int) {
        backPressedCallback.isEnabled =
            !(state == BottomSheetBehavior.STATE_COLLAPSED || state == BottomSheetBehavior.STATE_HIDDEN)
    }

    fun showFiltersSheet() {
//        if (::behavior.isInitialized) {
//            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//        } else {
        pendingSheetState = BottomSheetBehavior.STATE_EXPANDED
//        }
    }

    fun hideFiltersSheet() {
        if (::behavior.isInitialized) {
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            pendingSheetState = BottomSheetBehavior.STATE_HIDDEN
        }
    }
}
