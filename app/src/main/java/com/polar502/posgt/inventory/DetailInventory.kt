package com.polar502.posgt.inventory

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.polar502.posgt.R
import com.polar502.posgt.databinding.ActivityDetailBinding
import com.polar502.posgt.databinding.ActivityDetailInventoryBinding

class DetailInventory : AppCompatActivity() {

    private lateinit var bindingActivityDetail: ActivityDetailInventoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingActivityDetail = ActivityDetailInventoryBinding.inflate(layoutInflater)
        val view = bindingActivityDetail.root
        setContentView(view)

        val key = intent.getStringExtra("key")
        val database = Firebase.database
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val myRef = database.getReference("inventory").child(
            key.toString()
        )

        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val mInventory: Inventory? = dataSnapshot.getValue(Inventory::class.java)
                if (mInventory != null) {
                    bindingActivityDetail.nameTextView.text = mInventory.name.toString()
                    bindingActivityDetail.priceTextView.text = "Q " + mInventory.price.toString()
                    bindingActivityDetail.idTextView.text = mInventory.id.toString()
                    bindingActivityDetail.amountTextView.text = "Stock: " + mInventory.amount.toString()
                    bindingActivityDetail.dateTextView.text = mInventory.date.toString()
                    bindingActivityDetail.descriptionTextView.text = mInventory.description.toString()
                    Glide.with(view)
                        .load(mInventory.url.toString())
                        .into(bindingActivityDetail.posterImgeView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

}