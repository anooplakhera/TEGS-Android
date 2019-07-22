package com.tegs.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.tegs.DatabaseHandler.AppDatabase;
import com.tegs.DatabaseHandler.DBSetAnsMethodSync;
import com.tegs.DatabaseHandler.SetAnsChildEntity;
import com.tegs.DatabaseHandler.SetAnswerEntity;
import com.tegs.QuestionAnswerModel.QuestionAnswerModel;
import com.tegs.QuestionAnswerModel.QuestionAnswerModel.Datum;
import com.tegs.R;
import com.tegs.activities.QuestionAnswerListActivity;
import com.tegs.databinding.DialogThanksForSubmissionsBinding;
import com.tegs.model.SetAnswers;
import com.tegs.model.SetAnswersChild;
import com.tegs.retrofit.ApiResponseListener;
import com.tegs.retrofit.RequestParameters;
import com.tegs.retrofit.RestClient;
import com.tegs.utils.AppLog;
import com.tegs.utils.Constants;
import com.tegs.utils.Utils;

import retrofit2.Call;

/**
 * Created
 * by heena on 2/2/18.
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
A7 = " Image with CheckBox with TextBox
A8 = " CheckBox with textarea
A9 = " TextArea
*/

public class QuestionAnswerFragment extends Fragment {
    private String TAG = "QuestionAnswer";
    private Datum questionObject; // Array of Questions and Options
    private int optionSize, questionID;
    private String answerType; //Gives type of answer
    private String[] questionsType = {"Q0", "Q1", "Q2", "Q3"};
    private String[] answersType = {"A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10"};
    private View itemView;
    private boolean isTrueA10 = false;
    private int selectedIdA10 = 0;
    private AppCompatEditText etTextMsg, etRadioMsg; //EditText for other message
    private SetAnswers setAnswers; //Model to store date,title and answer's List
    private SetAnswerEntity setAnswerEntity; //Entity is used to store into local database
    private QuestionAnswerModel questionAnswerModel = Utils.getQuestionAnswerData();
    AppCompatTextView txtQuestion;

    public static QuestionAnswerFragment newInstance(int position, Datum questionAnswerModel) {
        QuestionAnswerFragment questionAnswerFragment = new QuestionAnswerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putSerializable("questionAnswerModel", questionAnswerModel);
        questionAnswerFragment.setArguments(bundle);
        return questionAnswerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int page = getArguments().getInt("position");
        questionObject = (Datum) getArguments().getSerializable("questionAnswerModel"); // Getting Question's Answer's Stored Object
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        itemView = inflater.inflate(R.layout.raw_question_answer, container, false);
        setAnswers = ((QuestionAnswerListActivity) getActivity()).setAnswers;
        setAnswerEntity = ((QuestionAnswerListActivity) getActivity()).setAnswerEntity;
        setAnswerEntity.setStatus(getString(R.string.btn_pending));

        String quesInstruction = questionObject.getQuestions().getInstruction(); //for ques instruction if it has
        String quesInstructionImage = questionObject.getQuestions().getImage(); //for instruction img if it has

        final AppCompatTextView txtInstruction;
        final SimpleDraweeView sdvInstructionImage;
        final AppCompatButton btnNextQues;
        final LinearLayout lnrAddQues;
        final CardView cardViewInstImage;

        Utils.hideKeyboard(getActivity());
        /*
        Main Ques n Answer Layout
        */
        txtInstruction = itemView.findViewById(R.id.txt_ques_instruction);
        sdvInstructionImage = itemView.findViewById(R.id.sdv_instruction_image);
        cardViewInstImage = itemView.findViewById(R.id.cardview_instruction_image);
        btnNextQues = itemView.findViewById(R.id.btn_next_ques);
        lnrAddQues = itemView.findViewById(R.id.lnr_add_ques);
        /*
        Settings the Instruction and Instruction Image.
        */
        cardViewInstImage.setVisibility(View.GONE);
        sdvInstructionImage.setVisibility(View.GONE);
        txtInstruction.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(quesInstruction)) {
            txtInstruction.setVisibility(View.VISIBLE);
            txtInstruction.setText(quesInstruction);
        }

        if (!TextUtils.isEmpty(quesInstructionImage)) {
            cardViewInstImage.setVisibility(View.VISIBLE);
            sdvInstructionImage.setVisibility(View.VISIBLE);
            int resID = itemView.getResources().getIdentifier(getImage(quesInstructionImage), "drawable", (getActivity()).getPackageName());
            sdvInstructionImage.setImageResource(resID);
        }

