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

/**
 * A Markdown file with YAML-like frontmatter persisted on an Okio [FileSystem].
 *
 * Metadata is stored as flat `key: value` pairs between `---` delimiters; the
 * Markdown prose lives in the body after the closing delimiter. Each [put] or
 * [updateBody] re-reads the file, applies the change in memory, and writes the
 * entire file back atomically — the same atomicity guarantee as [JsonFile][com.hadisatrio.libs.kotlin.json.JsonFile].
 *
 * @param fileSystem File system used for reading and writing.
 * @param path Path to the backing `.md` file.
 */
class FrontmatterFile(
    private val fileSystem: FileSystem,
    private val path: Path
) {

    val name: String get() = path.name

    /**
     * Writes [value] under [key] in the frontmatter, replacing any previous value.
     *
     * Keys are stripped of ASCII control characters (except tab) before being stored;
     * colons and newline characters are rejected outright.
     *
     * @param key Frontmatter key. Must not contain `:`, `\n`, or `\r`.
     * @param value Frontmatter value. Must not contain `\n` or `\r`.
     * @throws IllegalArgumentException if [key] contains `:`, `\n`, or `\r`, or if [value] contains `\n` or `\r`.
     */
    fun put(key: String, value: String) {
        require(!key.contains(':')) { "Key must not contain ':'" }
        require(!key.contains('\n') && !key.contains('\r')) { "Key must not contain newline characters" }
        require(!value.contains('\n') && !value.contains('\r')) { "Value must not contain newline characters" }
        val sanitizedKey = key.stripControlCharsExceptTab()
        val sanitizedValue = value.stripControlCharsExceptTab()
        val parsed = read()
        parsed.frontmatter[sanitizedKey] = sanitizedValue
        write(parsed)
    }

    /**
     * Returns the frontmatter value stored under [key], or `null` if the key is absent
     * or the file does not yet exist.
     *
     * @param key Key to look up.
     */
    fun get(key: String): String? {
        if (!fileSystem.exists(path)) return null
        return read().frontmatter[key]
    }

    /**
     * Returns the Markdown body — the content after the closing `---` delimiter.
     *
     * Returns an empty string if the file does not yet exist.
     */
    fun body(): String {
        if (!fileSystem.exists(path)) return ""
        return read().body
    }

    /**
     * Replaces the Markdown body with [content], preserving all frontmatter.
     *
     * ASCII control characters (except tab and newline) are stripped from [content]
     * before it is written; newlines are preserved so multi-paragraph descriptions
     * round-trip correctly.
     *
     * @param content New body text.
     */
    fun updateBody(content: String) {
        val sanitized = content.stripControlCharsExceptTabAndNewline()
        val parsed = read()
        write(ParsedFile(parsed.frontmatter, sanitized))
    }

    /**
     * Returns `true` if the backing file exists on the file system.
     */
    fun exists(): Boolean {
        return fileSystem.exists(path)
    }

    /**
     * Deletes the backing file. Does nothing if the file does not exist.
     */
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

        val lines = content.split('\n')
        val closingLineIndex = lines.drop(1).indexOfFirst { it == "---" }
        if (lines.isEmpty() || lines[0] != "---" || closingLineIndex < 0) {
            return ParsedFile(frontmatter, content)
        }
        val actualClosingIndex = closingLineIndex + 1

        val frontmatterLines = lines.subList(1, actualClosingIndex)
        val bodyLines = lines.drop(actualClosingIndex + 1)

        for (line in frontmatterLines) {
            val colonIndex = line.indexOf(':')
            if (colonIndex < 0) continue
            val k = line.substring(0, colonIndex).trim()
            val v = line.substring(colonIndex + 1).trim()
            frontmatter[k] = v
        }

        return ParsedFile(frontmatter, bodyLines.joinToString("\n"))
    }

    private data class ParsedFile(
        val frontmatter: LinkedHashMap<String, String>,
        val body: String
    )
}

private const val C0_LAST = 0x1F
private const val DEL = 0x7F

private fun String.stripControlCharsExceptTab(): String {
    return filter { c ->
        val code = c.code
        !(code in 0x00..C0_LAST && c != '\t') && code != DEL
    }
}

private fun String.stripControlCharsExceptTabAndNewline(): String {
    return filter { c ->
        val code = c.code
        !(code in 0x00..C0_LAST && c != '\t' && c != '\n') && code != DEL
    }
}
