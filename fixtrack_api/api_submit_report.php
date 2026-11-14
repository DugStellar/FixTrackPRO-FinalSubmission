<?php
// Enable error logging for debugging without breaking JSON output
error_reporting(E_ALL);
ini_set('display_errors', 0);

header('Content-Type: application/json; charset=utf-8');

try {
    require_once 'db_connect.php';

    if (!isset($conn) || $conn->connect_error) {
        echo json_encode(["status" => "error", "message" => "Database connection failed"]);
        exit();
    }

    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $title = isset($_POST['title']) ? trim($_POST['title']) : '';
        $location = isset($_POST['location']) ? trim($_POST['location']) : '';
        $category = isset($_POST['category']) ? trim($_POST['category']) : 'General';
        $description = isset($_POST['description']) ? trim($_POST['description']) : '';
        $userId = isset($_POST['user_id']) ? intval($_POST['user_id']) : 1;
        $imageBase64 = isset($_POST['image_base64']) ? $_POST['image_base64'] : '';
        $imageName = isset($_POST['image_name']) ? $_POST['image_name'] : '';

        if (empty($title) || empty($location) || empty($description)) {
            echo json_encode(["status" => "error", "message" => "All required fields must be filled."]);
            exit();
        }

        // Optional image saving
        $imageUrl = '';
        if (!empty($imageBase64) && !empty($imageName)) {
            $uploadDir = 'uploads/';
            if (!is_dir($uploadDir)) {
                mkdir($uploadDir, 0777, true);
            }
            $decodedImage = base64_decode($imageBase64);
            if ($decodedImage !== false) {
                $filePath = $uploadDir . basename($imageName);
                if (file_put_contents($filePath, $decodedImage)) {
                    $imageUrl = $filePath;
                }
            }
        }

        // Dynamically check columns to ensure query compatibility
        $columns = [];
        $colRes = $conn->query("SHOW COLUMNS FROM reports");
        if ($colRes) {
            while ($col = $colRes->fetch_assoc()) {
                $columns[] = $col['Field'];
            }
        }

        $hasUser = in_array('user_id', $columns);
        $hasLocation = in_array('location', $columns);
        $hasCategory = in_array('category', $columns);
        $hasImage = in_array('image_url', $columns);

        // Build dynamic insert query based on existing table structure
        if ($hasUser && $hasLocation && $hasCategory && $hasImage) {
            $stmt = $conn->prepare("INSERT INTO reports (user_id, title, location, category, description, image_url, status, created_at) VALUES (?, ?, ?, ?, ?, ?, 'Pending', NOW())");
            $stmt->bind_param("isssss", $userId, $title, $location, $category, $description, $imageUrl);
        } else {
            // Fallback basic insert
            $stmt = $conn->prepare("INSERT INTO reports (title, description, status, created_at) VALUES (?, ?, 'Pending', NOW())");
            $stmt->bind_param("ss", $title, $description);
        }

        if ($stmt && $stmt->execute()) {
            echo json_encode(["status" => "success", "message" => "Report submitted successfully!"]);
        } else {
            echo json_encode(["status" => "error", "message" => "SQL Error: " . ($stmt ? $stmt->error : $conn->error)]);
        }

        if (isset($stmt)) { $stmt->close(); }
        $conn->close();

    } else {
        echo json_encode(["status" => "error", "message" => "Invalid request method."]);
    }

} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => "Server Exception: " . $e->getMessage()]);
}
?>