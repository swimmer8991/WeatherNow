package com.app.weathernow

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.app.weathernow.data.CityPreferences
import com.app.weathernow.data.WeatherRepository
import com.app.weathernow.data.WeatherResult
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WeatherWidgetProvider : AppWidgetProvider() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WeatherWidgetEntryPoint {
        fun repository(): WeatherRepository
    }

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WeatherWidgetEntryPoint::class.java
        )
        val repository = entryPoint.repository()
        val cityPrefs = CityPreferences(context)
        val city = cityPrefs.getCity()

        scope.launch {
            val result = repository.getCurrentWeather(city)
            val views = RemoteViews(context.packageName, R.layout.weather_widget_layout)

            views.setTextViewText(R.id.widget_city_name, city)

            if (result is WeatherResult.Success) {
                val weather = result.data
                val temp = weather.main?.temp?.toInt() ?: 0
                val desc = weather.weather?.firstOrNull()?.description ?: "N/A"
                
                views.setTextViewText(R.id.widget_temperature, "$temp°")
                views.setTextViewText(R.id.widget_description, desc.replaceFirstChar { it.uppercase() })
                
                // Set weather icon (emoji)
                val iconCode = weather.weather?.firstOrNull()?.icon
                val emoji = when {
                    iconCode == null -> "🌤️"
                    iconCode.startsWith("01") -> "☀️"
                    iconCode.startsWith("02") -> "⛅"
                    iconCode.startsWith("03") -> "☁️"
                    iconCode.startsWith("04") -> "☁️"
                    iconCode.startsWith("09") -> "🌧️"
                    iconCode.startsWith("10") -> "🌦️"
                    iconCode.startsWith("11") -> "⛈️"
                    iconCode.startsWith("13") -> "❄️"
                    iconCode.startsWith("50") -> "🌫️"
                    else -> "🌤️"
                }
                views.setTextViewText(R.id.widget_icon, emoji)
            } else {
                views.setTextViewText(R.id.widget_temperature, "--")
                views.setTextViewText(R.id.widget_description, "Error loading")
            }

            // PendingIntent to launch MainActivity
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        job.cancel()
    }
}
