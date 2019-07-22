package com.tegs.retrofit;


import com.tegs.model.CreateInstantMsgResponse;
import com.tegs.model.CreateSparePartsResponse;
import com.tegs.model.GetChangePasswordResponse;
import com.tegs.model.GetCommonResponse;
import com.tegs.model.GetEditProfileResponse;
import com.tegs.model.GetEditSpareResponse;
import com.tegs.model.GetForgotPasswordResponse;
import com.tegs.model.GetInstallationQuotesData;
import com.tegs.model.GetInstantMessageResponse;
import com.tegs.model.GetLogOutResponse;
import com.tegs.model.GetLoginResponse;
import com.tegs.model.GetProfileResponse;
import com.tegs.model.GetQuestionsListResponse;
import com.tegs.model.GetSignUpResponse;
import com.tegs.model.GetSpareDetailsResponse;
import com.tegs.model.GetSparePartListResponse;
import com.tegs.model.SetAnswers;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by heena on 4/1/18.
 */

public interface ApiInterface {
    final String Login = "login";
    final String SignUp = "sign-up";
    final String ForgotPassword = "forgot-password";
    final String GetProfile = "get-profile";
    final String ChangePassword = "change-password";
    final String EditProfile = "edit-profile";
    final String LogOut = "logout";
    final String CreateInstantMsg = "create-instant-message";
    final String GetInstantMsgList = "get-instant-messages-list";
    final String CreateSparePart = "create-spare-part-quote";
    final String GetSparePartList = "get-spare-part-quote-list";
    final String GetSpareDetails = "get-spare-part-quote";
    final String EditSpareQuote = "edit-spare-part-quote";
    final String GetQuestionsList = "get-questions";
    final String GetAnswersList = "get-answers";
    final String SetAnswersList = "set-answers";
    final String UpdateAnswers = "update-answers";
    final String DeleteAnswers = "delete-answers";
    final String DeleteSpareAnswers = "delete-spare-part";

    @POST(Login)
    Call<GetLoginResponse> callLoginWS(@HeaderMap Map<String, String> headers,@Body RequestBody params);

    @Multipart
    @POST(SignUp)
    Call<GetSignUpResponse> callSignUpWS(@HeaderMap Map<String, String> headers,@Part MultipartBody.Part file
            , @Part(RequestParameters.EMAIL) RequestBody email
            , @Part(RequestParameters.PASSWORD) RequestBody password
            , @Part(RequestParameters.DEVICE_TYPE) RequestBody devicetype
            , @Part(RequestParameters.NAME) RequestBody name
            , @Part(RequestParameters.ROLE_ID) RequestBody roleID
            , @Part(RequestParameters.PHONE_NUMBER) RequestBody phoneno
            , @Part(RequestParameters.DEVICE_TOKEN) RequestBody deviceToken);

    @FormUrlEncoded
    @POST(ForgotPassword)
    Call<GetForgotPasswordResponse> callForgotPassWS(@HeaderMap Map<String, String> headers,@Field(RequestParameters.EMAIL) String email);

    @POST(GetProfile)
    Call<GetProfileResponse> callGetProfileWS(@HeaderMap Map<String, String> headers);

    @POST(ChangePassword)
    Call<GetChangePasswordResponse> callChangePassWS(@HeaderMap Map<String, String> headers,@Body RequestBody params);

    @Multipart
    @POST(EditProfile)
    Call<GetEditProfileResponse> callEditProfileWS(@HeaderMap Map<String, String> headers,@Part MultipartBody.Part file
            , @Part(RequestParameters.DEVICE_TYPE) RequestBody devicetype
            , @Part(RequestParameters.NAME) RequestBody name
            , @Part(RequestParameters.PHONE_NUMBER) RequestBody phoneno
            , @Part(RequestParameters.DEVICE_TOKEN) RequestBody deviceToken
            , @Part(RequestParameters.PHONE_NUMBER_2) RequestBody phoneno2);

    @POST(LogOut)
    Call<GetLogOutResponse> callLogOutWS(@HeaderMap Map<String, String> headers);

    @POST(CreateInstantMsg)
    Call<CreateInstantMsgResponse> callCreateInstantMsg(@HeaderMap Map<String, String> headers,@Body RequestBody params);

    @FormUrlEncoded
    @POST(GetInstantMsgList)
    Call<GetInstantMessageResponse> callInstantMsgList(@HeaderMap Map<String, String> headers,@Field(RequestParameters.PAGE) int page);


    @Multipart
    @POST(CreateSparePart)
    Call<CreateSparePartsResponse> callCreateSpareParts(@HeaderMap Map<String, String> headers,
            @Part("image[0]\";filename=\"image[0].png\" ") RequestBody image1,
            @Part("image[1]\";filename=\"image[1].png\" ") RequestBody image2,
            @Part("image[2]\";filename=\"image[2].png\" ") RequestBody image3,
            @Part("image[3]\";filename=\"image[3].png\" ") RequestBody image4,
            @Part(RequestParameters.MAKE) RequestBody make,
            @Part(RequestParameters.MODAL_NAME) RequestBody modal_name,
            @Part(RequestParameters.MESSAGE) RequestBody message);


    @FormUrlEncoded
    @POST(GetSparePartList)
    Call<GetSparePartListResponse> callSpareListResponse(@HeaderMap Map<String, String> headers, @Field(RequestParameters.SEARCH) String search, @Field(RequestParameters.PAGE) int page);

    @FormUrlEncoded
    @POST(GetSpareDetails)
    Call<GetSpareDetailsResponse> callSpareDetailsWS(@HeaderMap Map<String, String> headers,@Field(RequestParameters.ID) int id);

    @Multipart
    @POST(EditSpareQuote)
    Call<GetEditSpareResponse> callEditSpareWS(@HeaderMap Map<String, String> headers,@Part(RequestParameters.ID) RequestBody id
            , @Part(RequestParameters.MAKE) RequestBody make
            , @Part(RequestParameters.MODAL_NAME) RequestBody modal_name
            , @Part(RequestParameters.MESSAGE) RequestBody message
            , @Part MultipartBody.Part[] deleted_image
            , @Part MultipartBody.Part[] image);

    @POST(GetQuestionsList)
    Call<GetQuestionsListResponse> callQuestionListWS(@HeaderMap Map<String, String> headers);

    @POST(GetAnswersList)
    Call<GetInstallationQuotesData> GetAnswersListWS(@HeaderMap Map<String, String> headers);

    @FormUrlEncoded
    @POST(SetAnswersList)
    Call<SetAnswers> SetAnswersListWS(@HeaderMap Map<String, String> headers,@Field(RequestParameters.DATE) String date, @Field(RequestParameters.TITLE) String title, @Field(RequestParameters.ANSWERS) String json);

    @FormUrlEncoded
    @POST(UpdateAnswers)
    Call<SetAnswers> UpdateAns(@HeaderMap Map<String, String> headers,@Field(RequestParameters.QUOTE_ID) int quote_id, @Field(RequestParameters.ANSWERS) String json);

    @FormUrlEncoded
    @POST(DeleteAnswers)
    Call<GetCommonResponse> DeleteAns(@HeaderMap Map<String, String> headers,@Field(RequestParameters.QUOTE_ID) int quote_id);

    @FormUrlEncoded
    @POST(DeleteSpareAnswers)
    Call<GetCommonResponse> DeleteSpareAns(@HeaderMap Map<String, String> headers,@Field(RequestParameters.Spare_ID) int quote_id);

}