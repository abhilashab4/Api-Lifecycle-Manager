
import { useEffect, useState } from "react";
import "./App.css";

import {
  fetchApis,
  createApi,
  deprecateApiRequest,
  retireApiRequest,
  fetchApiUsage,
  registerConsumer,
  grantConsumerAccess,
  fetchConsumerAccess,
  testRuntimeApi,
} from "./services/apiService";

function App() {

  const [apis, setApis] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [registering, setRegistering] = useState(false);
  const [processingApi, setProcessingApi] = useState("");

  const [usage, setUsage] = useState(null);
  const [usageLoading, setUsageLoading] = useState(false);

const [consumers, setConsumers] = useState([]);
const [consumerName, setConsumerName] = useState("");
const [newApiKey, setNewApiKey] = useState("");
const [selectedConsumer, setSelectedConsumer] = useState("");
const [selectedApi, setSelectedApi] = useState("");
const [selectedVersion, setSelectedVersion] = useState("");
const [consumerAccess, setConsumerAccess] = useState([]);
const [consumerLoading, setConsumerLoading] = useState(false);


const [testApiName, setTestApiName] = useState("");
const [testApiVersion, setTestApiVersion] = useState("");
const [runtimeApiKey, setRuntimeApiKey] = useState("");
const [runtimeResult, setRuntimeResult] = useState(null);
const [runtimeLoading, setRuntimeLoading] = useState(false);

const [activePage, setActivePage] = useState("dashboard");

  async function loadApis() {
    const data = await fetchApis();
    setApis(data);
  }

  useEffect(() => {
    loadApis()
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  const activeCount = apis.filter(
    (api) => api.status === "ACTIVE"
  ).length;

  const deprecatedCount = apis.filter(
    (api) => api.status === "DEPRECATED"
  ).length;

  async function registerApi() {
    const name = prompt("Enter API name:");
    if (!name?.trim()) return;

    const version = prompt("Enter version (example: v1):");
    if (!version?.trim()) return;

    const owner = prompt("Enter API owner/team:");
    if (!owner?.trim()) return;

    const trimmedName = name.trim();
    const trimmedVersion = version.trim();

    const duplicate = apis.some(
      (api) =>
        (api.apiName ?? api.name) === trimmedName &&
        api.version === trimmedVersion
    );

    if (duplicate) {
      alert("This API version already exists.");
      return;
    }

    setRegistering(true);
    setError("");

    try {
      await createApi({
        apiName: trimmedName,
        version: trimmedVersion,
        owner: owner.trim(),
      });

      await loadApis();
      alert("API registered successfully!");
    } catch (err) {
      setError(err.message);
      alert("Could not register API: " + err.message);
    } finally {
      setRegistering(false);
    }
  }

  async function deprecateApi(api) {
    const apiName = api.apiName ?? api.name;
    const key = `${apiName}/${api.version}`;

    const sunsetDate = prompt(
      "Enter sunset date (YYYY-MM-DD):",
      "2026-12-31"
    );

    if (!sunsetDate?.trim()) return;

    if (!/^\d{4}-\d{2}-\d{2}$/.test(sunsetDate.trim())) {
      alert("Please enter the date in YYYY-MM-DD format.");
      return;
    }

    const replacementVersion = prompt(
      "Enter replacement version (example: v2):"
    );

    if (!replacementVersion?.trim()) return;

    const confirmed = window.confirm(
      `Deprecate ${apiName} ${api.version}?\n` +
        `Sunset date: ${sunsetDate}\n` +
        `Replacement: ${replacementVersion}`
    );

    if (!confirmed) return;

    setProcessingApi(key);
    setError("");

    try {
      await deprecateApiRequest(
        apiName,
        api.version,
        sunsetDate.trim(),
        replacementVersion.trim()
      );

      await loadApis();

      alert(`${apiName} ${api.version} deprecated successfully.`);
    } catch (err) {
      setError(err.message);
      alert("Could not deprecate API: " + err.message);
    } finally {
      setProcessingApi("");
    }
  }

  async function retireApi(api) {
    const apiName = api.apiName ?? api.name;

    if (api.status !== "DEPRECATED") {
      alert("Only deprecated API versions can be retired.");
      return;
    }

    const confirmed = window.confirm(
      `Are you sure you want to retire ${apiName} ${api.version}?`
    );

    if (!confirmed) return;

    const key = `${apiName}/${api.version}`;

    setProcessingApi(key);
    setError("");

    try {
      await retireApiRequest(apiName, api.version);

      await loadApis();

      alert(`${apiName} ${api.version} retired successfully.`);
    } catch (err) {
      setError(err.message);
      alert("Could not retire API: " + err.message);
    } finally {
      setProcessingApi("");
    }
  }

  async function viewUsage(api) {
    const apiName = api.apiName ?? api.name;

    setUsageLoading(true);
    setError("");
    setUsage(null);

    try {
      const data = await fetchApiUsage(apiName, api.version);
      setUsage(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setUsageLoading(false);
    }
  }


  async function handleRegisterConsumer() {
  if (!consumerName.trim()) {
    alert("Enter a consumer name.");
    return;
  }

  setConsumerLoading(true);
  setError("");
  setNewApiKey("");

  try {
    const result = await registerConsumer(consumerName.trim());

    setConsumers((previous) => [
      ...previous,
      { id: result.id, name: result.name },
    ]);

    setSelectedConsumer(String(result.id));
    setConsumerName("");
    setNewApiKey(result.apiKey);

    alert("Consumer registered successfully!");
  } catch (err) {
    setError(err.message);
  } finally {
    setConsumerLoading(false);
  }
}



async function handleGrantAccess() {
  if (!selectedConsumer || !selectedApi || !selectedVersion) {
    alert("Select a consumer, API, and version.");
    return;
  }

  setConsumerLoading(true);
  setError("");

  try {
    await grantConsumerAccess(
      Number(selectedConsumer),
      selectedApi,
      selectedVersion
    );

    const access = await fetchConsumerAccess(
      Number(selectedConsumer)
    );

    setConsumerAccess(access);

    alert("API access granted successfully!");
  } catch (err) {
    setError(err.message);
  } finally {
    setConsumerLoading(false);
  }
}


async function handleTestApi() {
  if (!testApiName || !testApiVersion || !runtimeApiKey.trim()) {
    setError("Select an API, version, and enter the consumer API key.");
    return;
  }

  setRuntimeLoading(true);
  setRuntimeResult(null);
  setError("");

  try {
    const result = await testRuntimeApi(
      testApiName,
      testApiVersion,
      runtimeApiKey.trim()
    );

    setRuntimeResult(result);
  } catch (err) {
    setError(err.message || "Unable to reach the runtime API.");
  } finally {
    setRuntimeLoading(false);
  }
}

  return (
    <div className="app">

<aside className="sidebar">
  <h2>API Manager</h2>

  <p
    className={`nav ${activePage === "dashboard" ? "active" : ""}`}
    onClick={() => setActivePage("dashboard")}
  >
    Dashboard
  </p>

  <p
    className={`nav ${activePage === "registry" ? "active" : ""}`}
    onClick={() => setActivePage("registry")}
  >
    API Registry
  </p>

  <p
    className={`nav ${activePage === "consumers" ? "active" : ""}`}
    onClick={() => setActivePage("consumers")}
  >
    Consumer Management
  </p>

  <p
    className={`nav ${activePage === "usage" ? "active" : ""}`}
    onClick={() => setActivePage("usage")}
  >
    Usage Reports
  </p>
</aside>

      <main className="main">
        <header>
          <div>
            <h1>API Lifecycle Manager</h1>
            <p>Manage API versions and their lifecycle.</p>
          </div>

          <button
            onClick={registerApi}
            disabled={registering || processingApi !== ""}
          >
            {registering ? "Registering..." : "+ Register API"}
          </button>
        </header>

        {loading && <p>Loading API versions...</p>}

        {error && (
          <p className="error-message">
            Error: {error}
          </p>
        )}

        <section className="stats">
          <div className="stat-card">
            <p>Total API Versions</p>
            <h2>{apis.length}</h2>
          </div>

          <div className="stat-card">
            <p>Active Versions</p>
            <h2>{activeCount}</h2>
          </div>

          <div className="stat-card">
            <p>Deprecated Versions</p>
            <h2>{deprecatedCount}</h2>
          </div>
        </section>

        <section className="table-card">
          <h2>Registered API Versions</h2>

          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>API</th>
                  <th>Version</th>
                  <th>Owner</th>
                  <th>Status</th>
                  <th>Sunset Date</th>
                  <th>Replacement</th>
                  <th>Actions</th>
                </tr>
              </thead>

              <tbody>
                {apis.map((api) => {
                  const apiName = api.apiName ?? api.name;
                  const key = `${apiName}/${api.version}`;

                  return (
                    <tr key={api.id ?? key}>
                      <td>{apiName}</td>
                      <td>{api.version}</td>
                      <td>{api.owner}</td>

                      <td>
                        <span
                          className={`status ${String(
                            api.status
                          ).toLowerCase()}`}
                        >
                          {api.status}
                        </span>
                      </td>

                      <td>{api.sunsetDate || "-"}</td>

                      <td>
                        {api.replacementVersion ||
                          api.replacement ||
                          "-"}
                      </td>

                      <td>
                        <button
                          className="action-button"
                          onClick={() => viewUsage(api)}
                          disabled={usageLoading}
                        >
                          {usageLoading
                            ? "Loading..."
                            : "View Usage"}
                        </button>{" "}

                        {api.status === "ACTIVE" && (
                          <button
                            className="action-button"
                            onClick={() => deprecateApi(api)}
                            disabled={
                              processingApi !== "" || usageLoading
                            }
                          >
                            {processingApi === key
                              ? "Processing..."
                              : "Deprecate"}
                          </button>
                        )}

                        {api.status === "DEPRECATED" && (
                          <button
                            className="action-button"
                            onClick={() => retireApi(api)}
                            disabled={
                              processingApi !== "" || usageLoading
                            }
                          >
                            {processingApi === key
                              ? "Processing..."
                              : "Retire"}
                          </button>
                        )}

                        {api.status === "RETIRED" && (
                          <span className="no-action">
                            Retired
                          </span>
                        )}
                      </td>
                    </tr>
                  );
                })}

                {!loading && apis.length === 0 && (
                  <tr>
                    <td colSpan="7">
                      No API versions registered.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </section>


 {activePage === "consumers" && (
  <>
    <section className="table-card consumer-section">
      <h2>Consumer Management</h2>
      <p>Register services and manage their API permissions.</p>

      <div className="consumer-form">
        <input
          type="text"
          placeholder="Enter consumer name (e.g. order-service)"
          value={consumerName}
          onChange={(e) => setConsumerName(e.target.value)}
        />

        <button
          onClick={handleRegisterConsumer}
          disabled={consumerLoading}
        >
          {consumerLoading ? "Registering..." : "Register Consumer"}
        </button>
      </div>

      {newApiKey && (
        <div className="api-key-result">
          <h3>Consumer registered successfully</h3>
          <p>
            <strong>API Key:</strong> {newApiKey}
          </p>
          <p>Save this key securely. It will not be displayed again.</p>
          <button
            className="action-button"
            onClick={() => {
              navigator.clipboard.writeText(newApiKey);
              alert("API key copied.");
            }}
          >
            Copy API Key
          </button>
        </div>
      )}

      {consumers.length > 0 && (
        <>
          <h3>Registered This Session</h3>

          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Consumer Name</th>
                </tr>
              </thead>
              <tbody>
                {consumers.map((consumer) => (
                  <tr key={consumer.id}>
                    <td>{consumer.id}</td>
                    <td>{consumer.name}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}
    </section>

    <section className="table-card consumer-section">
      <h2>Grant API Access</h2>

      <div className="consumer-form">
        <select
          value={selectedConsumer}
          onChange={async (e) => {
            const id = e.target.value;
            setSelectedConsumer(id);
            setConsumerAccess([]);

            if (id) {
              try {
                const access = await fetchConsumerAccess(Number(id));
                setConsumerAccess(access);
              } catch (err) {
                setError(err.message);
              }
            }
          }}
        >
          <option value="">Select consumer</option>
          {consumers.map((consumer) => (
            <option key={consumer.id} value={consumer.id}>
              {consumer.name}
            </option>
          ))}
        </select>

        <select
          value={selectedApi}
          onChange={(e) => {
            setSelectedApi(e.target.value);
            setSelectedVersion("");
          }}
        >
          <option value="">Select API</option>
          {[...new Set(apis.map((api) => api.apiName ?? api.name))].map(
            (name) => (
              <option key={name} value={name}>
                {name}
              </option>
            )
          )}
        </select>

        <select
          value={selectedVersion}
          onChange={(e) => setSelectedVersion(e.target.value)}
        >
          <option value="">Select version</option>
          {apis
            .filter((api) => (api.apiName ?? api.name) === selectedApi)
            .map((api) => (
              <option key={api.version} value={api.version}>
                {api.version} ({api.status})
              </option>
            ))}
        </select>

        <button
          onClick={handleGrantAccess}
          disabled={consumerLoading}
        >
          {consumerLoading ? "Granting..." : "Grant Access"}
        </button>
      </div>

      <h3>Assigned API Versions</h3>

      {consumerAccess.length > 0 ? (
        <ul>
          {consumerAccess.map((access, index) => (
            <li key={`${access.apiName}-${access.apiVersion}-${index}`}>
              {access.apiName} / {access.apiVersion}
            </li>
          ))}
        </ul>
      ) : (
        <p>No API access assignments loaded.</p>
      )}
    </section>
  </>
)}


{activePage !== "consumers" && (
  <section className="table-card runtime-section">
    <h2>Test Runtime API</h2>
  <p>
    Test whether a consumer can access a specific API version.
  </p>

  <div className="consumer-form">
    <select
      value={testApiName}
      onChange={(e) => {
        setTestApiName(e.target.value);
        setTestApiVersion("");
        setRuntimeResult(null);
      }}
    >
      <option value="">Select API</option>
      {[...new Set(apis.map((api) => api.apiName ?? api.name))].map(
        (name) => (
          <option key={name} value={name}>
            {name}
          </option>
        )
      )}
    </select>

    <select
      value={testApiVersion}
      onChange={(e) => {
        setTestApiVersion(e.target.value);
        setRuntimeResult(null);
      }}
      disabled={!testApiName}
    >
      <option value="">Select version</option>
      {apis
        .filter((api) => (api.apiName ?? api.name) === testApiName)
        .map((api) => (
          <option key={api.version} value={api.version}>
            {api.version} ({api.status})
          </option>
        ))}
    </select>

    <input
      type="password"
      placeholder="Consumer API key"
      value={runtimeApiKey}
      onChange={(e) => setRuntimeApiKey(e.target.value)}
    />

    <button
      onClick={handleTestApi}
      disabled={runtimeLoading}
    >
      {runtimeLoading ? "Testing..." : "Test API"}
    </button>
  </div>

  {runtimeResult && (
    <div className="api-key-result">
      <h3>
        HTTP Status: {runtimeResult.status}
      </h3>

      <p>
        <strong>Result:</strong>{" "}
        {runtimeResult.ok ? "Request successful" : "Request failed"}
      </p>

      <h3>Response Body</h3>
      <pre className="runtime-response">
        {JSON.stringify(runtimeResult.data, null, 2)}
      </pre>

      {runtimeResult.headers.deprecation && (
        <p>
          <strong>Deprecation:</strong>{" "}
          {runtimeResult.headers.deprecation}
        </p>
      )}

      {runtimeResult.headers.sunset && (
        <p>
          <strong>Sunset:</strong>{" "}
          {runtimeResult.headers.sunset}
        </p>
      )}

      {runtimeResult.headers.successorVersion && (
        <p>
          <strong>Replacement:</strong>{" "}
          {runtimeResult.headers.successorVersion}
        </p>
      )}
    </div>
  )}
  </section>
)}

{usage && (
  <section className="table-card usage-report">
    <h2>
      Usage Report: {usage.apiName} / {usage.version}
    </h2>

    <div className="stats">
      <div className="stat-card">
        <p>Total Requests</p>
        <h2>{usage.totalRequests}</h2>
      </div>

      <div className="stat-card">
        <p>Status</p>
        <h2>{usage.status}</h2>
      </div>

      <div className="stat-card">
        <p>Owner</p>
        <h2>{usage.owner || "-"}</h2>
      </div>
    </div>

    <p>
      <strong>Sunset Date:</strong> {usage.sunsetDate || "-"}
    </p>

    <p>
      <strong>Replacement Version:</strong>{" "}
      {usage.replacementVersion || "-"}
    </p>

    <h3>Active Consumers</h3>

    {usage.activeConsumers?.length > 0 ? (
      <ul>
        {usage.activeConsumers.map((consumer, index) => (
          <li key={index}>
            {typeof consumer === "object"
              ? JSON.stringify(consumer)
              : consumer}
          </li>
        ))}
      </ul>
    ) : (
      <p>No active consumers.</p>
    )}

    <button
      className="action-button"
      onClick={() => setUsage(null)}
    >
      Close Report
    </button>
  </section>
)}

      </main>
    </div>
  );
}

export default App;
