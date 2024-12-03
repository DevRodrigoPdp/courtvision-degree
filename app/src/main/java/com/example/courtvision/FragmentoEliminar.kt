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

class FragmentoEliminar : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var spinnerEquipos: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_fragmento_eliminar, container, false)

        spinnerEquipos = view.findViewById(R.id.spinnerEquiposEliminar)
        val btnEliminar = view.findViewById<Button>(R.id.BtnEnviarFormularioJugador)
        val btnVolver = view.findViewById<Button>(R.id.BtnVolverAñadirJugador)

        // Cargar la lista de equipos en el spinner
        cargarEquiposEnSpinner()

        // Configurar el listener para el botón Eliminar
        btnEliminar.setOnClickListener {
            eliminarEquipo()
        }

        // Configurar el listener para el botón Volver
        btnVolver.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.contenedorFragmentos, FragmentoEquipos())
            transaction.commit()
        }

        return view;
    }

    private fun cargarEquiposEnSpinner() {
        // Consultar la lista de equipos desde Firestore
        db.collection("equipo")
            .get()
            .addOnSuccessListener { querySnapshot -> mostrarEquiposEnSpinner(querySnapshot) }
            .addOnFailureListener { e -> /* Manejar el error */ }
    }

    private fun mostrarEquiposEnSpinner(querySnapshot: QuerySnapshot) {
        val equipos = mutableListOf<String>()
        for (document in querySnapshot) {
            val nombre = document.getString("Nombre") ?: ""
            equipos.add(nombre)
        }

        // Configurar el adaptador del Spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, equipos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEquipos.adapter = adapter
    }

    private fun eliminarEquipo() {

        // Obtener el nombre del equipo seleccionado en el Spinner
        val nombreEquipo = spinnerEquipos.selectedItem.toString()

        // Eliminar el equipo de la base de datos
        db.collection("equipo")
            .whereEqualTo("Nombre", nombreEquipo)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    document.reference.delete()
                }
                // Mostrar un mensaje de éxito al usuario
                mostrarAlertaEquipoEliminado()            }
            .addOnFailureListener { e -> /* Manejar el error */ }
    }

    private fun mostrarAlertaEquipoEliminado() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Equipo Eliminado")
            .setMessage("El equipo se ha eliminado correctamente.")
            .setPositiveButton("Aceptar") { _, _ ->
                // Puedes realizar acciones adicionales o cerrar el fragmento si es necesario
            }
            .show()
    }
}