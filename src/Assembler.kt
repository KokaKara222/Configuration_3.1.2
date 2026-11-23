import org.json.JSONArray
import org.json.JSONObject

/** Описание команды: код операции, размер и разрядность операндов. */
enum class Operation(val code: Long, val size: Int, val widths: List<Int>) {
    LOAD(28, 8, listOf(27, 28)),
    READ(13, 10, listOf(28, 28, 14)),
    WRITE(11, 8, listOf(28, 28)),
    ADD(9, 12, listOf(28, 28, 28))
}

/** Промежуточное представление одной команды. */
data class Instruction(val operation: Operation, val operands: List<Long>) {
    /** Возвращает поля A, B, C и, при наличии, D. */
    fun fields(): Map<String, Long> {
        val result = linkedMapOf("A" to operation.code)
        operands.forEachIndexed { index, value ->
            result[('B'.code + index).toChar().toString()] = value
        }
        return result
    }
}

/** Читает JSON-массив команд в промежуточное представление. */
fun parseProgram(text: String): List<Instruction> {
    val array = JSONArray(text)
    return (0 until array.length()).map { index ->
        parseInstruction(array.getJSONObject(index))
    }
}

/** Проверяет имя команды, набор полей и диапазоны операндов. */
fun parseInstruction(json: JSONObject): Instruction {
    val name = json.getString("op")
    val operation = Operation.entries.find { it.name == name }
        ?: error("Неизвестная команда: $name")
    val names = operation.widths.indices.map { index ->
        ('B'.code + index).toChar().toString()
    }
    require(json.keySet() == (names + "op").toSet()) {
        "Для $name нужны поля: op, ${names.joinToString()}"
    }
    val operands = names.mapIndexed { index, field ->
        val raw = json.get(field)
        require(raw is Number) { "$field должно быть целым числом" }
        val value =  raw.toString().toLongOrNull()
            ?: error("$field должно быть целым числом в диапазоне Long")
        val limit = 1L shl operation.widths[index]
        require(value in 0 until limit) { "$field вне диапазона 0..${limit - 1}" }
        value
    }
    return Instruction(operation, operands)
}

/** Форматирует поля как в спецификации УВМ. */
fun formatFields(instruction: Instruction): String =
    instruction.fields().entries.joinToString(", ") { "${it.key}=${it.value}" }
