package com.tinytools.files.helpers

import kotlin.math.log2
import kotlin.math.pow

val Long.formatAsFileSize: String
    get() = if(this == 0L) "0B" else log2(toDouble()).toInt().div(10).let {
        val precision = when (it) {
            0 -> 0; 1 -> 1; else -> 2
        }
        val prefix = arrayOf("", "K", "M", "G", "T", "P", "E", "Z", "Y")
        String.format("%.${precision}f ${prefix[it]}B", toDouble() / 2.0.pow(it * 10.0))
    }
