// import "./App.css";

// function App() {
//   return (
//     <div>
//       <h1>Commercial Lending Platform</h1>
//       <p>Internal Lending Operations System</p>
//     </div>
//   );
// }

// export default App;

import { useEffect, useState } from "react";
import api from "./services/api";

function App() {
  const [backendStatus, setBackendStatus] = useState("Checking...");

  useEffect(() => {
    api
      .get("/api/health")
      .then((response) => {
        setBackendStatus(response.data);
      })
      .catch(() => {
        setBackendStatus("Backend unavailable");
      });
  }, []);

  return (
    <div>
      <h1>Commercial Lending Platform</h1>
      <p>Internal Lending Operations System</p>

      <p>Backend status: {backendStatus}</p>
    </div>
  );
}

export default App;
