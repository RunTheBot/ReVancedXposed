package io.github.chsbuffer.revancedxposed.spotify

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import app.revanced.extension.shared.Utils
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import io.github.chsbuffer.revancedxposed.BaseHook
import io.github.chsbuffer.revancedxposed.setObjectField
import io.github.chsbuffer.revancedxposed.spotify.misc.ChangeLyricsProvider
import io.github.chsbuffer.revancedxposed.spotify.misc.FixThirdPartyLaunchersWidgets
import io.github.chsbuffer.revancedxposed.spotify.misc.HideCreateButton
import io.github.chsbuffer.revancedxposed.spotify.misc.SanitizeSharingLinks
import io.github.chsbuffer.revancedxposed.spotify.misc.UnlockPremium

@Suppress("UNCHECKED_CAST")
class SpotifyHook(app: Application, lpparam: LoadPackageParam) : BaseHook(app, lpparam) {
    override val hooks = arrayOf(
        ::Extension,
        ::SanitizeSharingLinks,
        ::UnlockPremium,
        ::HideCreateButton,
        ::FixThirdPartyLaunchersWidgets,
        ::ChangeLyricsProvider
    )

    fun Extension() {
        // Logger
        Utils.setContext(app)

        // load stubbed spotify classes
        injectClassLoader(this::class.java.classLoader!!, classLoader)
    }

    @SuppressLint("DiscouragedPrivateApi")
    fun injectClassLoader(self: ClassLoader, classLoader: ClassLoader) {
        val loader = self.parent
        val host = classLoader
        val bootClassLoader = Context::class.java.classLoader!!

        self.setObjectField("parent", object : ClassLoader(bootClassLoader) {
            override fun findClass(name: String?): Class<*> {
                try {
                    return bootClassLoader.loadClass(name)
                } catch (_: ClassNotFoundException) {
                }

                try {
                    return loader.loadClass(name)
                } catch (_: ClassNotFoundException) {
                }
                try {
                    return host.loadClass(name)
                } catch (_: ClassNotFoundException) {
                }

                throw ClassNotFoundException(name);
            }
        })
    }
}
