package com.lakshan.blinklabs.graphqlconnectorlibrary

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo3.network.okHttpClient
import com.apollographql.apollo3.network.ws.WebSocketNetworkTransport
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class TestClass {

    private lateinit var mockContext: Context
    private val serverUrl = "https://example.com/graphql"
    private val webSocketUrl = "wss://example.com/subscriptions"

    @Before
    fun setUp() {
        mockContext = mock(Context::class.java)
    }

    @Test
    fun `test ApolloClient initialization`() {
        GraphQLConnector.init(mockContext, serverUrl)

        val apolloClient = GraphQLConnector.getClient()
        assertNotNull("ApolloClient should not be null after initialization", apolloClient)

        val cacheFactory = SqlNormalizedCacheFactory(mockContext, "graphql_cache")
        val expectedClient = ApolloClient.Builder()
            .serverUrl(serverUrl)
            .okHttpClient(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
                    .build()
            )
            .normalizedCache(cacheFactory)
            .build()

        assertEquals("Server URL should match", serverUrl, apolloClient.serverUrl)
    }

    @Test
    fun `test WebSocketClient initialization`() {
        GraphQLConnector.initWebSocketClient(webSocketUrl)

        val apolloClient = GraphQLConnector.getClient()
        assertNotNull("ApolloClient should not be null after WebSocket initialization", apolloClient)
        assertTrue(
            "ApolloClient should use WebSocketNetworkTransport",
            apolloClient.networkTransport is WebSocketNetworkTransport
        )
    }

    @Test
    fun `test clear cache`() {
        GraphQLConnector.init(mockContext, serverUrl)

        GraphQLConnector.clearCache()
        val apolloClient = GraphQLConnector.getClient()
        assertNotNull("ApolloClient should not be null after clearing cache", apolloClient)
        assertDoesNotThrow("Cache clearing should not throw exceptions") {
            apolloClient.apolloStore.clearAll()
        }
    }

    @Test
    fun `test validate query depth`() {
        val validDepth = 5
        val invalidDepth = 15

        assertTrue("Query depth within limit should be valid", GraphQLConnector.validateQueryDepth(validDepth))
        assertFalse("Query depth exceeding limit should be invalid", GraphQLConnector.validateQueryDepth(invalidDepth))
    }

    @Test
    fun `test validate query security`() {
        val validQuery = """
            {
                user {
                    id
                    name
                }
            }
        """.trimIndent()

        val invalidQuery = """
            {
                user {
                    id
                    name
                    ... on Admin {
                        permissions {
                            ... on Details {
                                field1
                                field2
                            }
                        }
                    }
                }
            }
        """.trimIndent()

        assertTrue("Simple query should pass security validation", GraphQLConnector.validateQuerySecurity(validQuery))
        assertFalse("Deeply nested query should fail security validation", GraphQLConnector.validateQuerySecurity(invalidQuery))
    }

    @Test(expected = IllegalStateException::class)
    fun `test uninitialized client throws exception`() {
        GraphQLConnector.clearCache() // Ensures the client is not initialized
        GraphQLConnector.getClient()
    }
}