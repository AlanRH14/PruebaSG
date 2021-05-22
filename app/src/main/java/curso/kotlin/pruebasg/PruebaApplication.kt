package curso.kotlin.pruebasg

import android.app.Application
import com.google.firebase.auth.FirebaseUser

class PruebaApplication : Application() {
    companion object {
        const val RC_SIGN_IN = 1
        const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w342"
        const val REQUEST_CODE_LOCATION = 0
        const val RC_GALLERY = 2
        const val PATH_PRUEBA_SG = "prueba_sg"

        lateinit var currentUser: FirebaseUser
    }
}