package com.rgsannn.dailynotes.model;

import java.util.List;

/**
 * 10120076
 * Rifqi Galih Nur Ikhsan
 * IF-2
 */

public class CategoryWithNotes {
    private String category;
    private List<NotesModel> notesModels;

    public CategoryWithNotes(String category, List<NotesModel> notesModels) {
        this.category = category;
        this.notesModels = notesModels;
    }

    public String getCategory() {
        return category;
    }

    public List<NotesModel> getNotes() {
        return notesModels;
    }
}
