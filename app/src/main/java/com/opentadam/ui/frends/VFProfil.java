/*
 * Copyright (C) 2019 TadamGroup, LLC.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.opentadam.ui.frends;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.opentadam.App;
import com.opentadam.Constants;
import com.opentadam.Injector;
import com.opentadam.R;
import com.opentadam.bus.BusEnabledStorageFotoPermission;
import com.opentadam.bus.BusInitFotoLeftMenu;
import com.opentadam.data.DialogClient;
import com.opentadam.data.IResponseDialog;
import com.opentadam.menu.UtilitesMenu;
import com.opentadam.ui.BaseFr;
import com.opentadam.utils.TintIcons;
import com.opentadam.utils.Utilites;
import com.squareup.otto.Subscribe;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.InjectView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class VFProfil extends BaseFr implements IResponseDialog {

    private static final int CAMERA_CAPTURE = 3;
    private static final String JPEG_FILE_PREFIX = "avatar_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private final int IMG_GALLERY = 1;
    @InjectView(R.id.profil_phone)
    TextView profilPhone;
    @InjectView(R.id.profil_name)
    TextView profilName;
    @InjectView(R.id.profil_mail)
    TextView profilMail;
    @InjectView(R.id.profil_pfoto)
    com.pkmmte.view.CircularImageView profilPfoto;
    @InjectView(R.id.profil_pfoto_dis)
    ImageView profilPfotoDis;
    @InjectView(R.id.fpc_add_foto_view)
    View fpcAddFotoView;
    @InjectView(R.id.fpc_view)
    View fpcView;

    public static Fragment newInstance() {
        return new VFProfil().withViewId(R.layout.f_profil_client);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();

    }

    @OnClick({R.id.fpc_profil_name, R.id.fpc_profil_mail})
    public void showFSetNameAndMail(View v) {
        switch (v.getId()) {
            case R.id.fpc_profil_name:
                getAWork().showFSetNameAndMail(Constants.TYPE_NAME);
                break;
            case R.id.fpc_profil_mail:
                getAWork().showFSetNameAndMail(Constants.TYPE_MAIL);
                break;
        }


    }

    private void initUI() {

        try {
            showFoto(UtilitesMenu.loadBitmap(getAWork()));
        } catch (Exception e) {
            Log.e("err", "e.getMessage() : " + e.getMessage());
        }


        profilPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        String phone = Injector.getSettingsStore().readString(Constants.REG_PHONE_CLIENT, null);
        if (phone == null) {
            profilPhone.setHint(R.string.fpc_hint_phone);
        } else {
            setStuleReg(phone, profilPhone);
        }
        String name = Injector.getSettingsStore().readString(Constants.PROFIL_NAME, null);
        if (name != null) {
            setStuleReg(name, profilName);

        }

        String mail = Injector.getSettingsStore().readString(Constants.REG_USER_MAIL, null);
        if (mail != null) {
            setStuleReg(mail, profilMail);
        }

        if (Injector.getClientData().isRecreateLocale) {
            getAWork().showWorkCont();
            getAWork().showBody();
            Injector.getClientData().isRecreateLocale = false;
            fpcView.setVisibility(View.VISIBLE);

        }
    }

    private void setStuleReg(String text, TextView textView) {
        TintIcons.setStuleReg(text, textView, "fonts/Roboto/Roboto-Bold.ttf", 21);
    }

    @OnClick({R.id.profil_photo_and_name, R.id.profil_phone})
    public void onClickProfile(View v) {
        switch (v.getId()) {
            case R.id.profil_photo_and_name:
                requestPermissionsStorage();
                //   DialogClient.showProfilPhotoDialog(this, loadBitmap() == null);

                break;
            case R.id.profil_phone:
                String phone = Injector.getSettingsStore().readString(Constants.REG_PHONE_CLIENT, "");
                if (!"".equals(phone)) {


                    DialogClient.showV2TwoButtonDialog(
                            getString(R.string.title_dialog_repl_phone)
                            , getString(R.string.message_dialog_repl_phone)
                            , getString(R.string.cancel_button_dialog_repl)
                            , getString(R.string.continue_button_dialog_repl)
                            , this);
                } else
                    getAWork().showV2FRegistration(true, false);
                break;

        }

    }
    @Subscribe
    public void onBusEnabledStorageFotoPermission(BusEnabledStorageFotoPermission e) {
        showDialifAddFoto();
    }

    private void requestPermissionsStorage() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityCompat.requestPermissions(getAWork(),
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    Constants.PERMISSION_REQUEST_FOTO);
        } else {
            showDialifAddFoto();
        }
    }

    private void showDialifAddFoto() {
        DialogClient.showProfilPhotoDialog(this, loadBitmap() == null);
    }

    @OnClick(R.id.haburg)
    public void onHaburg() {
        getAWork().showMenu();
    }

    private void callCameraApp() {
        Intent cameraAppIntent = new Intent();
        cameraAppIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            File photoFile = createImageFile();
            if (Uri.fromFile(photoFile) == null) {
                alert(getString(R.string.error_user_policy_private));
                return;
            }

            Injector.getClientData().mOutputFileUri = Uri.fromFile(photoFile);

            cameraAppIntent.putExtra(MediaStore.EXTRA_OUTPUT, Injector.getClientData().mOutputFileUri);
            startActivityForResult(cameraAppIntent, CAMERA_CAPTURE);
        } catch (IOException e) {
            e.printStackTrace();
            alert(getString(R.string.error_user_policy_private));
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        );

        return File.createTempFile(
                imageFileName,
                JPEG_FILE_SUFFIX,
                externalStoragePublicDirectory
        );
    }

    private void onClickFotoCam() {
        try {
            callCameraApp();
        } catch (ActivityNotFoundException e) {
            // Выводим сообщение об ошибке
            alert(getString(R.string.error_camera));
        }
    }

    private void onClickFotoGal() {
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);
        // мы будем обрабатывать возвращенное значение в onActivityResult
        startActivityForResult(
                Intent.createChooser(pickIntent, getResources().getString(R.string.set_image)),
                IMG_GALLERY);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IMG_GALLERY:
                    Uri pickedUri = data.getData();

                    CropImage.ActivityBuilder activity = CropImage.activity(pickedUri);
                    activity.start(getAWork(), this); // (DO NOT use `getActivity()`)
                    //    performCrop(pickedUri);
                    break;
                case CAMERA_CAPTURE:
                    // Получим Uri снимка
                    Uri picUri = Injector.getClientData().mOutputFileUri;
                    // кадрируем его
                    CropImage.activity(picUri)

                            .start(getAWork(), this);
                    //   performCrop(picUri);
                    break;
                // Вернулись из операции кадрирования
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:

                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri uri = result.getUri();

                    Bitmap resultBitmap = BitmapFactory.decodeFile(uri.getPath());

                    saveBitmap(resultBitmap);

                    showFoto(resultBitmap);
           /*         if (getAWork().getFLMenu() != null)
                        getAWork().getFLMenu().initFotoLeftMenu();*/

                    App.bus.post(new BusInitFotoLeftMenu());
                    Injector.getClientData().isShowProfile = true;

                    break;
            }
        }
    }

    private void showFoto(Bitmap resultBitmap) {
        if (resultBitmap == null)
            return;
        fpcAddFotoView.setVisibility(View.GONE);
        profilPfoto.setImageBitmap(resultBitmap);

        profilPfoto.setVisibility(View.VISIBLE);
        profilPfotoDis.setVisibility(View.GONE);
    }

    private void saveBitmap(Bitmap bitmap) {
        try {
            FileOutputStream out = new FileOutputStream(getAWork().getFilesDir().toString() + "MyAvatar");
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void addPfotoCamera(boolean b) {
        if (!b)
            onClickFotoGal();
        else
            onClickFotoCam();
    }

    private Bitmap loadBitmap() {
        return BitmapFactory.decodeFile(getAWork().getFilesDir().toString() + "MyAvatar");
    }

    @Override
    public void onDestroyView() {
        getAWork().hideWorkProgress();
        Utilites.hideSoftKeyboard(getAWork(), profilPfoto);
        super.onDestroyView();
    }

    @Override
    public boolean onBackPressed() {
        getAWork().showV3FRoute();
        return true;
    }

    @Override
    public Context getContectApp() {
        return null;
    }

    @Override
    public void responseAction(String name) {

    }
}
