REM Start back end app.
start cmd /k "cd backend && gradlew bootRun"
REM Start listening for changes to back end files. If a file change is saved, the back end app is reloaded. 
start cmd /k "cd backend && gradlew classes --continuous --parallel"
REM Start front end app. Should automatically reload if file changes are saved.
start cmd /k "cd frontend && npm start"
