package karlhto.utils

import karlhto.data.Country

enum class SortOrder {
    ASC,
    DESC
}

enum class SortBy {
    NAME,
    CURRENCIES
}

/**
 * Sorts a list of countries by a given field,
 */
fun sortCountries(countries: List<Country>, sortBy: SortBy?, sortOrder: SortOrder?) =
    if (sortBy == null && sortOrder == null) {
        countries
    } else {
        countries.sortedWith(compareBy { country ->
            when (sortBy ?: SortBy.NAME) {
                SortBy.NAME -> country.name
                SortBy.CURRENCIES -> country.currencies.joinToString()
            }
        }).let {
            if (sortOrder == SortOrder.DESC) it.reversed() else it
        }
    }
