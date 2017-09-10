package com.hackthenorth.pennapps.investorbuddy

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.dianachang.myapplication.R
import android.view.animation.DecelerateInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.FragmentManager
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.animation.AccelerateInterpolator
import android.view.inputmethod.InputMethodManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.dianachang.myapplication.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_navigation.*
import com.example.dianachang.myapplication.R.id.imageView
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder


class NavigationFragment : Fragment() {

    var animator: ValueAnimator? = null
    var requestQueue: RequestQueue? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_navigation, container, false)
    }

    fun hideSearch() {
        navigationSearchWrapper.visibility = View.GONE
        navigationWrapper.visibility = View.VISIBLE
        view!!.layoutParams.height= ViewGroup.LayoutParams.WRAP_CONTENT;
        animator!!.interpolator = DecelerateInterpolator(2f)
        animator!!.start()
    }

    fun search(query: String) {
        val searchRequest = object : StringRequest(Request.Method.GET,
                "https://pa.clive.io/search?query=" + URLEncoder.encode(query.toString()),
                Response.Listener<String> { response ->
                    val searchResults = JSONArray(response)
                    val scrollView = view!!.findViewById<ScrollView>(R.id.navigationSearchScrollView)
                    scrollView.removeAllViews()
                    val resultView = LinearLayout(scrollView.context)
                    resultView.id = R.id.navigationSearchResultLayout
                    resultView.orientation = LinearLayout.VERTICAL
                    resultView.gravity = Gravity.TOP
                    resultView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

                    scrollView.addView(resultView)

                    val fragMan = fragmentManager
                    val fragTransaction = fragMan.beginTransaction()

                    for (i in 0 .. (searchResults.length() - 1)) {
                        val item = ItemFragment()
                        item.init(ItemData(false, searchResults.getString(i)))
                        fragTransaction.add(resultView.id, item)
                    }

                    fragTransaction.commit()


                },
                Response.ErrorListener { error ->
                    Log.d("ERROR", "error => " + error.toString())
                }
        ) {}
        requestQueue?.add(searchRequest)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requestQueue = Volley.newRequestQueue(activity)
        val dropdown = view!!.findViewById<Spinner>(R.id.navigationSpinner) as Spinner
        val items = arrayOf("1wk", "1mo", "1yr")

        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        dropdown.adapter = adapter
        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {

            }

            override fun onNothingSelected(parent: AdapterView<out Adapter>?) {

            }

        }
        val navigationWrapper = view!!.findViewById<RelativeLayout>(R.id.navigationWrapper)
        val navigationSearchField = view!!.findViewById<SearchView>(R.id.navigationSearchField)
        val navigationSearchWrapper = view!!.findViewById<LinearLayout>(R.id.navigationSearchWrapper)
        val navigationSearchCancel = view!!.findViewById<ImageButton>(R.id.navigationSearchCancel)

        navigationSearchField.setOnQueryTextListener( object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                search(query)
                return false
            }
        });

        val r = resources
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f, r.displayMetrics)
        var stackHeight = 0
        animator = ValueAnimator.ofInt(px.toInt(), px.toInt() * 2)
        animator!!.addUpdateListener { valueAnimator ->
            navigationSearchWrapper.setPadding(px.toInt() * 2, valueAnimator.animatedValue as Int, px.toInt() * 2, valueAnimator.animatedValue as Int)
            navigationWrapper.setPadding(px.toInt() * 2, valueAnimator.animatedValue as Int, px.toInt() * 2, valueAnimator.animatedValue as Int)}


        activity.supportFragmentManager.addOnBackStackChangedListener {
            if (activity.supportFragmentManager.backStackEntryCount == stackHeight && navigationSearchWrapper.visibility == View.VISIBLE) {
                hideSearch()
            }
        }


        navigationSearchCancel.setOnClickListener {
            activity.supportFragmentManager.popBackStackImmediate("search", 0);
            hideSearch()
        }

        view!!.findViewById<ImageButton>(R.id.navigationSearch).setOnClickListener {
            navigationSearchWrapper.visibility = View.VISIBLE
            navigationWrapper.visibility = View.GONE
            navigationSearchField.requestFocusFromTouch()
            stackHeight = activity.supportFragmentManager.backStackEntryCount
            view!!.layoutParams.height= ViewGroup.LayoutParams.MATCH_PARENT;
            activity.supportFragmentManager.beginTransaction()
                    .addToBackStack("search")
                    .commit();
            animator!!.interpolator = AccelerateInterpolator(2f)
            animator!!.reverse()
        }

        view!!.findViewById<ImageView>(R.id.logo).setOnLongClickListener {
            FirebaseAuth.getInstance().signOut()
            val i = Intent(activity, LoginActivity::class.java) // Your list's Intent
            i.flags = Intent.FLAG_ACTIVITY_NO_HISTORY; // Adds the FLAG_ACTIVITY_NO_HISTORY flag
            startActivity(i);
            return@setOnLongClickListener false
        }

    }


    fun onScroll(scrollPosition: Int){
        val view = getView()

    }

    companion object {
        fun newInstance(): NavigationFragment {
            val fragment = NavigationFragment()
            return fragment
        }
    }
}// Required empty public constructor
