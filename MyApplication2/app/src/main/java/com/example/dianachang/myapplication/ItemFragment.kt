package com.hackthenorth.pennapps.investorbuddy

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.dianachang.myapplication.R
import android.util.TypedValue
import android.view.Gravity
import android.widget.ScrollView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.LabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import org.json.JSONArray
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ItemFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class ItemFragment : Fragment() {

    var params: ItemData? = null
    var name: String = "Untitled"
    var descriptorColor: Int = R.color.colorPrimary
    var descriptor: String = "Loading..."
    var cost: Float = 0f
    var previousCost: Float = 0f
    var shareCost: Float = 0f
    var gpr: Float = 0f
    var requestQueue: RequestQueue? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item, container, false)
    }

    @SuppressLint("ResourceAsColor")
    fun update() {

        view!!.findViewById<TextView>(R.id.itemName).text = name

        val itemDescriptor = view!!.findViewById<TextView>(R.id.itemDescriptor)

        val diff = Math.abs(cost-previousCost)/cost

        view!!.findViewById<TextView>(R.id.itemCost).text = "$" + cost.toString()

        if (params!!.portfolio) {
            val itemShareCost = view!!.findViewById<TextView>(R.id.itemShareCost)
            itemShareCost.text = shareCost.toString() + "/share"
            itemShareCost.visibility = View.VISIBLE
        } else {
            itemDescriptor.setTextColor(resources.getColor(if (cost < previousCost) R.color.negative else R.color.positive))
            itemDescriptor.text = (if (cost < previousCost) "" else "+") + String.format("%.2f", cost - previousCost) + " ("+String.format("%.2f", diff * 100)+"%)"
        }

        view!!.findViewById<TextView>(R.id.itemMcr).text = (if (gpr < 0) "?" else gpr.toString()) + " gpr"
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun _init() {
        requestQueue = Volley.newRequestQueue(activity)
        if (!params!!.static) {
            view!!.isClickable = true
            val typedValue = TypedValue()
            activity?.theme?.resolveAttribute(R.attr.selectableItemBackground, typedValue, true)
            view!!.background = ContextCompat.getDrawable(context, typedValue.resourceId)
        }

        if (params!!.portfolio) {

        } else {
            name = params!!.ticker

            val graph = view!!.findViewById<GraphView>(R.id.itemGraph);
            graph.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.NONE
            graph.gridLabelRenderer.isHorizontalLabelsVisible = false
            graph.gridLabelRenderer.isVerticalLabelsVisible = false

            val tickerRequest = object : StringRequest(Request.Method.GET,
                    "https://pa.clive.io/data?ticker=" + name + "&interval=1w",
                    Listener<String> { response ->
                        val results = JSONArray(response)
                        val current = results.getJSONObject(0)
                        val previous = results.getJSONObject(1)
                        cost = current.getString("price").substring(1).toFloat()
                        previousCost = previous.getString("price").substring(1).toFloat()
                        val parser = SimpleDateFormat("yyyy-MM-dd")

                        val lastTime = parser.parse(results.getJSONObject(results.length() - 1).getString("date")).time.toDouble()/86400000
                        val data = Array<DataPoint>(results.length(), fun(i: Int):DataPoint {
                            val point = results.getJSONObject(results.length()-i-1);
                            Log.d(name, (results.length()-i-1).toString())
                            Log.d(name, ((parser.parse(point.getString("date")).time.toDouble()/86400000 - lastTime)*2).toString());
                            Log.d(name+"%", point.getString("price").substring(1).toDouble().toString())
                            //return DataPoint(24.0-i, 350.0);
                            return DataPoint((parser.parse(point.getString("date")).time.toDouble()/86400000 - lastTime)*2, point.getString("price").substring(1).toDouble());
                        });
                        val series = LineGraphSeries<DataPoint>(data);
                        if(cost > previousCost)
                            series.color = resources.getColor(R.color.positive);
                        else
                            series.color = resources.getColor(R.color.negative);

                        series.thickness = 10
                        series.isDrawAsPath = true
                        graph.addSeries(series);
                        update()
                    },
                    Response.ErrorListener { error ->
                        Log.d("ERROR", "error => " + error.toString())
                    }
            ) {}
            tickerRequest.retryPolicy = DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            requestQueue?.add(tickerRequest)
        }
        update()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun init(itemData: ItemData){

        params = itemData

        if (view != null) {
            _init()
        }


    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("ResourceType")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (params != null) {
            _init()
        }
    }

    companion object {
        fun newInstance(): ItemFragment {
            val fragment = ItemFragment()
            return fragment
        }
    }
}// Required empty public constructor

data class ItemData(val portfolio: Boolean, val ticker: String, val static: Boolean = false);
