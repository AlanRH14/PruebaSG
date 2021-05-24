package curso.kotlin.pruebasg.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.*
import android.os.health.TimerStat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import curso.kotlin.pruebasg.PruebaApplication
import curso.kotlin.pruebasg.databinding.FragmentFirestoreBinding
import curso.kotlin.pruebasg.models.LocationModel

class FirestoreFragment : Fragment() {
    private lateinit var binding: FragmentFirestoreBinding
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var mLocationReference: DatabaseReference

    var time = 60

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirestoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLocationReference =
            FirebaseDatabase.getInstance().reference.child(PruebaApplication.PATH_LOCATION)
        fusedLocation = LocationServices.getFusedLocationProviderClient(context!!)

        //binding.btnMyLocation.setOnClickListener { getLocation() }

        timer()
    }

    private fun timer() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (time > 0) {
                time--
                timer()
                binding.txtTimer.text = time.toString()
                binding.prbAvance.progress = (time)
            } else {
                if (time == 0) {
                    time = 60
                    binding.txtTimer.text = time.toString()
                    getLocation()
                    timer()
                }
            }
        }, 1000)
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        fusedLocation.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                val locationModel = LocationModel(latitude, longitude)
                val key = mLocationReference.push().key!!

                mLocationReference.child(key).setValue(locationModel)
                    .addOnSuccessListener {
                        Toast.makeText(
                            context!!,
                            "Latitud: $latitude Logitud: $longitude",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context!!,
                            "No se subio",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }
}