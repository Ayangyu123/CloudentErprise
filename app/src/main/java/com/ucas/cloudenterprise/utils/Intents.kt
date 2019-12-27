package com.ucas.cloudenterprise.utils
import android.app.Activity
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent

inline fun <reified T> Context.intent(builder: Intent.() -> Unit = {}) =
    Intent(this, T::class.java).apply(builder)

fun Intent.action(value: String) = apply { action = value }

inline fun <reified T : Activity> Context.startActivity() = startActivity(intent<T>())
inline fun <reified T : Service> Context.startService() = startService(intent<T>())

inline fun <reified T : Service> Context.pendingService() = pendingService(intent<T>())
fun Context.pendingService(intent: Intent) = PendingIntent.getService(this, 0, intent, 0)

inline fun <reified T : Activity> Context.pendingActivity() = pendingActivity(intent<T>())
fun Context.pendingActivity(intent: Intent) = PendingIntent.getActivity(this, 0, intent, 0)