package curso.kotlin.pruebasg.fragments

import MovieAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import curso.kotlin.pruebasg.*
import curso.kotlin.pruebasg.databinding.FragmentRestBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RestFragment : Fragment() {
    private lateinit var binding: FragmentRestBinding
    private lateinit var mMovieAdapter: MovieAdapter
    private lateinit var mGridLayout: GridLayoutManager

    private var disposable: Disposable? = null
    private var movieList: ArrayList<MovieList> = ArrayList()
    private val apiService by lazy { MovieInterface.create() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRestBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun parseJSON() {
        disposable = apiService.getPopularMovie()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                movieList.clear()
                movieList.addAll(result.movieList)
                mMovieAdapter.notifyDataSetChanged()

                binding.progressBar.visibility = View.GONE
            },
                { error ->
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                })
    }

    private fun setupRecyclerView() {
        mMovieAdapter = MovieAdapter(movieList)
        mGridLayout = GridLayoutManager(context, resources.getInteger(R.integer.main_columns))
        parseJSON()

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mMovieAdapter
        }
    }
}