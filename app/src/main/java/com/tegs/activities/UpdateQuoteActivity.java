package com.tegs.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.tegs.DatabaseHandler.AppDatabase;
import com.tegs.DatabaseHandler.DBSetAnsMethodSync;
import com.tegs.DatabaseHandler.SetAnsChildEntity;
import com.tegs.DatabaseHandler.SetAnswerEntity;
import com.tegs.QuestionAnswerModel.QuestionAnswerModel;
import com.tegs.R;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivityUpdateQuestionBinding;
import com.tegs.model.GetInstallationQuotesData;
import com.tegs.model.SetAnswers;
import com.tegs.retrofit.ApiResponseListener;
import com.tegs.retrofit.RestClient;
import com.tegs.utils.AppLog;
import com.tegs.utils.Constants;
import com.tegs.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created
 * by heena on 27/2/18.
 */


/*
Question Type:
Q1 = Only Question Text
Q2 = Question Text is with Youtube Link
Q3 = Question Text with Image
Answer Type:
A1 = Options as Radio Button
A2 = " Image with Radio Button
A3 = " Image with CheckBox
A4 = " Image with radio Button with TextBox
A5 = " Radio Button with TextBox
A6 = " CheckBox
A7 = " Image with CheckBox
A8 = " CheckBox with textarea
A9 = " TextArea
*/

public class UpdateQuoteActivity extends BaseActivity {

