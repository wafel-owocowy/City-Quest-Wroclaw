Opis aplikacji: [Opis aplikacji-1.pdf](https://github.com/wafel-owocowy/City-Quest-Wroclaw/blob/master/Opis%20aplikacji-1.pdf)

## Diagram ekranów i przejść aplikacji: 
![diagram_drawio.png](https://github.com/wafel-owocowy/City-Quest-Wroclaw/blob/master/diagram_drawio.png)

## Struktura bazy danych: 
![database-structure.png](https://github.com/wafel-owocowy/City-Quest-Wroclaw/blob/master/database-structure.png)

## Użyte technologie i serwisy
- **Język:** Kotlin
- **UI:** Jetpack Compose (w tym Material Design 3 oraz system animacji).
- **Nawigacja:** Navigation Compose.
- **Baza danych:** Room Database (z użyciem Kotlin Flow do reaktywnego odświeżania UI).
- **Architektura:** MVVM (Model-View-ViewModel) z wykorzystaniem Coroutines.
- **Seriws map** OpenStreetMap (poprzez bibliotekę osmdroid)
## Opis logiki biznesowej w Viewmodel

### Cała logika aplikacji zawiera się w klasie CityQuestViewModel obsługującej:
1. Bazę danych w tym:
    1. zarządzanie połączeniem z bazą
        ```
        private val db :AppDatabase
        private val attractionDao :AttractionDao
        ```
    2. udostępnianie stanu miejsc do UI
        ```
        val attractions: StateFlow<List<Attraction>>
        ```
    3. Oznaczanie lokacji jako odwiedzonych
        ```
        private fun markAsVisited(id: Int)
        ```
2. Zarządzanie danymi geolokalizacyjnymi, tj:
    1. Uruchomieniem usługi zwracającej lokalizację użytkownika
        ```
        private val locationService :LocationService
        fun startLocationUpdates()
        ```
    2. Udostępnianie lokalizacji użytkownika do UI
        ```
        val currentLocation: StateFlow<Location?>
        ```
    3. Wykrywanie odwiedzenia lokacji
        ```
        private fun checkDistanceToAttractions(userLocation: Location)
        ```
3. Utrzymanie stanu UI mapy:
    ```
    var lastMapCenterLat: Double? 
    var lastMapCenterLon: Double? 
    var lastMapZoom: Double
    var shouldCenterOnUser: Boolean
    fun saveMapState(lat: Double, lon: Double, zoom: Double)
    fun resetMapCentering()
    fun markMapCentered()
    ```
4. Udostępnianie funkcji zmieniania ustawień:
    ```
    fun setLanguage(languageTag: String)
    fun setRadiusMeters(newRadius: Float)
    fun setDarkMode(enabled: Boolean)
    ```
## Przepływy danych
![dataflow.png](https://github.com/wafel-owocowy/City-Quest-Wroclaw/blob/master/dataflow.png)
