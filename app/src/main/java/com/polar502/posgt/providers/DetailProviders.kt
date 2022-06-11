package com.polar502.posgt.providers

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.polar502.posgt.databinding.ActivityDetailProvidersBinding

class DetailProviders : AppCompatActivity() {

        private lateinit var bindingActivityDetail: ActivityDetailProvidersBinding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            bindingActivityDetail = ActivityDetailProvidersBinding.inflate(layoutInflater)
            val view = bindingActivityDetail.root
            setContentView(view)

        val key = intent.getStringExtra("key")
        val database = Firebase.database
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val myRef = database.getReference("providers").child(
            key.toString()
        )

        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val mProviders: Providers? = dataSnapshot.getValue(Providers::class.java)
                if (mProviders != null) {
                    bindingActivityDetail.idTextView.text = "" + mProviders.id.toString()
                    bindingActivityDetail.nameTextView.text = mProviders.name.toString()
                    bindingActivityDetail.nitTextView.text = mProviders.nit.toString()
                    bindingActivityDetail.phoneTextView.text = "" + mProviders.phone.toString()
                    bindingActivityDetail.emailTextView.text = "" + mProviders.email.toString()
                    bindingActivityDetail.addressTextView.text = "" + mProviders.address.toString()
                    Glide.with(view)
                        .load(mProviders.url.toString())
                        .into(bindingActivityDetail.posterImgeView)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }
}