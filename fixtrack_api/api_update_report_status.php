<?php
error_reporting(0);
@ini_set('display_errors', 0);

header('Content-Type: application/json; charset=utf-8');
require_once 'db_connect.php';

$raw_input = file_get_contents('php://input');
$json_data = json_decode($raw_input, true);

$reportId = intval($_POST['report_id'] ?? $json_data['report_id'] ?? 0);
$newStatus = $_POST['status'] ?? $json_data['status'] ?? '';

$allowedStatuses = ['Pending', 'Being Fixed', 'Progressed', 'Working On It', 'Repaired', 'Fixed', 'In Progress', 'Resolved', 'Completed'];

if ($reportId <= 0 || !in_array($newStatus, $allowedStatuses)) {
    echo json_encode(["status" => "error", "message" => "Invalid report ID or status value"]);
    exit();
}

$stmt = $conn->prepare("UPDATE reports SET status = ? WHERE id = ?");
$stmt->bind_param("si", $newStatus, $reportId);

if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Report status updated successfully"]);
} else {
    echo json_encode(["status" => "error", "message" => "Failed to update status: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>