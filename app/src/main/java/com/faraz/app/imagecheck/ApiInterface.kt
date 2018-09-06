package com.faraz.app.imagecheck

import io.reactivex.Flowable
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET

/**
 * Created by root on 6/9/18.
 */
interface ApiInterface {
    @GET("dlpw4aumb/image/list/samples.json")
    fun getAllImages(): Observable<Response<ImageModel>>
}