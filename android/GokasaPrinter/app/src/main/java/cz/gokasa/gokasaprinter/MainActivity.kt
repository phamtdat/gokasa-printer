package cz.gokasa.gokasaprinter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import cz.gokasa.gokasaprinter.support.BluetoothDeviceListAdapter
import cz.gokasa.gokasaprinter.support.ScanForDevicesTaskFragment
import cz.gokasa.gokasaprinter.support.ScanForDevicesTaskFragment.DevicesScanListener
import cz.gokasa.gokasaprinter.support.TestAdapter

class MainActivity : AppCompatActivity(), DevicesScanListener {

    private var recyclerView: RecyclerView? = null
    //private var adapter: BluetoothDeviceListAdapter = BluetoothDeviceListAdapter()
    private var adapter: TestAdapter = TestAdapter()
    private var progressBar: ProgressBar? = null
    private var scanButton: FloatingActionButton? = null

    private val TAG_SCAN_TASK_FRAGMENT = "scan_task_fragment"
    private var scanTaskFragment: ScanForDevicesTaskFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scanTaskFragment = supportFragmentManager
                .findFragmentByTag(TAG_SCAN_TASK_FRAGMENT) as? ScanForDevicesTaskFragment

        if (scanTaskFragment == null) {
            scanTaskFragment = ScanForDevicesTaskFragment()
            supportFragmentManager.beginTransaction()
                    .add(scanTaskFragment, TAG_SCAN_TASK_FRAGMENT).commit()
        }

        progressBar = findViewById(R.id.pb_scan_progress)
        if(scanTaskFragment!!.taskIsRunning) {
            progressBar?.visibility = View.VISIBLE
        } else {
            progressBar?.visibility = View.INVISIBLE
        }

        recyclerView = findViewById(R.id.bt_devices_list)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter
        (recyclerView?.adapter as TestAdapter).setData(scanTaskFragment!!.devices)

        scanButton = findViewById(R.id.fab_scan)
        scanButton?.setOnClickListener {
            if(!scanTaskFragment!!.taskIsRunning) {
                (recyclerView?.adapter as TestAdapter).setData(ArrayList())
                scanTaskFragment?.scan()
            } else {
                Log.e("SCAN BUTTON", "Scan already running")
            }
        }
    }

    override fun onPreExecute() {
        progressBar?.visibility = View.VISIBLE
    }

    override fun onPostExecute(result: ArrayList<String>) {
        progressBar?.visibility = View.INVISIBLE
        (recyclerView?.adapter as TestAdapter).setData(result)
    }
}
