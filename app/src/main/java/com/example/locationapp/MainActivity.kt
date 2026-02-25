package com.example.locationapp

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.Locale

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var guessInput: EditText
    private lateinit var guessButton: Button
    private lateinit var resultText: TextView
    private lateinit var distanceText: TextView
    private lateinit var continentText: TextView
    private lateinit var directionText: TextView
    private lateinit var mapView: MapView

    private val targetCountry = "Japan"
    private var targetLocation: Location? = null
    private var targetAddress: Address? = null
    private lateinit var googleMap: GoogleMap

    private val countryToContinentMap = mapOf(
        "Afghanistan" to "Asia",
        "Albania" to "Europe",
        "Algeria" to "Africa",
        "Andorra" to "Europe",
        "Angola" to "Africa",
        "Antigua and Barbuda" to "North America",
        "Argentina" to "South America",
        "Armenia" to "Asia",
        "Australia" to "Oceania",
        "Austria" to "Europe",
        "Azerbaijan" to "Asia",
        "Bahamas" to "North America",
        "Bahrain" to "Asia",
        "Bangladesh" to "Asia",
        "Barbados" to "North America",
        "Belarus" to "Europe",
        "Belgium" to "Europe",
        "Belize" to "North America",
        "Benin" to "Africa",
        "Bhutan" to "Asia",
        "Bolivia" to "South America",
        "Bosnia and Herzegovina" to "Europe",
        "Botswana" to "Africa",
        "Brazil" to "South America",
        "Brunei" to "Asia",
        "Bulgaria" to "Europe",
        "Burkina Faso" to "Africa",
        "Burundi" to "Africa",
        "Cabo Verde" to "Africa",
        "Cambodia" to "Asia",
        "Cameroon" to "Africa",
        "Canada" to "North America",
        "Central African Republic" to "Africa",
        "Chad" to "Africa",
        "Chile" to "South America",
        "China" to "Asia",
        "Colombia" to "South America",
        "Comoros" to "Africa",
        "Congo, Democratic Republic of the" to "Africa",
        "Congo, Republic of the" to "Africa",
        "Costa Rica" to "North America",
        "Cote d'Ivoire" to "Africa",
        "Croatia" to "Europe",
        "Cuba" to "North America",
        "Cyprus" to "Asia",
        "Czech Republic" to "Europe",
        "Denmark" to "Europe",
        "Djibouti" to "Africa",
        "Dominica" to "North America",
        "Dominican Republic" to "North America",
        "Ecuador" to "South America",
        "Egypt" to "Africa",
        "El Salvador" to "North America",
        "Equatorial Guinea" to "Africa",
        "Eritrea" to "Africa",
        "Estonia" to "Europe",
        "Eswatini" to "Africa",
        "Ethiopia" to "Africa",
        "Fiji" to "Oceania",
        "Finland" to "Europe",
        "France" to "Europe",
        "Gabon" to "Africa",
        "Gambia" to "Africa",
        "Georgia" to "Asia",
        "Germany" to "Europe",
        "Ghana" to "Africa",
        "Greece" to "Europe",
        "Grenada" to "North America",
        "Guatemala" to "North America",
        "Guinea" to "Africa",
        "Guinea-Bissau" to "Africa",
        "Guyana" to "South America",
        "Haiti" to "North America",
        "Honduras" to "North America",
        "Hungary" to "Europe",
        "Iceland" to "Europe",
        "India" to "Asia",
        "Indonesia" to "Asia",
        "Iran" to "Asia",
        "Iraq" to "Asia",
        "Ireland" to "Europe",
        "Israel" to "Asia",
        "Italy" to "Europe",
        "Jamaica" to "North America",
        "Japan" to "Asia",
        "Jordan" to "Asia",
        "Kazakhstan" to "Asia",
        "Kenya" to "Africa",
        "Kiribati" to "Oceania",
        "Kosovo" to "Europe",
        "Kuwait" to "Asia",
        "Kyrgyzstan" to "Asia",
        "Laos" to "Asia",
        "Latvia" to "Europe",
        "Lebanon" to "Asia",
        "Lesotho" to "Africa",
        "Liberia" to "Africa",
        "Libya" to "Africa",
        "Liechtenstein" to "Europe",
        "Lithuania" to "Europe",
        "Luxembourg" to "Europe",
        "Madagascar" to "Africa",
        "Malawi" to "Africa",
        "Malaysia" to "Asia",
        "Maldives" to "Asia",
        "Mali" to "Africa",
        "Malta" to "Europe",
        "Marshall Islands" to "Oceania",
        "Mauritania" to "Africa",
        "Mauritius" to "Africa",
        "Mexico" to "North America",
        "Micronesia" to "Oceania",
        "Moldova" to "Europe",
        "Monaco" to "Europe",
        "Mongolia" to "Asia",
        "Montenegro" to "Europe",
        "Morocco" to "Africa",
        "Mozambique" to "Africa",
        "Myanmar" to "Asia",
        "Namibia" to "Africa",
        "Nauru" to "Oceania",
        "Nepal" to "Asia",
        "Netherlands" to "Europe",
        "New Zealand" to "Oceania",
        "Nicaragua" to "North America",
        "Niger" to "Africa",
        "Nigeria" to "Africa",
        "North Korea" to "Asia",
        "North Macedonia" to "Europe",
        "Norway" to "Europe",
        "Oman" to "Asia",
        "Pakistan" to "Asia",
        "Palau" to "Oceania",
        "Palestine" to "Asia",
        "Panama" to "North America",
        "Papua New Guinea" to "Oceania",
        "Paraguay" to "South America",
        "Peru" to "South America",
        "Philippines" to "Asia",
        "Poland" to "Europe",
        "Portugal" to "Europe",
        "Qatar" to "Asia",
        "Romania" to "Europe",
        "Russia" to "Europe",
        "Rwanda" to "Africa",
        "Saint Kitts and Nevis" to "North America",
        "Saint Lucia" to "North America",
        "Saint Vincent and the Grenadines" to "North America",
        "Samoa" to "Oceania",
        "San Marino" to "Europe",
        "Sao Tome and Principe" to "Africa",
        "Saudi Arabia" to "Asia",
        "Senegal" to "Africa",
        "Serbia" to "Europe",
        "Seychelles" to "Africa",
        "Sierra Leone" to "Africa",
        "Singapore" to "Asia",
        "Slovakia" to "Europe",
        "Slovenia" to "Europe",
        "Solomon Islands" to "Oceania",
        "Somalia" to "Africa",
        "South Africa" to "Africa",
        "South Korea" to "Asia",
        "South Sudan" to "Africa",
        "Spain" to "Europe",
        "Sri Lanka" to "Asia",
        "Sudan" to "Africa",
        "Suriname" to "South America",
        "Sweden" to "Europe",
        "Switzerland" to "Europe",
        "Syria" to "Asia",
        "Taiwan" to "Asia",
        "Tajikistan" to "Asia",
        "Tanzania" to "Africa",
        "Thailand" to "Asia",
        "Timor-Leste" to "Asia",
        "Togo" to "Africa",
        "Tonga" to "Oceania",
        "Trinidad and Tobago" to "North America",
        "Tunisia" to "Africa",
        "Turkey" to "Asia",
        "Turkmenistan" to "Asia",
        "Tuvalu" to "Oceania",
        "Uganda" to "Africa",
        "Ukraine" to "Europe",
        "United Arab Emirates" to "Asia",
        "United Kingdom" to "Europe",
        "United States" to "North America",
        "Uruguay" to "South America",
        "Uzbekistan" to "Asia",
        "Vanuatu" to "Oceania",
        "Vatican City" to "Europe",
        "Venezuela" to "South America",
        "Vietnam" to "Asia",
        "Yemen" to "Asia",
        "Zambia" to "Africa",
        "Zimbabwe" to "Africa"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        guessInput = findViewById(R.id.guess_input)
        guessButton = findViewById(R.id.guess_button)
        resultText = findViewById(R.id.result_text)
        distanceText = findViewById(R.id.distance_text)
        continentText = findViewById(R.id.continent_text)
        directionText = findViewById(R.id.direction_text)
        mapView = findViewById(R.id.map_view)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocationName(targetCountry, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                targetAddress = addresses[0]
                targetLocation = Location("").apply {
                    latitude = targetAddress!!.latitude
                    longitude = targetAddress!!.longitude
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        guessButton.setOnClickListener {
            val guessedCountry = guessInput.text.toString()
            handleGuess(guessedCountry)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    private fun handleGuess(guessedCountry: String) {
        googleMap.clear()
        val geocoder = Geocoder(this, Locale.getDefault())
        if (guessedCountry.equals(targetCountry, ignoreCase = true)) {
            resultText.text = "Congratulations! You found the country."
            distanceText.text = ""
            continentText.text = ""
            directionText.text = ""
            val targetLatLng = LatLng(targetLocation!!.latitude, targetLocation!!.longitude)
            googleMap.addMarker(MarkerOptions().position(targetLatLng).title(targetCountry))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLatLng, 5f))
        } else {
            try {
                val addresses = geocoder.getFromLocationName(guessedCountry, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val guessedLocation = Location("").apply {
                        latitude = address.latitude
                        longitude = address.longitude
                    }
                    val distance = targetLocation?.distanceTo(guessedLocation)?.div(1000)

                    resultText.text = "Incorrect. Try again!"
                    distanceText.text = "Distance: %.2f km".format(distance)
                    continentText.text = "Continent: ${getContinent(address)}"
                    directionText.text = "Direction: ${getDirection(guessedLocation, targetLocation!!)}"

                    val guessedLatLng = LatLng(address.latitude, address.longitude)
                    googleMap.addMarker(MarkerOptions().position(guessedLatLng).title(guessedCountry))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guessedLatLng, 2f))

                } else {
                    resultText.text = "Country not found. Try again!"
                    distanceText.text = ""
                    continentText.text = ""
                    directionText.text = ""
                }
            } catch (e: IOException) {
                e.printStackTrace()
                resultText.text = "Error finding country. Try again!"
            }
        }
    }

    private fun getContinent(address: Address): String {
        return countryToContinentMap[address.countryName] ?: address.countryName
    }

    private fun getDirection(start: Location, end: Location): String {
        val bearing = start.bearingTo(end)
        val directions = arrayOf("North", "North-East", "East", "South-East", "South", "South-West", "West", "North-West", "North")
        return directions[((bearing + 22.5) / 45).toInt() % 8]
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}