package com.example.ozelgunsayaci

import android.os.Bundle
import android.content.Context
import com.google.gson.Gson
import androidx.compose.foundation.lazy.items
import com.google.gson.reflect.TypeToken
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.activity.ComponentActivity
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TurnedInNot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import androidx.compose.ui.graphics.toArgb
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.compose.foundation.BorderStroke
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.compose.foundation.isSystemInDarkTheme // Bunu importlara ekle!
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.work.OneTimeWorkRequestBuilder
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_OzelGunSayaci)
        super.onCreate(savedInstanceState)

        val hatirlaticiIstegi = PeriodicWorkRequestBuilder<HatirlaticiWorker>(24, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "GunlukKontrol",
            ExistingPeriodicWorkPolicy.KEEP,
            hatirlaticiIstegi
        )
        setContent {
            MaterialTheme(
                colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AnaIskelet()
                }
            }
        }
    }
}

// --- 1. VERİ PAKETİMİZ ---
data class KayitliEtkinlik(
    val id: Long = System.currentTimeMillis(), // Bu satırı ekle
    var baslik: String,
    var tarih: String = "Tarih Belirtilmedi",
    val sonuc: String,
    val detay: String,
    var ozelNot: String = "",
    val ikon: ImageVector,
    val renk: Color,
    var sarkiLinki: String = "",
)

