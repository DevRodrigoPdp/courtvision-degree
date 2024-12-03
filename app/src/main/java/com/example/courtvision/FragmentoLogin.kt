package com.example.courtvision

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import java.util.zip.Inflater


class FragmentoLogin : Fragment() {

    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamo la vista del fragmento
        val view = inflater.inflate(R.layout.fragment_fragmento_login, container, false)

        // Inicializamos los componentes
        etCorreo = view.findViewById(R.id.etCorreo)
        etContrasena = view.findViewById(R.id.etContrasena)

        // Agregamos el evento al botón de acceso
        view.findViewById<Button>(R.id.btnAcceder).setOnClickListener {
            val correo = etCorreo.text.toString()
            val contraseña = etContrasena.text.toString()

            // Validamos que el correo electrónico y la contraseña no estén vacíos
            if (correo.isEmpty() || contraseña.isEmpty()) {
                // Mostramos un mensaje de error
                Toast.makeText(requireContext(), "Debes ingresar un correo electrónico y una contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificamos si el usuario existe en la base de datos
            FirebaseAuth.getInstance().signInWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        // El usuario existe
                        // Cambiamos de fragmento
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.contenedorFragmentos, FragmentoBienvenida())
                            ?.commit()
                    } else {
                        // El usuario no existe
                        // Mostramos un mensaje de error
                        Toast.makeText(requireContext(), "El usuario no existe", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Agregamos el botón de registro
        view.findViewById<Button>(R.id.btnRegistrar).setOnClickListener {
            val correo = etCorreo.text.toString()
            val contraseña = etContrasena.text.toString()

            // Creamos un nuevo usuario con el correo y la contraseña proporcionados
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // El usuario se ha registrado correctamente
                        // Podemos mostrar un mensaje de éxito
                        Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                    } else {
                        // El registro ha fallado
                        // Podemos mostrar un mensaje de error
                        Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }

        return view
    }
}
