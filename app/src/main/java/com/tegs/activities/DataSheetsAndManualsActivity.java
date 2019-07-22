package com.tegs.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.tegs.R;
import com.tegs.adapters.DataSheetsAndManualsAdapter;
import com.tegs.base.BaseActivity;
import com.tegs.databinding.ActivityDataSheetsManualsBinding;
import com.tegs.model.GetPdfData;
import com.tegs.model.PdfChildData;
import com.tegs.utils.AppLog;
import com.tegs.utils.Constants;
import com.tegs.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created
 * by heena on 3/1/18.
 */

public class DataSheetsAndManualsActivity extends BaseActivity {
    private ActivityDataSheetsManualsBinding binding;
    private List<PdfChildData> dataList = new ArrayList<>();
    private Activity activity = DataSheetsAndManualsActivity.this;
    private List<PdfChildData> filterList = new ArrayList<>();
    private DataSheetsAndManualsAdapter adapter;
    private Context mContext = DataSheetsAndManualsActivity.this;
    private static final String AUTHORITY = "com.tegs";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_data_sheets_manuals);

        //Setting Toolbar
        initToolbar(this);
        setToolbarTitle(getString(R.string.data_sheets));
        setValues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showFilterDialog(activity, loadJSONFromAsset(), new onTextClickView() {
            @Override
            public void onTextClickView(String catName) {
                AppLog.d("SelectedCatName", catName);
                if (catName != null) {
                    filterList = Utils.getDataAsCategory(loadJSONFromAsset(), catName);
                    filterValues();
                }
            }
        });
        return true;
    }

    private class GetAssetsTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... docPdfFile) {
            try {
                //Storing json to Model Class named GetPdfData
                GetPdfData brochuresData = new Gson().fromJson(loadJSONFromAsset(), GetPdfData.class);
                for (int data = 0; data < brochuresData.getData().size(); data++) {
                    PdfChildData brochures = new PdfChildData();
                    brochures.setTitle(brochuresData.getData().get(data).getTitle());
                    brochures.setAddr(brochuresData.getData().get(data).getAddr());
                    brochures.setCatetory(brochuresData.getData().get(data).getCatetory());
                    brochures.setDate(brochuresData.getData().get(data).getDate());
                    brochures.setDesc(brochuresData.getData().get(data).getDesc());
                    brochures.setDoc(brochuresData.getData().get(data).getDoc());
                    String filePath = brochuresData.getData().get(data).getDoc();
                    if ((new File(getFilesDir().getAbsolutePath() + "/" + filePath).exists())) {
                        brochures.setImagePath((getFilesDir().getAbsolutePath() + "/" + filePath));
                    } else {
                        File imageFile = generateImage(brochuresData.getData().get(data).getDoc());
                        brochures.setImagePath(imageFile.getAbsolutePath());
                    }
                    dataList.add(brochures);
                }
            } catch (Exception e) {
                e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Utils.dismissDialog();
            adapter = new DataSheetsAndManualsAdapter(dataList);
            binding.recViewDataSheetsManuals.setLayoutManager(new LinearLayoutManager(DataSheetsAndManualsActivity.this));
            binding.recViewDataSheetsManuals.setAdapter(adapter);

            searchList();
            clickOnToOpenPdf();
            clickOnToSharePdf();
        }
    }

    private void searchList() {
        EditText etSearchText;
        etSearchText = findViewById(R.id.et_search_text);
        etSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchingList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterValues() {
        if (filterList.size() != 0) {
            adapter = new DataSheetsAndManualsAdapter(new ArrayList<PdfChildData>());
            adapter.clearList();
            for (int data = 0; data < filterList.size(); data++) {
                filterList.get(data).setImagePath((getFilesDir().getAbsolutePath() + "/" + filterList.get(data).getDoc()));
            }
            adapter.addList(filterList);
            binding.recViewDataSheetsManuals.setAdapter(adapter);
            clickOnToOpenPdf();
            clickOnToSharePdf();
        }
    }

    private void clickOnToOpenPdf() {
        adapter.setOnClickView(new DataSheetsAndManualsAdapter.onPdfClickView() {
            @Override
            public void onPdfClickView(String docPdfName) {
                boolean result = Utils.checkPermission(DataSheetsAndManualsActivity.this);
                if (result) {
                    accessAssetsPdfFile(docPdfName, true);
                }
            }
        });
    }

    private void clickOnToSharePdf() {
        adapter.setOnImageClickView(new DataSheetsAndManualsAdapter.onImageShareClick() {
            @Override
            public void onImageShareClickView(String docPdfname) {
                boolean result = Utils.checkPermission(mContext);
                if (result) {
                    accessAssetsPdfFile(docPdfname, false);
                }
            }
        });
    }


    private File generateImage(String docPdfFile) {
        File storedBitmap = null;
        if (docPdfFile != null) {
            AssetManager assetManager = this.getAssets();
            InputStream in = null;
            try {
                in = assetManager.open("pdf/" + docPdfFile + ".pdf");
                OutputStream out = new FileOutputStream(getFilesDir().getAbsolutePath() + "/" + docPdfFile + ".pdf");
                byte[] buffer = new byte[1024];
                int read = in.read(buffer);
                while (read != -1) {
                    out.write(buffer, 0, read);
                    read = in.read(buffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            File fetchFile = new File(getFilesDir().getAbsolutePath() + "/" + docPdfFile + ".pdf");
            generateImageFromPdf(fetchFile, docPdfFile);
            storedBitmap = new File(getFilesDir().getAbsolutePath() + "/" + docPdfFile);
        }
        return storedBitmap;
    }

    private void generateImageFromPdf(File file, String docPdfFileName) {
        int pageNumber = 0;
        Bitmap bitmap = null;
        PdfiumCore pdfiumCore = new PdfiumCore(this);
        try {
            ParcelFileDescriptor fd = getContentResolver().openFileDescriptor(Uri.fromFile(file), "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, pageNumber);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
            saveImage(bmp, docPdfFileName);
            pdfiumCore.closeDocument(pdfDocument);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void saveImage(Bitmap bmp, String docPdfFileName) {
        FileOutputStream out = null;
        try {
            File folder = new File(getFilesDir().getAbsolutePath() + "/");
            if (!folder.exists())
                folder.mkdirs();
            File file = new File(folder, docPdfFileName);
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void searchingList(String searchingString) {
        List<PdfChildData> filteredList = new ArrayList<PdfChildData>();
        for (PdfChildData d : dataList) {
            if (d.getTitle().toLowerCase().contains(searchingString.toLowerCase())) {
                filteredList.add(d);
            }
        }
        //update recyclerview
        adapter = new DataSheetsAndManualsAdapter(new ArrayList<PdfChildData>());
        adapter.clearList();
        adapter.addList(filteredList);
        binding.recViewDataSheetsManuals.setAdapter(adapter);
        clickOnToOpenPdf();
        clickOnToSharePdf();
    }

    private void setValues() {
        Utils.showProgressDialog(this);
        new GetAssetsTask().execute();
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open(Constants.DATASHEETSMANUALS_JSON);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void accessAssetsPdfFile(String docPdfFileName, boolean isOnlyView) {
        if (docPdfFileName != null) {
            AssetManager assetManager = DataSheetsAndManualsActivity.this.getAssets();
            try {
                //"pdf/" + docPdfFileName + ".pdf" path is used for accessing from assets folder under main
                InputStream in = assetManager.open("pdf/" + docPdfFileName + ".pdf");
                // Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + docPdfFileName + ".pdf" path is used to
                //store pdf externally
                OutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + docPdfFileName + ".pdf");
                byte[] buffer = new byte[1024];
                int read = in.read(buffer);
                while (read != -1) {
                    out.write(buffer, 0, read);
                    read = in.read(buffer);
                }
            } catch (Exception e) {
                e.getMessage();
            }
            try {
                //Fetch file from external storage
                File fetchFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + docPdfFileName + ".pdf");
                Intent openpdfIntent;
                if (isOnlyView) {
                    openpdfIntent = new Intent(Intent.ACTION_VIEW);
                    Uri pdfUri = FileProvider.getUriForFile(
                            this,
                            AUTHORITY + ".fileprovider", fetchFile);
                    openpdfIntent.setDataAndType(pdfUri, "application/pdf");
                    openpdfIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    openpdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(openpdfIntent);
                } else {
                    openpdfIntent = new Intent(Intent.ACTION_SEND);
                    Uri pdfUri = FileProvider.getUriForFile(
                            this,
                            AUTHORITY + ".fileprovider", fetchFile);
                    openpdfIntent.setType("application/pdf");
                    openpdfIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
                    startActivity(openpdfIntent);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, "No Application found for this type of file.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
