package com.victorgponce.permadeath_mod;

public class SQLCommands {
    // Players table
    static final String createPlayersTable = "CREATE TABLE IF NOT EXISTS Players ("
            + "PlayerID INT AUTO_INCREMENT PRIMARY KEY, "
            + "Username VARCHAR(50) UNIQUE NOT NULL, "
            + "Lives INT NOT NULL DEFAULT 3, "
            + "InitialLives INT NOT NULL DEFAULT 3, "
            + "DeathCount INT NOT NULL DEFAULT 0, "
            + "Status ENUM('active', 'inactive') DEFAULT 'active', "
            + "RegistrationDate DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + "LastConnection DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
            + ")";

    // Deaths Table
    static final String createDeathsTable = "CREATE TABLE IF NOT EXISTS Deaths ("
            + "DeathID INT AUTO_INCREMENT PRIMARY KEY, "
            + "PlayerID INT NOT NULL, "
            + "DeathDate DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + "Cause VARCHAR(100), "
            + "FOREIGN KEY (PlayerID) REFERENCES Players(PlayerID)"
            + ")";
}
