import java.math.BigInteger

private const val OPCODE_BITS = 5
private const val BYTE_BITS = 8
private const val BYTE_MASK = 0xFF

/** Упаковывает поля команды в байты от младшего к старшему. */
fun encode(instruction: Instruction): ByteArray {
    var value = BigInteger.valueOf(instruction.operation.code)
    var shift = OPCODE_BITS
    instruction.operands.forEachIndexed { index, operand ->
        value = value.or(BigInteger.valueOf(operand).shiftLeft(shift))
        shift += instruction.operation.widths[index]
    }
    return ByteArray(instruction.operation.size) { index ->
        value.shiftRight(index * BYTE_BITS).toByte()
    }
}

/** Объединяет машинные коды всех команд. */
fun assemble(program: List<Instruction>): ByteArray =
    program.flatMap { encode(it).toList() }.toByteArray()

/** Выводит байты в шестнадцатеричном формате спецификации. */
fun formatBytes(bytes: ByteArray): String =
    bytes.joinToString(", ") { "0x%02X".format(it.toInt() and BYTE_MASK) }
