package com.nomanim.bunual.ui.fragments.adsDetailsActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator
import com.nomanim.bunual.databinding.FragmentShowPicturesBinding

class ShowPicturesFragment : Fragment() {

    private var _binding: FragmentShowPicturesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentShowPicturesBinding.inflate(inflater)

        binding.mBigImage.setProgressIndicator(ProgressPieIndicator())


        /*val sharedPref = activity?.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        val imagesLinksStringSet = sharedPref?.getString("imagesLinks",null)
        val list = imagesLinksStringSet?.split("|") as ArrayList<String>
        val imagesLists = ArrayList<Uri>()
        for (i in 0 until list.size) { imagesLists.add(list[i].toUri()) }
        val prefetch = BigImageViewer.prefetch(imagesLists[0])*/


        return binding.root
    }

}