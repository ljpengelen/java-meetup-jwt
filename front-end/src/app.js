import { html, render } from 'https://unpkg.com/lit-html@1.1.2?module';
import { Page } from "./components/Page.js";

const CSRF_HEADER_NAME = "X-CSRF-Token";

let token;

let state = {
  isLoggedIn: false,
  view: "LOG_IN",
  flash: {
    message: null,
    type: null
  },
  username: "",
  password: ""
}

const setState = newState => {
  state = {
    ...state,
    ...newState
  };

  render(
    Page({ state, setState, logIn, logOut, createAccount, updateAccount }),
    document.body
  );
}

const showFlashMessage = (message, type) => {
  setState({
    flash: {
      message,
      type
    }
  });
  setTimeout(
    () => setState({
      flash: {
        message: null,
        type: null
      }
    }),
    2000
  );
}

const logIn = () => {
  if (!state.username || state.username.trim().length == 0) {
    showFlashMessage("Username may not be blank", "danger");
  } else if (!state.password || state.password.trim().length == 0) {
    showFlashMessage("Password may not be blank", "danger");
  } else {
    fetch("/api/session", {
      method: "POST",
      headers: {
        "Content-Type": "application/json; charset=utf-8",
        [CSRF_HEADER_NAME]: token
      },
      body: JSON.stringify({
        username: state.username,
        password: state.password
      })
    })
    .then(response => {
      token = response.headers.get(CSRF_HEADER_NAME);
      return response.json();
    })
    .then(status => {
      if (status.status == "INVALID_CREDENTIALS") {
        showFlashMessage("The credentials you provided are invalid", "danger");
      } else {
        setState({
          isLoggedIn: true,
          view: "UPDATE_ACCOUNT"
        });
      }
    });
  }
}

const createAccount = () => {
  if (!state.username || state.username.trim().length == 0) {
    showFlashMessage("Username may not be blank", "danger");
  } else if (!state.password || state.password.trim().length == 0) {
    showFlashMessage("Password may not be blank", "danger");
  } else {
    fetch("/api/account", {
      method: "POST",
      headers: {
        "Content-Type": "application/json; charset=utf-8",
        [CSRF_HEADER_NAME]: token
      },
      body: JSON.stringify({
        username: state.username,
        password: state.password
      })
    })
    .then(response => {
      token = response.headers.get(CSRF_HEADER_NAME);
      return response.json();
    })
    .then(() =>
      setState({
        view: "LOG_IN"
      })
    );
  }
}

const updateAccount = () =>
  fetch("/api/account", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json; charset=utf-8",
      [CSRF_HEADER_NAME]: token
    },
    body: JSON.stringify({
      username: state.username,
      password: state.password
    })
  }).then(response => token = response.headers.get(CSRF_HEADER_NAME));

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
  })
  .then(account =>
    setState({
      username: account.username
    })
  );

const showLoginView = () => state.view = "LOG_IN";

const logOut = () =>
  fetch("/api/session", {method: "DELETE"})
    .then(() =>
        setState({
          isLoggedIn: false,
          view: "LOG_IN"
        })
    );

fetch("/api/session")
  .then(response => {
    token = response.headers.get(CSRF_HEADER_NAME);
    return response.json();
  })
  .then(status => {
    if (status.status == "LOGGED_IN") {
      setState({
        isLoggedIn: true,
        view: "UPDATE_ACCOUNT"
      });
      getAccount();
    } else {
      setState({
        isLoggedIn: false,
        view: "LOG_IN"
      });
    }
  });
