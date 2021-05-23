package curso.kotlin.pruebasg.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import curso.kotlin.pruebasg.PruebaApplication
import curso.kotlin.pruebasg.R
import curso.kotlin.pruebasg.databinding.ActivityMainBinding
import curso.kotlin.pruebasg.fragments.FirestoreFragment
import curso.kotlin.pruebasg.fragments.GalleryFragment
import curso.kotlin.pruebasg.fragments.LocationFragment
import curso.kotlin.pruebasg.fragments.RestFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mActiveFragment: Fragment
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private lateinit var menu: Menu

    private var mFragmentManager: FragmentManager? = null
    private var mFirebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toggle = object : ActionBarDrawerToggle(this@MainActivity, binding.drawerLayout, R.string.open, R.string.close) {
            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                invalidateOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                dataDrawerLayout(PruebaApplication.currentUser.displayName!!, PruebaApplication.currentUser.email!!)
                invalidateOptionsMenu()
            }
        }

        binding.navigationView.setNavigationItemSelectedListener(this)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_menu)!!
        drawable.setTint(ContextCompat.getColor(this, R.color.white))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.navigationIcon = drawable
        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        menu = binding.navigationView.menu
        supportActionBar?.title = ""
        setutAuth()
    }

    private fun setutAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null) {
                startActivityForResult(
                        AuthUI.getInstance().createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(
                                        listOf(
                                                AuthUI.IdpConfig.EmailBuilder().build(),
                                                AuthUI.IdpConfig.GoogleBuilder().build()
                                        )
                                ).build(), PruebaApplication.RC_SIGN_IN
                )
            } else {
                PruebaApplication.currentUser = it.currentUser!!

                if (mFragmentManager == null) {
                    mFragmentManager = supportFragmentManager
                    setupBottomNav(mFragmentManager!!)
                }
            }
        }
    }

    private fun setupBottomNav(fragmentManager: FragmentManager) {
        val restFragment = RestFragment()
        val firestoreFragment = FirestoreFragment()
        val locationFragment = LocationFragment()
        val galleryFragment = GalleryFragment()

        mActiveFragment = restFragment

        fragmentManager.beginTransaction()
                .add(R.id.hostFragment, galleryFragment, GalleryFragment::class.java.name)
                .hide(galleryFragment).commit()

        fragmentManager.beginTransaction()
                .add(R.id.hostFragment, locationFragment, LocationFragment::class.java.name)
                .hide(locationFragment).commit()

        fragmentManager.beginTransaction()
                .add(R.id.hostFragment, firestoreFragment, FirestoreFragment::class.java.name)
                .hide(firestoreFragment).commit()

        fragmentManager.beginTransaction()
                .add(R.id.hostFragment, restFragment, RestFragment::class.java.name).commit()

        binding.bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_Api -> {
                    fragmentManager.beginTransaction().hide(mActiveFragment).show(restFragment)
                            .commit()

                    mActiveFragment = restFragment
                    true
                }

                R.id.action_cloud_firestore -> {
                    fragmentManager.beginTransaction().hide(mActiveFragment).show(firestoreFragment)
                            .commit()

                    mActiveFragment = firestoreFragment
                    true
                }

                R.id.action_location_map -> {
                    fragmentManager.beginTransaction().hide(mActiveFragment).show(locationFragment)
                            .commit()

                    mActiveFragment = locationFragment
                    true
                }

                R.id.action_gallery -> {
                    fragmentManager.beginTransaction().hide(mActiveFragment).show(galleryFragment)
                            .commit()

                    mActiveFragment = galleryFragment
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mFirebaseAuth?.addAuthStateListener(mAuthListener)
    }

    override fun onPause() {
        super.onPause()
        mFirebaseAuth?.removeAuthStateListener(mAuthListener)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navSingOut -> {
                MaterialAlertDialogBuilder(this@MainActivity)
                        .setTitle(R.string.dialog_message_logout)
                        .setPositiveButton(R.string.dialog_message_logout_confirm) { _, _ ->
                            singOut()
                        }
                        .setNegativeButton(R.string.dialog_message_logout_cancel, null)
                        .show()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PruebaApplication.RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, getString(R.string.message_welcome), Toast.LENGTH_SHORT).show()
            } else {
                if (IdpResponse.fromResultIntent(data) == null) {
                    finish()
                }
            }
        }
    }

    private fun singOut() {
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener {
                    Toast.makeText(this, R.string.logout_success, Toast.LENGTH_SHORT).show()

                    dataDrawerLayout("", "")
                }
    }

    private fun dataDrawerLayout(userText: String, emailText: String) {
        val navigationView = findViewById<View>(R.id.navigationView) as NavigationView
        val headerView: View = navigationView.getHeaderView(0)
        val nameTxt: TextView = headerView.findViewById(R.id.txtUser)
        val mailTxt: TextView = headerView.findViewById(R.id.txtMail)

        nameTxt.text = userText
        mailTxt.text = emailText
    }


}