package com.bankmas.report.servicecsv.dto;

public class FileData {

    private String wilayah;
    private String tanggal;
    private String gambar;

    

    public String getWilayah() {
        return wilayah;
    }

    public void setWilayah(String wilayah) {
        this.wilayah = wilayah;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public FileData() {
    }

    public FileData(String wilayah, String tanggal, String gambar) {
        this.wilayah = wilayah;
        this.tanggal = tanggal;
        this.gambar = gambar;
    }

    
    

}
