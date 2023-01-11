package com.sebix.testinfuraapi.ui.pages.newwallet

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.sebix.testinfuraapi.testPassword
import com.sebix.testinfuraapi.utils.MnemonicSeedGenerator
import com.sebix.testinfuraapi.utils.SharedPreferencesHelper
import com.sebix.testinfuraapi.utils.SharedPreferencesKeys.fullWalletPathKey
import com.sebix.testinfuraapi.utils.SharedPreferencesKeys.walletFileKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import java.io.File
import java.security.Provider
import java.security.Security
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class NewWalletViewModel @Inject constructor(
    private val mnemonicSeedGenerator: MnemonicSeedGenerator,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ViewModel() {
    init {
        setupBouncyCastle()
    }

    var credentials: Credentials? = null
    val TAG = "NewWalletViewModel"

    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun generateMnemonicSeeds(): String {
        return mnemonicSeedGenerator.mnemonic
    }

    fun createNewWallet(context: Context, mnemonic: String) {
        ioScope.launch {
            try {
                val path = context.filesDir.path
                val walletFile =
                    WalletUtils.generateNewWalletFile(testPassword, File(path), false)
                Log.d(TAG, "walletFile $walletFile")
                Log.d(TAG, "mnemonic:  $mnemonic")
                val fullPath = "$path/$walletFile"
                Log.d(TAG, "walletFile full path $fullPath")
                credentials = WalletUtils.loadCredentials(testPassword, fullPath)
                sharedPreferencesHelper.saveString(walletFileKey, walletFile)
                sharedPreferencesHelper.saveString(fullWalletPathKey, fullPath)
                Log.d(TAG, "Wallet address : ${credentials?.address}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

    fun createNewWalletRunBlocking(context: Context, mnemonic: String): String {
        return runBlocking {
            try {
                val path = context.filesDir.path
                val walletFile =
                    WalletUtils.generateNewWalletFile(testPassword, File(path), false)
                Log.d(TAG, "walletFile $walletFile")
                Log.d(TAG, "mnemonic:  $mnemonic")
                val fullPath = "$path/$walletFile"
                Log.d(TAG, "walletFile full path $fullPath")
                credentials = WalletUtils.loadCredentials(testPassword, fullPath)
                sharedPreferencesHelper.saveString(walletFileKey, walletFile)
                sharedPreferencesHelper.saveString(fullWalletPathKey, fullPath)
                Log.d(TAG, "Wallet address : ${credentials?.address}")
                "SUCCESS"
            } catch (e: Exception) {
                e.printStackTrace()
                "ERROR"
            }
        }
    }
}