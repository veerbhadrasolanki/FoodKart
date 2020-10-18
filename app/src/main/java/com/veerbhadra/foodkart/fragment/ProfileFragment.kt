package com.veerbhadra.foodkart.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.veerbhadra.foodkart.R

class ProfileFragment : Fragment() {
    lateinit var currentProfile: RelativeLayout
    lateinit var textName: TextView
    lateinit var textMobileNumber: TextView
    lateinit var textEmail: TextView
    lateinit var textAddress: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        currentProfile = view.findViewById(R.id.currentUser)
        textName = view.findViewById(R.id.txtName)
        textMobileNumber = view.findViewById(R.id.txtMobileNumber)
        textEmail = view.findViewById(R.id.txtEmail)
        textAddress = view.findViewById(R.id.txtAddress)

        sharedPreferences = activity!!.getSharedPreferences(
            getString(R.string.pref_file_name),
            Context.MODE_PRIVATE
        )

        textName.text = sharedPreferences.getString("name", "")
        textEmail.text = sharedPreferences.getString("email", "")
        textMobileNumber.text = "+91-" + sharedPreferences.getString("mobile_number", "")
        textAddress.text = sharedPreferences.getString("address", "")

        return view
    }


}