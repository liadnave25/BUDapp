# 🌿 BUD – Plant Delivery App

Welcome to **BUD**, your one-stop mobile app for **ordering plants directly from nurseries near you**.  
Whether you're a plant lover or a nursery manager, BUD brings you the tools to buy, manage, and grow your green business. 🪴

---

## ✨ App Overview

### 👤 For Customers:

- 🔐 Sign up & login with **two-step verification** (email or phone)
- 🌱 Browse plants by category (Houseplants, Herbs, Summer/Winter, etc.)
- 🛒 Add plants to a cart and **place an order**
- 🌸 View plants by nursery and read full descriptions
- ✏️ Edit your profile details
- 📅 View active and historical **appointments/orders**
- 🖼️ Browse the public photo **gallery**

### 🏪 For Nursery Managers:

- 🔐 Login and access the **nursery dashboard**
- 📦 View and manage plant inventory by category
- ✏️ Edit plant details: name, stock, price, description, image
- ➕ Add new plants to Firestore
- 🖼️ Upload & manage **gallery images**
- 📋 View **incoming orders** from customers

---

## 🔥 Key Features

- ⚡ Smooth navigation by user type (Customer / Nursery)
- 📸 Full support for **image uploads** from the gallery
- 🔐 **Two-step verification** (via SMS or email)
- 🛍️ **Cart system** with adjustable quantities per plant
- 💾 Persistent login using **SharedPreferences**
- 💬 Order confirmation dialog with full order breakdown
- 📅 **Appointment system** for scheduling services (future feature)

---

## 🔥 Firebase Integration

| Firebase Tool             | Purpose in the App                                       |
|--------------------------|-----------------------------------------------------------|
| **Authentication**       | Sign-up, login & **two-step verification** 🔐             |
| **Firestore Database**   | Store users, plants, orders, gallery images, appointments |
| **Firebase Storage**     | Store and load plant and gallery **images** 🖼️            |
| **Firestore Queries**    | Load data dynamically (by category, nursery, price...) ⚡  |
| **Realtime Updates**     | Live data updates for orders, profile changes 🔁          |

---

## 🧠 Code Structure
/activities/ → All main screens (Login, Home, SignUp, Edit Profile, etc.)
/adapters/ → RecyclerView Adapters for plants, gallery, orders
/models/ → Data classes (Plant, User, Order, Appointment...)
/utils/ → Utility classes for image loading, validation, date formatting
/res/layout/ → XML UI files for all screens

## 🛠 Technologies Used

- `Kotlin`
- `Firebase` (Auth, Firestore, Storage)
- `Glide` for image loading
- `SharedPreferences` for login persistence
- `RecyclerView` + custom adapters
- `Material Components` for modern UI
- `BottomSheetDialog` for flow like appointment confirmation

---

## 🚀 Get Started

1. Clone the repository  
2. Open in Android Studio  
3. Add your own `google-services.json`  
4. Build & run the app on a real/emulated Android device

---

> 💚 Created with care for plant lovers & small businesses 🌿  
> Let's make the world greener, one delivery at a time.
