# Google Drive API Demo

This project demonstrates the integration of the **Google Drive API** in an Android application, allowing users to interact with their Google Drive files and folders seamlessly. The app supports navigation, file management, and synchronization with Google Drive.

---

## Features

- **View Files and Folders**: Users can view the contents of their **My Drive** folder.
- **Navigate Nested Folders**: Explore nested directories and access files in a hierarchical structure.
- **Create Files or Folders**: Add new files or folders to Google Drive.
- **Delete Folders or Files**: Remove files or folders and send them to the trash.
- **Trash Management**: 
  - View trashed files and folders. 
  - Recover files and folders from the trash.
- **Sync with Google Drive**: Synchronize the app’s file list with the actual Google Drive files for real-time updates.

---

## Integration Guide

### Admin-Side Setup

1. **Create or Choose a Project**:
   - Log in to your [Google Cloud Console](https://console.cloud.google.com/).
   - Create a new project or select an existing one.

2. **Enable Google Drive API**:
   - Navigate to **API & Services > Library**.
   - Search for **Google Drive API** and enable it for your project.

3. **Set Up Credentials**:
   - Go to **API & Services > Credentials**.
   - Click on **Create Credentials** and choose **OAuth 2.0 Client IDs**.
   - Add your app's **Package Name** and **SHA1 Key** in the credentials configuration.

---

### Android-Side Setup

Add the following dependencies to your `build.gradle` file:

```gradle
// Guava Library
implementation 'com.google.guava:guava:33.3.1-jre'

// Google Play Services for Authentication
implementation 'com.google.android.gms:play-services-auth:21.2.0'

// Google API Client for Android
implementation 'com.google.api-client:google-api-client-android:1.23.0' {
    exclude group: 'org.apache.httpcomponents', module: 'guava-jdk5'
}

// Google Drive API
implementation 'com.google.apis:google-api-services-drive:v3-rev136-1.25.0' {
    exclude group: 'org.apache.httpcomponents', module: 'guava-jdk5'
}

```

### Video Demonstration

To better understand the features and implementation of the project, watch the demo video below:

https://github.com/user-attachments/assets/6d617a02-5b2f-4f8f-a28e-ec44efc295a6



