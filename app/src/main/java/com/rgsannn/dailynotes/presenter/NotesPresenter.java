package com.rgsannn.dailynotes.presenter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rgsannn.dailynotes.model.NotesModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 10120076
 * Rifqi Galih Nur Ikhsan
 * IF-2
 */

public class NotesPresenter {
    private View view;
    private DatabaseReference databaseReference;

    public NotesPresenter(View view) {
        this.view = view;
        // Reference to the base node where user-specific notes are stored
        databaseReference = FirebaseDatabase.getInstance().getReference("daily_notes");
    }

    // Load all notes for the authenticated user
    public void loadNotes() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userNotesRef = databaseReference.child(userId);
            userNotesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<NotesModel> notesModels = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        NotesModel notesModel = snapshot.getValue(NotesModel.class);
                        notesModels.add(notesModel);
                    }

                    // Log the data for debugging
                    Log.d("NotesPresenter", "Loaded " + notesModels.size() + " notes: " + notesModels);

                    view.showNotes(notesModels);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error, if needed.
                }
            });
        }
    }

    // Add a new note for the authenticated user
    public void addNote(NotesModel notesModel) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String idCatatan = generateIdCatatan();
            notesModel.setIdCatatan(idCatatan);
            DatabaseReference userNotesRef = databaseReference.child(userId).child(idCatatan);
            userNotesRef.setValue(notesModel);
        }
    }

    // Load a specific note by its ID
    public void getNoteById(String idCatatan) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            databaseReference.child(userId).child(idCatatan).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    NotesModel notesModel = dataSnapshot.getValue(NotesModel.class);
                    if (notesModel != null) {
                        view.showNoteDetail(notesModel);
                    } else {
                        view.showNoteNotFound();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error, if needed.
                }
            });
        }
    }

    // Update an existing note
    public void updateNoteById(String idCatatan, String judul, String kategori, String catatan) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference noteRef = databaseReference.child(userId).child(idCatatan);
            noteRef.child("judul").setValue(judul);
            noteRef.child("kategori").setValue(kategori);
            noteRef.child("catatan").setValue(catatan);
        }
    }

    // Load notes based on a specific date for the authenticated user
    public void loadNotesByDate(String tanggal) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userNotesRef = databaseReference.child(userId);

            Query query = userNotesRef.orderByChild("tanggal").equalTo(tanggal);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<NotesModel> notesModels = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        NotesModel notesModel = snapshot.getValue(NotesModel.class);
                        notesModels.add(notesModel);
                    }

                    view.showNotes(notesModels);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error, if needed.
                }
            });
        }
    }

    // Delete a note by its ID for the authenticated user
    public void deleteNoteById(String idCatatan) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference noteRef = databaseReference.child(userId).child(idCatatan);
            noteRef.removeValue();
        }
    }

    public interface View {
        void showNotes(List<NotesModel> notesModels);

        void showNoteDetail(NotesModel notesModel);

        void showNoteNotFound();
    }

    // Generates a random ID for a note
    public String generateIdCatatan() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(12);
        Random random = new Random();

        for (int i = 0; i < 16; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
