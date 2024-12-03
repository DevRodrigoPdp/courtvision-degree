package com.example.courtvision

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class FragmentoEliminarJugador : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var spinnerJugadores: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_fragmento_eliminar_jugador, container, false)

        spinnerJugadores = view.findViewById(R.id.spinnerJugadoresEliminar)
        val btnEliminar = view.findViewById<Button>(R.id.BtnEnviarFormularioJugador)
        val btnVolver = view.findViewById<Button>(R.id.BtnVolverAñadirJugador)

        // Cargar la lista de equipos en el spinner
        cargarJugadoresEnSpinner()

        // Configurar el listener para el botón Eliminar
        btnEliminar.setOnClickListener {
            eliminarJugador()
        }

        // Configurar el listener para el botón Volver
        btnVolver.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.contenedorFragmentos, JugadoresFragmento())
            transaction.commit()
        }

        return view;
    }
    private fun cargarJugadoresEnSpinner() {
        // Consultar la lista de equipos desde Firestore
        db.collection("jugador")
            .get()
            .addOnSuccessListener { querySnapshot -> mostrarEquiposEnSpinner(querySnapshot) }
            .addOnFailureListener { e -> /* Manejar el error */ }
    }

    private fun mostrarEquiposEnSpinner(querySnapshot: QuerySnapshot) {
        val jugadores = mutableListOf<String>()
        for (document in querySnapshot) {
            val nombre = document.getString("Nombre") ?: ""
            jugadores.add(nombre)
        }

        // Configurar el adaptador del Spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, jugadores)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJugadores.adapter = adapter
    }

    private fun eliminarJugador() {
        // Obtener el nombre del equipo seleccionado en el Spinner
        val nombreJugador = spinnerJugadores.selectedItem.toString()

        // Eliminar el equipo de la base de datos
        db.collection("jugador")
            .whereEqualTo("Nombre", nombreJugador)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    document.reference.delete()
                }
                // Mostrar un mensaje de éxito al usuario
                mostrarAlertaJugadorEliminado()
            }
            .addOnFailureListener { e -> /* Manejar el error */ }
    }

    private fun mostrarAlertaJugadorEliminado() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Jugador Eliminado")
            .setMessage("El Jugador se ha eliminado correctamente.")
            .setPositiveButton("Aceptar") { _, _ ->
                // Puedes realizar acciones adicionales o cerrar el fragmento si es necesario
            }
            .show()
    }
}