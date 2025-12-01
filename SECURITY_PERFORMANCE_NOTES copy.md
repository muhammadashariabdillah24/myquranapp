# Catatan Keamanan & Performa Aplikasi

## 1. Performa - Mencegah Memory Leak

### Setup Leak Canary
- **Status**: Sudah dipasang
- **Lokasi**: `app/build.gradle.kts` baris 89
- **Versi**: LeakCanary 2.14
```kotlin
debugImplementation(libs.leakcanary)
```

### Perbaikan Memory Leak yang Sudah Dilakukan
**File**: `app/src/main/java/com/myquranapp/ui/home/HomeFragment.kt`
- Hapus operator `!!` di baris 27 (sering bikin crash soalnya)
-  Sekarang pakai safe call `?.` di semua binding
- ViewBinding dibersihkan dengan benar di `onDestroyView()`

**File**: `favorite/src/main/java/com/myquranapp/favorite/FavoriteFragment.kt`
- Binding sekarang null-safe
- Coroutine sudah diperbaiki biar ngikutin lifecycle fragment pakai `viewLifecycleOwner`
- Cleanup sudah benar di `onDestroyView()`

### Hasil Inspeksi Kode
- Gak ada warning performa
- Gak ada masalah memory leak
- Semua fragment bersihin resource dengan benar

---

## 2. Fitur Keamanan

### A. Obfuscation dengan ProGuard

- **Status**: Aktif

