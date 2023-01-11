package com.sebix.testinfuraapi.ui.pages.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.sebix.testinfuraapi.testPassword
import com.sebix.testinfuraapi.utils.MnemonicSeedGenerator
import com.sebix.testinfuraapi.utils.SharedPreferencesHelper
import com.sebix.testinfuraapi.utils.SharedPreferencesKeys.fullWalletPathKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import org.web3j.tx.Transfer
import org.web3j.utils.Convert
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.security.Provider
import java.security.Security
import java.util.*
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ViewModel() {
    val apiKey = "https://goerli.infura.io/v3/"
    var credentials: Credentials? = null
    val TAG = "MainActivity"

    val ioScope = CoroutineScope(Dispatchers.IO)
    val mainScope = CoroutineScope(Dispatchers.Main)

    init {
        Log.d(TAG, "Viewmodel INIT")
        val walletPath = sharedPreferencesHelper.getString(fullWalletPathKey)
        Log.d(TAG, "Wallet path : $walletPath")
        credentials = WalletUtils.loadCredentials(testPassword, walletPath)
    }

    fun makeTransaction(context: Context) {
        ioScope.launch {
            getWalletBalance(context, apiKey)
            val web3j =
                Web3j.build(HttpService(apiKey))
            val nonce = web3j.ethGetTransactionCount(
                credentials?.address, DefaultBlockParameterName.LATEST
            ).send().transactionCount
            val gasPrice = web3j.ethGasPrice().send().gasPrice
            val gasLimit = BigInteger.valueOf(21000)
//            val value = BigInteger.valueOf(10000000)
            val value = 0.00001
            val receipt = Transfer.sendFunds(
                web3j,
                credentials,
                "0xb519F04c7483658eaA971CbC8ff177f69019f3ed",
                BigDecimal.valueOf(value),
                Convert.Unit.ETHER
            ).send()

            Log.d(TAG, "Transaction successful: " + receipt.transactionHash)
            Log.d(TAG, "nonce : $nonce")
            Log.d(TAG, "gasPrice : $gasPrice")
            Log.d(TAG, "gasLimit : $gasLimit")
            Log.d(TAG, "value : $value")
        }
    }

    fun getWalletBalance(context: Context, apiUrl: String) {
        ioScope.launch {
            try {
                val web3j =
                    Web3j.build(HttpService(apiKey))
                Log.d(TAG, "Wallet address : ${credentials?.address}")
                Log.d(TAG, "Api url : $apiKey")
                val balance =
                    web3j.ethGetBalance(
                        credentials?.address,
                        DefaultBlockParameter.valueOf("latest")
                    )
                        .send()
                val balanceInWei = balance.balance
                val balanceDecimal = balanceInWei.toDouble() * 0.1.pow(18.0)
                Log.d(TAG, "Wallet address : $balanceInWei")
                Log.d(TAG, "Wallet address : $balanceDecimal")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Storage Permissions variables
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    //permission method.
    fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have read or write permission
        val writePermission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val readPermission =
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    fun runBlockRequest(context: Context, apiUrl: String) {
        ioScope.launch {
            try {
                val web3j =
                    Web3j.build(HttpService(apiUrl))
                val blockNumber = web3j.ethBlockNumber().sendAsync().get().blockNumber
                mainScope.launch {
                    Toast.makeText(context, "Block number: $blockNumber", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mainScope.launch {
                    Toast.makeText(context, "An error has occurred.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun runGasRequest(context: Context, apiUrl: String) {
        ioScope.launch {
            try {
                val web3j =
                    Web3j.build(HttpService(apiUrl))
                val gas = web3j.ethGasPrice().sendAsync().get().gasPrice
                val gasDouble: Double = gas.toDouble() / 1000000000.0
                mainScope.launch {
                    Toast.makeText(context, "Gas: ${gasDouble}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mainScope.launch {
                    Toast.makeText(context, "An error has occurred.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getWalletCredentials(): Credentials {
        val walletPath = sharedPreferencesHelper.getString(fullWalletPathKey)
        Log.d(TAG, "Wallet path : $walletPath")
        return WalletUtils.loadCredentials(testPassword, walletPath)
    }
}