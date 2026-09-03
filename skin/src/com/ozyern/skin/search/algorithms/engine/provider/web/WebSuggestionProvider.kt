package com.ozyern.skin.search.algorithms.engine.provider.web

import android.content.Context
import android.util.Log
import com.ozyern.skin.preferences.PreferenceManager
import com.ozyern.skin.preferences2.PreferenceManager2
import com.ozyern.skin.preferences2.firstCached
import com.ozyern.skin.search.algorithms.engine.SearchProvider
import com.ozyern.skin.search.algorithms.engine.SearchResult
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.timeout

object WebSuggestionProvider : SearchProvider {
    override val id = "web_suggestions"

    override fun search(
        context: Context,
        query: String,
    ): Flow<List<SearchResult>> {
        val prefs = PreferenceManager.getInstance(context)
        val prefs2 = PreferenceManager2.getInstance(context)

        if (query.isBlank() || !prefs.searchResultStartPageSuggestion.get()) {
            return flow { emit(emptyList()) }
        }

        val provider = prefs2.webSuggestionProvider.firstCached()
        val timeout = prefs2.maxWebSuggestionDelay.firstCached()
        val maxResults = prefs2.maxWebSuggestionResultCount.firstCached()

        val webProvider = provider
            .configure(context)

        return webProvider.getSuggestions(query)
            .timeout(timeout.milliseconds)
            .catch {
                if (it is TimeoutCancellationException) {
                    Log.w(TAG, "Web suggestion request timed out")
                    emit(emptyList())
                }
            }
            .map { suggestions ->
                suggestions
                    .take(maxResults)
                    .map { suggestion ->
                        SearchResult.WebSuggestion(suggestion = suggestion, provider = webProvider.id)
                    }
            }
    }

    const val TAG: String = "WebSuggestionProvider"
}
