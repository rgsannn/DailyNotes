package com.rgsannn.dailynotes.view.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rgsannn.dailynotes.model.CategoryWithNotes;
import com.rgsannn.dailynotes.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rgsannn.dailynotes.model.NotesModel;
import com.rgsannn.dailynotes.presenter.NotesPresenter;
import com.rgsannn.dailynotes.view.adapter.NotesAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 10120076
 * Rifqi Galih Nur Ikhsan
 * IF-2
 */

public class NotesFragment extends Fragment implements NotesPresenter.View, View.OnClickListener {
    private RecyclerView recyclerView;
    private NotesAdapter noteAdapter;
    private NotesPresenter notePresenter;
    private List<NotesModel> notesModelList;
    private TextView dateTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notePresenter = new NotesPresenter(this);
        notesModelList = new ArrayList<>();
        noteAdapter = new NotesAdapter(new ArrayList<>(), requireActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_notes, container, false);
        initializeViews(view);
        setListeners(view);
        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.kategoriview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(noteAdapter);

        FloatingActionButton fab = view.findViewById(R.id.addNav_button);
        dateTextView = view.findViewById(R.id.dateTextView);
    }

    private void setListeners(View view) {
        FloatingActionButton fab = view.findViewById(R.id.addNav_button);
        fab.setOnClickListener(this);

        LinearLayout dateLayout = view.findViewById(R.id.dateLayout);
        dateLayout.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Start listening for real-time updates from the database
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        DatabaseReference notesRef = FirebaseDatabase.getInstance().getReference("daily_notes").child(userId);
        notesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // A new note is added
                NotesModel newNote = dataSnapshot.getValue(NotesModel.class);
                updateNotesList(newNote);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // An existing note is updated
                NotesModel updatedNote = dataSnapshot.getValue(NotesModel.class);
                updateNotesList(updatedNote);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // A note is deleted
                String deletedNoteId = dataSnapshot.getKey();
                removeNoteFromList(deletedNoteId);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Not used in this case
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error, if needed
            }
        });

        // Load initial data
        notePresenter.loadNotes();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addNav_button:
                navigateToFragmentAddDailyNotes();
                break;
            case R.id.dateLayout:
                showDatePicker();
                break;
        }
    }

    @Override
    public void showNotes(List<NotesModel> notesModels) {
        // The `notesModels` list contains all notesModels, and we need to group them by category.
        Map<String, List<NotesModel>> notesByCategory = new HashMap<>();

        for (NotesModel notesModel : notesModels) {
            String category = notesModel.getKategori();
            if (!notesByCategory.containsKey(category)) {
                notesByCategory.put(category, new ArrayList<>());
            }
            notesByCategory.get(category).add(notesModel);
        }

        List<CategoryWithNotes> categoriesWithNotes = new ArrayList<>();

        for (String category : notesByCategory.keySet()) {
            List<NotesModel> notesForCategory = notesByCategory.get(category);
            categoriesWithNotes.add(new CategoryWithNotes(category, notesForCategory));
        }

        noteAdapter = new NotesAdapter(categoriesWithNotes, requireActivity());
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    public void showNoteDetail(NotesModel notesModel) {

    }

    @Override
    public void showNoteNotFound() {

    }

    private void updateNotesList(NotesModel notesModel) {
        // Check if the note already exists in the list
        int index = -1;
        for (int i = 0; i < notesModelList.size(); i++) {
            if (notesModelList.get(i).getIdCatatan().equals(notesModel.getIdCatatan())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            // Update the existing note
            notesModelList.set(index, notesModel);
        } else {
            // Add the new note to the list
            notesModelList.add(notesModel);
        }

        // Refresh the UI with the updated list
        showNotes(notesModelList);
    }

    private void removeNoteFromList(String noteId) {
        // Find the note in the list and remove it
        for (int i = 0; i < notesModelList.size(); i++) {
            if (notesModelList.get(i).getIdCatatan().equals(noteId)) {
                notesModelList.remove(i);
                break;
            }
        }

        // Refresh the UI with the updated list
        showNotes(notesModelList);
    }

    private void navigateToFragmentAddDailyNotes() {
        AddNotesFragment fragmentAddDailyNotes = new AddNotesFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragmentAddDailyNotes);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDayOfMonth);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String selectedDate = dateFormat.format(calendar.getTime());
                    dateTextView.setText(selectedDate);
                    notePresenter.loadNotesByDate(selectedDate);
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }
}