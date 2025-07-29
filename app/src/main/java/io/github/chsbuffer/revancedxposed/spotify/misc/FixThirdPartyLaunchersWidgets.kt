package io.github.chsbuffer.revancedxposed.spotify.misc

import de.robv.android.xposed.XC_MethodReplacement
import io.github.chsbuffer.revancedxposed.Opcode
import io.github.chsbuffer.revancedxposed.fingerprint
import io.github.chsbuffer.revancedxposed.spotify.SpotifyHook

fun SpotifyHook.FixThirdPartyLaunchersWidgets() {
    getDexMethod("canBindAppWidgetPermissionFingerprint"){
        fingerprint {
            strings("android.permission.BIND_APPWIDGET")
            opcodes(Opcode.AND_INT_LIT8)
        }
    }.hookMethod(XC_MethodReplacement.returnConstant(true))
}
