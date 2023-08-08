package com.rgsannn.dailynotes.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rgsannn.dailynotes.model.CategoryWithNotes;
import com.rgsannn.dailynotes.R;
import com.rgsannn.dailynotes.model.NotesModel;

import java.util.List;

/**
 * 10120076
 * Rifqi Galih Nur Ikhsan
 * IF-2
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<CategoryWithNotes> categoriesWithNotes;
    private FragmentActivity fragmentActivity;

    public NotesAdapter(List<CategoryWithNotes> categoriesWithNotes, FragmentActivity fragmentActivity) {
        this.categoriesWithNotes = categoriesWithNotes;
        this.fragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_kategori, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        CategoryWithNotes categoryWithNotes = categoriesWithNotes.get(position);
        holder.bind(categoryWithNotes);
    }

    @Override
    public int getItemCount() {
        return categoriesWithNotes.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView kategoriTextView;
        private RecyclerView recyclerView;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            kategoriTextView = itemView.findViewById(R.id.kategoriTextView);
            recyclerView = itemView.findViewById(R.id.recyclerview);
        }

        void bind(CategoryWithNotes categoryWithNotes) {
            String category = categoryWithNotes.getCategory();
            List<NotesModel> notesModels = categoryWithNotes.getNotes();

            kategoriTextView.setText(category);

            NoteItemsAdapter adapter = new NoteItemsAdapter(notesModels, fragmentActivity);
            recyclerView.setLayoutManager(new LinearLayoutManager(fragmentActivity));
            recyclerView.setAdapter(adapter);
        }
    }
}