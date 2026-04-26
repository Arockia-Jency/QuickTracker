package com.example.quicktasker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.quicktasker.data.ProfileStore
import com.example.quicktasker.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private var selectedPhotoUri: Uri? = null

    private val photoPicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri == null) return@registerForActivityResult

            try {
                requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Some providers don't allow persistable permissions; still try to use the URI.
            }

            selectedPhotoUri = uri
            binding.editProfileImageView.setImageURI(uri)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditProfileBinding.bind(view)

        ProfileStore.profile.value?.let { profile ->
            binding.nameEditText.setText(profile.name)
            binding.titleEditText.setText(profile.title)
            val uri = profile.photoUri?.let(Uri::parse)
            selectedPhotoUri = uri
            if (uri != null) binding.editProfileImageView.setImageURI(uri)
            else binding.editProfileImageView.setImageResource(R.drawable.ic_user_profile)
        }

        binding.changePhotoButton.setOnClickListener {
            photoPicker.launch(arrayOf("image/*"))
        }

        binding.saveProfileButton.setOnClickListener {
            val name = binding.nameEditText.text?.toString().orEmpty()
            val title = binding.titleEditText.text?.toString().orEmpty()
            ProfileStore.update(
                name = name,
                title = title,
                photoUri = selectedPhotoUri?.toString()
            )
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

