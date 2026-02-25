/*
 * Copyright (C) 2022 Hadi Satrio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hadisatrio.libs.kotlin.frontmatter

import okio.FileSystem
import okio.Path
import okio.buffer
import okio.use

class FrontmatterFile(
    private val fileSystem: FileSystem,
    private val path: Path
) {

    val name: String get() = path.name

    fun put(key: String, value: String) {
        val parsed = read()
        parsed.frontmatter[key] = value
        write(parsed)
    }

    fun get(key: String): String? {
        if (!fileSystem.exists(path)) return null
        return read().frontmatter[key]
    }

    fun body(): String {
        if (!fileSystem.exists(path)) return ""
        return read().body
    }

    fun updateBody(content: String) {
        val parsed = read()
        write(ParsedFile(parsed.frontmatter, content))
    }

    fun exists(): Boolean {
        return fileSystem.exists(path)
    }

    fun delete() {
        fileSystem.delete(path = path, mustExist = false)
    }

    private fun read(): ParsedFile {
        if (!fileSystem.exists(path)) return ParsedFile(LinkedHashMap(), "")

        val content = fileSystem.source(path).buffer().use { it.readUtf8() }
        return parse(content)
    }

    private fun write(parsed: ParsedFile) {
        val sb = StringBuilder()
        sb.append("---\n")
        for ((k, v) in parsed.frontmatter) {
            sb.append(k)
            sb.append(": ")
            sb.append(v)
            sb.append("\n")
        }
        sb.append("---\n")
        sb.append(parsed.body)

        fileSystem.sink(path).buffer().use { it.writeUtf8(sb.toString()) }
    }

    private fun parse(content: String): ParsedFile {
        val frontmatter = LinkedHashMap<String, String>()
        var body = ""

        if (!content.startsWith("---\n")) {
            return ParsedFile(frontmatter, content)
        }

        val rest = content.removePrefix("---\n")
        val closingIndex = rest.indexOf("\n---\n")
        if (closingIndex < 0) {
            return ParsedFile(frontmatter, content)
        }

        val frontmatterBlock = rest.substring(0, closingIndex)
        body = rest.substring(closingIndex + "\n---\n".length)

        for (line in frontmatterBlock.lines()) {
            val colonIndex = line.indexOf(':')
            if (colonIndex < 0) continue
            val k = line.substring(0, colonIndex).trim()
            val v = line.substring(colonIndex + 1).trim()
            frontmatter[k] = v
        }

        return ParsedFile(frontmatter, body)
    }

    private data class ParsedFile(
        val frontmatter: LinkedHashMap<String, String>,
        val body: String
    )
}
