package com.slack.exercise.dataprovider

import android.content.Context
import com.slack.exercise.api.SlackApi
import com.slack.exercise.model.UserSearchResult
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [UserSearchResultDataProvider].
 */
@Singleton
class UserSearchResultDataProviderImpl @Inject constructor(
    private val slackApi: SlackApi,
    private val context: Context
) : UserSearchResultDataProvider {

    // Feature flag to enable/disable auto-denylist functionality
    private val autoDenylistEnabled: Boolean = true
    
    private val denylist: Set<String> by lazy {
        loadDenylist()
    }
    
    // Runtime denylist for terms that return no results
    private val runtimeDenylist = ConcurrentHashMap<String, Boolean>()

    /**
     * Returns a set of [UserSearchResult].
     */
    override suspend fun fetchUsers(searchTerm: String): Set<UserSearchResult> {
        // Check if search term matches or starts with any denylist term
        if (isDenylisted(searchTerm)) {
            return emptySet()
        }
        
        val apiResults = slackApi.searchUsers(searchTerm)
            .map { user ->
                UserSearchResult(user.avatarUrl, user.displayName, user.username)
            }.toSet()
        
        // Auto-denylist feature: if API returns no results, add to runtime denylist
        if (autoDenylistEnabled && apiResults.isEmpty() && searchTerm.isNotBlank()) {
            runtimeDenylist[searchTerm.lowercase()] = true
        }
        
        return apiResults
    }

    /**
     * Checks if the search term matches or starts with any denylist term.
     */
    private fun isDenylisted(searchTerm: String): Boolean {
        // Check static denylist from file
        val isInStaticDenylist = denylist.any { denylistTerm ->
            searchTerm.equals(denylistTerm, ignoreCase = true) ||
            searchTerm.startsWith(denylistTerm, ignoreCase = true)
        }
        
        // Check runtime denylist (auto-added terms)
        val isInRuntimeDenylist = runtimeDenylist.containsKey(searchTerm.lowercase())
        
        return isInStaticDenylist || isInRuntimeDenylist
    }

    /**
     * Loads the denylist from resources.
     */
    private fun loadDenylist(): Set<String> {
        return try {
            val inputStream = context.resources.openRawResource(com.slack.exercise.R.raw.denylist)
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.lineSequence()
                    .filter { it.isNotBlank() }
                    .map { it.trim() }
                    .toSet()
            }
        } catch (e: Exception) {
            emptySet()
        }
    }
    
    /**
     * Gets the current runtime denylist for debugging purposes.
     */
    fun getRuntimeDenylist(): Set<String> {
        return runtimeDenylist.keys.toSet()
    }
    
    /**
     * Clears the runtime denylist (useful for testing).
     */
    fun clearRuntimeDenylist() {
        runtimeDenylist.clear()
    }
}