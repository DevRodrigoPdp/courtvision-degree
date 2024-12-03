package com.example.courtvision

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.google.firebase.firestore.FirebaseFirestore

class FragmentoAnadirJugador : Fragment() {

    private val equiposList = mutableListOf<Equipo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragmento_anadir_jugador, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Obtén una referencia a Cloud Firestore
        val db = FirebaseFirestore.getInstance()

        // Obtén una referencia a la colección "equipos"
        val equiposRef = db.collection("equipo")

        // Obtén la lista de equipos desde Firestore
        equiposRef.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val equipo = Equipo(
                        nombre = document.getString("Nombre") ?: "",
                        division = document.getString("Division") ?: ""
                    )
                    equiposList.add(equipo)
                }

                // Configura el adaptador para el Spinner
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, equiposList.map { it.nombre })
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                val spinner = view.findViewById<Spinner>(R.id.spinnerEquipos)
                spinner.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents.", exception)
            }

        val botonVolverAñadirJugador = view.findViewById<Button>(R.id.BtnVolverAñadirJugador)
        botonVolverAñadirJugador.setOnClickListener(View.OnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.contenedorFragmentos, JugadoresFragmento())
            transaction.addToBackStack(null)
            transaction.commit()
        })

        val botonAñadirJugador = view.findViewById<Button>(R.id.BtnEnviarFormularioJugador)
        botonAñadirJugador.setOnClickListener {

            // Obtén una referencia a Cloud Firestore
            val db = FirebaseFirestore.getInstance()

            // Obtén los valores de los EditText
            val nombreJugador = view.findViewById<EditText>(R.id.FormularioNombreJugador).text.toString()
            val dorsalJugador = view.findViewById<EditText>(R.id.FormularioDorsalJugador).text.toString()
            val saludJugador = view.findViewById<EditText>(R.id.FormularioSaludJugador).text.toString()
            val telefonoJugador = view.findViewById<EditText>(R.id.FormularioTelefonoJugador).text.toString()
            val posicionJugador = view.findViewById<EditText>(R.id.FormularioPosicionJugador).text.toString()
            val fechaJugador = view.findViewById<EditText>(R.id.FormularioFechaJugador).text.toString()

            // Obtén el equipo seleccionado del Spinner
            val spinner = view.findViewById<Spinner>(R.id.spinnerEquipos)
            val equipoSeleccionado = equiposList[spinner.selectedItemPosition]
            val nombreEquipoSeleccionado = equipoSeleccionado.nombre

            // Verifica que no haya campos en blanco
            if (nombreJugador.isEmpty() || dorsalJugador.isEmpty() || saludJugador.isEmpty() ||
                telefonoJugador.isEmpty() || posicionJugador.isEmpty() || fechaJugador.isEmpty()) {
                mostrarAlertDialog("Por favor, completa todos los campos.")
                return@setOnClickListener
            }

            // Verifica que el nombre solo contenga letras y espacios
            if (!nombreJugador.matches("[a-zA-Z\\s]+".toRegex())) {
                mostrarAlertDialog("Nombre inválido. Solo se permiten letras y espacios.")
                return@setOnClickListener
            }

            // Verifica que la posición solo contenga letras
            if (!posicionJugador.matches("[a-zA-Z]+".toRegex())) {
                mostrarAlertDialog("Posición inválida. Debe contener solo letras.")
                return@setOnClickListener
            }

            // Verifica que la salud solo contenga letras
            if (!saludJugador.matches("[a-zA-Z]+".toRegex())) {
                mostrarAlertDialog("Salud inválida. Debe contener solo letras.")
                return@setOnClickListener
            }

            // Verifica que el dorsal solo contenga números
            if (!dorsalJugador.matches("\\d+".toRegex())) {
                mostrarAlertDialog("Dorsal inválido. Solo se permiten números.")
                return@setOnClickListener
            }

            // Verifica que el teléfono solo contenga números y tenga exactamente 8 dígitos
            if (!telefonoJugador.matches("\\d{8}".toRegex())) {
                mostrarAlertDialog("Teléfono inválido. Debe contener exactamente 8 números.")
                return@setOnClickListener
            }

            // Crea un nuevo documento en la colección "jugadores"
            val nuevoJugadorRef = db.collection("jugador").document()

            // Guarda los datos en el documento
            val datosAInsertar: MutableMap<String, Any> = HashMap()
            datosAInsertar["Nombre"] = nombreJugador
            datosAInsertar["Dorsal"] = dorsalJugador
            datosAInsertar["Salud"] = saludJugador
            datosAInsertar["Telefono"] = telefonoJugador
            datosAInsertar["Posicion"] = posicionJugador
            datosAInsertar["Fecha"] = fechaJugador
            datosAInsertar["equipojugador"] = nombreEquipoSeleccionado

            nuevoJugadorRef.set(datosAInsertar)
                .addOnSuccessListener {
                    // Operación exitosa
                    mostrarAlertDialog("Datos agregados correctamente")
                    view.findViewById<EditText>(R.id.FormularioNombreJugador).setText("")
                    view.findViewById<EditText>(R.id.FormularioDorsalJugador).setText("")
                    view.findViewById<EditText>(R.id.FormularioSaludJugador).setText("")
                    view.findViewById<EditText>(R.id.FormularioTelefonoJugador).setText("")
                    view.findViewById<EditText>(R.id.FormularioPosicionJugador).setText("")
                    view.findViewById<EditText>(R.id.FormularioFechaJugador).setText("")
                }
                .addOnFailureListener {
                    // Manejar errores
                    mostrarAlertDialog("Error al agregar datos")
                }

        }
    }
    private fun mostrarAlertDialog(mensaje: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(mensaje)
            .setPositiveButton("Aceptar") { dialog, _ ->
                // Manejar clic en Aceptar si es necesario
                dialog.dismiss()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}