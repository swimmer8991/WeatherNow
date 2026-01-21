package com.app.weathernow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.weathernow.data.WeatherRepository
import com.app.weathernow.data.WeatherResponse
import com.app.weathernow.data.ForecastResponse
import com.app.weathernow.data.CityPreferences
import com.app.weathernow.data.GeoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeatherUiState(
    val city: String = "",
    val isLoading: Boolean = false,
    val weather: WeatherResponse? = null,
    val forecast: ForecastResponse? = null,
    val error: String? = null,
    val searchQuery: String = "",
    val searchResults: List<GeoItem> = emptyList(),
    val isSearching: Boolean = false
)

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val cityPreferences: CityPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState

    // Debounce search
    private var searchJob: kotlinx.coroutines.Job? = null

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        
        searchJob?.cancel()
        if (query.length > 2) {
            searchJob = viewModelScope.launch {
                kotlinx.coroutines.delay(500) // Debounce
                _uiState.value = _uiState.value.copy(isSearching = true)
                val result = repository.searchCity(query)
                when (result) {
                    is com.app.weathernow.data.WeatherResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isSearching = false,
                            searchResults = result.data
                        )
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(
                            isSearching = false,
                            searchResults = emptyList()
                        )
                    }
                }
            }
        } else {
             _uiState.value = _uiState.value.copy(searchResults = emptyList())
        }
    }

    fun onCitySelected(city: GeoItem) {
        val cityName = city.name ?: return
        _uiState.value = _uiState.value.copy(
            city = cityName,
            searchQuery = "",
            searchResults = emptyList()
        )
        loadWeather()
    }

    fun onCityChange(city: String) {
        _uiState.value = _uiState.value.copy(city = city)
    }

    fun loadWeather() {
        val city = _uiState.value.city.trim()
        if (city.isBlank()) return

        // Save city for widget
        cityPreferences.saveCity(city)

        _uiState.value = _uiState.value.copy(isLoading = true, error = null, searchResults = emptyList())

        viewModelScope.launch {
            // Load current weather and forecast in parallel
            val weatherResultDeferred = async { repository.getCurrentWeather(city) }
            val forecastResultDeferred = async { repository.getForecast(city) }

            val weatherResult = weatherResultDeferred.await()
            val forecastResult = forecastResultDeferred.await()

            val currentState = _uiState.value
            
            val newState = if (weatherResult is com.app.weathernow.data.WeatherResult.Success && 
                               forecastResult is com.app.weathernow.data.WeatherResult.Success) {
                currentState.copy(
                    isLoading = false,
                    weather = weatherResult.data,
                    forecast = forecastResult.data,
                    error = null
                )
            } else {
                val errorMessage = (weatherResult as? com.app.weathernow.data.WeatherResult.Error)?.message 
                    ?: (forecastResult as? com.app.weathernow.data.WeatherResult.Error)?.message 
                    ?: "Unknown error"
                
                currentState.copy(
                    isLoading = false,
                    error = errorMessage,
                    weather = null,
                    forecast = null
                )
            }
            _uiState.value = newState
        }
    }
}

