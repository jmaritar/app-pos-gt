package com.polar502.posgt.customers

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.polar502.posgt.databinding.ActivityAddCustomersBinding


class AddCustomers : AppCompatActivity() {

    //Variable para instanciar los componentes de la interfaz add
    private lateinit var bindingActivityAdd: ActivityAddCustomersBinding
    private val database = Firebase.database
    private val myRef = database.getReference("customers")
    private val file = 1

    //Imagenes de URL
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingActivityAdd = ActivityAddCustomersBinding.inflate(layoutInflater)
        val view = bindingActivityAdd.root
        setContentView(view)

        //Al clickear encima del botton Guardar hacer → saveButton
        bindingActivityAdd.saveButton.setOnClickListener {

            val id : String = bindingActivityAdd.idEditText.text.toString()
            val name : String = bindingActivityAdd.nameEditText.text.toString()
            val nit : String = bindingActivityAdd.nitEditText.text.toString()
            val phone : String = bindingActivityAdd.phoneEditText.text.toString()
            val email : String = bindingActivityAdd.emailEditText.text.toString()
            val address : String = bindingActivityAdd.addressEditText.text.toString()
            val key: String = myRef.push().key.toString()

            //folder del Storege en Firebase
            val folder: StorageReference = FirebaseStorage.getInstance().reference.child("customers")
            val customersReference : StorageReference = folder.child("img$key")

            if(fileUri==null){
                val mCustomers = Customers(id, name, nit, phone, email, address)
                myRef.child(key).setValue(mCustomers)
            } else {
                customersReference.putFile(fileUri!!).addOnSuccessListener {
                    customersReference.downloadUrl.addOnSuccessListener { uri ->
                        val mCustomers = Customers(id, name, nit, phone, email, address, uri.toString())
                        myRef.child(key).setValue(mCustomers)
                    }
                }
            }

            finish()
        }

        bindingActivityAdd.posterImageView.setOnClickListener {
            fileUpload()
        }
    }
    //Función Upload cargar imagen
    private fun fileUpload() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, file)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == file) {
            if (resultCode == RESULT_OK) {
                fileUri = data!!.data
                bindingActivityAdd.posterImageView.setImageURI(fileUri)
            }
        }
    }

}