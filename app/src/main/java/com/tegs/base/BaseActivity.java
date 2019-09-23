package com.tegs.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tegs.R;
import com.tegs.activities.SpareDetailsActivity;
import com.tegs.utils.Utils;

import java.util.List;


/**
 * Created
 * by heena on 27/12/17.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    public Toolbar toolbar;
    private TextView txtToolbarTitle;
    private String selectedFilterText = "abc";
    public ImageView imgExit;

    public void initToolbar(final Activity activity) {
        toolbar = findViewById(R.id.toolbar);//Toolbar
        txtToolbarTitle = findViewById(R.id.txt_toolbar_title); //Toolbar Title
        imgExit = findViewById(R.id.imgExit); //Toolbar Title
        toolbar.inflateMenu(R.menu.menu_edit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.back_arrow_with_text_2);
        toolbar.setPadding(15, 0, 0, 0);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(activity);
                onBackPressed();
            }
        });
        imgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    public void setToolMenu(int menu, final Activity activtiy) {
        toolbar.inflateMenu(menu);
        Log.e("Menu", String.valueOf(menu));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals(getString(R.string.edit))) {
                    SpareDetailsActivity nActivity = (SpareDetailsActivity) activtiy;
                    nActivity.menuClick();
                }
                return true;
            }
        });
    }

    public void showFilterDialog(final Activity activtiy, String jsonArray, final onTextClickView onTextClickView) {
        final Dialog dialog = new Dialog(activtiy);
        dialog.setContentView(R.layout.dialog_filter);
        LinearLayout lnrFilterCategory = dialog.findViewById(R.id.lnr_category);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(true);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (jsonArray != null) {
            final List<String> catNameList = Utils.getCatNameList(jsonArray);
            for (int cat = 0; cat < catNameList.size(); cat++) {
                LayoutInflater inflater = (LayoutInflater) activtiy.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final TextView textView = (TextView) inflater.inflate(R.layout.view_filter_text_view, lnrFilterCategory, false);
                textView.setText(catNameList.get(cat));
                textView.setId(cat);
                final int position = cat;
                if (textView.getText().toString().equals(selectedFilterText)) {
                    textView.setSelected(true);
                } else {
                    textView.setSelected(false);
                }
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textView.setSelected(true);
                        selectedFilterText = textView.getText().toString();
                        String selCat = catNameList.get(position);
                        onTextClickView.onTextClickView(selCat);
                        dialog.dismiss();
                    }
                });
                if (textView.getParent() != null) {
                    lnrFilterCategory.removeAllViews();
                }
                lnrFilterCategory.addView(textView);
            }
        }
        dialog.show();
    }

    public void setOnClickView(onTextClickView clickView) {
        BaseActivity.onTextClickView onTextClickView = clickView;
    }

    public interface onTextClickView {
        void onTextClickView(String catName);
    }


    @Override
    public void onClick(View v) {
    }

    public void setToolbarTitle(String title) {
        txtToolbarTitle.setText(title);
    }
}
