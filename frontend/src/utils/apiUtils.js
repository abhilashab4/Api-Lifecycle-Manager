
export async function getErrorMessage(response) {
  const text = await response.text();

  if (!text) {
    return `Request failed (${response.status})`;
  }

  try {
    const data = JSON.parse(text);

    return (
      data.message ||
      data.detail ||
      data.error ||
      `Request failed (${response.status})`
    );
  } catch {
    return text;
  }
}
