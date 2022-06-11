package com.polar502.posgt.inventory

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
import com.polar502.posgt.*

class InventoryFragment : Fragment() {

    //Declaración de Variables
  /*  private lateinit var bindingFragmentInventory: FragmentInventoryBinding //Esta linea de codigo queda en desuso*/
    private lateinit var messagesListener: ValueEventListener

    private val database = Firebase.database
    private val listInventory:MutableList<Inventory> = ArrayList()
    private val myRef = database.getReference("inventory")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        //Obtengo la vista y la guardo en una variable
        val vista = inflater.inflate(R.layout.fragment_inventory, container, false)

        //Creo una variable para almacenar el boton agregar.
        val btn_img: ImageView = vista.findViewById(R.id.addImageView) as ImageView


        //Evento del boton (+) del AddActivity
        btn_img.setOnClickListener { v ->
            val intent = Intent(activity, AddInventory::class.java)
            v.context.startActivity(intent)
        }

        //Elimina el contenedor de los objetos -> recyclerView
        listInventory.clear()

        //Llamo el metodo y le paso como parametro el reciclerView
        setupRecyclerView(vista.findViewById(R.id.recyclerView))

        //Creo la vista
        return vista

    }

/*    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingFragmentInventory = FragmentInventoryBinding.inflate(layoutInflater)

        //Colocar el contenedor de los objetos -> recyclerView
        val view = bindingFragmentInventory.root
        setContentView(view)

//        Evento del boton (+) del AddActivity
        bindingFragmentInventory.addImageView.setOnClickListener { v ->
            val intent = Intent(this, AddInventory::class.java)
            v.context.startActivity(intent)
        }

        //Elimina el contenedor de los objetos -> recyclerView
        listInventory.clear()
        setupRecyclerView(bindingFragmentInventory.recyclerView)

    }*/

    private fun setupRecyclerView(recyclerView: RecyclerView) {

        messagesListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listInventory.clear()
                dataSnapshot.children.forEach { resp ->
                    val mInventory =
                        Inventory(
                            resp.child("id").getValue<String>(),
                            resp.child("name").getValue<String>(),
                            resp.child("amount").getValue<String>(),
                            resp.child("date").getValue<String>(),
                            resp.child("price").getValue<String>(),
                            resp.child("description").getValue<String>(),
                            resp.child("url").getValue<String>(),
                            resp.key)
                    mInventory.let { listInventory.add(it) }
                }
                recyclerView.adapter = InventoryViewAdapter(listInventory)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "messages:onCancelled: ${error.message}")
            }
        }
        myRef.addValueEventListener(messagesListener)

        deleteSwipe(recyclerView)
    }

    class InventoryViewAdapter(private val values: MutableList<Inventory>) :
        RecyclerView.Adapter<InventoryViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.inventory_content, parent, false)
            return ViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val mInventory = values[position]
            holder.mIdTextView.text = mInventory.id
            holder.mNameTextView.text = mInventory.name
            holder.mAmountTextView.text = "Stock: " + mInventory.amount
            holder.mDateTextView.text = mInventory.date
            holder.mPriceTextView.text = "Q " + mInventory.price
            holder.mPosterImgeView.let {
                Glide.with(holder.itemView.context)
                    .load(mInventory.url)
                    .into(it)
            }

            //Al clickear encima del item ir a → detalles
            holder.itemView.setOnClickListener { v ->
                val intent = Intent(v.context, DetailInventory::class.java).apply {
                    putExtra("key", mInventory.key)
                }
                v.context.startActivity(intent)
            }

            //Al mantener presionado encima del item ir a → edit
            holder.itemView.setOnLongClickListener{ v ->
                val intent = Intent(v.context, EditInventory::class.java).apply {
                    putExtra("key", mInventory.key)
                }
                v.context.startActivity(intent)
                true
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val mIdTextView: TextView = view.findViewById(R.id.idTextView) as TextView
            val mNameTextView: TextView = view.findViewById(R.id.nameTextView) as TextView
            val mAmountTextView: TextView = view.findViewById(R.id.amountTextView) as TextView
            val mDateTextView: TextView = view.findViewById(R.id.dateTextView) as TextView
            val mPriceTextView: TextView = view.findViewById(R.id.priceTextView) as TextView
            val mPosterImgeView: ImageView = view.findViewById(R.id.posterImgeView) as ImageView
        }
    }

    private fun deleteSwipe(recyclerView: RecyclerView){
        val touchHelperCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val imageFirebaseStorage = FirebaseStorage.getInstance().reference.child("inventory/img"+listInventory[viewHolder.adapterPosition].key)
                imageFirebaseStorage.delete()

                listInventory[viewHolder.adapterPosition].key?.let { myRef.child(it).setValue(null) }
                listInventory.removeAt(viewHolder.adapterPosition)

                recyclerView.adapter?.notifyItemRemoved(viewHolder.adapterPosition)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
        val itemTouchHelper = ItemTouchHelper(touchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

}