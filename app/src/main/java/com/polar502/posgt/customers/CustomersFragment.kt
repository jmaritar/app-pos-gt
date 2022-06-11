package com.polar502.posgt.customers

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.polar502.posgt.R
import com.polar502.posgt.databinding.FragmentCustomersBinding
import com.polar502.posgt.inventory.*

class CustomersFragment : Fragment() {

 /*   private lateinit var bindingFragmentCustomers: FragmentCustomersBinding //Esta linea de codigo queda en desuso*/
    private lateinit var messagesListener: ValueEventListener

    private val database = Firebase.database
    private val listCustomers:MutableList<Customers> = ArrayList()
    private val myRef = database.getReference("customers")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        //Obtengo la vista y la guardo en una variable
        val vista = inflater.inflate(R.layout.fragment_customers, container, false)

        //Creo una variable para almacenar el boton agregar.
        val btn_img: ImageView = vista.findViewById(R.id.addImageView) as ImageView


        //Evento del boton (+) del AddActivity
        btn_img.setOnClickListener { v ->
            val intent = Intent(activity, AddCustomers::class.java)
            v.context.startActivity(intent)
        }

        //Elimina el contenedor de los objetos -> recyclerView
        listCustomers.clear()

        //Llamo el metodo y le paso como parametro el reciclerView
        setupRecyclerView(vista.findViewById(R.id.recyclerView))

        //Creo la vista
        return vista

    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {

        messagesListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listCustomers.clear()
                dataSnapshot.children.forEach { resp ->
                    val mCustomers =
                        Customers(
                            resp.child("id").getValue<String>(),
                            resp.child("name").getValue<String>(),
                            resp.child("nit").getValue<String>(),
                            resp.child("phone").getValue<String>(),
                            resp.child("email").getValue<String>(),
                            resp.child("address").getValue<String>(),
                            resp.child("url").getValue<String>(),
                            resp.key)
                    mCustomers.let { listCustomers.add(it) }
                }
                recyclerView.adapter = CustomersViewAdapter(listCustomers)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "messages:onCancelled: ${error.message}")
            }
        }
        myRef.addValueEventListener(messagesListener)

        deleteSwipe(recyclerView)
    }

    class CustomersViewAdapter(private val values: MutableList<Customers>) :
        RecyclerView.Adapter<CustomersViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.customers_content, parent, false)
            return ViewHolder(view)
        }

        @SuppressLint("SetTextI18n")

        //Mostrar en el custom_content
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val mCustomers = values[position]
            holder.mPhoneTextView.text = mCustomers.phone
            holder.mNameTextView.text = mCustomers.name
            holder.mEmailTextView.text = mCustomers.email
            holder.mPosterImgeView.let {
                Glide.with(holder.itemView.context)
                    .load(mCustomers.url)
                    .into(it)
            }

            //Al clickear encima del item ir a → detalles
            holder.itemView.setOnClickListener { v ->
                val intent = Intent(v.context, DetailCustomers::class.java).apply {
                    putExtra("key", mCustomers.key)
                }
                v.context.startActivity(intent)
            }

            //Al mantener presionado encima del item ir a → edit
            holder.itemView.setOnLongClickListener{ v ->
                val intent = Intent(v.context, EditCustomers::class.java).apply {
                    putExtra("key", mCustomers.key)
                }
                v.context.startActivity(intent)
                true
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val mPhoneTextView: TextView = view.findViewById(R.id.phoneTextView) as TextView
            val mNameTextView: TextView = view.findViewById(R.id.nameTextView) as TextView
            val mEmailTextView: TextView = view.findViewById(R.id.emailTextView) as TextView
            val mPosterImgeView: ImageView = view.findViewById(R.id.posterImgeView) as ImageView
        }
    }

    private fun deleteSwipe(recyclerView: RecyclerView){
        val touchHelperCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                //Borrar la imagen del Storage
                val imageFirebaseStorage = FirebaseStorage.getInstance().reference.child("customers/img"+listCustomers[viewHolder.adapterPosition].key)
                imageFirebaseStorage.delete()

                listCustomers[viewHolder.adapterPosition].key?.let { myRef.child(it).setValue(null) }
                listCustomers.removeAt(viewHolder.adapterPosition)

                recyclerView.adapter?.notifyItemRemoved(viewHolder.adapterPosition)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
        val itemTouchHelper = ItemTouchHelper(touchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}