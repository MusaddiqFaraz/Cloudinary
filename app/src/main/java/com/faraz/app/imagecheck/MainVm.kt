package com.faraz.app.imagecheck

import android.arch.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.Observable
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by root on 6/9/18.
 */
class MainVm @Inject constructor(private val apiInterface: ApiInterface) : ViewModel() {

    private var imagesFlowable: Observable<Response<ImageModel>> ?= null

    fun getAllStoredImages():Observable<Response<ImageModel>>? {
        if(imagesFlowable == null)
            imagesFlowable = apiInterface.getAllImages()
        return imagesFlowable
    }
}