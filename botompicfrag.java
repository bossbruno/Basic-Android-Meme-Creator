package com.example.doubletap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.app.Activity.RESULT_OK;

public class botompicfrag extends Fragment {
    public static TextView stattop;
    public static TextView statbottom;
    public Button saveimage;
    public Button chooseimage;
    private ImageView memeimage;
    private static final int PICK_IMAGE = 1;
    float[] lastEvent = null;
    float d = 0f;
    float newRot = 0f;
    private boolean isZoomAndRotate;
    private boolean isOutSide;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private PointF start = new PointF();
    private PointF mid = new PointF();
    float oldDist = 1f;
    private float xCoOrdinate, yCoOrdinate;

    @SuppressLint("ClickableViewAccessibility")


            Uri imageurl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.bottomragmentpic, container, false);
        stattop = view.findViewById(R.id.textView2);
        saveimage = view.findViewById(R.id.button2);
        chooseimage = view.findViewById(R.id.button3);
        statbottom = view.findViewById(R.id.textView4);
        memeimage = view.findViewById(R.id.imageView2);
        saveimage = view.findViewById(R.id.button2);
        final RelativeLayout savingLayout =view.findViewById(R.id.laay) ;


        //Contains methods to run when save image button is clicked
        saveimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
saveimage.setVisibility(View.GONE);
               chooseimage.setVisibility(View.GONE);
              // saveToGallery(getActivity(),savingLayout);

                try {
                    saveImage(getBitmp(savingLayout),stattop.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //  getBitmp(savingLayout);


                chooseimage.setVisibility(View.VISIBLE);

            }
        });



//What happens when you click the choose image buttton
        chooseimage.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                saveimage.setVisibility(View.VISIBLE);
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Select Image"), PICK_IMAGE);
//code to move the text to any position

                stattop.setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {

                        TextView view = (TextView) v;
                        view.bringToFront();
                        viewTransformation(view, event);
                        return true;
                    }
                });

                statbottom.setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {

                        TextView view = (TextView) v;
                        view.bringToFront();
                        viewTransformation(view, event);
                        return true;
                    }
                });
            }
//methods that move components

            private void viewTransformation(View view, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        xCoOrdinate = view.getX() - event.getRawX();
                        yCoOrdinate = view.getY() - event.getRawY();

                        start.set(event.getX(), event.getY());
                        isOutSide = false;
                        mode = DRAG;
                        lastEvent = null;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            midPoint(mid, event);
                            mode = ZOOM;
                        }

                        lastEvent = new float[4];
                        lastEvent[0] = event.getX(0);
                        lastEvent[1] = event.getX(1);
                        lastEvent[2] = event.getY(0);
                        lastEvent[3] = event.getY(1);
                        d = rotation(event);
                        break;
                    case MotionEvent.ACTION_UP:
                        isZoomAndRotate = false;
                        if (mode == DRAG) {
                            float x = event.getX();
                            float y = event.getY();
                        }
                    case MotionEvent.ACTION_OUTSIDE:
                        isOutSide = true;
                        mode = NONE;
                        lastEvent = null;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        lastEvent = null;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!isOutSide) {
                            if (mode == DRAG) {
                                isZoomAndRotate = false;
                                view.animate().x(event.getRawX() + xCoOrdinate).y(event.getRawY() + yCoOrdinate).setDuration(0).start();
                            }
                            if (mode == ZOOM && event.getPointerCount() == 2) {
                                float newDist1 = spacing(event);
                                if (newDist1 > 10f) {
                                    float scale = newDist1 / oldDist * view.getScaleX();
                                    view.setScaleX(scale);
                                    view.setScaleY(scale);
                                }
                                if (lastEvent != null) {
                                    newRot = rotation(event);
                                    view.setRotation((float) (view.getRotation() + (newRot - d)));
                                }
                            }
                        }
                        break;
                }
            }

            private float rotation(MotionEvent event) {
                double delta_x = (event.getX(0) - event.getX(1));
                double delta_y = (event.getY(0) - event.getY(1));
                double radians = Math.atan2(delta_y, delta_x);
                return (float) Math.toDegrees(radians);
            }

            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return (int) Math.sqrt(x * x + y * y);
            }

            private void midPoint(PointF point, MotionEvent event) {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }


        });
        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            assert data != null;
            imageurl = data.getData();


            try {
                memeimage.setImageURI(imageurl);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//passes string from one fragmentt to the other
    public void setmeme(String Tops, String Bottoms) {
        stattop.setText(Tops);
        statbottom.setText(Bottoms);
    }

   public void scanGallery(Context cntx, String path)

   {
       try{
           MediaScannerConnection.scanFile(cntx, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
               @Override
               public void onScanCompleted(String path, Uri uri) {
                      }

           });
       }catch ( Exception e)
       {
           e.printStackTrace();
           Log.i("TAG", "There was a scanning issue ");
       }
   }
   public Bitmap getBitmp (View y) {

       ContentResolver resolver = new ContentResolver(getContext()) {
           @Nullable
           @Override
           public String[] getStreamTypes(@NonNull Uri url, @NonNull String mimeTypeFilter) {
               return super.getStreamTypes(imageurl, mimeTypeFilter);
           }

       };

        Bitmap btm = Bitmap.createBitmap(y.getWidth(),y.getHeight(),Bitmap.Config.ARGB_8888);
       Canvas cnv = new Canvas(btm);
        y.draw(cnv);
      /* MediaStore.Images.Media.insertImage(resolver, btm, "Me", "Okay");
       scanGallery(getActivity(), "DoubleTap");*/
       Toast.makeText(getActivity(), "Image Saved", Toast.LENGTH_SHORT).show();
       return btm;
   }




    private void saveImage(Bitmap bitmap, @NonNull String name) throws IOException {
        boolean saved;
        OutputStream fos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getContext().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "DoubleTap");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + File.separator + "DoubleTap";

            File file = new File(imagesDir);

            if (!file.exists()) {
                file.mkdir();
            }

            File image = new File(imagesDir, name + ".png");
            fos = new FileOutputStream(image);

        }
        try {
            saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(getActivity(),"Image Saved" , Toast.LENGTH_SHORT).show();

        }
        catch (Exception e)
        {
            Toast.makeText(getActivity(),"Image Download Failed" + e.toString() , Toast.LENGTH_SHORT).show();

        }

    }



}