**Lokasi**: `app/build.gradle.kts`
```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

**File rules**: `app/proguard-rules.pro`

**Apa aja yang dilakukan ProGuard**:
1. Obfuscate kode - nama class sama method dirubah jadi nama yang gak jelas
2. Hapus kode yang gak kepake - bersihin dead code
3. Optimasi bytecode - bikin aplikasi lebih kecil dan cepat
4. Buang resource yang gak dipake - hapus asset yang gak terpakai
5. Hapus log debug - buang semua Log.d/v/i di build release

**Rules penting yang harus di-keep**:
- Class security harus tetap utuh: `CertificatePinner`, `DatabaseEncryption`
- Model data Retrofit/Gson butuh nama asli
- Entity Room database gak boleh direname
- Class Koin DI harus dijaga
- Log debug otomatis kehapus

---

### B. Enkripsi Database pakai SQLCipher

- **Status**: Jalan

**Implementasi**: `core/src/main/java/com/myquranapp/core/utils/DatabaseEncryption.kt`

**Library yang dipake**: 
- SQLCipher 4.5.4 (didefinisikan di `gradle/libs.versions.toml`)
- Ditambahkan ke `core/build.gradle.kts`

**Cara kerja enkripsinya**:

1. **Metode enkripsi**: AES-256 bit (standar industri dari SQLCipher)

2. **Bikin passphrase** (`generatePassphrase()`):
   ```kotlin
   Device ID (ANDROID_ID) + Package Name + App Signature
   ```
   - Tiap device punya passphrase unik sendiri
   - Gak mungkin dicopy ke device lain
   - Terikat sama sertifikat signing aplikasi

3. **Setup database**:
   ```kotlin
   val factory = SupportFactory(passphrase)
   Room.databaseBuilder(...).openHelperFactory(factory).build()
   ```

4. **Load library**:
   - SQLCipher di-load pakai `SQLiteDatabase.loadLibs(context)`
   - Dipasang di `CoreModule.kt` dalam modul database

**Database yang dienkripsi**:
- `quran_database.db` (nyimpen SurahEntity)

**Kenapa ini bagus**:
- Database gak bisa dibuka pakai SQLite browser biasa
- Tiap device punya kunci enkripsi berbeda
- Enkripsi/dekripsi otomatis tanpa ribet
- Cuma nambah overhead sekitar 5-15%

---

### C. Certificate Pinning

- **Status**: Aktif

**Lokasi kode**: `core/src/main/java/com/myquranapp/core/utils/CertificatePinner.kt`

**Cara kerjanya**:

1. **Public Key Pinning** (pakai hash SHA-256):
   ```kotlin
   CertificatePinner.Builder()
       .add("staticquran.vercel.app", 
            "sha256/E7UccXKLRnDi5spNXHtSCSSCAJHh7EQZbQv5vDZAVKc=",
            "sha256/C5+lpZ7tcVwmwQIMcRtPbsQtWLABXhQzejna0wHFr8M=",
            "sha256/diGVwiVYbubAI3RW4hB9xU8e/CH2GnkuvVFZE8zmgzI="
       )
   ```

2. **Pin beberapa sertifikat sekaligus**:
   - Pin utama: Sertifikat Let's Encrypt dari Vercel
   - Backup 1: ISRG Root X1 (kalau Let's Encrypt ganti)
   - Backup 2: ISRG Root X2 (buat jaga-jaga kedepannya)

3. **Dipasang di layer network**:
   - Terhubung ke OkHttpClient di `CoreModule.kt` (networkModule)
   ```kotlin
   OkHttpClient.Builder()
       .certificatePinner(CertificatePinner.getCertificatePinner())
   ```

**Cara verifikasi pin secara manual**:
```bash
openssl s_client -connect staticquran.vercel.app:443 | \
openssl x509 -pubkey -noout | \
openssl pkey -pubin -outform der | \
openssl dgst -sha256 -binary | base64
```

**Keuntungannya**:
- Blokir serangan man-in-the-middle
- Verifikasi kalau kita ngomong sama server yang bener
- Ada backup pin kalau sertifikat utama diganti
- Tolak sertifikat palsu atau yang udah disusupi

**Test SSL server**:
- https://www.ssllabs.com/ssltest/analyze.html?d=staticquran.vercel.app

---

### D. SharedPreferences Terenkripsi

**Status**: Jalan

**Implementasi**: `core/src/main/java/com/myquranapp/core/data/source/local/EncryptedSettingsPreferences.kt`

**Library**:
- androidx.security:security-crypto:1.1.0-alpha06 (di `gradle/libs.versions.toml`)
- Ditambahkan ke `core/build.gradle.kts`

**Detail enkripsi**:

1. **Algoritma yang dipake**: 
   - Keys dienkripsi pakai: AES256_SIV
   - Values dienkripsi pakai: AES256_GCM
   - Master key disimpan di: Android Keystore

2. **Setup kode** (buat Android 6.0 keatas):
   ```kotlin
   val masterKey = MasterKey.Builder(context)
       .setKeyGenParameterSpec(spec)
       .build()
       
   EncryptedSharedPreferences.create(
       context,
       "encrypted_app_settings",
       masterKey,
       EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
       EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
   )
   ```

3. **Kompatibilitas ke belakang**:
   - Android 6.0+ (API 23+): Pakai storage terenkripsi
   - Android versi lama: Fallback ke SharedPreferences biasa

4. **Dipake dimana**:
   - Ganti `SettingsDataStore` dengan `EncryptedSettingsPreferences`
   - Update `SettingsRepository.kt` sama `CoreModule.kt`

**Setting user yang dienkripsi**:
- Ukuran Font Arab
- Ukuran Font Terjemahan
- Toggle Tampilkan Transliterasi
- Toggle Tampilkan Bismillah
- Mode Tema (terang/gelap)
- Kecepatan Playback Audio
- Auto Scroll dengan Audio
- Tampilkan Modal No Internet

**Kenapa ini penting**:
- Preferensi user terenkripsi di storage
- Keys sama values dilindungi
- Master key ada di Android Keystore (dilindungi hardware kalau support)
- Enkripsi/dekripsi otomatis di background
- Gak ada dampak ke performa UI

---

## Referensi Cepat - Lokasi Semua File

**Konfigurasi ProGuard** : `app/proguard-rules.pro`

**Enkripsi Database** : `core/src/main/java/com/myquranapp/core/utils/DatabaseEncryption.kt`
**Fungsi Enkripsi Database** : `buildEncryptedDatabase()`

**Certificate Pinning** : `core/src/main/java/com/myquranapp/core/utils/CertificatePinner.kt`
**Fungsi Certificate Pinning** : `getCertificatePinner()`

**Preferences Terenkripsi** : `core/src/main/java/com/myquranapp/core/data/source/local/EncryptedSettingsPreferences.kt` **Fungsi Preferences Terenkripsi** : `Constructor, getSettings()`

**Integrasi Security** : `core/src/main/java/com/myquranapp/core/di/CoreModule.kt`
**Fungsi Integrasi Security** : `databaseModule`, `networkModule`, `repositoryModule`

**Library SQLCipher** : `core/build.gradle.kts`
**Library Security Crypto** : `core/build.gradle.kts`
**Leak Canary** : `app/build.gradle.kts`

---

## Checklist Testing

### Pengecekan Performa
- Leak Canary udah jalan
- Gak ada memory leak yang muncul
- ViewBinding dibersihkan dengan benar
- Udah hapus semua operator force-unwrap `!!`
- Coroutine ngikutin lifecycle fragment

### Keamanan - ProGuard
- Minifikasi udah dinyalakan (isMinifyEnabled = true)
- Pakai config ProGuard yang udah dioptimasi
- Rules custom udah ditambah ke proguard-rules.pro
- Class security gak bakal ke-obfuscate

### Keamanan - Enkripsi Database
- SQLCipher 4.5.4 udah ditambahkan ke dependencies
- Enkripsi AES-256 udah jalan
- Passphrase unik per device
- Database factory udah terpasang
- Library SQLCipher load dengan benar

### Keamanan - Certificate Pinning
- Pin SHA-256 udah dikonfigurasi
- Udah tambah backup pin buat redundansi
- Terpasang di OkHttpClient
- Pinning staticquran.vercel.app

### Keamanan - SharedPreferences Terenkripsi
- Library androidx.security:security-crypto udah ditambah
- Keys dienkripsi pakai AES256_SIV
- Values dienkripsi pakai AES256_GCM
- MasterKey pakai Android Keystore
- Fallback dengan baik di Android versi lama
- Semua setting user udah terenkripsi

---

## Cara Verifikasi Semuanya Berjalan dengan Baik

### 1. Testing Performa (Leak Canary)
```bash
# Build versi debug
./gradlew assembleDebug

# Install dan jalankan aplikasinya
# Leak Canary bakal otomatis ngawasin memory leak
# Kalau ada masalah, bakal muncul notifikasi
```

### 2. Verifikasi ProGuard
```bash
# Build APK release
./gradlew assembleRelease

# Decompile buat cek apakah obfuscation berhasil
jadx app/build/outputs/apk/release/app-release.apk

# Cari nama class yang udah ke-obfuscate (kayak a.b.c.d)
# Konstanta string harusnya udah diminimalisir
```

### 3. Test Enkripsi Database
```bash
# Ambil database dari device
adb pull /data/data/com.myquranapp/databases/quran_database.db

# Coba buka pakai SQLite browser biasa
# Harusnya muncul: "file is encrypted or is not a database"
```

### 4. Test Certificate Pinning
```bash
# Coba intercept traffic pakai proxy (Burp Suite, Charles)
# Koneksi harusnya GAGAL karena sertifikat gak cocok
# Cek log buat: CertificatePin Exception
```

---

**Catatan**:
Semua fitur keamanan udah siap produksi dan mengikuti best practices Android. Kita pakai SQLCipher buat enkripsi database (standar industri), certificate pinning buat mencegah serangan MITM, dan ProGuard buat bikin reverse engineering lebih susah.