    private String TAG = UpdateQuoteActivity.class.getSimpleName();
    private int questionID;
    private ActivityUpdateQuestionBinding binding;
    private List<Integer> selectedAnswerIdList;
    private String others;
    private String updatedOther;
    private String[] questionsType = {"Q0", "Q1", "Q2", "Q3"};
    private String[] answersType = {"A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9"};
    private String getAnswerType;
    private int quoteID;
    private SetAnswerEntity fetchLocalSetAns;
    private GetInstallationQuotesData getQuesAnsSubmittedList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_question);

        //Fetching Quote ID (It would be zero when user clicks on Locally Submitted Ans)
        quoteID = getIntent().getIntExtra(Constants.QUOTEID, 0);
        AppLog.d(TAG, "QuoteID=" + quoteID);

        //Fetching Row ID (It would be zero when user clicks on Online Submitted Ans)
        int rowID = getIntent().getIntExtra(Constants.ROWID, 0);
        AppLog.d(TAG, "RowID=" + rowID);

        //Fetching Whole Ques Ans List from Locally
        fetchLocalSetAns = DBSetAnsMethodSync.fetchAt(AppDatabase.getAppDatabase(this), rowID);

        //Fetching Whole Ques Ans List
        getQuesAnsSubmittedList = (GetInstallationQuotesData) getIntent().getSerializableExtra(Constants.FULL_ANS_LIST);

        //Fetching QuestionID
        questionID = getIntent().getExtras().getInt("EditingQuestionID");

        //Fetching Answers Array
        selectedAnswerIdList = getIntent().getExtras().getIntegerArrayList("answerIDList");

        //Fetching Others Text
        others = getIntent().getStringExtra("ansOthers");

        if (questionID != 0) {
            AppLog.d("UpdateQuesID=", String.valueOf(questionID));
        }
        setValues();
    }

    private void setValues() {
        //Getting QuesAnsModel from SharedPref Storage
        final QuestionAnswerModel questionAnswerModel = Utils.getQuestionAnswerData();
        for (int fetchQuesID = 0; fetchQuesID < questionAnswerModel.getData().size(); fetchQuesID++) {
            for (int ques = 0; ques < questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().size(); ques++) {
                int fetchQuestionID = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getQuestion().getQuestionId();
                if (fetchQuestionID == questionID) {
                    String title = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getTitle();
                    String instruction = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getInstruction();
                    String instructionImage = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getImage();
                    String getQuestionType = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getQuestionType();
                    getAnswerType = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getAnswerType();

                    String questionText = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getQuestion().getText();
                    String questionImageName = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getQuestion().getLink();
                    String youtubeLink = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getQuestion().getLink();
                    final int optionSize = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().size();

                    //Setting Toolbar
                    initToolbar(this);
                    setToolbarTitle(title);

                    //If instruction exists!
                    if (instruction.equals("")) {
                        binding.txtQuesInstruction.setVisibility(View.GONE);
                    } else {
                        binding.txtQuesInstruction.setVisibility(View.VISIBLE);
                        binding.txtQuesInstruction.setText(instruction);
                    }
                    //If instruction image exists!
                    if (instructionImage.equals("")) {
                        binding.sdvInstructionImage.setVisibility(View.GONE);
                    } else {
                        binding.sdvInstructionImage.setVisibility(View.VISIBLE);
                        int resID = getResources().getIdentifier(getImage(instructionImage), "drawable", (this.getPackageName()));
                        binding.sdvInstructionImage.setImageResource(resID);
                    }

                    //Finding Question Type and Fetching Question Type

                    /*
                    QUESTION TYPE-1
                    */
                    binding.txtQuestion.setText(questionText); //Set Question Text

                    /*
                    QUESTION TYPE-2
                    */
                    if (getQuestionType.equals(questionsType[2])) {  //Question Text With Youtube Link
                        binding.txtYoutubeLink.setVisibility(View.VISIBLE);
                        binding.txtYoutubeLink.setText(youtubeLink);
                    }

                    /*
                    QUESTION TYPE-3
                    */

                    if (getQuestionType.equals(questionsType[3])) { //Question Text with Image
                        binding.sdvQuestionsImage.setVisibility(View.VISIBLE);
                        int resID = getResources().getIdentifier(getImage(questionImageName), "drawable", (this.getPackageName()));
                        binding.sdvQuestionsImage.setImageResource(resID);
                    }


                    /*
                    ANSWER TYPE-1 Simple RadioButton
                    */

                    if (getAnswerType.equals(answersType[1])) {
                        binding.lnrRadioImage.setVisibility(View.GONE);
                        binding.lnrRadio.setVisibility(View.VISIBLE);
                        RadioGroup radioGroup = new RadioGroup(this);
                        radioGroup.setTag("radioGroup" + ques);
                        binding.lnrRadio.addView(radioGroup);
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        for (int radioBtn = 0; radioBtn < optionSize; radioBtn++) {
                            AppCompatRadioButton radioButton = (AppCompatRadioButton) this.getLayoutInflater().inflate(R.layout.view_radio, radioGroup, false);
                            radioButton.setText(questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(radioBtn).getText());
                            radioButton.setId(radioBtn + 1);
                            radioButton.setLayoutParams(param);
                            if (selectedAnswerIdList.size() != 0) {
                                int selectedID = selectedAnswerIdList.get(0);
                                if (radioBtn + 1 == selectedID) {
                                    radioButton.setChecked(true);
                                }
                            }
                            radioGroup.addView(radioButton);
                        }
                    }

                    /*
                    ANSWER TYPE-2 Image with RadioButton
                    */
                    else if (getAnswerType.equals(answersType[2])) {
                        String radioImage;
                        binding.lnrRadio.setVisibility(View.VISIBLE);
                        binding.lnrRadioImage.setVisibility(View.VISIBLE);
                        RadioGroup radioGroup = new RadioGroup(this);
                        radioGroup.setTag(Constants.RADIO_GROUP_WITH_IMAGE + ques);
                        binding.lnrRadio.addView(radioGroup);
                        for (int radioBtn = 0; radioBtn < optionSize; radioBtn++) {
                            //Fetching the name of image
                            radioImage = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(radioBtn).getImage();
                            AppCompatRadioButton radioButton = (AppCompatRadioButton) this.getLayoutInflater().inflate(R.layout.view_radio, radioGroup, false);
                            View viewCardView = this.getLayoutInflater().inflate(R.layout.view_card_view_image, binding.lnrRadioImage, false);
                            final SimpleDraweeView sdvRadioImage = viewCardView.findViewById(R.id.sdv_image);
                            int resID = getResources().getIdentifier(getImage(radioImage), "drawable", (this.getPackageName()));
                            sdvRadioImage.setImageResource(resID);
                            if (radioImage.equals("")) {
                                viewCardView.setVisibility(View.INVISIBLE);
                            } else {
                                viewCardView.setVisibility(View.VISIBLE);
                            }
                            radioButton.setText(questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(radioBtn).getText());
                            radioButton.setId(radioBtn + 1);
                            radioGroup.addView(radioButton);
                            if (selectedAnswerIdList.size() != 0) {
                                int selectedID = selectedAnswerIdList.get(0);
                                if (radioBtn + 1 == selectedID) {
                                    radioButton.setChecked(true);
                                    sdvRadioImage.setPadding(7, 7, 7, 7);
                                }
                            }
                            sdvRadioImage.setScaleType(ImageView.ScaleType.FIT_XY);
                            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        sdvRadioImage.setPadding(7, 7, 7, 7);
                                    } else {
                                        sdvRadioImage.setPadding(0, 0, 0, 0);
                                    }
                                }
                            });
                            binding.lnrRadioImage.addView(viewCardView);
                        }
                    }

                    /*
                    ANSWER TYPE-3 CheckBox with Images
                    */
                    else if (getAnswerType.equals(answersType[3])) {
                        String checkBoxImage;
                        int selectedID = 0;
                        binding.lnrCheckbox.setVisibility(View.VISIBLE);
                        binding.lnrCheckboxImage.setVisibility(View.VISIBLE);
                        for (int option = 0; option < optionSize; option++) {
                            LinearLayout.LayoutParams checkBoxImageParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            final SimpleDraweeView sdvCheckBoxImage = new SimpleDraweeView(this);
                            final CardView cardView = new CardView(this);
                            cardView.setRadius(15f);
                            cardView.setCardBackgroundColor(getResources().getColor(R.color.colorOrange));
                            AppCompatCheckBox checkBoxView = (AppCompatCheckBox) this.getLayoutInflater().inflate(R.layout.view_checkbox, binding.lnrCheckbox, false);
                            checkBoxView.setText(questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(option).getText());
                            checkBoxImage = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(option).getImage();
                            for (int selAns = 0; selAns < selectedAnswerIdList.size(); selAns++) {
                                if (selectedAnswerIdList.get(selAns) != null) {
                                    selectedID = selectedAnswerIdList.get(selAns);
                                    AppLog.d("Type-3 SelectedID=", String.valueOf(selectedID));
                                    if (option + 1 == selectedID) {
                                        checkBoxView.setChecked(true);
                                        sdvCheckBoxImage.setPadding(7, 7, 7, 7);
                                    }
                                }
                            }
                            int resID = getResources().getIdentifier(getImage(checkBoxImage), "drawable", this.getPackageName());
                            sdvCheckBoxImage.setScaleType(ImageView.ScaleType.FIT_XY);
                            sdvCheckBoxImage.setImageResource(resID);
                            if (checkBoxImage.equals("")) {
                                sdvCheckBoxImage.setVisibility(View.INVISIBLE);
                            } else {
                                sdvCheckBoxImage.setVisibility(View.VISIBLE);
                            }
                            checkBoxImageParam.setMargins(0, 15, 0, 5);
                            checkBoxView.setTag(option);
                            checkBoxView.setId(option);
                            cardView.setLayoutParams(checkBoxImageParam);
                            cardView.addView(sdvCheckBoxImage);
                            checkBoxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        sdvCheckBoxImage.setPadding(7, 7, 7, 7);
                                    } else {
                                        sdvCheckBoxImage.setPadding(0, 0, 0, 0);
                                    }
                                }
                            });
                            binding.lnrCheckboxImage.addView(cardView);
                            binding.lnrCheckbox.addView(checkBoxView);
                        }
                    }

                    /*
                    ANSWER TYPE-4 Image with radio button with textbox
                    */
                    else if (getAnswerType.equals(answersType[4])) {
                        String radioImage;
                        binding.lnrRadio.setVisibility(View.VISIBLE);
                        binding.lnrRadioImage.setVisibility(View.VISIBLE);
                        RadioGroup radioGroup = new RadioGroup(this);
                        radioGroup.setTag(Constants.RADIO_GROUP_WITH_IMAGE + ques);
                        binding.lnrRadio.addView(radioGroup);
                        for (int radioBtn = 0; radioBtn < optionSize; radioBtn++) {
                            View viewCardView = this.getLayoutInflater().inflate(R.layout.view_card_view_image, binding.lnrRadioImage, false);
                            final SimpleDraweeView sdvRadioImage = viewCardView.findViewById(R.id.sdv_image);
                            radioImage = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(radioBtn).getImage();
                            int resID = getResources().getIdentifier(getImage(radioImage), "drawable", this.getPackageName());
                            sdvRadioImage.setImageResource(resID);
                            if (radioImage.equals("")) {
                                viewCardView.setVisibility(View.INVISIBLE);
                            } else {
                                viewCardView.setVisibility(View.VISIBLE);
                            }
                            AppCompatRadioButton radioButton = (AppCompatRadioButton) this.getLayoutInflater().inflate(R.layout.view_radio, radioGroup, false);
                            radioButton.setText(questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(radioBtn).getText());
                            radioButton.setId(radioBtn + 1);
                            if (selectedAnswerIdList.size() != 0) {
                                int selectedID = selectedAnswerIdList.get(0);
                                if (radioBtn + 1 == selectedID) {
                                    radioButton.setChecked(true);
                                    sdvRadioImage.setPadding(7, 7, 7, 7);
                                }
                            }
                            radioGroup.addView(radioButton);
                            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        sdvRadioImage.setPadding(7, 7, 7, 7);
                                    } else {
                                        sdvRadioImage.setPadding(0, 0, 0, 0);
                                    }
                                }
                            });
                            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    if (checkedId == optionSize) {
                                        binding.etTextMsg.setVisibility(View.VISIBLE);
                                        binding.etTextMsg.setText(others);
                                    } else {
                                        binding.etTextMsg.setVisibility(View.GONE);
                                    }
                                }
                            });
                            binding.lnrRadioImage.addView(viewCardView);
                        }
                    }

                    /*
                    ANSWER TYPE-5 Radio button with textbox
                    */
                    else if (getAnswerType.equals(answersType[5])) {
                        binding.lnrRadio.setVisibility(View.VISIBLE);
                        binding.etTextMsg.setVisibility(View.VISIBLE);
                        RadioGroup radioGroup = new RadioGroup(this);
                        radioGroup.setTag("radioGroup" + ques);
                        binding.lnrRadio.addView(radioGroup);
                        for (int radioBtn = 0; radioBtn < optionSize; radioBtn++) {
                            AppCompatRadioButton radioButton = (AppCompatRadioButton) this.getLayoutInflater().inflate(R.layout.view_radio, radioGroup, false);
                            radioButton.setText(questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(radioBtn).getText());
                            radioButton.setId(radioBtn + 1);
                            if (selectedAnswerIdList.size() != 0) {
                                int selectedID = selectedAnswerIdList.get(0);
                                if (radioBtn + 1 == selectedID) {
                                    radioButton.setChecked(true);
                                }
                            }
                            LinearLayout.LayoutParams radioButtonParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); //change
                            radioButton.setLayoutParams(radioButtonParam);

                            radioGroup.addView(radioButton);
                            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    if (checkedId == optionSize) {
                                        binding.etTextMsg.setVisibility(View.VISIBLE);
                                        binding.etTextMsg.setText(others);
                                    } else {
                                        binding.etTextMsg.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }

                    /*
                    ANSWER TYPE - 6 Simple CheckBox
                    */
                    else if (getAnswerType.equals(answersType[6])) {
                        binding.lnrCheckbox.setVisibility(View.VISIBLE);
                        for (int row = 0; row < optionSize; row++) {
                            AppCompatCheckBox checkBoxView = (AppCompatCheckBox) this.getLayoutInflater().inflate(R.layout.view_checkbox, binding.lnrCheckbox, false);
                            checkBoxView.setText(questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(row).getText());
                            CheckBox checkBox = new CheckBox(this);
                            checkBoxView.setTag("checkbox" + row);
                            checkBoxView.setId(row);
                            for (int selID = 0; selID < selectedAnswerIdList.size(); selID++) {
                                if (selectedAnswerIdList.get(selID) != null) {
                                    int selectedID = selectedAnswerIdList.get(selID);
                                    AppLog.d("Type-6 selected Ans", String.valueOf(selectedID));
                                    if (row + 1 == selectedID) {
                                        checkBoxView.setChecked(true);
                                    }
                                }
                            }
                            checkBox.setButtonDrawable(R.drawable.checkbox_background);
                            binding.lnrCheckbox.addView(checkBoxView);
                        }
                    }

                    /*
                    ANSWER TYPE-3 CheckBox with Images
                    */
                    else if (getAnswerType.equals(answersType[3])) {
                        String checkBoxImage;
                        int selectedID = 0;
                        binding.lnrCheckbox.setVisibility(View.VISIBLE);
                        binding.lnrCheckboxImage.setVisibility(View.VISIBLE);
                        for (int option = 0; option < optionSize; option++) {
                            LinearLayout.LayoutParams checkBoxImageParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            final SimpleDraweeView sdvCheckBoxImage = new SimpleDraweeView(this);
                            final CardView cardView = new CardView(this);
                            cardView.setRadius(15f);
                            cardView.setCardBackgroundColor(getResources().getColor(R.color.colorOrange));
                            AppCompatCheckBox checkBoxView = (AppCompatCheckBox) this.getLayoutInflater().inflate(R.layout.view_checkbox, binding.lnrCheckbox, false);
                            checkBoxView.setText(questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(option).getText());
                            checkBoxImage = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(option).getImage();
                            for (int selAns = 0; selAns < selectedAnswerIdList.size(); selAns++) {
                                if (selectedAnswerIdList.get(selAns) != null) {
                                    selectedID = selectedAnswerIdList.get(selAns);
                                    AppLog.d("Type-3 SelectedID=", String.valueOf(selectedID));
                                    if (option + 1 == selectedID) {
                                        checkBoxView.setChecked(true);
                                        sdvCheckBoxImage.setPadding(7, 7, 7, 7);
                                    }
                                }
                            }
                            int resID = getResources().getIdentifier(getImage(checkBoxImage), "drawable", this.getPackageName());
                            sdvCheckBoxImage.setScaleType(ImageView.ScaleType.FIT_XY);
                            sdvCheckBoxImage.setImageResource(resID);
                            if (checkBoxImage.equals("")) {
                                sdvCheckBoxImage.setVisibility(View.INVISIBLE);
                            } else {
                                sdvCheckBoxImage.setVisibility(View.VISIBLE);
                            }
                            checkBoxImageParam.setMargins(0, 15, 0, 5);
                            checkBoxView.setTag(option);
                            checkBoxView.setId(option);
                            cardView.setLayoutParams(checkBoxImageParam);
                            cardView.addView(sdvCheckBoxImage);
                            checkBoxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        sdvCheckBoxImage.setPadding(7, 7, 7, 7);
                                    } else {
                                        sdvCheckBoxImage.setPadding(0, 0, 0, 0);
                                    }
                                }
                            });
                            binding.lnrCheckboxImage.addView(cardView);
                            binding.lnrCheckbox.addView(checkBoxView);
                        }
                    }

                    /*
                    ANSWER TYPE - 7 Simple CheckBox with images with textarea
                    */
                    else if (getAnswerType.equals(answersType[7])) {
                        String checkBoxImage;
                        int selectedID = 0;
                        binding.lnrCheckbox.setVisibility(View.VISIBLE);
                        binding.lnrCheckboxImage.setVisibility(View.VISIBLE);
                        for (int option = 0; option < optionSize; option++) {
                            LinearLayout.LayoutParams checkBoxImageParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            final SimpleDraweeView sdvCheckBoxImage = new SimpleDraweeView(this);
                            final CardView cardView = new CardView(this);
                            cardView.setRadius(15f);
                            cardView.setCardBackgroundColor(getResources().getColor(R.color.colorOrange));
                            AppCompatCheckBox checkBoxView = (AppCompatCheckBox) this.getLayoutInflater().inflate(R.layout.view_checkbox, binding.lnrCheckbox, false);
                            checkBoxView.setText(questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(option).getText());
                            checkBoxImage = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(option).getImage();
                            for (int selAns = 0; selAns < selectedAnswerIdList.size(); selAns++) {
                                if (selectedAnswerIdList.get(selAns) != null) {
                                    selectedID = selectedAnswerIdList.get(selAns);
                                    if (option + 1 == selectedID) {
                                        checkBoxView.setChecked(true);
                                        sdvCheckBoxImage.setPadding(7, 7, 7, 7);
                                        if (selectedID == optionSize) {
                                            binding.etTextMsg.setVisibility(View.VISIBLE);
                                        } else {
                                            binding.etTextMsg.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }

                            int resID = getResources().getIdentifier(getImage(checkBoxImage), "drawable", this.getPackageName());
                            sdvCheckBoxImage.setScaleType(ImageView.ScaleType.FIT_XY);
                            sdvCheckBoxImage.setImageResource(resID);
                            if (checkBoxImage.equals("")) {
                                cardView.setVisibility(View.INVISIBLE);
                                int transImg = getResources().getIdentifier(getImage("transparent_image.png"), "drawable", this.getPackageName());
                                sdvCheckBoxImage.setScaleType(ImageView.ScaleType.FIT_XY);
                                sdvCheckBoxImage.setImageResource(transImg);
                            } else {
                                sdvCheckBoxImage.setVisibility(View.VISIBLE);
                            }
                            checkBoxImageParam.setMargins(0, 15, 0, 5);
                            checkBoxView.setTag(option);
                            checkBoxView.setId(option);
                            cardView.setLayoutParams(checkBoxImageParam);
                            cardView.addView(sdvCheckBoxImage);
                            final int finalOption = option;
                            checkBoxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (finalOption == optionSize - 1) {
                                        if (isChecked) {
                                            binding.etTextMsg.setVisibility(View.VISIBLE);
                                        } else {
                                            binding.etTextMsg.setVisibility(View.GONE);
                                        }
                                    }

                                    if (isChecked) {
                                        sdvCheckBoxImage.setPadding(7, 7, 7, 7);
                                    } else {
                                        sdvCheckBoxImage.setPadding(0, 0, 0, 0);
                                    }
                                }
                            });
                            binding.lnrCheckboxImage.addView(cardView);
                            binding.lnrCheckbox.addView(checkBoxView);
                        }
                    }

                    /*
                    ANSWER TYPE - 8 Simple CheckBox with textarea
                    */
                    else if (getAnswerType.equals(answersType[8])) {
                        binding.lnrCheckbox.setVisibility(View.VISIBLE);
                        AppLog.d("TextMessage", binding.etTextMsg.getText().toString());
                        for (int row = 0; row < optionSize; row++) {
                            AppCompatCheckBox checkBoxView = (AppCompatCheckBox) this.getLayoutInflater().inflate(R.layout.view_checkbox, binding.lnrCheckbox, false);
                            checkBoxView.setText(questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(row).getText());
                            final CheckBox checkBox = new CheckBox(this);
                            checkBoxView.setTag("checkbox" + row);
                            for (int selID = 0; selID < selectedAnswerIdList.size(); selID++) {
                                if (selectedAnswerIdList.get(selID) != null) {
                                    int selectedID = selectedAnswerIdList.get(selID);
                                    if (row + 1 == selectedID) {
                                        checkBoxView.setChecked(true);
                                        binding.etTextMsg.setVisibility(View.GONE);
                                    }
                                    if (selectedID == optionSize) {
                                        binding.etTextMsg.setVisibility(View.VISIBLE);
                                        binding.etTextMsg.setText(others);
                                    }
                                }
                            }
                            checkBoxView.setId(row);
                            checkBox.setButtonDrawable(R.drawable.checkbox_background);
                            if (row + 1 == optionSize) {
                                checkBoxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {
                                            binding.etTextMsg.setVisibility(View.VISIBLE);
                                        } else {
                                            binding.etTextMsg.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }

                            binding.lnrCheckbox.addView(checkBoxView);
                        }
                    }


                     /*
                    ANSWER TYPE-9 Simple TextBox.
                    */
                    else if (getAnswerType.equals(answersType[9])) {
                        binding.etTextMsg.setVisibility(View.VISIBLE);
                        binding.etTextMsg.setText(others);
                    }
                }
            }
        }

        binding.btnUpdateQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int fetchQuesID = 0; fetchQuesID < questionAnswerModel.getData().size(); fetchQuesID++) {
                    List<Integer> updatedAns = new ArrayList<>();
                    for (int ques = 0; ques < questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().size(); ques++) {
                        int quesID = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getQuestion().getQuestionId();
                        if (questionID == quesID) {
                            final int ansOptionSize = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().size();
                            /*
                            ANSWER TYPE-1 Simple RadioButton
                            */
                            if (getAnswerType.equals(answersType[1])) {
                                RadioGroup radioGroup = binding.lnrRadio.findViewWithTag("radioGroup" + ques);
                                AppLog.d("SelectedRadioButton", String.valueOf(radioGroup.getCheckedRadioButtonId()));
                                int selectedID = radioGroup.getCheckedRadioButtonId();
                                int updatedAnswerID = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(selectedID - 1).getAnswerId();
                                updatedAns.add(updatedAnswerID);
                                AppLog.d("questionID,answerID", String.valueOf(questionID) + "," + updatedAnswerID);
                                updateAnswer(questionID, updatedAns, "");
                            }

                            /*
                            ANSWER TYPE-2 RadioButton with Image
                            */
                            else if (getAnswerType.equals(answersType[2])) {
                                RadioGroup radioGroup = binding.lnrRadio.findViewWithTag(Constants.RADIO_GROUP_WITH_IMAGE + ques);
                                AppLog.d("SelectedRadioButton", String.valueOf(radioGroup.getCheckedRadioButtonId()));
                                int selectedID = radioGroup.getCheckedRadioButtonId();
                                int updatedAnswerID = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(selectedID - 1).getAnswerId();
                                updatedAns.add(updatedAnswerID);
                                //Setting Values of QuestionId , others and answerList when internet is connected.
                                AppLog.d("questionID,answerID", String.valueOf(questionID) + "," + updatedAnswerID);
                                updateAnswer(questionID, updatedAns, "");
                            }

                            /*
                            ANSWER TYPE-3 Image with CheckBox
                            */
                            else if (getAnswerType.equals(answersType[3])) {
                                for (int checkbox = 0; checkbox < ansOptionSize; checkbox++) {
                                    CheckBox checkBox = findViewById(checkbox);
                                    if (checkBox.isChecked()) {
                                        int selectedID = checkBox.getId();
                                        int updatedAnswerID = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(selectedID).getAnswerId();
                                        updatedAns.add(updatedAnswerID);
                                        //Setting Values of QuestionId , others and answerList when internet is connected.
                                        AppLog.d("questionID,answerID", String.valueOf(questionID) + "," + updatedAnswerID);

                                    } else {
                                        Utils.showSnackBar(UpdateQuoteActivity.this, getString(R.string.err_this_is_required_question));
                                    }
                                }
                                updateAnswer(questionID, updatedAns, "");
                            }
                            /*
                            ANSWER TYPE-4 Image with Radio Button with textBox
                            */
                            else if (getAnswerType.equals(answersType[4])) {
                                RadioGroup radioGroup = binding.lnrRadio.findViewWithTag(Constants.RADIO_GROUP_WITH_IMAGE + ques);
                                AppLog.d("SelectedRadioButton", String.valueOf(radioGroup.getCheckedRadioButtonId()));
                                int selectedID = radioGroup.getCheckedRadioButtonId();
                                if (selectedID == ansOptionSize) {
                                    if (TextUtils.isEmpty(binding.etTextMsg.getText().toString())) {
                                        Utils.showSnackBar(UpdateQuoteActivity.this, getString(R.string.err_message));
                                    } else {
                                        updatedOther = binding.etTextMsg.getText().toString();
                                        updatedAns.add(selectedID);
                                        updateAnswer(questionID, updatedAns, updatedOther);
                                    }

                                } else {
                                    int updatedAnswerID = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(selectedID - 1).getAnswerId();
                                    updatedAns.add(updatedAnswerID);
                                    updateAnswer(questionID, updatedAns, "");
                                }
                            }

                            /*
                            ANSWER TYPE-5 Radio button with textbox
                            */
                            else if (getAnswerType.equals(answersType[5])) {
                                RadioGroup radioGroup = binding.lnrRadio.findViewWithTag("radioGroup" + ques);
                                AppLog.d("SelectedRadioButton", String.valueOf(radioGroup.getCheckedRadioButtonId()));
                                int selectedID = radioGroup.getCheckedRadioButtonId();
                                if (selectedID == ansOptionSize) {
                                    if (TextUtils.isEmpty(binding.etTextMsg.getText().toString())) {
                                        Utils.showSnackBar(UpdateQuoteActivity.this, getString(R.string.err_message));
                                    } else {
                                        updatedOther = binding.etTextMsg.getText().toString();
                                        updatedAns.add(selectedID);
                                        updateAnswer(questionID, updatedAns, updatedOther);
                                    }
                                } else {
                                    int updatedAnswerID = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(selectedID - 1).getAnswerId();
                                    updatedAns.add(updatedAnswerID);
                                    updateAnswer(questionID, updatedAns, "");
                                }
                            }
                            /*
                            ANSWER TYPE - 6 Simple CheckBox
                            */
                            else if (getAnswerType.equals(answersType[6])) {
                                for (int checkbox = 0; checkbox < ansOptionSize; checkbox++) {
                                    CheckBox checkBox = findViewById(checkbox);
                                    if (checkBox.isChecked()) {
                                        int selectedID = checkBox.getId();
                                        int updatedAnswerID = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(selectedID).getAnswerId();
                                        updatedAns.add(updatedAnswerID);
                                        //Setting Values of QuestionId , others and answerList when internet is connected.
                                        AppLog.d("questionID,answerID", String.valueOf(questionID) + "," + updatedAnswerID);

                                    } else {
                                        Utils.showSnackBar(UpdateQuoteActivity.this, getString(R.string.err_this_is_required_question));
                                    }
                                }
                                updateAnswer(questionID, updatedAns, "");
                            }

                            /*
                            ANSWER TYPE-7 Image with Checkbox with textbox
                            */
                            else if (getAnswerType.equals(answersType[7])) {
                                for (int checkbox = 0; checkbox < ansOptionSize; checkbox++) {
                                    CheckBox checkBox = findViewById(checkbox);
                                    if (checkBox.isChecked()) {
                                        int selectedID = checkBox.getId();
                                        int updatedAnswerID = questionAnswerModel.getData().get(fetchQuesID).getQuestions().
                                                getQuestions().get(ques).getOptions().get(selectedID).getAnswerId();
                                        updatedAns.add(updatedAnswerID);
                                        //Setting Values of QuestionId , others and answerList when internet is connected.
                                        AppLog.d("questionID,answerID", String.valueOf(questionID) + "," + updatedAnswerID);
                                        if (selectedID == ansOptionSize - 1) {
                                            if (TextUtils.isEmpty(binding.etTextMsg.getText())) {
                                                Utils.showSnackBar(UpdateQuoteActivity.this, getString(R.string.err_this_is_required_question));
                                            } else {
                                                updateAnswer(questionID, updatedAns, "");
                                            }
                                        }
                                    }
                                }

                            }

                            /*
                            ANSWER TYPE-8 Simple CheckBox With TextArea
                            */
                            else if (getAnswerType.equals(answersType[8])) {
                                for (int checkbox = 0; checkbox < ansOptionSize; checkbox++) {
                                    CheckBox checkBox = findViewById(checkbox);
                                    if (checkBox.isChecked()) {
                                        int selectedID = checkBox.getId();
                                        int updatedAnswerID = questionAnswerModel.getData().get(fetchQuesID).getQuestions().getQuestions().get(ques).getOptions().get(selectedID).getAnswerId();
                                        updatedAns.add(updatedAnswerID);
                                        if (selectedID == ansOptionSize - 1) {
                                            if (!TextUtils.isEmpty(binding.etTextMsg.getText().toString())) {
                                                updatedOther = binding.etTextMsg.getText().toString();
                                            }
                                        }
                                        //Setting Values of QuestionId , others and answerList when internet is connected.
                                        AppLog.d("questionID,answerID", String.valueOf(questionID) + "," + updatedAnswerID);

                                    } else {
                                        Utils.showSnackBar(UpdateQuoteActivity.this, getString(R.string.err_this_is_required_question));
                                    }
                                }
                                updateAnswer(questionID, updatedAns, updatedOther);
                            }
                            /*
                            ANSWER TYPE-9 Simple TextBox
                            */
                            else if (getAnswerType.equals(answersType[9])) {
                                if (TextUtils.isEmpty(binding.etTextMsg.getText().toString())) {
                                    Utils.showSnackBar(UpdateQuoteActivity.this, getString(R.string.err_message));
                                } else {
                                    Utils.dismissDialog();
                                    updateAnswer(questionID, updatedAns, binding.etTextMsg.getText().toString());
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void updateAnswer(int questionID, List<Integer> updatedAns, String others) {
        //Check if Quote Id Is zero or not
        if (quoteID != 0) {
            for (int list = 0; list < getQuesAnsSubmittedList.getData().size(); list++) {
                int fetchQuoteID = getQuesAnsSubmittedList.getData().get(list).getId();
                if (quoteID == fetchQuoteID) {
                    for (int ques = 0; ques < getQuesAnsSubmittedList.getData().get(list).getAnswers().size(); ques++) {
                        if (questionID == getQuesAnsSubmittedList.getData().get(list).getAnswers().get(ques).getQuestion_id()) {
                            getQuesAnsSubmittedList.getData().get(list).getAnswers().get(ques).setAnswerId(updatedAns);
                            getQuesAnsSubmittedList.getData().get(list).getAnswers().get(ques).setOther(others);
                            callUpdateWS(list);
                        }
                    }
                }
            }
        } else {
            for (int ques = 0; ques < fetchLocalSetAns.getAnswers().size(); ques++) {
                if (questionID == fetchLocalSetAns.getAnswers().get(ques).getQuestion_id()) {
                    fetchLocalSetAns.getAnswers().get(ques).setAnswerId(updatedAns);
                    fetchLocalSetAns.getAnswers().get(ques).setOther(others);
                    updatePendingAnswers();
                }
            }
        }
    }

    private void updatePendingAnswers() {
        Utils.showProgressDialog(this);
        List<SetAnsChildEntity> entity = fetchLocalSetAns.getAnswers(); //At a particular id
        //Query for Update
        DBSetAnsMethodSync.updateAll(AppDatabase.getAppDatabase(this), fetchLocalSetAns);
        Utils.showSnackBar(this, getString(R.string.ques_ans_submitted_success));
        Utils.dismissDialog();
        Intent intent = new Intent();
        intent.putExtra(Constants.QUIZ_UPDATE_ANS, (Serializable) entity);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    private void callUpdateWS(final int list) {
        Utils.showProgressDialog(this);
        String answerJSON = new Gson().toJson(getQuesAnsSubmittedList.getData().get(list).getAnswers());
        Call<SetAnswers> call = RestClient.getInstance(true).getApiInterface().UpdateAns(Utils.getRequestMap(true),quoteID, answerJSON);
        RestClient.makeApiRequest(this, call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                Utils.dismissDialog();
                Utils.showSnackBar(UpdateQuoteActivity.this, getString(R.string.successfully_updated));
                Intent intent = new Intent();
                intent.putExtra(Constants.QUIZ_UPDATE_ANS, (Serializable) getQuesAnsSubmittedList.getData().get(list).getAnswers());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

            @Override
            public void onApiError(Call<Object> call, Throwable throwable) {
                Utils.dismissDialog();
                AppLog.d(TAG, "onApiError");
            }
        });
    }


    private String getImage(String image) {
        String[] convertedImage = image.split("\\.");  // Eliminate .jpg or .png from name
        return convertedImage[0];
    }
}
