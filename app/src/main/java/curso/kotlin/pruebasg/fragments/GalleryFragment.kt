package curso.kotlin.pruebasg.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import curso.kotlin.pruebasg.PruebaApplication
import curso.kotlin.pruebasg.R
import curso.kotlin.pruebasg.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {
    private lateinit var binding: FragmentGalleryBinding
    private lateinit var mStorageReference: StorageReference
    private lateinit var mDatabaseReference: DatabaseReference

    private var mPhotoSelectedUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFirebase()
        setupButtons()
    }

    private fun setupFirebase() {
        mStorageReference =
            FirebaseStorage.getInstance().reference.child(PruebaApplication.PATH_PRUEBA_SG)
        mDatabaseReference =
            FirebaseDatabase.getInstance().reference.child(PruebaApplication.PATH_PRUEBA_SG)
    }

    private fun setupButtons() {
        with(binding) {
            btnUpload.setOnClickListener {
                if (btnUpload.text == getString(R.string.btn_clean)) {
                    tvMessage.text = getString(R.string.gallery_message_title)
                    tilCommentary.visibility = View.INVISIBLE
                    edtCommentary.setText("")
                    imgPhoto.setImageDrawable(null)
                    btnUpload.text = getString(R.string.btn_upload)
                } else
                    postPhoto()
            }

            btnSelectPhoto.setOnClickListener { opnGallery() }
        }
    }

    private fun opnGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PruebaApplication.RC_GALLERY)
    }

    private fun postPhoto() {
        if (mPhotoSelectedUri != null) {
            enableUI(false)
            binding.progressBar.visibility = View.VISIBLE

            val key = mDatabaseReference.push().key!!
            val mStorageRef = mStorageReference.child(PruebaApplication.currentUser.uid)
                .child(key)

            mStorageRef.putFile(mPhotoSelectedUri!!)
                .addOnProgressListener {
                    val progress = (100 * it.bytesTransferred / it.totalByteCount).toInt()

                    with(binding) {
                        progressBar.progress = progress
                        tvMessage.text = String.format("%s%%", progress)
                    }
                }
                .addOnCompleteListener {
                    binding.progressBar.visibility = View.INVISIBLE
                }
                .addOnSuccessListener {
                    binding.tvMessage.text = getString(R.string.gallery_message_success)
                    binding.btnUpload.text = getString(R.string.btn_clean)
                    enableUI(true)
                }
                .addOnFailureListener {
                    Toast.makeText(context, R.string.post_message_image_fail, Toast.LENGTH_SHORT)
                        .show()
                    enableUI(true)
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PruebaApplication.RC_GALLERY) {
                mPhotoSelectedUri = data?.data

                with(binding) {
                    imgPhoto.setImageURI(mPhotoSelectedUri)
                    tvMessage.text = getString(R.string.gallery_message_title_instruction)
                    tilCommentary.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun enableUI(enable: Boolean) {
        with(binding) {
            btnUpload.isEnabled = enable
            btnSelectPhoto.isEnabled = enable
            tilCommentary.isEnabled = enable
        }
    }
}