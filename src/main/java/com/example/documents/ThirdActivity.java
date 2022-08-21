package com.example.documents;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static com.rjesture.startupkit.utils.AppTools.getTextInputEditTextData;
import static com.rjesture.startupkit.utils.AppTools.handleCatch;
import static com.rjesture.startupkit.utils.AppTools.setLog;
import static com.rjesture.startupkit.utils.AppTools.showToast;

public class ThirdActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String IMAGE_DIRECTORY_NAME = "DocumentsD";
    private static final int SELECT_PICTURE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int permission_granted_code = 100;
    private static final String[] appPermission =
            {
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
    public Uri fileUri;
    ArrayList<HashMap<String, String>> userArrayList = new ArrayList<>();
    UserAdapter userAdapter;
    FloatingActionButton fab_add;
    private RecyclerView rv_profile;
    private Button btn_clear_all;
    //    private EditText et_search;
    private SearchView searchView;

    public static Bitmap stringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private static File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        setId();
        setData();
        setListener();

    }

    private void setId() {
        rv_profile = findViewById(R.id.rv_profile);
        btn_clear_all = findViewById(R.id.btn_clear_all);
        //       et_search = findViewById(R.id.et_search);
        searchView = findViewById(R.id.search);
        fab_add = findViewById(R.id.fab_add);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        userAdapter.filter(text);
        return false;
    }

    private void setData() {
        try {
            ArrayList<HashMap<String, String>> getDataArray = SaveData.getArrayList("myArrayList", this);
            if (getDataArray != null) {
                userArrayList = getDataArray;
                setAdapter();

                setLog("MyImageArray", "size " + userArrayList.size());
            }
        } catch (Exception e) {
            handleCatch(e);
        }
    }

    private void setListener() {
        btn_clear_all.setOnClickListener(v -> {
            DeleteAllDialogBox();
        });
//        et_search.setOnClickListener(v -> {
//            searchData();
//        });
        searchView.setOnQueryTextListener(this);
        fab_add.setOnClickListener(v -> {
            choosePictureDialogBox();
        });
    }

    private void choosePictureDialogBox() {

        final Dialog dialog = new Dialog(ThirdActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialogueboxchoose);
        dialog.show();
        ImageView camera_image = dialog.findViewById(R.id.camera_icon);
        ImageView gallery_image = dialog.findViewById(R.id.gallery_icon);

        if (checkAndRequestPermissions()) {
            camera_image.setOnClickListener(v -> {
                takePictureFromCamera();
                dialog.dismiss();
            });
            gallery_image.setOnClickListener(v -> {
                takePictureFromGallery();
                dialog.dismiss();
            });
        }
    }

    private boolean checkAndRequestPermissions() {
        /*if (Build.VERSION.SDK_INT >= 23) {
            int cameraPermission = ActivityCompat.checkSelfPermission(SecondActivity.this, Manifest.permission.CAMERA);
            if (cameraPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(SecondActivity.this, new String[]{Manifest.permission.CAMERA}, 20);
                return false;
            }
        }
        return true;*/
        List<String> listPermissionsNeedeFor = new ArrayList<>();
        for (String permission : appPermission) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeedeFor.add(permission);
            }
        }
        if (!listPermissionsNeedeFor.isEmpty()) {
            ActivityCompat.requestPermissions(ThirdActivity.this, listPermissionsNeedeFor.toArray(new String[listPermissionsNeedeFor.size()]), permission_granted_code);
            return false;
        }
        return true;
    }

    private void takePictureFromCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private void takePictureFromGallery() {
//        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//        photoPickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        photoPickerIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        photoPickerIntent.setType("image/*");
//        startActivityForResult(photoPickerIntent, SELECT_PICTURE);

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        photoPickerIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PICTURE);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private void DeleteAllDialogBox() {
        final Dialog dialog = new Dialog(ThirdActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialogueboxalert);
        dialog.show();
        Button btn_yes = dialog.findViewById(R.id.btn_yes);
        Button btn_no = dialog.findViewById(R.id.btn_no);

        btn_yes.setOnClickListener(v -> {
            empty_array_list();
            dialog.dismiss();
        });
        btn_no.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void empty_array_list() {
        ArrayList<HashMap<String, String>> empty_list = new ArrayList<>();
        SaveData.saveArrayList(empty_list, "myArrayList", this);
        setLog("MyArraySize", "Size " + empty_list.size());
        if (!userArrayList.isEmpty()) {
            userArrayList.clear();
            userAdapter.notifyDataSetChanged();
        } else
            showToast(this, "Already empty");
    }

    private void setAdapter() {
        rv_profile.setHasFixedSize(true);
        rv_profile.setLayoutManager(new LinearLayoutManager(this));
        rv_profile.setNestedScrollingEnabled(false);
        userAdapter = new UserAdapter(userArrayList);
        rv_profile.setAdapter(userAdapter);

    }

    public void showPopup(TextView tv_anchor, int position) {
        PopupMenu popupMenu = new PopupMenu(ThirdActivity.this, tv_anchor);
        popupMenu.inflate(R.menu.menu_single_picture_item);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_rename:
                        showToast(ThirdActivity.this, "Option rename clicked");
                        rename(position);
                        break;
                    case R.id.menu_delete:
                        delete(position);
                }
                return false;
            }
        });
        popupMenu.show();
    }

    public void delete(int position) {
        Dialog dialog = new Dialog(ThirdActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogueboxdelete);
        dialog.setCancelable(true);
        dialog.show();
        Button btn_yes = dialog.findViewById(R.id.btn_yes);
        Button btn_no = dialog.findViewById(R.id.btn_no);
        btn_yes.setOnClickListener(v -> {
            userArrayList.remove(position);
            userAdapter.notifyDataSetChanged();
            SaveData.saveArrayList(userArrayList, "myArrayList", ThirdActivity.this);
            showToast(ThirdActivity.this, "Image deleted");
            dialog.dismiss();
        });
        btn_no.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    public void rename(int position) {
        Dialog dialog = new Dialog(ThirdActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.menu_rename_dialogbox);
        dialog.setCancelable(true);
        dialog.show();
        EditText et_new_name = dialog.findViewById(R.id.et_new_name);
        Button btn_done = dialog.findViewById(R.id.btn_done);
        Button btn_leave = dialog.findViewById(R.id.btn_leave);
        btn_done.setOnClickListener(v -> {
            if (getTextInputEditTextData(et_new_name) != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    userArrayList.get(position).replace("name", getTextInputEditTextData(et_new_name));
                } else {
                    userArrayList.get(position).remove("name");
                    userArrayList.get(position).put("name", getTextInputEditTextData(et_new_name));
                }
                userAdapter.notifyDataSetChanged();
                SaveData.saveArrayList(userArrayList, "myArrayList", this);
                setLog("MyNewArray", userArrayList.toString());
                dialog.dismiss();
            } else
                showToast(ThirdActivity.this, "Please enter a valid name");
            btn_leave.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
        });
    }

    public void fullscreendialogbox(int v) {
        Dialog dialog = new Dialog(ThirdActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.fullscreen_activity);
        dialog.show();
        ImageView iv_fullscreen = dialog.findViewById(R.id.iv_fullscreen);
        iv_fullscreen.setImageBitmap(stringToBitMap(userArrayList.get(v).get("image")));
    }

