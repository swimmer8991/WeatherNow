package com.app.weathernow

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
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
                
                // Note: Setting dynamic icons from URL in RemoteViews usually requires 
                // downloading the image and setting it as a bitmap. For simplicity,
                // we'll use the app icon for now or we could add more logic later.
                views.setImageViewResource(R.id.widget_icon, R.mipmap.ic_launcher)
            } else {
                views.setTextViewText(R.id.widget_temperature, "--")
                views.setTextViewText(R.id.widget_description, "Error loading")
            }

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
