package com.example.courtvision

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import androidx.fragment.app.Fragment


class FragmentoBienvenida : Fragment() {

    private fun isValidEmail(email: String): Boolean {
        // Puedes agregar una lógica de validación de correo electrónico aquí
        // Este es un ejemplo simple que verifica si el correo contiene "@" y "."
        return email.contains("@") && email.contains(".")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Equipos -> {
                val fragmentManager = requireActivity().supportFragmentManager
                val transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.contenedorFragmentos, FragmentoEquipos())
                transaction.commit()
                return true
            }
            R.id.Calendario -> {
                val fragmentManager = requireActivity().supportFragmentManager
                val transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.contenedorFragmentos, FragmentoCalendario())
                transaction.commit()
                return true
            }
            R.id.Ayuda -> {
                val fragmentManager = requireActivity().supportFragmentManager
                val transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.contenedorFragmentos, FragmentoCorreoAyuda())
                transaction.commit()
                return true


            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}