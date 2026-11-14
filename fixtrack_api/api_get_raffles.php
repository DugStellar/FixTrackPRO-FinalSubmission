<?php
error_reporting(0);
@ini_set('display_errors', 0);

$isApi = (!empty($_SERVER['HTTP_ACCEPT']) && strpos($_SERVER['HTTP_ACCEPT'], 'application/json') !== false) 
      || (!empty($_SERVER['CONTENT_TYPE']) && strpos($_SERVER['CONTENT_TYPE'], 'application/json') !== false)
      || (isset($_SERVER['HTTP_USER_AGENT']) && strpos(strtolower($_SERVER['HTTP_USER_AGENT']), 'dalvik') !== false);

if ($isApi) {
    header('Content-Type: application/json; charset=utf-8');
}

try {
    require_once 'db_connect.php';

    $result = $conn->query("SELECT raffle_id, title, description, status FROM raffles ORDER BY raffle_id DESC");
    
    $raffles = array();
    if ($result) {
        while ($row = $result->fetch_assoc()) {
            $raffles[] = $row;
        }
    }

    if ($isApi) {
        echo json_encode([
            "status" => "success",
            "raffles" => $raffles
        ]);
    } else {
        // Fallback for website direct view
        echo json_encode($raffles);
    }

    $conn->close();
} catch (Exception $e) {
    if ($isApi) {
        echo json_encode(["status" => "error", "raffles" => [], "message" => $e->getMessage()]);
    } else {
        echo "Error: " . $e->getMessage();
    }
}
?>