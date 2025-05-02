package com.elite.parking

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elite.parking.Model.UserSession.companyId
import com.elite.parking.Model.UserSession.token
import com.elite.parking.Model.VehicleViewModelFactory
import com.elite.parking.apis.ApiService
import com.elite.parking.apis.RetrofitClient
import com.elite.parking.repository.VehicleRepository
import com.elite.parking.storage.SharedPreferencesHelper
import com.elite.parking.viewModel.VehicleViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HistoryFragment : Fragment() {
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var vehicleAdapter: VehicleAdapter
    private lateinit var vehicleViewModel: VehicleViewModel.VehicleViewModelList
    private lateinit var userId: String
    private lateinit var authToken: String
    private lateinit var lnrNoData: LinearLayout
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var childFab1: FloatingActionButton
    private lateinit var childFab2: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        shimmerLayout = view.findViewById(R.id.shimmerLayout)
        recyclerView = view.findViewById(R.id.vehicleRecyclerView)
        lnrNoData = view.findViewById(R.id.lnrNoData)
        recyclerView.layoutManager = LinearLayoutManager(context)
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        val loginResponse = sharedPreferencesHelper.getLoginResponse()

        val fabAddVehicle: FloatingActionButton = view.findViewById(R.id.fabAddVehicle)
        childFab1 = view.findViewById(R.id.childFab1)
        childFab2 = view.findViewById(R.id.childFab2)
        toggleChildFabs()

        loginResponse?.let { response ->
            val loginData = response.status.firstOrNull()
            if (loginData != null) {
                userId = loginData.uuid
                authToken = loginData.token
                companyId = loginData.companyId
            } else {
                Toast.makeText(requireContext(), "Login data is missing.", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(requireContext(), "Please Logout and Login Once.", Toast.LENGTH_SHORT).show()
        }

        shimmerLayout.startShimmer()

        childFab1.setOnClickListener {
            val intent = Intent(requireActivity(), CarFormActivity::class.java)
            startActivity(intent)
        }

        childFab2.setOnClickListener {
            val intent = Intent(requireActivity(), PaymentActivity::class.java)
            startActivity(intent)
        }

        initialAPICall()

        return view
    }

    private fun initialAPICall() {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val repository = VehicleRepository(apiService)
        vehicleViewModel = ViewModelProvider(this, VehicleViewModelFactory(repository)).get(VehicleViewModel.VehicleViewModelList::class.java)

        vehicleViewModel.vehicleList.observe(viewLifecycleOwner) { vehicleList ->
            if (vehicleList.isNotEmpty()) {
                lnrNoData.visibility = View.GONE
            } else {
                lnrNoData.visibility = View.VISIBLE
            }

            shimmerLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            vehicleAdapter = VehicleAdapter(requireContext(), vehicleList) { vehicle ->
                val intent = Intent(requireActivity(), PaymentActivity::class.java)
                intent.putExtra("vehicleUuid", vehicle.uuid)
                startActivity(intent)
            }
            recyclerView.adapter = vehicleAdapter
        }

        vehicleViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Loading state handler if needed
        }

        vehicleViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }

        vehicleViewModel.fetchVehicleDetails(userId, authToken)
    }

    override fun onResume() {
        super.onResume()
        initialAPICall()
    }

    private fun toggleChildFabs() {
        if (childFab1.visibility == View.GONE) {
            showChildFabs()
        } else {
            hideChildFabs()
        }
    }

    private fun showChildFabs() {
        childFab1.visibility = View.VISIBLE
        childFab2.visibility = View.VISIBLE

        val animator1 = ObjectAnimator.ofFloat(childFab1, "translationY", 0f, -10f)
        val animator2 = ObjectAnimator.ofFloat(childFab2, "translationY", 0f, -200f)

        AnimatorSet().apply {
            playTogether(animator1, animator2)
            duration = 600
            start()
        }
    }

    private fun hideChildFabs() {
        val animator1 = ObjectAnimator.ofFloat(childFab1, "translationY", -10f, 0f)
        val animator2 = ObjectAnimator.ofFloat(childFab2, "translationY", -200f, 0f)

        AnimatorSet().apply {
            playTogether(animator1, animator2)
            duration = 300
            start()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    childFab1.visibility = View.GONE
                    childFab2.visibility = View.GONE
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
    }
}