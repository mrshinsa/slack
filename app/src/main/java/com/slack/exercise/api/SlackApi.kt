package com.slack.exercise.api

/**
 * Interface to the backend API.
 */
interface SlackApi {
  /**
   * Fetches users with name or username matching the [searchTerm].
   * Calling the API passing an empty [searchTerm] fetches the entire team directory.
   *
   * Returns a set of [User] returned by the API or
   * an empty set if no users are found.
   *
   */
  suspend fun searchUsers(searchTerm: String): List<User>
}