import React from "react";
import { Routes, Route } from "react-router-dom";
import LoginPage from "./LoginPage";
import Registration from "./Registration";
import ForgotPassword from "./ForgotPassword";
import OtpVerification from "./OtpVerification";
import ResetPassword from "./ResetPassword";
import CustomerHomePage from "./CustomerHomePage";
import CartPage from "./CartPage";
import Orders from "./Orders";
import AdminLogin from "./AdminLogin"; 
import AdminDashboard from "./AdminDashboard";

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<Registration />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/verify-otp" element={<OtpVerification />} /> {/* Fixed path */}
      <Route path="/reset-password" element={<ResetPassword />} />
      <Route path="/customerhome" element={<CustomerHomePage />} />
      <Route path="/UserCartPage" element={<CartPage />} />
      <Route path="/orders" element={<Orders/>} />
      <Route path="/admin" element={<AdminLogin />} />
      <Route path="/admindashboard" element={<AdminDashboard />} />
      {/* <Route path="/cart" element={<CartPage />} /> */}
    </Routes>
  );
};

export default AppRoutes;
