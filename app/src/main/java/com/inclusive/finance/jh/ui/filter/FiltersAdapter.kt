package com.inclusive.finance.jh.ui.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.databinding.ItemFilterChipSelectableBinding
import com.inclusive.finance.jh.databinding.ItemFilterGenericSectionHeaderBinding

class FiltersAdapter(private val viewModelDelegate: FiltersViewModelDelegate) :
    BaseQuickAdapter<Any, BaseViewHolder>(R.layout.item_filter_chip_selectable, ArrayList()) {


    companion object {
        private const val VIEW_TYPE_HEADING = R.layout.item_filter_generic_section_header
        private const val VIEW_TYPE_FILTER = R.layout.item_filter_chip_selectable

        /**
         * Inserts category headings in a list of [FilterChip]s to make a heterogeneous list.
         * Assumes the items are already grouped by [FilterChip.categoryLabel], beginning with
         * categoryLabel == '0'.
         */
        private fun insertCategoryHeadings(list: MutableList<FilterChip>?): MutableList<Any> {
            val newList = mutableListOf<Any>()
            var previousCategory = ""
            list?.forEach {
                val category = it.categoryLabel
                if (category != previousCategory && category != "") {
                    newList += SectionHeader(
                        title = category,
                        useHorizontalPadding = false
                    )
                }
                newList.add(it)
                previousCategory = category
            }
            return newList
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is SectionHeader -> VIEW_TYPE_HEADING
            is FilterChip -> VIEW_TYPE_FILTER
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADING -> createHeadingViewHolder(parent)
            VIEW_TYPE_FILTER -> createFilterViewHolder(parent)
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }


    private fun createHeadingViewHolder(parent: ViewGroup): HeadingViewHolder {
        return HeadingViewHolder(
            ItemFilterGenericSectionHeaderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    private fun createFilterViewHolder(parent: ViewGroup): FilterViewHolder {
        val binding = ItemFilterChipSelectableBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ).apply {
            viewModel = viewModelDelegate
        }
        return FilterViewHolder(binding)
    }

    /** ViewHolder for category heading items. */
    class HeadingViewHolder(
        private val binding: ItemFilterGenericSectionHeaderBinding,
    ) : BaseViewHolder(binding.root) {

        internal fun bind(item: SectionHeader) {
            binding.sectionHeader = item
            binding.executePendingBindings()
        }
    }

    /** ViewHolder for [FilterChip] items. */
    class FilterViewHolder(private val binding: ItemFilterChipSelectableBinding) :
        BaseViewHolder(binding.root) {

        internal fun bind(item: FilterChip) {
            binding.filterChip = item
            binding.executePendingBindings()
        }

    }


    fun setNew(list: MutableList<FilterChip>?) {

        super.setDiffNewData(insertCategoryHeadings(list))
    }

    override fun convert(holder: BaseViewHolder, item: Any) {
        when (holder) {
            is HeadingViewHolder -> holder.bind(item as SectionHeader)
            is FilterViewHolder -> holder.bind(item as FilterChip)
        }
    }
}

object FilterChipAndHeadingDiff : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when (oldItem) {
            is FilterChip -> newItem is FilterChip && newItem.filter == oldItem.filter
            else -> oldItem == newItem // SectionHeader
        }
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when (oldItem) {
            is FilterChip -> oldItem.isSelected == (newItem as FilterChip).isSelected
            else -> true
        }
    }
}