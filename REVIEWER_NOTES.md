# MyQuranApp – Ringkasan Proyek untuk Reviewer

MyQuranApp adalah aplikasi Android yang menampilkan daftar surat Al-Qur’an, detail tiap surat, serta fitur Favorit dan Pencarian. Proyek ini dibuat dengan Kotlin dan menerapkan arsitektur modern Android, termasuk Clean Architecture, modularisasi, dan dynamic feature modules.

## 1. Struktur Modul

Proyek dibagi menjadi beberapa modul agar lebih rapi, terpisah, dan mudah dikembangkan:

- `:app`  
  Modul utama yang berisi:
  - `MainActivity` sebagai titik masuk aplikasi
  - `HomeFragment` untuk daftar surat
  - `DetailActivity` untuk menampilkan detail surat dan ayat
  - `SettingsActivity` dan `AboutActivity` untuk halaman pengaturan dan informasi aplikasi

- `:core`  
  Modul yang menampung logika inti aplikasi:
  - Domain layer (model domain, use case, dan interface repository)
  - Data layer (implementasi repository, API Retrofit, dan database Room)
  - Utility seperti mapper, wrapper `Resource`, dan extension function

- `:favorite` (dynamic feature)  
  Modul fitur Favorit:
  - Menyediakan `FavoriteFragment` yang menampilkan daftar surat favorit
  - Mengambil data favorit dari database lokal melalui modul `core`

- `:search` (dynamic feature)  
  Modul fitur Pencarian:
  - Berisi `SearchActivity` dan `SearchViewModel`
  - Mendukung pencarian biasa dan pencarian fuzzy
  - Menyimpan riwayat pencarian dalam bentuk chip di layar

## 2. Arsitektur dan Pola yang Digunakan

Aplikasi ini mengikuti Clean Architecture dan pola MVVM untuk memisahkan tanggung jawab setiap layer dengan jelas.

- Presentation layer (di `:app`, `:favorite`, `:search`)
  - Activity, Fragment, dan ViewModel
  - Menggunakan ViewBinding untuk mengakses view
  - Mengamati data dari ViewModel melalui LiveData atau Flow

- Domain layer (di `:core/domain`)
  - Use case seperti `QuranUseCase` / `QuranInteractor`
  - Interface repository `IQuranRepository`
  - Model domain seperti `Surah`, `SurahDetail`, dan `Ayah`
  - Tidak bergantung pada Android framework

- Data layer (di `:core/data`)
  - Implementasi repository `QuranRepository`
  - `RemoteDataSource` dengan Retrofit `ApiService`
  - `LocalDataSource` dengan Room (`QuranDatabase`, `SurahDao`)
  - `DataMapper` untuk mengubah model dari data layer ke domain layer

Pendekatan ini dipilih agar:
- Logika bisnis tidak bercampur dengan kode tampilan (UI)
- Kode lebih mudah diuji
- Struktur proyek tetap terjaga saat aplikasi berkembang

## 3. Teknologi yang Digunakan

Beberapa teknologi utama yang digunakan di MyQuranApp:

- Bahasa: Kotlin  
- Asynchronous & Reactive:
  - Kotlin Coroutines
  - Kotlin Flow
- AndroidX:
  - ViewModel, LiveData, dan Lifecycle
  - RecyclerView
  - ConstraintLayout
  - SwipeRefreshLayout
- Dependency Injection:
  - Koin, dengan modul DI yang terpisah untuk core dan masing-masing fitur
- Networking:
  - Retrofit + Gson
  - OkHttp + Logging Interceptor (aktif hanya di build debug)
- Database:
  - Room (entity, DAO, dan database)
- UI:
  - ViewBinding di seluruh layar
  - Shimmer untuk loading state
  - Glide untuk memuat gambar

## 4. Fitur Utama

Secara garis besar, fitur-fitur yang sudah ada di MyQuranApp adalah:

