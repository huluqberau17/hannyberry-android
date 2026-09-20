# HannyBerry Android

Aplikasi Android offline-first untuk usaha stroberi keluarga. Bagian dari monorepo
`strawberry-business`, terhubung ke API HannyBerry di `https://app.husnulhuluq.com`.

## Requirement

- Android Studio (Ladybug atau lebih baru)
- JDK 17
- Android SDK Platform 35
- Perangkat atau emulator Android 8.0 (API 26) ke atas

## Struktur

```text
android/
├── app/
│   └── src/main/
│       ├── java/com/hannyberry/
│       │   ├── MainActivity.kt
│       │   ├── data/local/          # Room: entity, dao, database
│       │   └── ui/                  # Compose: layar aplikasi
│       ├── AndroidManifest.xml
│       └── res/values/themes.xml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## Menjalankan

1. Buka folder `android/` di Android Studio.
2. Tunggu Gradle sync selesai.
3. Jalankan `app` pada perangkat atau emulator.

## Prinsip V1

- Data disimpan lokal di Room dengan UUID yang dibuat di perangkat.
- Transaksi memiliki status `dirty` yang menandai baris belum tersinkron.
- Delete bersifat lunak (`deletedAt`), bukan penghapusan permanen.
- Jenis transaksi V1: `INCOME`, `COST_OF_GOODS`, `OPERATING_EXPENSE`.
- Jumlah uang disimpan sebagai bilangan bulat rupiah.
- Sinkronisasi bersifat idempotent dengan aturan last-write-wins.
