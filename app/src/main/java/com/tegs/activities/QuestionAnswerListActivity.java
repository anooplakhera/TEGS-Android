package com.tegs.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.tegs.DatabaseHandler.SetAnswerEntity;
import com.tegs.QuestionAnswerModel.QuestionAnswerModel;
import com.tegs.R;
import com.tegs.adapters.QuesAnsPagerAdapter;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivityQuestionAnswerBinding;
import com.tegs.fragment.QuestionAnswerFragment;
import com.tegs.model.SetAnswers;
import com.tegs.utils.AppLog;
import com.tegs.utils.Utils;

import java.util.Stack;

/**
 * Created
 * by heena on 28/12/17.
 */

public class QuestionAnswerListActivity extends BaseActivity implements View.OnClickListener {
    public ActivityQuestionAnswerBinding binding;
    private QuestionAnswerModel questionsModel;
    public SetAnswers setAnswers;
    public SetAnswerEntity setAnswerEntity;
    public static Stack<String> quesId = new Stack<>();
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_question_answer);

        if (Utils.getQuestionAnswerData() != null) {
            questionsModel = Utils.getQuestionAnswerData(); //Fetching questionData Json from Shared Preferences.
        }


        setAnswers = (SetAnswers) getIntent().getExtras().getSerializable("AnswerList");
        setAnswerEntity = (SetAnswerEntity) getIntent().getExtras().getSerializable("AnswerListForEntity");

        QuesAnsPagerAdapter pagerAdapter = new QuesAnsPagerAdapter(getSupportFragmentManager());
        if (questionsModel != null) {
            if (questionsModel.getData() != null) {
                pagerAdapter.addItem(questionsModel.getData());
            }
        }


        /*
        Setting Toolbar
         */
        initToolbar(this);
        imgExit.setVisibility(View.VISIBLE);


        setToolbarTitle(questionsModel.getData().get(0).getQuestions().getTitle());
        binding.viewpager.setAdapter(pagerAdapter);
        binding.viewpager.addOnPageChangeListener(onPageChangeListener);
        int viewPagerCurrentItem = binding.viewpager.getCurrentItem();
        AppLog.d("ViewPagerCurrentItem", String.valueOf(viewPagerCurrentItem));


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuestionAnswerFragment fragment = new QuestionAnswerFragment();
                if (quesId != null && quesId.size() != 0) {
                    binding.viewpager.setCurrentItem(((QuestionAnswerFragment) fragment).getIndexId(Integer.parseInt(quesId.pop())), false);
                    AppLog.d("ArrayListQues", quesId + "");
                } else {
                    onBackPressed();
                }

            }
        });

    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            setToolbarTitle(questionsModel.getData().get(position).getQuestions().getTitle());
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };


    @Override
    public void onBackPressed() {
        QuestionAnswerFragment fragment = new QuestionAnswerFragment();
        if (quesId != null && quesId.size() != 0) {
            binding.viewpager.setCurrentItem(((QuestionAnswerFragment) fragment).getIndexId(Integer.parseInt(quesId.pop())), false);
            AppLog.d("ArrayListQues", quesId + "");
        } else {
            onBackPressed();
        }
    }
}
