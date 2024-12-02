package karlhto.utils

import karlhto.data.Country
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SortingUtilsTest {

    private val countries = listOf(
        Country("France", listOf("EUR")),
        Country("Germany", listOf("EUR", "USD")),
        Country("Spain", listOf("EUR", "GBP"))
    )

    @Test
    fun `sort by currencies ascending when sortOrder is null`() {
        val sortedCountries = sortCountries(countries, SortBy.CURRENCIES, null)
        val expected = listOf(
            Country("France", listOf("EUR")),
            Country("Spain", listOf("EUR", "GBP")),
            Country("Germany", listOf("EUR", "USD"))
        )
        assertEquals(expected, sortedCountries)
    }

    @Test
    fun `sort by name ascending when sortBy is null and sortOrder is not null`() {
        val sortedCountries = sortCountries(countries, null, SortOrder.ASC)
        val expected = listOf(
            Country("France", listOf("EUR")),
            Country("Germany", listOf("EUR", "USD")),
            Country("Spain", listOf("EUR", "GBP"))
        )
        assertEquals(expected, sortedCountries)
    }

    @Test
    fun `sort by name descending when sortBy is null and sortOrder is DESC`() {
        val sortedCountries = sortCountries(countries, null, SortOrder.DESC)
        val expected = listOf(
            Country("Spain", listOf("EUR", "GBP")),
            Country("Germany", listOf("EUR", "USD")),
            Country("France", listOf("EUR"))
        )
        assertEquals(expected, sortedCountries)
    }

    @Test
    fun `do not sort when both sortBy and sortOrder are null`() {
        val sortedCountries = sortCountries(countries, null, null)
        assertEquals(countries, sortedCountries)
    }
}
