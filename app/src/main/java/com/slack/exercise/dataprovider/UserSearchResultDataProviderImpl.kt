package com.slack.exercise.dataprovider

import com.slack.exercise.api.SlackApi
import com.slack.exercise.model.UserSearchResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [UserSearchResultDataProvider].
 */
@Singleton
class UserSearchResultDataProviderImpl @Inject constructor(
    private val slackApi: SlackApi
) : UserSearchResultDataProvider {

    /**
     * Returns a set of [UserSearchResult].
     */
    override suspend fun fetchUsers(searchTerm: String): Set<UserSearchResult> {
        return slackApi.searchUsers(searchTerm)
            .map { user ->
                UserSearchResult(user.username)
            }.toSet()
    }
}