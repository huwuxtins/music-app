package com.example.musicapp.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.musicapp.R
import com.example.musicapp.adapters.AccountSettingViewPagerAdapter
import com.example.musicapp.dialog.LoadingDialog
import com.facebook.FacebookSdk
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import java.io.File
import java.util.logging.Logger.global


class AccountSettingFragment() : Fragment() {
    private lateinit var imgAvatar: ImageView
    private lateinit var tvName: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var vpEdit: ViewPager2
    private lateinit var editImage : ImageView
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var dialog : LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit, container, false)
        imgAvatar = view.findViewById(R.id.imgAvatar)
        tvName = view.findViewById(R.id.tvName)
        tabLayout = view.findViewById(R.id.tabLayout)
        vpEdit = view.findViewById(R.id.vpEdit)
        editImage = view.findViewById(R.id.editImage)
        auth = FirebaseAuth.getInstance()
        db =FirebaseFirestore.getInstance()
        dialog = LoadingDialog(requireActivity())

        val fragmentList = listOf(EditInformationFragment(), ChangePasswordFragment())

        vpEdit.adapter = activity?.let {
            AccountSettingViewPagerAdapter(fragmentList,it.supportFragmentManager,lifecycle)
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    vpEdit.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        editImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 11)
        }

        updateUI()

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 11 && resultCode == Activity.RESULT_OK){
            dialog.ShowDialog("Upload Avatar")
            val file_uri = data?.data
            val fileName = getNameFromURI(file_uri)
            val refStorage = FirebaseStorage.getInstance().reference.child("users/$fileName")
            if (file_uri != null) {
                refStorage.putFile(file_uri)
                    .addOnSuccessListener(
                        OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                            taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                                val imageUrl = it.toString()
                                val fUser = auth.currentUser
                                val email = fUser?.email.toString()
                                val docRef = db.collection("Users").document(email)

                                docRef.update("avatar",imageUrl)
                                    .addOnSuccessListener {
                                        dialog.HideDialog()
                                        Toast.makeText(context,"Edit avatar successfully",Toast.LENGTH_SHORT).show()
                                        imgAvatar.setImageURI(file_uri)
                                    }
                                    .addOnFailureListener{
                                        dialog.HideDialog()
                                        Toast.makeText(context,"Edit avatar failed",Toast.LENGTH_SHORT).show()
                                    }

                            }
                        })

                    ?.addOnFailureListener(OnFailureListener { e ->
                        dialog.HideDialog()
                        Toast.makeText(context,"Edit avatar failed",Toast.LENGTH_SHORT).show()
                    })
            }
        }

    }


    @SuppressLint("Range")
    fun getNameFromURI(uri: Uri?): String? {
        val c: Cursor? =
            uri?.let { FacebookSdk.getApplicationContext().contentResolver.query(it, null, null, null, null) }
        if (c != null) {
            c.moveToFirst()
            return c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
        return c;
    }


    fun updateUI(){
        val fUser = auth.currentUser
        val email = fUser?.email.toString()
        val docRef = db.collection("Users").document(email)


        docRef.get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(com.example.musicapp.models.User::class.java)
            tvName.text = user?.username
            val linkImg = user?.avatar.toString()
            if(linkImg.contains("https://")){
                Picasso.get().load(linkImg).into(imgAvatar);
            }
        }

    }

}