package com.polar502.posgt.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.polar502.posgt.MainActivity
import com.polar502.posgt.R
import com.polar502.posgt.inventory.AddInventory
import com.polar502.posgt.inventory.InventoryFragment

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        //Obtengo la vista y la guardo en una variable
        val vista = inflater.inflate(R.layout.fragment_home, container, false)

        //Creo una variable para almacenar el boton agregar.
        val btn_add: ImageView = vista.findViewById(R.id.add_item) as ImageView
        //Creo una variable para almacenar el boton ir a inventario.
        val btn_go_inventory: ImageView = vista.findViewById(R.id.go_inventory) as ImageView


        //Evento del boton (+) del AddActivity
        btn_add.setOnClickListener { v ->
            val intent = Intent(activity, AddInventory::class.java)
            v.context.startActivity(intent)
        }

        //Evento del boton ir a inventario del InventoryFragment.
        btn_go_inventory.setOnClickListener { v ->
            val intent = Intent(activity, MainActivity::class.java)

            //Le envio parametros al MainActivity para que incie con el InventoryFragment
            intent.putExtra("estado", "inventario")
            v.context.startActivity(intent)
        }

        //Creo la vista
        return vista

    }

}