package chat.sphinx.features.coredb.adapters.contact

import chat.sphinx.wrapper.contact.*
import chat.sphinx.wrapper.lightning.LightningNodeAlias
import chat.sphinx.wrapper.lightning.LightningRouteHint
import chat.sphinx.wrapper.rsa.RsaPublicKey
import com.squareup.sqldelight.ColumnAdapter

internal class ContactAliasAdapter: ColumnAdapter<ContactAlias, String> {
    override fun decode(databaseValue: String): ContactAlias {
        return ContactAlias(databaseValue)
    }

    override fun encode(value: ContactAlias): String {
        return value.value
    }
}

internal class ContactOwnerAdapter: ColumnAdapter<Owner, Long> {
    override fun decode(databaseValue: Long): Owner {
        return databaseValue.toInt().toOwner()
    }

    override fun encode(value: Owner): Long {
        return value.value.toLong()
    }
}

internal class ContactPublicKeyAdapter: ColumnAdapter<RsaPublicKey, String> {
    override fun decode(databaseValue: String): RsaPublicKey {
        return RsaPublicKey(databaseValue.toCharArray())
    }

    override fun encode(value: RsaPublicKey): String {
        return value.value.joinToString("")
    }
}

internal class ContactStatusAdapter: ColumnAdapter<ContactStatus, Long> {

    companion object {
        const val NULL = Long.MIN_VALUE
    }

    override fun decode(databaseValue: Long): ContactStatus {
        return if (databaseValue == NULL) {
            ContactStatus.AccountOwner
        } else {
            databaseValue.toInt().toContactStatus()
        }
    }

    override fun encode(value: ContactStatus): Long {
        return value.value?.toLong() ?: NULL
    }
}

internal class DeviceIdAdapter: ColumnAdapter<DeviceId, String> {
    override fun decode(databaseValue: String): DeviceId {
        return DeviceId(databaseValue)
    }

    override fun encode(value: DeviceId): String {
        return value.value
    }
}

internal class LightningRouteHintAdapter: ColumnAdapter<LightningRouteHint, String> {
    override fun decode(databaseValue: String): LightningRouteHint {
        return LightningRouteHint(databaseValue)
    }

    override fun encode(value: LightningRouteHint): String {
        return value.value
    }
}

internal class LightningNodeAliasAdapter: ColumnAdapter<LightningNodeAlias, String> {
    override fun decode(databaseValue: String): LightningNodeAlias {
        return LightningNodeAlias(databaseValue)
    }

    override fun encode(value: LightningNodeAlias): String {
        return value.value
    }
}

internal class NotificationSoundAdapter: ColumnAdapter<NotificationSound, String> {
    override fun decode(databaseValue: String): NotificationSound {
        return databaseValue.toNotificationSound()
    }

    override fun encode(value: NotificationSound): String {
        return value.value
    }
}

internal class PrivatePhotoAdapter: ColumnAdapter<PrivatePhoto, Long> {
    override fun decode(databaseValue: Long): PrivatePhoto {
        return databaseValue.toInt().toPrivatePhoto()
    }

    override fun encode(value: PrivatePhoto): Long {
        return value.value.toLong()
    }
}

internal class BlockedAdapter private constructor(): ColumnAdapter<Blocked, Long> {

    companion object {
        @Volatile
        private var instance: BlockedAdapter? = null
        fun getInstance(): BlockedAdapter =
            instance ?: synchronized(this) {
                instance ?: BlockedAdapter()
                    .also { instance = it }
            }
    }

    override fun decode(databaseValue: Long): Blocked {
        return databaseValue.toInt().toBlocked()
    }

    override fun encode(value: Blocked): Long {
        return value.value.toLong()
    }
}
