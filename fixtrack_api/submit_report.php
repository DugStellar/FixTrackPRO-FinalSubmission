<?php
session_start();
error_reporting(0);
@ini_set('display_errors', 0);

require_once 'db_connect.php';

$isApi = (!empty($_SERVER['HTTP_ACCEPT']) && strpos($_SERVER['HTTP_ACCEPT'], 'application/json') !== false) 
      || (!empty($_SERVER['CONTENT_TYPE']) && strpos($_SERVER['CONTENT_TYPE'], 'application/json') !== false)
      || (isset($_SERVER['HTTP_USER_AGENT']) && strpos(strtolower($_SERVER['HTTP_USER_AGENT']), 'dalvik') !== false);

if ($isApi) {
    header('Content-Type: application/json; charset=utf-8');
}

// Read both standard form POST data and raw JSON payloads from Android
$raw_input = file_get_contents('php://input');
$json_data = json_decode($raw_input, true);

$title       = trim($_POST['title'] ?? $json_data['title'] ?? '');
$location    = trim($_POST['location'] ?? $json_data['location'] ?? 'Campus');
$category    = trim($_POST['category'] ?? $json_data['category'] ?? 'General');
$description = trim($_POST['description'] ?? $json_data['description'] ?? '');
$userId      = intval($_POST['user_id'] ?? $json_data['user_id'] ?? $_SESSION['user_id'] ?? 1);

if (empty($title)) {
    if ($isApi) {
        echo json_encode(["status" => "error", "message" => "Title is required"]);
    } else {
        echo "<script>alert('Title is required'); window.history.back();</script>";
    }
    exit();
}

try {
    // Generate a unique raffle ticket automatically with the report
    $ticketNumber = "FIX-" . mt_rand(10000, 99999);

    // Check which columns your reports table currently has so the INSERT never fails
    $columns = [];
    $colRes = $conn->query("SHOW COLUMNS FROM reports");
    while ($col = $colRes->fetch_assoc()) {
        $columns[] = $col['Field'];
    }

    if (in_array('category', $columns) && in_array('raffle_ticket', $columns)) {
        $stmt = $conn->prepare("INSERT INTO reports (title, location, category, description, status, raffle_ticket) VALUES (?, ?, ?, ?, 'Pending', ?)");
        $stmt->bind_param("sssss", $title, $location, $category, $description, $ticketNumber);
    } elseif (in_array('category', $columns)) {
        $stmt = $conn->prepare("INSERT INTO reports (title, location, category, description, status) VALUES (?, ?, ?, ?, 'Pending')");
        $stmt->bind_param("ssss", $title, $location, $category, $description);
    } else {
        $stmt = $conn->prepare("INSERT INTO reports (title, location, description, status) VALUES (?, ?, ?, 'Pending')");
        $stmt->bind_param("sss", $title, $location, $description);
    }

    if ($stmt->execute()) {
        if ($isApi) {
            echo json_encode([
                "status" => "success",
                "message" => "Report submitted successfully!",
                "raffle_ticket" => $ticketNumber
            ]);
        } else {
            echo "<script>alert('Report submitted successfully!'); window.location.href='dashboard.php';</script>";
        }
    } else {
        if ($isApi) {
            echo json_encode(["status" => "error", "message" => "Database error: " . $stmt->error]);
        } else {
            echo "<script>alert('Failed to submit report'); window.history.back();</script>";
        }
    }

    $stmt->close();
    $conn->close();

} catch (Exception $e) {
    if ($isApi) {
        echo json_encode(["status" => "error", "message" => "Server Exception: " . $e->getMessage()]);
    } else {
        echo "Server Exception: " . $e->getMessage();
    }
}
?>