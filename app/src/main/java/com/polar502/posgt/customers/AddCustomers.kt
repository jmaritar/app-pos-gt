package com.polar502.posgt.customers

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.polar502.posgt.R
import com.polar502.posgt.databinding.ActivityAddBinding
import com.polar502.posgt.inventory.VideoGame

class AddCustomers : AppCompatActivity() {


    //Variable para instanciar los componentes de la interfaz add
    private lateinit var bindingActivityAdd: ActivityAddBinding
    private val database = Firebase.database
    private val myRef = database.getReference("game")
    private val file = 1

    //Imagenes de URL
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingActivityAdd = ActivityAddBinding.inflate(layoutInflater)
        val view = bindingActivityAdd.root
        setContentView(view)

        bindingActivityAdd.saveButton.setOnClickListener {

            val name : String = bindingActivityAdd.nameEditText.text.toString()
            val date : String = bindingActivityAdd.dateEditText.text.toString()
            val price : String = bindingActivityAdd.priceEditText.text.toString()
            val description: String = bindingActivityAdd.descriptionEditText.text.toString()
            val key: String = myRef.push().key.toString()
            val folder: StorageReference = FirebaseStorage.getInstance().reference.child("game")
            val videoGameReference : StorageReference = folder.child("img$key")

            if(fileUri==null){
                val mVideoGame = VideoGame(name, date, price, description)
                myRef.child(key).setValue(mVideoGame)
            } else {
                videoGameReference.putFile(fileUri!!).addOnSuccessListener {
                    videoGameReference.downloadUrl.addOnSuccessListener { uri ->
                        val mVideoGame = VideoGame(name, date, price, description, uri.toString())
                        myRef.child(key).setValue(mVideoGame)
                    }
                }
            }

            finish()
        }

        bindingActivityAdd.posterImageView.setOnClickListener {
            fileUpload()
        }
    }
    //Función Upload
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