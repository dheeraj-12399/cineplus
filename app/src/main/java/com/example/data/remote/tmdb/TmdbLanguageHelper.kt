package com.example.data.remote.tmdb

object TmdbLanguageHelper {

    private val languageMap = mapOf(
        "te" to "Telugu",
        "hi" to "Hindi",
        "ta" to "Tamil",
        "ml" to "Malayalam",
        "kn" to "Kannada",
        "en" to "English",
        "bn" to "Bengali",
        "mr" to "Marathi",
        "pa" to "Punjabi",
        "es" to "Spanish",
        "fr" to "French",
        "ja" to "Japanese",
        "ko" to "Korean",
        "zh" to "Chinese",
        "de" to "German",
        "it" to "Italian"
    )

    fun getLanguageName(isoCode: String?): String {
        if (isoCode.isNullOrBlank()) return "Unknown"
        return languageMap[isoCode.lowercase()] ?: isoCode.uppercase()
    }

    fun getGenreName(id: Int): String {
        return when (id) {
            28 -> "Action"
            12 -> "Adventure"
            16 -> "Animation"
            35 -> "Comedy"
            80 -> "Crime"
            99 -> "Documentary"
            18 -> "Drama"
            10751 -> "Family"
            14 -> "Fantasy"
            36 -> "History"
            27 -> "Horror"
            10402 -> "Music"
            9648 -> "Mystery"
            10749 -> "Romance"
            878 -> "Science Fiction"
            10770 -> "TV Movie"
            53 -> "Thriller"
            10752 -> "War"
            37 -> "Western"
            10759 -> "Action & Adventure"
            10762 -> "Kids"
            10763 -> "News"
            10764 -> "Reality"
            10765 -> "Sci-Fi & Fantasy"
            10766 -> "Soap"
            10767 -> "Talk"
            10768 -> "War & Politics"
            else -> "Feature"
        }
    }

    fun mapGenreIdsToString(ids: List<Int>?): String {
        if (ids.isNullOrEmpty()) return "Cinema"
        return ids.take(3).joinToString(", ") { getGenreName(it) }
    }

    fun getGenreId(name: String): Int? {
        return when (name.lowercase().trim()) {
            "action" -> 28
            "adventure" -> 12
            "animation" -> 16
            "comedy" -> 35
            "crime" -> 80
            "documentary" -> 99
            "drama" -> 18
            "family" -> 10751
            "fantasy" -> 14
            "history" -> 36
            "horror" -> 27
            "music" -> 10402
            "mystery" -> 9648
            "romance" -> 10749
            "science fiction", "sci-fi" -> 878
            "thriller" -> 53
            "war" -> 10752
            "western" -> 37
            else -> null
        }
    }

    fun getLanguageCode(name: String): String? {
        return when (name.lowercase().trim()) {
            "telugu" -> "te"
            "hindi" -> "hi"
            "tamil" -> "ta"
            "malayalam" -> "ml"
            "kannada" -> "kn"
            "english" -> "en"
            "bengali" -> "bn"
            "marathi" -> "mr"
            "punjabi" -> "pa"
            else -> null
        }
    }
}
