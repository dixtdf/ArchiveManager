package com.github.dixtdf.archive.manager.action.utils

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications

object NotificationUtils {

    @JvmStatic
    fun error(title: String, content: String) {
        val notification = Notification(
            "CompressCustomNotificationGroup",
            title,
            content,
            NotificationType.ERROR
        )
        Notifications.Bus.notify(notification)
    }

    @JvmStatic
    fun info(title: String, content: String) {
        val notification = Notification(
            "CompressCustomNotificationGroup",
            title,
            content,
            NotificationType.INFORMATION
        )
        Notifications.Bus.notify(notification)
    }

}
