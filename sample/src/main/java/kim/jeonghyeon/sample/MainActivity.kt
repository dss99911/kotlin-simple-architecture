package kim.jeonghyeon.sample

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.ShareActionProvider
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseActivity
import kim.jeonghyeon.androidlibrary.architecture.mvvm.bindingViewModel
import kim.jeonghyeon.androidlibrary.extension.toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    override val layoutId: Int = R.layout.activity_main

    val viewModel: MainActivityViewModel by bindingViewModel()

    override val navHostId: Int = R.id.my_nav_host_fragment

    init {
        setMenu(R.menu.sample_menu) {

            when (it.itemId) {
                R.id.create_new -> {
                    toast("new")
                    true
                }
                else -> false
            }
        }
    }

    override val appBarConfiguration: AppBarConfiguration?
        get() {
            val drawerLayout : DrawerLayout? = findViewById(R.id.drawer_layout)

            //doesn't show up icon on mainFragment, depplink_dest fragment
            return AppBarConfiguration.Builder(R.id.mainFragment, R.id.deeplink_dest)
                //show drawer Icon on action bar
                .setDrawerLayout(drawerLayout)
                .build()
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        setupSearch(menu)
        setupShare(menu)

        return true
    }

    private fun setupSearch(menu: Menu) {

        // Define the listener
        val expandListener = object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                toast("collapsed")
                // Do something when action item collapses
                return true // Return true to collapse action view
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                toast("Expanded")
                // Do something when expanded
                return true // Return true to expand action view
            }
        }

        // Get the MenuItem for the action item

        val actionMenuItem = menu.findItem(R.id.action_search)

        // Assign the listener to that action item
        actionMenuItem?.setOnActionExpandListener(expandListener)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                toast("submitted")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                toast("textChanged")
                return true
            }
        })
    }

    private fun setupShare(menu: Menu) {
        val shareItem = menu.findItem(R.id.action_share)
        val myShareActionProvider = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider
        val myShareIntent = Intent(Intent.ACTION_SEND)
        myShareIntent.putExtra(Intent.EXTRA_TEXT, "dsfdsf")

        myShareActionProvider.setShareIntent(myShareIntent)

        myShareActionProvider.setShareHistoryFileName("custom_share_history.xml")
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        bottom_nav_view?.setupWithNavController(navController)
        nav_view?.setupWithNavController(navController)

    }
}
