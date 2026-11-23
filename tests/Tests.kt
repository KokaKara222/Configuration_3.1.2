import java.io.File

/** Проверяет промежуточное представление всех тестов спецификации. */
fun main() {
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
    println("Все тесты пройдены")
}
