package curso.kotlin.pruebasg

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

//https://api.themoviedb.org/3/movie/popular?api_key=5ead30eff86e4911e669a5d88b620dbb&page=1
interface MovieInterface {
    @GET("movie/popular?api_key=5ead30eff86e4911e669a5d88b620dbb&page=1")
    fun getPopularMovie(): Observable<MovieResponse>

    companion object {
        fun create(): MovieInterface {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.themoviedb.org/3/")
                .build()

            return retrofit.create(MovieInterface::class.java)
        }
    }
}