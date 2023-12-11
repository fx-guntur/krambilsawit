package com.kelompokNizarBersaudara.krambilsawit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val navController = Navigation.findNavController(this, R.id.container)
//        val currentDestination = navController.currentDestination?.id
//
//        return when (item.itemId) {
//            R.id.account -> {
//                if (currentDestination == R.id.aboutFragment) {
//                    navController.navigate(R.id.action_aboutFragment_to_profileActivity)
//                } else {
//                    navController.navigate(R.id.action_dashboardFragment_to_profileActivity)
//                }
//                true
//            }
//            R.id.aboutActions -> {
//                if (currentDestination == R.id.aboutFragment) {
//                    navController.navigate(R.id.action_aboutFragment_self)
//                } else {
//                    navController.navigate(R.id.action_dashboardFragment_to_aboutFragment)
//                }
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isChecked = true
        when(item.itemId) {
            R.id.article_item -> {
                Toast.makeText(this, "Article Item", Toast.LENGTH_SHORT).show()
            }
            R.id.favourites_item -> {
                Toast.makeText(this, "Favorite Item", Toast.LENGTH_SHORT).show()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}