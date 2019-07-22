package com.tegs.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import com.google.gson.Gson;
import com.tegs.DatabaseHandler.AppDatabase;
import com.tegs.DatabaseHandler.DBSetAnsMethodSync;
import com.tegs.DatabaseHandler.SetAnswerEntity;
import com.tegs.R;
import com.tegs.adapters.InstallationQuotesAdapter;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivityInstallationQuotesBinding;
import com.tegs.model.GetInstallationQuotesData;
import com.tegs.model.SetAnswers;
import com.tegs.retrofit.ApiResponseListener;
import com.tegs.retrofit.RequestParameters;
import com.tegs.retrofit.RestClient;
import com.tegs.utils.AppLog;
import com.tegs.utils.Constants;
import com.tegs.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

/**
 * Created
 * by heena on 28/12/17.
 */

public class InstallationQuotesActivity extends BaseActivity implements View.OnClickListener {
    private ActivityInstallationQuotesBinding binding;
    private InstallationQuotesAdapter installationQuotesAdapter;
    private String TAG = InstallationQuotesActivity.class.getSimpleName();
    private Calendar myCalendar;
    private List<GetInstallationQuotesData> dataList = new ArrayList<>();
    private MenuItem menuAscendingSorting, menuDescendingSorting;
    private List<SetAnswerEntity> pendingDataList;
    private int REQ_QUOTES = 101;
    private int REQ_VIEW_EDIT = 102;
    private List<SetAnswerEntity> ascendingList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myCalendar = Calendar.getInstance();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_installation_quotes);
        //Fetching Pending List
        pendingDataList = DBSetAnsMethodSync.fetchData(AppDatabase.getAppDatabase(this));
        initToolbar(this);
        setToolbarTitle(getString(R.string.title_installation_quotes));
        findValues();
        callGetAnswersList(); //Call API to get all the Question Answer List
        //To create a new Quotation Button
        binding.imgAddInstallationQuotes.setOnClickListener(this);
        binding.relSearching.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_sorting, menu);
        menuAscendingSorting = menu.findItem(R.id.menu_sorting_ascending);
        menuDescendingSorting = menu.findItem(R.id.menu_sorting_descending);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sorting_ascending:
                menuDescendingSorting.setVisible(true);
                menuAscendingSorting.setVisible(false);
                List<SetAnswerEntity> ascending = new ArrayList<>();
                if (dataList.get(0) != null) {
                    ascending.addAll(dataList.get(0).getData());
                }
                ascending.addAll(pendingDataList);
                Collections.sort(ascending, new Comparator<SetAnswerEntity>() {
                    @Override
                    public int compare(SetAnswerEntity o1, SetAnswerEntity o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });
                installationQuotesAdapter.clearList();
                installationQuotesAdapter.addList(ascending);
                break;
            case R.id.menu_sorting_descending:
                menuDescendingSorting.setVisible(false);
                menuAscendingSorting.setVisible(true);
                List<SetAnswerEntity> descending = new ArrayList<>();
                if (dataList.get(0) != null) {
                    descending.addAll(dataList.get(0).getData());
                }
                descending.addAll(pendingDataList);
                Collections.sort(descending, new Comparator<SetAnswerEntity>() {
                    @Override
                    public int compare(SetAnswerEntity o1, SetAnswerEntity o2) {
                        return o2.getDate().compareTo(o1.getDate());
                    }
                });
                installationQuotesAdapter.clearList();
                installationQuotesAdapter.addList(descending);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_add_installation_quotes:
                startActivityForResult(new Intent(InstallationQuotesActivity.this, NewQuotesActivity.class), REQ_QUOTES);
                break;
            case R.id.relSearching:
                //Click on Search Bar and selecting date
                DatePickerDialog dp = new DatePickerDialog(this, R.style.CalenderDialogTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dp.show();
                break;
            case R.id.img_cancel:
                //Set a cancel icon when user selects date.
                onCancel();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_QUOTES) {
                installationQuotesAdapter.clearList();
                setPendingData();
                callGetAnswersList();
            }
            if (requestCode == REQ_VIEW_EDIT) {
                setPendingData();
                callGetAnswersList();
            }
        }
    }

    private void findValues() {
        //Setting Recycler View
        installationQuotesAdapter = new InstallationQuotesAdapter(this, new ArrayList<SetAnswerEntity>());
        binding.recViewInstallQuotes.setLayoutManager(new LinearLayoutManager(InstallationQuotesActivity.this));
        binding.recViewInstallQuotes.setAdapter(installationQuotesAdapter);
        binding.swipeQuote.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                installationQuotesAdapter.clearList();
                callGetAnswersList();
                binding.txtSearchText.setText("");
                binding.imgCancel.setVisibility(View.GONE);
                binding.imgSearch.setVisibility(View.VISIBLE);
                binding.swipeQuote.setRefreshing(false);
            }
        });
    }

    private void setPendingData() {
        Utils.dismissDialog();
        //During no internet connection Add all local data in list
        installationQuotesAdapter.addList(pendingDataList);

        installationQuotesAdapter.setOnClickQues(new InstallationQuotesAdapter.onClickQues() {
            @Override
            public void onClickQues(int quoteId, String status) {
                if (status.equals(Constants.PENDINGSTATUS)) {
                    GetInstallationQuotesData pendingList = new GetInstallationQuotesData();
                    Intent intent = new Intent(InstallationQuotesActivity.this, ViewEditQuesAnsActivity.class);
                    //pendingDataList gives total rows which are stored locally
                    for (int findId = 0; findId < pendingDataList.size(); findId++) {
                        if (quoteId == pendingDataList.get(findId).getRow_id()) {
                            AppLog.d(TAG, "Row_ID(Pending Data)=" + pendingDataList.get(findId).getRow_id());
                            intent.putExtra(Constants.ANS_ARRAY, pendingDataList.get(findId));
                            pendingList.setData(pendingDataList);
                            intent.putExtra(Constants.ROWID, quoteId);
                            intent.putExtra(Constants.PENDING_Ans_List, pendingList);
                        }
                    }
                    startActivityForResult(intent, REQ_VIEW_EDIT);
                } else if (status.equals(Constants.ONLINESTATUS)) {
                    Intent intent = new Intent(InstallationQuotesActivity.this, ViewEditQuesAnsActivity.class);
                    if (quoteId != 0) {
                        for (int findId = 0; findId < dataList.get(0).getData().size(); findId++) {
                            //Finding the answers list by matching the quoteID and fetched data from WebAPI.
                            if (dataList.get(0).getData().get(findId).getId() == quoteId) {
                                intent.putExtra(Constants.QUOTEID, dataList.get(0).getData().get(findId).getId());
                                intent.putExtra(Constants.ANS_ARRAY, dataList.get(0).getData().get(findId));
                                intent.putExtra(Constants.FULL_ANS_LIST, dataList.get(0));
                            }
                        }
                    }
                    startActivityForResult(intent, REQ_VIEW_EDIT);
                }
            }

            @Override
            public void onButtonClick(int rowID) {
                Utils.showProgressDialog(InstallationQuotesActivity.this);
                if (Utils.isConnected(InstallationQuotesActivity.this)) {
                    Utils.dismissDialog();
                    //Fetching particular row from db and set into API .
                    SetAnswerEntity answerList = DBSetAnsMethodSync.fetchAt(AppDatabase.getAppDatabase(InstallationQuotesActivity.this), rowID);
                    setPendingAnsToSubmit(answerList, rowID);

                } else {
                    Utils.dismissDialog();
                    Utils.showSnackBar(InstallationQuotesActivity.this, getString(R.string.err_internet));
                }
            }
        });
    }


    //Calling the API to get List
    private void callGetAnswersList() {
        Utils.showProgressDialog(this);
        setPendingData();
        Call<GetInstallationQuotesData> call = RestClient.getInstance(true).getApiInterface().GetAnswersListWS(Utils.getRequestMap(true));

        //Calling Webservice when internet is connected
        RestClient.makeApiRequest(InstallationQuotesActivity.this, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, final Object response) {

                try {


                    final GetInstallationQuotesData result = (GetInstallationQuotesData) response;
                    AppLog.d(TAG, getString(R.string.success_response));

                    if (result.getStatus() == RequestParameters.STATUS) {
                        Utils.dismissDialog();
                        dataList.add(result);
                        installationQuotesAdapter.addList(result.getData());
                        installationQuotesAdapter.setOnClickQues(new InstallationQuotesAdapter.onClickQues() {
                            @Override
                            public void onClickQues(int quoteId, String status) {
                                if (status.equals(Constants.PENDINGSTATUS)) {
                                    GetInstallationQuotesData pendingList = new GetInstallationQuotesData();
                                    Intent intent = new Intent(InstallationQuotesActivity.this, ViewEditQuesAnsActivity.class);
                                    //pendingDataList gives total rows which are stored locally
                                    for (int findId = 0; findId < pendingDataList.size(); findId++) {
                                        if (quoteId == pendingDataList.get(findId).getRow_id()) {
                                            AppLog.d(TAG, "Row_ID(Pending Data)=" + pendingDataList.get(findId).getRow_id());
                                            intent.putExtra(Constants.ANS_ARRAY, pendingDataList.get(findId));
                                            pendingList.setData(pendingDataList);
                                            intent.putExtra(Constants.ROWID, quoteId);
                                            intent.putExtra(Constants.PENDING_Ans_List, pendingList);
                                        }
                                    }
                                    startActivityForResult(intent, REQ_VIEW_EDIT);
                                } else if (status.equals(Constants.ONLINESTATUS)) {
                                    Intent intent = new Intent(InstallationQuotesActivity.this, ViewEditQuesAnsActivity.class);
                                    if (quoteId != 0) {
                                        for (int findId = 0; findId < result.getData().size(); findId++) {
                                            //Finding the answers list by matching the quoteID and fetched data from WebAPI.
                                            if (result.getData().get(findId).getId() == quoteId) {
                                                intent.putExtra(Constants.QUOTEID, result.getData().get(findId).getId());
                                                intent.putExtra(Constants.ANS_ARRAY, result.getData().get(findId));
                                                intent.putExtra(Constants.FULL_ANS_LIST, result);
                                            }
                                        }
                                    }
                                    startActivity(intent);
                                }

                            }


                            //This method is for thoses who's status is pending and user want's to submit it if having an internet connection
                            @Override
                            public void onButtonClick(int rowID) {
                                Utils.showProgressDialog(InstallationQuotesActivity.this);
                                if (Utils.isConnected(InstallationQuotesActivity.this)) {
                                    Utils.dismissDialog();
                                    //Fetching particular row from db and set into API .
                                    SetAnswerEntity answerList = DBSetAnsMethodSync.fetchAt(AppDatabase.getAppDatabase(InstallationQuotesActivity.this), rowID);
                                    setPendingAnsToSubmit(answerList, rowID);
                                } else {
                                    Utils.dismissDialog();
                                    Utils.showSnackBar(InstallationQuotesActivity.this, getString(R.string.err_internet));
                                }
                            }
                        });
                    }


                    //Check if same user is login on another device
                    if (result.getStatus() == RequestParameters.STATUS_403) {
                        Utils.dismissDialog();
                        Utils.logout(InstallationQuotesActivity.this);
                    }
                    //Check if data is null (Answer's List)
                    if (installationQuotesAdapter.getItemCount() > 0) {
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

    //Call this webservice when user clicks on pending button and internet is connected then this is to send answers
    private void setPendingAnsToSubmit(SetAnswerEntity pendingAnswer, final int rowID) {
        Utils.showProgressDialog(InstallationQuotesActivity.this);
        String answerJSON = new Gson().toJson(pendingAnswer.getAnswers());
        Call<SetAnswers> call = RestClient.getInstance(true).getApiInterface().SetAnswersListWS(Utils.getRequestMap(true), pendingAnswer.getDate(), pendingAnswer.getTitle(), answerJSON);
        RestClient.makeApiRequest(InstallationQuotesActivity.this, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                AppLog.d(TAG, getString(R.string.success_response));
                Utils.dismissDialog();
                SetAnswers result = (SetAnswers) response;
                if (result.getStatus() == RequestParameters.STATUS) {
                    AppLog.d(TAG, getString(R.string.success_result));
                    DBSetAnsMethodSync.deleteDataAt(AppDatabase.getAppDatabase(InstallationQuotesActivity.this), rowID);
                    Utils.showSnackBar(InstallationQuotesActivity.this, getString(R.string.ques_ans_submitted_success));
                    installationQuotesAdapter.clearList();
                    callGetAnswersList();
                } else {
                    Utils.dismissDialog();
                    AppLog.d(TAG, "onError");
                }
            }

            @Override
            public void onApiError(Call<Object> call, Throwable throwable) {
                AppLog.d(TAG, "onApiError");
            }
        });
    }

    private void search() {
        String searchingDate = binding.txtSearchText.getText().toString();
        List<SetAnswerEntity> searchedList = new ArrayList<>();
        if (pendingDataList.size() != 0) {
            for (SetAnswerEntity pendingList : pendingDataList) {
                String date = Utils.convertDateFormat("yyyy-MM-dd", "dd MMMM yyyy", pendingList.getDate());
                if (date.equals(searchingDate)) {
                    searchedList.add(pendingList);
                }
            }

        }
        if (dataList.size() != 0) {
            List<SetAnswerEntity> list = dataList.get(0).getData();
            for (SetAnswerEntity d : list) {
                String date = Utils.convertDateFormat("yyyy-MM-dd", "dd MMMM yyyy", d.getDate());
                if (date.equals(searchingDate)) {
                    searchedList.add(d);
                }
            }
        }
        installationQuotesAdapter = new InstallationQuotesAdapter(this, new ArrayList<SetAnswerEntity>());
        installationQuotesAdapter.clearList();
        installationQuotesAdapter.addList(searchedList);
        binding.recViewInstallQuotes.setAdapter(installationQuotesAdapter);
        installationQuotesAdapter.setOnClickQues(new InstallationQuotesAdapter.onClickQues() {
            @Override
            public void onClickQues(int quoteId, String status) {
                if (status.equals(Constants.PENDINGSTATUS)) {
                    GetInstallationQuotesData pendingList = new GetInstallationQuotesData();
                    Intent intent = new Intent(InstallationQuotesActivity.this, ViewEditQuesAnsActivity.class);
                    //pendingDataList gives total rows which are stored locally
                    for (int findId = 0; findId < pendingDataList.size(); findId++) {
                        if (quoteId == pendingDataList.get(findId).getRow_id()) {
                            AppLog.d(TAG, "Row_ID(Pending Data)=" + pendingDataList.get(findId).getRow_id());
                            intent.putExtra(Constants.ANS_ARRAY, pendingDataList.get(findId));
                            pendingList.setData(pendingDataList);
                            intent.putExtra(Constants.ROWID, quoteId);
                            intent.putExtra(Constants.PENDING_Ans_List, pendingList);
                        }
                    }
                    startActivityForResult(intent, REQ_VIEW_EDIT);
                } else if (status.equals(Constants.ONLINESTATUS)) {
                    Intent intent = new Intent(InstallationQuotesActivity.this, ViewEditQuesAnsActivity.class);
                    if (quoteId != 0) {
                        for (int findId = 0; findId < dataList.get(0).getData().size(); findId++) {
                            //Finding the answers list by matching the quoteID and fetched data from WebAPI.
                            if (dataList.get(0).getData().get(findId).getId() == quoteId) {
                                intent.putExtra(Constants.QUOTEID, dataList.get(0).getData().get(findId).getId());
                                intent.putExtra(Constants.ANS_ARRAY, dataList.get(0).getData().get(findId));
                                intent.putExtra(Constants.FULL_ANS_LIST, dataList.get(0));
                            }
                        }
                    }
                    startActivityForResult(intent, REQ_VIEW_EDIT);
                }
            }

            @Override
            public void onButtonClick(int rowID) {
                Utils.showProgressDialog(InstallationQuotesActivity.this);
                if (Utils.isConnected(InstallationQuotesActivity.this)) {
                    Utils.dismissDialog();
                    //Fetching particular row from db and set into API .
                    SetAnswerEntity answerList = DBSetAnsMethodSync.fetchAt(AppDatabase.getAppDatabase(InstallationQuotesActivity.this), rowID);
                    setPendingAnsToSubmit(answerList, rowID);

                } else {
                    Utils.dismissDialog();
                    Utils.showSnackBar(InstallationQuotesActivity.this, getString(R.string.err_internet));
                }
            }
        });
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private void onCancel() {
        binding.txtSearchText.setText("");
        binding.imgSearch.setVisibility(View.VISIBLE);
        binding.imgCancel.setVisibility(View.GONE);
        installationQuotesAdapter.clearList();
        callGetAnswersList();
    }

    private void updateLabel() {
        String myFormat = "dd MMMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Log.e("UserSelected Date:", String.valueOf(myCalendar.getTime()));
        binding.txtSearchText.setText(sdf.format(myCalendar.getTime()));
        if (!binding.txtSearchText.getText().toString().equals("")) {
            binding.imgSearch.setVisibility(View.GONE);
            binding.imgCancel.setVisibility(View.VISIBLE);
        }
        search();
        binding.imgCancel.setOnClickListener(this);
    }
}
