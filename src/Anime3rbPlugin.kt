package com.anime3rb

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class Anime3rbPlugin : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(Anime3rbProvider())
    }
}
