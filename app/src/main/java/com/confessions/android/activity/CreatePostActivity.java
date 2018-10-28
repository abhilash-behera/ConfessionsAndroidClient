package com.confessions.android.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.confessions.android.R;
import com.confessions.android.Utils;
import com.confessions.android.retrofit.ApiClient;
import com.confessions.android.retrofit.CreatePostResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends AppCompatActivity {

    private ImageView imgPost;
    private Button btnChangeImage;
    private TextInputLayout txtInpPostTitle;
    private TextInputEditText txtPostTitle;
    private TextInputLayout txtInpPostDescription;
    private TextInputEditText txtPostDescription;
    private Spinner spinnerPostType;
    private Button btnCreatePost;

    private String userChoosenTask;

    private final int REQUEST_CAMERA=1;
    private final int SELECT_FILE=2;

    private File selectedImageFile;
    private String[] postTypes;

    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeViews();
    }

    private void initializeViews() {
        imgPost=findViewById(R.id.imgPost);
        btnChangeImage=findViewById(R.id.btnChangeImage);
        txtInpPostTitle=findViewById(R.id.txtInpPostTitle);
        txtPostTitle=findViewById(R.id.txtPostTitle);
        txtInpPostDescription=findViewById(R.id.txtInpPostDescription);
        txtPostDescription=findViewById(R.id.txtPostDescription);
        btnCreatePost=findViewById(R.id.btnCreatePost);
        btnChangeImage.requestFocus();
        spinnerPostType=findViewById(R.id.spinnerPostType);

        postTypes=new String[]{"Choose Post Type...","Confession","Technology","Politics","Sex and Relationships","Health and Fitness"};
        ArrayAdapter<String> postTypesAdapter=new ArrayAdapter<String>(CreatePostActivity.this,android.R.layout.simple_list_item_1,postTypes);
        spinnerPostType.setAdapter(postTypesAdapter);

        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseImageDialog();
            }
        });

        btnCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(everythingValid()){
                    createPost();
                }
            }
        });
    }

    private void createPost(){
        progressDialog=new ProgressDialog(CreatePostActivity.this);
        progressDialog.setTitle("Creating Post");
        progressDialog.setMessage("please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        btnCreatePost.setEnabled(false);

        final String MEDIA_TYPE_TEXT = "text/plain";

        MultipartBody.Part fileBody;
        RequestBody titleBody;
        RequestBody descriptionBody;
        RequestBody typeBody;
        RequestBody authorBody;
        RequestBody timeBody;
        SharedPreferences sharedPreferences=getSharedPreferences(Utils.USER_SHARED_PREF,MODE_PRIVATE);

        fileBody=MultipartBody.Part.createFormData("image",selectedImageFile.getName(),
                RequestBody.create(MediaType.parse("image/*"),selectedImageFile));
        titleBody=RequestBody.create(okhttp3.MediaType.parse(MEDIA_TYPE_TEXT), txtPostTitle.getText().toString());
        descriptionBody=RequestBody.create(okhttp3.MediaType.parse(MEDIA_TYPE_TEXT), txtPostDescription.getText().toString());
        typeBody = RequestBody.create(okhttp3.MediaType.parse(MEDIA_TYPE_TEXT), spinnerPostType.getSelectedItem().toString().toLowerCase());
        authorBody=RequestBody.create(MediaType.parse(MEDIA_TYPE_TEXT),sharedPreferences.getString(Utils.KEY_USER_NAME,""));
        timeBody=RequestBody.create(MediaType.parse(MEDIA_TYPE_TEXT),Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()+"");

        Call<CreatePostResponse> call=ApiClient.getClient()
                .createPost(fileBody,titleBody,descriptionBody,typeBody,authorBody,timeBody);

        call.enqueue(new Callback<CreatePostResponse>() {
            @Override
            public void onResponse(Call<CreatePostResponse> call, Response<CreatePostResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()){
                    if(response.body().isSuccess()){
                        Snackbar.make(btnCreatePost,response.body().getData(),Snackbar.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },700);
                    }else{
                        btnCreatePost.setEnabled(true);
                        Snackbar.make(btnCreatePost,response.body().getData(),Snackbar.LENGTH_LONG)
                                .setActionTextColor(Color.RED).show();
                    }
                }else{
                    btnCreatePost.setEnabled(true);
                    Snackbar.make(btnCreatePost,"Something went wrong. Please try again.",
                            Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CreatePostResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("awesome","Failed to create post: "+t.getLocalizedMessage());
                btnCreatePost.setEnabled(true);
                Snackbar.make(btnCreatePost,"Something went wrong. Please try again.",Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private boolean everythingValid(){
        if(selectedImageFile==null){
            Snackbar.make(btnCreatePost,"Please select an image",Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(txtPostTitle.getText().toString().length()<5){
            Snackbar.make(btnCreatePost,"Title must be atleast 5 characters long",Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(txtPostDescription.getText().toString().length()<10){
            Snackbar.make(btnCreatePost,"Description is too short",Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(spinnerPostType.getSelectedItem().equals(postTypes[0])){
            Snackbar.make(btnCreatePost,"Please select a post type",Snackbar.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private void showChooseImageDialog() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utils.checkPermission(CreatePostActivity.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result){
                        cameraIntent();
                    }
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result){
                        galleryIntent();

                    }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utils.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        try{
            Uri selectedImageUri = data.getData();
            System.out.println(selectedImageUri);
            String selectedImagePath = getPath(selectedImageUri);//returns as null
            System.out.println("Image Path : " + selectedImagePath);
            imgPost.setImageURI(selectedImageUri);
            selectedImageFile=new File(getUriRealPath(CreatePostActivity.this,selectedImageUri));
            Log.d("awesome","selectedImageFile:"+selectedImageFile.toString());
        }catch (Exception e){
            Snackbar.make(btnCreatePost,"Something went wrong. Please try again.",Snackbar.LENGTH_LONG).show();
        }

    }

    private String getUriRealPath(Context ctx, Uri uri)
    {
        String ret = "";

        if( isAboveKitKat() )
        {
            // Android OS above sdk version 19.
            ret = getUriRealPathAboveKitkat(ctx, uri);
        }else
        {
            // Android OS below sdk version 19
            ret = getImageRealPath(getContentResolver(), uri, null);
        }

        return ret;
    }

    private String getUriRealPathAboveKitkat(Context ctx, Uri uri)
    {
        String ret = "";

        if(ctx != null && uri != null) {

            if(isContentUri(uri))
            {
                if(isGooglePhotoDoc(uri.getAuthority()))
                {
                    ret = uri.getLastPathSegment();
                }else {
                    ret = getImageRealPath(getContentResolver(), uri, null);
                }
            }else if(isFileUri(uri)) {
                ret = uri.getPath();
            }else if(isDocumentUri(ctx, uri)){

                // Get uri related document id.
                String documentId = DocumentsContract.getDocumentId(uri);

                // Get uri authority.
                String uriAuthority = uri.getAuthority();

                if(isMediaDoc(uriAuthority))
                {
                    String idArr[] = documentId.split(":");
                    if(idArr.length == 2)
                    {
                        // First item is document type.
                        String docType = idArr[0];

                        // Second item is document real id.
                        String realDocId = idArr[1];

                        // Get content uri by document type.
                        Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        if("image".equals(docType))
                        {
                            mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        }else if("video".equals(docType))
                        {
                            mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        }else if("audio".equals(docType))
                        {
                            mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        // Get where clause with real document id.
                        String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;

                        ret = getImageRealPath(getContentResolver(), mediaContentUri, whereClause);
                    }

                }else if(isDownloadDoc(uriAuthority))
                {
                    // Build download uri.
                    Uri downloadUri = Uri.parse("content://downloads/public_downloads");

                    // Append download document id at uri end.
                    Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.valueOf(documentId));

                    ret = getImageRealPath(getContentResolver(), downloadUriAppendId, null);

                }else if(isExternalStoreDoc(uriAuthority))
                {
                    String idArr[] = documentId.split(":");
                    if(idArr.length == 2)
                    {
                        String type = idArr[0];
                        String realDocId = idArr[1];

                        if("primary".equalsIgnoreCase(type))
                        {
                            ret = Environment.getExternalStorageDirectory() + "/" + realDocId;
                        }
                    }
                }
            }
        }

        return ret;
    }

    /* Check whether current android os version is bigger than kitkat or not. */
    private boolean isAboveKitKat()
    {
        boolean ret = false;
        ret = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        return ret;
    }

    /* Check whether this uri represent a document or not. */
    private boolean isDocumentUri(Context ctx, Uri uri)
    {
        boolean ret = false;
        if(ctx != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(ctx, uri);
        }
        return ret;
    }

    /* Check whether this uri is a content uri or not.
     *  content uri like content://media/external/images/media/1302716
     *  */
    private boolean isContentUri(Uri uri)
    {
        boolean ret = false;
        if(uri != null) {
            String uriSchema = uri.getScheme();
            if("content".equalsIgnoreCase(uriSchema))
            {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this uri is a file uri or not.
     *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
     * */
    private boolean isFileUri(Uri uri)
    {
        boolean ret = false;
        if(uri != null) {
            String uriSchema = uri.getScheme();
            if("file".equalsIgnoreCase(uriSchema))
            {
                ret = true;
            }
        }
        return ret;
    }


    /* Check whether this document is provided by ExternalStorageProvider. */
    private boolean isExternalStoreDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.externalstorage.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by DownloadsProvider. */
    private boolean isDownloadDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.providers.downloads.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by MediaProvider. */
    private boolean isMediaDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.providers.media.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by google photos. */
    private boolean isGooglePhotoDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.google.android.apps.photos.content".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Return uri represented document file real local path.*/
    private String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause)
    {
        String ret = "";

        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if(cursor!=null)
        {
            boolean moveToFirst = cursor.moveToFirst();
            if(moveToFirst)
            {

                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;

                if( uri==MediaStore.Images.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Images.Media.DATA;
                }else if( uri==MediaStore.Audio.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Audio.Media.DATA;
                }else if( uri==MediaStore.Video.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Video.Media.DATA;
                }

                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);

                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
            }
        }

        return ret;
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            imgPost.setImageURI(Uri.fromFile(destination));
            selectedImageFile=destination;
        } catch (Exception e) {
            Snackbar.make(btnCreatePost,"Something went wrong. Please try again.",Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
