package kim.jeonghyeon.sample.feature.apicall

import kim.jeonghyeon.androidlibrary.sample.Repo
import kim.jeonghyeon.androidlibrary.ui.binder.recyclerview.DiffComparable

class RepoItemViewModel(val repo: Repo) : DiffComparable<RepoItemViewModel> {
    override fun areItemsTheSame(item: RepoItemViewModel): Boolean = repo.id == item.repo.id

    override fun areContentsTheSame(item: RepoItemViewModel): Boolean = repo.name == item.repo.name
}