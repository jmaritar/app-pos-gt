package com.polar502.posgt.providers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.polar502.posgt.customers.Customers
import com.polar502.posgt.databinding.ActivityEditProvidersBinding

class EditProviders : AppCompatActivity() {

    private lateinit var bindingActivityEdit: ActivityEditProvidersBinding
    private val file = 1
    private var fileUri: Uri? = null
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingActivityEdit = ActivityEditProvidersBinding.inflate(layoutInflater)
        val view = bindingActivityEdit.root
        setContentView(view)

        val key = intent.getStringExtra("key")
        val database = Firebase.database
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val myRef = database.getReference("providers").child(
            key.toString()
        )

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val mProviders: Providers? = dataSnapshot.getValue(Providers::class.java)
                if (mProviders != null) {

                    //Del layoutEdit. Traemos el EditText en cual obtendra el valor de la lista mCustomers
                    bindingActivityEdit.idEditText.text = Editable.Factory.getInstance().newEditable(mProviders.id)
                    bindingActivityEdit.nameEditText.text = Editable.Factory.getInstance().newEditable(mProviders.name)
                    bindingActivityEdit.nitEditText.text = Editable.Factory.getInstance().newEditable(mProviders.nit)
                    bindingActivityEdit.phoneEditText.text = Editable.Factory.getInstance().newEditable(mProviders.phone)
                    bindingActivityEdit.emailEditText.text = Editable.Factory.getInstance().newEditable(mProviders.email)
                    bindingActivityEdit.addressEditText.text = Editable.Factory.getInstance().newEditable(mProviders.address)

                    imageUrl = mProviders.url.toString()

                    if(fileUri==null){
                        Glide.with(view)
                            .load(imageUrl)
                            .into(bindingActivityEdit.posterImageView)
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        //Hacer cuando se presione el buttuon Guardar del Edit
        bindingActivityEdit.saveButton.setOnClickListener {

            val id : String = bindingActivityEdit.idEditText.text.toString()
            val name : String = bindingActivityEdit.nameEditText.text.toString()
            val nit : String = bindingActivityEdit.nitEditText.text.toString()
            val phone : String = bindingActivityEdit.phoneEditText.text.toString()
            val email : String = bindingActivityEdit.emailEditText.text.toString()
            val address: String = bindingActivityEdit.addressEditText.text.toString()

            val folder: StorageReference = FirebaseStorage.getInstance().reference.child("providers")
            val ProvidersReference : StorageReference = folder.child("img$key")

            if(fileUri==null){
                val mProviders = Providers(id, name, nit, phone, email, address, imageUrl)
                myRef.setValue(mProviders)
            } else {
                ProvidersReference.putFile(fileUri!!).addOnSuccessListener {
                    ProvidersReference.downloadUrl.addOnSuccessListener { uri ->
                        val mProviders = Providers(id, name, nit, phone, email, address, uri.toString())
                        myRef.setValue(mProviders)
                    }
                }
            }

            finish()
        }

        bindingActivityEdit.posterImageView.setOnClickListener {
            fileUpload()
        }
    }

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
                bindingActivityEdit.posterImageView.setImageURI(fileUri)
            }
        }
    }

}