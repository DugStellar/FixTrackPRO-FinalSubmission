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

$raw_input = file_get_contents('php://input');
$json_data = json_decode($raw_input, true);

// Accept user_id and raffle_id from POST, JSON payload, or active website session
$userId = intval($_POST['user_id'] ?? $json_data['user_id'] ?? $_SESSION['user_id'] ?? 0);
$raffleId = intval($_POST['raffle_id'] ?? $json_data['raffle_id'] ?? 1);

if ($userId <= 0) {
    if ($isApi) {
        echo json_encode(["status" => "error", "message" => "Please log in to enter the raffle"]);
    } else {
        echo "<script>alert('Please log in to enter the raffle'); window.history.back();</script>";
    }
    exit();
}

try {
    $ticketNumber = "FT-" . strtoupper(substr(md5(uniqid(mt_rand(), true)), 0, 8));

    $stmt = $conn->prepare("INSERT INTO raffle_entries (raffle_id, user_id, ticket_number) VALUES (?, ?, ?)");
    $stmt->bind_param("iis", $raffleId, $userId, $ticketNumber);

    if ($stmt->execute()) {
        if ($isApi) {
            echo json_encode([
                "status" => "success",
                "message" => "Raffle entry confirmed!",
                "ticket_number" => $ticketNumber
            ]);
        } else {
            echo "<script>alert('🎟️ Entry confirmed! Your ticket: {$ticketNumber}'); window.location.href='guest_feed.php';</script>";
        }
    } else {
        if ($isApi) {
            echo json_encode(["status" => "error", "message" => "Failed to enter raffle: " . $stmt->error]);
        } else {
            echo "<script>alert('Failed to enter raffle'); window.history.back();</script>";
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