1. **Daftar Surat (Home)**
   - Menampilkan daftar surat dari API
   - Mendukung pull-to-refresh
   - Menampilkan loading state dengan efek shimmer
   - Ketika satu surat diketuk, pengguna diarahkan ke halaman detail

2. **Detail Surat**
   - Menampilkan ayat-ayat dalam surat tersebut
   - Menampilkan teks Arab dan terjemahan
   - Terintegrasi dengan fitur favorit untuk menandai surat sebagai favorit

3. **Favorit (Dynamic Feature)**
   - Menampilkan daftar surat yang ditandai sebagai favorit
   - Data disimpan di database lokal (Room)
   - Saat item favorit ditekan, akan membuka `DetailActivity`
   - Modul ini berjalan sebagai dynamic feature, sehingga tidak dibundel penuh di base APK

4. **Pencarian (Dynamic Feature)**
   - Menyediakan halaman untuk mencari surat
   - Mendukung mode pencarian biasa dan pencarian fuzzy
   - Menyimpan riwayat pencarian dalam bentuk chip agar mudah diakses kembali

5. **Settings dan About**
   - Halaman pengaturan untuk konfigurasi aplikasi (dipersiapkan untuk preferensi)
   - Halaman tentang aplikasi untuk informasi singkat mengenai MyQuranApp

## 5. Alur Data Secara Singkat

Berikut gambaran sederhana alur data, misalnya saat menampilkan daftar surat:

1. Pengguna membuka halaman atau melakukan aksi refresh.
2. Fragment memanggil fungsi di ViewModel.
3. ViewModel memanggil use case di domain layer.
4. Use case memanggil `IQuranRepository`.
5. Repository menentukan sumber data:
   - Mengambil dari database lokal (Room) jika data sudah tersedia.
   - Mengambil dari API (RemoteDataSource dengan Retrofit) jika data baru dibutuhkan.
6. Data dari data layer diubah menjadi model domain menggunakan `DataMapper`.
7. Hasil dibungkus dalam `Resource<T>` (Loading, Success, atau Error).
8. ViewModel mengekspos data ini ke UI.
9. Fragment/Activity mengamati dan menampilkan tampilan sesuai state (loading, error, atau data).

## 6. Konfigurasi dan Keamanan

Beberapa poin terkait konfigurasi dan keamanan:

- Nilai `BASE_URL` untuk API tidak ditulis langsung di source code:
  - Disimpan di `local.properties`
  - Dimasukkan ke `BuildConfig.BASE_URL` melalui pengaturan di `build.gradle.kts`
- Logging interceptor hanya diaktifkan pada build `debug`.
- Izin `POST_NOTIFICATIONS` dan logika permintaan izin notifikasi sudah dihapus, karena aplikasi ini tidak menampilkan notifikasi sistem.

## 7. Praktik Baik yang Diterapkan

Beberapa praktik yang dijaga konsisten di proyek ini:

- Tidak menggunakan operator `!!`
  - Menggunakan safe call (`?.`) dan pengecekan null yang eksplisit
- Tidak ada hardcoded string di layout atau kode
  - Teks disimpan di `strings.xml`
- Menggunakan ConstraintLayout untuk menghindari nested layout yang dalam
- Menggunakan ViewBinding di seluruh layar untuk membuat akses view lebih aman dan bersih
- Memanfaatkan extension function untuk kode yang sering dipakai berulang
- Memisahkan model di tiap layer (network/dto, entity database, dan domain)
- Menggunakan wrapper `Resource` untuk menangani state loading, sukses, dan error di UI

## 8. Dynamic Feature Modules

Modul `:favorite` dan `:search` dibuat sebagai dynamic feature dengan tujuan:

- Menunjukkan pemahaman tentang modularisasi dan Play Feature Delivery
- Mengurangi ukuran base APK
- Memisahkan fitur yang sifatnya tambahan dari modul utama

Base module (`:app`) mendeklarasikan dynamic feature ini dan memuatnya saat dibutuhkan, misalnya ketika pengguna membuka halaman Favorit atau Pencarian.
