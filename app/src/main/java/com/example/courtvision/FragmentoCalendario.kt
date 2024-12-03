package com.example.courtvision

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class  FragmentoCalendario : Fragment() {
    private lateinit var calendarView: CalendarView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fragmento_calendario, container, false)
        calendarView = view.findViewById(R.id.calendarioEventos)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$year-${month + 1}-$dayOfMonth"

            // Verifica si hay eventos para la fecha seleccionada
            obtenerInformacionDesdeFirestore(selectedDate)
        }

        // Crear el botón BtnVolver y asignarle un evento de clic
        val btnVolver: Button = view.findViewById(R.id.BtnVolver)
        btnVolver.setOnClickListener {
            // Lógica para volver al FragmentoBienvenida
            val FragmentoBienvenida = FragmentoBienvenida()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.contenedorFragmentos,FragmentoBienvenida)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
    }

    private fun obtenerInformacionDesdeFirestore(selectedDate: String) {
        val usuarioActual = firebaseAuth.currentUser
        if (usuarioActual != null) {
            val eventosCollection = firestore.collection("eventos")
            eventosCollection
                .whereEqualTo("usuarioId", usuarioActual.uid)
                .whereEqualTo("fecha", selectedDate)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        // Hay eventos para la fecha seleccionada
                        val evento = documents.documents[0].data
                        mostrarDialogConInformacion(evento as Map<String, Any>, selectedDate)
                    } else {
                        // No hay eventos para la fecha seleccionada
                        irAFragmentoEventosCalendario(selectedDate)
                    }
                }
                .addOnFailureListener { e ->
                    // Ocurrió un error al obtener eventos desde Firestore
                    irAFragmentoEventosCalendario(selectedDate)
                }
        }
    }

    private fun mostrarDialogConInformacion(evento: Map<String, Any>, selectedDate: String) {
        val titulo = evento["titulo"] as? String ?: ""
        val descripcion = evento["descripcion"] as? String ?: ""
        val ubicacion = evento["ubicacion"] as? String ?: ""
        val hora = evento["hora"] as? String ?: ""

        val mensaje = "Evento:\n" +
                "Título: $titulo\n" +
                "Descripción: $descripcion\n" +
                "Ubicación: $ubicacion\n" +
                "Hora: $hora"

        // Crea un Dialog personalizado
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Detalles del Evento")
            .setMessage(mensaje)
            .setPositiveButton("Crear Otro Evento") { dialog, _ ->
                // Cierra el diálogo actual
                dialog.dismiss()

                // Inicia la transición al fragmento de creación de eventos
                irAFragmentoEventosCalendario(selectedDate)
            }
            .setNegativeButton("Aceptar") { dialog, _ ->
                // Cierra el diálogo actual
                dialog.dismiss()
            }

        // Muestra el Dialog
        builder.create().show()
    }

    private fun irAFragmentoEventosCalendario(selectedDate: String) {
        val fragmentoEventosCalendario = FragmentoEventosCalendario()
        val bundle = Bundle()
        bundle.putString("selectedDate", selectedDate)
        fragmentoEventosCalendario.arguments = bundle

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedorFragmentos, fragmentoEventosCalendario)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
