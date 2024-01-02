package com.kelompokNizarBersaudara.krambilsawit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kelompokNizarBersaudara.krambilsawit.databinding.ActivityMainBinding
import com.kelompokNizarBersaudara.krambilsawit.extensions.Extensions.toast
import com.kelompokNizarBersaudara.krambilsawit.utils.FirebaseUtils
import com.kelompokNizarBersaudara.krambilsawit.utils.FirebaseUtils.firebaseAuth
import com.kelompokNizarBersaudara.krambilsawit.utils.FirebaseUtils.firebaseUser

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    public override fun onStart() {
        super.onStart()

        if (firebaseUser == null) {
            goToSignInActivity()
        }
    }

    private fun setupNavigation() {
        setSupportActionBar(binding.appBarMain.toolbar)

        drawerLayout = binding.drawerLayout
        navigationView = binding.navView

        val headerNav: View = navigationView.getHeaderView(0)
        val userName = headerNav.findViewById<TextView>(R.id.name_header_drawer)
        val userEmail = headerNav.findViewById<TextView>(R.id.email_header_drawer)
        val userPhoto = headerNav.findViewById<ShapeableImageView>(R.id.image_header_drawer)

        val photo: String? = getPhotoUrl()
        if (photo !== null) {
            loadImageIntoView(userPhoto, photo, true)
        }

        userName.text = getUserName()
        userEmail.text = getEmail()

        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            navController.graph, drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navigationView.setupWithNavController(navController)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout_item -> {
                if (firebaseUser != null) {
                    firebaseAuth.signOut()
                    toast("signed out successfully")
                    goToSignInActivity()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getPhotoUrl(): String? {
        return firebaseUser?.photoUrl?.toString()
    }

    private fun getUserName(): String? {
        return if (firebaseUser != null) {
            firebaseUser!!.displayName
        } else ANONYMOUS
    }

    private fun getEmail(): String? {
        return if (firebaseUser != null) {
            firebaseUser!!.email
        } else ANONYMOUS_EMAIL
    }

    private fun loadImageIntoView(view: ImageView, url: String, isCircular: Boolean = true) {
        if (url.startsWith("gs://")) {
            val storageReference = Firebase.storage.getReferenceFromUrl(url)
            storageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    loadWithGlide(view, downloadUrl, false)
                }
                .addOnFailureListener { e ->
                    Log.w(
                        TAG,
                        "Getting download url was not successful.",
                        e
                    )
                }
        } else {
            loadWithGlide(view, url, isCircular)
        }
    }

    private fun loadWithGlide(view: ImageView, url: String, isCircular: Boolean = true) {
        Glide.with(view.context).load(url).into(view)
        var requestBuilder = Glide.with(view.context).load(url)
        if (isCircular) {
            requestBuilder = requestBuilder.transform(CircleCrop())
        }
        requestBuilder.into(view)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun goToSignInActivity() {
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    companion object {
        private const val TAG = "MainActivity"
        const val ANONYMOUS = "anonymous"
        const val ANONYMOUS_EMAIL = "anonymous@email.com"
    }
}