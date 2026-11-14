<?php
/**
 * Dedicated Mobile API Endpoint: api_submit_report.php
 * Handles multipart/form-data and JSON POST submissions from the Android Kotlin app.
 * Keeps existing web portal scripts completely safe and untouched.
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

$user_id = $_POST['user_id'] ?? 1;
$title = $_POST['title'] ?? '';
$description = $_POST['description'] ?? '';
$location = $_POST['location'] ?? '';
$category = $_POST['category'] ?? 'General';
$status = 'Pending'; // Default status requirement

$image_path = "";

// Handle file upload via multipart/form-data $_FILES['image']
if (isset($_FILES['image']) && $_FILES['image']['error'] == UPLOAD_ERR_OK) {
    $upload_dir = 'uploads/';
    if (!file_exists($upload_dir)) {
        mkdir($upload_dir, 0777, true);
    }
    $file_name = time() . '_' . basename($_FILES['image']['name']);
    $target_file = $upload_dir . $file_name;
    if (move_uploaded_file($_FILES['image']['tmp_name'], $target_file)) {
        $image_path = $target_file;
    }
}
// Handle Base64 encoded image if sent via POST
else if (isset($_POST['image_base64']) && !empty($_POST['image_base64'])) {
    $upload_dir = 'uploads/';
    if (!file_exists($upload_dir)) {
        mkdir($upload_dir, 0777, true);
    }
    $file_name = $_POST['image_name'] ?? (time() . '_report.jpg');
    $target_file = $upload_dir . basename($file_name);
    $decoded_data = base64_decode($_POST['image_base64']);
    if (file_put_contents($target_file, $decoded_data)) {
        $image_path = $target_file;
    }
}

$stmt = $conn->prepare("INSERT INTO reports (user_id, title, description, location, category, image_path, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())");
if ($stmt) {
    $stmt->bind_param("issssss", $user_id, $title, $description, $location, $category, $image_path, $status);
    if ($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Report submitted successfully", "report_id" => $stmt->insert_id]);
    } else {
        echo json_encode(["status" => "error", "message" => "Execute failed: " . $stmt->error]);
    }
    $stmt->close();
} else {
    echo json_encode(["status" => "error", "message" => "Prepare failed: " . $conn->error]);
}

$conn->close();
?>
