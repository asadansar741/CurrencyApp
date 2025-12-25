package com.asad.currency

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform