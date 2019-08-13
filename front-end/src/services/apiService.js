const CSRF_HEADER_NAME = "X-CSRF-Token";

let token;

const createAccount = (username, password) =>
  fetch("/api/account", {
    method: "POST",
    headers: {
      "Content-Type": "application/json; charset=utf-8",
      [CSRF_HEADER_NAME]: token
    },
    body: JSON.stringify({
      username,
      password
    })
  })
  .then(response => {
    token = response.headers.get(CSRF_HEADER_NAME);
    return response.json();
  })

const updateAccount = (username, password) =>
  fetch("/api/account", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json; charset=utf-8",
      [CSRF_HEADER_NAME]: token
    },
    body: JSON.stringify({
      username,
      password
    })
  })
  .then(response => {
    token = response.headers.get(CSRF_HEADER_NAME);
    if (response.status == 400) {
      throw new Error("This username is already taken");
    }
  });

const logIn = (username, password) =>
  fetch("/api/session", {
    method: "POST",
    headers: {
      "Content-Type": "application/json; charset=utf-8",
      [CSRF_HEADER_NAME]: token
    },
    body: JSON.stringify({
      username,
      password
    })
  })
  .then(response => {
    token = response.headers.get(CSRF_HEADER_NAME);
    return response.json();
  });

const logOut = () =>
  fetch("/api/session", {method: "DELETE"})
    .then(response => {
      token = response.headers.get(CSRF_HEADER_NAME);
      return;
    });

const getSessionStatus = () =>
  fetch("/api/session")
    .then(response => {
      token = response.headers.get(CSRF_HEADER_NAME);
      return response.json();
    });

const getAccount = () =>
  fetch("/api/account", {
    method: "GET",
    headers: {
      "Content-Type": "application/json; charset=utf-8",
      [CSRF_HEADER_NAME]: token
    }
  })
  .then(response => {
    token = response.headers.get(CSRF_HEADER_NAME);
    return response.json();
  });

export const apiService = {
  createAccount,
  updateAccount,
  getAccount,
  logIn,
  logOut,
  getSessionStatus
}
