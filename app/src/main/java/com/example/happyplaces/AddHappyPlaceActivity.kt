package com.example.happyplaces

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.karumi.dexter.Dexter
import java.text.SimpleDateFormat
import java.util.*
import com.karumi.dexter.PermissionToken

import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.PermissionRequest

import com.karumi.dexter.listener.multi.MultiplePermissionsListener




class AddHappyPlaceActivity : AppCompatActivity() , View.OnClickListener{
    private var binding:ActivityAddHappyPlaceBinding?=null        //binding
    private var cal=Calendar.getInstance()                // calander object
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        /** code for backpressed button**/
        setSupportActionBar(binding?.toolbarAddPlace)
         supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }

        /** Date set listner for datePickerdialog**/
        dateSetListener=DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
          cal.set(Calendar.YEAR,year)
          cal.set(Calendar.MONTH,month)
          cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
          updatedayinview()            // update date function

      }

        /** ON CLICK EVENTS **/
        binding?.etDate?.setOnClickListener(this)
        binding?.tvAddImage?.setOnClickListener(this)
    }


    /** ON CLICK FUNCTION **/
    override fun onClick(v: View?) {
      when(v)
      {
          binding?.etDate->{
              /** DATE PICKER DIALOG */
              DatePickerDialog(this@AddHappyPlaceActivity,
                  dateSetListener,
                  cal.get(Calendar.YEAR),
                  cal.get(Calendar.MONTH),
                  cal.get(Calendar.DAY_OF_MONTH )).show()
          }


          binding?.tvAddImage->{
              /** PICTURE TAKING FUNCITON  **/
              val picturedialogitems=arrayOf(" select image from galary ","select image from camera")
              val picturedialog=AlertDialog.Builder(this)
                  .setTitle("select action")
                  .setItems(picturedialogitems)
                  {
                      dialog,which->
                      when(which)
                      {
                          0-> choosePhotoFromGallary()  // CHOOSING PHOTO FORM GALLARY FUNCITON
                          1->Toast.makeText(this@AddHappyPlaceActivity,"camera comming soon ",Toast.LENGTH_SHORT).show()
                      }
                  }
                  .show()
          }

      }
    }

    private fun choosePhotoFromGallary() {
        /** PERMISSION HANDLING FOR GALLARY */
       Dexter.withActivity(this@AddHappyPlaceActivity).
       withPermissions( Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
           .withListener(object :MultiplePermissionsListener{
               /** if the permission is granted or checked true */
               override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                      if(p0!!.areAllPermissionsGranted())
                      {
                          Toast.makeText(this@AddHappyPlaceActivity,"permission granted  ",Toast.LENGTH_SHORT).show()
                      }
               }
               /** if the permission is not granted and denied from the options */
               override fun onPermissionRationaleShouldBeShown(
                   p0: MutableList<PermissionRequest>?,
                   p1: PermissionToken?
               ) {
                   showRationalDialogPermission()          //rational dialog permission functions
               }
           } ).onSameThread().check()
    }

    private fun showRationalDialogPermission() {
      AlertDialog.Builder(this@AddHappyPlaceActivity).setMessage("it looks like u have turned off permissions it can be turned on by settings ")
          .setPositiveButton("GOTO SEETIONGS ")     /** ON POSITIVE BUTTON CLICK*/
          {_,_,->
              try{
                  /** SENDITNG USER TO SEETINGS TO GET THE PERMISSIONS */
                  val intent= Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                  val uri= Uri.fromParts("package",packageName,null)
                  intent.data=uri
                  startActivity(intent)
              }
              catch (e:ActivityNotFoundException)
              {
                  e.printStackTrace()
              }

          }
          .setNegativeButton("Cancle")           /** ON NEGATIVE BUTTON CLICK */
          {
                   dialog,which->
              dialog.dismiss()
          }
          .show()
    }


     /** FUNCTION TO GET DATE IN DESIRED FORMAT FROM THE CALENDER OBJECT */
    private fun updatedayinview()
    {
        val myformat="dd.MM.yyyy"
        val sdf=SimpleDateFormat(myformat,Locale.getDefault())
        binding?.etDate?.setText(sdf.format(cal.time).toString())
    }
}

