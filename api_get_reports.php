<?php
/**
 * Dedicated Mobile API Endpoint: api_get_reports.php
 * Queries reports table ordered by created_at DESC and returns clean JSON response.
 * Matches Kotlin data model fields: report_id, id, user_id, title, description, location, image_path, status, created_at.
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

$sql = "SELECT id, user_id, title, description, location, category, image_path, status, created_at FROM reports ORDER BY created_at DESC";
$result = $conn->query($sql);

$reports = array();
if ($result && $result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        if (empty($row['status'])) {
            $row['status'] = 'Pending';
        }
        $row['report_id'] = $row['id'];
        $reports[] = $row;
    }
}

echo json_encode([
    "status" => "success",
    "reports" => $reports
]);

$conn->close();
?>
