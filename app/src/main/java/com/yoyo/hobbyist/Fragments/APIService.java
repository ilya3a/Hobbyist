package com.yoyo.hobbyist.Fragments;

import com.yoyo.hobbyist.Notifications.MyResponse;
import com.yoyo.hobbyist.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAPp81o9o:APA91bFIxD-hnOSdPhGzCD7wGtE-of6OWANn44pQFapd1xSccxippK9IorvoK3jsGBn9RpMv8P6W44-Mo5X9itXu7Fntu0x3o5L01tsFjRQaNAueI2QFLQPwwxz-naOyXMOYfwSbnRoy"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
