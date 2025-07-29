package io.github.chsbuffer.revancedxposed.spotify.misc

import de.robv.android.xposed.XC_MethodHook
import io.github.chsbuffer.revancedxposed.findMethod
import io.github.chsbuffer.revancedxposed.spotify.SpotifyHook
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

fun SpotifyHook.ChangeLyricsProvider() {
    val lyricsProviderHost = "lyrics.natanchiodi.fr"

    getDexKitBridge().findMethod {
        matcher {
            parameters("Ljava/lang/String;")
            returns("Lokhttp3/HttpUrl;")
        }
    }.forEach { methodData ->
        methodData.hookMethod(object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val url = param.args[0] as String
                if (url.contains("lyrics")) {
                    val newUrl = url.toHttpUrl().newBuilder().host(lyricsProviderHost).build()
                    param.result = newUrl
                }
            }
        })
    }
}