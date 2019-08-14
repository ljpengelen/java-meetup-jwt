const CSRF_HEADER_NAME = "X-CSRF-Token";

let token;

const jsonFetch = (url, method, body) =>
  fetch(url, {
    method,
    headers: {
      "Content-Type": "application/json; charset=utf-8",
      [CSRF_HEADER_NAME]: token
    },
    body: JSON.stringify(body)
  }).then(response => {
    token = response.headers.get(CSRF_HEADER_NAME);
    return response.json();
  }).then(json => {
    if (json.error) {
      return Promise.reject(new Error(json.message));
    }

    return json;
  });

const createAccount = (username, password) =>
  jsonFetch("/api/account", "POST", {
    username,
    password
  });

const updateAccount = (username, password) =>
  jsonFetch("/api/account", "PUT", {
    username,
    password
  });

const logIn = (username, password) =>
  jsonFetch("/api/session", "POST", {
    username,
    password
  });

const logOut = () => jsonFetch("/api/session", "DELETE");

const getSessionStatus = () => jsonFetch("/api/session", "GET");

const getAccount = () => jsonFetch("/api/account", "GET");

export const apiService = {
  createAccount,
  updateAccount,
  getAccount,
  logIn,
  logOut,
  getSessionStatus
}
