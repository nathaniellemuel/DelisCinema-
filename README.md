# 🎬 Delis Cinema Ticket Booking System

A desktop application for managing movie schedules, seat bookings, and transactions at Delis Cinema.

***

## ✨ Features
- ✅ **Film Management:** Add, update, and delete film data.
- ✅ **Studio Management:** Manage available cinema studios.
- ✅ **Schedule Management:** Create screening schedules with conflict prevention.
- ✅ **Real-time Booking:** Select and book seats in real-time.
- ✅ **Transaction History:** View transaction history and summaries.
- ✅ **Revenue Reports:** Generate reports by month, studio, and film.
- ✅ **Printable Tickets:** Print ticket receipts for customers.
- ✅ **User Authentication:** Secure login system for staff and admins.

***

## 🛠️ Technologies Used
- **Language:** Java (Swing for GUI)
- **Database:** MySQL
- **Connectivity:** JDBC
- **IDE:** IntelliJ IDEA (Recommended)

***

## ⚙️ Installation
1.  **Clone the repository**
    ```bash
    git clone https://github.com/fauzanakbarwijaya/DelisCinema.git DelisCinema
    ```
2.  **Import the Project**
    Open and import the cloned project into your preferred Java IDE (IntelliJ IDEA is recommended).

3.  **Configure the Database**
    -   Create a new MySQL database.
    -   Import the database schema from the provided `.sql` file.
    -   Update the database connection settings in `src/Utility/DBUtil.java` to match your credentials.

4.  **Run the Application**
    Build and run the project from your IDE.

***

## 🚀 Usage
The system has two primary user roles with distinct functionalities:

### Admin
-   Logs into the system to access the main dashboard.
-   Manages master data including **films, studios, and schedules**.
-   Views transaction summaries and revenue reports.

### Staff (Cashier)
-   Selects a movie schedule.
-   Chooses available seats for customers.
-   Completes the booking transaction.
-   Prints tickets for the customer.

***

## 🤝 Contributing
Contributions are always welcome! Please fork the repository and submit a pull request with your changes.

## 📄 License
This project is licensed under the [MIT License](LICENSE).

## 📞 Contact
For any questions or support, please reach out:
-   **Zan:** `neacakbar@gmail.com`
-   **Nathan** `nathanlemuel14@gmail.com`
-   **GitHub Issues:** [Report an Issue](https://github.com/fauzanakbarwijaya/DelisCinema/issues)
