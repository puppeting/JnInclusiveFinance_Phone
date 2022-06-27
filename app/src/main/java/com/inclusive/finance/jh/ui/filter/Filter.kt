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

sealed class Filter {

    data class TagFilter(val tag: Tag) : Filter()
    data class SearchFilter(val tag: Tag) : Filter()

    object MyScheduleFilter : Filter()
}

data class Tag(
        /**
         * Unique string identifying this tag.
         */
        val id: String,

        /**
         * Tag category type. For example, "track", "level", "type", "theme", etc.
         */
        val categoryId: String,
        val category: String,

        val hint: String?="",
        var valueName: String,
        val keyName: String,
        val isSingleCheck: Boolean,

        /**
         * The color associated with this tag as a color integer.
         */
        val color: Int? = null,

        /**
         * The font color associated with this tag as a color integer.
         */
        val fontColor: Int? = null
) {

    companion object {
        const val CATEGORY_KHXM = "客户姓名"
        const val CATEGORY_SFBR = "是否本人发起"
        const val CATEGORY_LCZT = "流程状态"
        const val CATEGORY_SFQP = "是否签批"
        const val CATEGORY_JG = "机构"
        const val CATEGORY_JYZT = "检验状态"
        const val CATEGORY_SFDB = "是否待办"

    }

    /** Only IDs are used for equality. */
//    override fun equals(other: Any?): Boolean = this === other || (other is Tag && other.id == id)
//
//    /** Only IDs are used for equality. */
//    override fun hashCode(): Int = id.hashCode()

}