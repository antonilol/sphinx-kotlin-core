package chat.sphinx.concepts.network.query.contact

import chat.sphinx.concepts.network.query.contact.model.*
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.response.ResponseError
import chat.sphinx.wrapper.DateTime
import chat.sphinx.wrapper.contact.Blocked
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.dashboard.ContactId
import chat.sphinx.wrapper.relay.AuthorizationToken
import chat.sphinx.wrapper.relay.RelayUrl
import kotlinx.coroutines.flow.Flow

abstract class NetworkQueryContact {

    ///////////
    /// GET ///
    ///////////
    abstract fun getContacts(
        relayData: Pair<AuthorizationToken, RelayUrl>? = null
    ): Flow<LoadResponse<GetContactsResponse, ResponseError>>

    abstract fun getLatestContacts(
        date: DateTime?,
        relayData: Pair<AuthorizationToken, RelayUrl>? = null
    ): Flow<LoadResponse<GetLatestContactsResponse, ResponseError>>

    abstract fun getTribeMembers(
        chatId: ChatId,
        offset: Int = 0,
        limit: Int = 50,
        relayData: Pair<AuthorizationToken, RelayUrl>? = null
    ): Flow<LoadResponse<GetTribeMembersResponse, ResponseError>>

    ///////////
    /// PUT ///
    ///////////
    abstract fun updateContact(
        contactId: ContactId,
        putContactDto: PutContactDto,
        relayData: Pair<AuthorizationToken, RelayUrl>? = null
    ): Flow<LoadResponse<ContactDto, ResponseError>>

    abstract fun toggleBlockedContact(
        contactId: ContactId,
        blocked: Blocked,
        relayData: Pair<AuthorizationToken, RelayUrl>? = null
    ): Flow<LoadResponse<ContactDto, ResponseError>>

    ////////////
    /// POST ///
    ////////////
    abstract fun generateToken(
        relayUrl: RelayUrl,
        token: AuthorizationToken,
        password: String?,
        pubkey: String? = null
    ): Flow<LoadResponse<GenerateTokenResponse, ResponseError>>

    abstract fun createContact(
        postContactDto: PostContactDto,
        relayData: Pair<AuthorizationToken, RelayUrl>? = null
    ): Flow<LoadResponse<ContactDto, ResponseError>>

    //////////////
    /// DELETE ///
    //////////////
    abstract suspend fun deleteContact(
        contactId: ContactId,
        relayData: Pair<AuthorizationToken, RelayUrl>? = null,
    ): Response<Any, ResponseError>

    abstract fun createNewInvite(
        nickname: String,
        welcomeMessage: String,
        relayData: Pair<AuthorizationToken, RelayUrl>? = null
    ): Flow<LoadResponse<ContactDto, ResponseError>>

    //    app.post('/contacts/:id/keys', contacts.exchangeKeys)
    //    app.post('/contacts', contacts.createContact)
}
