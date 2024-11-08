package com.example.calcularpropina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.calcularpropina.ui.theme.CalcularPropinaTheme
import java.text.NumberFormat

/* En este ejercicio de Calcular Propinas vamos a usar los siguientes conceptos:
* Variables 
* Funciones 
* Modificadores 
* TextField, es un componente que nos permite crear un campo de entrada de texto
* Switch, es un componente que nos permite crear un switch, un switch es un botón que nos permite activar o desactivar una opción
*/

/* Para realizar este ejercicio hay que seguir estos pasos:
* 1. Crear el layout de la aplicación
* 2. Crear el componente para el campo de entrada de texto
* 3. Crear el componente para el switch de redondear a la cantidad de propina
* 4. Crear la función para calcular la propina
*/

// Falta poner un slider o a lo mejor botones de 25%, 35%, 50%, etc para que cada usuario pueda poner la cantidad de propina que quiera

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            CalcularPropinaTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    CalcularPropinaLayout()
                }
            }
        }
    }
}

@Composable
fun CalcularPropinaLayout() {
    // Usamos var para poder modificar el valor de la variable, ya que es una variable de estado
    // Usamos by remember para recordar el valor de la variable porque sino se reiniciaría cada vez que se "renderice" el componente, es decir, cada vez que se vuelva a ejecutar el composable, 
    // esto se debe a que es una variable de estado y los valores de las variables de estado se reinician cuando se renderiza el componente
    var amountInput by remember { mutableStateOf("") }
    var tipInput by remember { mutableStateOf(15f) }
    var roundUp by remember { mutableStateOf(false) }

    // Son valores que no cambian, son valores que se calculan una vez y no cambian por eso usamos val
    // Convertimos el valor de amountInput a Double, si no es un número válido, usamos 0.0
    val amount = amountInput.toDoubleOrNull() ?: 0.0
    // Convertimos el valor de tipInput a Double
    val tipPercent = tipInput.toDouble()
    // Calculamos el tip usando la función calculateTip
    val tip = calculateTip(amount, tipPercent, roundUp)

    // Column es un componente que nos permite organizar los elementos en una columna

    Column(
        modifier = Modifier // Modificador que nos permite agregar padding, scroll, etc.
            .statusBarsPadding() // Padding para la barra de estado
            .padding(horizontal = 40.dp) // Padding horizontal
            .verticalScroll(rememberScrollState()) // Scroll vertical
            .safeDrawingPadding(), // Padding para la parte de arriba
        horizontalAlignment = Alignment.CenterHorizontally, // Alineación horizontal al centro
        verticalArrangement = Arrangement.Center // Alineación vertical al centro
    ) {
        Text(
            text = stringResource(R.string.calculate_tip),
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp) // Padding para el texto
                .align(alignment = Alignment.Start) // Alineación del texto al inicio
        )
        EditNumberField(
            value = amountInput,
            onValueChanged = { amountInput = it },
            modifier = Modifier
                .padding(bottom = 32.dp) // Padding para el campo de entrada
                .fillMaxWidth() // Ocupa todo el ancho
        )

        Text(
            text = stringResource(R.string.tip_percentage, tipPercent.toInt()),
            modifier = Modifier.align(alignment = Alignment.Start) // Alineación del texto al inicio
        )

        RoundTheTipRow(
            roundUp = roundUp, // Valor del switch, es el valor que se muestra en el switch
            onRoundUpChanged = {
                roundUp = it
            }, // Función que se ejecuta cuando cambia el valor del switch, cuando el usuario mueve el switch
            modifier = Modifier.padding(bottom = 32.dp) // Padding para el switch
        )

        Text(
            text = stringResource(R.string.tip_amount, tip), // Texto con el tip
            style = MaterialTheme.typography.displaySmall // Estilo del texto
        )
        Spacer(modifier = Modifier.height(150.dp)) // Espacio para separar el texto del botón
    }
}

@Composable
fun EditNumberField( // Componente para el campo de entrada
    value: String, // Valor del campo de entrada
    // Función que se ejecuta cuando cambia el valor del campo de entrada, onValueChanged es una función que recibe un String y devuelve un Unit
    // Unit es un tipo de dato que no devuelve nada ya que es el tipo de dato que devuelve la función onValueChanged
    onValueChanged: (String) -> Unit,
    // Modificador para el campo de entrada
    modifier: Modifier
) {
    // TextField es un componente que nos permite crear un campo de entrada de texto, necesitamos TextField para que el usuario pueda ingresar un valor
    TextField(
        value = value, // Valor del campo de entrada
        singleLine = true, // Solo una línea
        modifier = modifier, // Modificador para el campo de entrada
        onValueChange = onValueChanged, // Función que se ejecuta cuando cambia el valor del campo de entrada
        label = { Text(stringResource(R.string.bill_amount)) }, // Label del campo de entrada
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Opciones del teclado
    )
}

@Composable
fun RoundTheTipRow( // Componente para el switch
    roundUp: Boolean, // Valor del switch
    onRoundUpChanged: (Boolean) -> Unit, // Función que se ejecuta cuando cambia el valor del switch
    // Modificador para el switch
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(), // Ocupa todo el ancho
        verticalAlignment = Alignment.CenterVertically // Alineación vertical al centro 
    ) {
        Text(text = stringResource(R.string.round_up_tip)) // Texto del switch
        Switch(
            checked = roundUp, // Valor del switch
            onCheckedChange = onRoundUpChanged, // Función que se ejecuta cuando cambia el valor del switch
            modifier = Modifier
                .fillMaxWidth() // Ocupa todo el ancho
                .wrapContentWidth(Alignment.End) // Alineación del switch al final
        )
    }
}

/**
 * Calculates the tip based on the user input and format the tip amount
 * according to the local currency.
 * Example would be "$10.00".
 */
private fun calculateTip( // Función para calcular el tip
    amount: Double, // Valor del tip
    tipPercent: Double = 15.0, // Valor del tip por defecto
    roundUp: Boolean = false // Valor del switch por defecto
): String {
    var tip = tipPercent / 100 * amount // Calculamos el tip
    if (roundUp) {
        tip = kotlin.math.ceil(tip) // Si el switch está activado, redondeamos el tip
    }
    return NumberFormat.getCurrencyInstance()
        .format(tip) // Formateamos el tip para que se vea como una moneda
}

@Preview(showBackground = true)
@Composable
fun CalcularPropinaPreview() {
    CalcularPropinaTheme() {
        CalcularPropinaLayout()
    }
}

