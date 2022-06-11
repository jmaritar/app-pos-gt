package com.polar502.posgt.inventory

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.polar502.posgt.R
import com.polar502.posgt.databinding.ActivityEditBinding
import com.polar502.posgt.databinding.ActivityEditInventoryBinding

class EditInventory : AppCompatActivity() {


    private lateinit var bindingActivityEdit: ActivityEditInventoryBinding
    private val file = 1
    private var fileUri: Uri? = null
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingActivityEdit = ActivityEditInventoryBinding.inflate(layoutInflater)
        val view = bindingActivityEdit.root
        setContentView(view)

        val key = intent.getStringExtra("key")
        val database = Firebase.database
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val myRef = database.getReference("inventory").child(
            key.toString()
        )

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val mInventory: Inventory? = dataSnapshot.getValue(Inventory::class.java)
                if (mInventory != null) {

                    //Del layoutEdit. Traemos el EditText en cual obtendra el valor de la lista Inventory
                    bindingActivityEdit.nameEditText.text = Editable.Factory.getInstance().newEditable(mInventory.name)
                    bindingActivityEdit.amountEditText.text = Editable.Factory.getInstance().newEditable(mInventory.amount)
                    bindingActivityEdit.idEditText.text = Editable.Factory.getInstance().newEditable(mInventory.id)
                    bindingActivityEdit.priceEditText.text = Editable.Factory.getInstance().newEditable(mInventory.price)
                    bindingActivityEdit.dateEditText.text = Editable.Factory.getInstance().newEditable(mInventory.date)
                    bindingActivityEdit.descriptionEditText.text = Editable.Factory.getInstance().newEditable(mInventory.description)

                    imageUrl = mInventory.url.toString()

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

            val name : String = bindingActivityEdit.nameEditText.text.toString()
            val amount : String = bindingActivityEdit.amountEditText.text.toString()
            val id : String = bindingActivityEdit.idEditText.text.toString()
            val price : String = bindingActivityEdit.priceEditText.text.toString()
            val date : String = bindingActivityEdit.dateEditText.text.toString()
            val description: String = bindingActivityEdit.descriptionEditText.text.toString()

            val folder: StorageReference = FirebaseStorage.getInstance().reference.child("inventory")
            val InventoryReference : StorageReference = folder.child("img$key")

            if(fileUri==null){
                val mInventory = Inventory(id, name, amount, date, price, description, imageUrl)
                myRef.setValue(mInventory)
            } else {
                InventoryReference.putFile(fileUri!!).addOnSuccessListener {
                    InventoryReference.downloadUrl.addOnSuccessListener { uri ->
                        val mInventory = Inventory(id, name, amount, date, price, description, uri.toString())
                        myRef.setValue(mInventory)
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