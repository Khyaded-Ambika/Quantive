import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./assets/Style.css";

export default function VerifyOtpPage({ email }) {
  const [otp, setOtp] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch(
        `http://localhost:9090/api/auth/verify-otp?email=${email}&otp=${otp}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (response.ok) {
        alert("OTP verified successfully!");
        navigate("/reset-password");
      } else {
        const errorData = await response.json();
        alert(errorData.message || "Invalid OTP. Please try again.");
      }
    } catch (error) {
      console.error("Error verifying OTP:", error);
      alert("An error occurred while verifying OTP. Please try again later.");
    }
  };

  return (
    <div className="page-container">
      <div className="form-container">
        <h1 className="form-title">Enter OTP</h1>
        <form onSubmit={handleSubmit} className="form-content">
          <div className="form-group">
            <label htmlFor="otp" className="form-label">
              OTP
            </label>
            <input
              id="otp"
              type="text"
              placeholder="Enter your OTP"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              required
              className="form-input"
            />
          </div>
          <button type="submit" className="form-button">
            Submit
          </button>
        </form>
      </div>
    </div>
  );
}