// BU YENİ BİR SINIF, ESKİSİNE DOKUNMUYORUZ
@Composable
fun AnaIskelet() {
    var karanlikModMu by remember { mutableStateOf(false) }
    var secilenSekme by remember { mutableIntStateOf(0) }
    var taslakBaslik by remember { mutableStateOf("") }
    var taslakNot by remember { mutableStateOf("") }
    var seciliTema by remember { mutableStateOf(temaListesi[1]) }
    val context = LocalContext.current
    val kaydedilenlerListesi = remember { mutableStateListOf<KayitliEtkinlik>().apply { addAll(verileriGetir(context)) } }

    val sekmeler = listOf("Anasayfa", "Hesapla", "Profil")
    val ikonlar = listOf(Icons.Filled.Lightbulb, Icons.Filled.DateRange, Icons.Filled.Person)

    // MaterialTheme'i burada açtık
    MaterialTheme(
        colorScheme = if (karanlikModMu) darkColorScheme() else lightColorScheme()
    ) {
        // İÇERİK BURAYA GELMELİ (Sarma işlemi)
        Surface {
            Scaffold(
                containerColor = seciliTema.arkaPlanRengi,
                topBar = {
                    UstBar(tema = seciliTema) // İŞTE YENİ BARIMIZ BURADA!
                },
                bottomBar = {
                    // ... buradaki kodların aynen kalsın
                    NavigationBar(
                        containerColor = seciliTema.arkaPlanRengi, // Menünün kendi arka planı
                        contentColor = seciliTema.yaziRengi        // Yazıların ve ikonların genel rengi
                    ) {
                        sekmeler.forEachIndexed { index, baslik ->
                            NavigationBarItem(
                                icon = { Icon(ikonlar[index], contentDescription = baslik) },
                                label = { Text(baslik) },
                                selected = secilenSekme == index,
                                onClick = { secilenSekme = index },

                                // SİHİRLİ DOKUNUŞ: Seçili/Seçilmemiş durumu burası yönetiyor
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = seciliTema.barRengi,      // Seçili ikon temanın bar rengi olsun
                                    selectedTextColor = seciliTema.barRengi,      // Seçili yazı temanın bar rengi olsun
                                    unselectedIconColor = seciliTema.yaziRengi.copy(alpha = 0.5f), // Seçilmeyen daha soluk
                                    unselectedTextColor = seciliTema.yaziRengi.copy(alpha = 0.5f),
                                    indicatorColor = seciliTema.barRengi.copy(alpha = 0.2f)       // Arkasındaki o hafif vurgu
                                )
                            )
                        }
                    }
                }
            ) { paddingValues ->
                // ... buradaki when bloğun aynen kalsın
                Box(modifier = Modifier.padding(paddingValues)) {
                    // --- AnaIskelet içindeki when bloğunu şöyle yap ---
                    when (secilenSekme) {
                        0 -> FikirlerSayfasi(
                            kaydedilenler = kaydedilenlerListesi,
                            seciliTema = seciliTema,
                            onIlhamTikladi = { baslik, not ->
                                // Fikirler sayfasından gelenleri çantaya koy ve 1. sekmeye geç
                                taslakBaslik = baslik
                                taslakNot = not
                                secilenSekme = 1
                            }
                        )
                        1 -> HesaplaSayfasi(
                            gelenBaslik = taslakBaslik,
                            gelenNot = taslakNot,
                            seciliTema = seciliTema, // <-- Buraya bunu ekliyoruz!
                            onKaydet = { yeniEtkinlik ->
                                kaydedilenlerListesi.add(yeniEtkinlik)
                                verileriKaydet(context, liste = kaydedilenlerListesi)
                                secilenSekme = 2

                                taslakBaslik = ""
                                taslakNot = ""
                            }
                        )
                        2 -> ProfilSayfasi(
                            kaydedilenler = kaydedilenlerListesi,
                            onSil = { silinecekEtkinlik ->
                                kaydedilenlerListesi.remove(silinecekEtkinlik)
                                verileriKaydet(context, kaydedilenlerListesi) // EKLE: Buraya da bu satırı ekle
                            },
                            seciliTema = seciliTema,
                            onTemaDegistir = { seciliTema = it },
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun FikirlerSayfasi(
    kaydedilenler: List<KayitliEtkinlik>,
    seciliTema: UygulamaTemasi,
    onIlhamTikladi: (String, String) -> Unit
) {
    val saat = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    val karsilamaMesaji = when (saat) {
        in 5..11 -> "Günaydın canım,\nharika bir gün olsun! ☀️"
        in 12..16 -> "Tünaydın canım,\ngünün güzel geçiyor mu? ☁️"
        in 17..21 -> "İyi akşamlar canım,\nbugünün en güzel anı neydi? ✨"
        else -> "İyi geceler canım,\ntatlı rüyalar... 🌙"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // --- 1. KARŞILAMA KARTI ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            // Akıllı Arka Plan: Temanın rengini %15 saydamlıkla kullanıyoruz
            colors = CardDefaults.cardColors(containerColor = seciliTema.barRengi.copy(alpha = 0.15f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, seciliTema.barRengi.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.Start) {
                // Yazılar zaten akıllı renge bağlıydı, harika parlayacak
                Text(text = karsilamaMesaji, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = seciliTema.yaziRengi, lineHeight = 30.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Bugün yeni anılar biriktirmek için harika bir gün.", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = seciliTema.yaziRengi.copy(alpha = 0.6f))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 2. YAKLAŞAN BÜYÜK GÜNLERİN (Canlı Hesaplama Eklendi ✨) ---
        val yaklasanEtkinlikler = kaydedilenler.map { etkinlik ->
            val (guncelSonuc, _) = anlikTarihHesapla(etkinlik.tarih)
            etkinlik to guncelSonuc
        }
            .filter { it.second.contains("kaldı") || it.second.contains("Bugün") }
            .sortedBy { it.second.filter { c -> c.isDigit() }.toIntOrNull() ?: 9999 }

        if (yaklasanEtkinlikler.isNotEmpty()) {
            Text(text = "Yaklaşan Büyük Günlerin ✨", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = seciliTema.yaziRengi, modifier = Modifier.padding(bottom = 16.dp))

            LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(yaklasanEtkinlikler) { (etkinlik, guncelSonuc) ->
                    Card(
                        modifier = Modifier.width(160.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = seciliTema.barRengi.copy(alpha = 0.1f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, seciliTema.barRengi.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
                            Surface(shape = CircleShape, color = seciliTema.barRengi.copy(alpha = 0.2f), modifier = Modifier.size(36.dp)) {
                                Icon(etkinlik.ikon, contentDescription = null, tint = seciliTema.yaziRengi, modifier = Modifier.padding(8.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = etkinlik.baslik, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = seciliTema.yaziRengi, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.height(4.dp))

                            val safakYazisi = if (guncelSonuc.contains("kaldı")) "Şafak: " + guncelSonuc.replace(" kaldı", "").replace("gün", "Gün") else guncelSonuc

                            // Sabit pembeyi sildik, temanın vurgu rengini (barRengi) verdik
                            Text(safakYazisi, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = seciliTema.barRengi)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // --- 3. İLHAM AL BÖLÜMÜ ---
        Text(text = "💡 İlham Al", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = seciliTema.yaziRengi, modifier = Modifier.padding(bottom = 16.dp))

        val ilhamListesi = listOf(
            Triple("❤️", "Yıl Dönümümüz", "Birlikte yazdığımız bu güzel hikayenin en özel günü."),
            Triple("✈️", "Rüya Gibi Tatil", "Bavulları toplayıp kaçtığımız o harika seyahat..."),
            Triple("💪", "Sağlıklı Yaşama Adım", "Daha iyi bir sen için atılan o ilk adım..."),
            Triple("🚭", "Sigarayı Bırakma", "Dumansız, sağlıklı ve tertemiz bir hayata adım..."),
            Triple("🎓", "Mezuniyet Günü", "Yılların emeği, uykusuz geceler ve o haklı gurur..."),
            Triple("💼", "İlk İş Günü", "Kariyer yolculuğunda atılan o ilk heyecanlı adım...")
        )
        ilhamListesi.forEach { fikir ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                // Akıllı Arka Plan eklendi
                colors = CardDefaults.cardColors(containerColor = seciliTema.barRengi.copy(alpha = 0.15f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = fikir.first, fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        // Yazılar Akıllı Renge bağlandı
                        Text(text = fikir.second, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = seciliTema.yaziRengi)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = fikir.third, fontSize = 12.sp, color = seciliTema.yaziRengi.copy(alpha = 0.7f), maxLines = 2)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onIlhamTikladi(fikir.second, fikir.third) },
                        // Süper Kontrast: Buton içi yazı rengi, yazılar arka plan rengi oldu!
                        colors = ButtonDefaults.buttonColors(containerColor = seciliTema.yaziRengi),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text("Şimdi Dene ✨", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = seciliTema.arkaPlanRengi)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}
// 2. Sayfa: Hesapla Sayfası
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HesaplaSayfasi(
    gelenBaslik: String = "",
    gelenNot: String = "",
    seciliTema: UygulamaTemasi,
    onKaydet: (KayitliEtkinlik) -> Unit
) {
    var takvimAcikMi by remember { mutableStateOf(false) }
    var secilenTarih by remember { mutableStateOf("Tarih seçmek için butona tıkla") }
    var sonucMetni by remember { mutableStateOf("") }
    var detayliSonucMetni by remember { mutableStateOf("") }

    var etkinlikBasligi by remember(gelenBaslik) { mutableStateOf(gelenBaslik) }
    var etkinlikNotu by remember(gelenNot) { mutableStateOf(gelenNot) }
    var sarkiLinki by remember { mutableStateOf("") }

    val ikonSecenekleri = listOf(
        Icons.Filled.Favorite,
        Icons.Filled.Cake,
        Icons.Filled.Flight,
        Icons.Filled.School,
        Icons.Filled.Star,
        Icons.Filled.NoAccounts,
        Icons.Filled.FitnessCenter,
        Icons.Filled.Work,
        Icons.Filled.Pets
    )
    var secilenIkon by remember { mutableStateOf(ikonSecenekleri[0]) }
    val context = LocalContext.current
    val renkSecenekleri = listOf(Color(0xFFFFCDD2), Color(0xFFBBDEFB), Color(0xFFC8E6C9), Color(0xFFE1BEE7), Color(0xFFFFE0B2))
    var secilenRenk by remember { mutableStateOf(renkSecenekleri[0]) }

    val takvimDurumu = rememberDatePickerState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(), // Liste ekranı kaplasın
        contentPadding = PaddingValues(24.dp), // Dıştan boşlukları buradan veriyoruz
        verticalArrangement = Arrangement.Center, // İçerik azsa ortaya hizala
        horizontalAlignment = Alignment.CenterHorizontally // Ortaya hizala
    ) {
        item {
            Column(
                // SİHİRLİ DOKUNUŞ BURADA: fillParentMinHeight() ile ekranı kaplamasını zorluyoruz
                modifier = Modifier.fillMaxWidth(), // Artık burada fazladan fillMaxSize'a gerek yok
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (sonucMetni.isEmpty()) {
                    // EMOJİSİZ, DAHA SADELİTİLMİŞ KART
                    // --- KARTLI KARŞILAMA ---
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp), // Kart ekranın geneline yayılsın
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = seciliTema.barRengi.copy(alpha = 0.1f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth() // İçerik kartın tüm genişliğini alsın
                                .padding(24.dp), // İçeriden boşluk ver
                            horizontalAlignment = Alignment.CenterHorizontally // İçerideki tüm yazıları enden ortala
                        ) {
                            // Artık emojiyi kaldırdık, sadece yazılar kaldı.
                            // HorizontalAlignment sayesinde "Yeni Bir Anı Planla" otomatik ortalanacak.
                            Text(
                                text = "Yeni Bir Anı Planla",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = seciliTema.yaziRengi,
                                textAlign = TextAlign.Center // Yazıların kendisini de ortala
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Hangi günü ölümsüzleştirelim?",
                                fontSize = 14.sp,
                                color = seciliTema.yaziRengi.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center // Yazıların kendisini de ortala
                            )
                        }
                    }
                } else {

                    val textFieldRenkleri = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = seciliTema.yaziRengi,
                        unfocusedTextColor = seciliTema.yaziRengi,
                        focusedBorderColor = seciliTema.barRengi,
                        unfocusedBorderColor = seciliTema.yaziRengi.copy(alpha = 0.3f),
                        focusedLabelColor = seciliTema.barRengi,
                        unfocusedLabelColor = seciliTema.yaziRengi.copy(alpha = 0.6f),
                        cursorColor = seciliTema.barRengi
                    )

                    Text(
                        text = secilenTarih,
                        style = MaterialTheme.typography.titleMedium,
                        color = seciliTema.yaziRengi.copy(alpha = 0.9f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(text = sonucMetni, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = seciliTema.yaziRengi, modifier = Modifier.padding(bottom = 8.dp))
                    Text(text = detayliSonucMetni, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = seciliTema.yaziRengi, modifier = Modifier.padding(bottom = 24.dp))

                    OutlinedTextField(
                        value = etkinlikBasligi,
                        onValueChange = { etkinlikBasligi = it },
                        label = { Text("Bir İsim Ver") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        singleLine = true,
                        colors = textFieldRenkleri
                    )

                    OutlinedTextField(
                        value = etkinlikNotu,
                        onValueChange = { etkinlikNotu = it },
                        label = { Text("Özel Bir Anı Notu Ekle") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        maxLines = 2,
                        colors = textFieldRenkleri
                    )

                    OutlinedTextField(
                        value = sarkiLinki,
                        onValueChange = { sarkiLinki = it },
                        label = { Text("Şarkı Linki") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        singleLine = true,
                        colors = textFieldRenkleri
                    )

                    Text("Bir İkon Seç:", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.Bold, color = seciliTema.yaziRengi)
                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        ikonSecenekleri.forEach { ikon ->
                            IconButton(onClick = { secilenIkon = ikon }) {
                                Icon(ikon, contentDescription = null, tint = if (secilenIkon == ikon) seciliTema.yaziRengi else seciliTema.yaziRengi.copy(alpha = 0.3f), modifier = Modifier.size(32.dp))
                            }
                        }
                    }

                    Text("Kart Rengini Seç:", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.Bold, color = seciliTema.yaziRengi)
                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)) {
                        renkSecenekleri.forEach { renk ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(renk, shape = CircleShape)
                                    .clickable { secilenRenk = renk }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (secilenRenk == renk) {
                                    Icon(Icons.Filled.Check, contentDescription = "Seçili", tint = Color.Black.copy(alpha = 0.6f))
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val yeniEtkinlik = KayitliEtkinlik(
                                baslik = if (etkinlikBasligi.isEmpty()) "Özel Günüm" else etkinlikBasligi,
                                tarih = secilenTarih,
                                sonuc = sonucMetni,
                                detay = detayliSonucMetni,
                                ozelNot = etkinlikNotu,
                                ikon = secilenIkon,
                                renk = secilenRenk,
                                sarkiLinki = sarkiLinki
                            )
                            onKaydet(yeniEtkinlik)
                            etkinlikBasligi = ""; etkinlikNotu = ""; sarkiLinki = ""; sonucMetni = ""; detayliSonucMetni = ""; secilenTarih = "Tarih seçmek için butona tıkla"
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = seciliTema.barRengi)
                    ) {
                        Text("Profilime Ekle", color = seciliTema.arkaPlanRengi, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { takvimAcikMi = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = seciliTema.barRengi)
                ) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Takvim", modifier = Modifier.padding(end = 8.dp), tint = seciliTema.arkaPlanRengi)
                    Text("Tarih Seç", color = seciliTema.arkaPlanRengi, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (takvimAcikMi) {
        DatePickerDialog(
            onDismissRequest = { takvimAcikMi = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        takvimAcikMi = false
                        takvimDurumu.selectedDateMillis?.let { milisaniye ->
                            val format = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                            secilenTarih = format.format(java.util.Date(milisaniye))

                            val secilenLocalDate = java.time.Instant.ofEpochMilli(milisaniye).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                            val bugunLocalDate = java.time.LocalDate.now()

                            val toplamGunFarki = java.time.temporal.ChronoUnit.DAYS.between(bugunLocalDate, secilenLocalDate)

                            if (toplamGunFarki == 0L) {
                                sonucMetni = "Etkinlik Bugün! 🎉"
                                detayliSonucMetni = "Kutlamalara başla!"
                            } else {
                                val isGecmis = toplamGunFarki < 0

                                val period = if (isGecmis) {
                                    java.time.Period.between(secilenLocalDate, bugunLocalDate)
                                } else {
                                    java.time.Period.between(bugunLocalDate, secilenLocalDate)
                                }

                                val yil = period.years
                                val ay = period.months
                                val gun = period.days

                                val detayListesi = mutableListOf<String>()
                                if (yil > 0) detayListesi.add("$yil yıl")
                                if (ay > 0) detayListesi.add("$ay ay")
                                if (gun > 0) detayListesi.add("$gun gün")

                                val detayString = detayListesi.joinToString(", ")

                                if (isGecmis) {
                                    sonucMetni = "${kotlin.math.abs(toplamGunFarki)} gün geçti"
                                    detayliSonucMetni = "Yani: $detayString geçti"
                                } else {
                                    sonucMetni = "$toplamGunFarki gün kaldı"
                                    detayliSonucMetni = "Yani: $detayString kaldı"
                                }
                            }
                        }
                    }
                ) {
                    Text("Tamam")
                }
            },
            dismissButton = {
                TextButton(onClick = { takvimAcikMi = false }) {
                    Text("İptal")
                }
            }
        ) {
            DatePicker(state = takvimDurumu)
        }
    }
}

// 3. Sayfa: Profil Ekranı
// Profil Ekranı - Yenilenmiş Estetik Kart Tasarımı
@Composable
fun ProfilSayfasi(
    kaydedilenler: List<KayitliEtkinlik>,
    onSil: (KayitliEtkinlik) -> Unit,
    seciliTema: UygulamaTemasi,
    onTemaDegistir: (UygulamaTemasi) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    var silinecekEtkinlik by remember { mutableStateOf<KayitliEtkinlik?>(null) }
    var aramaMetni by remember { mutableStateOf("") }
    var duzenlenecekEtkinlik by remember { mutableStateOf<KayitliEtkinlik?>(null) }

    val filtrelenmisListe = kaydedilenler.filter {
        it.baslik.contains(aramaMetni, ignoreCase = true)
    }.sortedBy { etkinlik ->
        // Artık sıralamayı statik sonuc üzerinden değil, dinamik hesaplamanın verdiği gün sayısından alıyor
        val (guncelSonuc, _) = anlikTarihHesapla(etkinlik.tarih)
        guncelSonuc.filter { char -> char.isDigit() }.toIntOrNull() ?: 99999
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = aramaMetni,
            onValueChange = { aramaMetni = it },
            label = { Text("Anılarını Ara...") },
            leadingIcon = { Icon(Icons.Filled.Search, null, tint = seciliTema.yaziRengi.copy(alpha = 0.6f)) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            // Şık ve akıllı renk geçişi için bunu ekleyebilirsin:
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = seciliTema.yaziRengi,
                unfocusedTextColor = seciliTema.yaziRengi,
                focusedLabelColor = seciliTema.barRengi,
                unfocusedLabelColor = seciliTema.yaziRengi.copy(alpha = 0.6f),
                focusedBorderColor = seciliTema.barRengi,
                unfocusedBorderColor = seciliTema.yaziRengi.copy(alpha = 0.3f)
            )
        )

        if (kaydedilenler.isNotEmpty() && aramaMetni.isEmpty()) {
            IstatistikPaneli(kaydedilenler = kaydedilenler, seciliTema = seciliTema)
        }
        if (aramaMetni.isEmpty()) {
            TemaSecici(seciliTema = seciliTema, onTemaSecildi = onTemaDegistir)
        }
        if (filtrelenmisListe.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(if (kaydedilenler.isEmpty()) "Henüz bir şey kaydetmediniz. 😊" else "Aradığın kriterde anı yok.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn {
                items(filtrelenmisListe) { etkinlik ->
                    var genisletildi by remember { mutableStateOf(false) }

                    // İŞTE SİHİR BURADA: Her kart çizilirken, tarih bugüne göre yeniden hesaplanıyor! ✨
                    val (guncelSonuc, guncelDetay) = anlikTarihHesapla(etkinlik.tarih)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { genisletildi = !genisletildi },
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(0.dp),
                        colors = CardDefaults.cardColors(containerColor = etkinlik.renk.copy(alpha = 0.2f))
                    ) {
                        Column {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Row(
                                    modifier = Modifier.align(Alignment.TopEnd),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (etkinlik.sarkiLinki.isNotEmpty()) {
                                        Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(32.dp)) {
                                            IconButton(onClick = { try { uriHandler.openUri(if (!etkinlik.sarkiLinki.startsWith("http")) "https://${etkinlik.sarkiLinki}" else etkinlik.sarkiLinki) } catch (e: Exception) {} }) {
                                                Icon(Icons.Filled.MusicNote, null, tint = Color(0xFF5F3B43).copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                    Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(32.dp)) {
                                        IconButton(onClick = { duzenlenecekEtkinlik = etkinlik }) {
                                            Icon(Icons.Filled.Edit, "Düzenle", tint = Color(0xFF5F3B43).copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(32.dp)) {
                                        IconButton(onClick = { silinecekEtkinlik = etkinlik }) {
                                            Icon(Icons.Filled.Delete, "Sil", tint = Color(0xFF5F3B43).copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }

                                Row(modifier = Modifier.fillMaxWidth().padding(end = 90.dp)) {
                                    Icon(
                                        imageVector = etkinlik.ikon,
                                        contentDescription = null,
                                        tint = etkinlik.renk,
                                        modifier = Modifier.size(52.dp).padding(top = 4.dp)
                                    )

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            text = etkinlik.baslik,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = seciliTema.yaziRengi // <-- Akıllı Renk!
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Başlangıç: ${etkinlik.tarih}",
                                            fontSize = 11.sp,
                                            color = seciliTema.yaziRengi.copy(alpha = 0.6f) // <-- Yarı saydam Akıllı Renk
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = guncelSonuc,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 18.sp,
                                                color = seciliTema.yaziRengi // <-- Akıllı Renk!
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("✨", fontSize = 16.sp)
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = guncelDetay,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = seciliTema.yaziRengi.copy(alpha = 0.7f) // <-- Akıllı Renk!
                                        )
                                    }
                                }
                            }

                            if (etkinlik.ozelNot.isNotEmpty()) {
                                AnimatedVisibility(visible = genisletildi) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = seciliTema.arkaPlanRengi.copy(alpha = 0.5f), // <-- Sabit beyaz yerine temanın arka planı
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp)
                                    ) {
                                        Text(
                                            text = "✍️ ${etkinlik.ozelNot}",
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 12.sp,
                                            color = seciliTema.yaziRengi.copy(alpha = 0.9f), // <-- Akıllı yazı rengi
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (silinecekEtkinlik != null) {
        AlertDialog(
            onDismissRequest = { silinecekEtkinlik = null },
            title = { Text("Silmek istiyor musun?") },
            text = { Text("Bu anıyı sildiğinde geri dönüşü olmayacak.") },
            confirmButton = { TextButton(onClick = { onSil(silinecekEtkinlik!!); silinecekEtkinlik = null }) { Text("Evet", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { silinecekEtkinlik = null }) { Text("İptal") } }
        )
    }

    if (duzenlenecekEtkinlik != null) {
        var yeniBaslik by remember { mutableStateOf(duzenlenecekEtkinlik!!.baslik) }
        var yeniNot by remember { mutableStateOf(duzenlenecekEtkinlik!!.ozelNot) }
        var yeniLink by remember { mutableStateOf(duzenlenecekEtkinlik!!.sarkiLinki) }

        AlertDialog(
            onDismissRequest = { duzenlenecekEtkinlik = null },
            title = { Text("Anıyı Düzenle", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = yeniBaslik, onValueChange = { yeniBaslik = it }, label = { Text("Başlık") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = yeniNot, onValueChange = { yeniNot = it }, label = { Text("Özel Not") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = yeniLink, onValueChange = { yeniLink = it }, label = { Text("Spotify/Youtube Linki") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    duzenlenecekEtkinlik!!.baslik = yeniBaslik
                    duzenlenecekEtkinlik!!.ozelNot = yeniNot
                    duzenlenecekEtkinlik!!.sarkiLinki = yeniLink
                    duzenlenecekEtkinlik = null
                }) { Text("Kaydet", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { duzenlenecekEtkinlik = null }) { Text("İptal") } }
        )
    }
}
// --- DOSYANIN EN ALT KISMI ---

data class KaydedilebilirEtkinlik(
    val baslik: String,
    val tarih: String,
    val sonuc: String,
    val detay: String,
    val ozelNot: String,
    val sarkiLinki: String,
    val renkInt: Int,       // Long yerine tekrar Int yaptık
    val ikonAdi: String
)

fun verileriKaydet(context: Context, liste: List<KayitliEtkinlik>) {
    val gson = Gson()
    val kaydedilebilirListe = liste.map { it ->
        KaydedilebilirEtkinlik(
            baslik = it.baslik,
            tarih = it.tarih,
            sonuc = it.sonuc,
            detay = it.detay,
            ozelNot = it.ozelNot,
            sarkiLinki = it.sarkiLinki,
            renkInt = it.renk.toArgb(), // Artık kırmızı yanmayacak!
            ikonAdi = it.ikon.name      // toString() yerine tam adını alıyoruz
        )
    }
    val jsonString = gson.toJson(kaydedilebilirListe)
    context.getSharedPreferences("OzelGunData", Context.MODE_PRIVATE)
        .edit().putString("etkinlikler", jsonString).apply()
}

fun verileriGetir(context: Context): List<KayitliEtkinlik> {
    val sharedPreferences = context.getSharedPreferences("OzelGunData", Context.MODE_PRIVATE)
    val jsonString = sharedPreferences.getString("etkinlikler", null) ?: return emptyList()

    return try {
        val type = object : TypeToken<List<KaydedilebilirEtkinlik>>() {}.type
        val kaydedilenler: List<KaydedilebilirEtkinlik> = Gson().fromJson(jsonString, type)

        kaydedilenler.map {
            KayitliEtkinlik(
                baslik = it.baslik,
                tarih = it.tarih,
                sonuc = it.sonuc,
                detay = it.detay,
                ozelNot = it.ozelNot,
                sarkiLinki = it.sarkiLinki,
                renk = Color(it.renkInt),           // Rengi tam olarak geri alıyoruz
                ikon = getIkonByName(it.ikonAdi)    // İkonu tam isminden geri buluyoruz
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

fun getIkonByName(name: String): ImageVector {
    return when {
        name.contains("Favorite", ignoreCase = true) -> Icons.Filled.Favorite
        name.contains("Cake", ignoreCase = true) -> Icons.Filled.Cake
        name.contains("Flight", ignoreCase = true) -> Icons.Filled.Flight
        name.contains("School", ignoreCase = true) -> Icons.Filled.School
        else -> Icons.Filled.Star
    }
}
@Composable
fun IstatistikPaneli(kaydedilenler: List<KayitliEtkinlik>, seciliTema: UygulamaTemasi) {
    val toplamAni = kaydedilenler.size

    // Sihirli Dokunuş: İstatistiklerin de canlı hesaplanması için güncel durumları alıyoruz ✨
    val guncelEtkinlikler = kaydedilenler.map { it to anlikTarihHesapla(it.tarih).first }

    val bekleyenGun = guncelEtkinlikler.count { it.second.contains("kaldı") || it.second.contains("Bugün") }

    val enYakinCift = guncelEtkinlikler
        .filter { it.second.contains("kaldı") }
        .minByOrNull { it.second.filter { char -> char.isDigit() }.toIntOrNull() ?: 9999 }
        ?: guncelEtkinlikler.find { it.second.contains("Bugün") }

    val enYakin = enYakinCift?.first
    val enYakinSonuc = enYakinCift?.second ?: ""

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = seciliTema.barRengi.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // color = seciliTema.yaziRengi eklenerek karanlıkta parlaması sağlandı 🌟
                        Text("Biriken Anı", style = MaterialTheme.typography.labelSmall, color = seciliTema.yaziRengi.copy(alpha = 0.7f))
                        Text("💌", fontSize = 22.sp)
                    }
                    Text("$toplamAni", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = seciliTema.yaziRengi)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = seciliTema.barRengi.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // color = seciliTema.yaziRengi eklendi 🌟
                        Text("Gelecek Planı", style = MaterialTheme.typography.labelSmall, color = seciliTema.yaziRengi.copy(alpha = 0.7f))
                        Text("🗓️", fontSize = 22.sp)
                    }
                    Text("$bekleyenGun", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = seciliTema.yaziRengi)
                }
            }
        }

        if (enYakin != null) {
            Spacer(modifier = Modifier.height(6.dp))
            val vurguRengi = seciliTema.barRengi.copy(alpha = 0.6f)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = vurguRengi),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = seciliTema.arkaPlanRengi.copy(alpha = 0.6f)) {
                        Icon(
                            imageVector = enYakin.ikon,
                            contentDescription = null,
                            tint = seciliTema.yaziRengi,
                            modifier = Modifier.padding(6.dp).size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("En Yakın Heyecan", fontSize = 10.sp, color = seciliTema.yaziRengi.copy(alpha = 0.8f))
                            Text("🎈", fontSize = 16.sp)
                        }
                        Text(enYakin.baslik, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = seciliTema.yaziRengi)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    // Burası da artık donuk veriyi değil, yukarıda hesaplanan canlı güncel sonucu gösteriyor ✨
                    Text(text = enYakinSonuc, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = seciliTema.yaziRengi)
                }
            }
        }
    }
}
class HatirlaticiWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val context = applicationContext
        val kaydedilenler = verileriGetir(context)

        kaydedilenler.forEach { etkinlik ->
            val sonuc = etkinlik.sonuc

            // Kaç gün kaldığına bakıp, ona göre en tatlı mesajı seçiyoruz
            val bildirimMesaji = when {
                sonuc.contains("7 gün kaldı") -> "Heyecan başlıyor, son 1 hafta kaldı! 🤩"
                sonuc.contains("3 gün kaldı") -> "Son hazırlıklar, sadece 3 gün kaldı! ⏳"
                sonuc.contains("1 gün kaldı") -> "Yarın büyük gün! 🎉 Son 1 gün!"
                sonuc.contains("Bugün") -> "Büyük gün geldi! 🥳 Hazır mısın?"
                else -> null // Eğer bu özel günlerden biri değilse sessiz kalıyoruz
            }

            // Eğer bildirimMesaji doluysa (yani o gün geldiyse) bildirimi gönder
            if (bildirimMesaji != null) {
                bildirimGonder(context, etkinlik.baslik, bildirimMesaji)
            }
        }
        return Result.success()
    }

    private fun bildirimGonder(context: Context, baslik: String, mesaj: String) {
        val kanalId = "ozel_gun_kanali"
        val bildirimYoneticisi = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0 ve üzeri için Bildirim Kanalı (Channel) zorunludur
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val kanal = NotificationChannel(kanalId, "Özel Gün Hatırlatmaları", NotificationManager.IMPORTANCE_HIGH)
            bildirimYoneticisi.createNotificationChannel(kanal)
        }

        val bildirim = NotificationCompat.Builder(context, kanalId)
            .setSmallIcon(android.R.drawable.ic_menu_today) // İstersen daha sonra kendi tatlı ikonumuzla değiştiririz
            .setContentTitle(baslik)
            .setContentText(mesaj)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        bildirimYoneticisi.notify(baslik.hashCode(), bildirim)
    }
}
@Composable
fun UstBar(tema: UygulamaTemasi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(tema.barRengi)
            // Üstten 30dp ve alttan 10dp yaparak yüksekliği kıstık
            .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Hatıra Günlüğü",
            color = tema.yaziRengi,
            fontSize = 20.sp, // Yazıyı da biraz küçülterek ferahlattık
            fontWeight = FontWeight.ExtraBold
        )
        Text(text = tema.emoji, fontSize = 20.sp)
    }
}
data class UygulamaTemasi(
    val ad: String,
    val emoji: String,
    val barRengi: Color,
    val arkaPlanRengi: Color,
    val yaziRengi: Color
)

// Senin için tam istediğin gibi "cart" olmayan, en soft tonları seçtim:
val temaListesi = listOf(
    UygulamaTemasi("Gün Batımı", "\uD83C\uDF05", Color(0xFFCF7342), Color(0xFFFAF8F5), Color(0xFFA65B33)),
    UygulamaTemasi("Pembe", "🌸", Color(0xFFE6D0D3), Color(0xFFFFF5F6), Color(0xFF5F3B43)),
    UygulamaTemasi("Gece Mavisi", "🌌", Color(0xFFD0D4EA), Color(0xFFF4F5FB), Color(0xFF424874)),
    UygulamaTemasi("Yeşil", "🍃", Color(0xFFD4E2D4), Color(0xFFF7FBF7), Color(0xFF4A5D4E)),
    UygulamaTemasi(ad = "Antrasit", emoji = "🌑", barRengi = Color(0xFF546E7A), arkaPlanRengi = Color(0xFF263238), yaziRengi = Color(0xFFECEFF1)),

)
@Composable
fun TemaSecici(seciliTema: UygulamaTemasi, onTemaSecildi: (UygulamaTemasi) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Tema Seçimi:",
            fontWeight = FontWeight.Bold,
            color = seciliTema.yaziRengi,
            modifier = Modifier.padding(end = 8.dp)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(temaListesi) { tema ->
                val seciliMi = tema.ad == seciliTema.ad
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (seciliMi) tema.yaziRengi else tema.barRengi.copy(alpha = 0.5f),
                    modifier = Modifier.clickable { onTemaSecildi(tema) }
                ) {
                    Text(
                        text = "${tema.emoji} ${tema.ad}",
                        color = if (seciliMi) Color.White else tema.yaziRengi,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontWeight = if (seciliMi) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
fun anlikTarihHesapla(kayitliTarih: String): Pair<String, String> {
    return try {
        val format = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        val secilenDate = format.parse(kayitliTarih) ?: return Pair("", "")

        val secilenLocalDate = java.time.Instant.ofEpochMilli(secilenDate.time)
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        val bugunLocalDate = java.time.LocalDate.now()

        val toplamGunFarki = java.time.temporal.ChronoUnit.DAYS.between(bugunLocalDate, secilenLocalDate)

        if (toplamGunFarki == 0L) {
            Pair("Etkinlik Bugün! 🎉", "Kutlamalara başla!")
        } else {
            val isGecmis = toplamGunFarki < 0
            val period = if (isGecmis) {
                java.time.Period.between(secilenLocalDate, bugunLocalDate)
            } else {
                java.time.Period.between(bugunLocalDate, secilenLocalDate)
            }

            val detayListesi = mutableListOf<String>()
            if (period.years > 0) detayListesi.add("${period.years} yıl")
            if (period.months > 0) detayListesi.add("${period.months} ay")
            if (period.days > 0) detayListesi.add("${period.days} gün")
            val detayString = detayListesi.joinToString(", ")

            if (isGecmis) {
                Pair("${kotlin.math.abs(toplamGunFarki)} gün geçti", "Yani: $detayString geçti")
            } else {
                Pair("$toplamGunFarki gün kaldı", "Yani: $detayString kaldı")
            }
        }
    } catch (e: Exception) {
        Pair("Tarih Bekleniyor", "")
    }
}