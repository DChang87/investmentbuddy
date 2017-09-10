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
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.TypedValue
import android.view.animation.AccelerateInterpolator
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.fragment_navigation.*


class NavigationFragment : Fragment() {

    var animator: ValueAnimator? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_navigation, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val dropdown = view!!.findViewById<Spinner>(R.id.navigationSpinner) as Spinner
        val items = arrayOf("1wk", "1mo", "1yr")

        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        dropdown.adapter = adapter
        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {

            }

            override fun onNothingSelected(parent: AdapterView<out Adapter>?) {

            }

        }
        val navigationWrapper = view!!.findViewById<RelativeLayout>(R.id.navigationWrapper)
        val navigationSearchField = view!!.findViewById<AutoCompleteTextView>(R.id.navigationSearchField)
        val navigationSearchWrapper = view!!.findViewById<LinearLayout>(R.id.navigationSearchWrapper)
        val navigationSearchCancel = view!!.findViewById<ImageButton>(R.id.navigationSearchCancel)
        val r = resources
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f, r.displayMetrics)
        animator = ValueAnimator.ofInt(0, px.toInt())
        animator!!.addUpdateListener { valueAnimator ->
            view!!.setPadding(0, valueAnimator.animatedValue as Int, 0, valueAnimator.animatedValue as Int) }

        navigationSearchCancel.setOnClickListener {
            navigationSearchWrapper.visibility = View.GONE
            navigationWrapper.visibility = View.VISIBLE
            view!!.elevation = 0f
            animator!!.interpolator = DecelerateInterpolator(2f)
            animator!!.start()
        }

        view!!.findViewById<ImageButton>(R.id.navigationSearch).setOnClickListener {
            navigationSearchWrapper.visibility = View.VISIBLE
            navigationWrapper.visibility = View.GONE
            view!!.elevation = 10f
            navigationSearchField.requestFocus()
            @SuppressLint("ServiceCast")
            val lManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            lManager!!.showSoftInput(navigationSearchField, 0)
            animator!!.interpolator = AccelerateInterpolator(2f)
            animator!!.reverse()
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
