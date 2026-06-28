# Smart Resume Parser

A Spring Boot backend that accepts resume uploads, extracts text with Amazon Textract, parses the content into structured resume data with OpenAI, and stores the results in MySQL.

## Features

- Upload resumes through a REST API
- Supports PDF, PNG, JPEG, and TIFF files
- Validates file type, extension, size, and PDF page count
- Rejects multi-page PDFs with a clear `400 Bad Request`
- Uploads valid files to Amazon S3
- Extracts resume text using Amazon Textract
- Parses resume information using OpenAI
- Stores raw and structured resume data in MySQL
- Tracks processing status
- Runs locally with Docker Compose
- Uses persistent MySQL storage through a Docker volume

## Parsed Resume Data

The parser currently extracts:

- Full name
- Email
- Phone number
- Skills
- Education
- Work experience
- Certifications

The original extracted text is also stored in the database.

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- MySQL
- Spring AI
- OpenAI API
- Amazon S3
- Amazon Textract
- AWS SDK for Java
- Apache PDFBox
- Maven
- Docker
- Docker Compose
- Lombok

## Processing Flow

```text
Client uploads resume
        |
        v
File validation
        |
        v
Upload file to Amazon S3
        |
        v
Extract text with Amazon Textract
        |
        v
Parse structured data with OpenAI
        |
        v
Map data to JPA entities
        |
        v
Store resume data in MySQL
```

## API Endpoint

### Parse a Resume

```http
POST /api/resumes/parse
```

The request must use `multipart/form-data`.

| Key | Type | Description |
|---|---|---|
| `file` | File | Resume to upload and parse |

Example URL with the current Docker port mapping:

```text
http://localhost:8085/api/resumes/parse
```

## Example Successful Response

```json
{
  "id": 1,
  "originalFileName": "resume.pdf",
  "fileType": "application/pdf",
  "processingStatus": "COMPLETED",
  "fullName": "Jordan Michael Carter",
  "email": "jordan.carter@example.com",
  "phone": "(614) 555-0187",
  "skills": [
    {
      "id": 1,
      "name": "Java 21"
    },
    {
      "id": 2,
      "name": "Spring Boot"
    }
  ],
  "educations": [],
  "workExperiences": [],
  "certifications": [],
  "uploadedAt": "2026-06-28T17:11:08",
  "processedAt": "2026-06-28T17:11:18"
}
```

## File Validation

Accepted content types:

```text
image/png
image/jpeg
application/pdf
image/tiff
```

Allowed extensions:

```text
.png
.jpg
.jpeg
.pdf
.tiff
```

Maximum upload size:

```text
20 MB
```

PDF resumes must contain only one page.

Example multi-page error:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "MULTI_PAGE_RESUME_NOT_SUPPORTED, Resume must contain only one page",
  "path": "/api/resumes/parse"
}
```

## Environment Variables

Create a `.env` file in the project root:

```env
DB_PASSWORD=your_mysql_password

AWS_ACCESS_KEY_ID=your_aws_access_key
AWS_SECRET_ACCESS_KEY=your_aws_secret_key
AWS_REGION=your_aws_region
AWS_S3_BUCKET_NAME=your_s3_bucket_name

OPENAI_API_KEY=your_openai_api_key
```

## Application Configuration

Relevant values in `application.properties`:

```properties
spring.application.name=smart-resume-parser

spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/smart_resume_parser}
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}

aws.s3.bucket-name=${AWS_S3_BUCKET_NAME}
aws.region=${AWS_REGION}

spring.ai.openai.api-key=${OPENAI_API_KEY}

spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=20MB
spring.servlet.multipart.file-size-threshold=2MB

app.upload.allowed-content-types=image/png,image/jpeg,application/pdf,image/tiff
app.upload.allowed-extensions=.png,.jpg,.jpeg,.pdf,.tiff

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

server.error.include-message=always
```

## Running with Docker

Make sure Docker Desktop is running.

Build and start the application:

```bash
docker compose up --build
```

The API will be available at:

```text
http://localhost:8085
```

Stop the containers:

```bash
docker compose down
```

Stop the containers and delete the MySQL volume:

```bash
docker compose down -v
```

## Running Without Docker

Start MySQL and create the database:

```sql
CREATE DATABASE smart_resume_parser;
```

Set the required environment variables, then run:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
./mvnw.cmd spring-boot:run
```

## Docker Services

### `mysql`

- MySQL 8
- Persistent database volume
- Health check
- Creates the `smart_resume_parser` database

### `smart-resume-parser`

- Builds the Spring Boot application with Java 21
- Waits for MySQL to become healthy
- Receives database, AWS, and OpenAI settings through environment variables

## Processing Statuses

```text
UPLOADED
PROCESSING
COMPLETED
FAILED
```

## Current Limitation

The application currently supports only single-page PDF resumes because it uses synchronous Amazon Textract document detection.

PNG, JPEG, and TIFF images are also supported.

## Security Notes

- Never commit AWS credentials, database passwords, or OpenAI keys.
- Keep `.env` in `.gitignore`.
- Rotate credentials immediately if they are exposed.
- Use restricted IAM permissions in production.

## Future Improvements

- Add endpoints to retrieve resumes
- Add an endpoint to delete resumes
- Delete the associated S3 object when a resume is removed
- Return response DTOs instead of JPA entities
- Add global exception handling
- Add unit and integration tests
- Add project parsing
- Support multi-page PDFs with asynchronous Textract processing
- Add authentication and authorization
- Add API documentation

## Author

**Collins Lekeaka**
