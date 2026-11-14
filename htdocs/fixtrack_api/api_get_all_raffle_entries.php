<?php
error_reporting(0);
@ini_set('display_errors', 0);

header('Content-Type: application/json; charset=utf-8');
require_once 'db_connect.php';

$sql = "SELECT re.entry_id, re.ticket_number, re.created_at, u.name as user_name, u.email, r.title as raffle_title 
        FROM raffle_entries re 
        JOIN users u ON re.user_id = u.id 
        JOIN raffles r ON re.raffle_id = r.raffle_id 
        ORDER BY re.entry_id DESC";
        
$result = $conn->query($sql);
$entries = array();

if ($result) {
    while ($row = $result->fetch_assoc()) {
        $entries[] = $row;
    }
}

echo json_encode([
    "status" => "success",
    "entries" => $entries
]);

$conn->close();
?>