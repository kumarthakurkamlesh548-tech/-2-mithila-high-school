package com.example.util

import java.util.Locale

/**
 * Centralized Content Moderation Utility.
 * Provides text normalization and blocked word filtering for Doubts and Chat modules.
 */
object ContentModerator {

    /**
     * Centralized blocked words list.
     * Add or remove prohibited words easily here in one place.
     */
    val blockedWords: MutableList<String> = mutableListOf(
        "badword",
        "abuse",
        "spam",
        "hate",
        "idiot",
        "stupid",
        "scam",
        "fraud",
        "cheat",
        "fool",
        "dumb",
        "cursing",
        "slangword"
    )

    const val PROHIBITED_LANGUAGE_MESSAGE = "This message contains prohibited language."

    /**
     * Normalizes text according to system requirements:
     * - Lowercase
     * - Remove repeated spaces
     * - Common symbol substitutions (@ -> a, $ -> s, !/1/| -> i, 0 -> o, 3 -> e, 5 -> s, 4 -> a, 7 -> t, 8 -> b)
     */
    fun normalizeText(input: String): String {
        if (input.isBlank()) return ""

        var text = input.lowercase(Locale.ROOT)

        // Common symbol substitutions
        text = text
            .replace('@', 'a')
            .replace('$', 's')
            .replace('!', 'i')
            .replace('1', 'i')
            .replace('|', 'i')
            .replace('0', 'o')
            .replace('3', 'e')
            .replace('5', 's')
            .replace('4', 'a')
            .replace('7', 't')
            .replace('8', 'b')
            .replace('+', 't')

        // Replace non-alphanumeric symbols with spaces
        val cleaned = StringBuilder()
        for (ch in text) {
            if (ch.isLetterOrDigit() || ch.isWhitespace()) {
                cleaned.append(ch)
            } else {
                cleaned.append(' ')
            }
        }

        // Remove repeated spaces and return normalized string
        return cleaned.toString().trim().replace("\\s+".toRegex(), " ")
    }

    /**
     * Checks if the given text contains any blocked word.
     * Returns true if prohibited language is detected.
     */
    fun containsProhibitedLanguage(input: String): Boolean {
        if (input.isBlank()) return false

        val normalized = normalizeText(input)
        val rawLower = input.lowercase(Locale.ROOT)

        for (word in blockedWords) {
            val normalizedWord = normalizeText(word)
            if (normalizedWord.isBlank()) continue

            // Word token match
            val tokens = normalized.split(" ")
            if (tokens.contains(normalizedWord)) {
                return true
            }

            // Direct substring match
            if (normalized.contains(normalizedWord) || rawLower.contains(word.lowercase(Locale.ROOT))) {
                return true
            }
        }

        return false
    }
}
