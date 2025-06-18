# 🤖 Minecraft AI Chat Eklentisi | Lora Project Entegrasyonu

<div align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.16.5%2B-brightgreen" alt="Minecraft 1.16.5+" />
  <img src="https://img.shields.io/badge/API-KynuxCloud-blue" alt="KynuxCloud API" />
  <img src="https://img.shields.io/badge/Versiyon-1.1-orange" alt="Versiyon 1.1" />
  <img src="https://img.shields.io/badge/Güvenlik-Üst_Düzey-red" alt="Güvenlik" />
  <br />
  <img src="https://img.shields.io/badge/Grok--3--Mini-Desteklenir-blueviolet" alt="Grok-3-mini" />
</div>

<p align="center">
  <b>Minecraft sunucunuzda yapay zeka ile sohbeti bir üst seviyeye taşıyın!</b>
</p>

## 📋 İçindekiler

- [🌟 Genel Bakış](#-genel-bakış)
- [✨ Temel Özellikler](#-temel-özellikler)
- [🔧 Kurulum](#-kurulum)
- [⚙️ Yapılandırma](#️-yapılandırma)
- [📢 Komutlar](#-komutlar)
- [🛡️ İzinler](#️-izinler)
- [🧩 Gelişmiş Özellikler](#-gelişmiş-özellikler)
- [🚀 Performans Optimizasyonu](#-performans-optimizasyonu)
- [🔐 Güvenlik Önlemleri](#-güvenlik-önlemleri)
- [❓ Sık Sorulan Sorular](#-sık-sorulan-sorular)
- [📘 API Entegrasyonu](#-api-entegrasyonu)
- [🛠️ Geliştirici Bilgileri](#️-geliştirici-bilgileri)
- [🔮 Gelecek Özellikler](#-gelecek-özellikler)
- [📞 Destek ve İletişim](#-destek-ve-iletişim)

## 🌟 Genel Bakış

AiChat eklentisi, Minecraft sunucunuzda oyuncuların gelişmiş yapay zeka modelleriyle anlık olarak iletişim kurmasını sağlar. Lora Project'un güçlü yapay zeka API altyapısını kullanarak, en gelişmiş dil modellerine erişim sağlar ve oyuncularınıza benzersiz bir deneyim sunar.

## ✨ Temel Özellikler

### 💬 Sohbet Entegrasyonu
- **Komut Tabanlı Erişim**: `/ai <mesaj>` komutu ile anında AI asistana ulaşma
- **Sohbet Tetikleyicisi**: Normal sohbette `ai <mesaj>` yazarak AI ile konuşabilme
- **Renkli Yanıtlar**: Özelleştirilebilir renk kodları ile AI yanıtlarında görsel zenginlik
- **Oyuncu Bazlı Konuşma Geçmişi**: Her oyuncu için ayrı konuşma akışı

### 🧠 Yapay Zeka Özellikleri
- **Çoklu Model Desteği**: OpenAI, Anthropic, Google ve Cohere'in en güncel modellerini kullanabilme
- **Kişiselleştirilebilir Sistem Mesajı**: AI'nın karakterini ve yanıt tarzını belirleyebilme
- **Yaratıcılık Ayarı**: Temperature değerini değiştirerek AI yanıtlarının yaratıcılık seviyesini ayarlama
- **Token Limiti**: Yanıt uzunluğunu kontrol edebilme

### ⚙️ Yönetim Araçları
- **Kolay Yapılandırma**: Basit config.yml dosyası ile tüm ayarları özelleştirme
- **Yeniden Yükleme**: `/ai reload` komutu ile yapılandırma değişikliklerini anında uygulama
- **Konuşma Geçmişi Yönetimi**: Bireysel veya toplu konuşma geçmişi temizleme
- **Model Listesi**: Kullanılabilir güncel modelleri görüntüleme

## 🔧 Kurulum

### Hızlı Kurulum
1. [En son sürümü](https://kynux.cloud/download/aichat.jar) indirin
2. JAR dosyasını sunucunuzun `plugins` klasörüne yükleyin
3. Sunucuyu yeniden başlatın veya `/reload` komutunu çalıştırın
4. İlk çalıştırmadan sonra oluşturulan `/plugins/AiChat/config.yml` dosyasını düzenleyin
5. Lora Project API anahtarınızı config.yml'deki `api.key` alanına ekleyin
6. `/ai reload` komutu ile yapılandırmayı yeniden yükleyin

### API Anahtarı Alma
1. [KynuxCloud](https://kynux.cloud) websitesine kaydolun
2. Kontrol panelinde "API Anahtarları" bölümüne gidin
3. "Yeni API Anahtarı Oluştur" butonuna tıklayın
4. Oluşturulan anahtarı kopyalayın ve config.yml dosyanıza ekleyin

## ⚙️ Yapılandırma

AiChat eklentisi, kapsamlı ve detaylı bir yapılandırma dosyası (`config.yml`) sunar:

```yaml
# API Yapılandırması
api:
  url: 'http://ai.kynux.cloud/api/v1/chat/completions'
  key: 'YOUR_API_KEY'  # KynuxCloud API anahtarınız
  model: 'gpt-4o'      # Tercih ettiğiniz model
  temperature: 0.7     # Yaratıcılık seviyesi (0.0-1.0)
  max_tokens: 1000     # Maksimum yanıt uzunluğu

# Sohbet Yapılandırması
chat:
  system_prompt: 'Sen yardımcı bir asistansın. Minecraft oyuncusuna kısa ve öz yanıtlar ver.'
  prefix: '&b[AI] &r'  # AI yanıtlarının öneki (renk kodları desteklenir)
  
  # Sohbet Tetikleyici Özelliği
  chat_trigger:
    enabled: true      # Sohbette ai <mesaj> yazarak kullanabilme
    keyword: 'ai'      # Tetikleyici kelime
```

### Yapılandırma Seçenekleri Açıklaması

#### API Yapılandırması
- **url**: Lora Project API endpoint'i (değiştirmeyin)
- **key**: Lora Project API anahtarınız
- **model**: Kullanılacak AI modeli (güncel liste için `/ai models` komutunu kullanın)
- **temperature**: AI yanıtlarının yaratıcılık/rastgelelik seviyesi
  - 0.0: Çok tutarlı, deterministik yanıtlar
  - 0.7: Dengeli yaratıcılık (önerilen)
  - 1.0: Maksimum yaratıcılık, daha az tutarlı
- **max_tokens**: Yanıt için maksimum token sayısı (daha büyük değerler daha uzun yanıtlar üretir)

#### Sohbet Yapılandırması
- **system_prompt**: AI'nın karakterini ve davranışını belirleyen sistem mesajı
- **prefix**: AI yanıtlarının başına eklenecek önek (renk kodları: &0-&9, &a-&f, &k-&o)
- **chat_trigger**: Sohbet kutusundan tetikleme özelliği
  - enabled: Özelliği açar/kapatır
  - keyword: Tetikleyici kelime (örn: "ai yardım et" yazıldığında çalışır)

## 📢 Komutlar

AiChat eklentisi, kullanımı kolay bir komut seti sunar:

| Komut | Açıklama | İzin |
|-------|----------|------|
| `/ai <mesaj>` | AI'ya mesaj gönderir ve yanıt alır | `aichat.ai` |
| `/ai clear` | Kendi konuşma geçmişinizi temizler | `aichat.ai` |
| `/ai clearall` | Tüm oyuncuların konuşma geçmişlerini temizler | `aichat.admin` |
| `/ai reload` | Yapılandırma dosyasını yeniden yükler | `aichat.admin` |
| `/ai models` | Kullanılabilir AI modellerini listeler | `aichat.admin` |

### Otomatik Tamamlama

Tüm komutlar Tab tuşu ile otomatik tamamlama özelliğini destekler. Örneğin:
- `/ai c` + TAB → `/ai clear`
- `/ai re` + TAB → `/ai reload`

## 🛡️ İzinler

AiChat eklentisi, basit ve etkili bir izin sistemi kullanır:

| İzin | Açıklama | Varsayılan |
|------|----------|------------|
| `aichat.ai` | `/ai` komutunu kullanabilme | Tüm oyuncular |
| `aichat.admin` | Yönetici komutlarını kullanabilme | Operatörler |

Permissions eklentinizde bu izinleri özelleştirebilirsiniz:

```yaml
permissions:
  aichat.ai:
    description: Yapay zeka ile sohbet etme izni
    default: true
  aichat.admin:
    description: AiChat yönetici komutlarını kullanma izni
    default: op
```

## 🧩 Gelişmiş Özellikler

### 📜 Konuşma Geçmişi Yönetimi
AiChat, her oyuncu için ayrı bir konuşma geçmişi tutar ve en son 10 mesajı hatırlar. Bu, AI'nın bağlam içinde kalmasını ve tutarlı yanıtlar vermesini sağlar. Oyuncular `/ai clear` komutu ile kendi geçmişlerini temizleyebilir.

### 🎭 Kişiselleştirilmiş AI Karakteri
`system_prompt` ayarını değiştirerek AI'nın davranışını ve kişiliğini tamamen özelleştirebilirsiniz. Örneğin:

```yaml
system_prompt: 'Sen Minecraft uzmanı bir asistansın. Oyunculara Minecraft hakkındaki sorularında kısa ve bilgilendirici yanıtlar ver. Esprili bir tarzın var ve cevaplarında Minecraft terimleri kullanmayı seviyorsun.'
```

### 🎨 Renk Kodları
AI yanıtlarında Minecraft renk kodlarını kullanabilirsiniz:

| Kod | Renk | Kod | Renk |
|-----|------|-----|------|
| &0 | Siyah | &8 | Koyu Gri |
| &1 | Koyu Mavi | &9 | Mavi |
| &2 | Koyu Yeşil | &a | Yeşil |
| &3 | Koyu Aqua | &b | Aqua |
| &4 | Koyu Kırmızı | &c | Kırmızı |
| &5 | Mor | &d | Pembe |
| &6 | Altın | &e | Sarı |
| &7 | Gri | &f | Beyaz |

Biçimlendirme kodları:  
&k (Sihirli), &l (Kalın), &m (Üstü çizili), &n (Altı çizili), &o (İtalik), &r (Sıfırla)

## 🚀 Performans Optimizasyonu

AiChat eklentisi, sunucu performansını en üst düzeyde tutmak için tasarlanmıştır:

- **Asenkron API İstekleri**: Tüm API çağrıları arka planda asenkron olarak işlenir, böylece ana sunucu thread'i engellenmez.
- **Otomatik Konuşma Yönetimi**: Konuşma geçmişi otomatik olarak optimize edilir, çok uzun geçmişler kısaltılır.
- **OkHttp3 ile Verimli Bağlantı**: Modern HTTP istemcisi ile hızlı ve güvenilir API iletişimi.
- **Hafıza Optimizasyonu**: Yalnızca aktif oyuncuların konuşma geçmişleri bellekte tutulur.

## 🔐 Güvenlik Önlemleri

### API Anahtarı Güvenliği
- API anahtarınızı **kesinlikle gizli tutun**
- `config.yml` dosyasına sınırlı erişim sağlayın
- API anahtarınızı düzenli olarak değiştirin

### Veri Güvenliği
- Oyuncu konuşmaları Lora Project'un güvenli altyapısında işlenir
- Hassas bilgiler şifrelenir ve güvenli bir şekilde iletilir
- Tüm veri aktarımları modern güvenlik protokolleri kullanılarak gerçekleştirilir

## ❓ Sık Sorulan Sorular

### Eklenti sunucu performansımı etkiler mi?
Hayır. AiChat, tüm API isteklerini asenkron olarak işler ve ana sunucu thread'ini engellemez. Oyuncular AI ile konuşurken bile sunucunuz tam performansla çalışmaya devam eder.

### AI yanıtları ne kadar hızlı gelir?
Yanıt süresi, seçilen modele ve Lora Project API'nin yoğunluğuna bağlıdır. Genellikle yanıtlar 1-3 saniye içinde gelir. Daha hızlı yanıtlar için `grok-3-mini` veya `claude-3-haiku` gibi daha hızlı modelleri tercih edebilirsiniz.

### Konuşma geçmişi nerede saklanır?
Konuşma geçmişi, bellekte (RAM) tutulur ve sunucu kapatıldığında silinir. Kalıcı depolama yapılmaz. Bu, hem performans hem de gizlilik açısından avantaj sağlar.

### Bir mesajın maximum uzunluğu nedir?
Minecraft sohbet mesajlarının sınırı 256 karakterdir. AiChat, uzun AI yanıtlarını otomatik olarak bölerek birden fazla mesaj halinde gönderir.

### Promethium, Claude ve diğer modeller arasında ne fark var?
Her model farklı özelliklere, uzmanlık alanlarına ve yanıt stillerine sahiptir. Örneğin, Claude modelleri daha yaratıcı ve akıcı yanıtlar verirken, GPT modelleri teknik konularda daha başarılıdır. `/ai models` komutu ile tüm modelleri görebilir ve ihtiyacınıza en uygun modeli seçebilirsiniz.

## 📘 API Entegrasyonu

AiChat, LoraProject API'sini kullanır ve aşağıdaki endpoint'lere erişir:

- **Chat Tamamlama**: `http://ai.kynux.cloud/api/v1/chat/completions`
- **Model Listesi**: `http://ai.kynux.cloud/api/v1/models`

API dokümantasyonu için [api.kynux.cloud/api-docs](http://ai.kynux.cloud/api-docs) adresini ziyaret edebilirsiniz.

### API Örnek İsteği

```json
{
  "model": "grok-3-mini",
  "messages": [
    {"role": "system", "content": "Sen yardımcı bir asistansın."},
    {"role": "user", "content": "Merhaba, nasılsın?"}
  ],
  "temperature": 0.7,
  "max_tokens": 500
}
```

## 🛠️ Geliştirici Bilgileri

AiChat eklentisi aşağıdaki teknolojileri kullanır:

- **Java 17+**: Modern Java özellikleri
- **Spigot/Bukkit API**: Minecraft sunucu entegrasyonu
- **OkHttp3**: Hızlı ve güvenilir HTTP istekleri
- **Jackson**: JSON işleme ve serileştirme
- **CompletableFuture**: Asenkron işlem yönetimi

### Projeyi Geliştirme

1. Git deposunu klonlayın:
   ```bash
   git clone https://github.com/kynuxcloud/aichat.git
   ```

2. Maven ile bağımlılıkları yükleyin:
   ```bash
   cd aichat
   mvn install
   ```

3. Kod değişikliklerinizi yapın

4. Projeyi derleyin:
   ```bash
   mvn clean package
   ```

5. `target/aichat-1.0.jar` dosyasını test sunucunuzda çalıştırın

## 🔮 Gelecek Özellikler

AiChat ekibi sürekli olarak yeni özellikler geliştirmektedir. Yakında gelmesi planlanan özellikler:

- **Görsel Anlama**: Görselleri anlama ve açıklama yeteneği
- **Çoklu Dil Desteği**: Otomatik dil algılama ve çeviri
- **Sohbet Arayüzü**: Özel GUI ile daha gelişmiş sohbet deneyimi
- **Webhook Entegrasyonu**: Discord ve diğer platformlarla entegrasyon
- **Sohbet Kayıtları**: İsteğe bağlı olarak konuşmaları diske kaydetme
- **NPC Entegrasyonu**: Citizens gibi eklentilerle AI destekli NPC'ler oluşturma

## 📞 Destek ve İletişim

AiChat eklentisi ve KynuxCloud hizmetleri hakkında destek almak için:

- **Discord**: [Lora Project](https://discord.gg/Cgz29e3Fu3) 
- **Discord**: [KynuxCloud Discord Sunucusu](https://discord.gg/wCK5dVSY2n)
- **Website**: [kynux.cloud](https://kynux.cloud)

---

<div align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.16.5%2B-brightgreen" alt="Minecraft 1.16.5+" />
  <img src="https://img.shields.io/badge/Lisans-MIT-blue" alt="MIT License" />
  <p>© 2025 KynuxCloud - Tüm hakları saklıdır.</p>
  <p>AiChat, KynuxCloud'un resmi eklentisidir.</p>
</div>
