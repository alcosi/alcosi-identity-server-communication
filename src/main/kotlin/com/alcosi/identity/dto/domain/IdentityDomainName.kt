package com.alcosi.identity.dto.domain

import com.alcosi.identity.dto.api.IdentityServerFullName

/**
 * Represents a person's identity domain name.
 *
 * @property firstName The first name of the person.
 * @property lastName The last name of the person.
 * @property middleName The middle name of the person.
 */
open class IdentityDomainName(
    val firstName: String?,
    val lastName: String?,
    val middleName: String?,
)

fun IdentityDomainName?.toApi() = this?.let { acc->IdentityServerFullName(acc.firstName, acc.lastName, acc.middleName)}

fun IdentityServerFullName?.toDomain() = IdentityDomainName(this?.firstName, this?.lastName, this?.middleName)
