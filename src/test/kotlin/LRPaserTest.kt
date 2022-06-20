import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import java.io.LineNumberReader
import java.io.StringReader
import kotlin.test.assertEquals

class LRParserTest {
    @ParameterizedTest
    @CsvFileSource(resources = ["data.csv"])
    fun test(input: String, expected: String) {
        val parser = Java_LRParserAnalysis.LRParser(LineNumberReader(StringReader(input)))
        val result = parser.parse().toString().trim()
        val error = parser.errorMessages.toString()
        assertEquals(
            expected.trim(), error + result
        )
    }
}
