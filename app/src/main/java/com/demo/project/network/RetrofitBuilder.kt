package com.demo.project.network

import io.reactivex.Scheduler
import com.demo.project.di.SchedulersModule
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitBuilder(@SchedulersModule.IoScheduler val scheduler:Scheduler){

    val retrofit: Retrofit

    init {

        val requestInterceptor = Interceptor { chain ->
            var request = chain.request()
            val requestBuilder = request.newBuilder()
            requestBuilder.url(request.url.newBuilder()
                .build())

            chain.proceed(requestBuilder.build())
        }

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val builder = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(requestInterceptor)

        retrofit = Retrofit.Builder()
            .baseUrl(ApiCalls.BASE_URL)
            .client(builder.build())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(scheduler))
            .build()
    }

}