package br.ufpe.cin.if710.calculadora

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var calc_display = text_calc.text

        /*
            Setando listeners para cada botão,
            o EditText text_calc é atualizado com o símbolo
            que foi clicado
        */
        btn_0.setOnClickListener { calc_display.append(btn_0.text) }
        btn_1.setOnClickListener { calc_display.append(btn_1.text) }
        btn_2.setOnClickListener { calc_display.append(btn_2.text) }
        btn_3.setOnClickListener { calc_display.append(btn_3.text) }
        btn_4.setOnClickListener { calc_display.append(btn_4.text) }
        btn_5.setOnClickListener { calc_display.append(btn_5.text) }
        btn_6.setOnClickListener { calc_display.append(btn_6.text) }
        btn_7.setOnClickListener { calc_display.append(btn_7.text) }
        btn_8.setOnClickListener { calc_display.append(btn_8.text) }
        btn_9.setOnClickListener { calc_display.append(btn_9.text) }

        btn_Add.setOnClickListener { calc_display.append(btn_Add.text) }
        btn_Subtract.setOnClickListener { calc_display.append(btn_Subtract.text) }
        btn_Multiply.setOnClickListener { calc_display.append(btn_Multiply.text) }
        btn_Divide.setOnClickListener { calc_display.append(btn_Divide.text) }

        btn_Dot.setOnClickListener { calc_display.append(btn_Dot.text) }
        btn_LParen.setOnClickListener { calc_display.append(btn_LParen.text) }
        btn_RParen.setOnClickListener { calc_display.append(btn_RParen.text) }
        btn_Power.setOnClickListener { calc_display.append(btn_Power.text) }

        /*
            O botão Clear limpa o text_calc, que mostra a expressão,
            e o text_info, que mostra o resultado
        */
        btn_Clear.setOnClickListener {
            calc_display.clear()
            text_info.text = ""
        }

        /*
            Quando o botão Equal é clicado, a expressão é calculada usando
            a função eval
            Caso alguma exceção seja levantada, um Toast é mostrado, indicando
            o erro que ocorreu
        */
        btn_Equal.setOnClickListener {
            try {
                text_info.text = eval(calc_display.toString()).toString()
            } catch (e: RuntimeException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Como usar a função:
    // eval("2+2") == 4.0
    // eval("2+3*4") = 14.0
    // eval("(2+3)*4") = 20.0
    //Fonte: https://stackoverflow.com/a/26227947
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '
            fun nextChar() {
                val size = str.length
                ch = if ((++pos < size)) str.get(pos) else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Caractere inesperado: " + ch)
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            // | number | functionName factor | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'))
                        x += parseTerm() // adição
                    else if (eat('-'))
                        x -= parseTerm() // subtração
                    else
                        return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'))
                        x *= parseFactor() // multiplicação
                    else if (eat('/'))
                        x /= parseFactor() // divisão
                    else
                        return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor() // + unário
                if (eat('-')) return -parseFactor() // - unário
                var x: Double
                val startPos = this.pos
                if (eat('(')) { // parênteses
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') { // números
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = java.lang.Double.parseDouble(str.substring(startPos, this.pos))
                } else if (ch in 'a'..'z') { // funções
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()
                    if (func == "sqrt")
                        x = Math.sqrt(x)
                    else if (func == "sin")
                        x = Math.sin(Math.toRadians(x))
                    else if (func == "cos")
                        x = Math.cos(Math.toRadians(x))
                    else if (func == "tan")
                        x = Math.tan(Math.toRadians(x))
                    else
                        throw RuntimeException("Função desconhecida: " + func)
                } else {
                    throw RuntimeException("Caractere inesperado: " + ch.toChar())
                }
                if (eat('^')) x = Math.pow(x, parseFactor()) // potência
                return x
            }
        }.parse()
    }
}
