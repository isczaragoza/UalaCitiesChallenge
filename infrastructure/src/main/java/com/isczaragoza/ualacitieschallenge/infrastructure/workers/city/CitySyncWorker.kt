package com.isczaragoza.ualacitieschallenge.infrastructure.workers.city

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.isczaragoza.ualacitieschallenge.domain.constants.CITY_SYNC_WORKER_FAILURE_KEY
import com.isczaragoza.ualacitieschallenge.domain.constants.CITY_SYNC_WORKER_PROGRESS_KEY
import com.isczaragoza.ualacitieschallenge.domain.constants.CITY_SYNC_WORKER_SUCCESS_KEY
import com.isczaragoza.ualacitieschallenge.domain.resulthandlers.DownloadResultWrapper
import com.isczaragoza.ualacitieschallenge.domain.usecases.SyncCitiesByIntervalParameterUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.isczaragoza.ualacitieschallenge.infrastructure.R

@HiltWorker
class CitySyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val syncCitiesByIntervalParameterUseCase: SyncCitiesByIntervalParameterUseCase
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "city_sync_worker_id"
        const val CHANNEL_NAME = "city_sync_worker_name"
        const val CHANNEL_DESCRIPTION_TEXT = "city_sync_worker_description"
        const val NOTIFICATION_ID = 1
    }

    override suspend fun doWork(): Result {
        syncCitiesByIntervalParameterUseCase().collect { downloadResultWrapper ->
            when (downloadResultWrapper) {
                is DownloadResultWrapper.Progress -> {
                    println("Progress Worker")
                    setForeground(createForegroundInfo(downloadResultWrapper.percentage))
                    setProgress(workDataOf(CITY_SYNC_WORKER_PROGRESS_KEY to downloadResultWrapper.percentage))
                }

                is DownloadResultWrapper.Success -> {
                    println("Success Worker")
                }

                is DownloadResultWrapper.Failure -> {
                    println("Failure Worker")
                    Result.failure(workDataOf(CITY_SYNC_WORKER_FAILURE_KEY to downloadResultWrapper.baseError))
                }
            }
        }
        println("Success doWork")
        return Result.success(workDataOf(CITY_SYNC_WORKER_SUCCESS_KEY to "1"))
    }

    private fun createForegroundInfo(progress: Int): ForegroundInfo {
        val title = context.getString(R.string.city_sync_worker_notification_name)
        createNotificationChannel()
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon)
            .setLargeIcon(IconCompat.createWithResource(context, R.drawable.icon).toIcon(context))
            .setContentTitle(title)
            .setContentText(context.getString(R.string.progress_string, progress.toString()))
            .setOngoing(true)
            .setProgress(100, progress, false)
            .setOnlyAlertOnce(true)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    fun createNotificationChannel() {
        val name = CHANNEL_NAME
        val descriptionText = CHANNEL_DESCRIPTION_TEXT
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(
            CHANNEL_ID,
            name,
            importance
        ).apply {
            description = descriptionText
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
