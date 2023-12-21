package com.example.musicapp.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.musicapp.R


class SettingFragment: Fragment(R.layout.fragment_setting) {
    private lateinit var tvEmail: TextView
    private lateinit var btnExpand: ImageButton
    private lateinit var service: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        tvEmail = view.findViewById(R.id.tv_email)
        btnExpand = view.findViewById(R.id.btn_expand)
        service = view.findViewById(R.id.tv_service_paragraph)

        tvEmail.setOnClickListener{
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:${tvEmail.text}") // Specify the email address

            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject") // Specify the email subject if needed

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
        }

        service.text = "Last Updated: [Date]\n" +
                "\n" +
                "Please read these terms of service (\"Terms\") carefully before using [Your Music App Name] (the \"Service\") operated by [Your Company Name] (\"us\", \"we\", or \"our\").\n" +
                "\n" +
                "1. Acceptance of Terms\n" +
                "By accessing or using the Service, you agree to be bound by these Terms. If you disagree with any part of the terms, then you may not access the Service.\n" +
                "\n" +
                "2. Use of the Service\n" +
                "a. Eligibility: You must be at least [age] years old to use this Service. By using the Service, you represent and warrant that you are at least [age] years old.\n" +
                "\n" +
                "b. User Accounts: You may be required to create an account to use certain features of the Service. You are responsible for maintaining the confidentiality of your account and password. You agree to accept responsibility for all activities that occur under your account.\n" +
                "\n" +
                "c. Prohibited Activities: You agree not to engage in any of the following activities:\n" +
                "\n" +
                "Violating any applicable laws or regulations.\n" +
                "Infringing on the rights of others.\n" +
                "Uploading or transmitting viruses or any other malicious code.\n" +
                "Collecting or storing personal data of other users.\n" +
                "3. Content\n" +
                "a. User-Generated Content: You may have the ability to submit, upload, or post content to the Service (\"User Content\"). You retain ownership of your User Content, and by submitting it, you grant us a worldwide, non-exclusive, royalty-free license to use, reproduce, distribute, and display your User Content.\n" +
                "\n" +
                "b. Copyright Policy: We respect the intellectual property rights of others. If you believe that your work has been copied in a way that constitutes copyright infringement, please contact us.\n" +
                "\n" +
                "4. Privacy\n" +
                "Our Privacy Policy [link to your privacy policy] explains how we collect, use, and disclose information about you. By using the Service, you agree to our use of your data in accordance with our Privacy Policy.\n" +
                "\n" +
                "5. Termination\n" +
                "We may terminate or suspend access to our Service immediately, without prior notice or liability, for any reason whatsoever, including without limitation if you breach the Terms.\n" +
                "\n" +
                "6. Changes to Terms\n" +
                "We reserve the right to update or change these Terms at any time. Your continued use of the Service after we post any modifications to the Terms on this page will constitute your acknowledgment of the modifications and your consent to abide and be bound by the modified Terms.\n" +
                "\n" +
                "7. Contact Us\n" +
                "If you have any questions about these Terms, please contact us at [your contact email].\n" +
                "\n" +
                "By using the Service, you agree to these Terms. If you do not agree to these Terms, please do not use the Service."
        return view
    }
}