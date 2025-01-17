# README EXAMPLE: House Management Application

This project is a web application for managing house data, built with a Spring Boot backend and a Vaadin frontend. It includes features like adding, updating, deleting houses, and exporting data in CSV and PDF formats.

## Features
### Backend
- REST API:
  - Add, update, and delete houses.
  - Export data to CSV for a specific house.
  - Export all data to a PDF file.
- Data Persistence: Stores house data in a houses.json file.
- File Exports: Generates CSV and PDF files in the src/main/resources/exports directory.
### Frontend
- Vaadin Grid:
  - Displays all house data upon loading.
  - Editable rows with a form for house details.
  - Buttons for generating CSV and exporting data to PDF.
- Dynamic Updates: Reflects changes made through the frontend immediately in the backend.
## Prerequisites
- Java 17 or higher.
- Maven for dependency management.
- A modern web browser for accessing the Vaadin frontend.
## Setup
1. Clone the Repository
bash
Copiar
Editar
git clone https://github.com/your-repository-url/house-management-app.git
cd house-management-app
2. Build the Project
bash
Copiar
Editar
mvn clean install
3. Run the Application
bash
Copiar
Editar
mvn spring-boot:run
The backend will be available at http://localhost:8090.

4. Access the Frontend
Open a browser and navigate to http://localhost:8080.

Project Structure
css
Copiar
Editar
house-management-app
├── mab
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── es.ufv.dis.houseExam2025.mab
│   │   │   │       ├── House.java
│   │   │   │       ├── HouseController.java
│   │   │   │       ├── HouseControllerTests.java
│   │   │   ├── resources
│   │   │       ├── houses.json
│   │   │       ├── exports
│   │   │           ├── [Generated CSV and PDF files]
├── pom.xml
## API Endpoints
### House Operations
GET /api/houses: Retrieve all houses.
POST /api/houses: Add a new house.
PUT /api/houses/{id}: Update an existing house.
DELETE /api/houses/{id}: Delete a house.
Export Operations
POST /api/houses/generate-csv: Generate a CSV for a specific house.
GET /api/houses/export-pdf: Export all house data to a PDF.
Testing
Unit tests are implemented using JUnit 5. Run the tests with:

bash
Copiar
Editar
mvn test
Test cases include:

Adding a house.
Updating a house.
Deleting a house.
Exporting data (CSV and PDF).
Contribution
Contributions are welcome! Please follow these steps:

Fork the repository.
Create a feature branch: git checkout -b feature-name.
Commit your changes: git commit -m 'Add feature name'.
Push to the branch: git push origin feature-name.
Open a pull request.
## License
This project is licensed under the MIT License.

Acknowledgments
Spring Boot: For the backend framework.
Vaadin: For the frontend framework.
JUnit: For testing.
Google Gson: For JSON handling.
iText: For PDF generation.
