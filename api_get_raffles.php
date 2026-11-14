<?php
/**
 * Dedicated Mobile API Endpoint: api_get_raffles.php
 * Fetches active records from the existing raffles database table for the Android app.
 * Dynamically adapts to table columns to prevent SQL column errors.
 */
header('Content-Type: application/json');

$host = "localhost";
$username = "root";
$password = "";
$dbname = "fixtrack_db";

$conn = new mysqli($host, $username, $password, $dbname);
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Database connection failed: " . $conn->connect_error]);
    exit();
}

$sql = "SELECT * FROM raffles ORDER BY id DESC";
$result = $conn->query($sql);

$raffles = array();
if ($result && $result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        if (!isset($row['status']) || empty($row['status'])) {
            $row['status'] = 'active';
        }
        if (!isset($row['raffle_id'])) {
            $row['raffle_id'] = $row['id'] ?? 1;
        }
        if (!isset($row['prize'])) {
            $row['prize'] = $row['title'] ?? 'Community Prize';
        }
        $raffles[] = $row;
    }
}

echo json_encode([
    "status" => "success",
    "raffles" => $raffles
]);

$conn->close();
?>
