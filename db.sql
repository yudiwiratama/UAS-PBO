CREATE DATABASE spp_payment;
USE spp_payment;

CREATE TABLE pembayaran (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_siswa INT NOT NULL,
    nama_siswa VARCHAR(100) NOT NULL,
    kelas VARCHAR(50) NOT NULL,
    jurusan VARCHAR(50) NOT NULL,
    metode_pembayaran VARCHAR(50) NOT NULL,
    jumlah DECIMAL(10, 2) NOT NULL
);
