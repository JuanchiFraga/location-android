package com.rembertime.app.util

class Event<out T>(private val content: T) {

    private var consumed = false

    /**
     * Consumes the content if it's not been consumed yet
     *
     * @return The unconsumed content or `null` if it was already consumed
     */
    fun consume(): T? = if (consumed) null else { content.also { consumed = true } }

    /**
     * Retrieve the content whether it's been consumed or not.
     *
     * @return The content
     */
    fun peek(): T = content
}