package com.tegs.retrofit;

import retrofit2.Call;

/**
 * Created
 * by heena on 4/1/18.
 */

public interface ApiResponseListener {
    /**
     * Call on success API response with request code
     */
    void onApiResponse(Call<Object> call, Object response);
    /*Call on error of API with request code */
    void onApiError(Call<Object> call, Throwable throwable);
}