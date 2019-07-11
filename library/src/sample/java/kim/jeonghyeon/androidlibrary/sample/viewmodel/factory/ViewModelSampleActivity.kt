package kim.jeonghyeon.androidlibrary.sample.viewmodel.factory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import kim.jeonghyeon.androidlibrary.sample.viewmodel.SampleViewModel

class ViewModelSampleActivity : AppCompatActivity() {
    private lateinit var viewModel: SampleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setContentView(R.layout.viewmodel_sample_layout)

        // get the view model
        viewModel = ViewModelProviders.of(this, Injection.provideViewModelFactory(this))[SampleViewModel::class.java]
//
//        // add dividers between RecyclerView's row items
//        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
//        list.addItemDecoration(decoration)
//        setupScrollListener()
//
//        initAdapter()
//        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
//        viewModel.searchRepo(query)
//        initSearch(query)
    }
}