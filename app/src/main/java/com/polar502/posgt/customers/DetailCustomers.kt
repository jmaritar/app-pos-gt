package com.polar502.posgt.customers

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.polar502.posgt.databinding.ActivityDetailCustomersBinding

class DetailCustomers : AppCompatActivity() {

    private lateinit var bindingActivityDetail: ActivityDetailCustomersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingActivityDetail = ActivityDetailCustomersBinding.inflate(layoutInflater)
        val view = bindingActivityDetail.root
        setContentView(view)

        val key = intent.getStringExtra("key")
        val database = Firebase.database
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val myRef = database.getReference("customers").child(
            key.toString()
        )

        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val mCustomers: Customers? = dataSnapshot.getValue(Customers::class.java)
                if (mCustomers != null) {
                    bindingActivityDetail.idTextView.text = "" + mCustomers.id.toString()
                    bindingActivityDetail.nameTextView.text = mCustomers.name.toString()
                    bindingActivityDetail.nitTextView.text = mCustomers.nit.toString()
                    bindingActivityDetail.phoneTextView.text = "" + mCustomers.phone.toString()
                    bindingActivityDetail.emailTextView.text = "" + mCustomers.email.toString()
                    bindingActivityDetail.addressTextView.text = "" + mCustomers.address.toString()
                    Glide.with(view)
                        .load(mCustomers.url.toString())
                        .into(bindingActivityDetail.posterImgeView)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }
}