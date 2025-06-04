/*
 *
 *  * Copyright (c) 2025 Alcosi Group Ltd. and affiliates.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 */

package com.alcosi.identity.service.error

import com.alcosi.identity.exception.parser.api.*
import com.alcosi.identity.exception.parser.ids.*
import org.springframework.web.client.RestClient
import java.util.function.Supplier

interface IdentityErrorParser {
    /**
     * Process the exception related to the IDS.
     *
     * @param responseStatus The response status http code.
     * @param responseBodySupplier The response status http code.
     */
    fun processIdsException(responseStatus:Int ,responseBodySupplier : Supplier<String>)

    /**
     * Finds an exception based on the given REST client response.
     *
     * @param responseStatus The response status http code.
     * @param responseBodySupplier The response status http code.
     * @return The throwable exception found based on the response, or null if no exception is found.
     */
    fun findIdsException(responseStatus:Int ,responseBodySupplier : Supplier<String>): Throwable?

    /**
     * Processes the API exception based on the given REST client response.
     *
     * @param responseStatus The response status http code.
     * @param responseBodySupplier The response status http code.
     */
    fun processApiException(responseStatus:Int ,responseBodySupplier : Supplier<String>)

    /**
     * Finds an API exception based on the given REST client response.
     *
     * @param responseStatus The response status http code.
     * @param responseBodySupplier The response status http code.
     * @return The throwable exception found based on the response, or null if no exception is found.
     */
    fun findApiException(responseStatus:Int ,responseBodySupplier : Supplier<String>): Throwable?

    /**
     * Processes any exception that may occur during the execution of the REST client request.
     *
     * @param responseStatus The response status http code.
     * @param responseBodySupplier The response status http code.
     */
    fun processAnyException(responseStatus:Int ,responseBodySupplier : Supplier<String>)

    /**
     * Throws the throwable if it exists.
     *
     * @param throwable The throwable to be thrown.
     */
    fun Throwable?.throwIfExist()


    /**
     * IdentityErrorParser is a class that is responsible for parsing error responses from the Identity Server.
     * It provides methods to process specific types of exceptions and find exceptions based on the response received.
     *
     * ErrorVoter and ErrorSupplier are functional interfaces used by IdentityErrorParser to determine if an error response matches
     * a specific condition and create the corresponding exception, respectively.
     *
     * ErrorConfig is a helper class that holds an ErrorVoter and ErrorSupplier together.
     *
     * This class defines two lists: idsList and apiList, which store the ErrorConfigs for identification errors and API errors, respectively.
     *
     * The processIdsException method processes an identification error response by finding the corresponding exception and throwing it if it exists.
     *
     * The findIdsException method searches for the identification exception that matches the given response by iterating over the idsList and
     * checking if the ErrorVoter of each ErrorConfig votes for the response. It returns the first matching ErrorSupplier's created exception or null if no match is found.
     *
     * The processApiException method processes an API error response by finding the corresponding exception and throwing it if it exists.
     *
     * The findApiException method searches for the API exception that matches the given response by iterating over the apiList and
     * checking if the ErrorVoter of each ErrorConfig votes for the response. It returns the first matching ErrorSupplier's created exception or null if no match is found.
     *
     * The processAnyException method processes any type of error response by first trying to find an identification exception using
     * findIdsException and if not found, it searches for an API exception using findApiException. If an exception is found, it is thrown.
     *
     * The throwIfExist method is an extension function for Throwable that throws the exception if it is not null.
     *
     * Note: This class is open, allowing for subclassing and overriding of its methods and properties.
     * The class is not annotated with @author and @version tags as per the given instructions.
     */
    open class Implementation : IdentityErrorParser {
        /**
         * Represents the configuration for handling errors.
         *
         * @param voter The error voter used to decide whether an error should be handled.
         * @param errorSupplier The error supplier used to supply the error to be handled.
         */
        open class ErrorConfig(val voter: ErrorVoter, val errorSupplier: ErrorSupplier)

