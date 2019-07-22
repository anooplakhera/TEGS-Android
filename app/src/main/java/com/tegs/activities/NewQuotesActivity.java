package com.tegs.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;

import com.tegs.DatabaseHandler.SetAnswerEntity;
import com.tegs.R;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivityNewQuoteBinding;
import com.tegs.model.SetAnswers;
import com.tegs.utils.AppLog;
import com.tegs.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 *
 * Created by heena on 29/12/17.
 */

public class NewQuotesActivity extends BaseActivity implements View.OnClickListener {
    private ActivityNewQuoteBinding binding;
    private Calendar myCalendar = Calendar.getInstance();
    private String TAG = NewQuotesActivity.class.getSimpleName();
    private int REQUESTCODE = 11;
    private int SUCCESS_SUBMIT_ANS = 1;
    private boolean submitAns = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_quote);
        //Setting ToolBar
        initToolbar(this);
        setToolbarTitle(getString(R.string.title_new_quote));
        getTodayDate();
        onClickEvents();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_date:
                DatePickerDialog dp = new DatePickerDialog(NewQuotesActivity.this, R.style.CalenderDialogTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dp.getDatePicker().setMinDate(System.currentTimeMillis());
//                dp.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                dp.show();
                break;
            case R.id.btn_next:
                if (validation()) {
                    AppLog.d(TAG + "Date:", binding.txtDate.getText().toString().trim());
                    AppLog.d(TAG + "QuoteTitle:", binding.etQuoteTitle.getText().toString().trim());
                    SetAnswers setAnswers = new SetAnswers();
                    SetAnswerEntity setAnswerEntity = new SetAnswerEntity();

                    String date = Utils.convertDateFormat("dd MMMM yyyy", "yyyy-MM-dd", binding.txtDate.getText().toString().trim());

                    //Setting the date and Title to send when calling on webservice.
                    setAnswers.setDate(date);
                    setAnswers.setTitle(binding.etQuoteTitle.getText().toString().trim());

                    //Setting the date and Title to send when calling on webservice.
                    setAnswerEntity.setDate(date);
                    setAnswerEntity.setTitle(binding.etQuoteTitle.getText().toString().trim());

                    Intent intent = new Intent(NewQuotesActivity.this, QuestionAnswerListActivity.class);
                    intent.putExtra("AnswerList", setAnswers);
                    intent.putExtra("AnswerListForEntity",setAnswerEntity);
                    startActivityForResult(intent,REQUESTCODE);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUESTCODE){
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }

    private void onClickEvents() {
        if(submitAns){
            Intent intent = new Intent();
            setResult(RESULT_OK,intent);
            finish();
        }
        binding.txtDate.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);
    }

    private boolean validation() {
        if (TextUtils.isEmpty(binding.txtDate.getText().toString().trim())) {
            Utils.hideKeyboard(this);
            Utils.showSnackBar(this, getString(R.string.err_date));
            binding.txtDate.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(binding.etQuoteTitle.getText().toString().trim())) {
            Utils.hideKeyboard(this);
            Utils.showSnackBar(this, getString(R.string.err_quote_title));
            binding.etQuoteTitle.requestFocus();
            return false;
        }
        return true;
    }

    private void updateLabel() {
        String myFormat = "dd MMMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.txtDate.setText(sdf.format(myCalendar.getTime()));
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

    public void getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd MMMM yyyy");
        String strDate =mdformat.format(calendar.getTime());
        binding.txtDate.setText(strDate);
    }
}
