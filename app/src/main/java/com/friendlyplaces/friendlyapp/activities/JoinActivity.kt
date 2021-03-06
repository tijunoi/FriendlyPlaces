package com.friendlyplaces.friendlyapp.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.friendlyplaces.friendlyapp.R
import com.friendlyplaces.friendlyapp.model.FriendlyUser
import com.friendlyplaces.friendlyapp.utilities.FirestoreConstants
import com.friendlyplaces.friendlyapp.utilities.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_join.*
import java.io.ByteArrayOutputStream
import java.io.IOException

class JoinActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    //FIELDS
    lateinit var friendlyUser: FriendlyUser
    var bitmap: Bitmap? = null


    var imageString: String? = null
    private lateinit var sexOrientArray: Array<String>

    private var userHasChosenNewImage: Boolean = false

    //LIFECYCLE METHODS


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)
        register_button.setOnClickListener(this)
        imageJoin.setOnClickListener(this)
        et_name.setText(FirebaseAuth.getInstance().currentUser?.displayName)
        sexOrientArray = resources.getStringArray(R.array.sexual_orientation)
        val orienAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sexOrientArray)
        sp_sex_orientation.adapter = orienAdapter
        sp_sex_orientation.onItemSelectedListener = this

    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().currentUser?.photoUrl?.let {
            Picasso.get().load(it).into(imageJoin)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 999) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                showPictureDialog()
        } else if (resultCode == RESULT_OK) {

            userHasChosenNewImage = true
            when (requestCode) {
                GET_FROM_GALLERY -> if (data != null) {
                    val contentURI = data.data
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                        bitmap?.let {
                            imageString = getStringImage(it)
                        }
                        uploadPhotoToFirebase()

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
                GET_FROM_CAMERA -> {
                    bitmap = data!!.extras!!.get("data") as Bitmap
                    bitmap?.let {
                        imageString = getStringImage(it)
                        uploadPhotoToFirebase()
                    }

                }
            }
        }
    }

    //IMPLEMENTED METHODS FROM INTERFACES

    override fun onClick(view: View?) {
        Utils.preventTwoClick(view)
        when (view?.id) {
            R.id.register_button -> {
                spin_kit_join.visibility = View.VISIBLE
                if (checkearDatosNotEmpty()) {
                    (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.windowToken, 0)
                    setProfileValuesToUser(friendlyUser)
                } else {
                    spin_kit_join.visibility = View.GONE
                    Snackbar.make(view, getString(R.string.missing_field_snackbar_message), Snackbar.LENGTH_LONG)
                            .setAction("OK", {
                            }).show()

                }
            }
            R.id.imageJoin -> {
                view.startAnimation(AlphaAnimation(1f, 0.85f))
                checkPermisionCamera()
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view?.id) {
            R.id.sp_sex_orientation -> {
                sp_sex_orientation.setSelection(position)

            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    //IMAGEN PROCESS METHODS

    fun uploadPhotoToFirebase() {
        spin_kit_join.visibility = View.VISIBLE
        val storage = FirebaseStorage.getInstance()
        val imageReference = storage.reference.child("profilePictures/" + FirebaseAuth.getInstance().currentUser?.uid + ".jpg")

        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imageReference.putBytes(data)
        uploadTask.addOnCompleteListener({
            if (it.isSuccessful) {
                setearImagenPerfilUserFirebase(it.result.downloadUrl!!)
            }
        })
    }


    fun setearImagenPerfilUserFirebase(uri: Uri) {

        val profileChangeRequest = UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build()

        FirebaseAuth.getInstance().currentUser?.updateProfile(profileChangeRequest)?.addOnCompleteListener({
            if (it.isSuccessful) {
                Picasso.get().load(FirebaseAuth.getInstance().currentUser!!.photoUrl).into(imageJoin)
                spin_kit_join.visibility = View.GONE
            }
        })
    }

    fun checkearDatosNotEmpty(): Boolean {
        val trimName = et_name.text.toString().trim { it <= ' ' }
        val trimDescription = et_description.text.toString().trim({ it <= ' ' })

        var requiredConditions = true

        if (trimName.isEmpty()) {
            et_name.error = getString(R.string.mandatory_field_error_text)
            et_name.requestFocus()
            requiredConditions = false
        }
        if (trimDescription.isEmpty()) {
            et_description.error = getString(R.string.mandatory_field_error_text)
            et_description.requestFocus()
            requiredConditions = false
        }
        if (sp_sex_orientation.selectedItemPosition == 0) {
            sp_sex_orientation.requestFocus()
            requiredConditions = false
        }
        if (FirebaseAuth.getInstance().currentUser!!.photoUrl == null) {
            requiredConditions = false
        }
        if (requiredConditions) {
            friendlyUser = FriendlyUser(FirebaseAuth.getInstance().currentUser!!.uid, trimName, trimDescription, sp_sex_orientation.selectedItem as String)

        }
        return requiredConditions
    }

    //JOIN METHODS

    private fun setProfileValuesToUser(user: FriendlyUser) {
        val profileChangeRequest = UserProfileChangeRequest.Builder()
                .setDisplayName(user.username)
                .build()

        FirebaseAuth.getInstance().currentUser?.updateProfile(profileChangeRequest)?.addOnCompleteListener({
            if (it.isSuccessful) {
                FirebaseFirestore.getInstance()
                        .collection(FirestoreConstants.COLLECTION_USERS)
                        .document(user.uid)
                        .set(user)
                        .addOnCompleteListener({
                            if (it.isSuccessful) {
                                goToHomescreen()
                                spinGone()
                            } else Snackbar.make(et_name.rootView, getString(R.string.issue_on_register), Snackbar.LENGTH_LONG).show()
                            spinGone()
                        })
            } else {
                spinGone()
            }
        })
    }

    private fun goToHomescreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun spinGone() {
        spin_kit_join.visibility = View.GONE
    }
    //UTIL METHODS

    private fun checkPermisionCamera() {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 999)

        } else {
            showPictureDialog()
        }
    }

    private fun showPictureDialog() {

        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle(getString(R.string.picture_dialog_title))
        val pictureDialogItems = resources.getStringArray(R.array.picture_dialog_options)

        pictureDialog.setItems(pictureDialogItems) { _, which ->
            when (which) {
                1 -> choosePhotoFromGallery()
                0 -> takePhotoFromCamera()
            }
        }

        pictureDialog.show()
    }

    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GET_FROM_GALLERY)
    }


    private fun takePhotoFromCamera() {
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, GET_FROM_CAMERA)
    }

    private fun getStringImage(bmp: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    companion object {
        const val GET_FROM_GALLERY = 3
        const val GET_FROM_CAMERA = 4
    }

}





