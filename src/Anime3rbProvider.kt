package com.anime3rb

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.Jsoup

class Anime3rbProvider : MainAPI() {
    override var mainUrl = "https://anime3rb.com"
    override var name = "Anime3rb"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Anime)

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val document = app.get(mainUrl).document
        val items = document.select("div.anime-card a").map {
            val title = it.selectFirst(".anime-title")?.text() ?: return@map null
            val url = it.attr("href")
            val poster = it.selectFirst("img")?.attr("data-src") ?: ""
            AnimeSearchResponse(title, url, this.name, TvType.Anime, poster)
        }.filterNotNull()
        return newHomePageResponse("آخر الأنميات") {
            add(items)
        }
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url).document
        val title = doc.selectFirst("h1.entry-title")?.text() ?: "Anime3rb"
        val poster = doc.selectFirst(".anime-thumbnail img")?.attr("data-src") ?: ""
        val episodes = doc.select("ul.episodes-list li a").map {
            Episode(it.attr("href"), it.text())
        }

        return newAnimeLoadResponse(title, url, TvType.Anime) {
            this.posterUrl = poster
            this.episodes = episodes
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val doc = app.get(data).document
        val iframeUrl = doc.selectFirst("iframe")?.attr("src") ?: return
        loadExtractor(iframeUrl, data, subtitleCallback, callback)
    }
}
