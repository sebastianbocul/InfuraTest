package com.sebix.testinfuraapi.ui.pages.newwallet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import com.sebix.testinfuraapi.testPassword
import com.sebix.testinfuraapi.utils.MnemonicSeedGenerator
import com.sebix.testinfuraapi.utils.SharedPreferencesHelper
import com.sebix.testinfuraapi.utils.SharedPreferencesKeys.fullWalletPathKey
import com.sebix.testinfuraapi.utils.SharedPreferencesKeys.walletFileNameKey
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

@HiltViewModel
class NewWalletViewModel @Inject constructor(
    private val mnemonicSeedGenerator: MnemonicSeedGenerator,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ViewModel() {

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
                sharedPreferencesHelper.saveString(walletFileNameKey, walletFile)
                sharedPreferencesHelper.saveString(fullWalletPathKey, fullPath)
                Log.d(TAG, "Wallet address : ${credentials?.address}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createNewWalletRunBlocking(context: Context, mnemonic: String): String {
        return runBlocking {
            try {
                val path = context.filesDir.path
                val walletFileName =
                    WalletUtils.generateNewWalletFile(testPassword, File(path), false)
                Log.d(TAG, "walletFile $walletFileName")
                Log.d(TAG, "mnemonic:  $mnemonic")
                val fullPath = "$path/$walletFileName"
                Log.d(TAG, "walletFile full path $fullPath")
                credentials = WalletUtils.loadCredentials(testPassword, fullPath)
                sharedPreferencesHelper.saveString(walletFileNameKey, walletFileName)
                sharedPreferencesHelper.saveString(fullWalletPathKey, fullPath)
                Log.d(TAG, "Wallet address : ${credentials?.address}")
                "SUCCESS"
            } catch (e: Exception) {
                e.printStackTrace()
                "ERROR"
            }
        }
    }

    fun copyToClipboard(context: Context, text: CharSequence) {
        val clipboard = getSystemService(context, ClipboardManager::class.java)
        val clip = ClipData.newPlainText("seeds", text)
        clipboard!!.setPrimaryClip(clip)
    }
}