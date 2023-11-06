package com.example.musicapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.musicapp.R
import com.google.android.material.textfield.TextInputEditText

class ChangePasswordFragment(): Fragment() {
    private lateinit var inpCurrPass: TextInputEditText
    private lateinit var inpNewPass: TextInputEditText
    private lateinit var inpConfPass: TextInputEditText
    private lateinit var btnSave: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_change_password, container, false)
        inpCurrPass = view.findViewById(R.id.inpCurrPass)
        inpNewPass = view.findViewById(R.id.inpNewPass)
        inpConfPass = view.findViewById(R.id.inpConfPass)
        btnSave = view.findViewById(R.id.btnSave)
        return view
    }
}