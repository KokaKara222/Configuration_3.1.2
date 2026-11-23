import java.io.File
import kotlin.system.exitProcess

private const val REQUIRED_ARGS = 2
private const val MAX_ARGS = 3

/** Принимает пути входа, выхода и необязательный флаг --test. */
fun main(args: Array<String>) {
    try {
        require(args.size in REQUIRED_ARGS..MAX_ARGS) {
            "Запуск: bash run.sh input.json output.bin [--test]"
        }
        require(args.size == REQUIRED_ARGS || args.last() == "--test") {
            "Неизвестный флаг: ${args.last()}"
        }
        val program = parseProgram(File(args[0]).readText())
        if (args.last() == "--test") {
            program.forEach { println(formatFields(it)) }
        }
    } catch (error: Exception) {
        System.err.println("Ошибка: ${error.message}")
        exitProcess(1)
    }
}
