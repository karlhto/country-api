package karlhto.repository

import io.ktor.client.engine.HttpClientEngine
import karlhto.client.RestCountriesClient
import karlhto.data.Country
import karlhto.data.source.SourceCountry

enum class Region {
    AFRICA,
    AMERICAS,
    ASIA,
    EUROPE,
    OCEANIA;

    companion object {
        fun fromString(region: String?): Region? = entries.find { it.name.lowercase() == region?.lowercase() }
    }
}

class CountryRepository(engine: HttpClientEngine) {
    private val restCountriesClient = RestCountriesClient(engine)

    /**
     * Returns a list of countries by region, mapping the response to a list of [Country] objects.
     *
     * @param region The region to filter the countries by. As of now, there is no validation for the region,
     *               so in case of a mismatch, no results will be returned.
     */
    fun getCountriesByRegion(region: Region): List<Country> =
        restCountriesClient.sourceCountryList
            .filter { it.region.lowercase() == region.name.lowercase() }
            .map { Country(it.name.common, it.currencies.keys.toList().sorted()) }

    /**
     * Returns a list of currencies in the world, together with the countries that use them.
     */
    fun getAllCurrenciesWithCountries(): Map<String, List<String>> =
        restCountriesClient.sourceCountryList
            .flatMap { country: SourceCountry ->
                country.currencies.keys.map { currencyName: String -> Pair(currencyName, country.name.common) }
            }.groupBy({ it.first }, { it.second })
}
