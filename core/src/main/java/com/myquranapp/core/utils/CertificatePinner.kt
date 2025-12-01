package com.myquranapp.core.utils

import okhttp3.CertificatePinner

/**
 * Certificate Pinning untuk koneksi HTTPS
 * 
 * Location: core/src/main/java/com/myquranapp/core/utils/CertificatePinner.kt
 * 
 * Teknik:
 * 1. SHA-256 Public Key Pinning
 * 2. Pin certificates dari staticquran.vercel.app
 * 3. Backup pins untuk redundancy
 * 
 * Cara mendapatkan certificate pins:
 * ```bash
 * openssl s_client -connect staticquran.vercel.app:443 | openssl x509 -pubkey -noout | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | base64
 * ```
 */
object CertificatePinner {
    
    /**
     * Build Certificate Pinner
     * Pins untuk: staticquran.vercel.app
     */
    fun getCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            .add(
                "staticquran.vercel.app",
                // Vercel's Let's Encrypt certificate (primary)
                "sha256/E7UccXKLRnDi5spNXHtSCSSCAJHh7EQZbQv5vDZAVKc=",
                // Let's Encrypt ISRG Root X1 (backup)
                "sha256/C5+lpZ7tcVwmwQIMcRtPbsQtWLABXhQzejna0wHFr8M=",
                // Let's Encrypt ISRG Root X2 (backup)
                "sha256/diGVwiVYbubAI3RW4hB9xU8e/CH2GnkuvVFZE8zmgzI="
            )
            .build()
    }
    
    /**
     * Update pins jika certificate diperbaharui
     * Pin dapat diverifikasi melalui:
     * https://www.ssllabs.com/ssltest/analyze.html?d=staticquran.vercel.app
     */
    fun getCertificatePinnerForUpdate(): CertificatePinner {
        return CertificatePinner.Builder()
            .add(
                "staticquran.vercel.app",
                "sha256/E7UccXKLRnDi5spNXHtSCSSCAJHh7EQZbQv5vDZAVKc=",
                "sha256/C5+lpZ7tcVwmwQIMcRtPbsQtWLABXhQzejna0wHFr8M=",
                "sha256/diGVwiVYbubAI3RW4hB9xU8e/CH2GnkuvVFZE8zmgzI="
            )
            .build()
    }
}
