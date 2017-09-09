package com.example.dianachang.myapplication

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.*
import com.hackthenorth.pennapps.investorbuddy.DragGroup
import com.hackthenorth.pennapps.investorbuddy.ItemData
import com.hackthenorth.pennapps.investorbuddy.ItemFragment

import kotlinx.android.synthetic.main.activity_main.*
import android.text.Editable
import android.text.TextWatcher
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.design.widget.CoordinatorLayout.Behavior.setTag
import android.widget.EditText
import android.widget.TextView
import android.view.LayoutInflater

import android.view.View.OnFocusChangeListener
import android.support.design.widget.CoordinatorLayout.Behavior.setTag
import android.widget.ArrayAdapter
import android.widget.Spinner







class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lv = findViewById<View>(R.id.list) as ListView
        lv.adapter = ListExampleAdapter(this, arrayOf("One", "Two", "Three", "Four", "Five", "Six", "Seven",
                "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen"))

        val portfolio_lv = findViewById<View>(R.id.portfolio_lv) as ListView
        portfolio_lv.adapter = ListExampleAdapter(this, arrayOf( "1", "2", "3", "4", "5", "6" ,"7","8"))
        portfolio_lv.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->

        }
        fab.setOnClickListener { view ->
            fab.visibility = View.GONE
            findViewById<DragGroup>(R.id.dragGroup).minimize()
        }
        //button_cancel.visibility = View.GONE
        //button_save.visibility = View.GONE

        val dropdown = findViewById<Spinner>(R.id.spinner) as Spinner
        val items = arrayOf("1wk", "2wk", "3wk")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        dropdown.adapter = adapter
        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {

            }

            override fun onNothingSelected(parent: AdapterView<out Adapter>?) {

            }

        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }

    }
    fun hidePortfolioComponents(view: View) {
        fab.visibility = View.VISIBLE
        dragGroup.hide()

    }

    private class ListExampleAdapter(context: Context, slist: Array<String>) : BaseAdapter() {
        //internal var sList =
        internal var sList = slist
        private val mInflator: LayoutInflater

        init {
            this.mInflator = LayoutInflater.from(context)
        }

        override fun getCount(): Int {
            return sList.size
        }

        override fun getItem(position: Int): Any {
            return sList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val view: View?
            val vh: ListRowHolder
            if (convertView == null) {
                view = this.mInflator.inflate(R.layout.list_row, parent, false)
                vh = ListRowHolder(view)
                view.tag = vh
            } else {
                view = convertView
                vh = view.tag as ListRowHolder
            }

            vh.label.text = sList[position]
            return view
        }
    }

    private class ListRowHolder(row: View?) {
        public val label: TextView

        init {
            this.label = row?.findViewById<TextView>(R.id.label) as TextView
        }
    }
}
