# FiatX - Production-Ready Currency Converter 🚀

FiatX is a **production-ready**, privacy-focused Android currency converter that delivers real-time exchange rates with a beautiful Material Design 3 interface. Built with modern Android architecture and optimized for Google Play Store distribution, it provides enterprise-grade performance with zero data collection.

---

## 🚀 What's New in v2.0 - Production Release

### 🏪 **Google Play Store Ready**
- **Complete Privacy Policy** - GDPR compliant, zero data collection
- **Terms of Service** - Comprehensive legal coverage
- **MIT License** - Open source with clear licensing
- **Production APK** - Optimized with ProGuard/R8 (4.9MB)
- **Store Assets** - Professional descriptions and metadata

### 🔐 **Enterprise Security**
- **Network Security Config** - HTTPS-only with certificate pinning
- **ProGuard Optimization** - Code obfuscation and size reduction
- **Security Best Practices** - No data leaks or vulnerabilities
- **Privacy-First Design** - Local storage only, no tracking

### 🤖 **Professional CI/CD**
- **GitHub Actions** - Automated testing, building, and deployment
- **Quality Gates** - Lint checks, security scans, performance monitoring
- **Automated Releases** - Tag-based releases with signed APKs
- **Dependency Management** - Automated updates and security alerts

### ✨ **Professional UI/UX Redesign**
- **Modern Material Design 3** with custom branding colors
- **Edge-to-Edge Experience** for immersive full-screen design
- **Custom Splash Screen** - FiatX branded launch experience
- **Smart Layout** that fits all content without scrolling
- **Comprehensive Dark Mode** with proper contrast ratios

### 🔍 **Enhanced Currency Search**
- **Intelligent Search** - Find currencies by code (USD) or name (Dollar)
- **Real-time Filtering** with prioritized results
- **Smart Suggestions** - Exact matches appear first
- **Search Hints** - Clear guidance for users

### 🎯 **Optimized Performance**
- **Smart Conversion Tracking** - Prevents unnecessary API calls
- **No Duplicate Animations** - Smooth, efficient UI updates
- **Intelligent Caching** - Faster subsequent conversions
- **Build Optimization** - Parallel builds, R8 full mode, incremental compilation

---

## 📱 Features

### **Core Functionality**
- Fetches comprehensive list of fiat currencies from public REST APIs
- Downloads and caches exchange rates, refreshed every 24 hours
- Local persistence using Room database for offline use
- Reactive UI updates powered by Kotlin StateFlow and MVVM architecture
- Graceful error handling with fallback API and user-friendly messages

### **Advanced UI Features**
- **Smart Currency Search** - Type "USD", "Euro", or "Dollar" to find currencies instantly
- **Edge-to-Edge Design** - Modern full-screen experience
- **Material Design 3** - Latest Android design language
- **Dark Mode** - Automatic theme switching
- **Professional Animations** - Smooth transitions and feedback
- **Responsive Layout** - Optimized for all screen sizes

### **User Experience**
- **No Scrolling Required** - All content fits on screen
- **Instant Search Results** - Find currencies in seconds
- **Smart Validation** - Real-time input validation
- **Error Recovery** - Clear error messages with retry options
- **Offline Support** - Works without internet connection

---

## 🏗️ Architecture

### **Design Patterns**
- **MVVM**: Clean separation between UI, business logic, and data layers
- **Repository Pattern**: Single source of truth managing data from remote and local sources
- **Custom Adapters**: Enhanced search functionality with intelligent filtering

### **Technology Stack**
- **Room Database**: Local persistence with compile-time checked SQL queries
- **Retrofit + OkHttp**: Networking with coroutine support and logging
- **Kotlin Coroutines & StateFlow**: Asynchronous operations and reactive UI state management
- **Material Design 3**: Modern UI components and theming
- **Edge-to-Edge API**: Full-screen immersive experience

### **Key Components**
- `CurrencySearchAdapter`: Custom adapter with intelligent search and filtering
- `FiatCurrencyRepository`: Handles all data operations and caching logic
- `CurrencyViewModel`: Exposes state flows for UI and manages coroutine scopes
- `MainActivity`: Collects flows and updates UI with Edge-to-Edge support

---

## 🎯 How It Works

### **Data Flow**
1. **Startup**: App checks local database for currency list; fetches from API if empty
2. **Caching**: Exchange rates for curated base currencies are cached with 24-hour expiry
3. **Search**: Real-time filtering of currencies by code or name with smart prioritization
4. **Conversion**: Attempts local database first, falls back to network if needed
5. **UI Updates**: Reactive data streams update UI in real-time with smooth animations

### **Smart Features**
- **Duplicate Prevention**: Tracks conversion parameters to avoid unnecessary operations
- **Intelligent Search**: Prioritizes exact matches and starts-with results
- **Efficient Caching**: Minimizes network requests while maintaining data freshness
- **Error Resilience**: Fallback mechanisms ensure app reliability

---

## 🛠️ Setup & Build

### **Requirements**
- **Android SDK 35**
- **minSdk 24** (Android 7.0+)
- **Java 11**
- **Kotlin 2.0+**

### **APIs Used**
- Primary: `https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/`
- Fallback: `https://latest.currency-api.pages.dev/v1/`

### **Configuration**
- API base URLs are defined in `build.gradle` as `buildConfigField`s
- Custom branding colors extracted from app icon
- Material Design 3 theming with dark mode support

---

