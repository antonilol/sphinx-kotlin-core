package chat.sphinx.concepts.network.query.lightning

import chat.sphinx.concepts.network.query.lightning.model.balance.BalanceAllDto
import chat.sphinx.concepts.network.query.lightning.model.balance.BalanceDto
import chat.sphinx.concepts.network.query.lightning.model.channel.ChannelsDto
import chat.sphinx.concepts.network.query.lightning.model.invoice.*
import chat.sphinx.concepts.network.query.lightning.model.route.RouteSuccessProbabilityDto
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.ResponseError
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.lightning.LightningNodePubKey
import chat.sphinx.wrapper.lightning.LightningRouteHint
import chat.sphinx.wrapper.relay.AuthorizationToken
import chat.sphinx.wrapper.relay.RelayUrl
import kotlinx.coroutines.flow.Flow

abstract class NetworkQueryLightning {

    ///////////
    /// GET ///
    ///////////
    abstract fun getInvoices(
        relayData: Pair<AuthorizationToken, RelayUrl>? = null
    ): Flow<LoadResponse<InvoicesDto, ResponseError>>

    abstract fun getChannels(
        relayData: Pair<AuthorizationToken, RelayUrl>? = null
    ): Flow<LoadResponse<ChannelsDto, ResponseError>>

    abstract fun getBalance(
        relayData: Pair<AuthorizationToken, RelayUrl>? = null
    ): Flow<LoadResponse<BalanceDto, ResponseError>>

    abstract fun getBalanceAll(
        relayData: Pair<AuthorizationToken, RelayUrl>? = null
    ): Flow<LoadResponse<BalanceAllDto, ResponseError>>

    abstract fun checkRoute(
        publicKey: LightningNodePubKey,
        relayData: Pair<AuthorizationToken, RelayUrl>? = null
    ): Flow<LoadResponse<RouteSuccessProbabilityDto, ResponseError>>

    abstract fun checkRoute(
        publicKey: LightningNodePubKey,
        routeHint: LightningRouteHint,
        relayData: Pair<AuthorizationToken, RelayUrl>? = null
    ): Flow<LoadResponse<RouteSuccessProbabilityDto, ResponseError>>

    abstract fun checkRoute(
        chatId: ChatId,
        relayData: Pair<AuthorizationToken, RelayUrl>? = null
    ): Flow<LoadResponse<RouteSuccessProbabilityDto, ResponseError>>

    abstract fun getLogs(
        relayData: Pair<AuthorizationToken, RelayUrl>? = null
    ): Flow<LoadResponse<String, ResponseError>>

    abstract fun postRequestPayment(
        postPaymentDto: PostRequestPaymentDto,
        relayData: Pair<AuthorizationToken, RelayUrl>? = null,
    ): Flow<LoadResponse<LightningPaymentInvoiceDto, ResponseError>>

    /**
     * Makes request to pay provided [LightningPaymentInvoiceDto]
     */
    abstract fun putLightningPaymentRequest(
        payRequestDto: PayRequestDto,
        relayData: Pair<AuthorizationToken, RelayUrl>? = null,
    ): Flow<LoadResponse<PaymentMessageDto, ResponseError>>

//    app.get('/getinfo', details.getInfo)
//    app.get('/logs', details.getLogsSince)
//    app.get('/info', details.getNodeInfo)
//    app.get('/route', details.checkRoute)
//    app.get('/query/onchain_address/:app', queries.queryOnchainAddress)
//    app.get('/utxos', queries.listUTXOs)

    ///////////
    /// PUT ///
    ///////////
//    app.put('/invoices', invoices.payInvoice)

    ////////////
    /// POST ///
    ////////////
//    app.post('/invoices/cancel', invoices.cancelInvoice)

    //////////////
    /// DELETE ///
    //////////////
}