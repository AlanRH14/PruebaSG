package curso.kotlin.pruebasg.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import curso.kotlin.pruebasg.models.MovieList
import curso.kotlin.pruebasg.PruebaApplication
import curso.kotlin.pruebasg.R
import curso.kotlin.pruebasg.databinding.ItemMovieBinding

class MovieAdapter(private var movies: ArrayList<MovieList>) :
    RecyclerView.Adapter<MovieAdapter.ViewHolder>() {
    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_movie, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        val movieImageURL = PruebaApplication.POSTER_BASE_URL + movie.posterPath
        with(holder) {
            binding.tvMovieTitle.text = movie.title
            binding.tvMovieReleaseDate.text = movie.releaseDate

            Glide.with(mContext)
                .load(movieImageURL)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgMovie)
        }
    }

    override fun getItemCount(): Int = movies.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemMovieBinding.bind(view)
    }
}