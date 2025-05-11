-- Users table with full_name field
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    email TEXT UNIQUE,
    full_name TEXT,
    status TEXT DEFAULT 'Available',
    profile_image_path TEXT DEFAULT '/images/default_profile.png',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Messages table
CREATE TABLE IF NOT EXISTS messages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    content TEXT NOT NULL,
    sender TEXT NOT NULL,
    timestamp TEXT NOT NULL,
    is_from_bot INTEGER NOT NULL,
    username TEXT NOT NULL,
    FOREIGN KEY (username) REFERENCES users(username)
);

-- Default user
INSERT OR IGNORE INTO users (username, password, email, full_name, status)
VALUES ('bach', 'bach123', 'bach@gmail.com', 'NT_Bach', 'Online');