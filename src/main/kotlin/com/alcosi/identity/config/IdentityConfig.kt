package com.alcosi.identity.config

import com.alcosi.identity.service.api.*
import com.alcosi.identity.service.ids.IdentityGetProfileIdByTokenComponent
import com.alcosi.identity.service.ids.IdentityIntrospectTokenComponent
import com.alcosi.identity.service.ids.IdentityTokenComponent
import com.alcosi.identity.service.token.IdentityClientTokenHolder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.breninsul.rest.logging.RestTemplateLoggerConfiguration
import io.github.breninsul.synchronizationstarter.service.SynchronizationService
import io.github.breninsul.synchronizationstarter.service.local.LocalClearDecorator
import io.github.breninsul.synchronizationstarter.service.local.LocalSynchronizationService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.time.Duration

@AutoConfiguration
@ConditionalOnProperty(prefix = "identity-service", value = ["enabled"], havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(IdentityServerProperties::class)
open class IdentityConfig {
    //utils
    @Bean("identityServerRestClient")
    @ConditionalOnMissingBean(name = ["identityServerRestClient"])
    open fun identityServerRestClient(properties: IdentityServerProperties): RestClient {
        val simpleClientHttpRequestFactory = SimpleClientHttpRequestFactory()
        simpleClientHttpRequestFactory.setConnectTimeout(properties.connectTimeout.toMillis().toInt())
        simpleClientHttpRequestFactory.setReadTimeout(properties.readTimeout.toMillis().toInt())
        val factory = BufferingClientHttpRequestFactory(simpleClientHttpRequestFactory)
        return RestClient
            .builder()
            .requestFactory(factory)
            .requestInterceptor(RestTemplateLoggerConfiguration().registerRestTemplateLoggingInterceptor(properties.logging))
            .build()
    }

    @Bean("identityServerObjectMapper")
    @ConditionalOnMissingBean(name = ["identityServerObjectMapper"])
    fun identityServerObjectMapper(): ObjectMapper {
        val mapper = jacksonObjectMapper()
        mapper.findAndRegisterModules()
        return mapper
    }

    @Bean("identitySynchronizationService")
    @ConditionalOnMissingBean(name = ["identitySynchronizationService"])
    fun identitySynchronizationService(): SynchronizationService {
        val local = LocalSynchronizationService(Duration.ofSeconds(10))
        val cleared = LocalClearDecorator(Duration.ofHours(10), Duration.ofMinutes(10), Duration.ofSeconds(1), local)
        return cleared
    }
    //IDS
    @Bean("IdentityTokenComponent")
    @ConditionalOnMissingBean(IdentityTokenComponent::class)
    fun getIdentityTokenComponent(
        @Qualifier("identityServerObjectMapper") objectMapper: ObjectMapper,
        properties: IdentityServerProperties,
        @Qualifier("identityServerRestClient") restClient: RestClient,
    ): IdentityTokenComponent = IdentityTokenComponent.Implementation(objectMapper, properties, restClient)

    @Bean("IdentityGetProfileIdByTokenComponent")
    @ConditionalOnMissingBean(IdentityGetProfileIdByTokenComponent::class)
    fun getIdentityGetProfileIdByTokenComponent(
        @Qualifier("identityServerObjectMapper") objectMapper: ObjectMapper,
        properties: IdentityServerProperties,
        @Qualifier("identityServerRestClient") restClient: RestClient,
    ): IdentityGetProfileIdByTokenComponent = IdentityGetProfileIdByTokenComponent.Implementation(objectMapper, properties, restClient)

    @Bean("IdentityIntrospectTokenComponent")
    @ConditionalOnMissingBean(IdentityIntrospectTokenComponent::class)
    fun getIdentityIntrospectTokenComponent(
        @Qualifier("identityServerObjectMapper") objectMapper: ObjectMapper,
        properties: IdentityServerProperties,
        @Qualifier("identityServerRestClient") restClient: RestClient,
    ): IdentityIntrospectTokenComponent = IdentityIntrospectTokenComponent.Implementation(objectMapper, properties, restClient)

    //API
    @Bean("IdentityChangeContactComponent")
    @ConditionalOnMissingBean(IdentityChangeContactComponent::class)
    fun getIdentityChangeContactComponent(
        @Qualifier("identityServerObjectMapper") objectMapper: ObjectMapper,
        properties: IdentityServerProperties,
        @Qualifier("identityServerRestClient") restClient: RestClient,
    ): IdentityChangeContactComponent = IdentityChangeContactComponent.Implementation(properties, objectMapper, restClient)


    @Bean("IdentityClientTokenHolder")
    @ConditionalOnMissingBean(IdentityClientTokenHolder::class)
    fun getClientTokenHolder(
        tokenComponent: IdentityTokenComponent,
        @Qualifier("identityServerObjectMapper") objectMapper: ObjectMapper,
        properties: IdentityServerProperties,
        @Qualifier("identityServerRestClient") restClient: RestClient,
        @Qualifier("identitySynchronizationService") synchronizationService: SynchronizationService,
    ): IdentityClientTokenHolder =
        IdentityClientTokenHolder.Implementation(
            tokenComponent,
            synchronizationService,
            properties.ids.apiClient.scope
                .split(" "),
            properties.ids.apiClient.grantType,
            properties.ids.apiClient.id,
            properties.ids.apiClient.secret,
            Duration.ofSeconds(1),
            properties.serviceIp,
        )

    @Bean("IdentityActivationCodeComponent")
    @ConditionalOnMissingBean(IdentityActivationCodeComponent::class)
    fun getIdentityActivationCodeComponent(
        tokenHolder: IdentityClientTokenHolder,
        @Qualifier("identityServerObjectMapper") objectMapper: ObjectMapper,
        properties: IdentityServerProperties,
        @Qualifier("identityServerRestClient") restClient: RestClient,
    ): IdentityActivationCodeComponent = IdentityActivationCodeComponent.Implementation(tokenHolder, properties, objectMapper, restClient)

    @Bean("IdentityChangeComponent")
    @ConditionalOnMissingBean(IdentityChangeComponent::class)
    fun getIdentityChangeComponent(
        tokenHolder: IdentityClientTokenHolder,
        @Qualifier("identityServerObjectMapper") objectMapper: ObjectMapper,
        properties: IdentityServerProperties,
        @Qualifier("identityServerRestClient") restClient: RestClient,
    ): IdentityChangeComponent = IdentityChangeComponent.Implementation(tokenHolder, properties, objectMapper, restClient)

    @Bean("IdentityClaimsComponent")
    @ConditionalOnMissingBean(IdentityClaimsComponent::class)
    fun getIdentityClaimsComponent(
        tokenHolder: IdentityClientTokenHolder,
        @Qualifier("identityServerObjectMapper") objectMapper: ObjectMapper,
        properties: IdentityServerProperties,
        @Qualifier("identityServerRestClient") restClient: RestClient,
    ): IdentityClaimsComponent = IdentityClaimsComponent.Implementation(tokenHolder, properties, objectMapper, restClient)

    @Bean("IdentityDeleteComponent")
    @ConditionalOnMissingBean(IdentityDeleteComponent::class)
    fun getIdentityDeleteComponent(
        tokenHolder: IdentityClientTokenHolder,
        @Qualifier("identityServerObjectMapper") objectMapper: ObjectMapper,
        properties: IdentityServerProperties,
        @Qualifier("identityServerRestClient") restClient: RestClient,
    ): IdentityDeleteComponent = IdentityDeleteComponent.Implementation(tokenHolder, properties, objectMapper, restClient)

    @Bean("IdentityGetProfileComponent")
    @ConditionalOnMissingBean(IdentityGetProfileComponent::class)
    fun getIdentityGetProfileComponent(
        tokenHolder: IdentityClientTokenHolder,
        @Qualifier("identityServerObjectMapper") objectMapper: ObjectMapper,
        properties: IdentityServerProperties,
        @Qualifier("identityServerRestClient") restClient: RestClient,
    ): IdentityGetProfileComponent = IdentityGetProfileComponent.Implementation(tokenHolder, properties, objectMapper, restClient)

    @Bean("IdentityRegistrationComponent")
    @ConditionalOnMissingBean(IdentityRegistrationComponent::class)
    fun getIdentityRegistrationComponent(
        tokenHolder: IdentityClientTokenHolder,
        @Qualifier("identityServerObjectMapper") objectMapper: ObjectMapper,
        properties: IdentityServerProperties,
        @Qualifier("identityServerRestClient") restClient: RestClient,
    ): IdentityRegistrationComponent = IdentityRegistrationComponent.Implementation(tokenHolder, properties, objectMapper, restClient)

    @Bean("IdentityResetPasswordComponent")
    @ConditionalOnMissingBean(IdentityResetPasswordComponent::class)
    fun getIdentityResetPasswordComponent(
        tokenHolder: IdentityClientTokenHolder,
        @Qualifier("identityServerObjectMapper") objectMapper: ObjectMapper,
        properties: IdentityServerProperties,
        @Qualifier("identityServerRestClient") restClient: RestClient,
    ): IdentityResetPasswordComponent = IdentityResetPasswordComponent.Implementation(tokenHolder, properties, objectMapper, restClient)

    @Bean("IdentityRevokeTokenComponent")
    @ConditionalOnMissingBean(IdentityRevokeTokenComponent::class)
    fun getIdentityRevokeTokenComponent(
        tokenHolder: IdentityClientTokenHolder,
        @Qualifier("identityServerObjectMapper") objectMapper: ObjectMapper,
        properties: IdentityServerProperties,
        @Qualifier("identityServerRestClient") restClient: RestClient,
    ): IdentityRevokeTokenComponent = IdentityRevokeTokenComponent.Implementation(tokenHolder, properties, objectMapper, restClient)

    @Bean("IdentityTwoFactorComponent")
    @ConditionalOnMissingBean(IdentityTwoFactorComponent::class)
    fun getIdentityTwoFactorComponent(
        tokenHolder: IdentityClientTokenHolder,
        @Qualifier("identityServerObjectMapper") objectMapper: ObjectMapper,
        properties: IdentityServerProperties,
        @Qualifier("identityServerRestClient") restClient: RestClient,
    ): IdentityTwoFactorComponent = IdentityTwoFactorComponent.Implementation(tokenHolder, properties, objectMapper, restClient)
}
