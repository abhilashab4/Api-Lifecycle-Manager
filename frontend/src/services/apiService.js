

import { getErrorMessage } from "../utils/apiUtils";

const API_URL = "http://localhost:8080/api/registry";
const AUTH_HEADER = "Basic " + btoa("admin:AdminDemo123!");

async function handleResponse(response) {
  if (!response.ok) {
    throw new Error(await getErrorMessage(response));
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

export async function fetchApis() {
  const response = await fetch(API_URL, {
    headers: {
      Authorization: AUTH_HEADER,
    },
  });

  return handleResponse(response);
}

export async function createApi(api) {
  const response = await fetch(API_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: AUTH_HEADER,
    },
    body: JSON.stringify(api),
  });

  return handleResponse(response);
}

export async function deprecateApiRequest(
  apiName,
  version,
  sunsetDate,
  replacementVersion
) {
  const response = await fetch(
    `${API_URL}/${encodeURIComponent(apiName)}/${encodeURIComponent(version)}/deprecate`,
    {
      method: "PUT",
      headers: {
        Authorization: AUTH_HEADER,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        sunsetDate,
        replacementVersion,
      }),
    }
  );

  return handleResponse(response);
}

export async function retireApiRequest(apiName, version) {
  const response = await fetch(
    `${API_URL}/${encodeURIComponent(apiName)}/${encodeURIComponent(version)}/retire`,
    {
      method: "PUT",
      headers: {
        Authorization: AUTH_HEADER,
      },
    }
  );

  return handleResponse(response);
}

export async function fetchApiUsage(apiName, version) {
  const response = await fetch(
    `${API_URL}/${encodeURIComponent(apiName)}/${encodeURIComponent(version)}/usage`,
    {
      headers: {
        Authorization: AUTH_HEADER,
      },
    }
  );

  return handleResponse(response);
}





export async function registerConsumer(name) {
  const response = await fetch("http://localhost:8080/api/consumers", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: AUTH_HEADER,
    },
    body: JSON.stringify({ name }),
  });

  return handleResponse(response);
}

export async function grantConsumerAccess(consumerId, apiName, apiVersion) {
  const response = await fetch("http://localhost:8080/api/consumer-access", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: AUTH_HEADER,
    },
    body: JSON.stringify({
      consumerId,
      apiName,
      apiVersion,
    }),
  });

  return handleResponse(response);
}

export async function fetchConsumerAccess(consumerId) {
  const response = await fetch(
    `http://localhost:8080/api/consumer-access/${consumerId}`,
    {
      headers: {
        Authorization: AUTH_HEADER,
      },
    }
  );

  return handleResponse(response);
}



export async function testRuntimeApi(apiName, version, apiKey) {
  const response = await fetch(
    `http://localhost:8080/runtime/${encodeURIComponent(apiName)}/${encodeURIComponent(version)}`,
    {
      method: "GET",
      headers: {
        "X-API-Key": apiKey,
      },
    }
  );

  const body = await response.text();

  let data;
  try {
    data = body ? JSON.parse(body) : null;
  } catch {
    data = body;
  }

  return {
    status: response.status,
    ok: response.ok,
    headers: {
      deprecation: response.headers.get("Deprecation"),
      sunset: response.headers.get("Sunset"),
      successorVersion: response.headers.get("Link"),
    },
    data,
  };
}
