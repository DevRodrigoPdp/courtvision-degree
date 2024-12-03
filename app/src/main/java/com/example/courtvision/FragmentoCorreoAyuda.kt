package com.example.courtvision

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.HashMap

class FragmentoCorreoAyuda : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fragmento_correo_ayuda, container, false)

        FirebaseApp.initializeApp(requireContext())

        // Encuentra las referencias de los elementos de la interfaz
        val btnCancelar: Button = view.findViewById(R.id.BotonCancelar)
        val btnEnviar: Button = view.findViewById(R.id.BotonEnviar)
        val mensajeEditText: EditText = view.findViewById(R.id.MensajePersonalizado)

        // Configura el clic del botón Cancelar para cambiar al FragmentoBienvenida
        btnCancelar.setOnClickListener {
            val fragmentBienvenida = FragmentoBienvenida()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragmentos, fragmentBienvenida)
                .addToBackStack(null)
                .commit()
        }

        // Configura el clic del botón Enviar para enviar la solicitud de ayuda
        btnEnviar.setOnClickListener {
            val mensaje = mensajeEditText.text.toString()

            if (mensaje.isNotEmpty()) {
                // Obtener el correo electrónico del usuario autenticado
                val correoElectronico = obtenerCorreoUsuarioAutenticado()

                if (correoElectronico.isNotEmpty()) {
                    // Llamar a la función para enviar la solicitud de ayuda
                    CoroutineScope(Dispatchers.IO).launch {
                        enviarSolicitudAyuda(correoElectronico, mensaje)
                    }
                    mensajeEditText.text.clear()

                } else {
                    mostrarError("No se pudo obtener el correo electrónico del usuario.")
                }
            } else {
                mostrarError("Por favor, ingresa un mensaje.")
            }
        }

        return view
    }

    private suspend fun enviarSolicitudAyuda(correo: String, mensaje: String) {
        try {
            val solicitudRef = db.collection("solicitudesAyuda").document()
            val datosSolicitud: MutableMap<String, Any> = HashMap()
            datosSolicitud["correoUsuario"] = correo
            datosSolicitud["mensaje"] = mensaje
            solicitudRef.set(datosSolicitud).await()

            withContext(Dispatchers.Main) {
                mostrarMensaje("Solicitud de ayuda enviada correctamente.")
            }
        } catch (e: Exception) {
            Log.e("EnviarSolicitudAyuda", e.toString())
            withContext(Dispatchers.Main) {
                mostrarError("Error al enviar la solicitud de ayuda. Inténtalo de nuevo.")
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(mensaje)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun mostrarError(mensaje: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(mensaje)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun obtenerCorreoUsuarioAutenticado(): String {
        val auth = FirebaseAuth.getInstance()
        val usuario = auth.currentUser
        return usuario?.email ?: ""
    }
}
