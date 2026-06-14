# Modular MVI University App

Android take-home project that displays universities in the United Arab Emirates using a modular MVI Clean Architecture setup.

## Requirements Covered

- Listing screen implemented with Jetpack Compose.
- Details screen implemented with XML and ViewBinding.
- MVI contracts for presentation state, intents, and one-off effects.
- Clean Architecture separation across presentation, domain, and data layers.
- Retrofit API integration for `universities.hipolabs.com`.
- Room database cache for university data.
- Remote-first loading with local cache fallback on API failure.
- Details screen receives the selected university from Listing and performs no additional API request.
- Details Refresh closes Details, returns to Listing, and triggers a fresh API request/cache update.
- Hilt dependency injection.
- Unit tests for repository, use case, ViewModels, and mappers.

## Module Structure

```text
app/                  App shell, Hilt application, activity, navigation graph
core/data/            Retrofit, Room, repository implementation, data mappers
core/domain/          Domain models, repository contract, use case
core/designsystem/    Shared Compose theme/resources
core/navigation/      Shared navigation result contract
core/testing/         Shared test utilities
feature/listing/      Compose Listing feature with MVI ViewModel
feature/details/      XML/ViewBinding Details feature with MVI ViewModel
build-logic/          Shared Gradle convention plugins
```

## Data Flow

1. User lands on the Listing screen.
2. Listing requests universities through `GetUniversitiesUseCase`.
3. Repository fetches from the API and replaces the Room cache on success.
4. If the API fails, repository returns cached Room data when available.
5. If both API and cache are unavailable, Listing displays an error state.
6. Selecting a university passes the selected item to Details.
7. Details displays the passed data only.
8. Pressing Refresh in Details pops back to Listing and requests a new remote load.

## Build And Test

Run unit tests:

```bash
./gradlew testDebugUnitTest
```

Build a debug APK:

```bash
./gradlew assembleDebug
```

## Notes

- Debug builds use the assignment's HTTP endpoint with a debug network security config.
- Release builds use HTTPS for the same host.
- The UI is intentionally simple, with wrapping text so full university names can be displayed.
