package com.hazeltechnology.messageexporter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hazeltechnology.messageexporter.databinding.ExportFormatListItemBinding;

import java.util.ArrayList;

/**
 * Created by Parham on 8/09/2016.
 */

public class ExportTypeDialogRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ExportTypeObject> exportTypeObjects;
    private ExportTypeSelectionCallback callback;

    public ExportTypeDialogRecycleViewAdapter(Context context, ArrayList<ExportTypeObject> exportTypeObjects, ExportTypeSelectionCallback callback) {
        this.context = context;
        this.exportTypeObjects = exportTypeObjects;
        this.callback = callback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExportFormatViewHolder(LayoutInflater.from(context).inflate(R.layout.export_format_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ExportFormatViewHolder exportFormatViewHolder = (ExportFormatViewHolder) holder;

        exportFormatViewHolder.setExportFormat(exportTypeObjects.get(position));

        exportFormatViewHolder.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelectedItem(exportFormatViewHolder.getAdapterPosition());
                callback.exportFormatSelected(exportTypeObjects.get(exportFormatViewHolder.getAdapterPosition()), exportFormatViewHolder.getAdapterPosition());
            }
        });
    }

    private void changeSelectedItem(int newSelectedPosition) {
        for (int i = 0; i < exportTypeObjects.size(); i++) {
            if (i == newSelectedPosition) {
                exportTypeObjects.get(i).setArraySelected(true);
            } else {
                exportTypeObjects.get(i).setArraySelected(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return exportTypeObjects.size();
    }

    private class ExportFormatViewHolder extends RecyclerView.ViewHolder {

        private ExportFormatListItemBinding binding;

        public ExportFormatViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void setExportFormat(ExportTypeObject exportFormat) {
            binding.setExportObject(exportFormat);
        }

        public View getRootView() {
            return binding.getRoot();
        }
    }

    interface ExportTypeSelectionCallback {
        void exportFormatSelected(ExportTypeObject exportTypeObject, int position);
    }


}
