import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import './assets/Style.css';

function ResetPasswordPage() {
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const email = localStorage.getItem("email");
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (newPassword !== confirmPassword) {
            alert("Passwords do not match");
            return;
        }

        try {
            const response = await fetch(
                `http://localhost:9090/api/auth/reset-password?email=${email}&newPassword=${newPassword}`,
                {
                    method: "POST",
                }
            );

            if (response.ok) {
                alert("Password reset successfully");
                navigate("/login"); // Navigate to the login page
            } else {
                const error = await response.json();
                alert(error.error || "Failed to reset password");
            }
        } catch (error) {
            console.error("Error resetting password:", error);
            alert("An error occurred. Please try again later.");
        }
    };

    return (
        <div className="page-container">
            <div className="form-container">
                <h1 className="form-title">Reset Password</h1>
                <form onSubmit={handleSubmit} className="form-content">
                    <div className="form-group">
                        <label htmlFor="newPassword" className="form-label">New Password:</label>
                        <input
                            id="newPassword"
                            type="password"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            required
                            className="form-input"
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="confirmPassword" className="form-label">Confirm Password:</label>
                        <input
                            id="confirmPassword"
                            type="password"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            required
                            className="form-input"
                        />
                    </div>
                    <div className="button-group">
                        <button type="submit" className="form-button">Reset Password</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default ResetPasswordPage;
