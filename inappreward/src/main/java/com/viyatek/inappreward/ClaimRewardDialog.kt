package com.viyatek.inappreward

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.card.MaterialCardView
import com.viyatek.inappreward.databinding.ClaimRewardBinding

abstract class ClaimRewardDialog : DialogFragment(){

    private var activeCard : MaterialCardView? = null
    private var _binding: ClaimRewardBinding? = null
    private val binding get() = _binding!!
    private val inAppPrefsManager by lazy { InAppPrefsManager(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        _binding = ClaimRewardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onResume() {
        super.onResume()

        val width = Resources.getSystem().displayMetrics.widthPixels
        dialog?.window?.setLayout(width * 6 / 7, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.shareText.text = getString(R.string.thanks_for_sharing, requireContext().applicationInfo.loadLabel(requireContext().packageManager))

        setFirstCard(binding.firstCard.topicUnlockImg, binding.firstCard.firstGiftTitle)
        setSecondCard(binding.secondCard.topicUnlockImg, binding.secondCard.firstGiftTitle)

        binding.firstCard.apply {
            root.apply {
                isChecked = true
                activeCard = this
                setOnClickListener {
                    isChecked = true
                    binding.secondCard.root.isChecked = false
                    activeCard = this
                }
            }
        }

        binding.secondCard.apply {
            root.apply {
                isChecked = false

                setOnClickListener {
                    isChecked = true
                    binding.firstCard.root.isChecked = false
                    activeCard = this
                }
            }
        }
        binding.dialogButtonLayout.specialOfferActionButton.apply {
            text = "Get Reward"
            setOnClickListener {
                inAppPrefsManager.setShareRewarded(true)
                when(activeCard)
                {
                    binding.firstCard.root -> {

                        dismissAllowingStateLoss()

                        giveFirstReward()

                    }

                    binding.secondCard.root -> {

                        dismissAllowingStateLoss()
                       giveSecondReward()
                    }
                }
            }
        }

        binding.closeButton.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    abstract fun giveSecondReward()

    abstract fun giveFirstReward()

    abstract fun setFirstCard(firstRewardImage: ImageView, firstRewardTitle : TextView)

    abstract fun setSecondCard(secondRewardImage : ImageView, secondRewardTitle : TextView)
}