package io.kotest.tables

import com.univocity.parsers.common.record.Record
import com.univocity.parsers.csv.CsvFormat
import com.univocity.parsers.csv.CsvParser
import com.univocity.parsers.csv.CsvParserSettings
import java.io.InputStream

class CsvDataSource(val input: InputStream, val format: CsvFormat) {

  fun createParser(format: CsvFormat,
                   readHeaders: Boolean,
                   ignoreLeadingWhitespaces: Boolean = true,
                   ignoreTrailingWhitespaces: Boolean = true,
                   skipEmptyLines: Boolean = true,
                   emptyCellValue: String? = null,
                   nullValue: String? = null,
                   skipRows: Long? = null): CsvParser {
    val settings = CsvParserSettings()
    settings.format.delimiter = format.delimiter
    settings.format.quote = format.quote
    settings.format.quoteEscape = format.quoteEscape
    settings.isLineSeparatorDetectionEnabled = true
    settings.isHeaderExtractionEnabled = readHeaders
    settings.ignoreLeadingWhitespaces = ignoreLeadingWhitespaces
    settings.ignoreTrailingWhitespaces = ignoreTrailingWhitespaces
    settings.skipEmptyLines = skipEmptyLines
    settings.isCommentCollectionEnabled = true
    settings.emptyValue = emptyCellValue
    settings.nullValue = nullValue
    settings.maxCharsPerColumn = -1
    settings.maxColumns = 2048
    settings.readInputOnSeparateThread = false
    settings.numberOfRowsToSkip = skipRows ?: 0
    return CsvParser(settings)
  }

  fun <A> createTable(parseRow: (Record) -> Row1<A>, parseHeaders: (Array<String>) -> Headers1): Table1<A> {
    val parser = createParser(format, readHeaders = true)
    parser.beginParsing(input)
    var headers: Headers1? = null
    val rows = generateSequence { parser.parseNextRecord() }.map {
      if (headers == null)
        headers = parseHeaders(it.metaData.headers())
      parseRow(it)
    }.toList()
    parser.stopParsing()
    return Table1(headers!!, rows)
  }

  fun <A> createTable(parseRow: (Record) -> Row1<A>): Table1<A> {
    val parser = createParser(format, readHeaders = false)
    parser.beginParsing(input)
    val rows = generateSequence { parser.parseNextRecord() }.map { parseRow(it) }.toList()
    parser.stopParsing()
    return Table1(Headers1("A"), rows)
  }

  fun <A, B> createTable(parseRow: (Record) -> Row2<A, B>, parseHeaders: (Array<String>) -> Headers2): Table2<A, B> {
    val parser = createParser(format, readHeaders = true)
    parser.beginParsing(input)
    var headers: Headers2? = null
    val rows = generateSequence { parser.parseNextRecord() }.map {
      if (headers == null)
        headers = parseHeaders(it.metaData.headers())
      parseRow(it)
    }.toList()
    parser.stopParsing()
    return Table2(headers!!, rows)
  }

  fun <A, B> createTable(parseRow: (Record) -> Row2<A, B>): Table2<A, B> {
    val parser = createParser(format, readHeaders = false)
    parser.beginParsing(input)
    val rows = generateSequence { parser.parseNextRecord() }.map { parseRow(it) }.toList()
    parser.stopParsing()
    return Table2(Headers2("A", "B"), rows)
  }

  fun <A, B, C> createTable(parseRow: (Record) -> Row3<A, B, C>, parseHeaders: (Array<String>) -> Headers3): Table3<A, B, C> {
    val parser = createParser(format, readHeaders = true)
    parser.beginParsing(input)
    var headers: Headers3? = null
    val rows = generateSequence { parser.parseNextRecord() }.map {
      if (headers == null)
        headers = parseHeaders(it.metaData.headers())
      parseRow(it)
    }.toList()
    parser.stopParsing()
    return Table3(headers!!, rows)
  }

  fun <A, B, C> createTable(parseRow: (Record) -> Row3<A, B, C>): Table3<A, B, C> {
    val parser = createParser(format, readHeaders = false)
    parser.beginParsing(input)
    val rows = generateSequence { parser.parseNextRecord() }.map { parseRow(it) }.toList()
    parser.stopParsing()
    return Table3(Headers3("A", "B", "C"), rows)
  }

  fun <A, B, C, D> createTable(parseRow: (Record) -> Row4<A, B, C, D>, parseHeaders: (Array<String>) -> Headers4): Table4<A, B, C, D> {
    val parser = createParser(format, readHeaders = true)
    parser.beginParsing(input)
    var headers: Headers4? = null
    val rows = generateSequence { parser.parseNextRecord() }.map {
      if (headers == null)
        headers = parseHeaders(it.metaData.headers())
      parseRow(it)
    }.toList()
    parser.stopParsing()
    return Table4(headers!!, rows)
  }

  fun <A, B, C, D> createTable(parseRow: (Record) -> Row4<A, B, C, D>): Table4<A, B, C, D> {
    val parser = createParser(format, readHeaders = false)
    parser.beginParsing(input)
    val rows = generateSequence { parser.parseNextRecord() }.map { parseRow(it) }.toList()
    return Table4(Headers4("A", "B", "C", "D"), rows)
  }

  fun <A, B, C, D, E> createTable(parseRow: (Record) -> Row5<A, B, C, D, E>, parseHeaders: (Array<String>) -> Headers5): Table5<A, B, C, D, E> {
    val parser = createParser(format, readHeaders = true)
    parser.beginParsing(input)
    var headers: Headers5? = null
    val rows = generateSequence { parser.parseNextRecord() }.map {
      if (headers == null)
        headers = parseHeaders(it.metaData.headers())
      parseRow(it)
    }.toList()
    parser.stopParsing()
    return Table5(headers!!, rows)
  }

  fun <A, B, C, D, E> createTable(parseRow: (Record) -> Row5<A, B, C, D, E>): Table5<A, B, C, D, E> {
    val parser = createParser(format, readHeaders = false)
    parser.beginParsing(input)
    val rows = generateSequence { parser.parseNextRecord() }.map { parseRow(it) }.toList()
    parser.stopParsing()
    return Table5(Headers5("A", "B", "C", "D", "E"), rows)
  }

  fun <A, B, C, D, E, F> createTable(parseRow: (Record) -> Row6<A, B, C, D, E, F>, parseHeaders: (Array<String>) -> Headers6): Table6<A, B, C, D, E, F> {
    val parser = createParser(format, readHeaders = true)
    parser.beginParsing(input)
    var headers: Headers6? = null
    val rows = generateSequence { parser.parseNextRecord() }.map {
      if (headers == null)
        headers = parseHeaders(it.metaData.headers())
      parseRow(it)
    }.toList()
    parser.stopParsing()
    return Table6(headers!!, rows)
  }

  fun <A, B, C, D, E, F> createTable(parseRow: (Record) -> Row6<A, B, C, D, E, F>): Table6<A, B, C, D, E, F> {
    val parser = createParser(format, readHeaders = false)
    parser.beginParsing(input)
    val rows = generateSequence { parser.parseNextRecord() }.map { parseRow(it) }.toList()
    parser.stopParsing()
    return Table6(Headers6("A", "B", "C", "D", "E", "F"), rows)
  }
}