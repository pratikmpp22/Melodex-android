package com.pratik.music_find_app;




import android.app.ActionBar.LayoutParams
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pratik.music_find_app.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {


    private  var data: Intent? = null
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var isServiceStarted = false
    private lateinit var activityMainBinding : ActivityMainBinding
    val database = FirebaseDatabase.getInstance()
    val secretKeysRef = database.getReference("secret_keys")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(null)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        if(checkOverlayPermission())
        {
            activityMainBinding.btnCasting.setOnClickListener {
                if(isRecordAudioPermissionGranted())
                {
                    activityMainBinding.btnCasting.setBackgroundColor(Color.GRAY)
                    activityMainBinding.btnCasting.isEnabled = false
                    activityMainBinding.btnCasting.text = "Switch to other apps!"
                }
                startCapturing()
            }
        }
        else
        {
            val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivity(myIntent)
            Toast.makeText(this,"Reopen the app, once permitted",Toast.LENGTH_LONG).show()
            finish()
        }

        activityMainBinding.insta.setOnClickListener {
            val uri = Uri.parse("https://instagram.com/pratik___841?igshid=NGExMmI2YTkyZg==")
            val likeIng = Intent(Intent.ACTION_VIEW, uri)
            likeIng.setPackage("com.instagram.android")

            try {
                startActivity(likeIng)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://instagram.com/pratik___841?igshid=NGExMmI2YTkyZg==")))
            }
        }
        activityMainBinding.git.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/pratikmpp22"))
            val packageManager = this.packageManager
            val list = packageManager?.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY)
            if (list != null) {
                if (list.isEmpty()) {
                    intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/pratikmpp22"))
                }
            }
            startActivity(intent)
        }
        activityMainBinding.linked.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/pratik-patil-ba8b36193/"))
            val packageManager = this.packageManager
            val list = packageManager?.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY)
            if (list != null) {
                if (list.isEmpty()) {
                    intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.linkedin.com/in/pratik-patil-ba8b36193/"))
                }
            }
            startActivity(intent)
        }
    }
    private fun startCapturing() {
        if (!isRecordAudioPermissionGranted()) {
            requestRecordAudioPermission()
        } else {
            startMediaProjectionRequest()
        }
    }

    private fun startMediaProjectionRequest() {
        mediaProjectionManager =
            applicationContext.getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(
            mediaProjectionManager.createScreenCaptureIntent(),
            MEDIA_PROJECTION_REQUEST_CODE
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        isServiceStarted = true
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MEDIA_PROJECTION_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    startOverlayService(data!!)
                } else {
                    activityMainBinding.btnCasting.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500))
                    activityMainBinding.btnCasting.background = ContextCompat.getDrawable(this, R.drawable.card_border)
                    activityMainBinding.btnCasting.isEnabled = true
                    activityMainBinding.btnCasting.text = "ALLOW CASTING PERMISSION"
                    Toast.makeText(this,"Screen casting permission request rejected!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // method for starting the service
    private  fun startOverlayService(data:Intent) {
            // start the service based on the android version
        Toast.makeText(this@MainActivity,"Starting service, Please wait",Toast.LENGTH_SHORT).show()
        secretKeysRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val accessKey = dataSnapshot.child("access_key").getValue(String::class.java)
                val host = dataSnapshot.child("host").getValue(String::class.java)
                val secretKey = dataSnapshot.child("secret_key").getValue(String::class.java)

                val serviceintent = Intent(this@MainActivity,ForegroundService::class.java).apply {
                    action = ForegroundService.ACTION_START
                    putExtra(ForegroundService.EXTRA_RESULT_DATA, data)
                    putExtra("host",host)
                    putExtra("accessKey",accessKey)
                    putExtra("secretKey",secretKey)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceintent)
                } else {
                    startService(serviceintent)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity,"Failed to start service",Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun checkOverlayPermission() : Boolean{
        return Settings.canDrawOverlays(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, ForegroundService::class.java))
    }


    companion object {
        private const val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 42
        private const val MEDIA_PROJECTION_REQUEST_CODE = 13
    }

    private fun requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.RECORD_AUDIO),
            RECORD_AUDIO_PERMISSION_REQUEST_CODE
        )
    }

    private fun isRecordAudioPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "Permissions granted, click on Allow Casting now!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this, "Permissions to capture audio denied.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}