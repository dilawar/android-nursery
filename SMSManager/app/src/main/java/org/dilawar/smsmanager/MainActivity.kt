package org.dilawar.smsmanager

import android.content.pm.PackageManager
import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.dilawar.smsmanager.databinding.ActivityMainBinding

const val TAG: String = "main"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var checkSMSPermission: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //  Aks for sms permissions.
        checkSMSPermission = registerForActivityResult( ActivityResultContracts.RequestPermission() ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    Log.i(TAG, "Permission granted.")
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Log.w(TAG, "Permission is not granted!")
                }
            }

        this.readSMS()
    }

    fun readSMS() {
        this.checkSMSPermission.launch(Manifest.permission.READ_SMS)
        Log.i(TAG, "Reading SMS from INBOX.")
        val cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null)
        if (cursor!!.moveToFirst()) { // must check the result to prevent exception
            do {
                var msgData = ""
                for (idx in 0..<cursor!!.columnCount) {
                    msgData += " " + cursor!!.getColumnName(idx) + ":" + cursor!!.getString(idx)
                }
                // use msgData
                Log.i(TAG, "SMS: $msgData")
            } while (cursor!!.moveToNext())
        } else {
            // empty box, no SMS
        }
    }
}