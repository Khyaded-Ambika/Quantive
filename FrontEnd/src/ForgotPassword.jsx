import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./assets/Style.css";

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const navigate = useNavigate();

  const handleNext = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(
        `http://localhost:9090/api/auth/forgot-password?email=${email}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (response.ok) {
        alert("OTP has been sent to your email.");
        navigate("/verify-otp", { state: { email } }); // Pass email to OTP page
      } else {
        const errorData = await response.json();
        alert(errorData.error || "Failed to send OTP.");
      }
    } catch (error) {
      console.error("Error:", error);
      alert("An error occurred. Please try again later.");
    }
  };

  const handleBack = () => {
    navigate("/"); // Back to login page
  };

  return (
    <div className="page-container">
      <div className="form-container">
        <h1 className="form-title">Forgot Password</h1>
        <form onSubmit={handleNext} className="form-content">
          <div className="form-group">
            <label htmlFor="email" className="form-label">
              Enter your email
            </label>
            <input
              id="email"
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              className="form-input"
            />
          </div>
          <p className="info-message">
            Please enter the email associated with your account. We will send
            an OTP to reset your password.
          </p>
          <div className="button-group">
            <button type="submit" className="form-button">
              Next
            </button>
            <br />
            <br></br>
            <button type="button" onClick={handleBack} className="form-button">
              Back
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
