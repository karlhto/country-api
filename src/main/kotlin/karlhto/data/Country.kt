package karlhto.data

import kotlinx.serialization.Serializable

@Serializable
data class Country(val name: String, val currencies: List<String>)
