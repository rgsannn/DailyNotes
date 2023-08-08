package com.rgsannn.dailynotes.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.rgsannn.dailynotes.R;
import com.rgsannn.dailynotes.model.NotesModel;
import com.rgsannn.dailynotes.view.fragment.DetailNotesFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 10120076
 * Rifqi Galih Nur Ikhsan
 * IF-2
 */

public class NoteItemsAdapter extends RecyclerView.Adapter<NoteItemsAdapter.NoteViewHolder> {
    private List<NotesModel> notesModelList;
    private FragmentActivity fragmentActivity;

    public NoteItemsAdapter(List<NotesModel> notesModelList, FragmentActivity fragmentActivity) {
        this.notesModelList = notesModelList;
        this.fragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NotesModel notesModel = notesModelList.get(position);
        holder.bind(notesModel);
    }

    @Override
    public int getItemCount() {
        return notesModelList.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView idCatatanTextView, titleTextView, catatanTextView, dateTextView;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            idCatatanTextView = itemView.findViewById(R.id.idCatatanTextView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            catatanTextView = itemView.findViewById(R.id.catatanTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            itemView.setOnClickListener(this);
        }

        void bind(NotesModel notesModel) {
            idCatatanTextView.setText(notesModel.getIdCatatan());
            catatanTextView.setText(notesModel.getCatatan());
            titleTextView.setText(notesModel.getJudul());

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

            try {
                Date tanggal = inputFormat.parse(notesModel.getTanggal());
                String tanggalHanya = outputFormat.format(tanggal);
                dateTextView.setText(tanggalHanya);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                NotesModel notesModel = notesModelList.get(position);
                String idCatatan = notesModel.getIdCatatan();
                navigateToDetailsNotesFragment(idCatatan);
            }
        }
    }

    private void navigateToDetailsNotesFragment(String idCatatan) {
        DetailNotesFragment fragmentDetailsNotes = DetailNotesFragment.newInstance(idCatatan);

        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragmentDetailsNotes);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
