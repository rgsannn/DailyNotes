package com.rgsannn.dailynotes.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.rgsannn.dailynotes.R;
import com.rgsannn.dailynotes.model.NotesModel;
import com.rgsannn.dailynotes.presenter.NotesPresenter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 10120076
 * Rifqi Galih Nur Ikhsan
 * IF-2
 */

public class AddNotesFragment extends Fragment implements NotesPresenter.View {
    private EditText judulEditText;
    private EditText kategoriEditText;
    private EditText catatanEditText;
    private Button tambahCatatanButton;
    private NotesPresenter notePresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notePresenter = new NotesPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_daily_notes, container, false);
        ImageView backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        judulEditText = view.findViewById(R.id.judulEditText);
        kategoriEditText = view.findViewById(R.id.kategoriEditText);
        catatanEditText = view.findViewById(R.id.catatanEditText);
        tambahCatatanButton = view.findViewById(R.id.tambahCatatanButton);
        tambahCatatanButton.setOnClickListener(v -> tambahCatatan());

        return view;
    }

    private void tambahCatatan() {
        String id_catatan = notePresenter.generateIdCatatan();
        String judul = judulEditText.getText().toString();
        String kategori = kategoriEditText.getText().toString();
        String catatan = catatanEditText.getText().toString();
        Date tanggal = new Date();

        if (judul.isEmpty() || kategori.isEmpty() || catatan.isEmpty()) {
            Toast.makeText(getContext(), "Harap lengkapi semua data", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(tanggal);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String formattedTime = timeFormat.format(tanggal);

        NotesModel notesModel = new NotesModel(id_catatan, judul, kategori, catatan, formattedDate, formattedTime);
        notePresenter.addNote(notesModel);

        Toast.makeText(getContext(), "Catatan berhasil ditambahkan", Toast.LENGTH_SHORT).show();
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showNotes(List<NotesModel> notesModels) {
        // This method is not needed in AddNotesFragment, you can leave it empty or add a log message.
    }

    @Override
    public void showNoteDetail(NotesModel notesModel) {
        // This method is not needed in AddNotesFragment, you can leave it empty or add a log message.
    }

    @Override
    public void showNoteNotFound() {
        // This method is not needed in AddNotesFragment, you can leave it empty or add a log message.
    }
}
