package com.app.weathernow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.weathernow.data.WeatherRepository
import com.app.weathernow.data.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeatherUiState(
    val city: String = "",
    val isLoading: Boolean = false,
    val weather: WeatherResponse? = null,
    val error: String? = null
)

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState

    fun onCityChange(city: String) {
        _uiState.value = _uiState.value.copy(city = city)
    }

    fun loadWeather() {
        val city = _uiState.value.city.trim()
        if (city.isBlank()) return

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = repository.getCurrentWeather(city)
            when (result) {
                is com.app.weathernow.data.WeatherResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        weather = result.data,
                        error = null
                    )
                }

                is com.app.weathernow.data.WeatherResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                        weather = null
                    )
                }

                com.app.weathernow.data.WeatherResult.Loading -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true,
                        error = null
                    )
                }
            }
        }
    }
}

