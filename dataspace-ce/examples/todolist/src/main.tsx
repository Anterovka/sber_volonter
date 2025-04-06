import React from "react";
import ReactDOM from "react-dom/client"; 

import App from "./App";

import { AuthProvider, DataProvider } from "./context"; 

import "./index.css"; //

const root = document.getElementById("root");

if (!root) {
  throw Error('Корневой элемент не найден');
}

const container = ReactDOM.createRoot(root)

container.render(
  <React.StrictMode>
    <AuthProvider>
      <DataProvider>
        <App />
      </DataProvider>
    </AuthProvider>
  </React.StrictMode>
);