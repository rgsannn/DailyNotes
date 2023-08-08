package com.rgsannn.dailynotes.view.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rgsannn.dailynotes.R;
import com.rgsannn.dailynotes.model.NotesModel;
import com.rgsannn.dailynotes.presenter.NotesPresenter;

import java.util.List;

/**
 * 10120076
 * Rifqi Galih Nur Ikhsan
 * IF-2
 */

public class DetailNotesFragment extends Fragment implements NotesPresenter.View {
    private TextView idCatatanTextView, judulHeaderTextView, tanggalTextView, kategoriTextView, catatanTextView;
    private NotesPresenter notePresenter;
    private DatabaseReference noteRef;
    private ValueEventListener noteValueEventListener;

    public static DetailNotesFragment newInstance(String idCatatan) {
        DetailNotesFragment fragment = new DetailNotesFragment();
        Bundle args = new Bundle();
        args.putString("idCatatan", idCatatan);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notePresenter = new NotesPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_notes, container, false);
        idCatatanTextView = view.findViewById(R.id.idCatatanTextView);
        judulHeaderTextView = view.findViewById(R.id.judulHeaderTextView);
        kategoriTextView = view.findViewById(R.id.kategoriTextView);
        catatanTextView = view.findViewById(R.id.catatanTextView);
        tanggalTextView = view.findViewById(R.id.tanggalTextView);

        ImageView backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        Bundle args = getArguments();
        if (args != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                String idCatatan = args.getString("idCatatan");
                idCatatanTextView.setText(idCatatan);
                noteRef = FirebaseDatabase.getInstance().getReference("daily_notes").child(userId).child(idCatatan);
                setupNoteValueEventListener();
            }
        }

        ImageView actionBar = view.findViewById(R.id.action_bar);
        actionBar.setOnClickListener(v -> showPopupMenu(v, idCatatanTextView.getText().toString()));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeNoteValueEventListener();
    }

    private void setupNoteValueEventListener() {
        noteValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NotesModel notesModel = dataSnapshot.getValue(NotesModel.class);
                if (notesModel != null) {
                    showNoteDetail(notesModel);
                } else {
                    showNoteNotFound();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error, if needed.
            }
        };

        noteRef.addValueEventListener(noteValueEventListener);
    }

    private void removeNoteValueEventListener() {
        if (noteRef != null && noteValueEventListener != null) {
            noteRef.removeEventListener(noteValueEventListener);
        }
    }

    private void showPopupMenu(View view, String idCatatan) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.inflate(R.menu.detail_notes_menu_toolbar);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.ubah_data:
                    navigateToUpdateDailyNotes(idCatatan);
                    return true;
                case R.id.hapus_data:
                    showDeleteConfirmationDialog(idCatatan);
                    return true;
                default:
                    return false;
            }
        });

        popupMenu.show();
    }

    private void navigateToUpdateDailyNotes(String idCatatan) {
        Bundle args = new Bundle();
        args.putString("idCatatan", idCatatan);
        UpdateNotesFragment fragmentUpdateDailyNotes = new UpdateNotesFragment();
        fragmentUpdateDailyNotes.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragmentUpdateDailyNotes)
                .addToBackStack(null)
                .commit();
    }

    private void showDeleteConfirmationDialog(String idCatatan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah Anda yakin ingin menghapus data ini?");
        builder.setPositiveButton("Ya", (dialog, which) -> {
            notePresenter.deleteNoteById(idCatatan);
            Toast.makeText(getContext(), "Catatan berhasil dihapus", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        builder.setNegativeButton("Tidak", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void showNotes(List<NotesModel> notesModels) {
    }

    @Override
    public void showNoteDetail(NotesModel notesModel) {
        getActivity().runOnUiThread(() -> {
            judulHeaderTextView.setText(notesModel.getJudul());
            kategoriTextView.setText(notesModel.getKategori());
            tanggalTextView.setText(notesModel.getTanggal() + " " + notesModel.getWaktu());
            catatanTextView.setText(notesModel.getCatatan());
        });
    }

    @Override
    public void showNoteNotFound() {
        getActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), "Catatan tidak ditemukan", Toast.LENGTH_SHORT).show();
        });
    }
}