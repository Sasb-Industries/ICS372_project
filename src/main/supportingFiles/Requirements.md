# Functional Requirements

1. Staff can cancel an order only if its status is **NEW**.  
   - The system updates the order list right after the cancellation.  

2. The system checks the order status before allowing cancellation.  
   - Orders marked **STARTED** or **COMPLETED** cannot be canceled.  

3. Orders and related data are saved so they’re not lost when the app closes.  
   - Data is stored in a snapshot file for later restoration.  

4. Saved orders are automatically reloaded when the app starts or when changes occur.  
   - The display refreshes to show all restored orders.  

5. The system allows importing orders from **XML** or **JSON** files.  
   - Imported files are converted into the internal data format and assigned unique IDs.  

6. Invalid or malformed XML/JSON files are sent into the rejected directory.  
   - Only valid orders are imported, and any errors are logged without crashing the app.  

7. The system supports three fulfillment types: **PICKUP**, **TOGO**, and **DELIVERY**.  

8. Each order type is shown in the table with a different color.  
   - For example, green for PICKUP, blue for TOGO, and yellow for DELIVERY.  

9. The app automatically detects new order files in a specific folder and imports them.  
    - The system watches the folder continuously.  

10. A graphical interface lets staff view, cancel, and import orders.  
    - Includes buttons, labels, and tables for all main actions.  

11. Import summaries are created and saved to logs.  
    - Shows which files succeeded or failed with reasons.  

12. **Unit tests** are included for all main features.  
    - Tests cover cancellation, saving/loading, importing, and order type display.  


# Non-Functional Requirements

1. The app should restore saved orders when starting.  
 

2. Each order should be processed separately during import.  
   - One failed import shouldn’t stop other valid orders.  

3. All errors and failed operations must be logged without stopping the system.  




