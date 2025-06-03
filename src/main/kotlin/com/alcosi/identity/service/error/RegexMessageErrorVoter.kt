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

import java.util.logging.Level
import java.util.logging.Logger

/**
 * Represents a message error voter that uses regular expressions to match
 * error messages.
 *
 * @param regexes The list of regular expressions to match error messages.
 * @param options The set of regular expression options to apply.
 */
open class RegexMessageErrorVoter(regexes: List<Regex>, options: Set<RegexOption> = setOf(RegexOption.IGNORE_CASE)) : MessageErrorVoter {
    constructor(regex: Regex, options: Set<RegexOption> = setOf(RegexOption.IGNORE_CASE)) : this(listOf(regex), options)

    /**
     * Represents a list of regular expressions with options.
     *
     * This variable is used to store a list of regular expressions with
     * options. If the options are empty, the variable stores the original list
     * of regular expressions. Otherwise, it maps each regular expression with
     * the provided options.
     *
     * @property regexesWithOpts The list of regular expressions with options.
     */
    protected open val regexesWithOpts = if (options.isEmpty()) regexes else regexes.map { Regex(it.pattern, setOf(RegexOption.IGNORE_CASE)) }

    /**
     * Represents the result of a null message in the voting process.
     *
     * The `nullResult` property is a boolean value that determines the
     * result of the vote when the message is null. If `nullResult` is true,
     * it means that a null message should be considered as a valid vote. If
     * `nullResult` is false, it means that a null message should be considered
     * as an invalid vote. The default value is false.
     *
     * This property is used in the `vote` function to determine the final vote
     * result based on the message and status code.
     *
     * @property nullResult The boolean value indicating the result of a null
     *    message in the voting process.
     */
    protected open val nullResult: Boolean = false

    /**
     * Determines whether a given error message should be voted.
     *
     * @param message The error message to be voted.
     * @param statusCode The status code associated with the error.
     * @return `true` if the error message should be voted, `false` otherwise.
     */
    override fun vote(message: String?, statusCode: Int): Boolean {
        if (message == null) {
            return nullResult
        }
        return regexesWithOpts.any {
            try {
                it.matches(message)
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Error parsing pattern! ${t.javaClass}:${t.message}. Message:${message}. Pattern:${it}")
                return@any false
            }
        }
    }

    companion object {
        protected val logger: Logger = Logger.getLogger(this.javaClass.name)
    }

}