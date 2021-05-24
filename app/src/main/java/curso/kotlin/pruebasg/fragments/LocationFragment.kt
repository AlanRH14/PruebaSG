package curso.kotlin.pruebasg.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import curso.kotlin.pruebasg.R
import curso.kotlin.pruebasg.databinding.FragmentLocationBinding

class LocationFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentLocationBinding
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mMapView: MapView
    private lateinit var fusedLocation: FusedLocationProviderClient

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocation = LocationServices.getFusedLocationProviderClient(context!!)

        createMap()
    }

    private fun createMap() {
        val mapFragment =
                requireActivity().supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        mMapView = binding.map
        mMapView.onCreate(null)
        mMapView.onResume()
        mMapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        MapsInitializer.initialize(context!!)
        mGoogleMap = googleMap!!
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        mGoogleMap.isMyLocationEnabled = true
        mGoogleMap.uiSettings.isZoomControlsEnabled = true
        mGoogleMap.uiSettings.isCompassEnabled = true

        fusedLocation.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val myLocation = LatLng(location.latitude, location.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12f))
            }
        }

        mGoogleMap.setOnMapLongClickListener {
            val markerOptions = MarkerOptions().position(it)

            mGoogleMap.addMarker(markerOptions)
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(it))
        }
    }
}