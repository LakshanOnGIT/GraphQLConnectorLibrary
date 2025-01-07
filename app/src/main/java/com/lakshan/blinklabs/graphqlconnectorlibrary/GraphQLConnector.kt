package com.lakshan.blinklabs.graphqlconnectorlibrary

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.api.NormalizedCacheFactory
import com.apollographql.apollo3.cache.normalized.apolloStore
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo3.network.okHttpClient
import com.apollographql.apollo3.network.ws.WebSocketNetworkTransport
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object GraphQLConnector {

    private var apolloClient: ApolloClient? = null
    //private var mockServer: MockServer? = null

    /**
     * Initializes the Apollo Client with caching and logging enabled.
     *
     * @param context Application context.
     * @param serverUrl GraphQL endpoint URL.
     */
    fun init(context: Context, serverUrl: String) {
        if (apolloClient == null) {
            // Set up logging interceptor
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            // Set up SQL-based normalized cache for persistence
            val cacheFactory: NormalizedCacheFactory = SqlNormalizedCacheFactory(context, "graphql_cache")

            apolloClient = ApolloClient.Builder()
                .serverUrl(serverUrl)
                .okHttpClient(okHttpClient)
                .normalizedCache(cacheFactory)
                .build()
        }
    }

    /**
     * Initializes Apollo Client for WebSocket subscriptions.
     *
     * @param serverUrl WebSocket URL for subscriptions.
     */
    fun initWebSocketClient(serverUrl: String) {
        apolloClient = ApolloClient.Builder()
            .networkTransport(WebSocketNetworkTransport.Builder().serverUrl(serverUrl).build())
            .build()
    }

    /**
     * Initializes a Mock Server for testing.
     *
     * @param mockPort Port for the mock server.
     */
    fun initMockServer(mockPort: Int) {
        //mockServer = MockServer(mockPort)
        //mockServer?.start()
    }

    /**
     * Returns the Apollo Client instance.
     *
     * @return ApolloClient
     */
    fun getClient(): ApolloClient {
        return apolloClient ?: throw IllegalStateException("GraphQLConnector is not initialized. Call init() first.")
    }

    /**
     * Clears the cache.
     */
    fun clearCache() {
        apolloClient?.apolloStore?.clearAll()
    }

    /**
     * Utility method to prevent over-fetching by limiting query depth.
     *
     * @param queryDepth Limit on query depth.
     * @return boolean indicating whether query depth is valid.
     */
    fun validateQueryDepth(queryDepth: Int): Boolean {
        // Example rule: No query depth should exceed 10 levels
        return queryDepth <= 10
    }

    /**
     * Enables security checks such as rate limiting (stub example).
     *
     * @param query String representing the query.
     * @return boolean indicating whether the query is valid.
     */
    fun validateQuerySecurity(query: String): Boolean {
        // Example validation: block deeply nested queries
        return !query.contains("... on") || query.split("{ ").size <= 15
    }

    /**
     * Stops the mock server.
     */
    fun stopMockServer() {
        //mockServer?.stop()
    }
}