package com.inclusive.finance.jh.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.inclusive.finance.jh.IRouter
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.databinding.ItemMainBinding
import com.inclusive.finance.jh.databinding.ItemMainMenuBinding
import com.inclusive.finance.jh.ui.filter.FilterChip
import com.inclusive.finance.jh.utils.SZWUtils

class ItemMainAdapter(var viewModel: MainActivityModel) :
    BaseQuickAdapter<Any, BaseViewHolder>(R.layout.item_main, ArrayList()) {
    companion object {
        private var mLastClickTime: Long = 0
        private val TIME_INTERVAL = 500L
        private const val VIEW_TYPE_HEADING = R.layout.item_main
        private const val VIEW_TYPE_MENU = R.layout.item_main_menu

        /**
         * Inserts category headings in a list of [FilterChip]s to make a heterogeneous list.
         * Assumes the items are already grouped by [FilterChip.categoryLabel], beginning with
         * categoryLabel == '0'.
         */

        private fun insertCategory(list: MutableList<MainHeader>?): MutableList<MenuBean> {
            val newList = mutableListOf<MenuBean>()
            list?.forEach {
                it.list?.forEach { menuBean ->
                    menuBean.categoryLabel = it.title
                    newList.add(menuBean)
                }
            }
            return newList
        }

        private fun insertCategoryHeadings(list: MutableList<MenuBean>?): MutableList<Any> {
            val newList = mutableListOf<Any>()
            var previousCategory = ""
            list?.forEach {
                val category = it.categoryLabel
                if (category != previousCategory && category != "") {
                    newList += MainHeader(
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

    fun setNew(list: MutableList<MainHeader>?) {
        super.setNewInstance(insertCategoryHeadings(insertCategory(list)))
    }
    init {
        setGridSpanSizeLookup { gridLayoutManager, viewType, position ->
            when (viewType) {
                VIEW_TYPE_HEADING -> gridLayoutManager.spanCount
                VIEW_TYPE_MENU -> 1
                else -> throw IllegalArgumentException("Unknown item type")
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MainHeader -> VIEW_TYPE_HEADING
            is MenuBean -> VIEW_TYPE_MENU
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADING -> createHeaderViewHolder(parent)
            VIEW_TYPE_MENU -> createMainMenuViewHolder(parent)
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }

    private fun createHeaderViewHolder(parent: ViewGroup): HeadingViewHolder {
        val binding = ItemMainBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return HeadingViewHolder(binding)
    }

    private fun createMainMenuViewHolder(parent: ViewGroup): MainMenuViewHolder {
        val binding = ItemMainMenuBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return MainMenuViewHolder(context, binding)
    }

    override fun convert(holder: BaseViewHolder, item: Any) {
        when (holder) {
            is HeadingViewHolder -> holder.bind(item as MainHeader)
            is MainMenuViewHolder -> holder.bind(item as MenuBean)
        }
    }

    /** ViewHolder for category heading items. */
    class HeadingViewHolder(
        private val binding: ItemMainBinding,
    ) : BaseViewHolder(binding.root) {

        internal fun bind(item: MainHeader) {
            binding.data = item
            binding.executePendingBindings()
        }
    }

    /** ViewHolder for category heading items. */
    class MainMenuViewHolder(
        var context: Context,
        private val binding: ItemMainMenuBinding,
    ) : BaseViewHolder(binding.root) {

        internal fun bind(item: MenuBean) {
            binding.data = item
            SZWUtils.loadPhotoImg(context, item.img, binding.img)
            binding.root.setOnClickListener {
                val action = item.className
                if (System.currentTimeMillis() - mLastClickTime > TIME_INTERVAL) {
                    IRouter.goF(it, action)
                    mLastClickTime = System.currentTimeMillis()
                }
            }
            binding.executePendingBindings()
        }
    }

    open class MainHeader(val title: String, val useHorizontalPadding: Boolean = true) {
        var list: ArrayList<MenuBean>? = null
    }

    open class MenuBean {
        var title: String? = ""
        var categoryLabel: String = ""
        var img: String? = ""
        var className: String? = ""
    }
}