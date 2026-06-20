# 📄 My Document Reader - Android App

Ek simple, clean Android app jo PDF, Excel, Word, Text aur Image files open karta hai. **Koi Ads nahi!**

## Supported File Types
- 📕 PDF (.pdf)
- 📊 Excel (.xls, .xlsx)
- 📝 Word (.doc, .docx)
- 📃 Text (.txt)
- 🖼️ Images (.jpg, .jpeg, .png, .bmp)

---

## 🔧 APK Banane Ka Tarika (Step by Step)

### Step 1: Android Studio Install Karo
1. https://developer.android.com/studio pe jao
2. Download karo aur install karo

### Step 2: Project Clone Karo
```bash
git clone https://gitlab.com/sinewordit-group/sinewordit-project.git
cd sinewordit-project
```

### Step 3: Android Studio Mein Open Karo
1. Android Studio open karo
2. `File > Open` click karo
3. Clone ki hui folder select karo
4. Gradle sync hone do (internet chahiye)

### Step 4: APK Build Karo
**Option A - Android Studio se:**
1. Menu mein `Build > Build Bundle(s) / APK(s) > Build APK(s)` click karo
2. Wait karo build hone ka
3. `app/build/outputs/apk/debug/app-debug.apk` file milegi

**Option B - Command Line se:**
```bash
# Windows
gradlew.bat assembleDebug

# Mac/Linux
./gradlew assembleDebug
```

### Step 5: Phone Mein Install Karo
**Method 1 - Direct File Transfer:**
1. APK file ko phone mein copy karo (USB ya WhatsApp se)
2. Phone mein `Settings > Security > Unknown Sources` ON karo
3. File Manager se APK open karo
4. Install karo

**Method 2 - ADB se (USB):**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 📱 App Features
- Home screen pe recent files dikhti hain
- File browser se koi bhi file select karo
- PDF smooth scroll ke saath
- Excel sheets table format mein
- Word documents text format mein
- Clean Material Design UI
- Dark/Light mode support
