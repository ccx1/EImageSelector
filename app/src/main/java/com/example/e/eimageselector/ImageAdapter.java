package com.example.e.eimageselector;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;



public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int mRotate;
    private Context mContext;
    private List<Photo> data;
    private Bitmap mBitmap;

    public ImageAdapter(Context context, List<Photo> photoList, int rotate) {
        this.data = photoList;
        this.mContext = context;
        this.mRotate = rotate;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new RecyclerView.ViewHolder( LayoutInflater.from(mContext).inflate(R.layout.view_item,null,false)) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Photo     photo    = data.get(position);
        WindowManager   wm       = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int             width    = wm.getDefaultDisplay().getWidth();
        final ImageView viewById = (ImageView) holder.itemView.findViewById(R.id.imageview);
        viewById.setLayoutParams(new LinearLayout.LayoutParams(width / 3,width/3));
//        final RotateCenterCrop rotateCenterCrop = new RotateCenterCrop(mContext, mRotate);
//        rotateCenterCrop.setWidthAndHeight(width / 3,width / 3);
        Glide.with(mContext).load(photo.imageFilePath).centerCrop().into(viewById);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View      view   = LayoutInflater.from(mContext).inflate(R.layout.view_dialog, null);
                PhotoView pv     = view.findViewById(R.id.pv);
                if (mBitmap != null) {
                    mBitmap.recycle();
                    mBitmap = null;
                }
                mBitmap = BitmapFactory.decodeFile(photo.imageFilePath);
                pv.setImageBitmap(mBitmap);
                Dialog dialog = new Dialog(mContext, R.style.MyDialog);
                dialog.setContentView(view);
                dialog.show();
            }
        });

        viewById.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnLongClick != null) {
                    mOnLongClick.longClick(photo);
                    return true;
                }
                return false;
            }
        });
    }

    private onLongClick mOnLongClick;


    public void setOnLongClick(onLongClick onLongClick) {
        mOnLongClick = onLongClick;
    }

    public interface onLongClick{
        void longClick(Photo photo);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
