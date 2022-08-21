package com.example.documents;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rjesture.startupkit.database.MemoryBox;
import com.rjesture.startupkit.utils.DateFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static com.rjesture.startupkit.utils.AppTools.handleCatch;
import static com.rjesture.startupkit.utils.AppTools.setLog;
import static com.rjesture.startupkit.utils.AppTools.showAlertDialog;
import static com.rjesture.startupkit.utils.AppTools.showToast;

public class SecondActivity extends AppCompatActivity {

    private static final int permission_granted_code = 100;
    private static final String[] appPermission =
            {
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
    private static final int SELECT_PICTURE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final String IMAGE_DIRECTORY_NAME = "DocumentsD";
    public Uri fileUri;
    MemoryBox memoryBox;
    FloatingActionButton fab_add;
    ImageView iv_addPic, iv_folder;
    Button btn_save;
    EditText et_name;
    String myNewPic = "";
    String name = "";
    OutputStream outputStream;
    ArrayList<HashMap<String, String>> imageList = new ArrayList<>();

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

    public static int getImageOrientation(String imagePath) {
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        setIds();
        setListener();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
//        setData();
        getPreviousData();
    }

    private void getPreviousData() {
        try {
            ArrayList<HashMap<String, String>> getDataArray = SaveData.getArrayList("myArrayList", this);
            if (getDataArray != null)
                imageList = getDataArray;
            setLog("MyImageArray", "size " + imageList.size());
        } catch (Exception e) {
            handleCatch(e);
        }
    }

//    private void setData() {
//        try {
//            String myPic = memoryBox.get(profilePic);
//            if (!myPic.isEmpty()) {
//                iv_addPic.setImageBitmap(stringToBitMap(myPic));
//            }
//        }catch (Exception e){
//            handleCatch(e);
//        }
//    }

    private void setIds() {
        fab_add = findViewById(R.id.fab_add);
        iv_addPic = findViewById(R.id.iv_addPic);
        btn_save = findViewById(R.id.btn_save);
        memoryBox = new MemoryBox(SecondActivity.this);
        iv_folder = findViewById(R.id.iv_folder);
        et_name = findViewById(R.id.et_name);
    }

    private void setListener() {
        fab_add.setOnClickListener(v -> choosePictureDialogBox());
        btn_save.setOnClickListener(v -> {
            try {
                if (myNewPic.isEmpty()) {
                    showToast(SecondActivity.this, "No image is uploaded.");
                } else if (setName().isEmpty()) {
                    showToast(SecondActivity.this, "Enter a title.");
                } else {
                    // saving data in database
//                    memoryBox.set(profilePic,myNewPic).commit();
                    saveData(myNewPic, new DateFactory("").getCurrentDate(), name);
                    showToast(SecondActivity.this, "Image uploaded  successfully");
                }
            } catch (Exception e) {
                handleCatch(e);
            }
        });
        iv_folder.setOnClickListener(v -> {
            if (imageList.size() <= 0) {
                showToast(SecondActivity.this, "Please upload an image");
            } else {
                startActivity(new Intent(SecondActivity.this, ThirdActivity.class));
            }
        });
    }

    public void saveData(String myNewPic, String date, String name) {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("image", myNewPic);
        hashMap.put("date", date);
        hashMap.put("name", name);
        imageList.add(hashMap);
//        setLog("MyImageArray"," "+imageList);
        SaveData.saveArrayList(imageList, "myArrayList", this);
        setLog("MyImageArray", " " + imageList);
        setLog("MyImageArray", "size " + imageList.size());
    }

    private void choosePictureDialogBox() {

        final Dialog dialog = new Dialog(SecondActivity.this);
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

    private void takePictureFromCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
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

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }

    public void setPictures(Bitmap b, String picturePath, Matrix matrix, String encodedString) {
        matrix.postRotate(getImageOrientation(picturePath));
        myNewPic = encodedString;
        iv_addPic.setImageBitmap(b);
//
        /*if (picString.isEmpty()) {
            rl_continue.setBackground(getResources().getDrawable(R.drawable.round_corner_rect_filled_secondry));
        } else {
            rl_continue.setBackground(getResources().getDrawable(R.drawable.round_corner_rect_filled_primary));
        }*/
    }

    public String setName() {
        name = String.valueOf(et_name.getText());
        return name;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        String picturePath = "", filename = "", encodedString = "";
        Bitmap bitmap;
        Matrix matrix;
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                picturePath = fileUri.getPath();
                filename = picturePath.substring(picturePath.lastIndexOf("/") + 1);
                Log.v("MYImgPath", filename);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(picturePath, options);
                //BitmapFactory.decodeFile(picturePath, options);
                final int REQUIRED_SIZE = 500;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(picturePath, options);
                matrix = new Matrix();
                matrix.postRotate(getImageOrientation(picturePath));
                if (bitmap != null) {
                    Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                    byte[] ba = bao.toByteArray();
                    encodedString = getEncoded64ImageStringFromBitmap(bitmap);
                    Log.v("encodedstring", encodedString);
                    setPictures(bitmap, picturePath, matrix, encodedString);
                }
            }
        } else if (requestCode == SELECT_PICTURE) {
            if (data != null) {
                Uri contentURI = data.getData();
                //get the Uri for the captured image
                fileUri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(contentURI, filePathColumn, null, null, null);
                cursor.moveToFirst();
                Log.v("piccc", "pic");
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                System.out.println("Image Path : " + picturePath);
                cursor.close();
                filename = picturePath.substring(picturePath.lastIndexOf("/") + 1);
                String selectedImagePath = picturePath;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 500;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(selectedImagePath, options);

                matrix = new Matrix();
                matrix.postRotate(getImageOrientation(picturePath));
                if (bitmap != null) {
                    Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                    byte[] ba = bao.toByteArray();
                    encodedString = getEncoded64ImageStringFromBitmap(bitmap);
                    Log.v("encodedstring", encodedString);
                    Log.v("picture_path====", filename);
                    setPictures(bitmap, picturePath, matrix, encodedString);
                }
            } else {
                showToast(SecondActivity.this, "unable to select image");
            }

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
            ActivityCompat.requestPermissions(SecondActivity.this, listPermissionsNeedeFor.toArray(new String[listPermissionsNeedeFor.size()]), permission_granted_code);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*if (requestCode == 20 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePictureFromCamera();
        } else
            Toast.makeText(SecondActivity.this, "Permission not granted to access the camera", Toast.LENGTH_SHORT).show();*/

        if (requestCode == permission_granted_code) {
            HashMap<String, Integer> permisionResults = new HashMap<>();
            int deniedPermissionCount = 0;

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permisionResults.put(permissions[i], grantResults[i]);
                    deniedPermissionCount++;
                }
            }
            /*if (deniedPermissionCount == 0) {
                setInitals();
            }
            else {*/
            if (deniedPermissionCount != 0) {
                for (Map.Entry<String, Integer> entry : permisionResults.entrySet()) {
                    String permName = entry.getKey();
                    int permResult = entry.getValue();
                    setLog("MyPermissions", "denied  " + permName);
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SecondActivity.this, permName)) {
                        showAlertDialog(SecondActivity.this, "", "Do allow or permission to make application work fine"
                                , "Yes, Grant Permission", (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                    checkAndRequestPermissions();
                                }, "No, Exit app", (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                    finish();

                                }, false);
                    } else {
                        showAlertDialog(SecondActivity.this, "", "You have denied some permissons. Allow all permissions at [Settings] > [Permissions]", "Go to settings"
                                , (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }, "No, Exit app", (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                    finish();

                                }, false);
                        break;

                    }
                }
            }
        }
    }
}




















