package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.util.Locale

class PrzepisyActivity : Activity() {

    private lateinit var listView: ListView
    private lateinit var searchView: SearchView
    private lateinit var itemList: MutableList<String>
    private lateinit var adapter: ListAdapter
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_przepisy)
        drawerLayout = findViewById(R.id.drawerLayout)

        listView = findViewById(R.id.listView)
        searchView = findViewById(R.id.searchView)
        itemList = mutableListOf("spaghetti carbonara",  "pizza margherita", "piwko")
        adapter = ListAdapter(this, itemList)
        listView.adapter = adapter
        val navigationView: NavigationView = findViewById(R.id.navigationView)
        val bundle: Bundle? = intent.extras
        val userID = bundle!!.getString("UserID").toString()

        // side menu
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> {
                    // Handle menu item 1 click
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("UserID", userID)
                    startActivity(intent)
                    true
                }
                R.id.menu_przepisy -> {
                    // Handle menu item 2 click
                    val intent = Intent(this, PrzepisyActivity::class.java)
                    intent.putExtra("UserID", userID)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // Set up menu button click listener
        val menuButton: Button = findViewById(R.id.menuButton)
        menuButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView)
            } else {
                drawerLayout.openDrawer(navigationView)
            }
        }

        // Search item
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }

    class ListAdapter(
        context: Activity,
        itemList: MutableList<String>
    ) : BaseAdapter(), Filterable {

        // Save a copy of the original itemList
        val originalItemList: MutableList<String> = itemList.toMutableList()
        private var filteredItemList: MutableList<String> = itemList
        private val inflater: LayoutInflater = LayoutInflater.from(context)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View
            val viewHolder: ViewHolder

            if (convertView == null) {
                view = inflater.inflate(R.layout.list_item, parent, false)
                viewHolder = ViewHolder(view)
                view.tag = viewHolder
            } else {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }

            viewHolder.itemTextView.text = filteredItemList[position]
            return view
        }

        override fun getItem(position: Int): String {
            return filteredItemList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return filteredItemList.size
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val results = FilterResults()

                    if (constraint.isNullOrEmpty()) {
                        results.values = originalItemList
                        results.count = originalItemList.size
                    } else {
                        val query = constraint.toString().lowercase(Locale.getDefault())
                        val filteredList = originalItemList.filter { item ->
                            item.lowercase(Locale.ROOT).contains(query)
                        }.toMutableList()
                        if (filteredList.size > 0) {
                            results.values = filteredList
                            results.count = filteredList.size
                        }
                    }

                    return results
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    filteredItemList.clear()
                    if (results != null && results.count > 0) {
                        val filteredList = results.values as MutableList<String>
                        filteredItemList.addAll(filteredList)
                    } else if (results != null && results.count == 0){
                        filteredItemList.addAll(mutableListOf())
                    }else  {
                        filteredItemList.addAll(originalItemList)
                    }
                    notifyDataSetChanged()
                }

            }
        }

        private class ViewHolder(itemView: View) {
            val itemTextView: TextView = itemView.findViewById(R.id.itemTextView)
        }
    }
}