        /**
         * Represents the list of error configurations for handling errors related to Identity parsing.
         * Each ErrorConfig object consists of an error voter and an error supplier.
         */
        open val idsList = mutableListOf(
            ErrorConfig(
                RegexMessageErrorVoter(listOf(".*The Code must be at least.*".toRegex(), ".*invalid_grant.*Invalid code.*".toRegex(), ".*Invalid code.*invalid_grant.*".toRegex())),
                MessageErrorSupplier { msg, status -> IdentityInvalidTwoFaParserException(msg, status) }),
            ErrorConfig(RegexMessageErrorVoter(".*User is locked out.*".toRegex()), MessageErrorSupplier { msg, status -> IdentityLockedAccountParserException(msg, status) }),
            ErrorConfig(RegexMessageErrorVoter(".*Password not set.*".toRegex()), MessageErrorSupplier { msg, status -> IdentityNoPasswordParserException(msg, status) }),
            ErrorConfig(RegexMessageErrorVoter(listOf(".*Account hasn't been activated.*".toRegex(), ".*Account hasn\\\\u0027t been activated.*".toRegex())), MessageErrorSupplier { msg, status -> IdentityNotActivatedParserException(msg, status) }),
            ErrorConfig(RegexMessageErrorVoter(".*Invalid username or password.*".toRegex()), MessageErrorSupplier { msg, status -> IdentityInvalidCredentialsParserException(msg, status) }),
            ErrorConfig(RegexMessageErrorVoter(".*\\{\"error\"\\:\"invalid_grant\"\\}.*".toRegex()), MessageErrorSupplier { msg, status -> IdentityInvalidRefreshTokenParserException(msg, status) }),
            ErrorConfig(RegexMessageErrorVoter(".*Must be use 2FA code.*".toRegex()), MessageErrorSupplier { msg, status -> IdentityUseTwoFaParserException(msg, status) }),
            ErrorConfig(RegexMessageErrorVoter(".*Must use the code from the authenticator.*".toRegex()), MessageErrorSupplier { msg, status -> IdentityUseAuthentificatorParserException(msg, status) }),
        )

        /**
         * Represents a list of API error configurations.
         *
         * Each item in the list consists of an error voter and an error supplier.
         *
         * @property apiList The list of API error configurations.
         */
        open val apiList = mutableListOf(
            ErrorConfig(RegexMessageErrorVoter(".*Password_Validation_Failed.*".toRegex()), MessageErrorSupplier { msg, status -> IdentityPasswordIsNotStrongEnoughParserException(msg, status) }),
            ErrorConfig(RegexMessageErrorVoter(".*User_Not_Found.*".toRegex()), MessageErrorSupplier { msg, status -> IdentityProfileNotExistOnIdentityParserException(msg, status) }),
            ErrorConfig(RegexMessageErrorVoter(listOf( ".*Account_Already_Activated.*".toRegex())), MessageErrorSupplier { msg, status -> IdentityProfileIsAlreadyActivatedParserException(msg, status) }),
            ErrorConfig(RegexMessageErrorVoter(".*User_Activation_Fail: Invalid token.*".toRegex()), MessageErrorSupplier { msg, status -> IdentityInvalidActivationCodeParserException(msg, status) }),
            ErrorConfig(RegexMessageErrorVoter(".*Reset_Password_Fail: Invalid token.*".toRegex()), MessageErrorSupplier { msg, status -> IdentityInvalidResetPasswordCodeParserException(msg, status) }),
            ErrorConfig(RegexMessageErrorVoter(".*Update_Profile_Fail: Invalid token.*".toRegex()), MessageErrorSupplier { msg, status -> IdentityInvalidChangeContactsCodeParserException(msg, status) }),
            ErrorConfig(RegexMessageErrorVoter(".*Authenticator_Fail.*".toRegex()), MessageErrorSupplier { msg, status -> IdentityInvalidAuthentificatorCodeParserException(msg, status) }),
            ErrorConfig(RegexMessageErrorVoter(".*User_Already_Binded_Activated.*".toRegex()), MessageErrorSupplier { msg, status -> IdentityProfileIsAlreadyRegisteredAndActivatedParserException(msg, status) }),
            ErrorConfig(RegexMessageErrorVoter(".*User_Already_Binded_Not_Activated.*".toRegex()), MessageErrorSupplier { msg, status -> IdentityProfileIsAlreadyRegisteredButNotActivatedParserException(msg, status) }),
            ErrorConfig(RegexMessageErrorVoter(".*User_Already_Exists.*".toRegex()), MessageErrorSupplier { msg, status -> IdentityProfileIsAlreadyExistsParserException(msg, status) }),
            ErrorConfig(RegexMessageErrorVoter(".*Incorrect_Account_Request.*".toRegex()), MessageErrorSupplier { msg, status -> IdentityProfileIsIncorrectRequestParserException(msg, status) }),

            ErrorConfig(RegexMessageErrorVoter(listOf(".*User_Already_Binded:.*".toRegex(), ".*User_Already_Binded .*".toRegex(), ".*User_Already_Binded".toRegex())), MessageErrorSupplier { msg, status -> IdentityProfileIsAlreadyRegisteredParserException(msg, status) }),
            )