        /*
        Inflatating the questions and answers into main Linear Layout
        */
        for (int ques = 0; ques < questionObject.getQuestions().getQuestions().size(); ques++) {

            String questionType = questionObject.getQuestions().getQuestions().get(ques).getQuestionType();
            String questionText = questionObject.getQuestions().getQuestions().get(ques).getQuestion().getText();
            String questionImageName = questionObject.getQuestions().getQuestions().get(ques).getQuestion().getLink();
            String youtubeLink = questionObject.getQuestions().getQuestions().get(ques).getQuestion().getLink();

            optionSize = questionObject.getQuestions().getQuestions().get(ques).getOptions().size();//Get Options Size
            answerType = questionObject.getQuestions().getQuestions().get(ques).getAnswerType();//Get Answer Type

            View quesView = inflater.inflate(R.layout.view_questions, lnrAddQues, false);

            txtQuestion = quesView.findViewById(R.id.txt_question);
            AppCompatTextView txtYouTubeLink = quesView.findViewById(R.id.txt_youtube_link);
            SimpleDraweeView sdvQuestionImage = quesView.findViewById(R.id.sdv_questions_image);
            CardView cardViewQuesImg = quesView.findViewById(R.id.cardview_ques_img);

            /*
            QUESTION TYPE-1
            */
            if (questionObject.getQuestions().getQuestions().get(ques).getIsRequired().equals("true")) {
                txtQuestion.setVisibility(View.VISIBLE);
//                txtQuestion.setBackgroundColor(getResources().getColor(R.color.colorDialogBtnGray));
                txtQuestion.setText(questionText + "*"); //Set Question Text
            } else {
                txtQuestion.setVisibility(View.VISIBLE);
                txtQuestion.setText(questionText); //Set Question Text
            }

            /*
            QUESTION TYPE-2
            */
            if (questionType.equals(questionsType[2])) {
                txtYouTubeLink.setVisibility(View.VISIBLE);
                txtYouTubeLink.setText(youtubeLink);
            }

            /*
            QUESTION TYPE-3
            */
            else if (questionType.equals(questionsType[3])) { //Question Text with Image
                cardViewQuesImg.setVisibility(View.VISIBLE);
                sdvQuestionImage.setVisibility(View.VISIBLE);
                int resID = itemView.getResources().getIdentifier(getImage(questionImageName), "drawable", (getActivity()).getPackageName());
                sdvQuestionImage.setImageResource(resID);
            }

            /*
            Answers Options and Type's
            */
            lnrAddQues.addView(quesView); //Inflating Question's View to the main LinearLayout

            View ansView = inflater.inflate(R.layout.view_answer, lnrAddQues, false);

            GridLayoutManager mManager;

            etTextMsg = ansView.findViewById(R.id.et_text_msg_ques);
            etTextMsg.setImeOptions(EditorInfo.IME_ACTION_DONE);
            etTextMsg.setRawInputType(InputType.TYPE_CLASS_TEXT);

            LinearLayout lnrCheckBox = ansView.findViewById(R.id.lnr_checkbox);
            LinearLayout lnrCheckBoxImage = ansView.findViewById(R.id.lnr_checkboxImage);
            RecyclerView recycleview = ansView.findViewById(R.id.recycleview);
            LinearLayout lnrRadioImage = ansView.findViewById(R.id.lnr_radio_image);
            LinearLayout lnrRadio = ansView.findViewById(R.id.lnr_radio);
            etRadioMsg = ansView.findViewById(R.id.et_text_radio_msg);



            /*
              ANSWER TYPE-1 Simple RadioButton
            */
            if (answerType.equals(answersType[1])) {

                lnrRadioImage.setVisibility(View.GONE);
                lnrRadio.setVisibility(View.VISIBLE);
                recycleview.setVisibility(View.GONE);
                RadioGroup radioGroup = new RadioGroup(getActivity());
                radioGroup.setTag("radioGroup" + ques);
                lnrRadio.addView(radioGroup);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                for (int radioBtn = 0; radioBtn < optionSize; radioBtn++) {
                    AppCompatRadioButton radioButton = (AppCompatRadioButton) inflater.inflate(R.layout.view_radio, radioGroup, false);
                    radioButton.setText(questionObject.getQuestions().getQuestions().get(ques).getOptions().get(radioBtn).getText());
//                    radioButton.setTextColor(getResources().getColor(R.color.colorDialogBtnGray));
                    radioButton.setId(radioBtn + 1);
                    radioButton.setLayoutParams(param);
                    radioGroup.addView(radioButton);

                }

                lnrAddQues.addView(ansView);
            }


            /*
              ANSWER TYPE-2 Image with RadioButton
            */
            else if (answerType.equals(answersType[2])) {
                String radioImage;
                lnrRadio.setVisibility(View.VISIBLE);
                recycleview.setVisibility(View.GONE);
                lnrRadioImage.setVisibility(View.VISIBLE);
                RadioGroup radioGroup = new RadioGroup(getActivity());
                radioGroup.setTag(Constants.RADIO_GROUP_WITH_IMAGE + ques);
                lnrRadio.addView(radioGroup);
                for (int radioBtn = 0; radioBtn < optionSize; radioBtn++) {
                    //Fetching the name of image
                    radioImage = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(radioBtn).getImage();
                    AppCompatRadioButton radioButton = (AppCompatRadioButton) inflater.inflate(R.layout.view_radio, radioGroup, false);
                    View viewCardView = inflater.inflate(R.layout.view_card_view_image, lnrRadioImage, false);
                    final SimpleDraweeView sdvRadioImage = viewCardView.findViewById(R.id.sdv_image);
                    int resID = itemView.getResources().getIdentifier(getImage(radioImage), "drawable", ((QuestionAnswerListActivity) getActivity()).getPackageName());
                    sdvRadioImage.setImageResource(resID);
                    if (radioImage.equals("")) {
                        viewCardView.setVisibility(View.INVISIBLE);
                    } else {
                        viewCardView.setVisibility(View.VISIBLE);
                    }
                    radioButton.setText(questionObject.getQuestions().getQuestions().get(ques).getOptions().get(radioBtn).getText());
                    radioButton.setId(radioBtn + 1);
                    radioGroup.addView(radioButton);
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
                    lnrRadioImage.addView(viewCardView);
                }
                lnrAddQues.addView(ansView);
            }

            /*
            ANSWER TYPE-3 CheckBox with Images
            */
            else if (answerType.equals(answersType[3])) {
                String checkBoxImage;

                lnrCheckBox.setVisibility(View.VISIBLE);
                lnrCheckBoxImage.setVisibility(View.VISIBLE);
                recycleview.setVisibility(View.GONE);

                for (int option = 0; option < optionSize; option++) {

                    LinearLayout.LayoutParams checkBoxImageParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

//                    View viewCardView = inflater.inflate(R.layout.view_card_view_image, lnrRadioImage, false);
//                    final SimpleDraweeView sdvCheckBoxImage = viewCardView.findViewById(R.id.sdv_image);
                    final SimpleDraweeView sdvCheckBoxImage = new SimpleDraweeView(getContext());
                    final CardView cardView = new CardView(getActivity());
                    cardView.setRadius(10f);
                    sdvCheckBoxImage.setLayoutParams(checkBoxImageParam);
                    cardView.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                    AppCompatCheckBox checkBoxView = (AppCompatCheckBox) inflater.inflate(R.layout.view_checkbox, lnrCheckBox, false);
                    checkBoxView.setText(questionObject.getQuestions().getQuestions().get(ques).getOptions().get(option).getText());
                    checkBoxImage = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(option).getImage();
                    int resID = itemView.getResources().getIdentifier(getImage(checkBoxImage), "drawable", ((QuestionAnswerListActivity) getActivity()).getPackageName());
                    sdvCheckBoxImage.setScaleType(ImageView.ScaleType.FIT_XY);
                    sdvCheckBoxImage.setImageResource(resID);
                    if (checkBoxImage.equals("")) {
                        sdvCheckBoxImage.setVisibility(View.INVISIBLE);
                    } else {
                        sdvCheckBoxImage.setVisibility(View.VISIBLE);
                    }
                    checkBoxImageParam.setMargins(0, 7, 0, 7);
                    checkBoxView.setTag(option);
                    checkBoxView.setId(option);
                    cardView.setLayoutParams(checkBoxImageParam);
                    cardView.addView(sdvCheckBoxImage);
                    checkBoxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                sdvCheckBoxImage.setPadding(4, 4, 4, 4);
                                cardView.setCardBackgroundColor(getResources().getColor(R.color.colorOrange));
                            } else {
                                sdvCheckBoxImage.setPadding(0, 0, 0, 0);
                                cardView.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                            }
                        }
                    });
                    lnrCheckBoxImage.addView(cardView);
                    lnrCheckBox.addView(checkBoxView);
                }
                lnrAddQues.addView(ansView);
            }

            /*
               ANSWER TYPE-4 Image with radio button with textbox
            */
            else if (answerType.equals(answersType[4])) {
                String radioImage;
                lnrRadio.setVisibility(View.VISIBLE);
                lnrRadioImage.setVisibility(View.VISIBLE);
                recycleview.setVisibility(View.GONE);
                RadioGroup radioGroup = new RadioGroup(getActivity());
                radioGroup.setTag(Constants.RADIO_GROUP_WITH_IMAGE + ques);
                lnrRadio.addView(radioGroup);
                for (int radioBtn = 0; radioBtn < optionSize; radioBtn++) {
                    View viewCardView = inflater.inflate(R.layout.view_card_view_image, lnrRadioImage, false);
                    final SimpleDraweeView sdvRadioImage = viewCardView.findViewById(R.id.sdv_image);
                    radioImage = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(radioBtn).getImage();
                    int resID = itemView.getResources().getIdentifier(getImage(radioImage), "drawable", (getActivity()).getPackageName());
                    sdvRadioImage.setImageResource(resID);
                    if (radioImage.equals("")) {
                        viewCardView.setVisibility(View.INVISIBLE);
                    } else {
                        viewCardView.setVisibility(View.VISIBLE);
                    }
                    AppCompatRadioButton radioButton = (AppCompatRadioButton) inflater.inflate(R.layout.view_radio, radioGroup, false);
                    radioButton.setText(questionObject.getQuestions().getQuestions().get(ques).getOptions().get(radioBtn).getText());
                    radioButton.setId(radioBtn + 1);
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
                                etTextMsg.setVisibility(View.VISIBLE);
                            } else {
                                etTextMsg.setVisibility(View.GONE);
                            }
                        }
                    });
                    lnrRadioImage.addView(viewCardView);
                }
                lnrAddQues.addView(ansView);
            }

            /*
               ANSWER TYPE-5 Radio button with textbox
            */
           /* else if (answerType.equals(answersType[5])) {
                final int optionsSize = optionSize;
                etRadioMsg = ansView.findViewById(R.id.et_text_radio_msg);

                lnrRadio.setVisibility(View.VISIBLE);
                RadioGroup radioGroup = new RadioGroup(getActivity());
                radioGroup.setTag("radioGroup" + ques);
                lnrRadio.addView(radioGroup);
                for (int radioBtn = 0; radioBtn < optionsSize; radioBtn++) {
                    AppCompatRadioButton radioButton = (AppCompatRadioButton) inflater.inflate(R.layout.view_radio, radioGroup, false);
                    radioButton.setText(questionObject.getQuestions().getQuestions().get(ques).getOptions().get(radioBtn).getText());
                    radioButton.setId(radioBtn + 1);
                    LinearLayout.LayoutParams radioButtonParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); //change
                    radioButton.setLayoutParams(radioButtonParam);
                    radioGroup.addView(radioButton);
                }
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == optionsSize) {
                            etRadioMsg.setVisibility(View.VISIBLE);
                        } else {
                            etRadioMsg.setVisibility(View.GONE);
                        }
                    }
                });

                lnrAddQues.addView(ansView);
            } */

            else if (answerType.equals(answersType[5])) {
                final int optionsSize = optionSize;
                etRadioMsg = ansView.findViewById(R.id.et_text_radio_msg);


                recycleview.setVisibility(View.GONE);
                lnrRadio.setVisibility(View.VISIBLE);
                RadioGroup radioGroup = new RadioGroup(getActivity());
                radioGroup.setTag("radioGroup" + ques);
                lnrRadio.addView(radioGroup);
                for (int radioBtn = 0; radioBtn < optionsSize; radioBtn++) {
                    AppCompatRadioButton radioButton = (AppCompatRadioButton) inflater.inflate(R.layout.view_radio, radioGroup, false);
                    radioButton.setText(questionObject.getQuestions().getQuestions().get(ques).getOptions().get(radioBtn).getText());
                    radioButton.setId(radioBtn + 1);
                    LinearLayout.LayoutParams radioButtonParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); //change
                    radioButton.setLayoutParams(radioButtonParam);
                    radioGroup.addView(radioButton);

                }
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == optionsSize) {
                            etRadioMsg.setVisibility(View.VISIBLE);
                        } else {
                            etRadioMsg.setVisibility(View.GONE);
                        }
                    }
                });

