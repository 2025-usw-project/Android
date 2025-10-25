package com.su.washcall.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MachineApi {

    @POST("machines/register")
    Call<MachineResponse> registerMachine(@Body MachineRequest request);

    @GET("machines/status")
    Call<List<MachineResponse>> getMachineStatus();
}
