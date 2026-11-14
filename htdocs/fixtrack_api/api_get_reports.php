<?php
// Turn off standard error display to prevent HTML error pollution, log them safely instead
error_reporting(E_ALL);
ini_set('display_errors', 0);

header('Content-Type: application/json; charset=utf-8');

try {
    require_once 'db_connect.php';

    if (!isset($conn) || $conn->connect_error) {
        echo json_encode(["status" => "error", "message" => "Database connection failed"]);
        exit();
    }

    $userId = isset($_GET['user_id']) ? intval($_GET['user_id']) : 0;

    if ($userId > 0) {
        $stmt = $conn->prepare("SELECT report_id AS id, user_id, title, location, category, description, status, image_url, created_at FROM reports WHERE user_id = ? ORDER BY report_id DESC");
        if ($stmt) {
            $stmt->bind_param("i", $userId);
            $stmt->execute();
            $result = $stmt->get_result();
        } else {
            $result = false;
        }
    } else {
        $result = $conn->query("SELECT report_id AS id, user_id, title, location, category, description, status, image_url, created_at FROM reports ORDER BY report_id DESC");
    }

    $reports = [];
    if ($result) {
        while ($row = $result->fetch_assoc()) {
            $reports[] = [
                "id" => intval($row['id']),
                "user_id" => intval($row['user_id'] ?? 1),
                "title" => $row['title'] ?? "No Title",
                "location" => $row['location'] ?? "Campus",
                "category" => $row['category'] ?? "General",
                "description" => $row['description'] ?? "",
                "status" => $row['status'] ?? "Pending",
                "image_url" => $row['image_url'] ?? "",
                "created_at" => $row['created_at'] ?? ""
            ];
        }
    }

    echo json_encode($reports);

    if (isset($stmt)) { $stmt->close(); }
    $conn->close();

} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
?>