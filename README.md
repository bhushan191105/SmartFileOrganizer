# Smart File Organizer 📂

A simple yet effective Java application built with **Swing** and the modern **NIO.2** file handling API to automatically sort and organize files within any specified local directory based on their type.

## 🚀 Key Features

* **GUI Interface:** Easy-to-use graphical interface built with Java Swing.
* **Background Processing:** Utilizes `SwingWorker` to prevent UI freezing during long file operations.
* **Robust File Handling:** Employs `java.nio.file` (NIO.2) for reliable directory scanning and file movement.
* **Category-Based Sorting:** Sorts files into folders like `Images`, `PDFs`, `Audio`, and a catch-all `Others` folder.

---

## 1. Problem Statement 🎯

The core challenge addressed by this tool is the issue of **cluttered, unsorted download and temporary folders**. Manually sorting hundreds of diverse files (images, PDFs, videos, code, etc.) is highly inefficient. This application provides an automated solution to scan a user-specified directory and relocate files into type-specific, dedicated subfolders, enabling swift digital cleanup and organization.

---

## 2. Approach / Methodology ⚙️

The application is split into two primary components:

### A. Core Logic (`OrganizerLogic.java`)
1.  **Category Mapping:** A static `HashMap` (`FILE_CATEGORIES`) defines all organization rules, mapping file **extensions** (e.g., "jpg", "pdf") to **destination folder names**.
2.  **Concurrency:** All file system operations are isolated within the `doInBackground()` method of a **`SwingWorker`** to ensure the GUI remains responsive.
3.  **File Movement:** The logic iterates through files, determines the appropriate destination folder, uses `Files.createDirectories()` for safe folder creation, and performs the atomic move operation using `Files.move()`.

### B. GUI Integration (`FileOrganizerGUI.java`)
1.  **Interface:** Uses standard Java Swing components (`JFrame`, `JFileChooser`, `JTextArea`) for user interaction.
2.  **Real-time Logging:** The background worker uses the `publish()` method to send status messages back to the `JTextArea` log, providing the user with real-time feedback on every file move.

---

## 3. Installation and Execution 🛠️

### Prerequisites
* Java Development Kit (JDK) 8 or higher.
* An IDE (e.g., Eclipse, IntelliJ IDEA) for easy running, or command-line compilation.

### Running in Eclipse
1.  Create a new Java Project named `SmartFileOrganizer`.
2.  Add both `OrganizerLogic.java` and `FileOrganizerGUI.java` to the source folder.
3.  Right-click `FileOrganizerGUI.java` and select **Run As** $\rightarrow$ **Java Application**.

---

## 4. Results / Observations (Sample Run) ✅

## Before Organizing the Folder
<img width="1716" height="1012" alt="image" src="https://github.com/user-attachments/assets/d970230b-b847-4619-9f69-9e43db6db2bf" />

A test run on a directory containing 10 mixed files resulted in the following structure being created:
## After Organizing the Folder 
<img width="1467" height="936" alt="image" src="https://github.com/user-attachments/assets/ad90e411-2ca8-4e66-85d6-7e873ca97f5b" />




### Key Takeaways:
* **Success:** All files were categorized and moved correctly based on the `FILE_CATEGORIES` map.
* **Performance:** The background thread maintained a smooth UI, with log entries confirming file movement without any noticeable lag in the application window.
* **Robustness:** The use of `StandardCopyOption.REPLACE_EXISTING` ensures that the organizer can be run multiple times on the same folder without throwing exceptions.

---
## 5. Preview Of Swing GUI for Smart File Organizer

<img width="1920" height="1020" alt="image" src="https://github.com/user-attachments/assets/3925b497-2880-4721-a101-2ec920fd9c3a" />



<img width="734" height="554" alt="Screenshot 2025-10-28 230339" src="https://github.com/user-attachments/assets/666f00a6-a82a-4fb1-b71f-29807c0fe874" />


## 6. Next Steps & Future Enhancements ✨

* **Duplicate Handling:** Implement a renaming strategy (e.g., appending `(1)`) instead of overwriting existing files in the destination folder.
* **Progress Visualization:** Add a `JProgressBar` to visually track the percentage of files processed.
* **Configuration:** Allow users to define their own file mappings and categories via a settings file.
* **Recursive Sorting:** Add an option to scan and sort files within all subdirectories of the target folder.
