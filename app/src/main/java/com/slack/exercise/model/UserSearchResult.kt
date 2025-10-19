package com.slack.exercise.model

/**
 * Models users returned by the API.
 */

data class UserSearchResult(val avatarUrl: String, val displayName: String, val username: String)