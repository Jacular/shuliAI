package com.shuli.cc.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform