# InterviewOS — AI-Powered Interview Intelligence Dashboard

InterviewOS is an AI-powered interview preparation and management platform designed to help candidates organize upcoming interviews, track preparation progress, and improve their interview performance through intelligent insights.

It combines interview scheduling, automated preparation workflows, and AI-driven analysis into one interactive dashboard.

## 🚀 Key Features

* **Interview Dashboard:** View and manage upcoming interviews in one place.
* **Google Calendar Integration:** Fetch upcoming interview events directly from Google Calendar.
* **Interview Preparation:** Organize preparation activities based on interview dates and roles.
* **AI-Powered Insights:** Generate personalized preparation recommendations and identify areas for improvement.
* **Interactive UI:** Display interviews and preparation information through intuitive dashboard components.
* **Backend API:** Manage interview-related data and application logic through RESTful APIs.

## 🏗️ System Architecture

```text
                    ┌────────────────────┐
                    │    Angular UI      │
                    │  Interview Dashboard│
                    └─────────┬──────────┘
                              │
                              ▼
                    ┌────────────────────┐
                    │   Spring Boot API  │
                    │  Business Logic    │
                    └─────────┬──────────┘
                              │
               ┌──────────────┼──────────────┐
               ▼              ▼              ▼
       ┌────────────┐ ┌──────────────┐ ┌──────────────┐
       │ Interview  │ │ Google       │ │ AI Interview │
       │ Management │ │ Calendar API │ │ Intelligence  │
       └────────────┘ └──────────────┘ └──────────────┘
               │              │              │
               ▼              ▼              ▼
       ┌────────────┐ ┌──────────────┐ ┌──────────────┐
       │ Database   │ │ OAuth 2.0    │ │ LLM Service  │
       └────────────┘ └──────────────┘ └──────────────┘
```

## 🛠️ Tech Stack

### Frontend

* Angular
* TypeScript
* HTML5
* CSS3

### Backend

* Java
* Spring Boot
* Spring MVC
* RESTful APIs
* Maven

### Database

* MySQL / PostgreSQL

### Integrations & AI

* Google Calendar API
* OAuth 2.0
* Large Language Model (LLM) integration

## 🔄 Application Workflow

1. User opens the InterviewOS dashboard.
2. User connects their Google Calendar account through OAuth 2.0.
3. The backend retrieves upcoming calendar events.
4. Interview-related events are displayed in the Angular dashboard.
5. The user reviews interview details and prepares using AI-powered recommendations.
6. The system helps track preparation activities and interview readiness.

## 📁 Project Structure

```text
InterviewOS/
│
├── frontend/
│   └── interviewos-ui/
│       ├── src/
│       ├── angular.json
│       └── package.json
│
├── backend/
│   └── interviewos-api/
│       ├── src/
│       │   ├── main/
│       │   └── test/
│       └── pom.xml
│
└── README.md
```

## ⚙️ Getting Started

### Prerequisites

* Java 17+
* Maven
* Node.js and npm
* Angular CLI
* Google Cloud project with Google Calendar API enabled

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/interviewos.git
cd interviewos
```

### 2. Configure the Backend

Update the application configuration with your database credentials and Google OAuth 2.0 settings.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/interviewos
spring.datasource.username=your_username
spring.datasource.password=your_password

google.client-id=your_client_id
google.client-secret=your_client_secret
```

Use environment variables or a secure secrets manager for production credentials.

### 3. Run the Backend

```bash
cd backend/interviewos-api
mvn spring-boot:run
```

### 4. Run the Frontend

```bash
cd frontend/interviewos-ui
npm install
ng serve
```

Open the application at:

```text
http://localhost:4200
```

## 🔐 Security

* OAuth 2.0 is used for Google Calendar authorization.
* API credentials and secrets should be stored securely.
* Access tokens should not be committed to version control.
* Environment-specific configuration should be managed separately.

## 🔮 Future Enhancements

* AI-generated technical and behavioral interview questions.
* Resume-based interview preparation.
* Automated mock interviews with voice interaction.
* Interview performance analytics.
* Personalized study plans based on upcoming interviews.
* Email reminders and preparation notifications.
* Job description analysis and skill-gap detection.

## 🎯 Project Objective

InterviewOS aims to simplify interview preparation by bringing scheduling, organization, and AI-powered career intelligence into a single platform.

It is designed as a practical full-stack application demonstrating Java backend development, Angular frontend development, third-party API integration, OAuth 2.0, and GenAI-based feature development.

## 📄 License

This project is intended for educational and portfolio purposes.
