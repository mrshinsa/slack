package com.slack.exercise.ui.usersearch

import com.slack.exercise.dataprovider.UserSearchResultDataProvider
import com.slack.exercise.ui.usersearch.model.UserSearchState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Presenter responsible for reacting to user inputs and initiating search queries.
 */
class UserSearchPresenterImpl @Inject constructor(
    private val userNameResultDataProvider: UserSearchResultDataProvider
) : UserSearchPresenter {

    private val searchQuerySharedFlow =
        MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val stateFlow: MutableStateFlow<UserSearchState> =
        MutableStateFlow(UserSearchState(emptySet()))

    override fun getUserSearchState(): StateFlow<UserSearchState> = stateFlow

    init {
        scope.launch {
            searchQuerySharedFlow.map {
                if (it.isEmpty()) {
                    emptySet()
                } else {
                    userNameResultDataProvider.fetchUsers(it)
                }
            }.collect {
                stateFlow.tryEmit(UserSearchState(it))
            }
        }
    }

    override fun detach() {
        scope.cancel()
    }

    override fun onQueryTextChange(searchTerm: String) {
        searchQuerySharedFlow.tryEmit(searchTerm)
    }
}