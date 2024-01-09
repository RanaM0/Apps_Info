package com.champstech.appsinfo.Fragments

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import com.champstech.appsinfo.R
import com.champstech.appsinfo.databinding.FragmentSettingsBinding


class SettingsFrag : Fragment() {


    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    private lateinit var ratingDialog: Dialog
    private lateinit var feedbackDialog: Dialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.txtSharebtn.setOnClickListener {
            shareContent()

        }

        binding.txtRateUsbtn.setOnClickListener {

            rateUsContent()

        }

        return binding.root
    }

    private fun rateUsContent() {

        // Initialize rating dialog
        ratingDialog = Dialog(requireContext())
        ratingDialog.setContentView(R.layout.rate_us_dialogue)

        val ratingBar= ratingDialog.findViewById<RatingBar>(R.id.customRatingBar)
        val proceedBtn = ratingDialog.findViewById<Button>(R.id.btnRateProceed)
        val cancelBtn = ratingDialog.findViewById<Button>(R.id.btnRateCancel)


        ratingDialog.setCanceledOnTouchOutside(false)


        proceedBtn?.setOnClickListener {
            val rating = ratingBar?.rating
            if (rating!! <= 4) {
                // Show feedback dialog
                ratingDialog.dismiss()
                showFeedbackDialog()

            } else {
                // Redirect to Play Store
                redirectToPlayStore()
                ratingDialog.dismiss()
            }

        }

        cancelBtn?.setOnClickListener {
            ratingDialog.dismiss()

        }
        ratingDialog.show()

    }

    private fun showFeedbackDialog() {

        feedbackDialog = Dialog(requireContext())
        feedbackDialog.setContentView(R.layout.feedback_dialogue)

        // Initialize feedback dialog components
        // Add your UI components and logic here

        val cancelFeedBtn = feedbackDialog.findViewById<Button>(R.id.btnFeedbackCancel)
        val sendFeedBtn = feedbackDialog.findViewById<Button>(R.id.btnFeedbackSend)
        val etFeedback = feedbackDialog.findViewById<EditText>(R.id.etFeedback)

        feedbackDialog.setCanceledOnTouchOutside(false)

        cancelFeedBtn.setOnClickListener {
            feedbackDialog.dismiss()


        }
        sendFeedBtn.setOnClickListener {

            val txtfromEt = etFeedback.text.toString()

            if (etFeedback.text.isEmpty()) {

                etFeedback.error = "Please write feedback to proceed"
            } else {

                Toast.makeText(requireContext(), "Feedback Sent", Toast.LENGTH_SHORT).show()
                feedbackDialog.dismiss()
            }


        }

        feedbackDialog.show()
    }


    private fun redirectToPlayStore() {
        // Redirect to the Play Store
        // Example code to open the app page on the Play Store
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://play.google.com/store/games?hl=en&gl=US&pli=1")
        startActivity(intent)
    }


    private fun shareContent() {
        // Create an Intent with ACTION_SEND
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Sharing Example") // Optional subject
            putExtra(Intent.EXTRA_TEXT, "Check out this awesome fragment!") // Content to share
        }

        // Create a chooser to allow the user to pick the sharing app
        val shareIntent = Intent.createChooser(sendIntent, null)

        // Check if there are apps that can handle the share intent
        if (sendIntent.resolveActivity(requireActivity().packageManager) != null) {
            // Start the chosen sharing app
            startActivity(shareIntent)
        } else {
            // Handle the case where no apps can handle the share intent
            // (You can show a message or take other actions)
            Toast.makeText(
                requireContext(),
                "No app available to handle the share action",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}