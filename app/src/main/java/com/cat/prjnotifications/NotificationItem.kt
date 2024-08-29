package com.cat.prjnotifications

data class NotificationItem(val title: String, val message: String, val timestamp: Long) {
    override fun toString(): String {
        return "$title: $message"
    }
}

