package kim.jeonghyeon.sample.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kim.jeonghyeon.androidlibrary.sample.Repo
import kim.jeonghyeon.sample.R
import kim.jeonghyeon.sample.databinding.ItemRepoBinding
import java.util.*

class RepoAdapter : RecyclerView.Adapter<RepoAdapter.RepoViewHolder>(), Filterable {


    private val mFilter: Filter
    private val mOriginalList: MutableList<Repo>
    private val mFilteredList: MutableList<Repo>

    init {
        mOriginalList = ArrayList()
        mFilteredList = ArrayList()
        mFilter = RepoFilter()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val viewHolder = RepoViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_repo,
                parent,
                false
            )
        )

        viewHolder.itemView.setOnClickListener { v ->

            val position = viewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            mFilteredList[position]

        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(mFilteredList[position])
    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    fun setList(list: List<Repo>) {
        mOriginalList.clear()
        mFilteredList.clear()
        mOriginalList.addAll(list)
        mFilteredList.addAll(list)
    }

    fun clear() {
        mFilteredList.clear()
        mFilteredList.addAll(mOriginalList)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return mFilter
    }

    private inner class RepoFilter : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            if (constraint.isNullOrEmpty()) {
                return FilterResults().apply {
                    values = mOriginalList
                    count = mOriginalList.size
                }
            }

            return mOriginalList.filter {
                it.name.startsWith(constraint)
            }.let {
                FilterResults().apply {
                    values = it
                    count = it.size
                }
            }
        }

        @Synchronized
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            val resultList = results.values as List<Repo>
            val callback = RepoDiffCallback(mFilteredList, resultList)
            val result = DiffUtil.calculateDiff(callback)

            mFilteredList.clear()
            mFilteredList.addAll(resultList)
            result.dispatchUpdatesTo(this@RepoAdapter)
        }
    }

    class RepoViewHolder(private val binding: ItemRepoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(repo: Repo) {
            binding.model = repo
            binding.executePendingBindings()
        }
    }

    companion object {
        private val TAG = "RepoAdapter"
    }
}

