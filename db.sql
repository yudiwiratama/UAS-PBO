-- buat database
CREATE DATABASE ProjectSiswa;
USE ProjectSiswa;

-- buat table untuk data autentikasi login
CREATE TABLE admin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL
);

-- Contoh data admin
INSERT INTO admin (username, password) VALUES ('admin', 'admin123');



-- buat table untuk memuat data
CREATE TABLE pembayaran (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_siswa INT NOT NULL,
    nama_siswa VARCHAR(100) NOT NULL,
    kelas VARCHAR(50) NOT NULL,
    jurusan VARCHAR(50) NOT NULL,
    metode_pembayaran VARCHAR(50) NOT NULL,
    jumlah DECIMAL(10, 2) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

