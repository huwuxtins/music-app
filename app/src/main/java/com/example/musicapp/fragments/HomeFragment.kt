package com.example.musicapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.adapters.TypeAdapter
import com.example.musicapp.databinding.ActivityMainBinding
import com.example.musicapp.models.Type

class HomeFragment(): Fragment(R.layout.fragment_home) {

    lateinit var listType : ArrayList<Type>
    lateinit var recyclerView : RecyclerView
    lateinit var typeAdpater : TypeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        listType = createList()
        recyclerView =  view.findViewById(R.id.listKind)
        typeAdpater = activity?.let { TypeAdapter(it.applicationContext,listType) }!!
        recyclerView.adapter = typeAdpater
        recyclerView!!.layoutManager = LinearLayoutManager(requireActivity().applicationContext, LinearLayoutManager.HORIZONTAL, false)
        return view
    }



    private fun createList() : ArrayList<Type>{
        val l : ArrayList<Type> = ArrayList<Type>()

        l.add(Type("Ballad","#0075FF"))
        l.add(Type("POP","#FF00E5"))
        l.add(Type("EDM","#35BB52"))
        l.add(Type("K-POP","#E65880"))
        l.add(Type("US-UK","#1FB6BA"))
        l.add(Type("C-POP","#20DB8A"))
        return l
    }




}