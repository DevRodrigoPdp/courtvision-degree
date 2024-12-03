package com.example.courtvision

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FragmentoEventosCalendario : Fragment() {

    private lateinit var eventTitleEditText: EditText
    private lateinit var eventDescriptionEditText: EditText
    private lateinit var eventLocationEditText: EditText
    private lateinit var eventDateEditText: EditText
    private lateinit var eventTimeEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var botonInicio: Button
    private lateinit var botonVolverCalendario: Button
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
        val view = inflater.inflate(R.layout.fragment_fragmento_eventos_calendario, container, false)

        // Aquí debes vincular las vistas con los elementos del diseño
        eventTitleEditText = view.findViewById(R.id.eventTitle)
        eventDescriptionEditText = view.findViewById(R.id.eventDescription)
        eventLocationEditText = view.findViewById(R.id.eventLocation)
        eventDateEditText = view.findViewById(R.id.eventDate)
        eventTimeEditText = view.findViewById(R.id.eventTime)
        saveButton = view.findViewById(R.id.BotonGuardar)
        botonInicio = view.findViewById(R.id.BotonVolver)
        botonVolverCalendario = view.findViewById(R.id.BotonVolverCalendario)

        botonInicio.setOnClickListener {
            // En tu código para reemplazar un fragmento por otro
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.contenedorFragmentos, FragmentoBienvenida())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // Configuración del botón "Volver al Calendario"
        botonVolverCalendario.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.contenedorFragmentos, FragmentoCalendario())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // Recupera la fecha seleccionada del argumento
        val selectedDate = arguments?.getString("selectedDate")

        // Establece la fecha seleccionada en el editText eventDate
        eventDateEditText.setText(selectedDate)

        // Configura el clic del botón Guardar
        saveButton.setOnClickListener {
            guardarEventoEnFirestore()
        }

        return view
    }

    private fun guardarEventoEnFirestore() {
        val usuarioActual = firebaseAuth.currentUser
        if (usuarioActual != null) {
            val eventosCollection = firestore.collection("eventos")
            val nuevoEvento = hashMapOf(
                "usuarioId" to usuarioActual.uid,
                "titulo" to eventTitleEditText.text.toString(),
                "descripcion" to eventDescriptionEditText.text.toString(),
                "ubicacion" to eventLocationEditText.text.toString(),
                "fecha" to eventDateEditText.text.toString(),
                "hora" to eventTimeEditText.text.toString()
                // Agrega otros campos según tus necesidades
            )

            eventosCollection.add(nuevoEvento)
                .addOnSuccessListener { documentReference ->
                    // El evento se añadió correctamente
                    // Puedes realizar acciones adicionales si es necesario
                    // (por ejemplo, mostrar un mensaje de éxito)
                }
                .addOnFailureListener { e ->
                    // Ocurrió un error al añadir el evento
                    // Puedes manejar el error según tus necesidades
                }
        }
    }
}
