
# MeetingBook

Full-stack meeting room booking application built with Angular, Java, Spring Boot and PostgreSQL.

---

## About the Project

MeetingBook is a web application for managing meeting rooms and their reservations.

The project combines an Angular frontend with a REST API built using Spring Boot and PostgreSQL.

Users can browse meeting rooms, create reservations, filter bookings and cancel existing reservations. The backend manages room data, validates reservation requests and prevents overlapping bookings.

The project focuses on full-stack development, REST API design, database management, automated testing and containerized deployment.

---

## Features

- Meeting room listing and detail pages
- Room information including capacity, location, equipment and availability status
- Meeting room CRUD operations through the REST API
- Reservation creation with date and time validation
- Prevention of overlapping reservations
- Reservation listing with room names, dates and status
- Filtering by reservation status and meeting room
- Combined filters and one-click reset
- Reservation cancellation with confirmation
- Loading, error and empty states
- Responsive interface with sidebar navigation

---

## Built With

### Frontend

![Angular](https://img.shields.io/badge/Angular-22-DD0031?style=flat&logo=angular&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=flat&logo=typescript&logoColor=white)
![RxJS](https://img.shields.io/badge/RxJS-B7178C?style=flat&logo=reactivex&logoColor=white)
![Vitest](https://img.shields.io/badge/Vitest-6E9F18?style=flat&logo=vitest&logoColor=white)

### Backend

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4-6DB33F?style=flat&logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apachemaven&logoColor=white)
![MapStruct](https://img.shields.io/badge/MapStruct-1.6-4A6E8A?style=flat)

### Database

![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-4169E1?style=flat&logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=flat&logo=flyway&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-3FCF8E?style=flat&logo=supabase&logoColor=white)

### Infrastructure

![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![Render](https://img.shields.io/badge/Render-000000?style=flat&logo=render&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat&logo=githubactions&logoColor=white)

---

## Quality

- Angular unit and component tests with Vitest and Angular TestBed
- Backend tests with JUnit and Spring Boot Test
- Request validation with Jakarta Bean Validation
- Database schema migrations managed by Flyway
- Docker Compose for local backend and database services
- GitHub Actions CI for backend tests and Maven build
- GitHub Actions CI for frontend tests and production build
- Temporary PostgreSQL database for backend CI

---

## API

The Spring Boot backend exposes two main REST resources.

### Meeting Rooms

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/meeting-rooms` | List meeting rooms |
| GET | `/api/meeting-rooms/{id}` | Get room details |
| POST | `/api/meeting-rooms` | Create a room |
| PUT | `/api/meeting-rooms/{id}` | Update a room |
| DELETE | `/api/meeting-rooms/{id}` | Delete a room |

### Reservations

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/reservations` | List reservations |
| GET | `/api/reservations/{id}` | Get reservation details |
| POST | `/api/reservations` | Create a reservation |
| PATCH | `/api/reservations/{id}/cancel` | Cancel a reservation |

Overlapping active reservations for the same meeting room are rejected with HTTP `409 Conflict`.

Cancellation updates the reservation status without deleting its record.

---

## Installation

### Requirements

- Docker Desktop with Docker Compose
- Node.js 24
- npm

### Clone the Repository

```bash
git clone https://github.com/m-amroune/meetingbook.git
cd meetingbook
```

### Backend and Database

Create a `.env` file at the project root:

```env
MEETINGBOOK_DB_PASSWORD=your-local-database-password
```

The `.env` file is excluded from Git.

Start PostgreSQL and the Spring Boot backend:

```bash
docker compose up --build -d
```

The backend runs at:

```text
http://localhost:8080
```

The REST API is available under `/api`.

Flyway automatically applies the database migrations when the backend starts.

### Frontend

Open another terminal and navigate to the frontend directory:

```bash
cd frontend
npm ci
npm start
```

Open:

```text
http://localhost:4200
```

The Angular development configuration connects to the local backend at `http://localhost:8080/api`.

The database starts without demo meeting rooms. Rooms can be added through the REST API.

---

## Testing

### Frontend

From the `frontend` directory:

```bash
npm test -- --watch=false
```

Production build:

```bash
npm run build
```

### Backend

The Maven Wrapper can run the backend tests:

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

Backend tests require an accessible PostgreSQL database and the appropriate database environment variables.

GitHub Actions automatically provides a temporary PostgreSQL service for CI.

---

## Deployment

- **Backend:** Docker container hosted on Render
- **Database:** PostgreSQL hosted on Supabase
- **Continuous Integration:** GitHub Actions

The Angular frontend is configured to communicate with the deployed backend in production.

