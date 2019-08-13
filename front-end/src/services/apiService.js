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
    return response;
  });

const createAccount = (username, password) =>
  jsonFetch("/api/account", "POST", {
    username,
    password
  }).then(response => response.json());

const updateAccount = (username, password) =>
  jsonFetch("/api/account", "PUT", {
    username,
    password
  })
  .then(response => {
    if (response.status == 400) {
      throw new Error("This username is already taken");
    }
  });

const logIn = (username, password) =>
  jsonFetch("/api/session", "POST", {
    username,
    password
  }).then(response => response.json());

const logOut = () => jsonFetch("/api/session", "DELETE");

const getSessionStatus = () =>
  jsonFetch("/api/session", "GET").then(response => response.json());

const getAccount = () =>
  jsonFetch("/api/account", "GET").then(response => response.json());

export const apiService = {
  createAccount,
  updateAccount,
  getAccount,
  logIn,
  logOut,
  getSessionStatus
}
