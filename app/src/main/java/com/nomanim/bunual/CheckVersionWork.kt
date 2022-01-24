package com.nomanim.bunual

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters


class CheckVersionWork(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {

        val sharedPref = applicationContext.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        val status = sharedPref.edit().putBoolean("check_api_version", true)

        return Result.success()
    }
}