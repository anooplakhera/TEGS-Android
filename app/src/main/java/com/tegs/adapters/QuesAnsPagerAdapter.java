package com.tegs.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tegs.QuestionAnswerModel.QuestionAnswerModel.Datum;
import com.tegs.fragment.QuestionAnswerFragment;
import com.tegs.utils.AppLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created
 * by heena on 2/2/18.
 */

public class QuesAnsPagerAdapter extends FragmentPagerAdapter {
    private List<Datum> questionsList = new ArrayList<>();

    public QuesAnsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addItem(List<Datum> list) {
        this.questionsList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return QuestionAnswerFragment.newInstance(position,questionsList.get(position));
    }

    @Override
    public int getCount() {
        return questionsList.size();
    }
}
