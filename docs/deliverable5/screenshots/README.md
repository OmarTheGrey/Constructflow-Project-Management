# Postman screenshots

Place the following 9 PNG screenshots in this folder. They will be embedded in the technical report (`docs/DELIVERABLE5_REPORT.md`, §6 Testing):

| Filename | What to capture |
|---|---|
| `01-get-all-projects.png` | Request + 200 response for `GET /api/projects` |
| `02-get-project-by-id.png` | Request + 200 response for `GET /api/projects/{id}` |
| `03-post-project.png` | Request body + 201 response for `POST /api/projects` |
| `04-post-resource.png` | Request body + 201 response for `POST /api/resources` |
| `05-put-project.png` | Request body + 200 response for `PUT /api/projects/{id}` |
| `06-put-resource.png` | Request body + 200 response for `PUT /api/resources/{id}` |
| `07-delete-project.png` | 204 response for `DELETE /api/projects/{id}` |
| `08-delete-resource.png` | 204 response for `DELETE /api/resources/{id}` |
| `09-composite-project-kickoff.png` | Request body + 201 response for `POST /api/compositions/project-kickoff` |

## How to use the collection

1. Open Postman → **Import** → drag in both
   - `../postman/ConstructFlow-Deliverable5.postman_collection.json`
   - `../postman/ConstructFlow-local.postman_environment.json`
2. In the top-right environment dropdown, pick **ConstructFlow Local**.
3. Start the backend:
   ```bash
   cd backend
   ./mvnw spring-boot:run        # Linux / macOS
   mvnw.cmd spring-boot:run      # Windows (or 'mvn spring-boot:run' if Maven is installed)
   ```
4. Run the requests in this order so the environment variables get populated automatically:
   1. **POST create project** → populates `{{projectId}}`
   2. **POST create resource** → populates `{{resourceId}}`
   3. `GET all projects`, `GET project by id`
   4. `PUT update project`, `PUT update resource`
   5. `POST /api/compositions/project-kickoff`
   6. `DELETE project`, `DELETE resource`
5. After each request, take a screenshot (Postman's "Save Response → Save as PNG" or any screen-capture tool) and drop it here with the filename listed above.
