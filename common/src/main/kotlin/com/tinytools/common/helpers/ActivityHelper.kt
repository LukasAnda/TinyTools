package com.tinytools.common.helpers

import android.app.Activity
import android.widget.Toast
import androidx.annotation.StringRes
import com.tinytools.common.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private var backButtonPressedTwice = false

fun Activity.onBackPressedTwiceFinish(@StringRes message: Int, time: Long = 2000L) {
    onBackPressedTwiceFinish(getString(message), time)
}

fun Activity.onBackPressedTwiceFinish(message: String, time: Long = 2000L) {
    if (backButtonPressedTwice) {
        finish()
    } else {
        backButtonPressedTwice = true
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        GlobalScope.launch {
            delay(time)
            backButtonPressedTwice = false
        }
    }
}
