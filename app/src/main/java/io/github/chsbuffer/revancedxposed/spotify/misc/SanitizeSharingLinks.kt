package io.github.chsbuffer.revancedxposed.spotify.misc

import android.content.ClipData
import app.revanced.extension.spotify.misc.privacy.SanitizeSharingLinksPatch
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import io.github.chsbuffer.revancedxposed.ScopedHook
import io.github.chsbuffer.revancedxposed.spotify.SpotifyHook
import java.lang.reflect.Modifier

fun SpotifyHook.SanitizeSharingLinks() {
    getDexMethod("shareCopyUrlFingerprint") {
        runCatching {
            findMethod { matcher {
                returns("Ljava/lang/Object;")
                parameters("Ljava/lang/Object;")
                strings("clipboard", "Spotify Link")
                name("invokeSuspend")
            }}.single()
        }.getOrElse {
            findMethod { matcher {
                returns("Ljava/lang/Object;")
                parameters("Ljava/lang/Object;")
                strings("clipboard", "createNewSession failed")
                name("apply")
            }}.single()
        }
    }.hookMethod(
        ScopedHook(
            XposedHelpers.findMethodExact(
                ClipData::class.java.name,
                classLoader,
                "newPlainText",
                CharSequence::class.java,
                CharSequence::class.java
            )
        ) {
            before {
                val url = param.args[1] as String
                param.args[1] = SanitizeSharingLinksPatch.sanitizeUrl(url)
            }
        }
    )

    getDexMethod("formatAndroidShareSheetUrlFingerprint") {
        runCatching {
            findMethod {
                matcher {
                    returns("Ljava/lang/String;")
                    usingNumbers('\n'.code)
                    modifiers(Modifier.PUBLIC or Modifier.STATIC)
                    parameters("Ljava/lang/String;")
                }
            }.single {
                !it.usingStrings.contains("")
            }
        }.getOrElse {
            findMethod {
                matcher {
                    returns("Ljava/lang/String;")
                    usingNumbers('\n'.code)
                    modifiers(Modifier.PUBLIC)
                    parameters("Lcom/spotify/share/social/sharedata/ShareData;", "Ljava/lang/String;")
                }
            }.single {
                !it.usingStrings.contains("")
            }
        }
    }.hookMethod(object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            val url = param.args[1] as String
            param.args[1] = SanitizeSharingLinksPatch.sanitizeUrl(url)
        }
    })
}