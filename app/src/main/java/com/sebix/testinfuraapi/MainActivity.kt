package com.sebix.testinfuraapi

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.sebix.testinfuraapi.ui.theme.TestInfuraAPITheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import java.io.File
import java.security.Provider
import java.security.Security
import java.util.*


class MainActivity : ComponentActivity() {
    var credentials: Credentials? = null
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBouncyCastle()
        verifyStoragePermissions(this)
        setContent {
            TestInfuraAPITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        var text by rememberSaveable { mutableStateOf("") }
                        Row(Modifier.fillMaxWidth()) {
                            TextField(
                                value = text,
                                onValueChange = {
                                    text = it
                                },
                                label = {
                                    Text("Enter network endpoint eg. https://mainnet.infura.io/v3/xxxxxxxxxx")
                                }
                            )
                        }
                        Button(
                            onClick = { runBlockRequest(this@MainActivity, text) },
                            modifier = Modifier
                                .width(100.dp)
                                .height(100.dp)
                        ) {
                            Text(text = "Get block number")
                        }
                        Button(
                            onClick = { runGasRequest(this@MainActivity, text) },
                            modifier = Modifier
                                .width(100.dp)
                                .height(100.dp)
                        ) {
                            Text(text = "Get gas")
                        }
                        Button(
                            onClick = { createNewWallet(this@MainActivity) },
                            modifier = Modifier
                                .width(100.dp)
                                .height(100.dp)
                        ) {
                            Text(text = "Create new wallet")
                        }
                        Button(
                            onClick = { getWallet(this@MainActivity) },
                            modifier = Modifier
                                .width(100.dp)
                                .height(100.dp)
                        ) {
                            Text(text = "Get new wallet")
                        }
                    }
                }
            }
        }
    }

    private fun getWallet(context: Context) {
        try {
            Toast.makeText(context, "Wallet address : $credentials", Toast.LENGTH_SHORT).show()
            Log.d(TAG,"Wallet address : ${credentials?.address}")
        } catch (e: Exception) {
            e.printStackTrace()
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
    val ioScope = CoroutineScope(Dispatchers.IO)
    val mainScope = CoroutineScope(Dispatchers.Main)

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

    val password2 = "dupa1234"
    fun createNewWallet(context: Context) {
        ioScope.launch {
            try {
                val path = context.filesDir.path
                val credentialsDirectory = WalletUtils.getDefaultKeyDirectory()
                val initialEntropy = generateRandomEntropy()
                val mnemonic = MnemonicUtils.generateMnemonic(
                    initialEntropy
                )
                val walletFile = WalletUtils.generateNewWalletFile(password2, File(path), false)
                println("walletFile $walletFile")
                println("mnemonic:  $mnemonic")
                val fullpath = "$path/$walletFile"
                println("walletFile full path $fullpath")
                credentials = WalletUtils.loadCredentials(password2, fullpath)
                println("Wallet address : ${credentials?.address}")
                mainScope.launch {
                    Toast.makeText(
                        context,
                        "Wallet created $credentialsDirectory - $mnemonic",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mainScope.launch {
                    Toast.makeText(context, "An error has occurred.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun generateRandomEntropy(): ByteArray {
        val entropy = ByteArray(16)
        val random = Random()
        for (i in entropy.indices) {
            entropy[i] = random.nextInt(128).toByte()
        }
        return entropy
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TestInfuraAPITheme {
    }
}

