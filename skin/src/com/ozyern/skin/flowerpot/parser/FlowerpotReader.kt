/*
 *     This file is part of Skin Launcher.
 *
 *     Skin Launcher is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Skin Launcher is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Skin Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.ozyern.skin.flowerpot.parser

import com.ozyern.skin.flowerpot.Flowerpot
import com.ozyern.skin.flowerpot.FlowerpotFormatException
import com.ozyern.skin.flowerpot.rules.Rules
import com.ozyern.skin.util.listWhileNotNull
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class FlowerpotReader(inputStream: InputStream) : BufferedReader(InputStreamReader(inputStream)) {
    private var version: Int? = null

    /**
     * Read the next rule from the stream
     * @return the parsed rule or null if the end of the file has been reached
     */
    fun readRule(): Rules? {
        val line = readLine() ?: return null
        val filter = LineParser.parse(line, version)
        if (filter is Rules.Version) {
            if (version != null) {
                throw FlowerpotFormatException("Version declaration can only appear once")
            }
            if (!Flowerpot.SUPPORTED_VERSIONS.contains(filter.version)) {
                throw FlowerpotFormatException("Unsupported version ${filter.version} (supported are ${Flowerpot.SUPPORTED_VERSIONS.joinToString()})")
            }
            version = filter.version
        }
        return filter
    }

    /**
     * Read all rules contained in the file with None and Version rules already filtered out
     */
    fun readRules(): List<Rules> = listWhileNotNull { readRule() }.filterNot { it is Rules.None || it is Rules.Version }
}
