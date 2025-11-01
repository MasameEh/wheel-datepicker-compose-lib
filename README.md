# Wheel DatePicker Compose Library
[![](https://jitpack.io/v/MasameEh/wheel-datepicker-compose-lib.svg)](https://jitpack.io/#MasameEh/wheel-datepicker-compose-lib)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/Built%20With-Jetpack%20Compose-blue.svg)](https://developer.android.com/jetpack/compose)
A lightweight and elegant **Wheel Date Picker** built with **Jetpack Compose**


## Overview
This library provides a **beautiful wheel-style date picker** component for Jetpack Compose.
<p align="center">
  <img src="https://github.com/user-attachments/assets/c0825fc7-6737-4b81-a6b8-58263c6b65a6" alt="Wheel DatePicker Preview" width="300"/>
</p>


## Installation
Add JitPack to your project-level `settings.gradle`:
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```
Then add the dependency to your app-level build.gradle:
```kotlin
dependencies {
    implementation("com.github.MasameEh:wheel-datepicker-compose-lib:1.0.2")
}
```
## 🧩 Usage
Example usage in Jetpack Compose:

```kotlin
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExampleUsage() {
    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Selected Date: $selectedDate",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showDialog = true }) {
            Text("Pick a Date")
        }

        if (showDialog) {
            WheelDatePickerDialog(
                title = "Select Date",
                initialDate = selectedDate,
                onDateSelected = {
                    selectedDate = it
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}
```