## 🎨 UI/UX Highlights

### **Modern Design Elements**
- **Clean Header**: Logo and title integrated naturally
- **Smart Search**: Instant currency filtering with helpful hints
- **Material Cards**: Elevated design with proper shadows and corners
- **Professional Colors**: Custom brand palette with accessibility compliance
- **Smooth Animations**: Contextual feedback without overwhelming users

### **User Experience Improvements**
- **No Scrolling**: All content visible without vertical scrolling
- **Fast Search**: Find any currency in 1-2 keystrokes
- **Clear Feedback**: Loading states, success messages, and error handling
- **Intuitive Flow**: Logical progression from input to result
- **Accessibility**: Proper content descriptions and contrast ratios

---

## 📦 APK Installation

### **Quick Install**
A production-ready debug APK is included for immediate testing:

```bash
# Navigate to project directory
cd /path/to/fiatx

# Install APK
adb install -r apk/FiatX-v2.0-debug.apk
```

### **Build from Source**
```bash
# Clean and build
./gradlew clean assembleDebug

# Install
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔄 Version History

### **v2.0 - Professional UI/UX Redesign**
- ✅ Complete Material Design 3 implementation
- ✅ Edge-to-Edge experience with proper insets handling
- ✅ Advanced currency search with intelligent filtering
- ✅ Custom branding integration
- ✅ Dark mode support
- ✅ Performance optimizations
- ✅ Toolbar-free modern design
- ✅ Smart conversion tracking

### **v1.0 - Initial Release**
- Basic currency conversion functionality
- Room database integration
- MVVM architecture
- Simple UI with spinners

---

## 📦 Production APKs

### **Available Downloads**
- **Production APK**: [`FiatX-v2.0-production.apk`](apk/FiatX-v2.0-production.apk) (4.9MB)
  - ✅ ProGuard optimized and obfuscated
  - ✅ R8 full mode enabled
  - ✅ Production-ready for Google Play Store
  - ✅ Network security config enabled
  - ✅ Minimal size with maximum performance

- **Debug APK**: [`FiatX-v2.0-debug.apk`](apk/FiatX-v2.0-debug.apk) (7.4MB)
  - 🔧 Development build with debug symbols
  - 🔧 Useful for testing and development

### **Installation**
```bash
# Install production APK
adb install -r apk/FiatX-v2.0-production.apk

# Or install debug APK for development
adb install -r apk/FiatX-v2.0-debug.apk
```

---

## 🏪 Google Play Store Publishing

### **Publishing Checklist**
- ✅ **App Information**: Complete with proper metadata
- ✅ **Privacy Policy**: [PRIVACY_POLICY.md](PRIVACY_POLICY.md)
- ✅ **Terms of Service**: [TERMS_OF_SERVICE.md](TERMS_OF_SERVICE.md)
- ✅ **License**: [LICENSE](LICENSE) (MIT)
- ✅ **Store Assets**: Professional descriptions and graphics
- ✅ **Security**: Network security config and ProGuard
- ✅ **Performance**: Optimized APK size and startup time
- ✅ **Compliance**: GDPR, COPPA, and accessibility standards

### **Publishing Guide**
See [PUBLISHING_GUIDE.md](PUBLISHING_GUIDE.md) for complete instructions on:
- Google Play Console setup
- App signing configuration
- Store listing optimization
- Release management
- Post-launch monitoring

### **Legal & Privacy**
- **Privacy Policy**: Zero data collection, GDPR compliant
- **Terms of Service**: Clear usage terms and limitations
- **Content Rating**: Everyone (suitable for all ages)
- **Permissions**: Only INTERNET and ACCESS_NETWORK_STATE

---

## 🔧 Development & CI/CD

### **GitHub Workflows**
- **Android CI**: Automated testing, linting, and building
- **PR Checks**: Comprehensive validation for pull requests
- **Release Automation**: Tag-based releases with signed APKs
- **Code Quality**: Detekt, Ktlint, and coverage reporting
- **Performance Monitoring**: APK analysis and build optimization
- **Dependency Updates**: Weekly automated dependency checks

### **Quality Assurance**
- **Unit Tests**: Comprehensive test coverage
- **Lint Checks**: Zero errors policy
- **Security Scans**: Automated vulnerability detection
- **Performance Tests**: APK size and build time monitoring

See [.github/WORKFLOWS.md](.github/WORKFLOWS.md) for complete CI/CD documentation.

---

## 🚀 Future Enhancements

### **Planned Features**
- **Historical Rate Charts** - Graphical representation of exchange rate trends
- **Favorite Currencies** - Quick access to frequently used currencies
- **Rate Alerts** - Notifications when rates reach target values
- **Widget Support** - Home screen widget for quick conversions
- **Cryptocurrency Support** - Extend to crypto assets
- **Multiple Themes** - Additional color schemes and themes

### **Technical Improvements**
- **Jetpack Compose Migration** - Modern declarative UI
- **Modular Architecture** - Feature-based modules
- **Advanced Caching** - More sophisticated cache strategies
- **Analytics Integration** - Usage insights and crash reporting

---

## 📄 License

This project is open source under the MIT License.

---

## 🙏 Acknowledgments

- **Currency APIs**: Thanks to the free currency API providers
- **Material Design**: Google's excellent design system
- **Android Community**: For continuous innovation and support

---

**Thanks for checking out FiatX! 🚀**

*Built with ❤️ using modern Android development practices*