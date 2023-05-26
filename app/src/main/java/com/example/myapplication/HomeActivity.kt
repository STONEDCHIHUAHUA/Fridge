package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.util.Locale

class HomeActivity : Activity() {

    private lateinit var listView: ListView
    private lateinit var searchView: SearchView
    private lateinit var itemList: MutableList<String>
    private lateinit var adapter: ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        listView = findViewById(R.id.listView)
        searchView = findViewById(R.id.searchView)
        itemList = mutableListOf("Item 1", "Item 2", "Item 3")
        adapter = ListAdapter(this, itemList)

        listView.adapter = adapter

        // Add item
        listView.setOnItemClickListener { _, _, _, _ ->
            val editText = EditText(this)
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Add Item")
            alertDialog.setView(editText)

            alertDialog.setPositiveButton("Add") { _, _ ->
                itemList.add(editText.text.toString())
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Item added successfully!", Toast.LENGTH_SHORT).show()
            }

            alertDialog.setNegativeButton("Cancel") { _, _ -> }

            alertDialog.create().show()
        }

        // Edit item
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val editText = EditText(this)
            editText.setText(itemList[position])
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Edit Item")
            alertDialog.setView(editText)

            alertDialog.setPositiveButton("Update") { _, _ ->
                itemList[position] = editText.text.toString()
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Item updated successfully!", Toast.LENGTH_SHORT).show()
            }

            alertDialog.setNegativeButton("Cancel") { _, _ -> }

            alertDialog.create().show()
            true
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
        private val itemList: MutableList<String>
    ) : BaseAdapter(), Filterable {

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
                    val filteredList = mutableListOf<String>()

                    if (constraint.isNullOrEmpty()) {
                        filteredList.addAll(itemList)
                    } else {
                        for (item in itemList) {
                            if (item.lowercase(Locale.ROOT)
                                    .contains(constraint.toString().lowercase(Locale.getDefault()))
                            ) {
                                filteredList.add(item)
                            }
                        }
                    }

                    results.values = filteredList
                    results.count = filteredList.size

                    return results
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    filteredItemList.clear()
                    if ((results != null) && (results.count > 0)) {
                        val filteredList = results.values as MutableList<String>
                        filteredItemList.addAll(filteredList)
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
