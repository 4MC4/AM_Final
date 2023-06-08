package com.android.example.automation_control_helper

import android.os.Bundle
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class FragmentB : Fragment() {
    private lateinit var a1: EditText
    private lateinit var a0: EditText
    private lateinit var b1: EditText
    private lateinit var b0: EditText
    private lateinit var button: Button
    private lateinit var textViewSum: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_b, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        a1 = view.findViewById(R.id.a1)
        a0 = view.findViewById(R.id.a0)
        b1 = view.findViewById(R.id.b1)
        b0 = view.findViewById(R.id.b0)
        button = view.findViewById(R.id.button)
        textViewSum = view.findViewById(R.id.textViewSum)

        // Obsługa przycisku Calculate
        button.setOnClickListener {
            // Pobierz wartości wpisane przez użytkownika
            val a1_val = a1.text.toString().toDouble()
            val a0_val = a0.text.toString().toDouble()
            val b1_val = b1.text.toString().toDouble()
            val b0_val = b0.text.toString().toDouble()
            // Wykonaj obliczenia
            val sum = a1_val + a0_val

            // Wyświetl sumę poniżej przycisku
            textViewSum.text = "Sum: $sum"

            hideKeyboard()
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}