package com.nomanim.bunual.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseCoroutineScope() : BaseFragment(), CoroutineScope {

    val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


}