package chat.sphinx.concepts.network.client.crypto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CryptoSchemeUnitTest {

    @Test
    fun `ensure all CryptoSchemes are accounted for`() {

        fun getScheme(): CryptoScheme =
            CryptoScheme.Decrypt.JNCryptor

        // Will throw a build error. needed as the when statements contain `else`
        // because of the compiler, so we are ensuring that if a new scheme
        // has been added, that it is handled properly here too.
        val scheme: CryptoScheme = when (val scheme = getScheme()) {
            CryptoScheme.Decrypt.JNCryptor -> {
                scheme
            }
            CryptoScheme.Encrypt.JNCryptor -> {
                scheme
            }
        }
    }

    @Test
    fun `retrieveCryptoScheme returns correct scheme or null`() {
        val expectedEncrypt = CryptoScheme.Encrypt.JNCryptor

        val headerEnc = CryptoHeader.Encrypt.Builder()
            .setScheme(expectedEncrypt)
            .setPassword("sdlkfj")
            .build()

        assertEquals(expectedEncrypt, headerEnc.key.retrieveCryptoScheme())

        val expectedDecrypt = CryptoScheme.Decrypt.JNCryptor

        val headerDec = CryptoHeader.Decrypt.Builder()
            .setScheme(expectedDecrypt)
            .setPassword("sdlkfj")
            .build()

        assertEquals(expectedDecrypt, headerDec.key.retrieveCryptoScheme())

        assertNull("NON-CRYPTO-HEADER".retrieveCryptoScheme())
    }
}
