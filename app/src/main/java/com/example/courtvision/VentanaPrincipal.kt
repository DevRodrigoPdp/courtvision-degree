package com.example.courtvision

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class VentanaPrincipal : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventana_principal)

        if (savedInstanceState == null) {
            // Reemplaza "TuFragmentoPrincipal()" con el nombre de tu fragmento principal
            val mainFragment = FragmentoLogin()
            supportFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragmentos, mainFragment)
                .commit()
        }

        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.contenedorFragmentos,FragmentoVentanaPrincipal())
        transaction.commit()

    }


}