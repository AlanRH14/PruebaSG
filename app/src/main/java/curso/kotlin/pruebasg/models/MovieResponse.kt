package curso.kotlin.pruebasg.models

import com.google.gson.annotations.SerializedName

class MovieResponse {
    val page: Int = 0

    @SerializedName("results")
    var movieList: List<MovieList> = ArrayList()

    @SerializedName("total_pages")
    val totalPages: Int = 0

    @SerializedName("total_results")
    val totalResults: Int = 0
}

class MovieList {
    val id: Int = 0

    @SerializedName("poster_path")
    val posterPath: String = ""

    @SerializedName("release_date")
    val releaseDate: String = ""

    val title: String = ""
}