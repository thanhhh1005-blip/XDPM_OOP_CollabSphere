import React from "react";
import { Navigate } from "react-router-dom";
import { getAuthInfo } from "../utils/authStorage";

export default function RequireRole({ allow = [], children }) {
  const { token, role } = getAuthInfo();

  if (!token) return <Navigate to="/login" replace />;
  if (allow.length && !allow.includes(role)) return <Navigate to="/projects" replace />;

  return children;
}