//    public void fullScreenFunction(){
//        Intent intent = new Intent(this,
//                FullScreenActivity.class);
//
//        if("y".equals(fullScreenInd)){
//            intent.putExtra("fullScreenIndicator", "");
//        }else{
//            intent.putExtra("fullScreenIndicator", "y");
//        }
//        this.startActivity(intent);
//    }

    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

        ArrayList<HashMap<String, String>> arrayList1;

        public UserAdapter(ArrayList<HashMap<String, String>> arrayListEvent) {
            arrayList1 = arrayListEvent;
        }

        @NonNull
        @Override
        public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_picture_item, viewGroup, false);
            return new UserAdapter.MyViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return arrayList1.size();
        }


        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
//            setImgPicasso(arrayList1.get(i).get("image"),ThirdActivity.this,myViewHolder.iv_pic);
            myViewHolder.iv_pic.setImageBitmap(stringToBitMap(arrayList1.get(position).get("image")));
            myViewHolder.tv_pic_name.setText(arrayList1.get(position).get("name"));
            myViewHolder.tv_date.setText(arrayList1.get(position).get("date"));
            myViewHolder.iv_pic.setOnClickListener(v -> {
                fullscreendialogbox(position);
                //fullScreenFunction();

            });
            myViewHolder.tv_menu.setOnClickListener(v -> {
                showPopup(myViewHolder.tv_pic_name, position);
            });

        /*
        myViewHolder.rl_users.setOnClickListener(view ->
                startActivity(new Intent(mActivity, UserProfile.class).putExtra("userId", arrayList1.get(i).get("id"))));
    */
        }

        public void filter(String text) {
            ArrayList<HashMap<String, String>> searchDataArray = new ArrayList<>();
            text = text.toLowerCase(Locale.getDefault());
            if (text.length() == 0) {
                searchDataArray.addAll(userArrayList);
            } else {
                for (int i = 0; i < userArrayList.size(); i++) {
                    if (userArrayList.get(i).get("name").toLowerCase(Locale.getDefault()).contains(text)) {
                        searchDataArray.add(userArrayList.get(i));
                    }
                }
            }
            UserAdapter userAdapter = new UserAdapter(searchDataArray);
            rv_profile.setAdapter(userAdapter);
            notifyDataSetChanged();


            //        ArrayList<HashMap<String, String>> searchDataArray = new ArrayList<>();
//        if (getTextInputEditTextData(et_search).isEmpty()) {
//            searchDataArray = userArrayList;
//        } else {
//            for (int i = 0; i < userArrayList.size(); i++) {
//                if (userArrayList.get(i).get("name").toLowerCase().contains(getTextInputEditTextData(et_search).toLowerCase())) {
//                    searchDataArray.add(userArrayList.get(i));
//                }
//            }
//        }
//        UserAdapter userAdapter = new UserAdapter(searchDataArray);
//        rv_profile.setAdapter(userAdapter);
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_pic_name;
            ImageView iv_pic;
            TextView tv_date;
            TextView tv_menu;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_date = itemView.findViewById(R.id.tv_time);
                tv_pic_name = itemView.findViewById(R.id.tv_pic_name);
                iv_pic = itemView.findViewById(R.id.iv_pic);
                tv_menu = itemView.findViewById(R.id.tv_menu);
            }

        }
    }
}