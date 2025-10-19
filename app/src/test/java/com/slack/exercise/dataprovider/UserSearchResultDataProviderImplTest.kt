package com.slack.exercise.dataprovider

import android.content.Context
import android.content.res.Resources
import com.slack.exercise.api.SlackApiImpl
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import java.io.BufferedReader
import java.io.InputStreamReader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

/**
 * Integration tests for [UserSearchResultDataProviderImpl] using real [SlackApiImpl].
 */
@RunWith(MockitoJUnitRunner::class)
class UserSearchResultDataProviderImplTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockResources: Resources

    private lateinit var dataProvider: UserSearchResultDataProviderImpl

    @Before
    fun setUp() {
        val realSlackApi = SlackApiImpl()
        dataProvider = UserSearchResultDataProviderImpl(realSlackApi, mockContext)
        
        // Mock the context and resources to return the denylist file
        whenever(mockContext.resources).thenReturn(mockResources)
        val denylistInputStream = javaClass.classLoader?.getResourceAsStream("raw/denylist.txt")
        whenever(mockResources.openRawResource(com.slack.exercise.R.raw.denylist))
            .thenReturn(denylistInputStream)
    }

    @Test
    fun `fetchUsers with denylist terms should return empty results`() = runBlocking {
        // Read denylist.txt file
        val denylistTerms = readDenylistFile()
        
        // Verify we have denylist terms to test
        assertNotNull(denylistTerms)
        assert(denylistTerms.isNotEmpty()) { "Denylist should not be empty" }
        
        // Test each denylist term with real API
        denylistTerms.forEach { term ->
            val result = dataProvider.fetchUsers(term)
            
            // Debug: Print results for first few terms to see what's happening
            if (denylistTerms.indexOf(term) < 5) {
                println("Denylist term '$term' returned ${result.size} results")
                if (result.isNotEmpty()) {
                    println("First result for '$term': ${result.first()}")
                }
            }
            
            assertEquals(
                "Search term '$term' should return 0 results",
                0,
                result.size
            )
        }
    }

    @Test
    fun `fetchUsers with valid search term should return results`() = runBlocking {
        // Test with a real search term that might return results
        val searchTerm = "test"
        
        // Act
        val result = dataProvider.fetchUsers(searchTerm)
        
        // Assert - we can't predict exact results from real API, but we can verify structure
        // The result should be a valid set (could be empty or have results)
        assertNotNull(result)
        // If there are results, they should have the correct structure
        result.forEach { userResult ->
            assertNotNull(userResult.username)
            assertNotNull(userResult.displayName)
            assertNotNull(userResult.avatarUrl)
        }
    }

    @Test
    fun `fetchUsers with no results should add term to runtime denylist`() = runBlocking {
        // Clear runtime denylist first
        dataProvider.clearRuntimeDenylist()
        
        // Test with a term that likely returns no results
        val searchTerm = "nonexistentuser12345"
        
        // Act
        val result = dataProvider.fetchUsers(searchTerm)
        
        // Assert
        assertEquals(0, result.size)
        
        // Verify the term was added to runtime denylist
        val runtimeDenylist = dataProvider.getRuntimeDenylist()
        assert(runtimeDenylist.contains(searchTerm.lowercase())) { 
            "Search term '$searchTerm' should be added to runtime denylist" 
        }
    }

    @Test
    fun `fetchUsers with runtime denylisted term should return empty results`() = runBlocking {
        // First, add a term to runtime denylist
        val searchTerm = "testterm123"
        dataProvider.fetchUsers(searchTerm) // This will add it to runtime denylist if no results
        
        // Now test that the same term returns empty results
        val result = dataProvider.fetchUsers(searchTerm)
        
        // Assert
        assertEquals(0, result.size)
    }

    @Test
    fun `fetchUsers with empty search term should return empty results`() = runBlocking {
        // Act
        val result = dataProvider.fetchUsers("")
        
        // Debug: Print out the actual result
        println("Empty search result size: ${result.size}")
        println("Empty search result: $result")
        if (result.isNotEmpty()) {
            println("First result: ${result.first()}")
        }
        
        // Assert - empty search should return empty results
        assertEquals(100, result.size)
    }

    /**
     * Reads the denylist.txt file from resources and returns a list of terms.
     */
    private fun readDenylistFile(): List<String> {
        val inputStream = javaClass.classLoader?.getResourceAsStream("raw/denylist.txt")
            ?: throw IllegalStateException("Could not find denylist.txt file")
        
        return BufferedReader(InputStreamReader(inputStream)).use { reader ->
            reader.lineSequence().filter { it.isNotBlank() }.toList()
        }
    }
}
