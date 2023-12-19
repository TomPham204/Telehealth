from fastapi import FastAPI, UploadFile, File, status, HTTPException, Request
from fastapi.responses import FileResponse
import uvicorn
import time
import datetime
import threading
import shutil
import os
import random
import sqlite3
from contextlib import asynccontextmanager


# ===================================================================================== #
# [1.1]: GLOBAL VARIABLES
SRC_DATABASE = './database/db/primary.db'
DST_DATABASE = './database/db/backup.db'
TMP_DATABASE = './database/db/tmp.db'
LOG_FOLDER = './database/log/'
ERROR_LOG = './database/db/error.log'
LOG_SUFFIX = '.log'
RETRY: int = 3
INTERVAL: int = 60

# [1.2]: Database connection
DB_CONN = sqlite3.connect(SRC_DATABASE)
DB_CONN.execute('pragma journal_mode=WAL;')  # Write-Ahead Logging
DB_CONN.execute(f'pragma journal_size_limit = {200 * 1024 * 1024};')  # 200 MB
DB_CONN.execute('pragma locking_mode = NORMAL;')  # Locking mode
DB_CONN.execute('pragma temp_store = MEMORY;')  # Where to store temporary tables

# ===================================================================================== #
# [2]: FUNCTIONS
def backup() -> None:
    # [1]: Rename the backup database file to temporary database file to avoid overwriting
    #      the backup database file while copying the primary database file. We also want to
    #      avoid copying the backup database file while it is being updated.
    if os.path.exists(DST_DATABASE):
        print(f"Renaming backup database file {DST_DATABASE} to temporary database file {TMP_DATABASE}")
        os.rename(DST_DATABASE, TMP_DATABASE)

    # [2]: Copy from primary database file to backup database file
    try:
        # [2.1]: Copy the primary database file to the backup database file
        print(f"Copying primary database file {SRC_DATABASE} to backup database file {DST_DATABASE}")
        shutil.copyfile(SRC_DATABASE, DST_DATABASE)

        # [2.2]: Remove the temporary database file
        if os.path.exists(TMP_DATABASE):
            print(f"Removing temporary database file {TMP_DATABASE}")
            os.remove(TMP_DATABASE)
    except Exception as e:
        # [2.3]: If the copying process fails, we want to restore the backup database file to its original state
        #        by renaming the temporary database file back to the backup database file
        print(f"Copying primary database file {SRC_DATABASE} to backup database file {DST_DATABASE} failed")
        print(f"Restoring backup database file {TMP_DATABASE} to temporary database file {DST_DATABASE}")
        if os.path.exists(TMP_DATABASE):
            os.rename(TMP_DATABASE, DST_DATABASE)
        raise e


def _found_any_log_file(folder: str) -> bool:
    return len(os.listdir(folder)) > 0

def _stream_log_file_to_database(log_file: str) -> int:
    with open(ERROR_LOG, "a") as server_log:
        dt = datetime.datetime.now().strftime(f"%Y%m%d %H:%M:%S")
        server_log.write(f"[{dt}] Streaming log file {log_file} to database file {SRC_DATABASE}\n")

        failed_query: int = 0
        with open(os.path.join(LOG_FOLDER, log_file), "r") as f:
            for line in f:
                sql = line.strip().replace("\n", "").replace("\r", "")
                if sql == "": 
                    continue
                try:
                    DB_CONN.execute(sql)
                except Exception as e:
                    failed_query += 1
                    print(f"Error: {e}")
                    dt = datetime.datetime.now().strftime(f"%Y%m%d %H:%M:%S")
                    server_log.write(f"[{dt}] {e} >> {sql}\n")

        if failed_query > 0:
            server_log.write(f"Streaming log file {log_file} has failed on {failed_query} queries\n")
        server_log.write(f"Streaming log file {log_file} to database file {SRC_DATABASE} has finished\n")   
        os.remove(os.path.join(LOG_FOLDER, log_file))
    return "OK"


def synchronize_database():
    # [1]: Check if there is any log file in the log folder
    if not _found_any_log_file(LOG_FOLDER):
        print(f"No log file found in {LOG_FOLDER} >> Skip synchronization")
        return None

    # [1]: Backup the database file
    try:
        backup()
    except Exception as e:
        print(f"Backup database file failed: {e}")
        return None     # Make sure that the backup process is successful before proceeding
    
    # [2]: Stream the log files to the backup database file
    for log_file in os.listdir(LOG_FOLDER):
        result: str = _stream_log_file_to_database(log_file)
        
    return None


