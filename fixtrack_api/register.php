<?php
// Suppress unexpected warnings so JSON isn't corrupted
error_reporting(0);
ini_set('display_errors', 0);

require_once 'db_connect.php';

// Detect if request is from Android App / API
$isApi = (!empty($_SERVER['HTTP_ACCEPT']) && strpos($_SERVER['HTTP_ACCEPT'], 'application/json') !== false) 
      || (!empty($_SERVER['CONTENT_TYPE']) && strpos($_SERVER['CONTENT_TYPE'], 'application/json') !== false)
      || (isset($_SERVER['HTTP_USER_AGENT']) && strpos(strtolower($_SERVER['HTTP_USER_AGENT']), 'dalvik') !== false);

if ($isApi) {
    header('Content-Type: application/json; charset=utf-8');
}

$raw_input = file_get_contents('php://input');
$json_data = json_decode($raw_input, true);

// Accept both 'name' (from Android app) and 'full_name' (from web forms)
$name = trim($_POST['name'] ?? $_POST['full_name'] ?? $json_data['name'] ?? $json_data['full_name'] ?? '');
$email = trim($_POST['email'] ?? $json_data['email'] ?? '');
$password = trim($_POST['password'] ?? $json_data['password'] ?? '');
$role = trim($_POST['role'] ?? $json_data['role'] ?? 'User');

if (empty($name) || empty($email) || empty($password)) {
    if ($isApi) {
        echo json_encode(["status" => "error", "message" => "All fields are required"]);
    } else {
        echo "<script>alert('All fields are required'); window.history.back();</script>";
    }
    exit();
}

try {
    // Check existing account
    $checkStmt = $conn->prepare("SELECT user_id FROM users WHERE email = ?");
    if (!$checkStmt) {
        throw new Exception("SQL Prepare Failed: " . $conn->error);
    }
    
    $checkStmt->bind_param("s", $email);
    $checkStmt->execute();
    $checkStmt->store_result();

    if ($checkStmt->num_rows > 0) {
        if ($isApi) {
            echo json_encode(["status" => "error", "message" => "Email address already registered"]);
        } else {
            echo "<script>alert('Email address already registered'); window.history.back();</script>";
        }
        $checkStmt->close();
        $conn->close();
        exit();
    }
    $checkStmt->close();

    // Hash the password securely for authentication
    $password_hash = password_hash($password, PASSWORD_DEFAULT);

    // Register new account
    $stmt = $conn->prepare("INSERT INTO users (full_name, email, password_hash, role) VALUES (?, ?, ?, ?)");
    if (!$stmt) {
        throw new Exception("SQL Prepare Failed: " . $conn->error);
    }

    $stmt->bind_param("ssss", $name, $email, $password_hash, $role);

    if ($stmt->execute()) {
        if ($isApi) {
            echo json_encode(["status" => "success", "message" => "Account created successfully!"]);
        } else {
            echo "<script>alert('Account created successfully! Please log in.'); window.location.href='index.html';</script>";
        }
    } else {
        if ($isApi) {
            echo json_encode(["status" => "error", "message" => "Registration failed: " . $stmt->error]);
        } else {
            echo "<script>alert('Registration failed'); window.history.back();</script>";
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