package com.slack.exercise.dataprovider

import com.slack.exercise.model.UserSearchResult

/**
 * Provider of [UserSearchResult].
 * This interface abstracts the logic of searching for users through the API or other data sources.
 */
interface UserSearchResultDataProvider {

  /**
   * Returns a set of [UserSearchResult].
   */
  suspend fun fetchUsers(searchTerm: String): Set<UserSearchResult>
}