def on_startup():
    global DB_CONN
    DB_CONN = sqlite3.connect(SRC_DATABASE)
    DB_CONN.execute('pragma journal_mode=WAL;')  # Write-Ahead Logging
    DB_CONN.execute(f'pragma journal_size_limit = {200 * 1024 * 1024};')  # 200 MB
    DB_CONN.execute('pragma locking_mode = NORMAL;')  # Locking mode
    DB_CONN.execute('pragma temp_store = MEMORY;')  # Where to store temporary tables
    print("Starting up >> Open database connection ...")

def on_shutdown():
    global DB_CONN
    print("Shutting down >> Force final database synchronization ...")   
    synchronize_database()

    print("Shutting down >> Close database connection ...")
    DB_CONN.close()

@asynccontextmanager
async def lifespan(application: FastAPI):
    # [0]: Check if folder exists
    if not os.path.exists(LOG_FOLDER):
        os.makedirs(LOG_FOLDER)
    if not os.path.exists(os.path.dirname(ERROR_LOG)):
        os.makedirs(os.path.dirname(ERROR_LOG))
    if not os.path.exists(ERROR_LOG):
        with open(ERROR_LOG, "w") as f:
            f.write("")        
    if not os.path.exists(SRC_DATABASE):
        raise Exception(f"Database file {SRC_DATABASE} does not exist")
    with open(ERROR_LOG, "a") as f:
        dt = datetime.datetime.now().strftime(f"%Y%m%d %H:%M:%S")
        f.write(f"[{dt}] Starting error log and server\n") 

    # [1]: On Startup
    on_startup()

    print("Starting up >> Start database synchronization with threading...")
    thread_timer = threading.Timer(INTERVAL, synchronize_database).start()

    # [2]: Yield
    yield

    # [3]: On Shutdown
    on_shutdown()

# ===================================================================================== #
# [3]: MAIN PROGRAM
app = FastAPI(lifespan=lifespan)

@app.get("/", status_code=status.HTTP_200_OK)
async def root(request: Request):
    with open(ERROR_LOG, "a") as f:
        dt = datetime.datetime.now().strftime(f"%Y%m%d %H:%M:%S")
        f.write(f"[{dt}] [{request.client.host}]: Access to server.\n")

    return {"message": "FastAPI: Welcome to the API of database synchronization.", "status": "OK"}

@app.get("/download", status_code=status.HTTP_200_OK)
async def download(request: Request):
    # Replace 'your_database.db' with the path to your SQLite database file
    if not os.path.exists(DST_DATABASE):
        backup()
    with open(ERROR_LOG, "a") as f:
        dt = datetime.datetime.now().strftime(f"%Y%m%d %H:%M:%S")
        f.write(f"[{dt}] [{request.client.host}]: Downloading database file.\n")    
    return FileResponse(DST_DATABASE, media_type="application/octet-stream", filename="server.db")

@app.post("/upload", status_code=status.HTTP_201_CREATED)
async def upload_file(file: UploadFile = File(...), request: Request = None):
    # Replace 'destination_folder' with the path to the folder where you want to save the file
    if request is not None:
        with open(ERROR_LOG, "a") as f:
            dt = datetime.datetime.now().strftime(f"%Y%m%d %H:%M:%S")
            f.write(f"[{dt}] [{request.client.host}]: Uploading database file.\n")

    random_number = random.randint(0, 100000)
    write_filename = f"{file.filename}-{random_number}" + LOG_SUFFIX
    with open(os.path.join(LOG_FOLDER, write_filename), "wb") as buffer:
        for i in range(RETRY):
            try:
                print(f"Uploading file {file.filename} to server at retry #{i + 1} ...")
                shutil.copyfileobj(file.file, buffer)
                return {"filename": file.filename, "status": "OK"}
            except Exception as e:
                print(f"Error: {e}")
                print(f"Retrying...")
                time.sleep(1)
    return HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Failed to upload file")

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8080, reload=False, use_colors=True)