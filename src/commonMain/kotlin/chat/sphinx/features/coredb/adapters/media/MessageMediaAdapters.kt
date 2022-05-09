package chat.sphinx.features.coredb.adapters.media

import chat.sphinx.wrapper.message.media.*
import com.squareup.sqldelight.ColumnAdapter

internal class MediaKeyAdapter: ColumnAdapter<MediaKey, String> {
    override fun decode(databaseValue: String): MediaKey {
        return MediaKey(databaseValue)
    }

    override fun encode(value: MediaKey): String {
        return value.value
    }
}

internal class MediaKeyDecryptedAdapter: ColumnAdapter<MediaKeyDecrypted, String> {
    override fun decode(databaseValue: String): MediaKeyDecrypted {
        return MediaKeyDecrypted(databaseValue)
    }

    override fun encode(value: MediaKeyDecrypted): String {
        return value.value
    }
}

internal class MediaTypeAdapter: ColumnAdapter<MediaType, String> {
    override fun decode(databaseValue: String): MediaType {
        return databaseValue.toMediaType()
    }

    override fun encode(value: MediaType): String {
        return value.value
    }
}

internal class MediaTokenAdapter: ColumnAdapter<MediaToken, String> {
    override fun decode(databaseValue: String): MediaToken {
        return MediaToken(databaseValue)
    }

    override fun encode(value: MediaToken): String {
        return value.value
    }
}
