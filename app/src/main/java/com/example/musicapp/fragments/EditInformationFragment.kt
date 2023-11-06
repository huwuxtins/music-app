package com.example.musicapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.musicapp.R
import com.google.android.material.textfield.TextInputEditText

class EditInformationFragment(): Fragment() {
    private lateinit var inpFName: TextInputEditText
    private lateinit var inpLName: TextInputEditText
    private lateinit var inpEmail: TextInputEditText
    private lateinit var btnSave: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_information, container, false)
        inpFName = view.findViewById(R.id.inpFName)
        inpLName = view.findViewById(R.id.inpLName)
        inpEmail = view.findViewById(R.id.inpEmail)
        btnSave = view.findViewById(R.id.btnSave)
        return view
    }
}