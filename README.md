
# Tugas Kecil 3 IF2211 Strategi Algoritma
> Penyelesaian Puzzle Rush Hour Menggunakan Algoritma Pathfinding

Rush Hour adalah sebuah permainan puzzle logika berbasis grid yang menantang pemain untuk menggeser kendaraan di dalam sebuah kotak (biasanya berukuran 6x6) agar mobil utama (biasanya berwarna merah) dapat keluar dari kemacetan melalui pintu keluar di sisi papan. Setiap kendaraan hanya bisa bergerak lurus ke depan atau ke belakang sesuai dengan orientasinya (horizontal atau vertikal), dan tidak dapat berputar.

Program Rush Hour solver dibuat dalam bahasa java berbasis GUI untuk menyelesaikan persoalan tersebut menggunakan algoritma pencarian seperti Greedy Best First Search (GBFS), UCS, A*, dan IDS. Program ini menerima input konfigurasi puzzle dari file dan memberikan solusi untuk menggeser kendaraan ke pintu keluar dengan opsi algoritma pencarian dan heuristik yang tersedia. Hasil divisualisasikan dengan bentuk animasi pada program dan dapat disimpan ke dalam file.

## Made by
Muhammad Aufa Farabi - 13523023

## Features
* Solver puzzle Rush Hour dengan pilihan algoritma berikut:
  - Greedy Best First Search (GBFS)
  - Uniform Cost Search (UCS)
  - A* Algorithm
  - Iterative Deepening Search (IDS)    
* Pilihan Heuristik untuk algoritm GBFS dan A*:
  - Distance to Exit
  - Min Blocking Pieces
  - Distance to Exit + Min Blocking Pieces
  - Min Moveable Blocking Pieces 
* GUI
* Solusi dalam bentuk animasi pergerakan pieces
* Replay animasi solusi
* Save solusi ke dalam file

## How to Run
1. Clone repository pada terminal
   ```sh
   git clone https://github.com/AgungLucker/Tucil3_13523023
   ```
2. Pindah ke direktori src untuk memulai program
    ```sh
    cd src
    ```
2. Compile program 
    ```sh
    javac *.java
    ```
3. Jalankan program dengan command berikut
    ```sh
    java Main.java 
    ```

## Links
- Project Homepage:
(https://github.com/AgungLucker/Tucil3_13523023)

