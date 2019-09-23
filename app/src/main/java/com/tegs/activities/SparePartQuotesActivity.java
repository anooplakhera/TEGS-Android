package com.tegs.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.tegs.DatabaseHandler.AppDatabase;
import com.tegs.DatabaseHandler.DBSpareMethodSync;
import com.tegs.DatabaseHandler.SpareDataEntity;
import com.tegs.R;
import com.tegs.adapters.SparePartQuotesAdapter;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivitySparePartQuotesBinding;
import com.tegs.model.GetLoginResponse;
import com.tegs.model.GetSparePartListResponse;
import com.tegs.model.GetSparePartListResponse.Datum;
import com.tegs.retrofit.ApiResponseListener;
import com.tegs.retrofit.RequestParameters;
import com.tegs.retrofit.RestClient;
import com.tegs.utils.AppLog;
import com.tegs.utils.Constants;
import com.tegs.utils.EndlessScrollListener;
import com.tegs.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

/**
 * Created
 * by heena on 28/12/17.
 */

public class SparePartQuotesActivity extends BaseActivity implements View.OnClickListener {
    private ActivitySparePartQuotesBinding binding;
    private SparePartQuotesAdapter sparePartQuotesAdapter;
    private String TAG = SparePartQuotesActivity.class.getSimpleName();
    private EndlessScrollListener endlessScrollListener;
    private int currentPage = 1, totalPage = 0;
    private Utils utils;
    private AppCompatEditText etSearchText;
    private ImageView imgSearch;
    private String searchElement = null;
    private final int REQ_CREATESPARE = 1;
    private int REQ_SPAREPARTDETAILS = 22;
    List<SpareDataEntity> mSpareList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_spare_part_quotes);
        utils = new Utils(SparePartQuotesActivity.this);
        setValues();

        fetchOfflineData();

        if (!Utils.isConnected(this)) {
//            setRoomValues();
            AppLog.d("RoomContains Are", String.valueOf(DBSpareMethodSync.getCount(AppDatabase.getAppDatabase(this))));
        } else {
            callSpareListWS();
        }
        binding.imgAddSpare.setOnClickListener(this);
        imgSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_add_spare:
                startActivityForResult(new Intent(SparePartQuotesActivity.this, CreateSparePartsActivity.class), REQ_CREATESPARE);
                break;
            case R.id.img_search:
                sparePartQuotesAdapter.clearList();
                endlessScrollListener.resetState();
                currentPage = 1;
                searchElement = etSearchText.getText().toString().trim();
                callSpareListWS();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_CREATESPARE) {
                sparePartQuotesAdapter.clearList();
                endlessScrollListener.resetState();
                currentPage = 1;
                callSpareListWS();
            }
            if (requestCode == REQ_SPAREPARTDETAILS) {
                sparePartQuotesAdapter.clearList();
                endlessScrollListener.resetState();
                currentPage = 1;
                callSpareListWS();
            }
        }
    }

    private void setValues() {
        //Setting Toolbar
        initToolbar(this);
        setToolbarTitle(getString(R.string.spare_parts));
        //Searching Edit Text and Image
        etSearchText = findViewById(R.id.et_search_text);
        etSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    sparePartQuotesAdapter.clearList();
                    endlessScrollListener.resetState();
                    currentPage = 1;
                    searchElement = etSearchText.getText().toString().trim();
                    callSpareListWS();
                    return true;
                }
                return false;
            }
        });
        imgSearch = findViewById(R.id.img_search);

        //Setting Recycler View
        sparePartQuotesAdapter = new SparePartQuotesAdapter(SparePartQuotesActivity.this, new ArrayList<Datum>());
        sparePartQuotesAdapter.setOnClickView(new SparePartQuotesAdapter.onClickView() {
            @Override
            public void onClickView(int rowId) {
                Intent intent = new Intent(SparePartQuotesActivity.this, SpareDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.ID, String.valueOf(rowId));
                intent.putExtras(bundle);
                AppLog.d("SparePartQuotes", "SparePartsID" + String.valueOf(rowId));
                startActivityForResult(intent, REQ_SPAREPARTDETAILS);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SparePartQuotesActivity.this);
        binding.recViewSparePart.setLayoutManager(linearLayoutManager);
        binding.recViewSparePart.setAdapter(sparePartQuotesAdapter);
        //Searching
        endlessScrollListener = new EndlessScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (currentPage <= totalPage) {
                    callSpareListWS();
                }
            }
        };
        binding.recViewSparePart.addOnScrollListener(endlessScrollListener);
        binding.swipeSpareParts.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sparePartQuotesAdapter.clearList();
                endlessScrollListener.resetState();
                currentPage = 1;
                callSpareListWS();
                binding.swipeSpareParts.setRefreshing(false);
            }
        });
    }

    private void callSpareListWS() {
        Utils.showProgressDialog(this);
        Call<GetSparePartListResponse> call = RestClient.getInstance(true).getApiInterface().callSpareListResponse(Utils.getRequestMap(true), searchElement, currentPage);
        RestClient.makeApiRequest(this, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                try {


                    Utils.dismissDialog();

                    GetSparePartListResponse result = (GetSparePartListResponse) response;
                    AppLog.d(TAG, getString(R.string.success_response));
                    if (result.getStatus() == RequestParameters.STATUS) {
                        Utils.dismissDialog();
                        binding.txtEmptyView.setVisibility(View.GONE);
                        currentPage++;
                        totalPage = result.getData().getTotalPages();
                        sparePartQuotesAdapter.addList(result.getData().children);

                        saveDataInRoom(result.getData().children);
                    }
                    if (result.getStatus() == RequestParameters.STATUS_403) {
                        Utils.dismissDialog();
                        callLoginWS("");
//                        utils.logout(SparePartQuotesActivity.this);
                    }
                    if (sparePartQuotesAdapter.getItemCount() > 0) {
                        Utils.dismissDialog();
                        binding.txtEmptyView.setVisibility(View.GONE);
                    } else {
                        Utils.dismissDialog();
                        binding.txtEmptyView.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onApiError(Call<Object> call, Throwable throwable) {
                Utils.dismissDialog();
                AppLog.d(TAG, throwable.toString());
            }
        });
    }


    private void callLoginWS(final String type) {
        final Utils utils = new Utils(this);
        /*
         Parameters
        */
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(RequestParameters.EMAIL, utils.getEmail());
        hashMap.put(RequestParameters.PASSWORD, utils.getPassword());
        hashMap.put(RequestParameters.DEVICE_TYPE, RequestParameters.DEVICE_TYPE_VALUE);
        hashMap.put(RequestParameters.DEVICE_TOKEN, RequestParameters.DEVICE_TOKEN_VALUE);

        Call<GetLoginResponse> call = RestClient.getInstance().getApiInterface().callLoginWS(Utils.getRequestMap(false), Utils.getJSONRequestBody(hashMap));
        Utils.showProgressDialog(this);
        RestClient.makeApiRequest(SparePartQuotesActivity.this, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                AppLog.d("TAG", getString(R.string.success_response));
                Utils.dismissDialog();
                GetLoginResponse result = (GetLoginResponse) response;
                if (result != null) {
                    if (result.getStatus() == RequestParameters.STATUS) {
                        AppLog.d("TAG", getString(R.string.success_result));
                        utils.setPrefAuthToken(result.getData().getUserToken());
                        callSpareListWS();
                    }
                }
            }

            @Override
            public void onApiError(Call<Object> call, Throwable throwable) {
                Utils.dismissDialog();
                AppLog.e("TAG", "onError");
                Utils.showSnackBar(SparePartQuotesActivity.this, getString(R.string.invalid_wrong_email_password));
            }
        });
    }

    public void setRoomValues() {
//        sparePartQuotesAdapterRoom = new SparePartQuotesAdapterRoom(SparePartQuotesActivity.this, mSpareList);
//        sparePartQuotesAdapterRoom.setOnClickView(new SparePartQuotesAdapterRoom.onClickView() {
//            @Override
//            public void onClickView(int rowId) {
//                Intent intent = new Intent(SparePartQuotesActivity.this, SpareDetailsActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString(Constants.ID, String.valueOf(rowId));
//                intent.putExtras(bundle);
//                AppLog.d("SparePartQuotes", "SparePartsID" + String.valueOf(rowId));
//                startActivityForResult(intent, REQ_SPAREPARTDETAILS);
//            }
//        });
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SparePartQuotesActivity.this);
//        binding.recViewSparePart.setLayoutManager(linearLayoutManager);
//        binding.recViewSparePart.setAdapter(sparePartQuotesAdapterRoom);
    }

    private void saveDataInRoom(List<Datum> list) {
        List<SpareDataEntity> model1 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            SpareDataEntity model = new SpareDataEntity();
            model.setId(list.get(i).id);
            model.setMake(list.get(i).make);
            model.setMessage(list.get(i).message);
            model.setModalName(list.get(i).modalName);
            model.setUserId(list.get(i).userId);
            model.setCreatedAt(list.get(i).createdAt);
            model.setUpdatedAt(list.get(i).updatedAt);
            model.setImage_url(list.get(i).getSparePartImages().get(0).imageUrl);
            model1.add(model);
        }
        DBSpareMethodSync.insertData(AppDatabase.getAppDatabase(this), model1);
    }

    private void fetchOfflineData() {
//        mSpareList = DBSpareMethodSync.fetchData(AppDatabase.getAppDatabase(this), utils.getUserId());
//        sparePartQuotesAdapterRoom.addList(mSpareList);
    }
}
