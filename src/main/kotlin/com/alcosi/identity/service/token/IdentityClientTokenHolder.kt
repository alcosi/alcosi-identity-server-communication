package com.alcosi.identity.service.token

import com.alcosi.identity.dto.domain.IdentityDomainToken
import com.alcosi.identity.service.ids.IdentityTokenComponent
import io.github.breninsul.javatimerscheduler.registry.SchedulerType
import io.github.breninsul.javatimerscheduler.registry.TaskSchedulerRegistry
import io.github.breninsul.synchronizationstarter.service.SynchronizationService
import io.github.breninsul.synchronizationstarter.service.sync
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicReference
import java.util.logging.Level
import java.util.logging.Logger

/**
 * This class represents a token holder for a client.
 *
 * @property tokenComponent The component used to retrieve tokens from the identity server.
 * @property synchronizationService The service used for synchronization.
 * @property scopes The scopes of the token.
 * @property grantType The grant type used to retrieve the token.
 * @property clientId The client ID.
 * @property clientSecret The client secret.
 * @property checkRefreshDuration The duration between token refresh checks.
 * @property serviceIp The IP address of the service.
 */
open class IdentityClientTokenHolder(
    protected open val tokenComponent: IdentityTokenComponent,
    protected open val synchronizationService: SynchronizationService,
    protected open val scopes: List<String>,
    protected open val grantType: String = "client_credentials",
    protected open val clientId: String,
    protected open val clientSecret: String,
    checkRefreshDuration: Duration = Duration.ofSeconds(1),
    protected open val serviceIp: String = "127.0.0.1"
) {
    /**
     * This property represents a logger instance for logging messages. It is
     * used for logging messages related to the current class.
     */
    protected open val logger: Logger = Logger.getLogger(this.javaClass.name)

    /**
     * Represents a token used for authentication in the system.
     *
     * @property token The reference to the domain token object.
     */
    protected open val token = AtomicReference(IdentityDomainToken("", null, LocalDateTime.MIN, 0, listOf()))
    //Schedule token refresh
    init {
        TaskSchedulerRegistry.registerTypeTask(SchedulerType.VIRTUAL_WAIT, "MachineTokenHolder", checkRefreshDuration, loggingLevel = Level.FINE, runnable = {
            val tk = token.get()
            val expireDelay = tk.expiresIn / 2
            val expireDate = tk.validTill.minusSeconds(expireDelay.toLong())
            val isCloseToExpire = expireDate.isBefore(LocalDateTime.now())
            if (isCloseToExpire) {
                updateToken(expireDate)
            }
        })
    }

    /**
     * Updates the domain token if the current token is not newer than the specified time.
     *
     * @param updateIfTokenIsNotNewerThen The time to compare with the current token's valid till date.
     * @return The updated domain token.
     */
    protected open fun updateToken(updateIfTokenIsNotNewerThen: LocalDateTime): IdentityDomainToken {
        val syncId = "MACHINE_TOKEN_HOLDER_UPDATE"
        return synchronizationService.sync(syncId) {
            val actualToken = token.get()
            if (actualToken.validTill.isAfter(updateIfTokenIsNotNewerThen)) {
                return@sync actualToken
            }
            val time = System.currentTimeMillis()
            val tokenFromServer = tokenComponent.getFromServer(clientId, clientSecret, scopes, grantType, ip = serviceIp, userAgent = null)
            val took = System.currentTimeMillis() - time
            token.set(tokenFromServer)
            logger.log(Level.INFO, "MachineTokenHolder token taking took ${took}ms")
            return@sync tokenFromServer
        }
    }

    /**
     * Retrieves the access token.
     *
     * This method returns the access token. If the current token is expired, it will call the `updateToken` method
     * to fetch a new token and return its access token. Otherwise, it will return the access token of the current token.
     *
     * @return The access token.
     */
    open fun getAccessToken(): String {
        val tk = token.get()
        val now = LocalDateTime.now()
        val isExpired = tk.validTill.isBefore(now)
        if (isExpired) {
            return updateToken(now).accessToken
        }
        return token.get().accessToken
    }
}
