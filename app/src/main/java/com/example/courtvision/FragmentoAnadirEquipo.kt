package com.example.courtvision

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FragmentoAnadirEquipo : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fragmento_anadir_equipo, container, false)
        FirebaseApp.initializeApp(requireContext())
        return view
    }

    private suspend fun existeEquipoConNombre(nombreEquipo: String): Boolean {
        return withContext(Dispatchers.IO) {
            // Obtén una referencia a Cloud Firestore
            val db = FirebaseFirestore.getInstance()

            // Consulta para verificar si existe un equipo con el mismo nombre
            val query = db.collection("equipo")
                .whereEqualTo("Nombre", nombreEquipo)
                .get()
                .await()

            // Devuelve true si hay resultados, indicando que ya existe un equipo con ese nombre
            return@withContext !query.isEmpty
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val buttonVolver = view.findViewById<Button>(R.id.BtnVolverAñadir)

        // Configura el clic del botón de "Volver" para regresar a la lista de equipos
        buttonVolver.setOnClickListener {
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.contenedorFragmentos, FragmentoEquipos()) // Reemplaza con tu fragmento de lista de equipos
            transaction.addToBackStack(null)
            transaction.commit()
        }

        val botonAñadirEquipo = view.findViewById<Button>(R.id.BtnEnviarFormularioEquipo)
        botonAñadirEquipo.setOnClickListener {
            // Obtén una referencia a Cloud Firestore
            val db = FirebaseFirestore.getInstance()

            // Obtén el nombre y la división del equipo desde los EditText
            val nombreEquipo = view.findViewById<EditText>(R.id.FormularioNombreEquipo).text.toString()
            val divisionEquipo = view.findViewById<EditText>(R.id.FormularioDivisionEquipo).text.toString()

            // Verifica si ya existe un equipo con el mismo nombre
            CoroutineScope(Dispatchers.IO).launch {
                val equipoExistente = existeEquipoConNombre(nombreEquipo)
                withContext(Dispatchers.Main) {
                    if (equipoExistente) {
                        // Muestra un mensaje de error
                        mostrarAlertDialog("Ya existe un equipo con ese nombre")
                    } else {

                        // Crea un nuevo documento en la colección "equipos"
                        val nuevoEquipoRef = db.collection("equipo").document()

                        // Guarda los datos en el documento
                        val datosAInsertar: MutableMap<String, Any> = HashMap()
                        datosAInsertar["Nombre"] = nombreEquipo
                        datosAInsertar["Division"] = divisionEquipo

                        nuevoEquipoRef.set(datosAInsertar)
                            .addOnSuccessListener {
                                // Operación exitosa
                                mostrarAlertDialog("Datos agregados correctamente")
                                view.findViewById<EditText>(R.id.FormularioNombreEquipo).setText("")
                                view.findViewById<EditText>(R.id.FormularioDivisionEquipo).setText("")
                            }
                            .addOnFailureListener {
                                // Manejar errores
                                mostrarAlertDialog("Error al agregar datos")
                            }
                    }
                }
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