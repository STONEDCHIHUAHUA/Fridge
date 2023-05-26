//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.myapplication.R
//import java.util.*
//
//class PrzepisyActivity : AppCompatActivity() {
//
//    private lateinit var itemList: MutableList<String>
//    private lateinit var filteredItemList: MutableList<String>
//    private lateinit var adapter: ListAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_przepisy)
//
//        itemList = mutableListOf("Spaghetti Carbonara",  "Pizza Margherita")
//        filteredItemList = mutableListOf()
//        filteredItemList.addAll(itemList)
//
//        val searchView = findViewById<SearchView>(R.id.searchView)
//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        adapter = ListAdapter(filteredItemList, window.decorView.findViewById(android.R.id.content))
//        recyclerView.adapter = adapter
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                adapter.filter.filter(newText)
//                return false
//            }
//        })
//    }
//
//    private class ListAdapter(private val itemList: MutableList<String>, private val parent: ViewGroup) : RecyclerView.Adapter<ListAdapter.ViewHolder>(), Filterable {
//
//        private val filteredItemList = mutableListOf<String>()
//        private val inflater: LayoutInflater = LayoutInflater.from(parent.context)
//
//        init {
//            filteredItemList.addAll(itemList)
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            val view = inflater.inflate(R.layout.list_item, parent, false)
//            return ViewHolder(view)
//        }
//
//        override fun getItemCount(): Int {
//            return filteredItemList.size
//        }
//
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.itemTextView.text = filteredItemList[position]
//        }
//
//        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//            val itemTextView: TextView = itemView.findViewById(R.id.itemTextView)
//        }
//
//        override fun getFilter(): Filter {
//            return object : Filter() {
//                override fun performFiltering(constraint: CharSequence?): FilterResults {
//                    val results = FilterResults()
//                    val filteredList = mutableListOf<String>()
//
//                    if (constraint.isNullOrEmpty()) {
//                        filteredList.addAll(itemList)
//                    } else {
//                        for (item in itemList) {
//                            if (item.lowercase(Locale.ROOT).contains(constraint.toString().lowercase(Locale.getDefault()))) {
//                                filteredList.add(item)
//                            }
//                        }
//                    }
//
//                    results.values = filteredList
//                    results.count = filteredList.size
//
//                    return results
//                }
//
//                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//                    filteredItemList.clear()
//                    if ((results != null) && (results.count > 0)) {
//                        val filteredList = results.values as? MutableList<String>?
//                        if (filteredList != null) {
//                            filteredItemList.addAll(filteredList)
//                        }
//                    }
//                    notifyDataSetChanged()
//                }
//            }
//        }
//    }
//}
