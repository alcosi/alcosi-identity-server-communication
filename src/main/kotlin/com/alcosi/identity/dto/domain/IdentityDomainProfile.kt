package com.alcosi.identity.dto.domain

import com.alcosi.identity.dto.api.IdentityAccount

/**
 * This class represents an identity domain account.
 *
 * @property id The ID of the account.
 * @property name The name of the account.
 * @property email The email of the account.
 * @property phone The phone number of the account.
 * @property claims The list of claims associated with the account.
 * @property photoBase64 The base64 encoded photo of the account.
 */
open class IdentityDomainAccount(
    val id: String?,
    val name: IdentityDomainName?,
    val email: String?,
    val phone: String?,
    val claims: List<IdentityDomainClaim>,
    val photoBase64: String?,
)

fun IdentityDomainAccount?.toApi() = IdentityAccount(this?.id, this?.name.toApi(), this?.email, this?.phone, this?.claims?.map { it.toApi() } ?: listOf(), this?.photoBase64)

fun IdentityAccount?.toDomain() = IdentityDomainAccount(this?.id, this?.fullName.toDomain(), this?.email, this?.phoneNumber, this?.claims?.map { it.toDomain() } ?: listOf(), this?.photo)
