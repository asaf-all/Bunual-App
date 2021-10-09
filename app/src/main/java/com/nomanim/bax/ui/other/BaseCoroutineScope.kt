package com.nomanim.bax.ui.other

import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseCoroutineScope : CoroutineScope, Fragment() {

    val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

}