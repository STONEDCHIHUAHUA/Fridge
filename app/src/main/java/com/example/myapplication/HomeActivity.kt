package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.util.Locale
import com.google.firebase.firestore.FirebaseFirestore
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FieldValue

class HomeActivity : Activity() {

    private lateinit var listView: ListView
    private lateinit var searchView: SearchView
    private lateinit var itemList: MutableList<String>
    private lateinit var adapter: ListAdapter
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        drawerLayout = findViewById(R.id.drawerLayout)

        listView = findViewById(R.id.listView)
        searchView = findViewById(R.id.searchView)
        itemList = mutableListOf()
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

        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(userID)
        docRef.get()
            .addOnSuccessListener { result ->
                val data = result.data
                if (data != null) {
                    for ((key, value) in data) {
                        Log.d(TAG, "$key => $value")
                        itemList.add("$key: $value")
                    }
                    // Save a copy of the original itemList
                    adapter.originalItemList.addAll(itemList)
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Blad podczas pobierania danych: ${e.message}")
            }

        // Add item
        val addButton: Button = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            val layoutInflater = LayoutInflater.from(this)
            val addDialogView: View = layoutInflater.inflate(R.layout.dialog_add_item, null)

            val keyEditText: EditText = addDialogView.findViewById(R.id.keyEditText)
            val valueEditText: EditText = addDialogView.findViewById(R.id.valueEditText)

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setView(addDialogView)
            alertDialogBuilder.setTitle("Dodaj produkt")

            alertDialogBuilder.setPositiveButton("Dodaj") { _, _ ->
                val key = keyEditText.text.toString()
                val value = valueEditText.text.toString()
                val newItem = "$key: $value"
                itemList.add(newItem)
                adapter.originalItemList.add(newItem) // Add to the originalItemList as well
                adapter.notifyDataSetChanged()
                val data = mapOf(
                    key to value
                )

                docRef.update(data as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this,"Dodano produkt ",Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Nie dodano produktu",Toast.LENGTH_LONG).show()
                    }
            }

            alertDialogBuilder.setNegativeButton("Anuluj") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }



        // Edit item
        listView.setOnItemClickListener { _, _, position, _ ->
            val layoutInflater = LayoutInflater.from(this)
            val editDialogView: View = layoutInflater.inflate(R.layout.dialog_edit_item, null)

            val keyEditText: EditText = editDialogView.findViewById(R.id.keyEditText)
            val valueEditText: EditText = editDialogView.findViewById(R.id.valueEditText)

            val currentItem = itemList[position]
            val keyValue = currentItem.split(":").map { it.trim() }

            keyEditText.setText(keyValue[0])
            valueEditText.setText(keyValue[1])

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setView(editDialogView)
            alertDialogBuilder.setTitle("Edytuj")

            alertDialogBuilder.setPositiveButton("Aktualizuj") { _, _ ->
                val newKey = keyEditText.text.toString()
                val newValue = valueEditText.text.toString()
                val updatedItem = "$newKey: $newValue"
                itemList[position] = updatedItem
                adapter.notifyDataSetChanged()

                val data = mapOf(
                    newKey to newValue
                )
                val updates = mapOf(
                    keyValue[0] to FieldValue.delete(),
                )

                docRef.update(updates)


                docRef.update(data as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Zaktualizowano produkt", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Blad aktualizacji produktu", Toast.LENGTH_SHORT).show()
                    }
            }

            alertDialogBuilder.setNegativeButton("Anuluj") { dialog, _ ->
                dialog.dismiss()
            }

            // delete item
            alertDialogBuilder.setNeutralButton("Usun") { _, _ ->
                itemList.remove(currentItem)
                adapter.notifyDataSetChanged()

                val updates = mapOf(
                    keyValue[0] to FieldValue.delete(),
                )

                docRef.update(updates)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Produkt usuniety poprawnie", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Nie usunieto produktu", Toast.LENGTH_SHORT).show()
                    }
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
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
        val originalItemList: MutableList<String> = mutableListOf()
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

                        results.values = filteredList
                        results.count = filteredList.size
                    }

                    return results
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    filteredItemList.clear()
                    if (results != null && results.count > 0) {
                        val filteredList = results.values as MutableList<String>
                        filteredItemList.addAll(filteredList)
                    } else {
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
