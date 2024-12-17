package com.example.catalogsvg

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.catalogsvg.databinding.FragmentMapsBinding
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.util.zip.ZipInputStream
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.io.FileInputStream
import java.io.InputStreamReader
import java.text.DecimalFormat

class MapFragment : Fragment() {
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val view = binding.root
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val bundle = Bundle().apply {
                putBoolean("haveApiKey" , true)
            }
            findNavController().navigate(R.id.action_mapsFragment_to_catalogFragment, bundle)
        }
        return view
    }

    fun parseKmzFile(kmzFilePath: String): List<MarkerData> {
        val markers = mutableListOf<MarkerData>()
        val zipInputStream = ZipInputStream(FileInputStream(kmzFilePath))
        var entry = zipInputStream.nextEntry

        while (entry != null) {
            if (entry.name.endsWith(".kml")) {
                val parserFactory = XmlPullParserFactory.newInstance()
                val parser = parserFactory.newPullParser()
                parser.setInput(InputStreamReader(zipInputStream))

                var eventType = parser.eventType
                var currentMarker: MarkerData? = null

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    val tagName = parser.name
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            if (tagName.equals("Placemark", ignoreCase = true)) {
                                currentMarker = MarkerData()
                            } else if (currentMarker != null) {
                                when (tagName) {
                                    "name" -> currentMarker.name = parser.nextText()
                                    "description" -> currentMarker.description = parser.nextText()
                                    "coordinates" -> {
                                        val coords = parser.nextText().split(",")
                                        currentMarker.longitude = coords[0].toDouble()
                                        currentMarker.latitude = coords[1].toDouble()
                                    }
                                }
                            }
                        }
                        XmlPullParser.END_TAG -> {
                            if (tagName.equals("Placemark", ignoreCase = true)) {
                                currentMarker?.let { markers.add(it) }
                            }
                        }
                    }
                    eventType = parser.next()
                }
            }
            entry = zipInputStream.nextEntry
        }
        zipInputStream.close()
        return markers
    }

    fun showMarkersOnMap(markers: List<MarkerData>) {
        val mapObjects = markers.map { marker ->
            MapObject(
                marker.name,
                marker.description,
                marker.latitude,
                marker.longitude
            )
        }
        OrganicMapsApi.showMapObjects(this, "Markers from KMZ", mapObjects)
    }
}
data class MarkerData(
    var name: String = "",
    var description: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)












