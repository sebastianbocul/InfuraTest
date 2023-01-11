package com.sebix.testinfuraapi.utils

import org.web3j.crypto.MnemonicUtils
import java.util.*

class MnemonicSeedGenerator {
    val mnemonic = createMnemonicSeeds()

    private fun createMnemonicSeeds(): String {
        val initialEntropy = generateRandomEntropy()
        return MnemonicUtils.generateMnemonic(
            initialEntropy
        )
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