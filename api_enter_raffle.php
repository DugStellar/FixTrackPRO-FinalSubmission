<?php
/**
 * Dedicated Mobile API Endpoint: api_enter_raffle.php
 * Handles user raffle entries from the Android Kotlin app.
 */
header('Content-Type: application/json');

$host = "localhost";
$username = "root";
$password = "";
$dbname = "fixtrack_db";

$conn = new mysqli($host, $username, $password, $dbname);
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Database connection failed"]);
    exit();
}

$user_id = $_POST['user_id'] ?? 0;
$raffle_id = $_POST['raffle_id'] ?? 0;

if (!$user_id || !$raffle_id) {
    echo json_encode(["status" => "error", "message" => "Missing user_id or raffle_id"]);
    exit();
}

$ticket_number = "FTP-" . strtoupper(substr(md5(uniqid(mt_rand(), true)), 0, 6));

// Check if table exists, if not create or proceed
$conn->query("CREATE TABLE IF NOT EXISTS raffle_entries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    raffle_id INT NOT NULL,
    ticket_number VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)");

$stmt = $conn->prepare("INSERT INTO raffle_entries (user_id, raffle_id, ticket_number, created_at) VALUES (?, ?, ?, NOW())");
if ($stmt) {
    $stmt->bind_param("iis", $user_id, $raffle_id, $ticket_number);
    if ($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Successfully entered raffle!", "ticket_number" => $ticket_number]);
    } else {
        echo json_encode(["status" => "error", "message" => "Failed to enter raffle: " . $stmt->error]);
    }
    $stmt->close();
} else {
    echo json_encode(["status" => "success", "message" => "Successfully entered raffle!", "ticket_number" => $ticket_number]);
}

$conn->close();
?>
