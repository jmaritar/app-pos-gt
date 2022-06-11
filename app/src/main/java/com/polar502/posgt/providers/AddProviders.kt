package com.polar502.posgt.providers

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.polar502.posgt.R
import com.polar502.posgt.customers.Customers
import com.polar502.posgt.databinding.ActivityAddCustomersBinding
import com.polar502.posgt.databinding.ActivityAddProvidersBinding

class AddProviders : AppCompatActivity() {

    //Variable para instanciar los componentes de la interfaz add
    private lateinit var bindingActivityAdd: ActivityAddProvidersBinding
    private val database = Firebase.database
    private val myRef = database.getReference("providers")
    private val file = 1

    //Imagenes de URL
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingActivityAdd = ActivityAddProvidersBinding.inflate(layoutInflater)
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
            val folder: StorageReference = FirebaseStorage.getInstance().reference.child("providers")
            val providersReference : StorageReference = folder.child("img$key")

            if(fileUri==null){
                val mProviders = Providers(id, name, nit, phone, email, address)
                myRef.child(key).setValue(mProviders)
            } else {
                providersReference.putFile(fileUri!!).addOnSuccessListener {
                    providersReference.downloadUrl.addOnSuccessListener { uri ->
                        val mProviders = Providers(id, name, nit, phone, email, address, uri.toString())
                        myRef.child(key).setValue(mProviders)
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