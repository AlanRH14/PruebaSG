package curso.kotlin.pruebasg.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import curso.kotlin.pruebasg.R
import curso.kotlin.pruebasg.databinding.FragmentLocationBinding

class LocationFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentLocationBinding
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mMapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        googleMap.apply {
            uiSettings.isZoomControlsEnabled = false
            uiSettings.isMyLocationButtonEnabled = true

            val favoritePlace = LatLng(28.044195, -16.5363842)
            addMarker(MarkerOptions().position(favoritePlace).title("Mi playa favorita!"))

            moveCamera(CameraUpdateFactory.newLatLng(favoritePlace))

            animateCamera(
                CameraUpdateFactory.newLatLngZoom(favoritePlace, 18f),
                4000,
                null
            )
        }
    }
}