package com.slack.exercise.ui.usersearch

import com.slack.exercise.ui.usersearch.model.UserSearchState
import kotlinx.coroutines.flow.StateFlow

/**
 * MVP contract for User Search.
 */
interface UserSearchPresenter {

    /**
     * Returns a [StateFlow] with the latest [UserSearchState] to be displayed
     */
    fun getUserSearchState(): StateFlow<UserSearchState>

    /**
     * Notifies the presenter that the [searchTerm] has changed.
     */
    fun onQueryTextChange(searchTerm: String)

    /**
     * Call to detach a [UserSearchPresenter] and clean up resources.
     */
    fun detach()

}