        /**
         * Process the exception related to the IDS.
         *
         * @param response The response object from the REST client.
         */
        override fun processIdsException(responseStatus:Int ,responseBodySupplier : Supplier<String>) {
            val exception = findIdsException(responseStatus,responseBodySupplier)
            exception.throwIfExist()
        }

        /**
         * Finds an exception based on the given REST client response.
         *
         * @param responseStatus The response status http code.
         * @param responseBodySupplier The response status http code.
         * @return The throwable exception found based on the response, or null if no exception is found.
         */
        override fun findIdsException(responseStatus:Int ,responseBodySupplier : Supplier<String>): Throwable? {
            val exception = idsList.firstOrNull() { it.voter.vote(responseStatus,responseBodySupplier) }?.errorSupplier?.create(responseStatus,responseBodySupplier)
            return exception
        }

        /**
         * Processes the API exception based on the given REST client response.
         *
         * @param responseStatus The response status http code.
         * @param responseBodySupplier The response status http code.
         */
        override fun processApiException(responseStatus:Int ,responseBodySupplier : Supplier<String>) {
            val exception = findApiException(responseStatus,responseBodySupplier)
            exception.throwIfExist()
        }

        /**
         * Finds an API exception based on the given REST client response.
         *
         * @param responseStatus The response status http code.
         * @param responseBodySupplier The response status http code.
         * @return The throwable exception found based on the response, or null if no exception is found.
         */
        override fun findApiException(responseStatus:Int ,responseBodySupplier : Supplier<String>): Throwable? {
            val exception = apiList.firstOrNull() { it.voter.vote(responseStatus,responseBodySupplier) }?.errorSupplier?.create(responseStatus,responseBodySupplier)
            return exception
        }

        /**
         * Processes any exception that may occur during the execution of the REST client request.
         *
         * @param responseStatus The response status http code.
         * @param responseBodySupplier The response status http code.
         */
        override fun processAnyException(responseStatus:Int ,responseBodySupplier : Supplier<String>) {
            val exception = findIdsException(responseStatus,responseBodySupplier) ?: findApiException(responseStatus ,responseBodySupplier)
            exception.throwIfExist()
        }

        /**
         * Throws the throwable if it exists.
         *
         * @param throwable The throwable to be thrown.
         */
        override fun Throwable?.throwIfExist() {
            if (this != null) {
                throw this
            }
        }
    }
}
/**
 * IdentityErrorParserHolder is a singleton class that holds an instance of IdentityErrorParser. It provides access to the error parser instance.
 *
 * The errorParser property is a mutable variable that holds an instance of IdentityErrorParser, which is responsible for parsing error responses from the Identity Server.
 *
 * Example usage:
 *
 * ```
 * IdentityErrorParserHolder.errorParser.processAnyException(response)
 * ```
 *
 * Note: This class is not annotated with @author and @version tags as per the given instructions.
 */
object IdentityErrorParserHolder  {
    var errorParser: IdentityErrorParser = IdentityErrorParser.Implementation()
}

/**
 * Parses the response from the REST client and throws any possible exception that may occur during the execution of the REST client request.
 */
fun RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse.parseAndThrowPossibleException() {
    IdentityErrorParserHolder.errorParser.processAnyException(this.statusCode.value()) { this.bodyTo(String::class.java) ?: "" }
}

/**
 * Parses the response from the REST client and throws any possible exception that may occur during the execution of the REST client request.
 *
 * @param exchangeFunction The function that will be called to perform the exchange operation.
 * @return The result of the exchange operation.
 */
fun <T> RestClient.RequestHeadersSpec<*>.parseExceptionAndExchange(exchangeFunction: RestClient.RequestHeadersSpec.ExchangeFunction<T>): T {
   return this.exchange { clientRequest, clientResponse ->
        clientResponse.parseAndThrowPossibleException()
        exchangeFunction.exchange(clientRequest, clientResponse)
    }
}