//                gl.addView(ansView);
                lnrAddQues.addView(ansView);
            }

            /*
            ANSWER TYPE - 6 Simple CheckBox
            */
            else if (answerType.equals(answersType[6])) {
                lnrCheckBox.setVisibility(View.VISIBLE);
                recycleview.setVisibility(View.GONE);
                for (int row = 0; row < optionSize; row++) {
                    AppCompatCheckBox checkBoxView = (AppCompatCheckBox) inflater.inflate(R.layout.view_checkbox, lnrCheckBox, false);
                    checkBoxView.setText(questionObject.getQuestions().getQuestions().get(ques).getOptions().get(row).getText());
                    CheckBox checkBox = new CheckBox((getActivity()));
                    checkBoxView.setTag("checkbox" + row);
                    checkBoxView.setId(row);
                    checkBox.setButtonDrawable(R.drawable.checkbox_background);
                    lnrCheckBox.addView(checkBoxView);
                }
                lnrAddQues.addView(ansView);
            }

             /*
            ANSWER TYPE-7 CheckBox with Image with textbox
            */
            else if (answerType.equals(answersType[7])) {
                String checkBoxImage;
                lnrCheckBox.setVisibility(View.VISIBLE);
                lnrCheckBoxImage.setVisibility(View.VISIBLE);
                recycleview.setVisibility(View.GONE);
                for (int option = 0; option < optionSize; option++) {

                    LinearLayout.LayoutParams checkBoxImageParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT); //change
                    final SimpleDraweeView sdvCheckBoxImage = new SimpleDraweeView(getContext());
                    final CardView cardView = new CardView(getActivity());
                    cardView.setRadius(15f);
                    sdvCheckBoxImage.setLayoutParams(checkBoxImageParam);

                    cardView.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));

                    AppCompatCheckBox checkBoxView = (AppCompatCheckBox) inflater.inflate(R.layout.view_checkbox, lnrCheckBox, false);
                    checkBoxView.setText(questionObject.getQuestions().getQuestions().get(ques).getOptions().get(option).getText());

                    checkBoxImage = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(option).getImage();
                    int resID = itemView.getResources().getIdentifier(getImage(checkBoxImage), "drawable", ((QuestionAnswerListActivity) getActivity()).getPackageName());
                    sdvCheckBoxImage.setScaleType(ImageView.ScaleType.FIT_XY);
                    sdvCheckBoxImage.setImageResource(resID);

                    if (checkBoxImage.equals("")) {
                        int resImg = itemView.getResources().getIdentifier(getImage("transparent_image.png"), "drawable", ((QuestionAnswerListActivity) getActivity()).getPackageName());
                        sdvCheckBoxImage.setScaleType(ImageView.ScaleType.FIT_XY);
                        sdvCheckBoxImage.setImageResource(resImg);
                        cardView.setVisibility(View.INVISIBLE);
                    } else {
                        cardView.setVisibility(View.VISIBLE);
                        sdvCheckBoxImage.setVisibility(View.VISIBLE);
                    }

                    checkBoxImageParam.setMargins(0, 5, 0, 5);
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
                                    etTextMsg.setVisibility(View.VISIBLE);
                                } else {
                                    etTextMsg.setVisibility(View.GONE);
                                }
                            }
                            if (isChecked) {
                                cardView.setCardBackgroundColor(getResources().getColor(R.color.colorOrange));
                                sdvCheckBoxImage.setPadding(4, 4, 4, 4);

                            } else {
                                cardView.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
                                sdvCheckBoxImage.setPadding(0, 0, 0, 0);
                            }
                        }
                    });

                    lnrCheckBoxImage.addView(cardView);
                    lnrCheckBox.addView(checkBoxView);
                }
                lnrAddQues.addView(ansView);
            }

            /*
            ANSWER TYPE - 8 Simple CheckBox with textarea
            */
            else if (answerType.equals(answersType[8])) {
                lnrCheckBox.setVisibility(View.VISIBLE);
                recycleview.setVisibility(View.GONE);
                AppLog.d("TextMessage", etTextMsg.getText().toString());
                for (int row = 0; row < optionSize; row++) {
                    AppCompatCheckBox checkBoxView = (AppCompatCheckBox) inflater.inflate(R.layout.view_checkbox, lnrCheckBox, false);
                    checkBoxView.setText(questionObject.getQuestions().getQuestions().get(ques).getOptions().get(row).getText());
                    CheckBox checkBox = new CheckBox((getActivity()));
                    checkBoxView.setTag("checkbox" + row);
                    checkBoxView.setId(row);
                    checkBox.setButtonDrawable(R.drawable.checkbox_background);
                    if (row == optionSize - 1) {
                        checkBoxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    etTextMsg.setVisibility(View.VISIBLE);
                                } else {
                                    etTextMsg.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                    lnrCheckBox.addView(checkBoxView);
                }

                lnrAddQues.addView(ansView);
            }

            /*
            ANSWER TYPE-9 Simple TextBox.
            */
            else if (answerType.equals(answersType[9])) {
                etTextMsg.setVisibility(View.VISIBLE);
                recycleview.setVisibility(View.GONE);
                lnrAddQues.addView(ansView);
                int maxLength = 2;
                InputFilter[] fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(maxLength);
                if (questionObject.getQuestions().getQuestions().get(ques).getOptions().get(0).getText().equalsIgnoreCase("number-2")) {
                    etTextMsg.setInputType(InputType.TYPE_CLASS_NUMBER);
                    etTextMsg.setFilters(fArray);
                } else {
                    etTextMsg.setInputType(InputType.TYPE_CLASS_TEXT);
//                etTextMsg.setFilters(fArray);
                }
            }

            /*
              ANSWER TYPE-10 Simple RadioButton
            */

            else if (answerType.equals(answersType[10])) {
                lnrRadioImage.setVisibility(View.GONE);
                lnrRadio.setVisibility(View.GONE);
                recycleview.setVisibility(View.VISIBLE);


                RadioGroup radioGroup = new RadioGroup(getActivity());
                radioGroup.setTag("radioGroup" + ques);
                lnrRadio.addView(radioGroup);

                mManager = new GridLayoutManager(getActivity(), 4);
                recycleview.setLayoutManager(mManager);
                GridViewAdapter adapter = new GridViewAdapter(getActivity(), questionObject, ques, radioGroup, optionSize);
                recycleview.setAdapter(adapter);
                adapter.setOnClickItem(new onClickView() {
                    @Override
                    public void onClickView(int quesID, boolean isTrue) {
                        isTrueA10 = isTrue;
                        selectedIdA10 = quesID;
//                        Toast.makeText(getActivity(), "" + quesID, Toast.LENGTH_SHORT).show();
                    }


                });
                lnrAddQues.addView(ansView);
            }

        }

        btnNextQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String next_id = ""; //Gives next_id to jump to next question.
                int nextID;
                boolean flag = false; //Flag is used to show that the answer is required to attempt or not.
                String requiredQuestion = "true";
                int answerID;

                for (int ques = 0; ques < questionObject.getQuestions().getQuestions().size(); ques++) {
                    SetAnswersChild setAnswersChild = new SetAnswersChild(); // AnswersChild to set Child Answers List
                    SetAnsChildEntity setchildEntity = new SetAnsChildEntity(); //AnswersChild to set Answers List Locally
                    answerType = questionObject.getQuestions().getQuestions().get(ques).getAnswerType();//Get Answer Type
                    String isRequiredQuestion = questionObject.getQuestions().getQuestions().get(ques).getIsRequired();//Is Question is required or not
                    /*
                    ANSWER TYPE-1 Simple RadioButton
                    */
                    if (answerType.equals(answersType[1]) /*|| answerType.equals(answersType[10])*/) {
                        int selectedID = 1;
                        RadioGroup radioGroup = itemView.findViewWithTag("radioGroup" + ques);
                        questionID = questionObject.getQuestions().getQuestions().get(ques).getQuestion().getQuestionId();
                        flag = false;
                        if (radioGroup.getCheckedRadioButtonId() != -1) {
                            AppLog.d("SelectedRadioButton", String.valueOf(radioGroup.getCheckedRadioButtonId()));
                            selectedID = radioGroup.getCheckedRadioButtonId();
                            answerID = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID - 1).getAnswerId();

                            //Setting Values of QuestionId , others and answerList when internet is connected.
                            setAnswersChild.setQuestionId(questionID);
                            setAnswersChild.setOther("");
                            setAnswersChild.getAnswerId().add(answerID);
                            //Setting Values of QuestionID and Other for Locally
                            setchildEntity.setQuestion_id(questionID);
                            setchildEntity.setOther("");
                            setchildEntity.getAnswerId().add(answerID);
                            next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID - 1).getNextId();
                            if (!next_id.equals("")) {
                                nextID = Integer.parseInt(next_id);
                                AppLog.d("questionID,answerID,nextID", String.valueOf(questionID) + "," + answerID + "," + next_id);
                            }
                            flag = true;
                        } else {
                            if (isRequiredQuestion.equals(requiredQuestion)) {
                                flag = false;
                                txtQuestion.setBackgroundResource(R.drawable.border_shape_red);
                                Utils.showSnackBar(getActivity(), getString(R.string.err_this_is_required_question));
                                break;
                            } else {
                                //Setting Values of QuestionId , others and answerList when internet is connected.
                                setAnswersChild.setQuestionId(questionID);
                                setAnswersChild.setOther("");
                                //Setting Values of QuestionID and Other for Locally
                                setchildEntity.setQuestion_id(questionID);
                                setchildEntity.setOther("");
                                next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID - 1).getNextId();
                                if (!next_id.equals("")) {
                                    nextID = Integer.parseInt(next_id);
                                    if (nextID == -1) {
                                        showThankYouDialog();
                                    } else if (ques == questionObject.getQuestions().getQuestions().size() - 1) {
                                        QuestionAnswerListActivity.quesId.push(questionID + "");
                                        ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
                                    }
                                }
                            }
                        }
                        if (flag) {
                            if (!next_id.equals("")) {
                                nextID = Integer.parseInt(next_id);
                                if (nextID == -1) {
                                    showThankYouDialog();
                                } else {
                                    QuestionAnswerListActivity.quesId.push(questionID + "");
                                    ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
                                }
                            }
                        }
                        setAnswersChild.setQuestionId(questionID);
                        setAnswersChild.setOther("");
                        setchildEntity.setQuestion_id(questionID);
                        setchildEntity.setOther("");
                    }

                    /*
                    ANSWER TYPE-2 RadioButton with Image
                    */

                    else if (answerType.equals(answersType[10])) {
                        if (isTrueA10) {
                            questionID = questionObject.getQuestions().getQuestions().get(ques).getQuestion().getQuestionId();
                            answerID = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedIdA10 - 1).getAnswerId();

                            //Setting Values of QuestionId , others and answerList when internet is connected.
                            setAnswersChild.setQuestionId(questionID);
                            setAnswersChild.setOther("");
                            setAnswersChild.getAnswerId().add(answerID);
                            //Setting Values of QuestionID and Other for Locally
                            setchildEntity.setQuestion_id(questionID);
                            setchildEntity.setOther("");
                            setchildEntity.getAnswerId().add(answerID);
                            next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedIdA10 - 1).getNextId();
                            if (!next_id.equals("")) {
                                nextID = Integer.parseInt(next_id);
                                AppLog.d("questionID,answerID,nextID", String.valueOf(questionID) + "," + answerID + "," + next_id);
                            }

                            next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedIdA10 - 1).getNextId();
                            if (!next_id.equals("")) {
                                nextID = Integer.parseInt(next_id);
                                if (nextID == -1) {
                                    showThankYouDialog();
                                } else if (ques == questionObject.getQuestions().getQuestions().size() - 1) {
                                    QuestionAnswerListActivity.quesId.push(questionID + "");
                                    ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
                                }
                            }

                        } else {
                            txtQuestion.setBackgroundResource(R.drawable.border_shape_red);
                            Utils.showSnackBar(getActivity(), getString(R.string.err_this_is_required_question));
                            break;
                        }
                    }


                    /*
                    ANSWER TYPE-2 RadioButton with Image
                    */
                    else if (answerType.equals(answersType[2])) {
                        int selectedID = 1;
                        RadioGroup radioGroup = itemView.findViewWithTag(Constants.RADIO_GROUP_WITH_IMAGE + ques);
                        flag = false;
                        questionID = questionObject.getQuestions().getQuestions().get(ques).getQuestion().getQuestionId();
                        if (radioGroup.getCheckedRadioButtonId() != -1) {
                            AppLog.d("SelectedRadioButton", String.valueOf(radioGroup.getCheckedRadioButtonId()));
                            selectedID = radioGroup.getCheckedRadioButtonId();
                            answerID = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID - 1).getAnswerId();

                            //Setting Values of QuestionId , others and answerList when internet is connected.
                            setAnswersChild.setQuestionId(questionID);
                            setAnswersChild.setOther("");
                            setAnswersChild.getAnswerId().add(answerID);
                            //Setting Values of QuestionID and Other for Locally
                            setchildEntity.setQuestion_id(questionID);
                            setchildEntity.setOther("");
                            setchildEntity.getAnswerId().add(answerID);
                            next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID - 1).getNextId();
                            if (!next_id.equals("")) {
                                nextID = Integer.parseInt(next_id);
                                AppLog.d("questionID,answerID,nextID", String.valueOf(questionID) + "," + answerID + "," + next_id);
                            }
                            flag = true;
                        } else {
                            if (isRequiredQuestion.equals(requiredQuestion)) {
                                flag = false;
                                txtQuestion.setBackgroundResource(R.drawable.border_shape_red);
                                Utils.showSnackBar(getActivity(), getString(R.string.err_this_is_required_question));
                                break;
                            } else {
                                //Setting Values of QuestionId , others and answerList when internet is connected.
                                setAnswersChild.setQuestionId(questionID);
                                setAnswersChild.setOther("");

                                //Setting Values of QuestionID and Other for Locally
                                setchildEntity.setQuestion_id(questionID);
                                setchildEntity.setOther("");
                                next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID - 1).getNextId();
                                if (!next_id.equals("")) {
                                    nextID = Integer.parseInt(next_id);
                                    if (nextID == -1) {
                                        showThankYouDialog();
                                    } else {
                                        QuestionAnswerListActivity.quesId.push(questionID + "");
                                        ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
                                    }
                                }
                            }
                        }
                        if (flag) {
                            next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID - 1).getNextId();
                            if (!next_id.equals("")) {
                                nextID = Integer.parseInt(next_id);
                                if (nextID == -1) {
                                    showThankYouDialog();
                                } else {
                                    QuestionAnswerListActivity.quesId.push(questionID + "");
                                    ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
                                }
                            }
                        }
                        //Setting Values of QuestionId , others and answerList when internet is connected.
                        setAnswersChild.setQuestionId(questionID);
                        setAnswersChild.setOther("");
                        //Setting Values of QuestionID and Other for Locally
                        setchildEntity.setQuestion_id(questionID);
                        setchildEntity.setOther("");
                    }
                    /*
                    ANSWER TYPE-3 Image with CheckBox
                    */
                    else if (answerType.equals(answersType[3])) {
                        int selectedID = 0;
                        for (int i = 0; i < optionSize; i++) {
                            questionID = questionObject.getQuestions().getQuestions().get(ques).getQuestion().getQuestionId();
                            CheckBox checkBox = itemView.findViewById(i);
                            if (checkBox.isChecked()) {
                                selectedID = checkBox.getId();
                                answerID = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID).getAnswerId();
                                AppLog.d("Type-3 : AnswerID=", String.valueOf(answerID));
                                //Setting Values of QuestionId , others and answerList when internet is connected.
                                setAnswersChild.getAnswerId().add(answerID);
                                setAnswersChild.setQuestionId(questionID);
                                setAnswersChild.setOther("");

                                //Setting Values of QuestionID and Other for Locally
                                setchildEntity.setQuestion_id(questionID);
                                setchildEntity.setOther("");
                                setchildEntity.getAnswerId().add(answerID);
                                next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID).getNextId();
                                if ((next_id != null)) {
                                    if ((!next_id.equals(""))) {
                                        nextID = Integer.parseInt(next_id);
                                        AppLog.d("questionID,answerID,nextID", String.valueOf(questionID) + "," + answerID + "," + next_id);
                                    }
                                } else {
                                    nextID = questionID + 1;
                                }
                                flag = true;
                            } else {
                                if (isRequiredQuestion.equals(requiredQuestion)) {
                                    flag = false;
                                    txtQuestion.setBackgroundResource(R.drawable.border_shape_red);
                                    Utils.showSnackBar(getActivity(), getString(R.string.err_this_is_required_question));
                                    break;
                                } else {
                                    //Setting Values of QuestionId , others and answerList when internet is connected.
                                    setAnswersChild.setQuestionId(questionID);
                                    setAnswersChild.setOther("");

                                    //Setting Values of QuestionID and Other for Locally
                                    setchildEntity.setQuestion_id(questionID);
                                    setchildEntity.setOther("");
                                    next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID).getNextId();
                                    if (next_id != null) {
                                        if (!next_id.equals("")) {
                                            nextID = Integer.parseInt(next_id);
                                            if (nextID == -1) {
                                                showThankYouDialog();
                                            } else {
                                                QuestionAnswerListActivity.quesId.push(questionID + "");
                                                ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
                                                AppLog.d("questionID,nextID", String.valueOf(questionID) + "," + next_id);
                                            }
                                        } else {
                                            QuestionAnswerListActivity.quesId.push(String.valueOf(questionID + 1));
                                            ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(questionID + 1, false);
                                        }
                                    } else {
                                        QuestionAnswerListActivity.quesId.push(String.valueOf(questionID + 1));
                                        ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(questionID + 1, false);
                                    }

                                }
                            }
                        }
                        if (flag) {
                            setchildEntity.setQuestion_id(questionID);
                            setchildEntity.setOther("");
                            next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID).getNextId();
                            if (next_id != null) {
                                if (!next_id.equals("")) {
                                    nextID = Integer.parseInt(next_id);
                                    if (nextID == -1) {
                                        showThankYouDialog();
                                    } else {
                                        QuestionAnswerListActivity.quesId.push(questionID + "");
                                        ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
                                        AppLog.d("questionID,nextID", String.valueOf(questionID) + "," + next_id);
                                    }
                                } else {
                                    QuestionAnswerListActivity.quesId.push(String.valueOf(questionID + 1));
                                    ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(questionID + 1, false);
                                }
                            } else {
                                QuestionAnswerListActivity.quesId.push(String.valueOf(questionID + 1));
                                ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(questionID + 1, false);
                            }
                        }
                        setAnswersChild.setQuestionId(questionID);
                        setAnswersChild.setOther("");
                        setchildEntity.setQuestion_id(questionID);
                        setchildEntity.setOther("");
                    }
                    /*
                    ANSWER TYPE-4 Image with Radio Button with textBox
                    */
                    else if (answerType.equals(answersType[4])) {
                        int selectedID = 1;
                        RadioGroup radioGroup = itemView.findViewWithTag(Constants.RADIO_GROUP_WITH_IMAGE + ques);
                        flag = false;
                        questionID = questionObject.getQuestions().getQuestions().get(ques).getQuestion().getQuestionId();
                        if (radioGroup.getCheckedRadioButtonId() != -1) {
                            AppLog.d("SelectedRadioButton", String.valueOf(radioGroup.getCheckedRadioButtonId()));
                            selectedID = radioGroup.getCheckedRadioButtonId();
                            answerID = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID - 1).getAnswerId();

                            //Setting Values of QuestionId , others and answerList when internet is connected.
                            setAnswersChild.getAnswerId().add(answerID);

                            //Setting Values of QuestionID and Other for Locally
                            setchildEntity.getAnswerId().add(answerID);

                            next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID - 1).getNextId();
                            if (!next_id.equals("")) {
                                nextID = Integer.parseInt(next_id);
                                AppLog.d("questionID,answerID,nextID", String.valueOf(questionID) + "," + answerID + "," + nextID);
                            }
                            if (answerID == optionSize) {
                                setchildEntity.setOther(etTextMsg.getText().toString());
                                setAnswersChild.setOther(etTextMsg.getText().toString());
                            }
                            flag = true;
                        } else {
                            if (isRequiredQuestion.equals(requiredQuestion)) {
                                flag = false;
                                txtQuestion.setBackgroundResource(R.drawable.border_shape_red);
                                Utils.showSnackBar(getActivity(), getString(R.string.err_this_is_required_question));
                                break;
                            } else {
                                //Setting Values of QuestionId , others and answerList when internet is connected.
                                setAnswersChild.setQuestionId(questionID);
                                setAnswersChild.setOther(etTextMsg.getText().toString());

                                //Setting Values of QuestionID and Other for Locally
                                setchildEntity.setQuestion_id(questionID);
                                setchildEntity.setOther(etTextMsg.getText().toString());
                                next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID - 1).getNextId();
                                if (!next_id.equals("")) {
                                    nextID = Integer.parseInt(next_id);
                                    if (nextID == -1) {
                                        showThankYouDialog();
                                    } else {
                                        QuestionAnswerListActivity.quesId.push(questionID + "");
                                        ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
                                    }
                                }
                            }
                        }
                        if (flag) {
                            setchildEntity.setQuestion_id(questionID);
                            next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID - 1).getNextId();
                            if (!next_id.equals("")) {
                                nextID = Integer.parseInt(next_id);
                                if (nextID == -1) {
                                    showThankYouDialog();
                                } else {

                                    QuestionAnswerListActivity.quesId.push(questionID + "");
                                    ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
                                }
                            }
                        }
                        setAnswersChild.setQuestionId(questionID);
                        setAnswersChild.setOther(etTextMsg.getText().toString());
                        setchildEntity.setQuestion_id(questionID);
                        setchildEntity.setQuestion_id(questionID);
                    }
                    /*
                    ANSWER TYPE-5 Radio Button with Textbox
                    */
                    else if (answerType.equals(answersType[5])) {
                        int selectedID = 1;
                        RadioGroup radioGroup = itemView.findViewWithTag("radioGroup" + ques);
                        questionID = questionObject.getQuestions().getQuestions().get(ques).getQuestion().getQuestionId();
                        flag = false;
                        if (radioGroup.getCheckedRadioButtonId() != -1) {
                            AppLog.d("SelectedRadioButton", String.valueOf(radioGroup.getCheckedRadioButtonId()));
                            selectedID = radioGroup.getCheckedRadioButtonId();
                            answerID = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID - 1).getAnswerId();
                            //Setting Values of QuestionId , others and answerList when internet is connected.
                            setAnswersChild.getAnswerId().add(answerID);
                            setAnswersChild.setQuestionId(questionID);
                            setAnswersChild.setOther(etRadioMsg.getText().toString());
                            //Setting Values of QuestionID and Other for Locally
                            setchildEntity.setQuestion_id(questionID);
                            setchildEntity.getAnswerId().add(answerID);
                            setchildEntity.setOther(etRadioMsg.getText().toString());

                            next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID - 1).getNextId();

                            if (!next_id.equals("")) {
                                nextID = Integer.parseInt(next_id);
                                AppLog.d("questionID,answerID,nextID", String.valueOf(questionID) + "," + answerID + "," + next_id);
                            }
                            flag = true;
                        } else {
                            if (isRequiredQuestion.equals(requiredQuestion)) {
                                flag = false;
                                txtQuestion.setBackgroundResource(R.drawable.border_shape_red);
                                Utils.showSnackBar(getActivity(), getString(R.string.err_this_is_required_question));
                                break;
                            } else {
                                //Setting Values of QuestionId , others and answerList when internet is connected.
                                setAnswersChild.setQuestionId(questionID);
                                setAnswersChild.setOther(etRadioMsg.getText().toString());

                                //Setting Values of QuestionID and Other for Locally
                                setchildEntity.setQuestion_id(questionID);
                                setchildEntity.setOther(etRadioMsg.getText().toString());

                                next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID - 1).getNextId();
                                if (!next_id.equals("")) {
                                    nextID = Integer.parseInt(next_id);
                                    if (nextID == -1) {
                                        showThankYouDialog();
                                    } else {
                                        QuestionAnswerListActivity.quesId.push(questionID + "");
                                        ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
                                    }
                                }
                            }
                        }
                        if (flag) {
                            setchildEntity.setQuestion_id(questionID);
                            setchildEntity.setOther(etRadioMsg.getText().toString());
                            if (!next_id.equals("")) {
                                nextID = Integer.parseInt(next_id);
                                if (nextID == -1) {
                                    showThankYouDialog();
                                } else {
                                    QuestionAnswerListActivity.quesId.push(ques + "");
                                    ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
                                }
                            }
                        }
                        setAnswersChild.setQuestionId(questionID);
                        setAnswersChild.setOther(etRadioMsg.getText().toString());
                        setchildEntity.setQuestion_id(questionID);
                        setchildEntity.setOther(etRadioMsg.getText().toString());
                    }
                    /*
                    ANSWER TYPE-6 Simple CheckBox
                    */
                    else if (answerType.equals(answersType[6])) {
                        boolean checkBoxisSelected = false;
                        int selectedID;
                        for (int i = 0; i < questionObject.getQuestions().getQuestions().get(ques).getOptions().size(); i++) {
                            questionID = questionObject.getQuestions().getQuestions().get(ques).getQuestion().getQuestionId();
                            CheckBox checkBox = itemView.findViewById(i);
                            if (checkBox.isChecked()) {
                                checkBoxisSelected = true;
                                selectedID = checkBox.getId();
                                answerID = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID).getAnswerId();

                                //Setting Values of QuestionId , others and answerList when internet is connected.
                                setAnswersChild.getAnswerId().add(answerID);
                                setAnswersChild.setQuestionId(questionID);

                                //Setting Values of QuestionID and Other for Locally
                                setchildEntity.setQuestion_id(questionID);
                                setchildEntity.setOther("");
                                setchildEntity.getAnswerId().add(answerID);
                                next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID).getNextId();
                                if (!next_id.equals("")) {
                                    nextID = Integer.parseInt(next_id);
                                }
                                break;
                            } else {
                                checkBoxisSelected = false;
                            }
                        }
                        if (checkBoxisSelected) {
                            flag = true;
                        } else {
                            if (isRequiredQuestion.equals(requiredQuestion)) {
                                flag = false;
                                txtQuestion.setBackgroundResource(R.drawable.border_shape_red);
                                Utils.showSnackBar(getActivity(), getString(R.string.err_this_is_required_question));
                                break;
                            } else {
                                //Setting Values of QuestionId , others and answerList when internet is connected.
                                setAnswersChild.setQuestionId(questionID);
                                setAnswersChild.setOther("");

                                //Setting Values of QuestionID and Other for Locally
                                setchildEntity.setQuestion_id(questionID);
                                setchildEntity.setOther("");
                                next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(0).getNextId();
                                if (!next_id.equals("")) {
                                    nextID = Integer.parseInt(next_id);
                                    if (nextID == -1) {
                                        showThankYouDialog();
                                    } else {
                                        QuestionAnswerListActivity.quesId.push(questionID + "");
                                        ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
                                        AppLog.d("questionID,nextID", String.valueOf(questionID) + "," + next_id);
                                    }
                                }
                            }
                        }
                        if (flag) {
                            setAnswersChild.setQuestionId(questionID);
                            setAnswersChild.setOther("");
                            setchildEntity.setQuestion_id(questionID);
                            setchildEntity.setOther("");
                            if (!next_id.equals("")) {
                                nextID = Integer.parseInt(next_id);
                                if (nextID == -1) {
                                    showThankYouDialog();
                                } else {
//                                    if (((QuestionAnswerListActivity) getActivity()).binding.viewpager.getParent() != null) {
//                                        ((ViewGroup) ((QuestionAnswerListActivity) getActivity()).binding.viewpager.getParent()).removeView(((QuestionAnswerListActivity) getActivity()).binding.viewpager);
//                                        ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
//                                    } else {
                                    QuestionAnswerListActivity.quesId.push(questionID + "");
                                    ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
//                                    }
                                }
                            }
                        }
                        setchildEntity.setQuestion_id(questionID);
                        setchildEntity.setOther("");
                    }
                    /*
                    ANSWER TYPE-7 Image with Checkbox with textbox
                    */
                    else if (answerType.equals(answersType[7])) {
                        int selectedID = 1;
                        questionID = questionObject.getQuestions().getQuestions().get(ques).getQuestion().getQuestionId();
                        for (int i = 0; i < optionSize; i++) {
                            CheckBox checkBox = itemView.findViewById(i);
                            if (checkBox.isChecked()) {
                                selectedID = checkBox.getId();
                                answerID = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID).getAnswerId();
                                //Setting Values of QuestionId , others and answerList when internet is connected.
                                setAnswersChild.getAnswerId().add(answerID);
                                setAnswersChild.setQuestionId(questionID);
                                setAnswersChild.setOther("");
                                //Setting Values of QuestionID and Other for Locally
                                setchildEntity.setQuestion_id(questionID);
                                setchildEntity.setOther("");
                                setchildEntity.getAnswerId().add(answerID);
                                next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID).getNextId();
                                if (!next_id.equals("")) {
                                    nextID = Integer.parseInt(next_id);
                                    AppLog.d("questionID,answerID,nextID", String.valueOf(questionID) + "," + answerID + "," + next_id);
                                }
                                flag = true;
                            } else {
                                if (isRequiredQuestion.equals(requiredQuestion)) {
                                    flag = false;
                                    txtQuestion.setBackgroundResource(R.drawable.border_shape_red);
                                    Utils.showSnackBar(getActivity(), getString(R.string.err_this_is_required_question));
                                    break;
                                } else {
                                    //Setting Values of QuestionId , others and answerList when internet is connected.
                                    setAnswersChild.setQuestionId(questionID);
                                    setAnswersChild.setOther("");

                                    //Setting Values of QuestionID and Other for Locally
                                    setchildEntity.setQuestion_id(questionID);
                                    setchildEntity.setOther("");
                                    next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID).getNextId();
                                    if (!next_id.equals("")) {
                                        nextID = Integer.parseInt(next_id);
                                        if (nextID == -1) {
                                            showThankYouDialog();
                                        } else {
                                            QuestionAnswerListActivity.quesId.push(questionID + "");
                                            AppLog.d("questionID,nextID", String.valueOf(questionID) + "," + next_id);
                                            ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
                                        }
                                    }
                                }
                            }
                        }
                        if (flag) {
                            setchildEntity.setQuestion_id(questionID);
                            setchildEntity.setOther("");
                            next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID).getNextId();
                            if (!next_id.equals("")) {
                                nextID = Integer.parseInt(next_id);
                                AppLog.d("questionID,nextID", String.valueOf(questionID) + "," + next_id);
                                if (nextID == -1) {
                                    showThankYouDialog();
                                } else {
                                    QuestionAnswerListActivity.quesId.push(questionID + "");
                                    ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
                                }
                            }
                        }
                        setchildEntity.setQuestion_id(questionID);
                        setchildEntity.setOther("");
                    }
                    /*
                    ANSWER TYPE-8 Simple CheckBox With TextArea
                    */
                    else if (answerType.equals(answersType[8])) {
                        int selectedID = 1;
                        for (int i = 0; i < optionSize; i++) {
                            questionID = questionObject.getQuestions().getQuestions().get(ques).getQuestion().getQuestionId();
                            CheckBox checkBox = itemView.findViewById(i);
                            if (checkBox.isChecked()) {
                                selectedID = checkBox.getId();
                                answerID = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID).getAnswerId();
                                AppLog.d("Type-6 : AnswerID", String.valueOf(answerID));

                                //Setting Values of QuestionId , others and answerList when internet is connected.
                                setAnswersChild.getAnswerId().add(answerID);
                                setAnswersChild.setQuestionId(questionID);
                                setAnswersChild.setOther(etTextMsg.getText().toString());
                                //Setting Values of QuestionID and Other for Locally
                                setchildEntity.setQuestion_id(questionID);
                                setchildEntity.setOther(etTextMsg.getText().toString());
                                setchildEntity.getAnswerId().add(answerID);
                                next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID).getNextId();
                                if (!next_id.equals("")) {
                                    nextID = Integer.parseInt(next_id);
                                    AppLog.d("questionID,answerID,nextID", String.valueOf(questionID) + "," + answerID + "," + next_id);
                                }
                                flag = true;
                            } else {
                                if (isRequiredQuestion.equals(requiredQuestion)) {
                                    flag = false;
                                    txtQuestion.setBackgroundResource(R.drawable.border_shape_red);
                                    Utils.showSnackBar(getActivity(), getString(R.string.err_this_is_required_question));
                                    break;
                                } else {
                                    //Setting Values of QuestionId , others and answerList when internet is connected.
                                    setAnswersChild.setQuestionId(questionID);
                                    setAnswersChild.setOther(etTextMsg.getText().toString());
                                    //Setting Values of QuestionID and Other for Locally
                                    setchildEntity.setQuestion_id(questionID);
                                    setchildEntity.setOther(etTextMsg.getText().toString());
                                    next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(selectedID).getNextId();
                                    if (!next_id.equals("")) {
                                        nextID = Integer.parseInt(next_id);
                                        AppLog.d("questionID,nextID", String.valueOf(questionID) + "," + next_id);
                                        if (nextID == -1) {
                                            showThankYouDialog();
                                        } else {
                                            QuestionAnswerListActivity.quesId.push(questionID + "");
                                            ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
                                        }
                                    }
                                }
                            }
                        }
                        if (flag) {
                            setchildEntity.setQuestion_id(questionID);
                            setchildEntity.setOther(etTextMsg.getText().toString());
                            if (!next_id.equals("")) {
                                nextID = Integer.parseInt(next_id);
                                if (nextID == -1) {
                                    showThankYouDialog();
                                } else {
                                    QuestionAnswerListActivity.quesId.push(questionID + "");
                                    ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
                                }
                            }
                        }
                        setchildEntity.setQuestion_id(questionID);
                        setchildEntity.setOther(etTextMsg.getText().toString());
                    }
                    /*
                    ANSWER TYPE-9 Simple TextBox
                    */
                    else if (answerType.equals(answersType[9])) {
                        questionID = questionObject.getQuestions().getQuestions().get(ques).getQuestion().getQuestionId();
                        if (isRequiredQuestion.equals(requiredQuestion)) {
                            if (TextUtils.isEmpty(etTextMsg.getText().toString())) {
                                Utils.showSnackBar(getActivity(), getString(R.string.err_please_type_msg));
                                break;
                            } else {
                                //Setting Values of QuestionId , others and answerList when internet is connected.
                                setAnswersChild.setQuestionId(questionID);
                                setAnswersChild.setOther(etTextMsg.getText().toString());
                                //Setting Values of QuestionID and Other for Locally
                                setchildEntity.setQuestion_id(questionID);
                                setchildEntity.setOther(etTextMsg.getText().toString());

                                if (questionObject.getQuestions().getQuestions().get(ques).getOptions().get(0).getNextId() != null) {
                                    next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(0).getNextId();
                                    nextID = Integer.parseInt(next_id);
                                    if (nextID == -1) {
                                        showThankYouDialog();
                                    } else {
                                        QuestionAnswerListActivity.quesId.push(questionID + "");
                                        ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
                                    }
                                }
                            }
                        } else {
                            //Setting Values of QuestionId , others and answerList when internet is connected.
                            setAnswersChild.setQuestionId(questionID);
                            setAnswersChild.setOther(etTextMsg.getText().toString());

                            //Setting Values of QuestionID and Other for Locally
                            setchildEntity.setQuestion_id(questionID);
                            setchildEntity.setOther(etTextMsg.getText().toString());

                            next_id = questionObject.getQuestions().getQuestions().get(ques).getOptions().get(0).getNextId();
                            nextID = Integer.parseInt(next_id);

                            if (nextID == -1) {
                                showThankYouDialog();
                            } else {
                                QuestionAnswerListActivity.quesId.push(questionID + "");
                                ((QuestionAnswerListActivity) getActivity()).binding.viewpager.setCurrentItem(findNextID(nextID), false);
                            }
                        }
                    }
                    //Stored values in model
                    setAnswers.getAnswers().add(setAnswersChild);
                    //Stored values locally
                    setAnswerEntity.getAnswers().add(setchildEntity);
                }
            }
        });
        return itemView;
    }


    private String getImage(String image) {
        String[] convertedImage = image.split("\\.");  // Eliminate .jpg or .png from name
        return convertedImage[0];
    }

    private void callSetAnswersList() {
        Utils.showProgressDialog(getActivity());
        String answerJSON = new Gson().toJson(setAnswers.getAnswers());
        Call<SetAnswers> call = RestClient.getInstance(true).getApiInterface().SetAnswersListWS(Utils.getRequestMap(true),
                setAnswers.getDate(), setAnswers.getTitle(), answerJSON);
        RestClient.makeApiRequest(getActivity(), call, true, new ApiResponseListener() {
            @Override
            public void onApiResponse(Call<Object> call, Object response) {
                AppLog.d(TAG, getString(R.string.success_response));
                Utils.dismissDialog();
                SetAnswers result = (SetAnswers) response;
                if (result.getStatus() == RequestParameters.STATUS) {
                    AppLog.d(TAG, getString(R.string.success_result));
                    Intent intent = new Intent();
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    AppLog.d(TAG, "ResultOk");
                    getActivity().finish();
                }
            }

            @Override
            public void onApiError(Call<Object> call, Throwable throwable) {
                Utils.dismissDialog();
                AppLog.d(TAG, "onApiError");
                getActivity().finish();
            }
        });
    }

    private void showThankYouDialog() {
        DialogThanksForSubmissionsBinding dialogBinding;
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_thanks_for_submissions);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.dialog_thanks_for_submissions, null, false);
        dialog.setContentView(dialogBinding.getRoot());
        Window window = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(false);
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        dialogBinding.btnThanksOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isConnected(((QuestionAnswerListActivity) getActivity()))) {
                    Utils.dismissDialog();
                    dialog.dismiss();
                    callSetAnswersList();
                } else {
                    Utils.dismissDialog();
                    dialog.dismiss();
                    //Sync with Database and inserting data into it.
                    DBSetAnsMethodSync.insertData(AppDatabase.getAppDatabase(getActivity()), setAnswerEntity);
                    Intent intent = new Intent();
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    AppLog.d(TAG, "ResultOk");
                    getActivity().onBackPressed();
                    return;
                }
            }
        });
    }

    public int findNextID(int nextID) {
        int nextId = 0;
        for (int id = 0; id < questionAnswerModel.getData().size(); id++) {
            for (int ques = 0; ques < questionAnswerModel.getData().get(id).getQuestions().getQuestions().size(); ques++) {
                if (nextID == questionAnswerModel.getData().get(id).getId()) {
                    nextId = id;
                    AppLog.d("***FetchingNextID=", String.valueOf(nextId));
                }
            }
        }
        return nextId;
    }

    public int getIndexId(int questionId) {
        int indexId = 0;
        for (int id = 0; id < questionAnswerModel.getData().size(); id++) {
            for (int ques = 0; ques < questionAnswerModel.getData().get(id).getQuestions().getQuestions().size(); ques++) {
                if (questionId == questionAnswerModel.getData().get(id).getQuestions().getQuestions().get(ques).getQuestion().getQuestionId()) {
                    indexId = id;
                    AppLog.d("***FetchingNextID=", String.valueOf(indexId));
                }
            }
        }
        return indexId;
    }

    @Override
    public void onDestroy() {
        AppDatabase.destroyInstance();
        super.onDestroy();
    }

    public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.MyHolder> {
        public int mSelectedItem = -1;
        Context context;
        Datum datum;
        int ques;
        RadioGroup lnrRadio;
        int optionSize;
        onClickView onClickView;

        public GridViewAdapter(Context context, Datum datum, int ques, RadioGroup lnrRadio, int optionSize) {
            this.context = context;
            this.datum = datum;
            this.ques = ques;
            this.lnrRadio = lnrRadio;
            this.optionSize = optionSize;
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_radio, parent, false);
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_tab, parent, false);
            MyHolder vh = new MyHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder myHolder, final int i) {

//            myHolder.radioBtnC.setChecked(i == mSelectedItem);
//            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            myHolder.radioBtnC.setText(questionObject.getQuestions().getQuestions().get(ques).getOptions().get(i).getText());
//            myHolder.radioBtnC.setId(i + 1);
//            myHolder.radioBtnC.setLayoutParams(param);
            myHolder.txtGrid.setText(questionObject.getQuestions().getQuestions().get(ques).getOptions().get(i).getText());
            myHolder.txtGrid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSelectedItem = i;
                    notifyDataSetChanged();
                    onClickView.onClickView(questionObject.getQuestions().getQuestions().get(ques).getOptions().get(mSelectedItem).getAnswerId(), true);
                }
            });

            if (mSelectedItem == i) {
                myHolder.txtGrid.setBackgroundResource(R.drawable.orange_shape);
//                myHolder.txtGrid.setTextColor(R.color.colorWhite);
            } else {
                myHolder.txtGrid.setBackgroundResource(R.drawable.border_shape_white);
//                myHolder.txtGrid.setTextColor(R.color.colorBlack);
            }

        }

        @Override
        public int getItemCount() {
            return optionSize;
        }

        class MyHolder extends RecyclerView.ViewHolder {
            AppCompatRadioButton radioBtnC;
            TextView txtGrid;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
//                radioBtnC = (AppCompatRadioButton) itemView.findViewById(R.id.radioBtnC);
                txtGrid = (TextView) itemView.findViewById(R.id.txtGrid);


            }


        }

        public void setOnClickItem(onClickView onClickView) {
            this.onClickView = onClickView;
        }


    }


    public interface onClickView {
        void onClickView(int quesID, boolean isTrue);
    }
}
