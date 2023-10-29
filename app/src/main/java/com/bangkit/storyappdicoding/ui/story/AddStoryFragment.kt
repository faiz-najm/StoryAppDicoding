package com.bangkit.storyappdicoding.ui.story

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyappdicoding.R
import com.bangkit.storyappdicoding.data.ApiStatus
import com.bangkit.storyappdicoding.databinding.FragmentAddStoryBinding
import com.bangkit.storyappdicoding.getImageUri
import com.bangkit.storyappdicoding.reduceFileImage
import com.bangkit.storyappdicoding.uriToFile
import com.bangkit.storyappdicoding.viewmodels.AddStoryViewModel
import com.bangkit.storyappdicoding.viewmodels.ViewModelFactory
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Locale

class AddStoryFragment : Fragment() {

    private val viewModel: AddStoryViewModel by lazy {
        val factory = ViewModelFactory.getInstance(requireContext())
        ViewModelProvider(this, factory)[AddStoryViewModel::class.java]
    }

    private var _binding: FragmentAddStoryBinding? = null

    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null
    private var isLocation = false
    private lateinit var locationRequest: LocationRequest
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.CAMERA] ?: false -> {
                    Toast.makeText(
                        requireContext(),
                        "Permission request granted",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }

                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                else -> {
                    Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUEST_PERMISSION)
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener { uploadImage() }
        binding.themeInp.setOnClickListener {
            createLocationRequest()
        }

        viewModel.uploadResultLiveData.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is ApiStatus.Loading -> {
                        showLoading(true)
                    }

                    is ApiStatus.Success -> {
                        showLoading(false)
                        // restart activity
                        startActivity(requireActivity().intent)
                        requireActivity().finish()
                        showToast(result.data.message)
                    }

                    is ApiStatus.Error -> {
                        showLoading(false)
                        showToast(result.error)
                    }
                }
            }
        }

        return binding.root
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private val resolutionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            when (result.resultCode) {
                AppCompatActivity.RESULT_OK ->
                    Log.i(TAG, "onActivityResult: All location settings are satisfied.")

                AppCompatActivity.RESULT_CANCELED ->
                    Toast.makeText(
                        requireContext(),
                        "Anda harus mengaktifkan GPS untuk menggunakan aplikasi ini!",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(100, 10000).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            getMyLastLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    resolutionLauncher.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "createLocationRequest: Error")
                }
            }
        }
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                    if (isLocation) {
                        isLocation = false
                        binding.themeHint.hint = getString(R.string.location)
                    } else {
                        try {
                            val address = getGeoCoder(
                                currentLocation.latitude,
                                currentLocation.longitude
                            )?.get(0)?.getAddressLine(0)
                            binding.themeHint.hint = address
                            // change color hint to blue
                            binding.themeHint.hintTextColor = ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.teal_200
                                )
                            )
                            isLocation = true
                        } catch (e: Exception) {
                            Log.d(TAG, "getMyLastLocation: Error")
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun getGeoCoder(lat: Double, lon: Double): MutableList<Address>? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(
            lat,
            lon,
            1
        )
        return addresses
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edAddDescription.text.toString()

            showLoading(true)

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            if (!isLocation) {
                viewModel.uploadStory(multipartBody, requestBody)
            } else {
                viewModel.uploadStoryWithLocation(multipartBody, requestBody, currentLocation.latitude.toFloat(), currentLocation.longitude.toFloat())
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION =
            Manifest.permission.CAMERA + Manifest.permission.ACCESS_FINE_LOCATION + Manifest.permission.ACCESS_COARSE_LOCATION

        // request permission
        private val REQUEST_PERMISSION = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        private const val TAG = "MapsActivity"
    }
}