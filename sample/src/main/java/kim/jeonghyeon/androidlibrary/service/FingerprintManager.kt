package kim.jeonghyeon.androidlibrary.service

import android.annotation.SuppressLint
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.extension.ctx
import kim.jeonghyeon.androidlibrary.extension.toast

object FingerprintManager {
    private var cancellationSignal: CancellationSignal? = null


    @SuppressLint("MissingPermission")
    fun detect(listener: OnFingerprintTouchListener) {
        cancel()
        cancellationSignal = CancellationSignal()
        val signal = cancellationSignal

        val fingerprintManager = FingerprintManagerCompat.from(ctx)


        fingerprintManager.authenticate(null, 0, signal, object : FingerprintManagerCompat.AuthenticationCallback() {

            override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
                super.onAuthenticationError(errMsgId, errString)
                if (errMsgId == 7) {
                    toast(R.string.toast_fingerprint_wrong)
                }
            }

            override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                listener.onFingerprintTouch()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                listener.onFingerprintTouch()
            }
        }, null)
    }

    fun cancel() {
        cancellationSignal
                ?.takeIf { !it.isCanceled }
                ?.cancel()

        cancellationSignal = null
    }

    interface OnFingerprintTouchListener {
        fun onFingerprintTouch()
    }
}