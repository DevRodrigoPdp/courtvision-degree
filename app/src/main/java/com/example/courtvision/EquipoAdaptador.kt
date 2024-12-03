package com.example.courtvision

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EquipoAdaptador (private var teams: List<Equipo>) : RecyclerView.Adapter<EquipoAdaptador.TeamViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener{
        fun onItemClick(position:Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        itemClickListener = listener
    }

    class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val teamName: TextView = itemView.findViewById(R.id.teamName)
        val teamDivision: TextView = itemView.findViewById(R.id.teamDivision)
        //val teamId : TextView = itemView.findViewById(R.id.teamId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipoAdaptador.TeamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contenidolista, parent, false)
        return TeamViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {

        holder.itemView.setOnClickListener(View.OnClickListener {
            itemClickListener?.onItemClick(position)
        })
        val currentTeam = teams[position]
        holder.teamName.text = currentTeam.nombre
        holder.teamDivision.text = currentTeam.division
        //holder.teamId.text = currentTeam.idequipo
    }

    fun actualizarListaEquipos(nuevaLista: List<Equipo>) {
        teams = nuevaLista
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return teams.size
    }
}