package com.ucas.cloudenterprise.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import me.rosuh.filepicker.adapter.FileListAdapter;
import me.rosuh.filepicker.config.FileItemOnClickListener;
import me.rosuh.filepicker.config.FilePickerManager;

/**
 * @author simpler
 * @create 2020年07月30日  10:50
 */
public  class openfilepick {
    public  static void pickfile(Activity activity){
        FilePickerManager.INSTANCE
                .from(activity)
                .setItemClickListener(new FileItemOnClickListener() {
                   Context context =activity.getApplicationContext();
                    @Override
                    public void onItemClick(@NotNull FileListAdapter itemAdapter, @NotNull View itemView, int position) {
                        if (itemAdapter.getDataList() != null) {
                            Toast.makeText(context, itemAdapter.getDataList().get(position).getFileName(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onItemChildClick(@NotNull FileListAdapter itemAdapter, @NotNull View itemView, int position) {
                        if (itemAdapter.getDataList() != null) {
                            Toast.makeText(context, itemAdapter.getDataList().get(position).getFileName(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onItemLongClick(@NotNull FileListAdapter itemAdapter, @NotNull View itemView, int position) {
                        if (itemAdapter.getDataList() != null) {
                            Toast.makeText(context, itemAdapter.getDataList().get(position).getFileName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .enableSingleChoice()
                .forResult(101);
    }
}
