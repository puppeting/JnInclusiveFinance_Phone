/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
    @file:JvmName("Converter")
package com.inclusive.finance.jh.utils

import androidx.databinding.InverseMethod

@InverseMethod("convertIntToString")
fun convertStringToInt(age: String?=""): Int {
    return Integer.parseInt(if (age.isNullOrEmpty()) "0" else age)
}

fun convertIntToString(age: Int?=0): String {
    return if (age==0)"" else age.toString()
}


