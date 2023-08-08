package com.rgsannn.dailynotes.model;

/**
 * 10120076
 * Rifqi Galih Nur Ikhsan
 * IF-2
 */

public class NotesModel {
    private String idCatatan;
    private String judul;
    private String kategori;
    private String catatan;
    private String tanggal;
    private String waktu;

    public NotesModel() {
    }

    public NotesModel(String idCatatan, String judul, String kategori, String catatan, String tanggal, String waktu) {
        this.idCatatan = idCatatan;
        this.judul = judul;
        this.kategori = kategori;
        this.catatan = catatan;
        this.tanggal = tanggal;
        this.waktu = waktu;
    }

    public String getIdCatatan() {
        return idCatatan;
    }

    public void setIdCatatan(String idCatatan) {
        this.idCatatan = idCatatan;
    }

    public String getJudul() {
        return judul;
    }

    public String getKategori() {
        return kategori;
    }

    public String getCatatan() {
        return catatan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getWaktu() {
        return waktu;
    }
}




