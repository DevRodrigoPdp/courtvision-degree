package com.example.courtvision

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JugadorAdaptador (private var jugadores: List<Jugador>): RecyclerView.Adapter<JugadorAdaptador.JugadorViewHolder>(){

    class JugadorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val jugadorNombre: TextView = itemView.findViewById(R.id.jugadorNombre)
        val jugadorDorsal: TextView = itemView.findViewById(R.id.jugadorDorsal)
        val jugadorEstadoSalud: TextView = itemView.findViewById(R.id.jugadorEstadoSalud)
        val jugadorPosicion: TextView = itemView.findViewById(R.id.jugadorPosicion)
        val jugadorTelefono: TextView = itemView.findViewById(R.id.jugadorTelefono)
        val jugadorFechaNacimiento: TextView = itemView.findViewById(R.id.jugadorFechaNacimiento)
        val jugadorequipo: TextView = itemView.findViewById(R.id.jugadorequipo)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JugadorAdaptador.JugadorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.jugadoreslista, parent, false)
        return JugadorAdaptador.JugadorViewHolder(view)
    }

    override fun onBindViewHolder(holder: JugadorAdaptador.JugadorViewHolder, position: Int) {
        val jugadorActual = jugadores[position]
        holder.jugadorNombre.text = jugadorActual.nombreJugador
        holder.jugadorDorsal.text = jugadorActual.dorsalJugador.toString()
        holder.jugadorPosicion.text = jugadorActual.posicionJugador
        holder.jugadorEstadoSalud.text = jugadorActual.estadoSaludJugador
        holder.jugadorTelefono.text = jugadorActual.telefonoJugador.toString()
        holder.jugadorFechaNacimiento.text = jugadorActual.fechaNacimiento.toString()
        holder.jugadorequipo.text = jugadorActual.equipojugador
    }

    override fun getItemCount(): Int {
        return jugadores.size
    }

    fun actualizarListaJugadores(nuevaLista: List<Jugador>) {
        jugadores = nuevaLista
        notifyDataSetChanged()
    }
}