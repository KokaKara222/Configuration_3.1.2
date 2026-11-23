import java.io.File

private const val EXPECTED_PROGRAM_SIZE = 38

/** Проверяет промежуточное представление всех тестов спецификации. */
fun checkFields() {
    val program = parseProgram(File("examples/spec.json").readText())
    val expected = listOf(
        "A=28, B=67, C=763",
        "A=13, B=696, C=938, D=829",
        "A=11, B=831, C=827",
        "A=9, B=597, C=613, D=385"
    )
    check(program.map(::formatFields) == expected)
    val invalid = listOf(
        """[{"op":"LOAD","B":-1,"C":0}]""",
        """[{"op":"LOAD","B":134217728,"C":0}]""",
        """[{"op":"READ","B":0,"C":0,"D":16384}]""",
        """[{"op":"ADD","B":0,"C":0}]"""
    )
    invalid.forEach { text -> check(runCatching { parseProgram(text) }.isFailure) }
}

/** Проверяет все байтовые примеры и старшие биты длинной команды. */
fun checkMachineCode() {
    val program = parseProgram(File("examples/spec.json").readText())
    val expectedBytes = listOf(
        "0x7C, 0x08, 0x00, 0x00, 0xFB, 0x02, 0x00, 0x00",
        "0x0D, 0x57, 0x00, 0x00, 0x54, 0x07, 0x00, 0xA0, 0x67, 0x00",
        "0xEB, 0x67, 0x00, 0x00, 0x76, 0x06, 0x00, 0x00",
        "0xA9, 0x4A, 0x00, 0x00, 0xCA, 0x04, 0x00, 0x20, 0x30, 0x00, 0x00, 0x00"
    )
    check(program.map { formatBytes(encode(it)) } == expectedBytes)
    val maximal = parseProgram(
        """[{"op":"ADD","B":268435455,"C":268435455,"D":268435455}]"""
    ).single()
    val expected = byteArrayOf(
        0xE9.toByte(), -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0x01
    )
    check(encode(maximal).contentEquals(expected))
    check(assemble(program).size == EXPECTED_PROGRAM_SIZE)
}

/** Запускает проверки обоих этапов. */
fun main() {
    checkFields()
    checkMachineCode()
    println("Все тесты пройдены")
}
