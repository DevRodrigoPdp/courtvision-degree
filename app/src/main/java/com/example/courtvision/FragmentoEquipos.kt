package com.example.courtvision

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class FragmentoEquipos : Fragment(), EquipoAdaptador.OnItemClickListener {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: EquipoAdaptador

    private fun cargarEquiposDesdeFirestore() {
        db.collection("equipo")
            .get()
            .addOnSuccessListener { querySnapshot -> mostrarEquipos(querySnapshot) }
            .addOnFailureListener { e -> /* Manejar el error */ }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fragmento_equipos, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.listaEquipos)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val botonVolver = view.findViewById<Button>(R.id.botonVolverEquipos)
        botonVolver.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.contenedorFragmentos, FragmentoBienvenida())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        val botonAnadir = view.findViewById<Button>(R.id.BtnAñadirEquipo)
        botonAnadir.setOnClickListener{
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.contenedorFragmentos, FragmentoAnadirEquipo())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        val botonEliminar = view.findViewById<Button>(R.id.botonEliminarEquipo)
        botonEliminar.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.contenedorFragmentos, FragmentoEliminar())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // Inicializar el adaptador
        adapter = EquipoAdaptador(emptyList())
        adapter.setOnItemClickListener(this)
        recyclerView.adapter = adapter

        // Cargar equipos desde Firestore
        cargarEquiposDesdeFirestore()


        val campoBusqueda = view.findViewById<EditText>(R.id.campoBusquedaEquipos)
        val botonBuscar = view.findViewById<Button>(R.id.botonBuscarEquipos)

        botonBuscar.setOnClickListener {
            val nombreEquipo = campoBusqueda.text.toString()
            buscarYMostrarEquipos(nombreEquipo)
        }

        return view
    }

    private fun buscarYMostrarEquipos(nombre: String) {
        db.collection("equipo")
            .whereEqualTo("Nombre", nombre)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // El equipo no existe, muestra un AlertDialog
                    mostrarAlertaEquipoNoExiste()
                } else {
                    // El equipo existe, muestra la lista
                    mostrarEquipos(querySnapshot)
                }
            }
            .addOnFailureListener { e -> /* Manejar el error */ }
    }

    // Agrega esta función para mostrar un AlertDialog si el equipo no existe
    private fun mostrarAlertaEquipoNoExiste() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Equipo no encontrado")
            .setMessage("El equipo que estás buscando no existe.")
            .setPositiveButton("Aceptar") { _, _ -> }
            .show()
    }

    private fun mostrarEquipos(querySnapshot: QuerySnapshot) {
        val equipos = mutableListOf<Equipo>()
        for (document in querySnapshot) {
            val nombre = document.getString("Nombre") ?: ""
            val division = document.getString("Division") ?: ""
            equipos.add(Equipo(nombre, division))
        }
        adapter.actualizarListaEquipos(equipos)
    }

    override fun onItemClick(position: Int) {

        // Cambia al fragmento que muestra la lista de jugadores
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.contenedorFragmentos, JugadoresFragmento())
        transaction.addToBackStack(null)
        transaction.commit()
    }
}