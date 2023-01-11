package com.sebix.testinfuraapi.ui.pages.start

import androidx.lifecycle.ViewModel
import com.sebix.testinfuraapi.testPassword
import com.sebix.testinfuraapi.utils.SharedPreferencesHelper
import com.sebix.testinfuraapi.utils.SharedPreferencesKeys
import com.sebix.testinfuraapi.utils.SharedPreferencesKeys.walletFileNameKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.CipherException
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import java.security.Provider
import java.security.Security
import javax.inject.Inject

@HiltViewModel
class StartPageViewModel @Inject constructor(
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ViewModel() {
    init {
        setupBouncyCastle()
    }

    var credentials: Credentials? = null
    val TAG = "StartPageViewModel"

    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun getWalletName(): String? {
        return sharedPreferencesHelper.getString(walletFileNameKey)
    }

    fun validateWalletPassword(password: String): Boolean {
        return try {
            val walletPath =
                sharedPreferencesHelper.getString(SharedPreferencesKeys.fullWalletPathKey)
            WalletUtils.loadCredentials(testPassword, walletPath)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun setupBouncyCastle() {
        val provider: Provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
            ?: // Web3j will set up the provider lazily when it's first used.
            return
        if (provider.javaClass == BouncyCastleProvider::class.java) {
            // BC with same package name, shouldn't happen in real life.
            return
        }
        // Android registers its own BC provider. As it might be outdated and might not include
        // all needed ciphers, we substitute it with a known BC bundled in the app.
        // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
        // of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)
    }
}