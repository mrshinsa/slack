package com.slack.exercise.api

import retrofit2.http.GET
import retrofit2.http.Query

interface UserSearchService {
  /**
   * Search query. Returns the API response.
   */
  @GET("search")
  suspend fun searchUsers(@Query("query") query: String): UserSearchResponse
}

/**
 * Models the search query response.
 */
data class UserSearchResponse(val ok: Boolean, val users: List<User>)