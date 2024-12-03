package com.example.courtvision

import android.app.AlertDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.text.ParseException
import java.util.Date
import java.util.Locale

class JugadoresFragmento : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: JugadorAdaptador

    private fun cargarJugadoresDesdeFirestore() {
        db.collection("jugador")
            .get()
            .addOnSuccessListener { querySnapshot -> mostrarJugadores(querySnapshot) }
            .addOnFailureListener { e -> /* Manejar el error */ }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_jugadores_fragmento, container, false)

        // Encuentra la vista RecyclerView en el diseño inflado
        val recyclerView = view.findViewById<RecyclerView>(R.id.listaJugadores)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val botonVolver = view.findViewById<Button>(R.id.botonVolverJugadores)
        botonVolver.setOnClickListener(View.OnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.contenedorFragmentos, FragmentoBienvenida())
            transaction.addToBackStack(null)
            transaction.commit()
        })

        val botonAnadir = view.findViewById<Button>(R.id.BtnAñadirJugador)
        botonAnadir.setOnClickListener{
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.contenedorFragmentos, FragmentoAnadirJugador())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        val eliminarJugador = view.findViewById<Button>(R.id.botonEliminarJugador)
        eliminarJugador.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.contenedorFragmentos, FragmentoEliminarJugador())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // Inicializar el adaptador
        adapter = JugadorAdaptador(emptyList())
        recyclerView.adapter = adapter

        // Cargar equipos desde Firestore
        cargarJugadoresDesdeFirestore()

        val campoBusqueda = view.findViewById<EditText>(R.id.campoBusquedaJugadores)
        val botonBuscar = view.findViewById<Button>(R.id.botonBuscarJugadores)

        botonBuscar.setOnClickListener {
            val nombreEquipo = campoBusqueda.text.toString()
            buscarYMostrarJugadores(nombreEquipo)
        }

        return view
    }

    private fun buscarYMostrarJugadores(nombre: String) {
        db.collection("jugador")
            .whereEqualTo("equipojugador", nombre)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // El jugador no existe, muestra un AlertDialog
                    mostrarAlertaEquipoNoExiste()
                } else {
                    // El equipo existe, muestra la lista
                    mostrarJugadores(querySnapshot)
                }
            }
            .addOnFailureListener { e -> /* Manejar el error */ }
    }

    // Agrega esta función para mostrar un AlertDialog si el equipo no existe
    private fun mostrarAlertaEquipoNoExiste() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Jugador no encontrado")
            .setMessage("El jugador que estás buscando no existe.")
            .setPositiveButton("Aceptar") { _, _ -> }
            .show()
    }

    private fun mostrarJugadores(querySnapshot: QuerySnapshot) {
        val jugadores = mutableListOf<Jugador>()
        for (document in querySnapshot) {
            val dorsalValue = document.getString("Dorsal") ?: "0"
            val dorsal = try {
                dorsalValue.toInt()
            } catch (e: NumberFormatException) {
                0
            }

            val fechaString = document.getString("Fecha") ?: ""
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fecha = try {
                formatoFecha.parse(fechaString) ?: Date()
            } catch (e: ParseException) {
                // Manejar la excepción de análisis de fecha
                Date()
            }
            val nombre = document.getString("Nombre") ?: ""
            val posicion = document.getString("Posicion") ?: ""
            val salud = document.getString("Salud") ?: ""
            val telefonoValue = document.getString("Telefono") ?: "0"
            val equipo = document.get("equipojugador") as? String ?: "Equipo no especificado"
            val telefono = try {
                telefonoValue.toInt()
            } catch (e: NumberFormatException) {
                0
            }

            jugadores.add(Jugador(nombre, dorsal, salud, telefono, posicion, fecha, equipo))
        }
        adapter.actualizarListaJugadores(jugadores)
    }


}