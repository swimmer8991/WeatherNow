# WeatherNow 🌤️

A modern, beautiful Android weather application built with Jetpack Compose and Clean Architecture.

## Features

- **Current Weather**: Get real-time weather details including temperature, "feels like", humidity, and wind speed.
- **Hourly Forecast**: Scroll through the forecast for the next 24 hours.
- **Daily Forecast**: Check the weather outlook for the next 5 days.
- **City Search**: Search for weather in any city around the world.
- **Modern UI**: Features a sleek, dark-themed design with glassmorphism effects and smooth animations.

## Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/)
- **Networking**: [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/)
- **Asynchronous Programming**: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
- **API**: [OpenWeatherMap API](https://openweathermap.org/api)

## Setup & Running

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/yourusername/WeatherNow.git
    ```
2.  **Open in Android Studio**:
    Open the project directory in Android Studio (Ladybug or newer recommended).
3.  **API Key**:
    The project uses the OpenWeatherMap API. By default, a demo key is configured in `app/build.gradle.kts`. Functional usage may require your own API key.
    To use your own key, replace the `OPEN_WEATHER_API_KEY` value in `app/build.gradle.kts` or move it to `local.properties`.
4.  **Run**:
    Connect an Android device or start an emulator and run the `app` configuration.

## Screenshots



## License

This project is open source and available under the [MIT License](LICENSE).
