package com.kelompokNizarBersaudara.krambilsawit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.kelompokNizarBersaudara.krambilsawit.databinding.ActivityMainBinding
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

        if (firebaseUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

//        val toolbar: Toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        drawerLayout = findViewById(R.id.drawer_layout)
//
//        val toggle = ActionBarDrawerToggle(
//            this,
//            drawerLayout,
//            toolbar,
//            R.string.navigation_drawer_open,
//            R.string.navigation_drawer_close
//        )
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()

        navigationView = findViewById(R.id.nav_view)
        val navController = findNavController(this, R.id.nav_view)

        appBarConfiguration = AppBarConfiguration.Builder(navController.graph)
            .build()
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(navigationView, navController)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(this, R.id.nav_view)
        return (navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp())
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.top_menu, menu)
//        return true
//    }
//
//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        item.isChecked = true
//        when(item.itemId) {
//            R.id.article_item -> {
//                Toast.makeText(this, "Article Item", Toast.LENGTH_SHORT).show()
//            }
//            R.id.favourites_item -> {
//                Toast.makeText(this, "Favorite Item", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        drawerLayout.closeDrawer(GravityCompat.START);
//        return true;
//    }
}